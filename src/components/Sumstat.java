package components;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Font;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;

import page.controls.GameContent;
import page.home.GameCenter;
import utils.UseButton;
import utils.UseText;

public class Sumstat extends JPanel {

  private GameContent gamecontent;
  private GameCenter gamecenter;

  private boolean isShowBackButton = true;

  public Sumstat(GameContent gameContent, GameCenter gameCenter, boolean isShowBackButton) {
    this.gamecontent = gameContent;
    this.gamecenter = gameCenter;
    this.isShowBackButton = isShowBackButton;

    System.out.println("Create Sum Stat Work!");

    setLayout(new GridBagLayout());
    setOpaque(true);
    setBackground(Color.decode("#25252525"));
    // setPreferredSize(new Dimension(400, 400));

    GridBagConstraints gridConst = new GridBagConstraints();
    gridConst.fill = GridBagConstraints.BOTH;
    gridConst.anchor = GridBagConstraints.CENTER;
    gridConst.insets = new Insets(10, 10, 10, 10);
    gridConst.weightx = 1;
    gridConst.weighty = 1;

    gridConst.gridx = 1;
    gridConst.gridy = 0;
    add(new UseText(24, 250, 40, true).createSimpleText("Player", Color.BLACK, Color.WHITE, Font.BOLD), gridConst);

    JPanel statContain = new JPanel(new GridBagLayout());
    GridBagConstraints statGridConst = new GridBagConstraints();
    statGridConst.fill = GridBagConstraints.HORIZONTAL;
    statGridConst.anchor = GridBagConstraints.WEST;
    statGridConst.insets = new Insets(5, 5, 5, 5);
    statGridConst.weightx = 1;
    statGridConst.weighty = 1;
    statGridConst.gridx = 0;
    
    statContain.add(new UseText(20, 200, 30, false).createSimpleText("Name: Player", Color.BLACK, Color.WHITE, Font.PLAIN), statGridConst);
    statGridConst.gridy = 1;
    statContain.add(new UseText(20, 200, 30, false).createSimpleText("IP: 192.168.0.0", Color.BLACK, Color.WHITE, Font.PLAIN), statGridConst);
    statGridConst.gridy = 2;
    statContain.add(new UseText(20, 200, 30, false).createSimpleText("Kill: 11", Color.BLACK, Color.WHITE, Font.PLAIN), statGridConst);
    statGridConst.gridy = 3;
    statContain.add(new UseText(20, 200, 30, false).createSimpleText("Rank: 1", Color.BLACK, Color.WHITE, Font.PLAIN), statGridConst);
    statGridConst.gridy = 4;
    statContain.add(new UseText(20, 200, 30, false).createSimpleText("Alive Time: 00:05:00", Color.BLACK, Color.WHITE, Font.PLAIN), statGridConst);
    
    gridConst.gridy = 1;
    // gridConst.insets = new Insets(0, 20, 0, 20);
    add(statContain, gridConst);

    gridConst.gridy = 2;
    gridConst.insets = new Insets(10, 10, 10, 10);

    if (isShowBackButton) {
      add(new UseButton(24).createButtonAndChangePage("", "Back", Color.WHITE, 200, 40, "hand", this.gamecontent, () -> this.gamecenter), gridConst);

    }
  }
}