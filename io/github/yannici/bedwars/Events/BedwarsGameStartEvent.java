package io.github.yannici.bedwars.Events;

import io.github.yannici.bedwars.Game.Game;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BedwarsGameStartEvent
  extends Event
  implements Cancellable
{
  private static final HandlerList handlers = new HandlerList();
  private boolean cancelled = false;
  private Game game = null;
  
  public BedwarsGameStartEvent(Game paramGame)
  {
    this.game = paramGame;
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
 * Qualified Name:     io.github.yannici.bedwars.Events.BedwarsGameStartEvent
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */