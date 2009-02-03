/*
 * Copyright 2005 Patrick Gotthardt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jidesoft.swing;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * <code>StyledLabelBuilder</code> is a quick way to define StyledLabel. It provides two ways to handle the creation and
 * modification of StyleLabels.
 * <p/>
 * The first is to use it as a builder (thus the name). This way is preferred if you want to create a StyledLabel with a
 * specific format and partially generic content. Example:
 * <pre><code>StyledLabel label = new StyledLabelBuilder()
 * 	.add(file.getName())
 * 	.add(" (", Font.BOLD)
 * 	.add(file.getPath(), "italic") // using annotation style - see section two for information about annotations
 * 	.add(")", Font.BOLD)
 * 	.createLabel();</code></pre>
 * This code would be used to create a label like "something.txt (/temp/something.txt)" with some styling (the braces
 * would be bold, the path would be italic). In case you find yourself reusing a specific style quite often in such a
 * label you might consider to create a style for it. This can be done with the help of the {@link #register}-methods.
 * As an example, the code above could be rewritten like this (though it only pays off when used for creation of longer
 * styles):
 * <pre><code>StyledLabelBuilder builder = new StyledLabelBuilder()
 * 	.register("OPERATOR", Font.BOLD, new Color(0x000052)) // use parameters
 * 	.register("PATH", "italic, f:#0000CD"); // or style annotations
 * StyledLabel label = builder
 * 	.add(file.getName())
 * 	.add(" (", "OPERATOR")
 * 	.add(file.getPath(), "PATH,underlined") // use a style + style annotation
 * 	.add(")", "OPERATOR")
 * 	.createLabel();</code></pre>
 * Note that we're using different font colors this time. It pays off as soon as you want to modify a specific group of
 * text parts or as your styles start to get more complicated. The {@link #clear()}-method is very useful if you want to
 * use these styles. Instead of re-creating a new builder each time, you can use the clear-method to clear the internal
 * buffer of text without removing the previously defined styles.
 * <p/>
 * Let's have an example (we're going to reuse the code from above!):
 * <pre><code>builder.clear();
 * builder
 * 	.add(file.getName())
 * 	.add(" (", "OPERATOR")
 * 	.add(file.getPath(), "PATH")
 * 	.add(")", "OPERATOR")
 *  .configure(label);</code></pre>
 * <p/>
 * If we were using Java 5, we could also do this:
 * <pre><code>// no need to call {@link #clear()} this time
 * builder.configure(label, String.format("%s ({%s:PATH})", file.getName(), file.getPath()));</code></pre>
 * <p/>
 * Each of the {@link #add} and {@link #register} methods is the same as using the corresponding StyleRange-constructor
 * directly (except that you don't have to care about its start and length).
 * <p/>
 * The second, even more advanced, way to use this class is in combination with an annotated string. Using the static
 * {@link #setStyledText} or {@link #createStyledLabel} methods you can create a fully styled label from just on string.
 * This is ideal if you need the string to be configurable or locale-specific. The usage is even more easy than the
 * builder-approach: <code>StyledLabel label = StyledLabelBuilder.createStyledLabel("I'm your {first:bold} styled
 * {label:italic}!");</code> In the above example, the resulting label would have a a bold "first" and an italic
 * "label". Each annotation is started by a "{" and ended by a "}". The text you want to be styled accordingly is
 * separated from its annotations by a ":". If your text needs to contain a ":" itself, you need to escape it using the
 * "\" character. The same goes for "{" that are not supposed to start an annotation. You don't need to escape the "}"
 * at all. If it is used within the annotated string it'll be ignored. It only counts after the annotation separator
 * (":"). There are multiply annotations available. Each annotation offers a shortcut made up from one or two of their
 * characters. For example: We used "bold" and "italic" in the example above, but we could've used "b" and "i" instead.
 * It is also possible to combine multiple styles by separating them with a ",". As an example: <code>{This text is
 * bold, italic and blue:b,i,f:blue}</code> Instead of writing "b,i" you can also write "bi" or "bolditalic". This
 * example brings us to colors. They've to be started with "f" or "font" for the font-color or "l" or "line" for the
 * line-color or "b" or "background" for the background color. There are a lot of ways to specify a color. You may use
 * its HTML name (as I did in the above example) or any of these: f:(0,0,255) f:#00F l:#0000FF l:0x0000FF The "#00F"
 * notation is just like it is in CSS. It is the same as if you had written "#0000FF". You can get and modify the map of
 * color-names the parser is using with the static {@link #getColorNamesMap()}-method.
 * <p/>
 * You saw some styles above. Here is a complete list of styles and its shortcut.
 * <p/>
 * <b>Font styles</b> <ul> <li>plain or p <li>bold or b <li>italic or i <li>bolditalic or bi </ul> <b>Additional
 * styles</b> <ul> <li>strike or s <li>doublestrike or ds <li>waved or w <li>underlined or u <li>dotted or d
 * <li>superscript or sp <li>subscript or sb </ul>
 * <p/>
 *
 * @author Patrick Gotthardt
 */
public class StyledLabelBuilder {
    private StringBuffer buffer;
    private List ranges;
    private int start;
    private Map styles;

    public StyledLabelBuilder() {
        buffer = new StringBuffer();
        ranges = new ArrayList();
        styles = new HashMap();
        start = 0;
    }

    public void clear() {
        buffer.delete(0, buffer.length());
        ranges.clear();
        start = 0;
    }

    public StyledLabelBuilder register(String text, Color fontColor) {
        styles.put(text, new StyleRange(fontColor));
        return this;
    }

    public StyledLabelBuilder register(String text, int fontStyle) {
        styles.put(text, new StyleRange(fontStyle));
        return this;
    }

    public StyledLabelBuilder register(String text, int fontStyle, Color fontColor) {
        styles.put(text, new StyleRange(fontStyle, fontColor));
        return this;
    }

    public StyledLabelBuilder register(String text, int fontStyle, Color fontColor, int additionalStyle) {
        styles.put(text, new StyleRange(start, text.length(), fontStyle, fontColor, additionalStyle));
        return this;
    }

    public StyledLabelBuilder register(String text, int fontStyle, Color fontColor, int additionalStyle, Color lineColor) {
        styles.put(text, new StyleRange(start, text.length(), fontStyle, fontColor, additionalStyle, lineColor));
        return this;
    }

    public StyledLabelBuilder register(String text, int fontStyle, Color fontColor, int additionalStyle, Color lineColor, Stroke lineStroke) {
        styles.put(text, new StyleRange(start, text.length(), fontStyle, fontColor, additionalStyle, lineColor, lineStroke));
        return this;
    }

    public StyledLabelBuilder register(String text, int fontStyle, Color fontColor, int additionalStyle, Color lineColor, Stroke lineStroke, float fontShrinkRatio) {
        styles.put(text, new StyleRange(start, text.length(), fontStyle, fontColor, additionalStyle, lineColor, lineStroke, fontShrinkRatio));
        return this;
    }

    public StyledLabelBuilder register(String text, int fontStyle, int additionalStyle) {
        styles.put(text, new StyleRange(start, text.length(), fontStyle, additionalStyle));
        return this;
    }

    public StyledLabelBuilder register(String text, int fontStyle, int additionalStyle, float fontShrinkRatio) {
        styles.put(text, new StyleRange(start, text.length(), fontStyle, additionalStyle, fontShrinkRatio));
        return this;
    }

    public StyledLabelBuilder register(String text, String format) {
        ParsedStyleResult result = parseStyleAnnotation(format.toCharArray(), 0, this);
        styles.put(text, new StyleRange(result.fontStyle, result.fontColor, result.backgroundColor,
                result.additionalStyle, result.lineColor));
        return this;
    }

    //

    public StyledLabelBuilder add(String text) {
        buffer.append(text);
        start += text.length();
        return this;
    }

    public StyledLabelBuilder add(String text, Color fontColor) {
        ranges.add(new StyleRange(start, text.length(), fontColor));
        return add(text);
    }

    public StyledLabelBuilder add(String text, int fontStyle) {
        ranges.add(new StyleRange(start, text.length(), fontStyle));
        return add(text);
    }

    public StyledLabelBuilder add(String text, int fontStyle, Color fontColor) {
        ranges.add(new StyleRange(start, text.length(), fontStyle, fontColor));
        return add(text);
    }

    public StyledLabelBuilder add(String text, int fontStyle, Color fontColor, int additionalStyle) {
        ranges.add(new StyleRange(start, text.length(), fontStyle, fontColor, additionalStyle));
        return add(text);
    }

    public StyledLabelBuilder add(String text, int fontStyle, Color fontColor, int additionalStyle, Color lineColor) {
        ranges.add(new StyleRange(start, text.length(), fontStyle, fontColor, additionalStyle, lineColor));
        return add(text);
    }

    public StyledLabelBuilder add(String text, int fontStyle, Color fontColor, Color backgroundColor, int additionalStyle, Color lineColor) {
        ranges.add(new StyleRange(start, text.length(), fontStyle, fontColor, backgroundColor, additionalStyle, lineColor));
        return add(text);
    }

    public StyledLabelBuilder add(String text, int fontStyle, Color fontColor, Color backgroundColor, int additionalStyle, Color lineColor, Stroke lineStroke) {
        ranges.add(new StyleRange(start, text.length(), fontStyle, fontColor, backgroundColor, additionalStyle, lineColor, lineStroke));
        return add(text);
    }

    public StyledLabelBuilder add(String text, int fontStyle, Color fontColor, int additionalStyle, Color lineColor, Stroke lineStroke, float fontShrinkRatio) {
        ranges.add(new StyleRange(start, text.length(), fontStyle, fontColor, additionalStyle, lineColor, lineStroke, fontShrinkRatio));
        return add(text);
    }

    public StyledLabelBuilder add(String text, String style) {
        StyleRange range = (StyleRange) styles.get(style);
        // not a stored style, thus it might be an annotation
        if (range == null) {
            ParsedStyleResult result = parseStyleAnnotation(style.toCharArray(), 0, this);
            return add(text, result.fontStyle, result.fontColor, result.backgroundColor, result.additionalStyle, result.lineColor);
        }
        return add(text, range.getFontStyle(), range.getFontColor(), range.getAdditionalStyle(), range.getLineColor(), range.getLineStroke(), range.getFontShrinkRatio());
    }

    public StyledLabelBuilder add(String text, int fontStyle, int additionalStyle) {
        ranges.add(new StyleRange(start, text.length(), fontStyle, additionalStyle));
        return add(text);
    }

    public StyledLabelBuilder add(String text, int fontStyle, int additionalStyle, float fontShrinkRatio) {
        ranges.add(new StyleRange(start, text.length(), fontStyle, additionalStyle, fontShrinkRatio));
        return add(text);
    }

    public StyledLabel configure(StyledLabel label, String style) {
        StyledLabelBuilder.setStyledText(label, style, this);
        return label;
    }

    public StyledLabel configure(StyledLabel label) {
        label.setText(buffer.toString());
        int size = ranges.size();
        for (int i = 0; i < size; i++) {
            label.addStyleRange((StyleRange) ranges.get(i));
        }
        return label;
    }

    public StyledLabel createLabel() {
        return configure(new StyledLabel());
    }

    // complex part
    public static StyledLabel createStyledLabel(String text) {
        StyledLabel label = new StyledLabel();
        setStyledText(label, text);
        return label;
    }

    /**
     * Before your call this method, you need call {link@ #parseToVoidStyledTextConfusion(String)} to make sure the text
     * will not contain confusion "\" or "{"
     *
     * @param label the styledLabel to be set with the text
     * @param text  the styled text
     */
    public static void setStyledText(StyledLabel label, String text) {
        setStyledText(label, text.toCharArray());
    }

    private static void setStyledText(StyledLabel label, String text, StyledLabelBuilder builder) {
        setStyledText(label, text.toCharArray(), builder);
    }

    /**
     * Before your call this method, you need call {link@ #parseToVoidStyledTextConfusion(String)} to make sure the text
     * will not contain confusion "\" or "{"
     *
     * @param label the styledLabel to be set with the text
     * @param text  the styled text
     */
    public static void setStyledText(StyledLabel label, char[] text) {
        setStyledText(label, text, null);
    }

    private static void setStyledText(StyledLabel label, char[] text, StyledLabelBuilder builder) {
        StringBuffer labelText = new StringBuffer(text.length);
        boolean escaped = false;
        label.clearStyleRanges();
        for (int i = 0; i < text.length; i++) {
            if (escaped) {
                labelText.append(text[i]);
                escaped = false;
                continue;
            }
            switch (text[i]) {
                case '{':
                    ParsedStyleResult result = parseStylePart(text, i + 1, builder);
                    int realIndex = labelText.length();
                    labelText.append(result.text);
                    if (result.text.length() > 0) {
                        label.addStyleRange(new StyleRange(
                                realIndex, result.text.length(),
                                result.fontStyle, result.fontColor, result.backgroundColor,
                                result.additionalStyle, result.lineColor));
                    }
                    i = result.endOffset;
                    break;
                case '\\':
                    escaped = true;
                    break;
                default:
                    labelText.append(text[i]);
                    break;
            }
        }
        label.setText(labelText.toString());
    }

    /**
     * This method need to be invoked to format your string before you invoke {@link #setStyledText(StyledLabel,
     * String)} or {@link #setStyledText(StyledLabel, char[])}
     *
     * @param originalString the original string.
     * @return a parsed string with "\" replaced by "\\" and "{" replaced by "\{".
     */
    public static String parseToVoidStyledTextConfusion(String originalString) {
        String destString = originalString.replaceAll("\\\\", "\\\\\\\\");
        destString = destString.replaceAll("\\{", "\\\\{");
        return destString;
    }

    private static ParsedStyleResult parseStylePart(char[] text, int start, StyledLabelBuilder builder) {
        ParsedStyleResult result = new ParsedStyleResult();
        int findIndex, i = start;
        // find end of text first
        findIndex = findNext(text, ':', i);
        result.text = createTrimmedString(text, i, findIndex - 1);
        return parseStyleAnnotation(text, findIndex + 1, builder, result);
    }

    private static ParsedStyleResult parseStyleAnnotation(char[] text, int start, StyledLabelBuilder builder) {
        ParsedStyleResult result = new ParsedStyleResult();
        return parseStyleAnnotation(text, start, builder, result);
    }

    private static ParsedStyleResult parseStyleAnnotation(char[] text, int findIndex, StyledLabelBuilder builder, ParsedStyleResult result) {
        int i = findIndex;
        char[] importantChars = {',', '}'};
        boolean endOfTag = false;
        while (i < text.length && !endOfTag) {
            findIndex = findNextOf(text, importantChars, i);
            String style;
            if (findIndex == -1 || text[findIndex] == '}') {
                endOfTag = true;
            }
            style = createTrimmedString(text, i, findIndex == -1 ? text.length - 1 : findIndex - 1);
            // start with colors first - they're easiest to guess
            int colonIndex = style.indexOf(':');
            if (colonIndex != -1) {
                String color = style.substring(colonIndex + 1);
                // the (r,g,b)-construct allows "," thus we'll have to handle it here!
                if (color.length() > 1) {
                    if (color.charAt(0) == '(') {
                        findIndex = findNext(text, ')', i + colonIndex + 1);
                        style = createTrimmedString(text, i, findIndex + 1);
                        color = style.substring(colonIndex + 1);
                        // we need to do some specific checking here
                        if (text[findIndex + 1] == '}') {
                            endOfTag = true;
                        }
                        // in any case: the cursor needs to be moved forward by one
                        findIndex++;
                    }
                    if (style.charAt(0) == 'f') {
                        result.fontColor = toColor(color);
                    }
                    else if (style.charAt(0) == 'b') {
                        result.backgroundColor = toColor(color);
                    }
                    else {
                        result.lineColor = toColor(color);
                    }
                }
            }
            else {
                // no color, now it's getting though
                if (style.equals("plain") || style.equals("p")) {
                    result.fontStyle = Font.PLAIN;

                }
                else if (style.equals("bold") || style.equals("b")) {
                    result.fontStyle = Font.BOLD;

                }
                else if (style.equals("italic") || style.equals("i")) {
                    result.fontStyle = Font.ITALIC;

                }
                else if (style.equals("bolditalic") || style.equals("bi")) {
                    result.fontStyle = Font.ITALIC + Font.BOLD;

                }
                else if (style.equals("strike") || style.equals("s")) {
                    result.additionalStyle |= StyleRange.STYLE_STRIKE_THROUGH;

                }
                else if (style.equals("doublestrike") || style.equals("ds")) {
                    result.additionalStyle |= StyleRange.STYLE_DOUBLE_STRIKE_THROUGH;

                }
                else if (style.equals("waved") || style.equals("w")) {
                    result.additionalStyle |= StyleRange.STYLE_WAVED;

                }
                else if (style.equals("underlined") || style.equals("u")) {
                    result.additionalStyle |= StyleRange.STYLE_UNDERLINED;

                }
                else if (style.equals("dotted") || style.equals("d")) {
                    result.additionalStyle |= StyleRange.STYLE_DOTTED;

                }
                else if (style.equals("superscript") || style.equals("sp")) {
                    result.additionalStyle |= StyleRange.STYLE_SUPERSCRIPT;

                }
                else if (style.equals("subscipt") || style.equals("sb")) {
                    result.additionalStyle |= StyleRange.STYLE_SUBSCRIPT;
                }
                else if (builder != null && builder.styles.containsKey(style)) {
                    StyleRange range = (StyleRange) builder.styles.get(style);
                    result.fontStyle = range.getFontStyle();
                    result.fontColor = range.getFontColor();
                    result.backgroundColor = range.getBackgroundColor();
                    result.additionalStyle = range.getAdditionalStyle();
                    result.lineColor = range.getLineColor();
                }
                else if (style.length() > 0) {
                    System.err.println("Unknown style '" + style + "'");
                }
            }
            i = findIndex + 1;
        }
        result.endOffset = i - 1;
        // done, return
        return result;
    }

    /**
     * Can be: (255, 0, 0) #FF0000 #F00 0xFF0000 red
     */
    private static Color toColor(String str) {
        switch (str.charAt(0)) {
            case '(':
                int red, green, blue;
                int index;

                red = nextColorInt(str, 1);

                index = str.indexOf(',');
                green = nextColorInt(str, index + 1);

                index = str.indexOf(',', index + 1);
                blue = nextColorInt(str, index + 1);

                return new Color(red, green, blue);
            case '#':
                // Shorthand?
                if (str.length() == 4) {
                    return new Color(
                            getShorthandValue(str.charAt(1)),
                            getShorthandValue(str.charAt(2)),
                            getShorthandValue(str.charAt(3))
                    );
                }
                else {
                    return new Color(Integer.parseInt(str.substring(1), 16));
                }
            case '0':
                return new Color(Integer.parseInt(str.substring(2), 16));
            default:
                return (Color) colorNamesMap.get(str);
        }
    }

    private static int nextColorInt(String str, int index) {
        // start with adjusting the start index
        while (index < str.length()) {
            char c = str.charAt(index);
            // a digit?
            if ('0' <= c && c <= '9') {
                break;
            }
            else {
                index++;
            }
        }
        // that's only the maximum limit!
        int colorLength = index;
        for (; colorLength < index + 3; colorLength++) {
            char c = str.charAt(colorLength);
            // not a digit?
            if (c < '0' || '9' < c) {
                break;
            }
        }
        return Integer.parseInt(str.substring(index, colorLength));
    }

    private static int getShorthandValue(char c) {
        c = Character.toUpperCase(c);
        if ('A' <= c && c <= 'F') {
            return colorShorthandTable[c - 'A' + 10];
        }
        return colorShorthandTable[c - '0'];
    }

    private static int[] colorShorthandTable = {
            0x00, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66,
            0x77, 0x88, 0x99, 0xAA, 0xBB, 0xCC, 0xDD,
            0xEE, 0xFF
    };

    private static Map colorNamesMap;

    static {
        colorNamesMap = new TreeMap();
        colorNamesMap.put("white", new Color(0xFFFFFF));
        colorNamesMap.put("lightGray", new Color(0xC0C0C0));
        colorNamesMap.put("gray", new Color(0x808080));
        colorNamesMap.put("darkGray", new Color(0x404040));
        colorNamesMap.put("black", new Color(0x000000));
        colorNamesMap.put("red", new Color(0xFF0000));
        colorNamesMap.put("pink", new Color(0xFFAFAF));
        colorNamesMap.put("orange", new Color(0xFFC800));
        colorNamesMap.put("yellow", new Color(0xFFFF00));
        colorNamesMap.put("green", new Color(0x00FF00));
        colorNamesMap.put("magenta", new Color(0xFF00FF));
        colorNamesMap.put("cyan", new Color(0x00FFFF));
        colorNamesMap.put("blue", new Color(0x0000FF));
    }

    public static Map getColorNamesMap() {
        return colorNamesMap;
    }

    private static String createTrimmedString(char[] text, int start, int end) {
        for (; (text[start] == ' ' || text[start] == '\t') && start < text.length; start++) ;
        for (; (text[end] == ' ' || text[end] == '\t') && start < end; end--) ;
        // need to remove escape chars
        if (end >= start) {
            StringBuffer buffer = new StringBuffer(end - start);
            boolean escaped = false;
            for (int i = start; i <= end; i++) {
                if (text[i] == '\\' && !escaped) {
                    escaped = true;
                }
                else {
                    buffer.append(text[i]);
                    if (escaped) {
                        escaped = false;
                    }
                }
            }
            return buffer.toString();
        }
        else {
            return "";
        }
    }

    private static int findNextOf(char[] text, char[] chars, int start) {
        boolean escaped = false;
        for (int i = start; i < text.length; i++) {
            if (escaped) {
                escaped = false;
                continue;
            }
            if (text[i] == '\\') {
                escaped = true;
            }
            else {
                for (char c : chars) {
                    if (text[i] == c) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    private static int findNext(char[] text, char c, int start) {
        boolean escaped = false;
        for (int i = start; i < text.length; i++) {
            if (escaped) {
                escaped = false;
                continue;
            }
            if (text[i] == '\\') {
                escaped = true;
            }
            else if (text[i] == c) {
                return i;
            }
        }
        return -1;
    }

    private static class ParsedStyleResult {
        String text;
        int endOffset;
        int fontStyle = Font.PLAIN;
        Color fontColor = null, lineColor = null, backgroundColor = null;
        int additionalStyle = 0;
    }
}
