/*
 * @(#)ButtonPanel.java
 *
 * Copyright 2002-2003 JIDE Software. All rights reserved.
 */
package com.jidesoft.swing;

import javax.swing.*;
import java.awt.*;

/**
 * ButtonPanel can have a collection of buttons. And it will make all buttons to have the same size
 * to make them looks better.
 *
 * @deprecated replaced by ButtonPanel under com.jidesoft.dialog.
 */
@Deprecated
public class ButtonPanel extends JPanel {

    /**
     * The default horizontal spacing.
     */
    public static final int DEFAULT_SPACING = 4;

    /**
     * The actual panel that have those buttons.
     */
    private JPanel _buttons;

    /**
     * Constructs a new <code>ButtonPanel</code> with the default horizontal spacing and right
     * alignment.
     */

    public ButtonPanel() {
        this(SwingConstants.RIGHT, DEFAULT_SPACING);
    }

    /**
     * Constructs a new <code>ButtonPanel</code> with default horizontal spacing and the given
     * alignment.
     *
     * @param alignment the alignment of the buttons. It can be one of <code>SwingConstants.LEFT</code>
     *                  or <code>SwingConstants.RIGHT</code> or <code>SwingConstants.TOP</code> or
     *                  <code>SwingConstants.BOTTOM</code>.
     */

    public ButtonPanel(int alignment) {
        this(alignment, DEFAULT_SPACING);
    }

    /**
     * Constructs a new <code>ButtonPanel</code> with the given horizontal spacing and alignment.
     *
     * @param spacing   The gridSize of the gap (in pixels) to place between buttons horizontally.
     * @param alignment the alignment of the buttons. It can be one of <code>SwingConstants.LEFT</code>
     *                  or <code>SwingConstants.RIGHT</code> or <code>SwingConstants.TOP</code> or
     *                  <code>SwingConstants.BOTTOM</code>.
     */

    public ButtonPanel(int alignment, int spacing) {
        if (alignment != SwingConstants.LEFT && alignment != SwingConstants.RIGHT
                && alignment != SwingConstants.TOP && alignment != SwingConstants.BOTTOM) {
            throw new IllegalArgumentException("Invalid alignment");
        }

        setLayout(new BorderLayout());

        _buttons = new NullPanel();

        if (alignment == SwingConstants.TOP || alignment == SwingConstants.BOTTOM)
            _buttons.setLayout(new GridLayout(0, 1, 0, spacing));
        else
            _buttons.setLayout(new GridLayout(1, 0, spacing, 0));

        switch (alignment) {
            case SwingConstants.LEFT:
                add(_buttons, BorderLayout.WEST);
                break;
            case SwingConstants.RIGHT:
                add(_buttons, BorderLayout.EAST);
                break;
            case SwingConstants.TOP:
                add(_buttons, BorderLayout.NORTH);
                break;
            case SwingConstants.BOTTOM:
                add(_buttons, BorderLayout.SOUTH);
                break;
        }
    }

    /**
     * Adds a button to <code>ButtonPanel</code>.
     *
     * @param button button to be added.
     */

    public void addButton(AbstractButton button) {
        if (button.getParent() != _buttons) {
            _buttons.add(button);
        }
    }

    /**
     * Adds a button to <code>ButtonPanel</code> at the specified position.
     *
     * @param button button to add.
     * @param pos    position at which to add the button. The value 0 denotes the first position,
     *               and -1 denotes the last position.
     *
     * @throws IllegalArgumentException If the value of <code>pos</code> is invalid.
     */

    public void addButton(AbstractButton button, int pos)
            throws IllegalArgumentException {
        if (button.getParent() != _buttons) {
            _buttons.add(button, pos);
        }
    }

    /**
     * Removes a button from the <code>ButtonPanel</code>.
     *
     * @param button button to remove.
     */

    public void removeButton(AbstractButton button) {
        if (button.getParent() == _buttons) {
            _buttons.remove(button);
        }
    }

    /**
     * Removes a button from the <code>ButtonPanel</code> at the specified position.
     *
     * @param position position of the button to remove, where 0 denotes the first position.
     */

    public void removeButton(int position) {
        _buttons.remove(position);
    }

    /**
     * Gets the button at the specified position in the <code>ButtonPanel</code>.
     *
     * @param position position of the button.
     *
     * @return button at the specified position.
     */

    public AbstractButton getButton(int position) {
        return ((AbstractButton) _buttons.getComponent(position));
    }

    /**
     * Gets the number of buttons in this <code>ButtonPanel</code>.
     *
     * @return the number of buttons.
     */

    public int getButtonCount() {
        return (_buttons.getComponentCount());
    }

    /**
     * Gets the actual panel that has the buttons. We expose this method so that user can customize
     * the panel such as the background. Please don't try to add non-button component to it.
     *
     * @return the actual button panel.
     */
    public JPanel getButtons() {
        return _buttons;
    }
}
