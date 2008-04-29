package com.jidesoft.swing;

import com.jidesoft.plaf.LookAndFeelFactory;
import com.jidesoft.plaf.UIDefaultsLookup;

import javax.swing.*;
import java.util.Vector;

/**
 * <code>JideComboBox</code> is a JCombobox used on JToolBar or CommandBar. It has a flat look which matches with that
 * of JideButton and JideSplitButton.
 */
public class JideComboBox extends JComboBox {
    private static final String uiClassID = "JideComboBoxUI";

    public JideComboBox(ComboBoxModel aModel) {
        super(aModel);
    }

    public JideComboBox(final Object items[]) {
        super(items);
    }

    public JideComboBox(Vector<?> items) {
        super(items);
    }

    public JideComboBox() {
        super();
    }

    /**
     * Resets the UI property to a value from the current look and feel.
     *
     * @see JComponent#updateUI
     */
    @Override
    public void updateUI() {
        if (UIDefaultsLookup.get(uiClassID) == null) {
            LookAndFeelFactory.installJideExtension();
        }
        setUI(UIManager.getUI(this));
    }

    /**
     * Returns a string that specifies the name of the L&F class that renders this component.
     *
     * @return the string "JideComboBoxUI"
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     */
    @Override
    public String getUIClassID() {
        return uiClassID;
    }
}