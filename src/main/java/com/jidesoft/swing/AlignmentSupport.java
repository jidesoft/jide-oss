package com.jidesoft.swing;

/**
 * A general interface for alignment support. All JIDE components will implement this method
 * if it has the following methods. In standard Swing package, AbstractButton, JLabel, JTextField etc should implement this too.
 */
public interface AlignmentSupport {
    /**
     * Returns the horizontal alignment of the content.
     * {@code AbstractButton}'s default is {@code SwingConstants.CENTER},
     * but subclasses such as {@code JCheckBox} may use a different default.
     *
     * @return the <code>horizontalAlignment</code> property,
     *         one of the following values:
     *         <ul>
     *         <li>{@code SwingConstants.RIGHT}
     *         <li>{@code SwingConstants.LEFT}
     *         <li>{@code SwingConstants.CENTER}
     *         <li>{@code SwingConstants.LEADING}
     *         <li>{@code SwingConstants.TRAILING}
     *         </ul>
     */
    int getHorizontalAlignment();

    /**
     * Sets the horizontal alignment of the content.
     * {@code AbstractButton}'s default is {@code SwingConstants.CENTER},
     * but subclasses such as {@code JCheckBox} may use a different default.
     *
     * @param alignment the alignment value, one of the following values:
     *                  <ul>
     *                  <li>{@code SwingConstants.RIGHT}
     *                  <li>{@code SwingConstants.LEFT}
     *                  <li>{@code SwingConstants.CENTER}
     *                  <li>{@code SwingConstants.LEADING}
     *                  <li>{@code SwingConstants.TRAILING}
     *                  </ul>
     * @throws IllegalArgumentException if the alignment is not one of the
     *                                  valid values
     */
    void setHorizontalAlignment(int alignment);

    /**
     * Returns the vertical alignment of the content.
     *
     * @return the <code>verticalAlignment</code> property, one of the
     *         following values:
     *         <ul>
     *         <li>{@code SwingConstants.CENTER} (the default)
     *         <li>{@code SwingConstants.TOP}
     *         <li>{@code SwingConstants.BOTTOM}
     *         </ul>
     */
    int getVerticalAlignment();

    /**
     * Sets the vertical alignment of the content.
     *
     * @param alignment one of the following values:
     *                  <ul>
     *                  <li>{@code SwingConstants.CENTER} (the default)
     *                  <li>{@code SwingConstants.TOP}
     *                  <li>{@code SwingConstants.BOTTOM}
     *                  </ul>
     * @throws IllegalArgumentException if the alignment is not one of the legal
     *                                  values listed above
     */
    void setVerticalAlignment(int alignment);
}
