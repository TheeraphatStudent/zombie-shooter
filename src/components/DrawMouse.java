package components;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import javax.swing.JPanel;
import utils.LoadImage;

public class DrawMouse extends JPanel {

    private BufferedImage cursorImage;

    public DrawMouse() {
        setOpaque(false);
        setFocusable(false);
        setLayout(new GridBagLayout());

        // Load the cursor image
        Image tempCursor = new LoadImage().getImage("resource/images/cursor.png");

        cursorImage = new BufferedImage(
                tempCursor.getWidth(null),
                tempCursor.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = cursorImage.createGraphics();
        g2d.drawImage(tempCursor, 0, 0, null);
        g2d.dispose();

        // Custom Image
        // float[] scales = { 1, 1, 1, 1 };
        // -> Default

        // Red
        float[] scales = { 1.5f, 0f, 0f, 1.0f };

        float[] offsets = new float[4];
        RescaleOp op = new RescaleOp(scales, offsets, null);
        cursorImage = op.filter(cursorImage, null);

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Cursor customCursor = toolkit.createCustomCursor(
                cursorImage,
                new Point(cursorImage.getWidth() / 2, (cursorImage.getHeight() / 4) - 5),
                "Custom Cursor");
        setCursor(customCursor);
    }
}
