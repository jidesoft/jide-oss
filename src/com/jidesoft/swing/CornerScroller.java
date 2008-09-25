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
import javax.swing.plaf.UIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.GeneralPath;
import java.io.Serializable;


/*
 * @author Davide Raccagni
 * @author JIDE Software
*/
@SuppressWarnings("serial")
public class CornerScroller extends JideButton {
    protected ScrollPaneOverview _scrollPaneBidule;

    public CornerScroller(JScrollPane scrollPane) {
        _scrollPaneBidule = new ScrollPaneOverview(scrollPane, this);
        setFocusPainted(false);
        addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _scrollPaneBidule.display();
            }
        });
        setBorderPainted(false);

        setIcon(new ScrollerIcon());
        setPreferredSize(new Dimension(16, 16));
    }

    public void setSelectionBorderColor(Color selectionBorder) {
        if (_scrollPaneBidule != null) {
            _scrollPaneBidule.setSelectionBorderColor(selectionBorder);
        }
    }

    private static class ScrollerIcon implements Icon, UIResource, Serializable {
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
