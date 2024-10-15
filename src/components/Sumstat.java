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

  public Sumstat(GameContent gameContent,GameCenter gameCenter) {
    this.gamecontent = gameContent;
    this.gamecenter = gameCenter ;

    setLayout(new GridBagLayout());
    setOpaque(true);
    setBackground(Color.decode("#25252525"));
    // setPreferredSize(new Dimension(400,400));

    GridBagConstraints gridConst = new GridBagConstraints();
    gridConst.fill = GridBagConstraints.BOTH;
    gridConst.anchor = GridBagConstraints.CENTER;
    gridConst.insets = new Insets(20, 20, 40, 20);
    gridConst.weightx = 1;
    gridConst.weighty = 0.5;
    gridConst.gridx = 1;
    gridConst.gridy = 0;

    add(new UseText(20, 200, 40, true).createSimpleText("Player", Color.BLACK, Color.white, Font.PLAIN), gridConst);

    gridConst.gridy = 1;
    gridConst.insets = new Insets(0, 20, 0, 20);

    add(new UseText(20, 200, 40, false).createSimpleText("Name : ", Color.BLACK, Color.white, Font.PLAIN), gridConst);
  
    gridConst.gridy = 2;
    gridConst.insets = new Insets(0, 20, 0, 20);

    add(new UseText(20, 200, 40, false).createSimpleText("IP : ", Color.BLACK, Color.white, Font.PLAIN), gridConst);
    
    gridConst.gridy = 3;
    gridConst.insets = new Insets(0, 20, 0, 20);

    add(new UseText(20, 200, 40, false).createSimpleText("Kill : ", Color.BLACK, Color.white, Font.PLAIN), gridConst);
    
    gridConst.gridy = 4;
    gridConst.insets = new Insets(0, 20, 0, 20);

    add(new UseText(20, 200, 40, false).createSimpleText("Rank : ", Color.BLACK, Color.white, Font.PLAIN), gridConst);
    
    gridConst.gridy = 5;
    gridConst.insets = new Insets(0, 20, 0, 20);

    add(new UseText(20, 200, 40, false).createSimpleText("Alive Time : ", Color.BLACK, Color.white, Font.PLAIN), gridConst);
    
    gridConst.gridy = 6;
    gridConst.insets = new Insets(40, 20, 20, 20);

    add(new UseButton().createButtonAndChangePage(null,"Back",Color.WHITE,200,40,"hand",this.gamecontent,() -> this.gamecenter), gridConst);

  }
}
