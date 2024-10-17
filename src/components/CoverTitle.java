package components;

import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.JTextPane;

import java.awt.Font;
import utils.UseText;

public class CoverTitle extends JPanel {

    private String displayName;
    private String displayIp;

    public CoverTitle(
            String displayName,
            String displayIp) {
        this.displayName = displayName;
        this.displayIp = displayIp;

        setOpaque(false);

        // ---------- Name ----------
        JTextPane title_name = new UseText(24, 400, 50, false)
                .createSimpleText("Name: " + this.displayName, null, null, Font.BOLD);
        setBounds(10, 10, 400, 50);
        title_name.setBounds(10, 10, 400, 50);
        add(title_name);
        // title_name.setOpaque(false);

        // ---------- IP ----------
        JTextPane title_ip = new UseText(24, 400, 50, false)
                .createSimpleText("IP: " + this.displayIp, null, null, Font.BOLD);
        title_ip.setBounds(10, 55, 400, 50);
        add(title_ip);
        // title_ip.setOpaque(false);



    }
}
