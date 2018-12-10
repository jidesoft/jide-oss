package com.jidesoft.swing;

/**
 */
public interface PartialSide {
    static final int NORTH = 1;
    static final int SOUTH = 2;
    static final int EAST = 4;
    static final int WEST = 8;
    static final int HORIZONTAL = NORTH | SOUTH;
    static final int VERTICAL = EAST | WEST;
    static final int ALL = VERTICAL | HORIZONTAL;
}
