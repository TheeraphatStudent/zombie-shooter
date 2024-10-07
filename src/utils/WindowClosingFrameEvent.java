package utils;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class WindowClosingFrameEvent {
    private JFrame currentFrame;
    private JFrame destFrame;
    private UseGlobal globals;

    public WindowClosingFrameEvent() {
    }

    public WindowClosingFrameEvent(UseGlobal globals) {
        this.globals = globals;

    }

    public WindowClosingFrameEvent(JFrame _currentFrame) {
        this.currentFrame = _currentFrame;
        this.currentFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                JFrame frame = (JFrame) e.getSource();

                int result = JOptionPane.showConfirmDialog(
                        frame,
                        "Are you sure! you want to exit my game?",
                        "Exit Game",
                        JOptionPane.YES_NO_OPTION);

                if (result == JOptionPane.YES_OPTION) {
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    changeFrameEvent(_currentFrame);

                } else {
                    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                }
            }
        });

        _currentFrame.revalidate();
    }

    public WindowClosingFrameEvent(JFrame _currentFrame, JFrame _destFrame) {
        System.out.println("Window Closing Work!");

        this.currentFrame = _currentFrame;
        this.destFrame = _destFrame;

        this.currentFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                changeFrameEvent();

            }

        });

    }

    private void changeFrameEvent() {
        if (!this.destFrame.isActive() || this.destFrame != null) {
            System.out.println("Change frame event work!");
            this.destFrame.setVisible(true);

        }

        this.currentFrame.dispose();
        this.currentFrame.setVisible(false);

        // System.exit(-1);

    }

    private void changeFrameEvent(JFrame breakDownFrame) {
        if (!breakDownFrame.isActive() && breakDownFrame != null) {
            System.out.println("Break down work!");
            breakDownFrame.dispose();
            System.exit(0);

        }

    }

    public void navigateTo(JFrame _current, JFrame _dest, boolean onEvent) {
        if (_current == null || _dest == null) {
            System.out.println("Something Went Wrong!");
            return;
        }

        if (onEvent) {
            _current.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent event) {
                    _current.dispose();
                    _dest.setVisible(true);
                }
            });
        } else {
            _current.setVisible(false);
            _dest.setVisible(true);
        }
    }

    public void closePage(JFrame _currentFrame) {
        JFrame frame = new JFrame();

        int result = JOptionPane.showConfirmDialog(
                frame,
                "Are you sure! you want to exit my game?",
                "Exit Game",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            changeFrameEvent(_currentFrame);
        } else {
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        }

    }
}