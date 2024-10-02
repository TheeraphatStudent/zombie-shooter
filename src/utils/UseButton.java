package utils;

// Swing
import javax.swing.JButton;
import javax.swing.JFrame;

import page.home.GameCenter;
import page.home.Register;

import javax.swing.ImageIcon;

// AWT
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Cursor;
import java.awt.Font;

// Img
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class UseButton {
    private int fontSize = 16;
    private Component componentRef;

    public UseButton() {
    }

    public UseButton(int getFontSize) {
        this.fontSize = getFontSize;

    }

    // Get Component Reference
    public UseButton(int getFontSize, Component ref) {
        this.fontSize = getFontSize;
        this.componentRef = ref;

    }

    public JButton createButtonWithImage(String imagePath, String title, int width, int height, String cursorCase) {
        JButton btn = new JButton(title);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(false);

        btn.setPreferredSize(new Dimension(width, height));

        switch (cursorCase) {
            case "hand":
                btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                break;

            case "cross":
                btn.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                break;

            default:
                btn.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                break;
        }

        try (InputStream is = UseButton.class.getClassLoader().getResourceAsStream(imagePath)) {
            if (is == null) {
                System.out.println("Image not found");
            } else {
                BufferedImage iconImage = ImageIO.read(is);
                int buttonWidth = btn.getPreferredSize().width;
                int buttonHeight = btn.getPreferredSize().height;
                Image resizedImage = iconImage.getScaledInstance(buttonWidth, buttonHeight, Image.SCALE_SMOOTH);
                btn.setIcon(new ImageIcon(resizedImage));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return btn;
    }

    //
    public JButton createButtonAndChangePage(
            String warringText,
            String imagePath,
            String title,
            Color bg,
            int width,
            int height,
            String cursorCase,
            JFrame thispage,
            String destpage) {
        JButton btn = new JButton();
        btn.setFont(new Font("Arial", Font.PLAIN, this.fontSize));
        btn.setText(title);

        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBackground(bg);

        btn.setPreferredSize(new Dimension(width, height));

        switch (cursorCase) {
            case "hand":
                btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                break;

            default:
                btn.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                break;
        }

        try (InputStream is = UseButton.class.getClassLoader().getResourceAsStream(imagePath)) {
            if (is == null) {
                System.out.println("Image not found");

            } else {
                BufferedImage iconImage = ImageIO.read(is);
                int buttonWidth = btn.getPreferredSize().width;
                int buttonHeight = btn.getPreferredSize().height;
                Image resizedImage = iconImage.getScaledInstance(buttonWidth, buttonHeight, Image.SCALE_SMOOTH);
                btn.setIcon(new ImageIcon(resizedImage));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // dest - ปลายทาง
        btn.addActionListener((e -> {
            switch (destpage) {
                case "game":
                    GameCenter gameCenter = new GameCenter();

                    thispage.dispose();
                    gameCenter.setVisible(true);

                    break;

                default:
                    System.out.println("Page not found!");
                    break;
            }
        }));

        return btn;
    }

    public JButton createSimpleButton(
            String title,
            Color bg,
            int width,
            int height,
            String cursorCase) {
        JButton btn = new JButton();
        btn.setFont(new Font("Arial", Font.PLAIN, this.fontSize));
        btn.setText(title);

        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBackground(bg);

        btn.setPreferredSize(new Dimension(width, height));

        switch (cursorCase) {
            case "hand":
                btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                break;

            default:
                btn.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                break;
        }

        return btn;
    }

}
