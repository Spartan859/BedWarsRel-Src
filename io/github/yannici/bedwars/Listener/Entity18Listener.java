package io.github.yannici.bedwars.Listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class Entity18Listener
  extends BaseListener
{
  private EntityListener entityListener = null;
  
  public Entity18Listener(EntityListener paramEntityListener)
  {
    this.entityListener = paramEntityListener;
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void onInteractAtEntity(PlayerInteractAtEntityEvent paramPlayerInteractAtEntityEvent)
  {
    this.entityListener.onInteractEntity(paramPlayerInteractAtEntityEvent);
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Listener.Entity18Listener
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */