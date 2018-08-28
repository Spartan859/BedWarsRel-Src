package io.github.yannici.bedwars.Listener;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Hanging;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;

public class HangingListener
  extends BaseListener
{
  @EventHandler(priority=EventPriority.HIGHEST)
  public void onHangingBreak(HangingBreakEvent paramHangingBreakEvent)
  {
    Hanging localHanging = paramHangingBreakEvent.getEntity();
    if (paramHangingBreakEvent.getCause().equals(HangingBreakEvent.RemoveCause.OBSTRUCTION))
    {
      localHanging.getLocation().getBlock().breakNaturally();
      paramHangingBreakEvent.setCancelled(true);
    }
    else if (paramHangingBreakEvent.getCause().equals(HangingBreakEvent.RemoveCause.EXPLOSION))
    {
      paramHangingBreakEvent.setCancelled(true);
    }
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Listener.HangingListener
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */