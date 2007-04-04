/*
 * @(#)AutoCompletion.java 6/22/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.swing;

import com.jidesoft.utils.SystemInfo;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;
import javax.swing.tree.TreePath;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

/**
 * <code>AutoCompletion</code> is a helper class to make JTextComponent or JComboBox auto-complete based
 * on a list of known items.
 * <p/>
 * There are three constructors. The simplest one is {@link #AutoCompletion(javax.swing.JComboBox)}.
 * It takes any combobox and make it auto completion. If you are looking for an auto-complete combobox solution,
 * this is all you need. However <code>AutoCompletion</code> can do more than that. There are two more constrcutors.
 * One is {@link #AutoCompletion(javax.swing.text.JTextComponent,Searchable)}.
 * It will use {@link Searchable} which is another component available in JIDE to
 * make the JTextCompoent auto-complete. We used Searchable here because it provides
 * a common interface to access the element in JTree, JList or JTable. In
 * the other word, the known list item we used to auto-complete can be got
 * from JTree or JList or even JTable or any other component as
 * long as it has Searchable interface implemented.
 * The last constrcutor takes any java.util.List and use it as auto completion list.
 * <p/>
 * The only option available on <code>AutoCompletion</code> is {@link #setStrict(boolean)}. If it's true, it will not allow
 * user to type in anything that is not in the known item list. If false, user can type in whatever he/she wants. If the text
 * can match with a item in the known item list, it will still auto-complete.
 * <p/>
 *
 * @author Thomas Bierhance
 * @author JIDE Software, Inc.
 */
public class AutoCompletion {
    private Searchable _searchable;
    private JTextComponent _textComponent;

    private AutoCompletionDocument _document;

    // flag to indicate if setSelectedItem has been called
    // subsequent calls to remove/insertString should be ignored
    private boolean _selecting = false;

    private boolean _hidePopupOnFocusLoss;
    private boolean _hitBackspace = false;
    private boolean _hitBackspaceOnSelection;

    private KeyListener _editorKeyListener;
    private FocusListener _editorFocusListener;

    private boolean _strict = true;
    private boolean _strictCompletion = true;

    public AutoCompletion(final JComboBox comboBox) {
        _searchable = new ComboBoxSearchable(comboBox);
        comboBox.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                if ("editor".equals(e.getPropertyName())) {
                    if (e.getNewValue() != null) {
                        _textComponent = (JTextComponent) ((ComboBoxEditor) e.getNewValue()).getEditorComponent();
                        configureEditor(getTextComponent());
                    }
                }
            }
        });
        _searchable.setWildcardEnabled(false);
        ((ComboBoxSearchable) _searchable).setShowPopupDuringSearching(false);
        _textComponent = (JTextComponent) comboBox.getEditor().getEditorComponent();
        installListeners();
        setInitValue();
    }

    public AutoCompletion(final JTextComponent textComponent, final Searchable searchable) {
        _searchable = searchable;
        _searchable.setWildcardEnabled(false);
        _textComponent = textComponent;
        registerSelectionListener(getSearchable());

        installListeners();
        setInitValue();
    }

    public AutoCompletion(final JTextComponent textComponent, final List list) {
        this(textComponent, new Searchable(new JLabel()) {
            int _selectIndex = -1;

            protected int getSelectedIndex() {
                return _selectIndex;
            }

            protected void setSelectedIndex(int index, boolean incremental) {
                _selectIndex = index;
            }

            protected int getElementCount() {
                return list.size();
            }

            protected Object getElementAt(int index) {
                return list.get(index);
            }

            protected String convertElementToString(Object element) {
                return "" + element;
            }
        });
    }

    public AutoCompletion(final JTextComponent textComponent, final Object[] array) {
        this(textComponent, new Searchable(new JLabel()) {
            int _selectIndex = -1;

            protected int getSelectedIndex() {
                return _selectIndex;
            }

            protected void setSelectedIndex(int index, boolean incremental) {
                _selectIndex = index;
            }

            protected int getElementCount() {
                return array.length;
            }

            protected Object getElementAt(int index) {
                return array[index];
            }

            protected String convertElementToString(Object element) {
                return "" + element;
            }
        });
    }

    private void registerSelectionListener(Searchable searchable) {
        if (searchable.getComponent() instanceof JList) {
            final JList list = (JList) getSearchable().getComponent();
            list.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    int index = list.getSelectedIndex();
                    if (index != -1) {
                        getTextComponent().setText("" + list.getModel().getElementAt(index));
                        highlightCompletedText(0);
                    }
                }
            });
            DelegateAction.replaceAction(getTextComponent(), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, list, JComponent.WHEN_FOCUSED, KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0));
            DelegateAction.replaceAction(getTextComponent(), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, list, JComponent.WHEN_FOCUSED, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0));
            DelegateAction.replaceAction(getTextComponent(), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, list, JComponent.WHEN_FOCUSED, KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0));
            DelegateAction.replaceAction(getTextComponent(), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, list, JComponent.WHEN_FOCUSED, KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0));
        }
        else if (searchable.getComponent() instanceof JTree) {
            final JTree tree = (JTree) getSearchable().getComponent();
            tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
                public void valueChanged(TreeSelectionEvent e) {
                    TreePath treePath = tree.getSelectionPath();
                    if (treePath != null) {
                        getTextComponent().setText("" + treePath.getLastPathComponent());
                        highlightCompletedText(0);
                    }
                }
            });
            DelegateAction.replaceAction(getTextComponent(), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, tree, JComponent.WHEN_FOCUSED, KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0));
            DelegateAction.replaceAction(getTextComponent(), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, tree, JComponent.WHEN_FOCUSED, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0));
            DelegateAction.replaceAction(getTextComponent(), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, tree, JComponent.WHEN_FOCUSED, KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0));
            DelegateAction.replaceAction(getTextComponent(), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, tree, JComponent.WHEN_FOCUSED, KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0));
        }
    }

    private void setInitValue() {
        int index = getSearchable().getSelectedIndex();
        if (index != -1) {
            Object selected = getSearchable().getElementAt(index);
            if (selected != null)
                _document.setText(getSearchable().convertElementToString(selected));
            highlightCompletedText(0);
        }
        else {
            _document.setText("");
        }
    }

    private void installListeners() {
        _editorKeyListener = new KeyAdapter() {
            private boolean _deletePressed;
            private String _saveText;

            public void keyPressed(KeyEvent e) {
                _hitBackspace = false;
                switch (e.getKeyCode()) {
                    // determine if the pressed key is backspace (needed by the remove method)
                    case KeyEvent.VK_BACK_SPACE:
                        if (isStrict()) {
                            _hitBackspace = true;
                            _hitBackspaceOnSelection = getTextComponent().getSelectionStart() != getTextComponent().getSelectionEnd();
                        }
                        break;
                        // ignore delete key
                    case KeyEvent.VK_DELETE:
                        if (isStrict()) {
                            _deletePressed = true;
                            _saveText = getTextComponent().getText();
                        }
                        break;
                }
            }

            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);

                if (_deletePressed) {
                    _deletePressed = false;
                    String text = getTextComponent().getText();
                    int index = getSearchable().findFirst(text);
                    if (index != -1) {
                        Object item = getSearchable().getElementAt(index);
                        setSelectedItem(item);
                        getTextComponent().setText(getSearchable().convertElementToString(item)); // this is what auto complete is
                        // select the completed part
                        highlightCompletedText(text.length());
                    }
                    else { // didn't find a matching one
                        if (isStrict()) {
                            getTextComponent().setText(_saveText);
                            e.consume();
                            UIManager.getLookAndFeel().provideErrorFeedback(_textComponent);
                        }
                    }
                }
            }
        };
        // Bug 5100422 on Java 1.5: Editable JComboBox won't hide popup when tabbing out
        _hidePopupOnFocusLoss = SystemInfo.isJdk15Above();
        // Highlight whole text when gaining focus
        _editorFocusListener = new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                highlightCompletedText(0);
            }

            public void focusLost(FocusEvent e) {
                // Workaround for Bug 5100422 - Hide Popup on focus loss
//                if (_hidePopupOnFocusLoss) comboBox.setPopupVisible(false);
            }
        };

        _document = createDocument();
        configureEditor(getTextComponent());
    }

    /**
     * Creates AutoCompletionDocument.
     *
     * @return the AutoCompletionDocument.
     */
    protected AutoCompletionDocument createDocument() {
        return new AutoCompletionDocument();
    }

    private void configureEditor(JTextComponent textComponent) {
        if (getTextComponent() != null) {
            getTextComponent().removeKeyListener(_editorKeyListener);
            getTextComponent().removeFocusListener(_editorFocusListener);
        }

        if (textComponent != null) {
            _textComponent = textComponent;
            getTextComponent().addKeyListener(_editorKeyListener);
            getTextComponent().addFocusListener(_editorFocusListener);
            getTextComponent().setDocument(_document);
        }
    }

    /**
     * The document class used by <tt>AutoCompletion</tt>.
     */
    protected class AutoCompletionDocument extends PlainDocument {
        public void remove(int offs, int len) throws BadLocationException {
            // return immediately when _selecting an item
            if (_selecting) return;
            if (_hitBackspace) {
                // user hit backspace => move the selection backwards
                // old item keeps being selected
                if (offs > 0) {
                    if (_hitBackspaceOnSelection) offs--;
                }
                else {
                    // User hit backspace with the cursor positioned on the start => beep
                    UIManager.getLookAndFeel().provideErrorFeedback(_textComponent);
                }
                highlightCompletedText(offs);
            }
            else {
                super.remove(offs, len);
            }
        }

        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            // return immediately when _selecting an item
            if (_selecting)
                return;
            // insert the string into the document
            super.insertString(offs, str, a);
            // lookup and select a matching item
            final String text = getText(0, getLength());
            int index = getSearchable().findFromCursor(text);
            Object item = null;
            if (index != -1) {
                item = getSearchable().getElementAt(index);
                setSelectedItem(item);
                setText(getSearchable().convertElementToString(item)); // this is what auto complete is
                // select the completed part
                highlightCompletedText(offs + str.length());
            }
            else { // didn't find a matching one
                if (isStrict()) {
                    index = getSearchable().getSelectedIndex();
                    if (index == -1) {
                        if (getSearchable().getElementCount() > 0) {
                            index = 0;
                            getSearchable().setSelectedIndex(0, false);
                        }
                    }

                    if (index != -1) {
                        item = getSearchable().getElementAt(index);
                        offs = offs - str.length();
                        // imitate no insert (later on offs will be incremented by str.length(): selection won't move forward)
                        UIManager.getLookAndFeel().provideErrorFeedback(_textComponent);
                        setText(getSearchable().convertElementToString(item));
                        // select the completed part
                        highlightCompletedText(offs + str.length());
                    }
                }
            }
        }

        protected void setText(String text) {
            try {
                // remove all text and insert the completed string
                if (isStrictCompletion()) {
                    super.remove(0, getLength());
                    super.insertString(0, text, null);
                }
                else {
                    String existingText = super.getText(0, getLength());
                    int matchIndex = existingText.length();
                    // try to find a match
                    for (int i = 0; i < existingText.length(); i++) {
                        if (!existingText.substring(0, matchIndex).equalsIgnoreCase(text.substring(0, matchIndex))) {
                            matchIndex--;
                        }
                    }
                    // remove the no-match part and complete with the one in Searchable
                    super.remove(matchIndex, getLength() - matchIndex);
                    super.insertString(matchIndex, text.substring(matchIndex), null);
                }
            }
            catch (BadLocationException e) {
                throw new RuntimeException(e.toString());
            }
        }
    }

    private void highlightCompletedText(int start) {
        getTextComponent().setCaretPosition(getTextComponent().getDocument().getLength());
        getTextComponent().moveCaretPosition(start);
    }

    private void setSelectedItem(Object item) {
        _selecting = true;
        for (int i = 0, n = getSearchable().getElementCount(); i < n; i++) {
            Object currentItem = getSearchable().getElementAt(i);
            // current item starts with the pattern?
            if (item == currentItem) {
                getSearchable().setSelectedIndex(i, false);
            }
        }
        _selecting = false;
    }

    /**
     * Gets the strict property.
     *
     * @return the value of strict property.
     */
    public boolean isStrict() {
        return _strict;
    }

    /**
     * Sets the strict property. If true, it will not allow user to type in anything
     * that is not in the known item list. If false, user can type in whatever he/she wants. If the text
     * can match with a item in the known item list, it will still auto-complete.
     *
     * @param strict
     */
    public void setStrict(boolean strict) {
        _strict = strict;
    }

    /**
     * Gets the strict completion property.
     *
     * @return the value of strict completion property.
     * @see #setStrictCompletion(boolean)
     */
    public boolean isStrictCompletion() {
        return _strictCompletion;
    }

    /**
     * Sets the strict completion property. If true, in case insensitive searching,
     * it will always use the exact item in the Searchable to replace whatever user types. For example,
     * when Searchable has an item "Arial" and user types in "AR", if this flag is true, it will autocompleted
     * as "Arial". If false, it will be autocompleted as "ARial". Of course, this flag will only
     * make a difference if Searchable is case insensitive.
     *
     * @param strictCompletion
     */
    public void setStrictCompletion(boolean strictCompletion) {
        _strictCompletion = strictCompletion;
    }

    /**
     * Gets the underlying text component which auto-completes.
     *
     * @return the underlying text component.
     */
    protected JTextComponent getTextComponent() {
        return _textComponent;
    }

    /**
     * Gets the underlying Searchable. If you use the constructor {@link #AutoCompletion(javax.swing.text.JTextComponent,Searchable)}, the return value
     * will be the Searcable you passed in. If you use the other two constrcutors, internally we will
     * still create a Searchable. If so, this Searchable will be returned.
     *
     * @return the Searchable.
     */
    public Searchable getSearchable() {
        return _searchable;
    }
}
