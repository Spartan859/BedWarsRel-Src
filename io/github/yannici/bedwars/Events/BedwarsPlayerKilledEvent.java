package io.github.yannici.bedwars.Events;

import io.github.yannici.bedwars.Game.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BedwarsPlayerKilledEvent
  extends Event
{
  private static final HandlerList handlers = new HandlerList();
  private Player player = null;
  private Player killer = null;
  private Game game = null;
  
  public BedwarsPlayerKilledEvent(Game paramGame, Player paramPlayer1, Player paramPlayer2)
  {
    this.player = paramPlayer1;
    this.killer = paramPlayer2;
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
  
  public Player getPlayer()
  {
    return this.player;
  }
  
  public Player getKiller()
  {
    return this.killer;
  }
  
  public Game getGame()
  {
    return this.game;
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Events.BedwarsPlayerKilledEvent
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */