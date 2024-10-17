package utils;

import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JFrame;

public class WindowResize {
    public void addWindowResize(JFrame frame, Component boundRef[], Component contentPane[]) {
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int width = frame.getWidth();
                int height = frame.getHeight();

                for (Component i : boundRef) {
                    i.setBounds(0, 0, width, height);
                    i.setBounds(0, 0, width, height);

                }

                for (Component j : contentPane) {
                    j.revalidate();
                    j.repaint();

                }
            }
        });

    }
}
