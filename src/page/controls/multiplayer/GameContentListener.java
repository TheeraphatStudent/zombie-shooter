package page.controls.multiplayer;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;

import models.Bullet;
import models.Player;
import models.Zombie.Info;

public interface GameContentListener extends Serializable {
    void onPlayerAction(Player player);
    void onShootBullet(CopyOnWriteArrayList<Bullet> bullets);
    void onZombieUpdate(CopyOnWriteArrayList<Info> zombies);

}
