package com.jidesoft.swing;

/**
 */
public interface PartialSide {
    final static int NORTH = 1;
    final static int SOUTH = 2;
    final static int EAST = 4;
    final static int WEST = 8;
    final static int HORIZONTAL = NORTH | SOUTH;
    final static int VERTICAL = EAST | WEST;
    final static int ALL = VERTICAL | HORIZONTAL;
}
