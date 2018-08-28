package io.github.yannici.bedwars.Events;

import io.github.yannici.bedwars.Game.Game;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BedwarsOpenTeamSelectionEvent
  extends Event
  implements Cancellable
{
  private static final HandlerList handlers = new HandlerList();
  private Player player = null;
  private Game game = null;
  private boolean cancelled = false;
  
  public BedwarsOpenTeamSelectionEvent(Game paramGame, Player paramPlayer)
  {
    this.player = paramPlayer;
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
  
  public CommandSender getPlayer()
  {
    return this.player;
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
 * Qualified Name:     io.github.yannici.bedwars.Events.BedwarsOpenTeamSelectionEvent
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */