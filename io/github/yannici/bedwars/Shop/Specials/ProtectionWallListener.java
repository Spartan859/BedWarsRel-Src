package io.github.yannici.bedwars.Shop.Specials;

import io.github.yannici.bedwars.Game.Game;
import io.github.yannici.bedwars.Game.GameManager;
import io.github.yannici.bedwars.Game.GameState;
import io.github.yannici.bedwars.Main;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ProtectionWallListener
  implements Listener
{
  @EventHandler(priority=EventPriority.HIGHEST)
  public void onInteract(PlayerInteractEvent paramPlayerInteractEvent)
  {
    if ((paramPlayerInteractEvent.getAction().equals(Action.LEFT_CLICK_AIR)) || (paramPlayerInteractEvent.getAction().equals(Action.LEFT_CLICK_BLOCK))) {
      return;
    }
    if (paramPlayerInteractEvent.getMaterial() == null) {
      return;
    }
    ProtectionWall localProtectionWall = new ProtectionWall();
    if (paramPlayerInteractEvent.getMaterial() != localProtectionWall.getItemMaterial()) {
      return;
    }
    Game localGame = Main.getInstance().getGameManager().getGameOfPlayer(paramPlayerInteractEvent.getPlayer());
    if (localGame == null) {
      return;
    }
    if (localGame.getState() != GameState.RUNNING) {
      return;
    }
    if (localGame.isSpectator(paramPlayerInteractEvent.getPlayer())) {
      return;
    }
    localProtectionWall.create(paramPlayerInteractEvent.getPlayer(), localGame);
  }
  
  @EventHandler(priority=EventPriority.HIGHEST)
  public void onPlace(BlockPlaceEvent paramBlockPlaceEvent)
  {
    if (paramBlockPlaceEvent.isCancelled()) {
      return;
    }
    ProtectionWall localProtectionWall = new ProtectionWall();
    if (paramBlockPlaceEvent.getBlock().getType() != localProtectionWall.getItemMaterial()) {
      return;
    }
    Game localGame = Main.getInstance().getGameManager().getGameOfPlayer(paramBlockPlaceEvent.getPlayer());
    if (localGame == null) {
      return;
    }
    if (localGame.getState() != GameState.RUNNING) {
      return;
    }
    if (localGame.isSpectator(paramBlockPlaceEvent.getPlayer()))
    {
      paramBlockPlaceEvent.setBuild(false);
      paramBlockPlaceEvent.setCancelled(true);
      return;
    }
    paramBlockPlaceEvent.setBuild(false);
    paramBlockPlaceEvent.setCancelled(true);
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Shop.Specials.ProtectionWallListener
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */