package io.github.yannici.bedwars.Game;

import com.google.common.collect.ImmutableMap;
import io.github.yannici.bedwars.Main;
import io.github.yannici.bedwars.Utils;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class GameLobbyCountdown
  extends BukkitRunnable
{
  private Game game = null;
  private int counter = 0;
  private int lobbytime;
  private int lobbytimeWhenFull;
  private GameLobbyCountdownRule rule = null;
  
  public GameLobbyCountdown(Game paramGame)
  {
    this.game = paramGame;
    this.counter = Main.getInstance().getConfig().getInt("lobbytime");
    this.rule = Main.getInstance().getLobbyCountdownRule();
    this.lobbytime = this.counter;
    this.lobbytimeWhenFull = Main.getInstance().getConfig().getInt("lobbytime-full");
  }
  
  public void setRule(GameLobbyCountdownRule paramGameLobbyCountdownRule)
  {
    this.rule = paramGameLobbyCountdownRule;
  }
  
  public void run()
  {
    ArrayList localArrayList = this.game.getPlayers();
    float f = 1.0F / this.lobbytime;
    if (this.game.getState() != GameState.WAITING)
    {
      this.game.setGameLobbyCountdown(null);
      cancel();
      return;
    }
    if ((this.counter > this.lobbytimeWhenFull) && (this.game.getPlayerAmount() == this.game.getMaxPlayers()))
    {
      this.counter = this.lobbytimeWhenFull;
      this.game.broadcast(ChatColor.YELLOW + Main._l("lobby.countdown", ImmutableMap.of("sec", new StringBuilder().append(ChatColor.RED.toString()).append(this.counter).append(ChatColor.YELLOW).toString())), localArrayList);
    }
    Object localObject1 = localArrayList.iterator();
    Object localObject2;
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (Player)((Iterator)localObject1).next();
      ((Player)localObject2).setLevel(this.counter);
      if (this.counter == this.lobbytime) {
        ((Player)localObject2).setExp(1.0F);
      } else {
        ((Player)localObject2).setExp(1.0F - f * (this.lobbytime - this.counter));
      }
    }
    if (this.counter == this.lobbytime) {
      this.game.broadcast(ChatColor.YELLOW + Main._l("lobby.countdown", ImmutableMap.of("sec", new StringBuilder().append(ChatColor.RED.toString()).append(this.counter).append(ChatColor.YELLOW).toString())), localArrayList);
    }
    if (!this.rule.isRuleMet(this.game))
    {
      this.game.broadcast(ChatColor.RED + Main._l(new StringBuilder().append("lobby.cancelcountdown.").append(this.rule.name()).toString()), localArrayList);
      this.counter = this.lobbytime;
      localObject1 = localArrayList.iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (Player)((Iterator)localObject1).next();
        ((Player)localObject2).setLevel(0);
        ((Player)localObject2).setExp(0.0F);
      }
      this.game.setGameLobbyCountdown(null);
      cancel();
    }
    if ((this.counter <= 10) && (this.counter > 0))
    {
      this.game.broadcast(ChatColor.YELLOW + Main._l("lobby.countdown", ImmutableMap.of("sec", new StringBuilder().append(ChatColor.RED.toString()).append(this.counter).append(ChatColor.YELLOW).toString())), localArrayList);
      localObject1 = null;
      localObject2 = null;
      String str = ChatColor.translateAlternateColorCodes('&', Main.getInstance().getStringConfig("titles.countdown.format", "&3{countdown}"));
      str = str.replace("{countdown}", String.valueOf(this.counter));
      if ((Utils.isSupportingTitles()) && (Main.getInstance().getBooleanConfig("titles.countdown.enabled", true))) {
        try
        {
          localObject1 = Main.getInstance().getVersionRelatedClass("Title");
          localObject2 = ((Class)localObject1).getMethod("showTitle", new Class[] { Player.class, String.class, Double.TYPE, Double.TYPE, Double.TYPE });
        }
        catch (Exception localException1)
        {
          localException1.printStackTrace();
        }
      }
      Iterator localIterator = localArrayList.iterator();
      while (localIterator.hasNext())
      {
        Player localPlayer = (Player)localIterator.next();
        localPlayer.playSound(localPlayer.getLocation(), Sound.CLICK, 20.0F, 20.0F);
        if (localObject1 != null) {
          try
          {
            ((Method)localObject2).invoke(null, new Object[] { localPlayer, str, Double.valueOf(0.2D), Double.valueOf(0.6D), Double.valueOf(0.2D) });
          }
          catch (Exception localException2)
          {
            localException2.printStackTrace();
          }
        }
      }
    }
    if (this.counter == 0)
    {
      this.game.setGameLobbyCountdown(null);
      cancel();
      localObject1 = localArrayList.iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (Player)((Iterator)localObject1).next();
        ((Player)localObject2).playSound(((Player)localObject2).getLocation(), Sound.LEVEL_UP, 20.0F, 20.0F);
        ((Player)localObject2).setLevel(0);
        ((Player)localObject2).setExp(0.0F);
      }
      this.game.start(Main.getInstance().getServer().getConsoleSender());
      return;
    }
    this.counter -= 1;
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Game.GameLobbyCountdown
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */