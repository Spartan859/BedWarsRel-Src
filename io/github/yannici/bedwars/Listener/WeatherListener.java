package io.github.yannici.bedwars.Listener;

import io.github.yannici.bedwars.Game.Game;
import io.github.yannici.bedwars.Game.GameManager;
import io.github.yannici.bedwars.Game.GameState;
import io.github.yannici.bedwars.Main;
import java.util.Iterator;
import java.util.List;
import org.bukkit.event.EventHandler;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WeatherListener
  extends BaseListener
{
  @EventHandler
  public void onWeatherEvent(WeatherChangeEvent paramWeatherChangeEvent)
  {
    if (paramWeatherChangeEvent.isCancelled()) {
      return;
    }
    List localList = Main.getInstance().getGameManager().getGamesByWorld(paramWeatherChangeEvent.getWorld());
    if (localList.size() == 0) {
      return;
    }
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      Game localGame = (Game)localIterator.next();
      if (localGame.getState() != GameState.STOPPED)
      {
        paramWeatherChangeEvent.setCancelled(true);
        return;
      }
    }
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Listener.WeatherListener
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */