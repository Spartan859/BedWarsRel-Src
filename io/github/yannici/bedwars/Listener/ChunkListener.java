package io.github.yannici.bedwars.Listener;

import io.github.yannici.bedwars.Game.Game;
import io.github.yannici.bedwars.Game.GameManager;
import io.github.yannici.bedwars.Game.GameState;
import io.github.yannici.bedwars.Main;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

public class ChunkListener
  implements Listener
{
  @EventHandler
  public void onUnload(ChunkUnloadEvent paramChunkUnloadEvent)
  {
    Game localGame = Main.getInstance().getGameManager().getGameByChunkLocation(paramChunkUnloadEvent.getChunk().getX(), paramChunkUnloadEvent.getChunk().getZ());
    if (localGame == null) {
      return;
    }
    if (localGame.getState() != GameState.RUNNING) {
      return;
    }
    paramChunkUnloadEvent.setCancelled(true);
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Listener.ChunkListener
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */