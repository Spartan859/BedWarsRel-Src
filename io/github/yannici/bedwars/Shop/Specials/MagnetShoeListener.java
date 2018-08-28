package io.github.yannici.bedwars.Shop.Specials;

import io.github.yannici.bedwars.Game.Game;
import io.github.yannici.bedwars.Game.GameManager;
import io.github.yannici.bedwars.Game.GameState;
import io.github.yannici.bedwars.Main;
import io.github.yannici.bedwars.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class MagnetShoeListener
  implements Listener
{
  @EventHandler(priority=EventPriority.HIGHEST)
  public void onDamage(EntityDamageByEntityEvent paramEntityDamageByEntityEvent)
  {
    if (paramEntityDamageByEntityEvent.isCancelled()) {
      return;
    }
    if (!(paramEntityDamageByEntityEvent.getEntity() instanceof Player)) {
      return;
    }
    Game localGame = Main.getInstance().getGameManager().getGameOfPlayer((Player)paramEntityDamageByEntityEvent.getEntity());
    if (localGame == null) {
      return;
    }
    if (localGame.getState() != GameState.RUNNING) {
      return;
    }
    Player localPlayer = (Player)paramEntityDamageByEntityEvent.getEntity();
    ItemStack localItemStack = localPlayer.getInventory().getBoots();
    if (localItemStack == null) {
      return;
    }
    MagnetShoe localMagnetShoe = new MagnetShoe();
    if (localItemStack.getType() != localMagnetShoe.getItemMaterial()) {
      return;
    }
    if (rollKnockbackDice())
    {
      paramEntityDamageByEntityEvent.setCancelled(true);
      localPlayer.damage(paramEntityDamageByEntityEvent.getDamage());
    }
  }
  
  private boolean rollKnockbackDice()
  {
    int i = Main.getInstance().getIntConfig("specials.magnetshoe.probability", 75);
    int j = Utils.randInt(0, 100);
    return j <= i;
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Shop.Specials.MagnetShoeListener
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */