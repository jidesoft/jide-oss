/*
 * @(#)Insets2D.java
 *
 * 2002 - 2012 JIDE Software Incorporated. All rights reserved.
 * Copyright (c) 2005 - 2012 Catalysoft Limited. All rights reserved.
 */
package com.jidesoft.awt.geom;

import java.io.Serializable;

/**
 * Similar to java.awt.Insets, but with doubles for the top, left, bottom, right values.
 * This means that instances of this class can be used for setting proportionally sized insets
 * which are better for components that should retain their shape and overall appearance when they
 * are re-sized.
 *
 * @see java.awt.Insets
 */
public abstract class Insets2D implements Serializable {

    private static final long serialVersionUID = -7723331225837606159L;

    /**
     * Returns the inset from the top in double precision
     * @return the inset from the top in double precision
     */
    public abstract double getTop();

    /**
     * Returns the inset from the left in double precision
     * @return the inset from the left in double precision
     */
    public abstract double getLeft();

    /**
     * Returns the inset from the bottom in double precision
     * @return the inset from the bottom in double precision
     */
    public abstract double getBottom();

    /**
     * Returns the inset from the right in double precision
     * @return the inset from the right in double precision
     */
    public abstract double getRight();

    /**
     * An Insets2D instance specified with double precision
     */
    public static class Double extends Insets2D implements Serializable {

        private static final long serialVersionUID = 4310600205031805311L;

        /**
         * The inset from the top
         */
        public double top;

        /**
         * The inset from the left
         */
        public double left;

        /**
         * The inset from the bottom
         */
        public double bottom;

        /**
         * The inset from the right
         */
        public double right;

        /**
         * Construct an Insets2D instance using double precision
         * @param top the inset from the top
         * @param left the inset from the left
         * @param bottom the inset from the bottom
         * @param right the inset from the right
         */
        public Double(double top, double left, double bottom, double right) {
            this.top = top;
            this.left = left;
            this.bottom = bottom;
            this.right = right;
        }

        /**
         * Sets the insets
         * @param top the inset from the top
         * @param left the inset from the left
         * @param bottom the inset from the bottom
         * @param right the inset from the right
         */
        public void set(double top, double left, double bottom, double right) {
            this.top = top;
            this.left = left;
            this.bottom = bottom;
            this.right = right;
        }

        /**
         * {@inheritDoc}
         */
        public double getBottom() {
            return bottom;
        }

        /**
         * {@inheritDoc}
         */
        public double getLeft() {
            return left;
        }

        /**
         * {@inheritDoc}
         */
        public double getRight() {
            return right;
        }

        /**
         * {@inheritDoc}
         */
        public double getTop() {
            return top;
        }

    }

    /**
     * An Insets2D instance specified with float precision
     */
    public static class Float extends Insets2D implements Serializable {

        private static final long serialVersionUID = 4796948322194231916L;

        public float top, left, bottom, right;

        /**
         * Construct an Insets2D instance using float precision
         * @param top the inset from the top
         * @param left the inset from the left
         * @param bottom the inset from the bottom
         * @param right the inset from the right
         */
        public Float(float top, float left, float bottom, float right) {
            this.top = top;
            this.left = left;
            this.bottom = bottom;
            this.right = right;
        }

        /**
         * Sets the insets with float precision
         * @param top the inset from the top
         * @param left the inset from the left
         * @param bottom the inset from the bottom
         * @param right the inset from the right
         */
        public void set(float top, float left, float bottom, float right) {
            this.top = top;
            this.left = left;
            this.bottom = bottom;
            this.right = right;
        }

        /**
         * {@inheritDoc}
         */
        public double getBottom() {
            return (double) bottom;
        }

        /**
         * {@inheritDoc}
         */
        public double getLeft() {
            return (double) left;
        }

        /**
         * {@inheritDoc}
         */
        public double getRight() {
            return (double) right;
        }

        /**
         * {@inheritDoc}
         */
        public double getTop() {
            return (double) top;
        }

    }
}
