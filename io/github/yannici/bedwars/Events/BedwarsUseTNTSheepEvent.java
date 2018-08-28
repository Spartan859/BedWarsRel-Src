package io.github.yannici.bedwars.Events;

import io.github.yannici.bedwars.Game.Game;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BedwarsUseTNTSheepEvent
  extends Event
  implements Cancellable
{
  private static final HandlerList handlers = new HandlerList();
  private Player player = null;
  private Game game = null;
  private Location startLocation = null;
  private Player targetPlayer = null;
  private boolean cancelled = false;
  
  public BedwarsUseTNTSheepEvent(Game paramGame, Player paramPlayer1, Player paramPlayer2, Location paramLocation)
  {
    this.player = paramPlayer1;
    this.game = paramGame;
    this.startLocation = paramLocation;
    this.targetPlayer = paramPlayer2;
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
  
  public Location getStartLocation()
  {
    return this.startLocation;
  }
  
  public Player getTargetPlayer()
  {
    return this.targetPlayer;
  }
  
  public void setStartLocation(Location paramLocation)
  {
    this.startLocation = paramLocation;
  }
  
  public void setTargetPlayer(Player paramPlayer)
  {
    this.targetPlayer = paramPlayer;
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
 * Qualified Name:     io.github.yannici.bedwars.Events.BedwarsUseTNTSheepEvent
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */