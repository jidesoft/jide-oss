/*
 * Copyright (c) 2007 Davide Raccagni. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of Davide Raccagni nor the names of
 *    its contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jidesoft.swing;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.UIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;


/*
 * @author Davide Raccagni
 * @author Qian Qian
 * @author JIDE Software
*/
@SuppressWarnings("serial")
public class CornerScroller extends JideButton {

    /*
      * Original code http://forums.java.net/jive/thread.jspa?forumID=73&threadID=14674
      * under "Do whatever you want with this code" license
      */
    @SuppressWarnings("serial")
    class PopupContent extends JComponent {
        private static final int MAX_SIZE = 400;

        private Component theComponent;

        private JPopupMenu thePopupMenu;

        private BufferedImage theImage;

        private Rectangle theStartRectangle;

        private Rectangle theRectangle;

        private Point theStartPoint;

        private double theScale;

        public PopupContent() {
            setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

            theImage = null;
            theStartRectangle = null;
            theRectangle = null;
            theStartPoint = null;
            theScale = 0.0;

            setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            MouseInputListener mil = new MouseInputAdapter() {
                // A new approach suggested by thomas.bierhance@sdm.de

                @Override
                public void mousePressed(MouseEvent e) {
                    if (theStartPoint != null) {
                        Point newPoint = e.getPoint();
                        int deltaX = (int) ((newPoint.x - theStartPoint.x) / theScale);
                        int deltaY = (int) ((newPoint.y - theStartPoint.y) / theScale);
                        scroll(deltaX, deltaY);
                    }
                    theStartPoint = null;
                    theStartRectangle = theRectangle;
                }

                @Override
                public void mouseMoved(MouseEvent e) {
                    if (theStartPoint == null) {
                        theStartPoint = new Point(theRectangle.x + theRectangle.width / 2, theRectangle.y + theRectangle.height / 2);
                    }
                    Point newPoint = e.getPoint();
                    moveRectangle(newPoint.x - theStartPoint.x, newPoint.y - theStartPoint.y);
                }
            };
            addMouseListener(mil);
            addMouseMotionListener(mil);
            thePopupMenu = new JPopupMenu();
            thePopupMenu.setLayout(new BorderLayout());
            thePopupMenu.add(this, BorderLayout.CENTER);
        }

        protected void paintComponent(Graphics g) {
            if (theImage == null || theRectangle == null)
                return;
            Graphics2D g2d = (Graphics2D) g;
            Insets insets = getInsets();
            int xOffset = insets.left;
            int yOffset = insets.top;

            g.setColor(scrollPane.getViewport().getView().getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
            g.drawImage(theImage, xOffset, yOffset, null);

            int availableWidth = getWidth() - insets.left - insets.right;
            int availableHeight = getHeight() - insets.top - insets.bottom;
            Area area = new Area(new Rectangle(xOffset, yOffset, availableWidth, availableHeight));
            area.subtract(new Area(theRectangle));
            g.setColor(new Color(255, 255, 255, 128));
            g2d.fill(area);

            Color oldcolor = g.getColor();
            g.setColor(selectionBorder);
            g.drawRect(theRectangle.x, theRectangle.y, theRectangle.width, theRectangle.height);

            g.setColor(oldcolor);
        }

        public Dimension getPreferredSize() {
            if (theImage == null || theRectangle == null)
                return new Dimension();
            Insets insets = getInsets();
            return new Dimension(theImage.getWidth(null) + insets.left + insets.right, theImage.getHeight(null) + insets.top + insets.bottom);
        }

        public void display() {
            theComponent = scrollPane.getViewport().getView();
            if (theComponent == null) {
                return;
            }

            int maxSize = Math.max(MAX_SIZE, Math.max(scrollPane.getWidth(), scrollPane.getHeight()) / 2);

            double compWidth = theComponent.getWidth();
            double compHeight = theComponent.getHeight();
            double scaleX = maxSize / compWidth;
            double scaleY = maxSize / compHeight;

            theScale = Math.min(scaleX, scaleY);

            theImage = new BufferedImage((int) (theComponent.getWidth() * theScale), (int) (theComponent.getHeight() * theScale), BufferedImage.TYPE_INT_RGB);
            Graphics2D g = theImage.createGraphics();

            g.scale(theScale, theScale);
            /// {{{ Qian Qian 10/72007
            boolean wasDoubleBuffered = theComponent.isDoubleBuffered();
            try {
                if (theComponent instanceof JComponent) {
                    ((JComponent) theComponent).setDoubleBuffered(false);
                }
                theComponent.paint(g);
            }
            finally {
                if (theComponent instanceof JComponent) {
                    ((JComponent) theComponent).setDoubleBuffered(wasDoubleBuffered);
                }
                g.dispose();
            }
            /// QianQian 10/7/2007 }}}
            theStartRectangle = scrollPane.getViewport().getViewRect();
            Insets insets = getInsets();
            theStartRectangle.x = (int) (theScale * theStartRectangle.x + insets.left);
            theStartRectangle.y = (int) (theScale * theStartRectangle.y + insets.right);
            theStartRectangle.width *= theScale;
            theStartRectangle.height *= theScale;
            theRectangle = theStartRectangle;
            Point centerPoint = new Point(theRectangle.x + theRectangle.width / 2, theRectangle.y + theRectangle.height / 2);
            thePopupMenu.show(CornerScroller.this, -centerPoint.x, -centerPoint.y);
        }

        private void moveRectangle(int aDeltaX, int aDeltaY) {
            if (theStartRectangle == null)
                return;
            Insets insets = getInsets();
            Rectangle newRect = new Rectangle(theStartRectangle);
            newRect.x += aDeltaX;
            newRect.y += aDeltaY;
            newRect.x = Math.min(Math.max(newRect.x, insets.left), getWidth() - insets.right - newRect.width);
            newRect.y = Math.min(Math.max(newRect.y, insets.right), getHeight() - insets.bottom - newRect.height);
            Rectangle clip = new Rectangle();
            Rectangle.union(theRectangle, newRect, clip);
            clip.grow(2, 2);
            theRectangle = newRect;
            paintImmediately(clip);
        }

        private void scroll(int aDeltaX, int aDeltaY) {
            JComponent component = (JComponent) scrollPane.getViewport().getView();
            Rectangle rect = component.getVisibleRect();
            rect.x += aDeltaX;
            rect.y += aDeltaY;
            component.scrollRectToVisible(rect);
            thePopupMenu.setVisible(false);
        }
    }

    private JScrollPane scrollPane;

    private Color selectionBorder;

    public CornerScroller(JScrollPane scrollPane) {
        this.scrollPane = scrollPane;

        final PopupContent scrollPaneBidule = new PopupContent();

        setFocusPainted(false);
        addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scrollPaneBidule.display();
            }
        });
        setBorderPainted(false);

        setIcon(new ScrollerIcon());
        setPreferredSize(new Dimension(16, 16));
    }

    public void setSelectionBorderColor(Color selectionBorder) {
        this.selectionBorder = selectionBorder;
    }

    private static class ScrollerIcon implements Icon, UIResource {
        public int getIconHeight() {
            return 16;
        }

        public int getIconWidth() {
            return 16;
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2d = (Graphics2D) g;

            Object oldrenderinghint = g2d.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GeneralPath path = new GeneralPath();

            int mx = x + getIconWidth() / 2;
            int my = y + getIconHeight() / 2;

            int gap = 4;

            path.moveTo(mx - gap, my - 2);
            path.lineTo(mx - gap - 2, my);
            path.lineTo(mx - gap, my + 2);

            path.moveTo(mx - 2, my - gap);
            path.lineTo(mx, my - gap - 2);
            path.lineTo(mx + 2, my - gap);

            path.moveTo(mx + gap, my - 2);
            path.lineTo(mx + gap + 2, my);
            path.lineTo(mx + gap, my + 2);

            path.moveTo(mx - 2, my + gap);
            path.lineTo(mx, my + gap + 2);
            path.lineTo(mx + 2, my + gap);

            g.setColor(Color.GRAY);
            g2d.draw(path);

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldrenderinghint);
        }
    }
}
