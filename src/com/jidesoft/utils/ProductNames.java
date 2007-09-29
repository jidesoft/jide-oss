/*
 * @(#)ProductNames.java
 *
 * Copyright 2002 - 2005 JIDE Software Inc. All rights reserved.
 */
package com.jidesoft.utils;

/**
 * A list of product names from JIDE Software, Inc.
 */
public interface ProductNames {
    public final static String PRODUCT_NAME_COMMON = "JIDE Common Layer";
    public final static String PRODUCT_NAME_DOCK = "JIDE Docking Framework";
    public final static String PRODUCT_NAME_COMPONENTS = "JIDE Components";
    public final static String PRODUCT_NAME_GRIDS = "JIDE Grids";
    public final static String PRODUCT_NAME_DIALOGS = "JIDE Dialogs";
    public final static String PRODUCT_NAME_ACTION = "JIDE Action Framework";
    public final static String PRODUCT_NAME_SHORTCUT = "JIDE Shortcut Editor";
    public final static String PRODUCT_NAME_PIVOT = "JIDE Pivot Grid";
    public final static String PRODUCT_NAME_CODE_EDITOR = "JIDE Code Editor";
    public final static String PRODUCT_NAME_FEEDREADER = "JIDE Feed Reader";
    public final static String PRODUCT_NAME_DASHBOARD = "JIDE Dashboard";
    public final static String PRODUCT_NAME_JDAF = "JIDE Desktop Application Framework";

    public final static String COMPONENT_PRODUCTS = PRODUCT_NAME_DOCK + ", " +
            PRODUCT_NAME_ACTION + ", " +
            PRODUCT_NAME_COMPONENTS + ", " +
            PRODUCT_NAME_GRIDS + ", " +
            PRODUCT_NAME_DIALOGS + ", " +
            PRODUCT_NAME_PIVOT + ", " +
            PRODUCT_NAME_SHORTCUT + ", " +
            PRODUCT_NAME_CODE_EDITOR + ", " +
            PRODUCT_NAME_FEEDREADER + ", " +
            "and " +
            PRODUCT_NAME_DASHBOARD;

    public final static String SOLUTION_PRODUCTS = PRODUCT_NAME_JDAF;

    public static final int PRODUCT_COMMON = 0;
    public static final int PRODUCT_DOCK = 0x1;
    public static final int PRODUCT_COMPONENTS = 0x2;
    public static final int PRODUCT_GRIDS = 0x4;
    public static final int PRODUCT_DIALOGS = 0x8;
    public static final int PRODUCT_ACTION = 0x10;
    public static final int PRODUCT_PIVOT = 0x20;
    public static final int PRODUCT_SHORTCUT = 0x40;
    public static final int PRODUCT_CODE_EDITOR = 0x80;
    public static final int PRODUCT_FEEDREADER = 0x100;
    public static final int PRODUCT_JDAF = 0x200;
    public static final int PRODUCT_DASHBOARD = 0x400;

    public static final int PRODUCT_PROFESSIONAL_SUITE = PRODUCT_COMMON | PRODUCT_ACTION | PRODUCT_DOCK;
    public static final int PRODUCT_ENTERPRISE_SUITE = PRODUCT_PROFESSIONAL_SUITE | PRODUCT_COMPONENTS | PRODUCT_GRIDS | PRODUCT_DIALOGS;
    public static final int PRODUCT_ULTIMATE_SUITE = PRODUCT_ENTERPRISE_SUITE | PRODUCT_PIVOT | PRODUCT_SHORTCUT | PRODUCT_FEEDREADER | PRODUCT_CODE_EDITOR | PRODUCT_DASHBOARD;
    public static final int PRODUCT_SOLUTION = PRODUCT_JDAF;
    public static final int PRODUCT_ALL = 0xFFFF;

}
