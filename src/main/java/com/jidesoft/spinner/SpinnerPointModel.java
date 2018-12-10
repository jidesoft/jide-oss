/*
 * @(#)SpinnerPointModel.java 4/8/2007
 *
 * Copyright 2002 - 2007 JIDE Software Inc. All rights reserved.
 */

package com.jidesoft.spinner;

import javax.swing.*;
import java.awt.*;

/**
 * @author Nako Ruru
 */
public class SpinnerPointModel extends AbstractSpinnerModel {

    public static final int FIELD_X = 0;
    public static final int FIELD_Y = 1;

    private Point point;
    private int field = FIELD_X;

    /**
     * Create a default <code>SpinnerPointModel</code>
     */
    public SpinnerPointModel() {
        this(null);
    }

    /**
     * Create a <code>SpinnerPointModel</code> with a specified <code>Point</code>
     *
     * @param point this specified<code>Point</code>
     */
    public SpinnerPointModel(Point point) {
        this.point = point == null ? new Point() : point;
    }

    /**
     * The <i>current element</i> of the sequence.  This element is usually displayed by the <code>editor</code> part of
     * a <code>JSpinner</code>.
     *
     * @return the current spinner value.
     *
     * @see #setValue
     */
    public Object getValue() {
        return point;
    }

    /**
     * Changes current value of the model, typically this value is displayed by the <code>editor</code> part of a
     * <code>JSpinner</code>. If the <code>SpinnerModel</code> implementation doesn't support the specified value then
     * an <code>IllegalArgumentException</code> is thrown.  For example a <code>SpinnerModel</code> for numbers might
     * only support values that are integer multiples of ten. In that case, <code>model.setValue(new Number(11))</code>
     * would throw an exception.
     *
     * @param value new value
     * @throws IllegalArgumentException if <code>value</code> isn't allowed
     * @see #getValue
     */
    public void setValue(Object value) {
        if (value instanceof Point) {
            setPoint((Point) value);
        }
    }

    /**
     * The <i>current element</i> of the sequence.  This element is usually displayed by the <code>editor</code> part of
     * a <code>JSpinner</code>.
     *
     * @return the current spinner value.
     *
     * @see #setPoint(Point)
     * @see #getValue()
     */
    public Point getPoint() {
        return point;
    }

    /**
     * @param point the new point
     */
    public void setPoint(Point point) {
        if (!this.point.equals(point)) {
            this.point = point;
            fireStateChanged();
        }
    }

    /**
     * @return the field.
     */
    public int getField() {
        return field;
    }

    /**
     * @param field the new field.
     */
    public void setField(int field) {
        this.field = field;
    }

    /**
     * Return the object in the sequence that comes after the object returned by <code>getValue()</code>. If the end of
     * the sequence has been reached then return null. Calling this method does not effect <code>value</code>.
     *
     * @return the next legal value or null if one doesn't exist
     *
     * @see #getValue
     * @see #getPreviousValue
     */
    public Object getNextValue() {
        Point p = (Point) point.clone();
        if (field == FIELD_X) {
            p.x++;
        }
        else {
            p.y++;
        }
        return p;
    }

    /**
     * Return the object in the sequence that comes before the object returned by <code>getValue()</code>.  If the end
     * of the sequence has been reached then return null. Calling this method does not effect <code>value</code>.
     *
     * @return the previous legal value or null if one doesn't exist
     *
     * @see #getValue
     * @see #getNextValue
     */
    public Object getPreviousValue() {
        Point p = (Point) point.clone();
        if (field == FIELD_X) {
            p.x--;
        }
        else {
            p.y--;
        }
        return p;
    }
}
