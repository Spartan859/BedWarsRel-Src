package io.github.yannici.bedwars.Listener;

import io.github.yannici.bedwars.Game.Game;
import io.github.yannici.bedwars.Game.GameManager;
import io.github.yannici.bedwars.Main;
import java.util.ArrayList;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

public class Player18Listener
  extends BaseListener
{
  @EventHandler(priority=EventPriority.HIGH)
  public void onPlayerSpawnLocation(PlayerSpawnLocationEvent paramPlayerSpawnLocationEvent)
  {
    if (Main.getInstance().isBungee())
    {
      Player localPlayer = paramPlayerSpawnLocationEvent.getPlayer();
      ArrayList localArrayList = Main.getInstance().getGameManager().getGames();
      if (localArrayList.size() == 0) {
        return;
      }
      Game localGame = (Game)localArrayList.get(0);
      paramPlayerSpawnLocationEvent.setSpawnLocation(localGame.getPlayerTeleportLocation(localPlayer));
    }
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Listener.Player18Listener
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */