/*
 * @(#)Validator.java
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.validation;

import java.util.EventListener;

/**
 * <code>Validator</code> is an interface for validating a object.
 */
public interface Validator extends EventListener {

    /**
     * Validates the value in the ValidationObject.
     * <p/>
     * The event has both old value and new value as well as the source who sends this object.
     * The implementation of this method should validate if the
     * value is valid. If the value is valid, simply return ValidationResult.OK.
     * Otherwise, constructs a ValidationResult and provide the id and message.
     *
     * @param vo the ValidationObject
     * @return ValidationResult
     */
    ValidationResult validating(ValidationObject vo);
}
