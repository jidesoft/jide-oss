package com.jidesoft.swing;

import com.jidesoft.plaf.LookAndFeelFactory;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ActionMapUIResource;
import java.awt.event.*;

/**
 * <strong>This class is deprecated and replaced by {@link TristateCheckBox}. We will no longer provide support for this
 * class.</strong>
 * <p/>
 * Maintenance tip - There were some tricks to getting this code working:
 * <p/>
 * 1. You have to overwrite addMouseListener() to do nothing 2. You have to add a mouse event on mousePressed by calling
 * super.addMouseListener() 3. You have to replace the UIActionMap for the keyboard event "pressed" with your own one.
 * 4. You have to remove the UIActionMap for the keyboard event "released". 5. You have to grab focus when the next
 * state is entered, otherwise clicking on the component won't get the focus. 6. You have to make a TristateDecorator as
 * a button model that wraps the original button model and does state management.
 * <p/>
 * To get notified for the state change, usually people use itemChange listener for a regular JCheckBox but for
 * TristateCheckBox, it doesn't work very well. It would be better to use addPropertyChangeListener on PROPERTY_STATE
 * property. It will be fired whenever the state is changed.
 *
 * @author Dr. Heinz M. Kabutz
 * @author JIDE Software
 * @deprecated Replaced by {@link TristateCheckBox}.
 */
public class LegacyTristateCheckBox extends JCheckBox {
    /**
     * Property name for the state. Listen to this property change if you want to get notified for the state change.
     */
    public final static String PROPERTY_STATE = "state";

    /**
     * This is a type-safe enumerated type
     */
    public static class State {
        private State() {
        }
    }

    public static final State NOT_SELECTED = new State();
    public static final State SELECTED = new State();
    public static final State DONT_CARE = new State();

    private final TristateDecorator model;

    public LegacyTristateCheckBox(String text, Icon icon, State initial) {
        super(text, icon);
        // Add a listener for when the mouse is pressed
        super.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                grabFocus();
                setState(getNextState(getState()));
            }
        });
        // Reset the keyboard action map
        ActionMap map = new ActionMapUIResource();
        map.put("pressed", new AbstractAction() {
            private static final long serialVersionUID = 7121802319351334948L;

            public void actionPerformed(ActionEvent e) {
                grabFocus();
                setState(getNextState(getState()));
            }
        });
        map.put("released", null);
        SwingUtilities.replaceUIActionMap(this, map);
        // set the model to the adapted model
        model = new TristateDecorator(getModel());
        setModel(model);
        setState(initial);
    }

    public LegacyTristateCheckBox(String text, State initial) {
        this(text, null, initial);
    }

    public LegacyTristateCheckBox(String text) {
        this(text, DONT_CARE);
    }

    public LegacyTristateCheckBox() {
        this(null);
    }

    /**
     * No one may add mouse listeners, not even Swing!
     */
    @Override
    public void addMouseListener(MouseListener l) {
    }

    /**
     * Set the new state to either SELECTED, NOT_SELECTED or DONT_CARE.  If state == null, it is treated as DONT_CARE.
     *
     * @param state the new state
     */
    public void setState(State state) {
        State old = model.getState();
        model.setState(state);
        if (old != state) {
            firePropertyChange(PROPERTY_STATE, old, state);
        }

        // for Synthetica

        if (LookAndFeelFactory.isSyntheticaLnfInstalled()
                && (UIManager.getLookAndFeel().getClass().getName().startsWith(LookAndFeelFactory.SYNTHETICA_LNF_PREFIX) || LookAndFeelFactory.isLnfInUse(LookAndFeelFactory.SYNTHETICA_LNF))) {
            if (state == DONT_CARE) {
                setName("HalfSelected");
            }
            else {
                setName("");
            }
        }
    }

    /**
     * Return the current state, which is determined by the selection status of the model.
     *
     * @return the current state.
     */
    public State getState() {
        return model.getState();
    }

    @Override
    public void setSelected(boolean b) {
        if (b) {
            setState(SELECTED);
        }
        else {
            setState(NOT_SELECTED);
        }
    }

    /**
     * Exactly which Design Pattern is this?  Is it an Adapter, a Proxy or a Decorator?  In this case, my vote lies with
     * the Decorator, because we are extending functionality and "decorating" the original model with a more powerful
     * model.
     */
    private class TristateDecorator implements ButtonModel {
        private final ButtonModel other;

        private State _state;

        private TristateDecorator(ButtonModel other) {
            this.other = other;
        }

        private void setState(State state) {
            if (state == NOT_SELECTED) {
                other.setArmed(false);
                setPressed(false);
                setSelected(false);
            }
            else if (state == SELECTED) {
                other.setArmed(false);
                setPressed(false);
                setSelected(true);
            }
            else { // either "null" or DONT_CARE
                other.setArmed(true);
                setPressed(true);
                setSelected(true);
            }
            _state = state;
        }

        /*
         * The current state is embedded in the selection / armed state of the model.
         * <p/>
         * We return the SELECTED state when the checkbox is selected but not armed, DONT_CARE state when the checkbox is selected and armed (grey) and NOT_SELECTED when the checkbox is deselected.
         */
        private State getState() {
            return _state == null ? DONT_CARE : _state;
//            if (isSelected() && !isArmed()) {
//                // normal black tick
//                return SELECTED;
//            }
//            else if (isSelected() && isArmed()) {
//                // don't care grey tick
//                return DONT_CARE;
//            }
//            else {
//                // normal deselected
//                return NOT_SELECTED;
//            }
        }

        /**
         * Filter: No one may change the armed status except us.
         */
        public void setArmed(boolean b) {
        }

        /**
         * We disable focusing on the component when it is not enabled.
         */
        public void setEnabled(boolean b) {
            setFocusable(b);
            other.setEnabled(b);
        }

        /**
         * All these methods simply delegate to the "other" model that is being decorated.
         */
        public boolean isArmed() {
            return other.isArmed();
        }

        public boolean isSelected() {
            return other.isSelected();
        }

        public boolean isEnabled() {
            return other.isEnabled();
        }

        public boolean isPressed() {
            return other.isPressed();
        }

        public boolean isRollover() {
            return other.isRollover();
        }

        public void setSelected(boolean b) {
            other.setSelected(b);
        }

        public void setPressed(boolean b) {
            other.setPressed(b);
        }

        public void setRollover(boolean b) {
            other.setRollover(b);
        }

        public void setMnemonic(int key) {
            other.setMnemonic(key);
        }

        public int getMnemonic() {
            return other.getMnemonic();
        }

        public void setActionCommand(String s) {
            other.setActionCommand(s);
        }

        public String getActionCommand() {
            return other.getActionCommand();
        }

        public void setGroup(ButtonGroup group) {
            other.setGroup(group);
        }

        public void addActionListener(ActionListener l) {
            other.addActionListener(l);
        }

        public void removeActionListener(ActionListener l) {
            other.removeActionListener(l);
        }

        public void addItemListener(ItemListener l) {
            other.addItemListener(l);
        }

        public void removeItemListener(ItemListener l) {
            other.removeItemListener(l);
        }

        public void addChangeListener(ChangeListener l) {
            other.addChangeListener(l);
        }

        public void removeChangeListener(ChangeListener l) {
            other.removeChangeListener(l);
        }

        public Object[] getSelectedObjects() {
            return other.getSelectedObjects();
        }
    }

    /**
     * We rotate between NOT_SELECTED, SELECTED and DONT_CARE. Subclass can override this method to tell the check box
     * what next state is. Here is the default implementation.
     * <code><pre>
     *   if (current == NOT_SELECTED) {
     *       return SELECTED;
     *   }
     *   else if (current == SELECTED) {
     *       return DONT_CARE;
     *   }
     *   else {
     *       return NOT_SELECTED;
     *   }
     * </code></pre>
     *
     * @param current the current state
     * @return the next state of the current state.
     */
    protected State getNextState(State current) {
        if (current == NOT_SELECTED) {
            return SELECTED;
        }
        else if (current == SELECTED) {
            return DONT_CARE;
        }
        else /*if (current == DONT_CARE)*/ {
            return NOT_SELECTED;
        }
    }
}

