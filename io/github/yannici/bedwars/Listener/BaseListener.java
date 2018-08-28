package io.github.yannici.bedwars.Listener;

import io.github.yannici.bedwars.Main;
import org.bukkit.Server;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

public abstract class BaseListener
  implements Listener
{
  public BaseListener()
  {
    registerEvents();
  }
  
  private void registerEvents()
  {
    Main.getInstance().getServer().getPluginManager().registerEvents(this, Main.getInstance());
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Listener.BaseListener
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */