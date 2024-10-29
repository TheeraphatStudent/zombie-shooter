package page.controls.multiplayer;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;

import models.Bullet;
import models.Player;

public interface PlayerBehaviorListener extends Serializable {
    void onPlayerAction(Player player);
    void onShootBullet(CopyOnWriteArrayList<Bullet> bullets);

}
