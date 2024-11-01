package page.controls.multiplayer;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import models.Bullet;
import models.ClientObj;
import models.Player;
import models.Zombie.Info;

public interface GameContentListener extends Serializable {
    void onPlayerAction(Player player);
    void onPlayerTakeDamage(List<ClientObj> clientObj);
    void onShootBullet(CopyOnWriteArrayList<Bullet> bullets);
    void onZombieUpdate(Info updatedZombie);

}
