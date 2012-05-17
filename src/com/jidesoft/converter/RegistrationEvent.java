/*
 * @(#)RegistrationEvent.java 3/20/2012
 *
 * Copyright 2002 - 2012 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.converter;

/**
 * This class was moved to com.jidesoft.utils since 3.3.7 release.
 *
 * @deprecated please use the one at com.jidesoft.utils.RegistrationEvent.
 */
@Deprecated
public class RegistrationEvent extends com.jidesoft.utils.RegistrationEvent {
    public RegistrationEvent(Object source, int id) {
        super(source, id);
    }

    public RegistrationEvent(Object source, int id, Object object, Object key, Object context) {
        super(source, id, object, key, context);
    }
}
