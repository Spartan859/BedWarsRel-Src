package io.github.yannici.bedwars.Events;

import io.github.yannici.bedwars.Game.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BedwarsPlayerJoinEvent
  extends Event
  implements Cancellable
{
  private static final HandlerList handlers = new HandlerList();
  private Game game = null;
  private Player player = null;
  private boolean cancelled = false;
  
  public BedwarsPlayerJoinEvent(Game paramGame, Player paramPlayer)
  {
    this.game = paramGame;
    this.player = paramPlayer;
  }
  
  public HandlerList getHandlers()
  {
    return handlers;
  }
  
  public static HandlerList getHandlerList()
  {
    return handlers;
  }
  
  public Game getGame()
  {
    return this.game;
  }
  
  public Player getPlayer()
  {
    return this.player;
  }
  
  public boolean isCancelled()
  {
    return this.cancelled;
  }
  
  public void setCancelled(boolean paramBoolean)
  {
    this.cancelled = paramBoolean;
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Events.BedwarsPlayerJoinEvent
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */