/*
 * @(#)OverlayPasswordField.java 8/11/2009
 *
 * Copyright 2002 - 2009 JIDE Software Inc. All rights reserved.
 *
 * Contributor: lonny27
 *
 */

package com.jidesoft.swing;

import javax.swing.JPasswordField;
import javax.swing.text.Document;

public class OverlayPasswordField extends JPasswordField {
  public OverlayPasswordField() {
  }

  public OverlayPasswordField(Document doc, String txt, int columns) {
    super(doc, txt, columns);
  }

  public OverlayPasswordField(int columns) {
    super(columns);
  }

  public OverlayPasswordField(String text, int columns) {
    super(text, columns);
  }

  public OverlayPasswordField(String text) {
    super(text);
  }

  @Override
  public void repaint(long tm, int x, int y, int width, int height) {
    super.repaint(tm, x, y, width, height);
    OverlayableUtils.repaintOverlayable(this);
  }
}