package com.jidesoft.swing;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * <code>AutoRepeatButtonUtils</code> is a utility class which can make a button automatically
 * trigger action events continuously. To enable this feature on any button, just call
 * AutoRepeatButtonUtils.install(button) or AutoRepeatButtonUtils.install(button, delay,
 * initialDelay).
 */
public class AutoRepeatButtonUtils implements ActionListener, MouseListener {
    public static String AUTO_REPEAT = "AutoRepeat";
    public static String CLIENT_PROPERTY_AUTO_REPEAT = "AutoRepeat.AutoRepeatButtonUtils";
    public static int DEFAULT_DELAY = 200;
    public static int DEFAULT_INITIAL_DELAY = 500;

    private Timer _timer = null;
    private AbstractButton _button;

    /**
     * Enable auto-repeat feature on the button.
     *
     * @param button the button.
     */
    public static void install(AbstractButton button) {
        uninstall(button);
        new AutoRepeatButtonUtils().installListeners(button, DEFAULT_DELAY, DEFAULT_INITIAL_DELAY);
    }

    /**
     * Enable auto-repeat feature on the button.
     *
     * @param button       the button.
     * @param delay        the delay between action events.
     * @param initialDelay the initial delay. It is from the time mouse is pressed to the first
     *                     action event.
     */
    public static void install(AbstractButton button, int delay, int initialDelay) {
        uninstall(button);
        new AutoRepeatButtonUtils().installListeners(button, delay, initialDelay);
    }

    /**
     * Disabled the auto-repeat feature on the button which called install before.
     *
     * @param button the button that has auto-repeat feature.
     */
    public static void uninstall(AbstractButton button) {
        Object clientProperty = button.getClientProperty(CLIENT_PROPERTY_AUTO_REPEAT);
        if (clientProperty instanceof AutoRepeatButtonUtils) {
            ((AutoRepeatButtonUtils) clientProperty).uninstallListeners();
        }
    }

    protected void installListeners(AbstractButton button, int delay, int initialDelay) {
        _button = button;
        button.putClientProperty(CLIENT_PROPERTY_AUTO_REPEAT, this);
        button.addMouseListener(this);

        _timer = new Timer(delay, this);
        _timer.setInitialDelay(initialDelay);
        _timer.setRepeats(true);
    }

    protected void uninstallListeners() {
        if (_button != null) {
            _button.putClientProperty(CLIENT_PROPERTY_AUTO_REPEAT, null);
            _button.removeMouseListener(this);
            _button = null;
        }
        if (_timer != null) {
            _timer.stop();
            _timer = null;
        }
    }

    public void mousePressed(MouseEvent e) {
        _timer.start();
    }

    public void mouseReleased(MouseEvent e) {
        _timer.stop();
    }

    public void mouseExited(MouseEvent e) {
        _timer.stop();
    }


    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void actionPerformed(ActionEvent event) {
        // Guaranteed to return a non-null array
        ActionListener[] listeners = _button.getActionListeners();
        ActionEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 1; i >= 0; i--) {
            ActionListener listener = listeners[i];
            if (e == null) {
                String actionCommand = event.getActionCommand();
                if (actionCommand == null) {
                    actionCommand = _button.getActionCommand();
                }
                e = new ActionEvent(_button,
                        ActionEvent.ACTION_PERFORMED,
                        actionCommand,
                        event.getWhen(),
                        event.getModifiers());
            }
            listener.actionPerformed(e);
        }
    }
}
