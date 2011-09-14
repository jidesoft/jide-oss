/* 
 * $Id: SwingPropertyChangeSupport.java,v 1.1 2005/06/18 21:27:14 idk Exp $
 * 
 * Copyright (c) 1995, 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.jidesoft.utils;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;

/**
 * This subclass of {@code java.beans.PropertyChangeSupport} is almost identical in functionality. The only difference
 * is if constructed with {@code SwingPropertyChangeSupport(sourceBean, true)} it ensures listeners are only ever
 * notified on the <i>Event Dispatch Thread</i>.
 *
 * @author Igor Kushnirskiy
 * @version $Revision: 1.1 $ $Date: 2005/06/18 21:27:14 $
 */

public final class SwingPropertyChangeSupport extends PropertyChangeSupport {

    /**
     * Constructs a SwingPropertyChangeSupport object.
     *
     * @param sourceBean The bean to be given as the source for any events.
     * @throws NullPointerException if {@code sourceBean} is {@code null}
     */
    public SwingPropertyChangeSupport(Object sourceBean) {
        this(sourceBean, false);
    }

    /**
     * Constructs a SwingPropertyChangeSupport object.
     *
     * @param sourceBean  the bean to be given as the source for any events
     * @param notifyOnEDT whether to notify listeners on the <i>Event Dispatch Thread</i> only
     * @throws NullPointerException if {@code sourceBean} is {@code null}
     * @since 1.6
     */
    public SwingPropertyChangeSupport(Object sourceBean, boolean notifyOnEDT) {
        super(sourceBean);
        this.notifyOnEDT = notifyOnEDT;
    }

    /**
     * {@inheritDoc}
     * <p/>
     * <p/>
     * If {@see #isNotifyOnEDT} is {@code true} and called off the <i>Event Dispatch Thread</i> this implementation uses
     * {@code SwingUtilities.invokeLater} to send out the notification on the <i>Event Dispatch Thread</i>. This ensures
     * listeners are only ever notified on the <i>Event Dispatch Thread</i>.
     *
     * @throws NullPointerException if {@code evt} is {@code null}
     * @since 1.6
     */
    @Override
    public void firePropertyChange(final PropertyChangeEvent evt) {
        if (evt == null) {
            throw new NullPointerException();
        }
        if (!isNotifyOnEDT()
                || SwingUtilities.isEventDispatchThread()) {
            super.firePropertyChange(evt);
        }
        else {
            SwingUtilities.invokeLater(
                    new Runnable() {
                        public void run() {
                            firePropertyChange(evt);
                        }
                    });
        }
    }

    /**
     * Returns {@code notifyOnEDT} property.
     *
     * @return {@code notifyOnEDT} property
     *
     * @since 1.6
     */
    public final boolean isNotifyOnEDT() {
        return notifyOnEDT;
    }

    // Serialization version ID
    static final long serialVersionUID = 7162625831330845068L;

    /**
     * whether to notify listeners on EDT
     *
     * @serial
     * @since 1.6
     */
    private final boolean notifyOnEDT;
}
