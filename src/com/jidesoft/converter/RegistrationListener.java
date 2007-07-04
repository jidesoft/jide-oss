/*
 * @(#)ManagerListener.java 11/28/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.converter;

import java.util.EventListener;

/**
 * The listener interface for receiving registration change events.
 */
public interface RegistrationListener extends EventListener {
    /**
     * Called whenever the registration is changed.
     *
     * @param event the RegistrationEvent to be fired.
     */
    public void registrationChanged(RegistrationEvent event);
}
