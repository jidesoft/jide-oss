/*
 * @(#)IconsFactory.java
 *
 * Copyright 2002 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.icons;

import com.jidesoft.swing.JideSwingUtilities;
import com.jidesoft.utils.SecurityUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * <code>IconsFactory</code> provides a consistent way to access icon resource in any application.
 * <p/>
 * Any application usually need to access image files. One way to do it is to put those image files in the installation
 * and access them use direct file access. However this is not a good way because you have to know the full path to the
 * image file. So a better way that most Java applications take is to bundle the image files with in the jar and use
 * class loader to load them.
 * <p/>
 * For example, if a class Foo needs to access image files foo.gif and bar.png, we put the image files right below the
 * source code under icons subfolder. See an example directory structure below.
 * <pre>
 * /src/com/jidesoft/Foo.java
 *                  /icons/foo.gif
 *                  /icons/bar.png
 * </pre>
 * When you compile the java class, you copy those images to class output directory like this.
 * <pre>
 * /classes/com/jidesoft/Foo.class
 *                      /icons/foo.gif
 *                      /icons/bar.png
 * </pre>
 * Notes:<i> In IntelliJ IDEA's "Compile" tab of "Project Property" dialog, there is a way to set "Resource Pattern".
 * Here is the setting on my computer - "?*.properties;?*.xml;?*.html;?*.tree;?*.gif;?*.png;?*.jpeg;?*.jpg;?*.vm;?*.xsd;?*.ilayout;?*.gz;?*.txt"
 * for your reference. If so, all your images will get copies automatically to class output folder. Although I haven't
 * tried, I believe most Java IDEs have the same or similar feature. This feature will make the usage of IconsFactory
 * much easier. </i>
 * <p/>
 * If you setup directory structure as above, you can now use IconsFactory to access the images like this.
 * <pre><code>
 * ImageIcon icon = IconsFactory.get(Foo.class, "icons/foo.gif");
 * </code></pre>
 * IconsFactory will cache the icon for you. So next time if you get the same icon, it will get from cache instead of
 * reading from disk again.
 * <p/>
 * There are a few methods on IconsFactory to create difference variation from the original icon. For example, {@link
 * #getDisabledImageIcon(Class,String)} will get the imaage icon with disabled effect.
 * <p/>
 * We also suggest you to use the template below to create a number of IconsFactory classes in your application. The
 * idea is that you should have one for each functional area so that all your image files can be grouped into each
 * functional area. All images used in that functional area should be put under the folder where this IconsFactory is.
 * Here is an template.
 * <pre><code>
 * class TemplateIconsFactory {
 *    public static class Group1 {
 *        public static final String IMAGE1 = "icons/image11.png";
 *        public static final String IMAGE2 = "icons/image12.png";
 *        public static final String IMAGE3 = "icons/image13.png";
 *    }
 * <p/>
 *    public static class Group2 {
 *        public static final String IMAGE1 = "icons/image21.png";
 *        public static final String IMAGE2 = "icons/image22.png";
 *        public static final String IMAGE3 = "icons/image23.png";
 *    }
 * <p/>
 *    public static ImageIcon getImageIcon(String name) {
 *        if (name != null)
 *            return IconsFactory.getImageIcon(TemplateIconsFactory.class, name);
 *        else
 *            return null;
 *    }
 * <p/>
 *    public static void main(String[] argv) {
 *        IconsFactory.generateHTML(TemplateIconsFactory.class);
 *    }
 * }
 * </code></pre>
 * In your own IconsFactory, you can further divide images into different groups. The example above has two groups.
 * There is also a convenient method getImageIcon() which takes just the icon name.
 * <p/>
 * In the template, we defined the image names as constants. When you have a lot of images, it's hard to remember all of
 * them when writing code. If using the IconsFactory above, you can use
 * <pre><code>
 * ImageIcon icon = TemplateIconsFactory.getImageIcon(TemplateIconsFactory.Group1.IMAGE1);
 * </code></pre>
 * without saying the actual image file name. With the help of intelli-sense (or code completion) feature in most Java
 * IDE, you will find it is much easier to find the icons you want. You can refer to JIDE Components Developer Guide to
 * see a screenshot of what it looks like in IntelliJ IDEA.
 * <p/>
 * You probably also notice this is a main() method in this template. You can run it. When you run, you will see a
 * message printed out like this.
 * <pre><code>
 * "File is generated at "... some directory ...\com.jidesoft.icons.TemplateIconsFactory.html".
 * Please copy it to the same directory as TemplateIconsFactory.java"
 * </code></pre>
 * if you follow the instruction and copy the html file to the same location as the source code and open the html, you
 * will see the all image files defined in this IconsFactory are listed nicely in the page.
 * <p/>
 * By default, all image files are loaded using ImageIO. However if you set system property "jide.useImageIO" to
 * "false", we will disable the usage of ImageIO and use Toolkit.getDefaultToolkit().createImage method to create the
 * image file.
 */
public class IconsFactory {

    static Map<String, ImageIcon> icons = new HashMap<String, ImageIcon>();
    static Map<String, ImageIcon> disableIcons = new HashMap<String, ImageIcon>();
    static Map<String, ImageIcon> enhancedIcons = new HashMap<String, ImageIcon>();

    public static ImageIcon EMPTY_ICON = new ImageIcon() {
        private static final long serialVersionUID = 5081581607741629368L;

        @Override
        public int getIconHeight() {
            return 16;
        }

        @Override
        public int getIconWidth() {
            return 16;
        }

        @Override
        public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
        }
    };

    /**
     * Gets ImageIcon by passing class and a relative image file path.
     * <p/>
     * Please note, getImageIcon will print out error message to stderr if image is not found. The reason we did so is
     * because we want you to make sure all image files are there in your application. If you ever see the error
     * message, you should correct it before shipping the product. But if you just want to test if the image file is
     * there, you don't want any error message print out. If so, you can use {@link #findImageIcon(Class,String)}
     * method. It will throw IOException when image is not found.
     *
     * @param clazz    the Class<?>
     * @param fileName relative file name
     * @return the ImageIcon
     */
    public static ImageIcon getImageIcon(Class<?> clazz, String fileName) {
        String id = clazz.getName() + ":" + fileName;
        Icon saved = icons.get(id);
        if (saved != null)
            return (ImageIcon) saved;
        else {
            ImageIcon icon = createImageIcon(clazz, fileName);
            icons.put(id, icon);
            return icon;
        }
    }

    /**
     * Gets ImageIcon by passing class and a relative image file path.
     *
     * @param clazz    the Class<?>
     * @param fileName relative file name
     * @return the ImageIcon
     *
     * @throws IOException when image file is not found.
     */
    public static ImageIcon findImageIcon(Class<?> clazz, String fileName) throws IOException {
        String id = clazz.getName() + ":" + fileName;
        ImageIcon saved = icons.get(id);
        if (saved != null)
            return saved;
        else {
            ImageIcon icon = createImageIconWithException(clazz, fileName);
            icons.put(id, icon);
            return icon;
        }
    }

    /**
     * Gets a disabled version of ImageIcon by passing class and a relative image file path.
     *
     * @param clazz    the Class<?>
     * @param fileName relative file name
     * @return the ImageIcon
     */
    public static ImageIcon getDisabledImageIcon(Class<?> clazz, String fileName) {
        String id = clazz.getName() + ":" + fileName;
        ImageIcon saved = disableIcons.get(id);
        if (saved != null)
            return saved;
        else {
            ImageIcon icon = createGrayImage(getImageIcon(clazz, fileName));
            disableIcons.put(id, icon);
            return icon;
        }
    }

    /**
     * Gets a brighter ImageIcon by passing class and a relative image file path.
     *
     * @param clazz    the Class<?>
     * @param fileName relative file name
     * @return the ImageIcon
     */
    public static ImageIcon getBrighterImageIcon(Class<?> clazz, String fileName) {
        String id = clazz.getName() + ":" + fileName;
        ImageIcon saved = enhancedIcons.get(id);
        if (saved != null)
            return saved;
        else {
            ImageIcon icon = createBrighterImage(getImageIcon(clazz, fileName));
            enhancedIcons.put(id, icon);
            return icon;
        }
    }

    /**
     * Gets a brighter ImageIcon by passing class, a relative image file path and a percentage of brightness.
     *
     * @param clazz    the Class<?>
     * @param fileName relative file name
     * @param percent  percentage of brightness
     * @return the ImageIcon
     */
    public static ImageIcon getBrighterImageIcon(Class<?> clazz, String fileName, int percent) {
        String id = clazz.getName() + ":" + fileName;
        ImageIcon saved = enhancedIcons.get(id);
        if (saved != null)
            return saved;
        else {
            ImageIcon icon = createBrighterImage(getImageIcon(clazz, fileName), percent);
            enhancedIcons.put(id, icon);
            return icon;
        }
    }


    /**
     * Creates a gray version from an input image. Usually gray icon indicates disabled. If input image is null, a blank
     * ImageIcon will be returned.
     *
     * @param image image
     * @return gray version of the image
     */
    public static ImageIcon createGrayImage(Image image) {
        if (image == null)
            return EMPTY_ICON;
        return new ImageIcon(GrayFilter.createDisabledImage(image));
    }

    /**
     * Creates a gray version from an input ImageIcon. Usually gray icon indicates disabled. If input icon is null, a
     * blank ImageIcon will be returned.
     *
     * @param icon image
     * @return gray version of the image
     */
    private static ImageIcon createGrayImage(ImageIcon icon) {
        if (icon == null)
            return EMPTY_ICON;
        return new ImageIcon(GrayFilter.createDisabledImage(icon.getImage()));
    }

    /**
     * Creates a gray version from an input image. Usually gray icon indicates disabled. If input icon is null, a blank
     * ImageIcon will be returned.
     *
     * @param c    The component to get properties useful for painting, e.g. the foreground or background color.
     * @param icon icon
     * @return gray version of the image
     */
    public static ImageIcon createGrayImage(Component c, Icon icon) {
        if (icon == null)
            return EMPTY_ICON;

        int w = icon.getIconWidth(), h = icon.getIconHeight();
        if ((w == 0) || (h == 0))
            return EMPTY_ICON;

        BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        icon.paintIcon(c, image.getGraphics(), 0, 0);
        return new ImageIcon(GrayFilter.createDisabledImage(image));
    }

    /**
     * Creates a brighter image from an input image. If input image is null, a blank ImageIcon will be returned.
     *
     * @param image image
     * @return dimmed version of the image
     */
    public static ImageIcon createBrighterImage(Image image) {
        if (image == null)
            return EMPTY_ICON;
        return new ImageIcon(ColorFilter.createBrighterImage(image));
    }

    /**
     * Creates a brighter image from an input image with a given percentage of brightness. If input image is null, a
     * blank ImageIcon will be returned.
     *
     * @param image   image
     * @param percent percentage of brightness
     * @return dimmed version of the image
     */
    public static ImageIcon createBrighterImage(Image image, int percent) {
        if (image == null)
            return EMPTY_ICON;
        return new ImageIcon(ColorFilter.createBrighterImage(image, percent));
    }


    /**
     * Creates a gray version from an input image. Usually gray icon indicates disabled. If input icon is null, a blank
     * ImageIcon will be returned.
     *
     * @param c    The component to get properties useful for painting, e.g. the foreground or background color.
     * @param icon icon
     * @return gray version of the image
     */
    public static ImageIcon createBrighterImage(Component c, Icon icon) {
        if (icon == null)
            return EMPTY_ICON;
        BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        icon.paintIcon(c, image.getGraphics(), 0, 0);
        return new ImageIcon(ColorFilter.createBrighterImage(image));
    }

    /**
     * Creates a gray version from an input image with a given percentage of brightness. Usually gray icon indicates
     * disabled. If input icon is null, a blank ImageIcon will be returned.
     *
     * @param c       The component to get properties useful for painting, e.g. the foreground or background color.
     * @param icon    icon
     * @param percent percentage of brightness
     * @return gray version of the image
     */
    public static ImageIcon createBrighterImage(Component c, Icon icon, int percent) {
        if (icon == null)
            return EMPTY_ICON;
        BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        icon.paintIcon(c, image.getGraphics(), 0, 0);
        return new ImageIcon(ColorFilter.createBrighterImage(image, percent));
    }

    /**
     * Creates a brighten version from an input ImageIcon. If input icon is null, a blank ImageIcon will be returned.
     *
     * @param icon image
     * @return dimmed version of the image
     */
    public static ImageIcon createBrighterImage(ImageIcon icon) {
        if (icon == null)
            return EMPTY_ICON;
        return new ImageIcon(ColorFilter.createBrighterImage(icon.getImage()));
    }

    /**
     * Creates a brighter image from an input image with a given percentage of brightness. If input image is null, a
     * blank ImageIcon will be returned.
     *
     * @param icon    image
     * @param percent percentage of brightness
     * @return dimmed version of the image
     */
    public static ImageIcon createBrighterImage(ImageIcon icon, int percent) {
        if (icon == null)
            return EMPTY_ICON;
        return new ImageIcon(ColorFilter.createBrighterImage(icon.getImage(), percent));
    }


    /**
     * Creates a gray version from an input image. Usually gray icon indicates disabled. If input image is null, a blank
     * ImageIcon will be returned.
     *
     * @param image image
     * @return gray version of the image
     */
    public static ImageIcon createNegativeImage(Image image) {
        if (image == null)
            return EMPTY_ICON;
        return new ImageIcon(MaskFilter.createNegativeImage(image));
    }

    /**
     * Creates a version from an input image which replaces one color with another color.
     *
     * @param c        The component to get properties useful for painting, e.g. the foreground or background color.
     * @param icon     icon
     * @param oldColor the old color to be replaced.
     * @param newColor the new color that will replace the old color.
     * @return the image after replacing the color.
     */
    public static ImageIcon createMaskImage(Component c, Icon icon, Color oldColor, Color newColor) {
        BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        icon.paintIcon(c, image.getGraphics(), 0, 0);
        return new ImageIcon(MaskFilter.createImage(image, oldColor, newColor));
    }

    static final double DEGREE_90 = 90.0 * Math.PI / 180.0;

    /**
     * Creates a rotated version of the input image.
     *
     * @param c            The component to get properties useful for painting, e.g. the foreground or background
     *                     color.
     * @param icon         the image to be rotated.
     * @param rotatedAngle the rotated angle, in degree, clockwise. It could be any double but we will mod it with 360
     *                     before using it.
     * @return the image after rotating.
     */
    public static ImageIcon createRotatedImage(Component c, Icon icon, double rotatedAngle) {
        // convert rotatedAngle to a value from 0 to 360
        double originalAngle = rotatedAngle % 360;
        if (rotatedAngle != 0 && originalAngle == 0) {
            originalAngle = 360.0;
        }

        // convert originalAngle to a value from 0 to 90
        double angle = originalAngle % 90;
        if (originalAngle != 0.0 && angle == 0.0) {
            angle = 90.0;
        }

        double radian = Math.toRadians(angle);

        int iw = icon.getIconWidth();
        int ih = icon.getIconHeight();
        int w;
        int h;

        if ((originalAngle >= 0 && originalAngle <= 90) || (originalAngle > 180 && originalAngle <= 270)) {
            w = (int) Math.round((iw * Math.sin(DEGREE_90 - radian) + ih * Math.sin(radian)));
            h = (int) Math.round((iw * Math.sin(radian) + ih * Math.sin(DEGREE_90 - radian)));
        }
        else {
            w = (int) (ih * Math.sin(DEGREE_90 - radian) + iw * Math.sin(radian));
            h = (int) (ih * Math.sin(radian) + iw * Math.sin(DEGREE_90 - radian));
        }

        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        Graphics2D g2d = (Graphics2D) g.create();

        // calculate the center of the icon.
        int cx = iw / 2;
        int cy = ih / 2;

        // account for images that have a center point in the middle of a pixel.
        // for these images (not divisible by two) we need to account for the
        // "down and to the right" bias of the graphics context.
        int xOffset = iw % 2 != 0 && originalAngle >= 90 && originalAngle <= 180
                ? 1 : 0;
        int yOffset = iw % 2 != 0 && originalAngle >= 180 && originalAngle < 360
                ? 1 : 0;

        // move the graphics center point to the center of the icon.
        g2d.translate(w / 2 + xOffset, h / 2 + yOffset);

        // rotate the graphics about the center point of the icon
        g2d.rotate(Math.toRadians(originalAngle));

        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        icon.paintIcon(c, g2d, -cx, -cy);

        g2d.dispose();
        return new ImageIcon(image);
    }

    /**
     * Creates a negative version from an input black image which basically replaces black pixel with white pixel.
     *
     * @param c    The component to get properties useful for painting, e.g. the foreground or background color.
     * @param icon icon
     * @return the negative version of the image
     */
    public static ImageIcon createNegativeImage(Component c, Icon icon) {
        BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        icon.paintIcon(c, image.getGraphics(), 0, 0);
        return new ImageIcon(MaskFilter.createNegativeImage(image));
    }

    private static ImageIcon createImageIcon(final Class<?> baseClass, final String file) {
        try {
            return createImageIconWithException(baseClass, file);
        }
        catch (IOException e) {
            System.err.println(e.getLocalizedMessage());
            return null;
        }
    }

    private static ImageIcon createImageIconWithException(final Class<?> baseClass, final String file) throws IOException {
        InputStream resource = baseClass.getResourceAsStream(file);
        if (resource == null) {
            System.err.println("Image file " + file + " is missing");
            return null;
        }
        else {
            Image image;
            if ("true".equals(SecurityUtils.getProperty("jide.useImageIO", "true"))) {
                image = ImageIO.read(resource);
            }
            else {
                image = readImageIcon(baseClass, file, resource);
            }
            resource.close();
            return new ImageIcon(image);
        }
    }


    private static Image readImageIcon(Class clazz, String file, InputStream resource) throws IOException {
        final byte[][] buffer = new byte[1][];
        try {
            BufferedInputStream in = new BufferedInputStream(resource);
            ByteArrayOutputStream out = new ByteArrayOutputStream(1024);

            buffer[0] = new byte[1024];
            int n;
            while ((n = in.read(buffer[0])) > 0) {

                out.write(buffer[0], 0, n);
            }
            in.close();
            out.flush();
            buffer[0] = out.toByteArray();
        }
        catch (IOException ioe) {
            throw ioe;
        }

        if (buffer[0] == null || buffer[0].length == 0) {
            Package pkg = clazz.getPackage();
            String pkgName = "";
            if (pkg != null) {
                pkgName = pkg.getName().replace('.', '/');
            }
            if (buffer[0] == null) {
                throw new IOException("Warning: Resource " + pkgName + "/" + file + " not found.");
            }
            if (buffer[0].length == 0) {
                throw new IOException("Warning: Resource " + pkgName + "/" + file + " is zero-length");
            }
        }

        return Toolkit.getDefaultToolkit().createImage(buffer[0]);
    }

    /**
     * Generates HTML that lists all icons in IconsFactory.
     *
     * @param clazz the IconsFactory class
     */
    public static void generateHTML(Class<?> clazz) {
        String fullClassName = clazz.getName();
        String className = getClassName(fullClassName);
        File file = new File(fullClassName + ".html");

        try {
            FileWriter writer = new FileWriter(file);
            try {
                writer.write("<html>\n<body>\n<p><b><font size=\"5\" face=\"Verdana\">Icons in " +
                        fullClassName + "</font></b></p>");
                writer.write("<p><b><font size=\"3\" face=\"Verdana\">Generated by JIDE Icons</font></b></p>");
                writer.write("<p><b><font size=\"3\" color=\"#AAAAAA\" face=\"Verdana\">1. If you cannot view the images in this page, " +
                        "make sure the file is at the same directory as " + className + ".java</font></b></p>");
                writer.write("<p><b><font size=\"3\" color=\"#AAAAAA\" face=\"Verdana\">2. To get a particular icon in your code, call " +
                        className + ".getImageIcon(FULL_CONSTANT_NAME). Replace FULL_CONSTANT_NAME with the actual " +
                        "full constant name as in the table below" + "</font></b></p>");
                generate(clazz, writer, className);
                writer.write("\n</body>\n</html>");
            }
            catch (IOException e) {
                System.err.println(e);
            }
            finally {
                writer.close();
            }
            System.out.println("File is generated at \"" + file.getAbsolutePath() + "\". Please copy it to the same directory as " + className + ".java");
        }
        catch (IOException e) {
            System.err.println(e);
        }
    }

    private static String getClassName(String fullName) {
        int last = fullName.lastIndexOf(".");
        if (last != -1) {
            fullName = fullName.substring(last + 1);
        }
        StringTokenizer tokenizer = new StringTokenizer(fullName, "$");
        StringBuffer buffer = new StringBuffer();
        while (tokenizer.hasMoreTokens()) {
            buffer.append(tokenizer.nextToken());
            buffer.append(".");
        }
        return buffer.substring(0, buffer.length() - 1);
    }

    private static void generate(Class<?> aClass, FileWriter writer, String prefix) throws IOException {
        Class<?>[] classes = aClass.getDeclaredClasses();
        // don't know why but the order is exactly the reverse of the order of definitions.
        for (int i = classes.length - 1; i >= 0; i--) {
            Class<?> clazz = classes[i];
            generate(clazz, writer, getClassName(clazz.getName()));
        }

        Field[] fields = aClass.getFields();
        writer.write("<p><font face=\"Verdana\"><b>" + prefix + "</b></font></p>");
        writer.write("<table border=\"1\" cellpadding=\"0\" cellspacing=\"0\" bordercolor=\"#CCCCCC\" width=\"66%\">");
        writer.write("<tr>\n");
        writer.write("<td width=\"24%\" align=\"center\"><b><font face=\"Verdana\" color=\"#003399\">Name</font></b></td>\n");
        writer.write("<td width=\"13%\" align=\"center\"><b><font face=\"Verdana\" color=\"#003399\">Image</font></b></td>\n");
        writer.write("<td width=\"32%\" align=\"center\"><b><font face=\"Verdana\" color=\"#003399\">File Name</font></b></td>\n");
        writer.write("<td width=\"31%\" align=\"center\"><b><font face=\"Verdana\" color=\"#003399\">Full Constant Name</font></b></td>\n");
        writer.write("</tr>\n");
        for (Field field : fields) {
            try {
                Object name = field.getName();
                Object value = field.get(aClass);
                writer.write("<tr>\n");
                writer.write("<td align=\"left\"><font face=\"Verdana\">" + name + "</font></td>\n");
                writer.write("<td align=\"center\"><font face=\"Verdana\"><img border=\"0\" src=\"" + value + "\"></font></td>\n");
                writer.write("<td align=\"left\"><font face=\"Verdana\">" + value + "</font></td>\n");
                writer.write("<td align=\"left\"><font face=\"Verdana\">" + prefix + "." + name + "</font></td>\n");
                writer.write("</tr>\n");
            }
            catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        writer.write("</table><br><p>\n");
    }

    /**
     * Gets part of the image from input image icon. It basically takes a snapshot of the input image at {x, y} location
     * and the size is width x height.
     *
     * @param c      the component where the returned icon will be used. The component is used as the ImageObserver. It
     *               could be null.
     * @param icon   the original icon. This is the larger icon where a sub-image will be created using this method.
     * @param x      the x location of the sub-image, relative to the original icon.
     * @param y      the y location of the sub-image, relative to the original icon.
     * @param width  the width of the sub-image. It should be less than the width of the original icon.
     * @param height the height of the sub-image. It should be less than the height of the original icon.
     * @return an new image icon that was part of the input image icon.
     */
    public static ImageIcon getIcon(Component c, ImageIcon icon, int x, int y, int width, int height) {
        return getIcon(c, icon, x, y, width, height, width, height);
    }

    /**
     * Gets part of the image from input image icon. It basically takes a snapshot of the input image at {x, y} location
     * and the size is width x height, then resize it to a size of destWidth x destHeight.
     *
     * @param c          the component where the returned icon will be used. The component is used as the ImageObserver.
     *                   It could be null.
     * @param icon       the original icon. This is the larger icon where a sub-image will be created using this
     *                   method.
     * @param x          the x location of the sub-image, relative to the original icon.
     * @param y          the y location of the sub-image, relative to the original icon.
     * @param width      the width of the sub-image. It should be less than the width of the original icon.
     * @param height     the height of the sub-image. It should be less than the height of the original icon.
     * @param destWidth  the width of the returned icon. The sub-image will be resize if the destWidth is not the same
     *                   as the width.
     * @param destHeight the height of the returned icon. The sub-image will be resize if the destHeight is not the same
     *                   as the height.
     * @return an new image icon that was part of the input image icon.
     */
    public static ImageIcon getIcon(Component c, ImageIcon icon, int x, int y, int width, int height, int destWidth, int destHeight) {
        return getIcon(c, icon, x, y, width, height, BufferedImage.TYPE_INT_ARGB, destWidth, destHeight);
    }

    /**
     * Gets part of the image from input image icon. It basically takes a snapshot of the input image at {x, y} location
     * and the size is width x height.
     *
     * @param c         the component where the returned icon will be used. The component is used as the ImageObserver.
     *                  It could be null.
     * @param icon      the original icon. This is the larger icon where a small icon will be created using this
     *                  method.
     * @param x         the x location of the smaller icon, relative to the original icon.
     * @param y         the y location of the smaller icon, relative to the original icon.
     * @param width     the width of the smaller icon. It should be less than the width of the original icon.
     * @param height    the height of the smaller icon. It should be less than the height of the original icon.
     * @param imageType image type is defined in {@link BufferedImage}, such as {@link BufferedImage#TYPE_INT_ARGB},
     *                  {@link BufferedImage#TYPE_INT_RGB} etc.
     * @return an new image icon that was part of the input image icon.
     */
    public static ImageIcon getIcon(Component c, ImageIcon icon, int x, int y, int width, int height, int imageType) {
        return getIcon(c, icon, x, y, width, height, imageType, width, height);
    }

    /**
     * Gets part of the image from input image icon. It basically takes a snapshot of the input image at {x, y} location
     * and the size is width x height, then resize it to a size of destWidth x destHeight. if the original icon is null
     * or the specified location is outside the original icon, EMPTY_ICON will be returned.
     *
     * @param c          the component where the returned icon will be used. The component is used as the ImageObserver.
     *                   It could be null.
     * @param icon       the original icon. This is the larger icon where a sub-image will be created using this
     *                   method.
     * @param x          the x location of the sub-image, relative to the original icon.
     * @param y          the y location of the sub-image, relative to the original icon.
     * @param width      the width of the sub-image. It should be less than the width of the original icon.
     * @param height     the height of the sub-image. It should be less than the height of the original icon.
     * @param imageType  image type is defined in {@link BufferedImage}, such as {@link BufferedImage#TYPE_INT_ARGB},
     *                   {@link BufferedImage#TYPE_INT_RGB} etc.
     * @param destWidth  the width of the returned icon. The sub-image will be resize if the destWidth is not the same
     *                   as the width.
     * @param destHeight the height of the returned icon. The sub-image will be resize if the destHeight is not the same
     *                   as the height.
     * @return an new image icon that was part of the input image icon.
     */
    public static ImageIcon getIcon(Component c, ImageIcon icon, int x, int y, int width, int height, int imageType, int destWidth, int destHeight) {
        if (icon == null || x < 0 || x + width > icon.getIconWidth() || y < 0 || y + height > icon.getIconHeight()) { // outside the original icon.
            return EMPTY_ICON;
        }
        BufferedImage image = new BufferedImage(destWidth, destHeight, imageType);
        image.getGraphics().drawImage(icon.getImage(), 0, 0, destWidth, destHeight, x, y, x + width, y + height, c);
        return new ImageIcon(image);
    }

    /**
     * Gets a new icon with the overlayIcon paints over the original icon.
     *
     * @param c           the component where the returned icon will be used. The component is used as the
     *                    ImageObserver. It could be null.
     * @param icon        the original icon
     * @param overlayIcon the overlay icon.
     * @param location    the location as defined in SwingConstants - CENTER, NORTH, SOUTH, WEST, EAST, NORTH_EAST,
     *                    NORTH_WEST, SOUTH_WEST and SOUTH_EAST.
     * @return the new icon.
     */
    public static ImageIcon getOverlayIcon(Component c, ImageIcon icon, ImageIcon overlayIcon, int location) {
        return getOverlayIcon(c, icon, overlayIcon, location, new Insets(0, 0, 0, 0));
    }

    /**
     * Gets a new icon with the overlayIcon paints over the original icon.
     *
     * @param c           the component where the returned icon will be used. The component is used as the
     *                    ImageObserver. It could be null.
     * @param icon        the original icon
     * @param overlayIcon the overlay icon.
     * @param location    the location as defined in SwingConstants - CENTER, NORTH, SOUTH, WEST, EAST, NORTH_EAST,
     *                    NORTH_WEST, SOUTH_WEST and SOUTH_EAST.
     * @param insets      the insets to the border. This parameter has no effect if the location is CENTER. For example,
     *                    if the location is WEST, insets.left will be the gap of the left side of the original icon and
     *                    the left side of the overlay icon.
     * @return the new icon.
     */
    public static ImageIcon getOverlayIcon(Component c, ImageIcon icon, ImageIcon overlayIcon, int location, Insets insets) {
        int x = -1, y = -1;
        int w = icon.getIconWidth();
        int h = icon.getIconHeight();
        int sw = overlayIcon.getIconWidth();
        int sh = overlayIcon.getIconHeight();
        switch (location) {
            case SwingConstants.CENTER:
                x = (w - sw) / 2;
                y = (h - sh) / 2;
                break;
            case SwingConstants.NORTH:
                x = (w - sw) / 2;
                y = insets.top;
                break;
            case SwingConstants.SOUTH:
                x = (w - sw) / 2;
                y = h - insets.bottom - sh;
                break;
            case SwingConstants.WEST:
                x = insets.left;
                y = (h - sh) / 2;
                break;
            case SwingConstants.EAST:
                x = w - insets.right - sw;
                y = (h - sh) / 2;
                break;
            case SwingConstants.NORTH_EAST:
                x = w - insets.right - sw;
                y = insets.top;
                break;
            case SwingConstants.NORTH_WEST:
                x = insets.left;
                y = insets.top;
                break;
            case SwingConstants.SOUTH_WEST:
                x = insets.left;
                y = h - insets.bottom - sh;
                break;
            case SwingConstants.SOUTH_EAST:
                x = w - insets.right - sw;
                y = h - insets.bottom - sh;
                break;
        }
        return getOverlayIcon(c, icon, overlayIcon, x, y);
    }

    /**
     * Gets a new icon with the overlayIcon paints over the original icon.
     *
     * @param c           the component where the returned icon will be used. The component is used as the
     *                    ImageObserver. It could be null.
     * @param icon        the original icon
     * @param overlayIcon the overlay icon.
     * @param x           the x location relative to the original icon where the overlayIcon will be pained.
     * @param y           the y location relative to the original icon where the overlayIcon will be pained.
     * @return the overlay icon
     */
    public static ImageIcon getOverlayIcon(Component c, ImageIcon icon, ImageIcon overlayIcon, int x, int y) {
        int w = icon == null ? overlayIcon.getIconWidth() : icon.getIconWidth();
        int h = icon == null ? overlayIcon.getIconHeight() : icon.getIconHeight();
        int sw = overlayIcon.getIconWidth();
        int sh = overlayIcon.getIconHeight();
        if (x != -1 && y != -1) {
            BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            if (icon != null) {
                image.getGraphics().drawImage(icon.getImage(), 0, 0, w, h, c);
            }
            image.getGraphics().drawImage(overlayIcon.getImage(), x, y, sw, sh, c);
            return new ImageIcon(image);
        }
        else {
            return icon;
        }
    }

    /**
     * Gets a new icon with the icon2 painting right or down to the icon1.
     *
     * @param c           the component where the returned icon will be used. The component is used as the
     *                    ImageObserver. It could be null
     * @param icon1       the left side or up side icon
     * @param icon2       the right side or down side icon
     * @param orientation the orientation as defined in SwingConstants - HORIZONTAL, VERTICAL
     * @param gap         the gap between the two icons
     * @return the new icon.
     */
    public static ImageIcon getCombinedIcon(Component c, ImageIcon icon1, ImageIcon icon2, int orientation, int gap) {
        if (icon1 == null) {
            return icon2;
        }
        if (icon2 == null) {
            return icon1;
        }
        int x1, y1, x2, y2, width, height;
        int w1 = icon1.getIconWidth();
        int h1 = icon1.getIconHeight();
        int w2 = icon2.getIconWidth();
        int h2 = icon2.getIconHeight();

        if (orientation == SwingConstants.HORIZONTAL) {
            width = w1 + w2 + gap;
            height = Math.max(h1, h2);
            x1 = 0;
            x2 = w1 + gap;
            y1 = h1 > h2 ? 0 : (h2 - h1) / 2;
            y2 = h1 < h2 ? 0 : (h1 - h2) / 2;
        }
        else {
            width = Math.max(w1, w2);
            height = h1 + h2 + gap;
            x1 = w1 > w2 ? 0 : (w2 - w1) / 2;
            x2 = w1 < w2 ? 0 : (w1 - w2) / 2;
            y1 = 0;
            y2 = h1 + gap;
        }

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        image.getGraphics().drawImage(icon1.getImage(), x1, y1, w1, h1, c);
        image.getGraphics().drawImage(icon2.getImage(), x2, y2, w2, h2, c);
        return new ImageIcon(image);
    }

    /**
     * Gets a scaled version of the existing icon.
     *
     * @param c    the component where the returned icon will be used. The component is used as the ImageObserver. It
     *             could be null.
     * @param icon the original icon
     * @param w    the new width
     * @param h    the new height
     * @return the scaled icon
     */
    public static ImageIcon getScaledImage(Component c, ImageIcon icon, int w, int h) {
        if (w >= icon.getIconWidth() / 2) {
            BufferedImage temp = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = temp.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.drawImage(icon.getImage(), 0, 0, temp.getWidth(), temp.getHeight(), c);
            g2.dispose();
            return new ImageIcon(temp);
        }
        else {
            BufferedImage temp = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = temp.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(icon.getImage(), 0, 0, temp.getWidth(), temp.getHeight(), c);
            g2.dispose();
            return new ImageIcon(JideSwingUtilities.getFasterScaledInstance(temp, w, h, RenderingHints.VALUE_INTERPOLATION_BILINEAR, true));
        }
    }
}
