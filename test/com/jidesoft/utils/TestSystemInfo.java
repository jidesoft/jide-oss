package com.jidesoft.utils;

import junit.framework.TestCase;

public class TestSystemInfo extends TestCase {
    public void testJDKVersion() {
        SystemInfo.JavaVersion v = new SystemInfo.JavaVersion("1.6.0");
        assertEquals(v.getMajorVersion(), 1.6);
        assertEquals(v.getMinorVersion(), 0);
        assertEquals(v.getBuildNumber(), 0);

        v = new SystemInfo.JavaVersion("1.6.0_10");
        assertEquals(v.getMajorVersion(), 1.6);
        assertEquals(v.getMinorVersion(), 0);
        assertEquals(v.getBuildNumber(), 10);

        v = new SystemInfo.JavaVersion("1.6.0_10-rc2");
        assertEquals(v.getMajorVersion(), 1.6);
        assertEquals(v.getMinorVersion(), 0);
        assertEquals(v.getBuildNumber(), 10);
        assertEquals(v.getPatch(), "-rc2");

        v = new SystemInfo.JavaVersion("1.4.2");
        assertEquals(v.getMajorVersion(), 1.4);
        assertEquals(v.getMinorVersion(), 2);
        assertEquals(v.getBuildNumber(), 0);

        v = new SystemInfo.JavaVersion("1.4.2_10");
        assertEquals(v.getMajorVersion(), 1.4);
        assertEquals(v.getMinorVersion(), 2);
        assertEquals(v.getBuildNumber(), 10);

        v = new SystemInfo.JavaVersion("1.5.0_beta2");
        assertEquals(v.getMajorVersion(), 1.5);
        assertEquals(v.getMinorVersion(), 0);
        assertEquals(v.getBuildNumber(), 0);
        assertEquals(v.getPatch(), "beta2");
    }
}
