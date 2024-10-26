package page.controls.multiplayer;

import java.util.List;

import models.ClientObj;
import page.controls.GameContent;
import page.home.GameCenter;

public class MultiplayerGameContent extends GameContent {

    private List<ClientObj> clientObjs;

    public MultiplayerGameContent(GameCenter gameCenter, ClientObj client, List<ClientObj> clientObjs) {
        super(gameCenter, client);

        this.clientObjs = clientObjs;

    }
}
