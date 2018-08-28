package io.github.yannici.bedwars.Events;

import io.github.yannici.bedwars.Game.Game;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BedwarsGameEndEvent
  extends Event
{
  private static final HandlerList handlers = new HandlerList();
  private Game game = null;
  
  public BedwarsGameEndEvent(Game paramGame)
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
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Events.BedwarsGameEndEvent
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */