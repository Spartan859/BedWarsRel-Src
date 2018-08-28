package io.github.yannici.bedwars.Shop.Specials;

import io.github.yannici.bedwars.Events.BedwarsGameStartEvent;
import io.github.yannici.bedwars.Game.Game;
import io.github.yannici.bedwars.Game.GameManager;
import io.github.yannici.bedwars.Game.GameState;
import io.github.yannici.bedwars.Main;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class TrackerListener
  implements Listener
{
  @EventHandler
  public void onInteract(PlayerInteractEvent paramPlayerInteractEvent)
  {
    Player localPlayer = paramPlayerInteractEvent.getPlayer();
    Game localGame = Main.getInstance().getGameManager().getGameOfPlayer(localPlayer);
    if (localGame == null) {
      return;
    }
    if (localGame.getState() != GameState.RUNNING) {
      return;
    }
    if (localGame.isSpectator(localPlayer)) {
      return;
    }
    Tracker localTracker = new Tracker();
    if (!paramPlayerInteractEvent.getMaterial().equals(localTracker.getItemMaterial())) {
      return;
    }
    if ((paramPlayerInteractEvent.getAction().equals(Action.LEFT_CLICK_AIR)) || (paramPlayerInteractEvent.getAction().equals(Action.LEFT_CLICK_BLOCK))) {
      return;
    }
    localTracker.setPlayer(localPlayer);
    localTracker.setGame(localGame);
    localTracker.trackPlayer();
    paramPlayerInteractEvent.setCancelled(true);
  }
  
  @EventHandler
  public void onStart(BedwarsGameStartEvent paramBedwarsGameStartEvent)
  {
    Game localGame = paramBedwarsGameStartEvent.getGame();
    if (localGame == null) {
      return;
    }
    Tracker localTracker = new Tracker();
    localTracker.setGame(localGame);
    localTracker.createTask();
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Shop.Specials.TrackerListener
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */