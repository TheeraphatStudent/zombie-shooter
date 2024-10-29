package utils;

import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class UseAlert {
    private transient BufferedImage successImage;

    public void warringAlert(String message) {
        JOptionPane.showMessageDialog(null, message, "Warning", JOptionPane.WARNING_MESSAGE);

    }

    public void successAlert(String message) {
        try {
            successImage = (BufferedImage) new LoadImage().getImage("resource/images/icon/success.png");
            Image scaledImage = successImage.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            ImageIcon successIcon = new ImageIcon(scaledImage);

            JOptionPane.showMessageDialog(null, message, "Success", JOptionPane.INFORMATION_MESSAGE, successIcon);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
