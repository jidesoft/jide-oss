package com.jidesoft.swing;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;

public class TestResizableWindow {

    public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                final ResizableWindow resizableWindow = new ResizableWindow();
                resizableWindow.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
                resizableWindow.getResizable().setResizableCorners(Resizable.ALL);
                resizableWindow.getContentPane().add(new JButton(new AbstractAction("Close window") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        resizableWindow.dispose();
                        System.exit(0);
                    }
                }));
                resizableWindow.pack();
                resizableWindow.setLocationRelativeTo(null);
                resizableWindow.setVisible(true);
            }
        });
    }
}
