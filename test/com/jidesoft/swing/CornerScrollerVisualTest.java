package com.jidesoft.swing;

import java.awt.*;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class CornerScrollerVisualTest {

    /*
     * Issue: if the view component is very large, the zoom must be limited or the overview becomes to small.
     * Possible illegal buffered image argument when the BufferedImage width/height becomes zero.
     * 
     * The problem is that you can only select to scroll to the top left corner with the overview.
     * Although for typical components this won't be a problem.
     */
    public static void main(String[] args) {
        
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                LargeView view = new LargeView();
                view.setPreferredSize(new Dimension(100000, 100000));
                JScrollPane pane = new JScrollPane(view);
                pane.setCorner(JScrollPane.LOWER_RIGHT_CORNER, new CornerScroller(pane));
                JFrame frame = new JFrame("Test");
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.getContentPane().add(pane);
                frame.setPreferredSize(new Dimension(800, 600));
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
    
    private static class LargeView extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            g.setFont(g.getFont().deriveFont(24f));
            Rectangle clipBounds = g.getClipBounds();
            int width = getWidth() / 200;
            int height = getWidth() / 200;
            for(int x = ((clipBounds.x / width) * width) - width / 2; x < clipBounds.x + clipBounds.width + width; x += width) {
                for(int y = ((clipBounds.y / height) * height) - height / 2; y < clipBounds.y + clipBounds.height + height; y += height) {
                    g.drawString("(" + x + ", " + y + ")", x, y);
                }
            }
        }
    }
}
