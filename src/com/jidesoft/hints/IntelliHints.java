/*
 * @(#)IntelliHints.java 7/24/2005
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.hints;

import javax.swing.*;

/**
 * <code>IntelliHints</code> is an interface that defines all necessary methods to implement
 * showing a hint popup depending on a context and allows user to pick from a list of hints.
 * {@link #createHintsComponent()} will create a component that contains the hints. It will be
 * shown in a popup window. After hint popup is created, {@link #updateHints(Objectcontext)} will update the
 * content of hints based on the context. Once user picks a hint from the hint popup, {@link #getSelectedHint()}
 * will be called to find the hint that user selected and call {@link #acceptHint(Object)} to accept it.
 */
public interface IntelliHints {
    /**
     * Creates the component which contains hints. At this moment, the content should be empty. Following call
     * {@link #updateHints(Object)} will update the content.
     *
     * @return the component which will be used to display the hints.
     */
    JComponent createHintsComponent();

    /**
     * Update hints depending on the context.
     *
     * @param context the current context
     * @return true or false. If it is false, hint popup will not be shown.
     */
    boolean updateHints(Object context);

    /**
     * Gets the selected value. This value will be used to complete the text component.
     *
     * @return the selected value.
     */
    Object getSelectedHint();

    /**
     * Accepts the selected hint.
     *
     * @param hint
     */
    void acceptHint(Object hint);
}
