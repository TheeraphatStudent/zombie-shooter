package utils;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

public class LoadImage {
    public static class BackgroundPanel extends JPanel {
        // No changes to the logic inside this class
        private Image backgroundImage;
        private int xOffset = 0;
        private int yOffset = 0;
        private double darkenFilter = 1f;

        public BackgroundPanel(
                String imagePath,
                int panelWidth,
                int panelHeight,
                int moveSpeed,
                double getDarken,
                boolean onMove) {
            setPreferredSize(new Dimension(panelWidth, panelHeight));
            this.backgroundImage = new LoadImage().getImage(imagePath);

            this.darkenFilter = 1 - getDarken;

            Timer timer = new Timer(moveSpeed, e -> {
                xOffset = (xOffset + 1) % backgroundImage.getWidth(this); // Move left
                yOffset = (yOffset + 1) % backgroundImage.getHeight(this); // Move down

                repaint();
            });

            if (onMove) {
                timer.start();

            } else {
                timer.stop();

            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            int imgWidth = backgroundImage.getWidth(this);
            int imgHeight = backgroundImage.getHeight(this);

            BufferedImage bufferedImage = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics bg = bufferedImage.getGraphics();
            bg.drawImage(backgroundImage, 0, 0, null);
            bg.dispose();


            if (darkenFilter < 1) {
                RescaleOp rescaleOp = new RescaleOp(0.5f, 0, null);
                bufferedImage = rescaleOp.filter(bufferedImage, null);
            }

            int imageXOffset = -imgWidth + xOffset;
            int imageYOffset = -imgHeight + yOffset;

            for (int x = imageXOffset; x < getWidth(); x += imgWidth) {
                for (int y = imageYOffset; y < getHeight(); y += imgHeight) {
                    g2d.drawImage(bufferedImage, x, y, this);
                }
            }
        }
    }

    public Image getImage(String fullPath) {
        Image backgroundImage = null;

        // System.out.println(fullPath);

        try (InputStream is = LoadImage.class.getClassLoader().getResourceAsStream(fullPath)) {
            if (is == null) {
                System.out.println("Image not found");
            } else {
                backgroundImage = ImageIO.read(is);
                return backgroundImage;
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return backgroundImage;
    }

    public Image getImage(String path, String source) {
        Image backgroundImage = null;
        String fullPath = path + source;

        // System.out.println(fullPath);

        try (InputStream is = LoadImage.class.getClassLoader().getResourceAsStream(fullPath)) {
            if (is == null) {
                System.out.println("Image not found");
            } else {
                backgroundImage = ImageIO.read(is);
                return backgroundImage;
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return backgroundImage;
    }
}
