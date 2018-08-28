package io.github.yannici.bedwars.Shop.Specials;

import com.google.common.collect.ImmutableMap;
import io.github.yannici.bedwars.ChatWriter;
import io.github.yannici.bedwars.Game.Game;
import io.github.yannici.bedwars.Game.Team;
import io.github.yannici.bedwars.Main;
import java.util.ArrayList;
import java.util.Iterator;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Tracker
  extends SpecialItem
{
  private Player player = null;
  private Game game = null;
  private ItemStack stack = null;
  
  public Material getItemMaterial()
  {
    return Material.COMPASS;
  }
  
  public Material getActivatedMaterial()
  {
    return null;
  }
  
  public void trackPlayer()
  {
    Player localPlayer = findTargetPlayer(this.player);
    if (localPlayer == null)
    {
      this.player.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + Main._l("ingame.specials.tracker.no-target-found")));
      this.player.setCompassTarget(this.game.getPlayerTeam(this.player).getSpawnLocation());
      return;
    }
    int i = (int)this.player.getLocation().distance(localPlayer.getLocation());
    this.player.sendMessage(ChatWriter.pluginMessage(Main._l("ingame.specials.tracker.target-found", ImmutableMap.of("player", localPlayer.getDisplayName(), "blocks", String.valueOf(i)))));
  }
  
  public void createTask()
  {
    final Game localGame = this.game;
    BukkitTask localBukkitTask = new BukkitRunnable()
    {
      public void run()
      {
        Iterator localIterator = localGame.getTeamPlayers().iterator();
        while (localIterator.hasNext())
        {
          Player localPlayer1 = (Player)localIterator.next();
          if (localPlayer1.getInventory().contains(Tracker.this.getItemMaterial()))
          {
            Player localPlayer2 = Tracker.this.findTargetPlayer(localPlayer1);
            if (localPlayer2 != null) {
              localPlayer1.setCompassTarget(localPlayer2.getLocation());
            }
          }
          else
          {
            localPlayer1.setCompassTarget(localGame.getPlayerTeam(localPlayer1).getSpawnLocation());
          }
        }
      }
    }.runTaskTimer(Main.getInstance(), 20L, 20L);
    this.game.addRunningTask(localBukkitTask);
  }
  
  private Player findTargetPlayer(Player paramPlayer)
  {
    Object localObject = null;
    double d1 = 1.7976931348623157E+308D;
    Team localTeam = this.game.getPlayerTeam(paramPlayer);
    ArrayList localArrayList = new ArrayList();
    localArrayList.addAll(this.game.getTeamPlayers());
    localArrayList.removeAll(localTeam.getPlayers());
    Iterator localIterator = localArrayList.iterator();
    while (localIterator.hasNext())
    {
      Player localPlayer = (Player)localIterator.next();
      double d2 = paramPlayer.getLocation().distance(localPlayer.getLocation());
      if (d2 < d1)
      {
        localObject = localPlayer;
        d1 = d2;
      }
    }
    return localObject;
  }
  
  public Player getPlayer()
  {
    return this.player;
  }
  
  public void setPlayer(Player paramPlayer)
  {
    this.player = paramPlayer;
  }
  
  public void setGame(Game paramGame)
  {
    this.game = paramGame;
  }
  
  public ItemStack getStack()
  {
    return this.stack;
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Shop.Specials.Tracker
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */