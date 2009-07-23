/*
 * @(#)AbstractIntelliHints.java 7/24/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.hints;

import com.jidesoft.plaf.UIDefaultsLookup;
import com.jidesoft.popup.JidePopup;
import com.jidesoft.swing.DelegateAction;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;


/**
 * <code>AbstractIntelliHints</code> is an abstract implementation of {@link com.jidesoft.hints.IntelliHints}. It covers
 * functions such as showing the hint popup at the correct position, delegating keystrokes, updating and selecting hint.
 * The only thing that is left out to subclasses is the creation of the hint popup.
 *
 * @author Santhosh Kumar T
 * @author JIDE Software, Inc.
 */
public abstract class AbstractIntelliHints implements IntelliHints {

    private JidePopup _popup;
    private JTextComponent _textComponent;

    private boolean _followCaret = false;

    // we use this flag to workaround the bug that setText() will trigger the hint popup.
    private boolean _keyTyped = false;

    // Specifies whether the hints popup should be displayed automatically.
    // Default is true for backward compatibility.
    private boolean _autoPopup = true;

    /**
     * Creates an IntelliHints object for a given JTextComponent.
     *
     * @param textComponent the text component.
     */
    public AbstractIntelliHints(JTextComponent textComponent) {
        _textComponent = textComponent;
        getTextComponent().putClientProperty(CLIENT_PROPERTY_INTELLI_HINTS, this);

        _popup = createPopup();

        getTextComponent().getDocument().addDocumentListener(documentListener);
        getTextComponent().addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {
            }

            public void keyPressed(KeyEvent e) {
            }

            public void keyReleased(KeyEvent e) {
                if (KeyEvent.VK_ESCAPE != e.getKeyCode() && KeyEvent.VK_ENTER != e.getKeyCode()) {
                    setKeyTyped(true);
                }
            }
        });
        getTextComponent().addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
            }

            public void focusLost(FocusEvent e) {
                Container topLevelAncestor = _popup.getTopLevelAncestor();
                if (topLevelAncestor == null) {
                    return;
                }
                Component oppositeComponent = e.getOppositeComponent();
                if (topLevelAncestor == oppositeComponent || topLevelAncestor.isAncestorOf(oppositeComponent)) {
                    return;
                }
                hideHintsPopup();
            }
        });

        DelegateAction.replaceAction(getTextComponent(), JComponent.WHEN_FOCUSED, getShowHintsKeyStroke(), showAction);

        KeyStroke[] keyStrokes = getDelegateKeyStrokes();
        for (KeyStroke keyStroke : keyStrokes) {
            DelegateAction.replaceAction(getTextComponent(), JComponent.WHEN_FOCUSED, keyStroke, new LazyDelegateAction(keyStroke));
        }

        getDelegateComponent().setRequestFocusEnabled(false);
        getDelegateComponent().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                hideHintsPopup();
                setHintsEnabled(false);
                acceptHint(getSelectedHint());
                setHintsEnabled(true);
            }
        });
    }

    protected JidePopup createPopup() {
        JidePopup popup = com.jidesoft.popup.JidePopupFactory.getSharedInstance().createPopup();
        popup.setLayout(new BorderLayout());
        popup.setResizable(true);
        popup.setPopupBorder(BorderFactory.createLineBorder(UIDefaultsLookup.getColor("controlDkShadow"), 1));
        popup.setMovable(false);
        popup.add(createHintsComponent());
        popup.addPopupMenuListener(new PopupMenuListener() {
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            }

            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                DelegateAction.restoreAction(getTextComponent(), JComponent.WHEN_FOCUSED, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), hideAction);
                DelegateAction.restoreAction(getTextComponent(), JComponent.WHEN_FOCUSED, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), acceptAction);
            }

            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });
        popup.setTransient(true);
        popup.setKeepPreviousSize(false);
        return popup;
    }

    public JTextComponent getTextComponent() {
        return _textComponent;
    }


    /**
     * After user has selected a item in the hints popup, this method will update JTextComponent accordingly to accept
     * the hint.
     * <p/>
     * For JTextArea, the default implementation will insert the hint into current caret position. For JTextField, by
     * default it will replace the whole content with the item user selected. Subclass can always choose to override it
     * to accept the hint in a different way. For example, {@link com.jidesoft.hints.FileIntelliHints} will append the
     * selected item at the end of the existing text in order to complete a full file path.
     */
    public void acceptHint(Object selected) {
        if (selected == null)
            return;

        String newText;
        int pos = getTextComponent().getCaretPosition();
        if (isMultilineTextComponent()) {
            String text = getTextComponent().getText();
            int start = text.lastIndexOf('\n', pos - 1);
            String remain = pos == -1 ? "" : text.substring(pos);
            text = text.substring(0, start + 1);
            text += selected;
            text += remain;
            newText = text;
        }
        else {
            newText = selected.toString();
        }

        getTextComponent().setText(newText);
        // DocumentFilters in JTextComponent's document model may alter the
        // provided text. The line separator has to be searched in the actual text
        String actualText = getTextComponent().getText();
        int separatorIndex = actualText.indexOf('\n', pos);
        getTextComponent().setCaretPosition(
                separatorIndex == -1 ? actualText.length() : separatorIndex);
    }

    /**
     * Returns whether this IntelliHints' <code>JTextComponent</code> supports single-line text or multi-line text.
     *
     * @return <code>true</code> if the component supports multiple text lines, <code>false</code> otherwise
     */
    protected boolean isMultilineTextComponent() {
        return getTextComponent() instanceof JTextArea ||
                getTextComponent() instanceof JEditorPane;
    }

    /**
     * This method will call {@link #showHints()} if and only if the text component is enabled and has focus.
     */
    protected void showHintsPopup() {
        if (!getTextComponent().isEnabled() || !getTextComponent().hasFocus()) {
            return;
        }
        showHints();
    }

    /**
     * Shows the hints popup which contains the hints. It will call {@link #updateHints(Object)}. Only if it returns
     * true, the popup will be shown. You can call this method to fore the hints to be displayed.
     */
    public void showHints() {
        if (updateHints(getContext())) {
            DelegateAction.replaceAction(getTextComponent(), JComponent.WHEN_FOCUSED, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), hideAction);
            DelegateAction.replaceAction(getTextComponent(), JComponent.WHEN_FOCUSED, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), acceptAction, true);

            int x = 0;
            int y = 0;
            int height = 0;

            try {
                int pos = getCaretPositionForPopup();
                Rectangle position = getCaretRectangleForPopup(pos);
                y = position.y;
                x = position.x;
                height = position.height;
            }
            catch (BadLocationException e) {
                // this should never happen!!!
            }

            _popup.setOwner(getTextComponent());
            _popup.showPopup(new Insets(y, x, getTextComponent().getHeight() - height - y, 0));
        }
        else {
            _popup.hidePopup();
        }
    }

    /**
     * Gets the caret rectangle where caret is displayed. The popup will be show around the area so that the returned
     * rectangle area is always visible. This method will be called twice.
     *
     * @param caretPosition the caret position.
     * @return the popup position relative to the text component. <br>Please note, this position is actually a rectangle
     *         area. The reason is the popup could be shown below or above the rectangle. Usually, the popup will be
     *         shown below the rectangle. In this case, the x and y of the rectangle will be the top-left corner of the
     *         popup. However if there isn't enough space for the popup because it's close to screen bottom border, we
     *         will show the popup above the rectangle. In this case, the bottom-left corner of the popup will be at x
     *         and (y - height). Simply speaking, the popup will never cover the area specified by the rectangle (either
     *         below it or above it).
     *
     * @throws BadLocationException if the given position does not represent a valid location in the associated
     *                              document.
     */
    protected Rectangle getCaretRectangleForPopup(int caretPosition) throws BadLocationException {
        return getTextComponent().getUI().modelToView(getTextComponent(), caretPosition);
    }


    /**
     * Gets the caret position which is used as the anchor point to display the popup. By default, it {@link
     * #isFollowCaret()} is true, it will return caret position. Otherwise it will return the caret position at the
     * beginning of the caret line. Subclass can override to return any caret position.
     *
     * @return the caret position which is used as the anchor point to display the popup.
     */
    protected int getCaretPositionForPopup() {
        int caretPosition = Math.min(getTextComponent().getCaret().getDot(), getTextComponent().getCaret().getMark());
        if (isFollowCaret()) {
            return caretPosition;
        }
        else {
            try {
                Rectangle viewRect = getTextComponent().getUI().modelToView(getTextComponent(), caretPosition);
                viewRect.x = 0;
                return getTextComponent().getUI().viewToModel(getTextComponent(), viewRect.getLocation());
            }
            catch (BadLocationException e) {
                return 0;
            }
        }
    }

    /**
     * Gets the context for hints. The context is the information that IntelliHints needs in order to generate a list of
     *  hints. For example, for code-completion, the context is current word the cursor is on. for file completion, the
     * context is the full string starting from the file system root. <p>We provide a default context in
     * AbstractIntelliHints. If it's a JTextArea, the context will be the string at the caret line from line beginning
     * to the caret position. If it's a JTextField, the context will be whatever string in the text field. Subclass can
     * always override it to return the context that is appropriate.
     *
     * @return the context.
     */
    protected Object getContext() {
        if (isMultilineTextComponent()) {
            int pos = getTextComponent().getCaretPosition();
            if (pos == 0) {
                return "";
            }
            else {
                String text = getTextComponent().getText();
                int start = text.lastIndexOf('\n', pos - 1);
                return text.substring(start + 1, pos);
            }
        }
        else {
            return getTextComponent().getText();
        }
    }

    /**
     * Hides the hints popup.
     */
    protected void hideHintsPopup() {
        if (_popup != null) {
            _popup.hidePopup();
        }
        setKeyTyped(false);
    }

    /**
     * Enables or disables the hints popup.
     *
     * @param enabled true to enable the hints popup. Otherwise false.
     */
    public void setHintsEnabled(boolean enabled) {
        if (!enabled) {
            // disable show hint temporarily
            getTextComponent().getDocument().removeDocumentListener(documentListener);
        }
        else {
            // enable show hint again
            getTextComponent().getDocument().addDocumentListener(documentListener);
        }

    }

    /**
     * Checks if the hints popup is visible.
     *
     * @return true if it's visible. Otherwise, false.
     */
    public boolean isHintsPopupVisible() {
        return _popup != null && _popup.isPopupVisible();
    }

    /**
     * Should the hints popup follows the caret.
     *
     * @return true if the popup shows up right below the caret. False if the popup always shows at the bottom-left
     *         corner (or top-left if there isn't enough on the bottom of the screen) of the JTextComponent.
     */
    public boolean isFollowCaret() {
        return _followCaret;
    }

    /**
     * Sets the position of the hints popup. If followCaret is true, the popup shows up right below the caret.
     * Otherwise, it will stay at the bottom-left corner (or top-left if there isn't enough on the bottom of the screen)
     * of JTextComponent.
     *
     * @param followCaret true or false.
     */
    public void setFollowCaret(boolean followCaret) {
        _followCaret = followCaret;
    }

    /**
     * Returns whether the hints popup is automatically displayed. Default is true
     *
     * @return true if the popup should be automatically displayed. False will never show it automatically and then need
     *         the user to manually activate it via the getShowHintsKeyStroke() key binding.
     */
    public boolean isAutoPopup() {
        return _autoPopup;
    }

    /**
     * Sets whether the popup should be displayed automatically. If autoPopup is true then is the popup automatically
     * displayed whenever updateHints() return true. If autoPopup is false it's not automatically displayed and will
     * need the user to activate the key binding defined by getShowHintsKeyStroke().
     *
     * @param autoPopup true or false
     */
    public void setAutoPopup(boolean autoPopup) {
        this._autoPopup = autoPopup;
    }

    /**
     * Gets the delegate keystrokes.
     * <p/>
     * When hint popup is visible, the keyboard focus never leaves the text component. However the hint popup usually
     * contains a component that user will try to use navigation key to select an item. For example, use UP and DOWN key
     * to navigate the list. Those keystrokes, if the popup is visible, will be delegated to the the component that
     * returns from {@link #getDelegateComponent()}.
     *
     * @return an array of keystrokes that will be delegate to {@link #getDelegateComponent()} when hint popup is
     *         shown.
     */
    abstract protected KeyStroke[] getDelegateKeyStrokes();

    /**
     * Gets the delegate component in the hint popup.
     *
     * @return the component that will receive the keystrokes that are delegated to hint popup.
     */
    abstract protected JComponent getDelegateComponent();

    /**
     * Gets the keystroke that will trigger the hint popup. Usually the hints popup will be shown automatically when
     * user types. Only when the hint popup is hidden accidentally, this keystroke will show the popup again.
     * <p/>
     * By default, it's the DOWN key for JTextField and CTRL+SPACE for JTextArea.
     *
     * @return the keystroke that will trigger the hint popup.
     */
    protected KeyStroke getShowHintsKeyStroke() {
        if (isMultilineTextComponent()) {
            return KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, KeyEvent.CTRL_MASK);
        }
        else {
            return KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0);
        }
    }

    private DelegateAction acceptAction = new DelegateAction() {
        private static final long serialVersionUID = -2516216121942080133L;

        @Override
        public boolean delegateActionPerformed(ActionEvent e) {
            JComponent tf = (JComponent) e.getSource();
            IntelliHints hints = getIntelliHints(tf);
            if (hints instanceof AbstractIntelliHints) {
                AbstractIntelliHints aih = (AbstractIntelliHints) hints;
                aih.hideHintsPopup();
                if (aih.getSelectedHint() != null) {
                    aih.setHintsEnabled(false);
                    aih.acceptHint(hints.getSelectedHint());
                    aih.setHintsEnabled(true);
                    return true;
                }
                else if (getTextComponent().getRootPane() != null) {
                    JButton button = getTextComponent().getRootPane().getDefaultButton();
                    if (button != null) {
                        button.doClick();
                        return true;
                    }
                }
            }
            return false;
        }
    };

    private static DelegateAction showAction = new DelegateAction() {
        private static final long serialVersionUID = 2243999895981912016L;

        @Override
        public boolean delegateActionPerformed(ActionEvent e) {
            JComponent tf = (JComponent) e.getSource();
            IntelliHints hints = getIntelliHints(tf);
            if (hints instanceof AbstractIntelliHints) {
                AbstractIntelliHints aih = (AbstractIntelliHints) hints;
                if (tf.isEnabled() && !aih.isHintsPopupVisible()) {
                    aih.showHintsPopup();
                    return true;
                }
            }
            return false;
        }
    };

    private DelegateAction hideAction = new DelegateAction() {
        private static final long serialVersionUID = 1921213578011852535L;

        @Override
        public boolean isEnabled() {
            return _textComponent.isEnabled() && isHintsPopupVisible();
        }

        @Override
        public boolean delegateActionPerformed(ActionEvent e) {
            if (isEnabled()) {
                hideHintsPopup();
                return true;
            }
            return false;
        }
    };

    private DocumentListener documentListener = new DocumentListener() {
        private Timer timer = new Timer(200, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (isKeyTyped()) {
                    if (isHintsPopupVisible() || isAutoPopup()) {
                        showHintsPopup();
                    }
                    setKeyTyped(false);
                }
            }
        });

        public void insertUpdate(DocumentEvent e) {
            startTimer();
        }

        public void removeUpdate(DocumentEvent e) {
            startTimer();
        }

        public void changedUpdate(DocumentEvent e) {
        }

        void startTimer() {
            if (timer.isRunning()) {
                timer.restart();
            }
            else {
                timer.setRepeats(false);
                timer.start();
            }
        }
    };

    private boolean isKeyTyped() {
        return _keyTyped;
    }

    private void setKeyTyped(boolean keyTyped) {
        _keyTyped = keyTyped;
    }

    private static class LazyDelegateAction extends DelegateAction {
        private KeyStroke _keyStroke;
        private static final long serialVersionUID = -5799290233797844786L;

        public LazyDelegateAction(KeyStroke keyStroke) {
            _keyStroke = keyStroke;
        }

        @Override
        public boolean delegateActionPerformed(ActionEvent e) {
            JComponent tf = (JComponent) e.getSource();
            IntelliHints hints = getIntelliHints(tf);
            if (hints instanceof AbstractIntelliHints) {
                AbstractIntelliHints aih = (AbstractIntelliHints) hints;
                if (tf.isEnabled()) {
                    if (aih.isHintsPopupVisible()) {
                        Object key = aih.getDelegateComponent().getInputMap().get(_keyStroke);
                        key = key == null ? aih.getTextComponent().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).get(_keyStroke) : key;
                        if (key != null) {
                            Object action = aih.getDelegateComponent().getActionMap().get(key);
                            if (action instanceof Action) {
                                ((Action) action).actionPerformed(new ActionEvent(aih.getDelegateComponent(), 0, "" + key));
                                return true;
                            }
                        }
                    }
                }
            }
            return false;
        }
    }

    /**
     * Gets the IntelliHints object if it was installed on the component before.
     *
     * @param component the component that has IntelliHints installed
     * @return the IntelliHints.
     */
    public static IntelliHints getIntelliHints(JComponent component) {
        return (IntelliHints) component.getClientProperty(CLIENT_PROPERTY_INTELLI_HINTS);
    }
}
