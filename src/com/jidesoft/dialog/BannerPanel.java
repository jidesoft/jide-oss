/*
 * @(#)BannerPanel.java
 *
 * Copyright 2002 - 2003 JIDE Software. All rights reserved.
 */
package com.jidesoft.dialog;

import com.jidesoft.swing.JideSwingUtilities;
import com.jidesoft.swing.MultilineLabel;
import com.jidesoft.utils.SecurityUtils;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * <code>BannerPanel</code> is a panel that can show title, subtitle and icon with title on top, subtitle on the bottom
 * and icon on the left. You can use ImageIcon as the icon but you can also use your own component as the icon component
 * by using {@link #setIconComponent(javax.swing.JComponent)}.
 * <p/>
 * <code>BannerPanel</code> can be placed on top of any dialog or any panel to show some help information or display a
 * product logo.
 */
public class BannerPanel extends JPanel {

    /**
     * Title of the banner panel.
     */
    protected String _title;

    /**
     * Subtitle or description of the banner panel.
     */
    protected String _subtitle;

    /**
     * Icon for the banner panel.
     */
    protected ImageIcon _titleIcon;


    public static final String TITLE_PROPERTY = "title";
    public static final String SUBTITLE_PROPERTY = "subTitle";
    public static final String ICON_PROPERTY = "icon";
    public static final String ICON_COMPONENT_PROPERTY = "iconComponent";
    public static final String PROPERTY_TITLE_FONT = "titleFont";
    public static final String PROPERTY_SUBTITLE_FONT = "subTitleFont";
    public static final String PROPERTY_TITLE_ICON_LOCATION = "titleIconLocation";


    /**
     * A component to be placed at position of icon.
     */
    private JComponent _iconComponent;

    protected int _subTitleIndent = 20;
    protected Font _titleFont;
    protected Color _titleColor;
    protected Font _subTitleFont;
    protected Color _subTitleColor;

    protected Paint _backgroundPaint;
    protected PropertyChangeListener _propertyListener;

    private JLabel _titleLabel;
    private MultilineLabel _subtitleLabel;

    protected Color _startColor;
    protected Color _endColor;
    protected boolean _isVertical;

    private int _titleIconLocation = SwingConstants.TRAILING;
    public JPanel _textPanel;

    /**
     * Creates an empty BannerPanel.
     */
    public BannerPanel() {
        lazyInitialize();
    }

    /**
     * Creates a BannerPanel with title and subtitle.
     *
     * @param title the title.
     */
    public BannerPanel(String title) {
        setTitle(title);
        lazyInitialize();
    }

    /**
     * Creates a BannerPanel with title and subtitle.
     *
     * @param title    the title.
     * @param subtitle the sub title.
     */
    public BannerPanel(String title, String subtitle) {
        setTitle(title);
        setSubtitle(subtitle);
        lazyInitialize();
    }

    /**
     * Creates a BannerPanel with title, subtitle and icon.
     *
     * @param title     the title.
     * @param subtitle  the sub title.
     * @param titleIcon the icon.
     */
    public BannerPanel(String title, String subtitle, ImageIcon titleIcon) {
        setTitle(title);
        setSubtitle(subtitle);
        setTitleIcon(titleIcon);
        lazyInitialize();
    }

    /**
     * Creates a BannerPanel with title, subtitle and component.
     *
     * @param title         the title.
     * @param subtitle      the sub title.
     * @param iconComponent the icon component. It will appear where the icon is if using constructor {@link
     *                      #BannerPanel(String,String,javax.swing.ImageIcon)}.
     */
    public BannerPanel(String title, String subtitle, JComponent iconComponent) {
        setTitle(title);
        setSubtitle(subtitle);
        _iconComponent = iconComponent;
        lazyInitialize();
    }

    public void lazyInitialize() {
        removeAll();
        _textPanel = new JPanel(new BorderLayout(5, 5));
        _textPanel.setOpaque(false);
        _textPanel.setBorder(BorderFactory.createEmptyBorder(3, 10, 2, 10));

        if (getSubTitleFont() == null) {
            setSubTitleFont(getFont());
        }

        _subtitleLabel = new MultilineLabel(getSubtitle()) {
            @Override
            public Dimension getMinimumSize() {
                return new Dimension(0, 0);
            }
        };
        _subtitleLabel.setFont(getSubTitleFont());
        if (getSubTitleColor() == null) {
            setSubTitleColor(getForeground());
        }
        _subtitleLabel.setForeground(getSubTitleColor());
        _subtitleLabel.setBorder(BorderFactory.createEmptyBorder(0, getSubTitleIndent(), 0, 0));
        _textPanel.add(_subtitleLabel, BorderLayout.CENTER);

        _titleLabel = new JLabel(getTitle()) {
            @Override
            public Dimension getMinimumSize() {
                return new Dimension(0, super.getMinimumSize().height);
            }
        };
        if (getTitleFont() == null) {
            setTitleFont(SecurityUtils.createFont(getFont().getFontName(), Font.BOLD, getFont().getSize() + 2));
        }
        _titleLabel.setFont(getTitleFont());
        if (getTitleColor() == null) {
            setTitleColor(getForeground());
        }
        _titleLabel.setForeground(getTitleColor());
        if (getSubtitle() != null && getSubtitle().length() != 0) {
            _textPanel.add(_titleLabel, BorderLayout.BEFORE_FIRST_LINE);
        }
        else {
            _textPanel.add(_titleLabel, BorderLayout.CENTER);
        }

        if (getTitleIcon() == null && _iconComponent == null) {
            _iconComponent = new JLabel("");
        }
        else if (getTitleIcon() == null && _iconComponent != null) {
        }
        else {
            _iconComponent = new JLabel(getTitleIcon());
        }
        _iconComponent.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        setLayout(new BorderLayout(5, 0));
        add(_textPanel, BorderLayout.CENTER);
        addIconComponent(_iconComponent);

        _propertyListener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (_titleLabel != null && TITLE_PROPERTY.equals(evt.getPropertyName())) {
                    _titleLabel.setText((String) evt.getNewValue());
                }
                else if (_subtitleLabel != null && SUBTITLE_PROPERTY.equals(evt.getPropertyName())) {
                    String text = (String) evt.getNewValue();
                    _subtitleLabel.setText(text);
                    if (text != null && text.length() != 0) {
                        _textPanel.add(_titleLabel, BorderLayout.BEFORE_FIRST_LINE);
                        _textPanel.add(_subtitleLabel, BorderLayout.CENTER);
                    }
                    else {
                        _textPanel.add(_titleLabel, BorderLayout.CENTER);
                    }
                }
                else if (ICON_PROPERTY.equals(evt.getPropertyName())) {
                    if (_iconComponent instanceof JLabel) {
                        ((JLabel) _iconComponent).setIcon(getTitleIcon());
                    }
                }
                else if (ICON_COMPONENT_PROPERTY.equals(evt.getPropertyName())) {
                    if (evt.getOldValue() instanceof JComponent) {
                        _textPanel.remove((JComponent) evt.getOldValue());
                    }
                    if (evt.getNewValue() instanceof JComponent) {
                        addIconComponent((JComponent) evt.getNewValue());
                    }
                }
                else if (PROPERTY_TITLE_FONT.equals(evt.getPropertyName())) {
                    if (_titleLabel != null) {
                        _titleLabel.setFont((Font) evt.getNewValue());
                    }
                }
                else if (PROPERTY_SUBTITLE_FONT.equals(evt.getPropertyName())) {
                    if (_subtitleLabel != null) {
                        _subtitleLabel.setFont((Font) evt.getNewValue());
                    }
                }
                else if (PROPERTY_TITLE_ICON_LOCATION.equals(evt.getPropertyName())) {
                    addIconComponent(_iconComponent);
                }
            }
        };
        addPropertyChangeListener(_propertyListener);
    }

    private void addIconComponent(JComponent component) {
        if (component != null) {
            switch (getTitleIconLocation()) {
                case SwingConstants.EAST:
                    add(component, BorderLayout.EAST);
                    break;
                case SwingConstants.WEST:
                    add(component, BorderLayout.WEST);
                    break;
                case SwingConstants.LEADING:
                    add(component, BorderLayout.BEFORE_LINE_BEGINS);
                    break;
                case SwingConstants.TRAILING:
                    add(component, BorderLayout.AFTER_LINE_ENDS);
                    break;
            }
        }
    }

    /**
     * Prepares the title icon.
     *
     * @param icon the input icon fro setTitleIcon(icon).
     * @return the image icon after processing. By default it will return the same image icon. Subclass can override it
     *         to scale the image or do other processing.
     */
    protected ImageIcon prepareTitleIcon(ImageIcon icon) {
        return icon;
    }

    /**
     * Gets the Paint used to paint the background of the BannerPanel.
     *
     * @return the Paint used to paint the background.
     */
    public Paint getBackgroundPaint() {
        return _backgroundPaint;
    }

    /**
     * Sets the Paint used to paint the background of the BannerPanel. User can set the paint to a gradient paint to
     * make the BannerPanel looks attractive.
     *
     * @param backgroundPaint the background paint.
     */
    public void setBackgroundPaint(Paint backgroundPaint) {
        _backgroundPaint = backgroundPaint;
    }

    /**
     * This method allows you to use gradient background without using {@link #setBackgroundPaint(java.awt.Paint)}
     * method. You can use GradientPaint to do the same thing. However if you use this method, it will use fast gradient
     * paint defined in JideSwingUtilities to do the painting.
     *
     * @param startColor start color of the gradient
     * @param endColor   end color of the gradient
     * @param isVertical vertical or not
     */
    public void setGradientPaint(Color startColor, Color endColor, boolean isVertical) {
        setStartColor(startColor);
        setEndColor(endColor);
        setVertical(isVertical);
    }

    /**
     * Paints the background.
     *
     * @param g the Graphics
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (getStartColor() != null && getEndColor() != null) {
            JideSwingUtilities.fillGradient((Graphics2D) g, new Rectangle(0, 0, getWidth(), getHeight()), getStartColor(), getEndColor(), isVertical());
        }
        else if (getBackgroundPaint() != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setPaint(getBackgroundPaint());
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    /**
     * Gets the title.
     *
     * @return the title of the banner panel.
     */
    public String getTitle() {
        return _title;
    }

    /**
     * Sets the title.
     *
     * @param title the new title.
     */
    public void setTitle(String title) {
        String old = _title;
        _title = title;
        firePropertyChange(TITLE_PROPERTY, old, _title);
    }

    /**
     * Gets the subtitle.
     *
     * @return the subtitle
     */
    public String getSubtitle() {
        return _subtitle;
    }

    /**
     * Sets the subtitle.
     *
     * @param subtitle the new subtitle.
     */
    public void setSubtitle(String subtitle) {
        String old = _subtitle;
        _subtitle = subtitle;
        firePropertyChange(SUBTITLE_PROPERTY, old, _subtitle);
    }

    /**
     * Gets the title icon.
     *
     * @return the title icon
     */
    public ImageIcon getTitleIcon() {
        return _titleIcon;
    }

    /**
     * Sets the title icon.
     *
     * @param titleIcon the new titleIcon.
     */
    public void setTitleIcon(ImageIcon titleIcon) {
        ImageIcon old = _titleIcon;
        _titleIcon = prepareTitleIcon(titleIcon);
        firePropertyChange(ICON_PROPERTY, old, _titleIcon);
    }


    /**
     * Gets the icon component. If you use constructor {@link #BannerPanel(String,String,javax.swing.ImageIcon)}, the
     * icon component will be a JLabel with the icon in the 3rd parameter. If you use the constructor {@link
     * #BannerPanel(String,String,javax.swing.JComponent)}, it will return the component as in the 3rd parameter.
     *
     * @return the icon component
     */
    public JComponent getIconComponent() {
        return _iconComponent;
    }

    /**
     * Sets the icon component.
     *
     * @param iconComponent the component that is used as the icon.
     */
    public void setIconComponent(JComponent iconComponent) {
        JComponent old = _iconComponent;
        _iconComponent = iconComponent;
        firePropertyChange(ICON_COMPONENT_PROPERTY, old, _iconComponent);
    }

    /**
     * Gets the font of the subtitle.
     *
     * @return the font of the subtitle
     */
    public Font getSubTitleFont() {
        return _subTitleFont;
    }

    /**
     * Sets the font for the subtitle.
     *
     * @param subTitleFont the new font for the subtitle.
     */
    public void setSubTitleFont(Font subTitleFont) {
        Font old = _subTitleFont;
        _subTitleFont = subTitleFont;
        firePropertyChange(PROPERTY_SUBTITLE_FONT, old, _subTitleFont);
    }

    /**
     * Gets the font of the title.
     *
     * @return the font of the title
     */
    public Font getTitleFont() {
        return _titleFont;
    }

    /**
     * Sets the font for the title.
     *
     * @param titleFont the new font for the title.
     */
    public void setTitleFont(Font titleFont) {
        Font old = _titleFont;
        _titleFont = titleFont;
        firePropertyChange(PROPERTY_TITLE_FONT, old, _titleFont);
    }

    /**
     * Gets the subtitle indent.
     *
     * @return the subtitle indent.
     */
    public int getSubTitleIndent() {
        return _subTitleIndent;
    }

    /**
     * Sets the subtitle indent. Subtitle is always behind the title. The indent will decide how behind. It's in
     * pixels.
     *
     * @param subTitleIndent the new index.
     */
    public void setSubTitleIndent(int subTitleIndent) {
        _subTitleIndent = subTitleIndent;
    }

    /**
     * Gets the title color.
     *
     * @return the color of title.
     */
    public Color getTitleColor() {
        return _titleColor;
    }

    /**
     * Sets the title color.
     *
     * @param titleColor the text color for the title.
     */
    public void setTitleColor(Color titleColor) {
        _titleColor = titleColor;
        if (_titleLabel != null) {
            _titleLabel.setForeground(titleColor);
        }
    }

    /**
     * Gets the subtitle color.
     *
     * @return the color of subtitle.
     */
    public Color getSubTitleColor() {
        return _subTitleColor;
    }

    /**
     * Sets the subtitle color.
     *
     * @param subTitleColor the text color for the subtitle.
     */
    public void setSubTitleColor(Color subTitleColor) {
        _subTitleColor = subTitleColor;
        if (_subtitleLabel != null) {
            _subtitleLabel.setForeground(subTitleColor);
        }
    }

    @Override
    public void setBackground(Color bg) {
        super.setBackground(bg);
        if (_titleLabel != null) {
            _titleLabel.setBackground(bg);
        }
        if (_subtitleLabel != null) {
            _subtitleLabel.setBackground(bg);
        }
        if (_iconComponent != null) {
            _iconComponent.setBackground(bg);
        }
    }

    @Override
    public void setForeground(Color fg) {
        super.setForeground(fg);
        setTitleColor(fg);
        setSubTitleColor(fg);
        if (_iconComponent != null) {
            _iconComponent.setForeground(fg);
        }
    }

    public Color getStartColor() {
        return _startColor;
    }

    public void setStartColor(Color startColor) {
        _startColor = startColor;
    }

    public Color getEndColor() {
        return _endColor;
    }

    public void setEndColor(Color endColor) {
        _endColor = endColor;
    }

    public boolean isVertical() {
        return _isVertical;
    }

    public void setVertical(boolean vertical) {
        _isVertical = vertical;
    }

    /**
     * Gets the title icon location. By default, it is SwingConstants.TRAILING.
     *
     * @return the title icon location.
     */
    public int getTitleIconLocation() {
        return _titleIconLocation;
    }

    /**
     * Sets the title icon location. By default the title icon is added a border layout using
     * BorderLayout.AFTER_LINE_ENDS. However you can use this method to decide where to add. Valid values are
     * SwingContants.EAST and SwingContants.WEST as well as SwingContants.LEADING and SwingContants.TRAILING considering
     * the case of both RTL and LTR.
     *
     * @param titleIconLocation the title icon location.
     */
    public void setTitleIconLocation(int titleIconLocation) {
        int old = _titleIconLocation;
        if (old != titleIconLocation) {
            _titleIconLocation = titleIconLocation;
            firePropertyChange(PROPERTY_TITLE_ICON_LOCATION, old, _titleIconLocation);
        }
    }

    /**
     * Gets the component for the title.
     *
     * @return a JLabel.
     */
    public JComponent getTitleLabel() {
        return _titleLabel;
    }

    /**
     * Gets the component for the subtitle.
     *
     * @return a MultilineLabel
     */
    public JComponent getSubtitleLabel() {
        return _subtitleLabel;
    }
}
