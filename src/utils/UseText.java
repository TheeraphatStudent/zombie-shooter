package utils;

import javax.swing.JTextPane;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.BorderFactory;

import java.awt.Font;
import java.awt.Color;
import java.awt.Dimension;

public class UseText {
    private int fontSize = 16;
    private int width = 0;
    private int height = 0;
    private UseGlobal global;

    public UseText() {

    }

    public UseText(int _fontSize, int _width, int _height) {
        this.fontSize = _fontSize;
        this.width = _width;
        this.height = _height;

    }

    public JTextPane createSimpleText(String text, Color textColor, Color bg, int font_style) {
        JTextPane textPane = new JTextPane();
        textPane.setPreferredSize(new Dimension(this.width, this.height));
        textPane.setBorder(BorderFactory.createEtchedBorder());

        textPane.setFont(new Font("Arial", font_style, this.fontSize));
        textPane.setText(text);
        textPane.setEditable(false);

        // Text Color
        textPane.setForeground(textColor != null ? textColor : Color.BLACK);

        if (bg == null) {
            textPane.setBackground(new Color(0, 0, 0, 0));
            textPane.setOpaque(false);

        } else {
            textPane.setBackground(bg);
            textPane.setOpaque(true);
        }

        return textPane;
    }

    public JTextField createTextField(String text, Color color, boolean isEditable) {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Arial", Font.PLAIN, this.fontSize));
        textField.setText(text);
        textField.setEditable(isEditable);
        textField.setBorder(
                BorderFactory.createBevelBorder(BevelBorder.RAISED, color.darker(), color.darker().darker()));

        textField.setFocusable(isEditable);

        if (color != null) {
            textField.setBackground(color.brighter());

        }
        return textField;
    }

    public String truncateText(String ref) {

        int length = 8;

        return ref.length() >= length
                ? ref.substring(0, length - 3) + "..."
                : ref;

    }
}
