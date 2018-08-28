package io.github.yannici.bedwars.Game;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.github.yannici.bedwars.ChatWriter;
import io.github.yannici.bedwars.Events.BedwarsGameEndEvent;
import io.github.yannici.bedwars.Main;
import io.github.yannici.bedwars.Utils;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

public class BungeeGameCycle
  extends GameCycle
{
  public BungeeGameCycle(Game paramGame)
  {
    super(paramGame);
  }
  
  public void onGameStart() {}
  
  private void kickAllPlayers()
  {
    Iterator localIterator1 = getGame().getTeamPlayers().iterator();
    Player localPlayer1;
    while (localIterator1.hasNext())
    {
      localPlayer1 = (Player)localIterator1.next();
      Iterator localIterator2 = getGame().getFreePlayers().iterator();
      while (localIterator2.hasNext())
      {
        Player localPlayer2 = (Player)localIterator2.next();
        localPlayer1.showPlayer(localPlayer2);
      }
      getGame().playerLeave(localPlayer1, false);
    }
    localIterator1 = getGame().getFreePlayersClone().iterator();
    while (localIterator1.hasNext())
    {
      localPlayer1 = (Player)localIterator1.next();
      getGame().playerLeave(localPlayer1, false);
    }
  }
  
  public void onGameEnds()
  {
    if (Main.getInstance().getBooleanConfig("bungeecord.full-restart", true))
    {
      kickAllPlayers();
      getGame().resetRegion();
      new BukkitRunnable()
      {
        public void run()
        {
          if ((Main.getInstance().isSpigot()) && (Main.getInstance().getBooleanConfig("bungeecord.spigot-restart", true))) {
            Main.getInstance().getServer().dispatchCommand(Main.getInstance().getServer().getConsoleSender(), "restart");
          } else {
            Bukkit.shutdown();
          }
        }
      }.runTaskLater(Main.getInstance(), 70L);
    }
    else
    {
      getGame().resetScoreboard();
      kickAllPlayers();
      setEndGameRunning(false);
      Iterator localIterator = getGame().getTeams().values().iterator();
      while (localIterator.hasNext())
      {
        Team localTeam = (Team)localIterator.next();
        localTeam.setInventory(null);
        localTeam.getChests().clear();
      }
      getGame().clearProtections();
      getGame().setState(GameState.WAITING);
      getGame().updateScoreboard();
      getGame().resetRegion();
    }
  }
  
  public void onPlayerLeave(Player paramPlayer)
  {
    if ((paramPlayer.isOnline()) || (paramPlayer.isDead())) {
      bungeeSendToServer(Main.getInstance().getBungeeHub(), paramPlayer, true);
    }
    if ((getGame().getState() == GameState.RUNNING) && (!getGame().isStopping())) {
      checkGameOver();
    }
  }
  
  public void onGameLoaded() {}
  
  public boolean onPlayerJoins(Player paramPlayer)
  {
    final Player localPlayer1 = paramPlayer;
    if ((getGame().isFull()) && (!paramPlayer.hasPermission("bw.vip.joinfull")))
    {
      if ((getGame().getState() != GameState.RUNNING) || (!Main.getInstance().spectationEnabled()))
      {
        bungeeSendToServer(Main.getInstance().getBungeeHub(), localPlayer1, false);
        new BukkitRunnable()
        {
          public void run()
          {
            BungeeGameCycle.this.sendBungeeMessage(localPlayer1, ChatWriter.pluginMessage(ChatColor.RED + Main._l("lobby.gamefull")));
          }
        }.runTaskLater(Main.getInstance(), 60L);
        return false;
      }
    }
    else if ((getGame().isFull()) && (paramPlayer.hasPermission("bw.vip.joinfull"))) {
      if (getGame().getState() == GameState.WAITING)
      {
        List localList = getGame().getNonVipPlayers();
        if (localList.size() == 0)
        {
          bungeeSendToServer(Main.getInstance().getBungeeHub(), localPlayer1, false);
          new BukkitRunnable()
          {
            public void run()
            {
              BungeeGameCycle.this.sendBungeeMessage(localPlayer1, ChatWriter.pluginMessage(ChatColor.RED + Main._l("lobby.gamefullpremium")));
            }
          }.runTaskLater(Main.getInstance(), 60L);
          return false;
        }
        Player localPlayer2 = null;
        if (localList.size() == 1) {
          localPlayer2 = (Player)localList.get(0);
        } else {
          localPlayer2 = (Player)localList.get(Utils.randInt(0, localList.size() - 1));
        }
        final Player localPlayer3 = localPlayer2;
        getGame().playerLeave(localPlayer3, false);
        new BukkitRunnable()
        {
          public void run()
          {
            BungeeGameCycle.this.sendBungeeMessage(localPlayer3, ChatWriter.pluginMessage(ChatColor.RED + Main._l("lobby.kickedbyvip")));
          }
        }.runTaskLater(Main.getInstance(), 60L);
      }
      else if ((getGame().getState() == GameState.RUNNING) && (!Main.getInstance().spectationEnabled()))
      {
        new BukkitRunnable()
        {
          public void run()
          {
            BungeeGameCycle.this.bungeeSendToServer(Main.getInstance().getBungeeHub(), localPlayer1, false);
          }
        }.runTaskLater(Main.getInstance(), 5L);
        new BukkitRunnable()
        {
          public void run()
          {
            BungeeGameCycle.this.sendBungeeMessage(localPlayer1, ChatWriter.pluginMessage(ChatColor.RED + Main._l("lobby.gamefull")));
          }
        }.runTaskLater(Main.getInstance(), 60L);
        return false;
      }
    }
    return true;
  }
  
  public void sendBungeeMessage(Player paramPlayer, String paramString)
  {
    ByteArrayDataOutput localByteArrayDataOutput = ByteStreams.newDataOutput();
    localByteArrayDataOutput.writeUTF("Message");
    localByteArrayDataOutput.writeUTF(paramPlayer.getName());
    localByteArrayDataOutput.writeUTF(paramString);
    paramPlayer.sendPluginMessage(Main.getInstance(), "BungeeCord", localByteArrayDataOutput.toByteArray());
  }
  
  public void bungeeSendToServer(final String paramString, final Player paramPlayer, boolean paramBoolean)
  {
    if (paramString == null)
    {
      paramPlayer.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + Main._l("errors.bungeenoserver")));
      return;
    }
    new BukkitRunnable()
    {
      public void run()
      {
        ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream localDataOutputStream = new DataOutputStream(localByteArrayOutputStream);
        try
        {
          localDataOutputStream.writeUTF("Connect");
          localDataOutputStream.writeUTF(paramString);
        }
        catch (Exception localException)
        {
          localException.printStackTrace();
          return;
        }
        if (localByteArrayOutputStream != null) {
          paramPlayer.sendPluginMessage(Main.getInstance(), "BungeeCord", localByteArrayOutputStream.toByteArray());
        }
      }
    }.runTaskLater(Main.getInstance(), paramBoolean ? 0L : 20L);
  }
  
  public void onGameOver(GameOverTask paramGameOverTask)
  {
    Object localObject;
    if (Main.getInstance().getBooleanConfig("bungeecord.endgame-in-lobby", true))
    {
      localObject = new ArrayList();
      final Game localGame = getGame();
      ((ArrayList)localObject).addAll(getGame().getTeamPlayers());
      ((ArrayList)localObject).addAll(getGame().getFreePlayers());
      Iterator localIterator = ((ArrayList)localObject).iterator();
      while (localIterator.hasNext())
      {
        Player localPlayer = (Player)localIterator.next();
        if (!localPlayer.getWorld().equals(getGame().getLobby().getWorld()))
        {
          localGame.getPlayerSettings(localPlayer).setTeleporting(true);
          localPlayer.teleport(getGame().getLobby());
          localGame.getPlayerStorage(localPlayer).clean();
        }
      }
      new BukkitRunnable()
      {
        public void run()
        {
          Iterator localIterator = this.val$players.iterator();
          while (localIterator.hasNext())
          {
            Player localPlayer = (Player)localIterator.next();
            localGame.setPlayerGameMode(localPlayer);
            localGame.setPlayerVisibility(localPlayer);
            ItemStack localItemStack = new ItemStack(Material.SLIME_BALL, 1);
            ItemMeta localItemMeta = localItemStack.getItemMeta();
            localItemMeta.setDisplayName(Main._l("lobby.leavegame"));
            localItemStack.setItemMeta(localItemMeta);
            localPlayer.getInventory().setItem(8, localItemStack);
            localPlayer.updateInventory();
          }
        }
      }.runTaskLater(Main.getInstance(), 20L);
    }
    if ((paramGameOverTask.getCounter() == paramGameOverTask.getStartCount()) && (paramGameOverTask.getWinner() != null)) {
      getGame().broadcast(ChatColor.GOLD + Main._l("ingame.teamwon", ImmutableMap.of("team", new StringBuilder().append(paramGameOverTask.getWinner().getDisplayName()).append(ChatColor.GOLD).toString())));
    } else if ((paramGameOverTask.getCounter() == paramGameOverTask.getStartCount()) && (paramGameOverTask.getWinner() == null)) {
      getGame().broadcast(ChatColor.GOLD + Main._l("ingame.draw"));
    }
    if (paramGameOverTask.getCounter() == 0)
    {
      localObject = new BedwarsGameEndEvent(getGame());
      Main.getInstance().getServer().getPluginManager().callEvent((Event)localObject);
      onGameEnds();
      paramGameOverTask.cancel();
    }
    else if ((paramGameOverTask.getCounter() == paramGameOverTask.getStartCount()) || (paramGameOverTask.getCounter() % 10 == 0) || ((paramGameOverTask.getCounter() <= 5) && (paramGameOverTask.getCounter() > 0)))
    {
      getGame().broadcast(ChatColor.AQUA + Main._l("ingame.serverrestart", ImmutableMap.of("sec", new StringBuilder().append(ChatColor.YELLOW.toString()).append(paramGameOverTask.getCounter()).append(ChatColor.AQUA).toString())));
    }
    paramGameOverTask.decCounter();
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Game.BungeeGameCycle
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */