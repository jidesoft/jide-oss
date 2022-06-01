package com.jidesoft.swing;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class RangeSliderTest {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            final RangeSlider rangeSlider = new RangeSlider(0, 100, 10, 80);
            final JPanel rangeSliderAndValuePanel = new JPanel(new BorderLayout());
            rangeSliderAndValuePanel.add(rangeSlider, BorderLayout.CENTER);

            JFrame f = new JFrame();
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.add(new JScrollPane(rangeSliderAndValuePanel));
            f.setSize(400, 400);
            f.setLocation(200, 200);
            f.setVisible(true);

        });
    }
}