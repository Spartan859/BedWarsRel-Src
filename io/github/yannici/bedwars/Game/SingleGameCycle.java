package io.github.yannici.bedwars.Game;

import com.google.common.collect.ImmutableMap;
import io.github.yannici.bedwars.ChatWriter;
import io.github.yannici.bedwars.Events.BedwarsGameEndEvent;
import io.github.yannici.bedwars.HolographicDisplaysInteraction;
import io.github.yannici.bedwars.Main;
import io.github.yannici.bedwars.Statistics.PlayerStatistic;
import io.github.yannici.bedwars.Statistics.PlayerStatisticManager;
import io.github.yannici.bedwars.Utils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public class SingleGameCycle
  extends GameCycle
{
  public SingleGameCycle(Game paramGame)
  {
    super(paramGame);
  }
  
  public void onGameStart()
  {
    getGame().resetRegion();
  }
  
  public void onGameEnds()
  {
    getGame().resetScoreboard();
    Object localObject1 = getGame().getTeamPlayers().iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (Player)((Iterator)localObject1).next();
      kickPlayer((Player)localObject2, false);
    }
    localObject1 = new ArrayList(getGame().getFreePlayers());
    Object localObject2 = ((List)localObject1).iterator();
    Object localObject3;
    while (((Iterator)localObject2).hasNext())
    {
      localObject3 = (Player)((Iterator)localObject2).next();
      kickPlayer((Player)localObject3, true);
    }
    setEndGameRunning(false);
    localObject2 = getGame().getTeams().values().iterator();
    while (((Iterator)localObject2).hasNext())
    {
      localObject3 = (Team)((Iterator)localObject2).next();
      ((Team)localObject3).setInventory(null);
      ((Team)localObject3).getChests().clear();
    }
    getGame().clearProtections();
    localObject2 = Main.getInstance().getLobbyCountdownRule();
    if ((((GameLobbyCountdownRule)localObject2).isRuleMet(getGame())) && (getGame().getLobbyCountdown() == null))
    {
      localObject3 = new GameLobbyCountdown(getGame());
      ((GameLobbyCountdown)localObject3).setRule((GameLobbyCountdownRule)localObject2);
      ((GameLobbyCountdown)localObject3).runTaskTimer(Main.getInstance(), 20L, 20L);
      getGame().setLobbyCountdown((GameLobbyCountdown)localObject3);
    }
    getGame().setState(GameState.WAITING);
    getGame().updateScoreboard();
  }
  
  private void kickPlayer(Player paramPlayer, boolean paramBoolean)
  {
    Object localObject = getGame().getFreePlayers().iterator();
    while (((Iterator)localObject).hasNext())
    {
      Player localPlayer = (Player)((Iterator)localObject).next();
      paramPlayer.showPlayer(localPlayer);
    }
    if ((paramBoolean) && (getGame().isFull()))
    {
      getGame().playerLeave(paramPlayer, false);
      return;
    }
    if (Main.getInstance().toMainLobby())
    {
      if (Main.getInstance().allPlayersBackToMainLobby())
      {
        getGame().playerLeave(paramPlayer, false);
        return;
      }
      paramPlayer.teleport(getGame().getLobby());
    }
    else
    {
      paramPlayer.teleport(getGame().getLobby());
    }
    if ((Main.getInstance().isHologramsEnabled()) && (Main.getInstance().getHolographicInteractor() != null) && (getGame().getLobby() == paramPlayer.getWorld())) {
      Main.getInstance().getHolographicInteractor().updateHolograms(paramPlayer);
    }
    if (Main.getInstance().statisticsEnabled())
    {
      localObject = Main.getInstance().getPlayerStatisticManager().getStatistic(paramPlayer);
      ((PlayerStatistic)localObject).setScore(((PlayerStatistic)localObject).getScore() + ((PlayerStatistic)localObject).getCurrentScore());
      ((PlayerStatistic)localObject).setCurrentScore(0);
      ((PlayerStatistic)localObject).store();
      if (Main.getInstance().getBooleanConfig("statistics.show-on-game-end", true)) {
        Main.getInstance().getServer().dispatchCommand(paramPlayer, "bw stats");
      }
    }
    getGame().setPlayerDamager(paramPlayer, null);
    localObject = getGame().getPlayerStorage(paramPlayer);
    ((PlayerStorage)localObject).clean();
    ((PlayerStorage)localObject).loadLobbyInventory(getGame());
  }
  
  public void onPlayerLeave(Player paramPlayer)
  {
    PlayerStorage localPlayerStorage = getGame().getPlayerStorage(paramPlayer);
    if (Main.getInstance().toMainLobby())
    {
      if ((Main.getInstance().isHologramsEnabled()) && (Main.getInstance().getHolographicInteractor() != null) && (getGame().getMainLobby().getWorld() == paramPlayer.getWorld())) {
        Main.getInstance().getHolographicInteractor().updateHolograms(paramPlayer);
      }
      paramPlayer.teleport(getGame().getMainLobby());
    }
    else
    {
      if ((Main.getInstance().isHologramsEnabled()) && (Main.getInstance().getHolographicInteractor() != null) && (localPlayerStorage.getLeft() == paramPlayer.getWorld())) {
        Main.getInstance().getHolographicInteractor().updateHolograms(paramPlayer);
      }
      paramPlayer.teleport(localPlayerStorage.getLeft());
    }
    if ((getGame().getState() == GameState.RUNNING) && (!getGame().isStopping()) && (!getGame().isSpectator(paramPlayer))) {
      checkGameOver();
    }
  }
  
  public void onGameLoaded() {}
  
  public boolean onPlayerJoins(Player paramPlayer)
  {
    if ((getGame().isFull()) && (!paramPlayer.hasPermission("bw.vip.joinfull")))
    {
      if ((getGame().getState() != GameState.RUNNING) || (!Main.getInstance().spectationEnabled()))
      {
        paramPlayer.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + Main._l("lobby.gamefull")));
        return false;
      }
    }
    else if ((getGame().isFull()) && (paramPlayer.hasPermission("bw.vip.joinfull"))) {
      if (getGame().getState() == GameState.WAITING)
      {
        List localList = getGame().getNonVipPlayers();
        if (localList.size() == 0)
        {
          paramPlayer.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + Main._l("lobby.gamefullpremium")));
          return false;
        }
        Player localPlayer = null;
        if (localList.size() == 1) {
          localPlayer = (Player)localList.get(0);
        } else {
          localPlayer = (Player)localList.get(Utils.randInt(0, localList.size() - 1));
        }
        localPlayer.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + Main._l("lobby.kickedbyvip")));
        getGame().playerLeave(localPlayer, false);
      }
      else if ((getGame().getState() == GameState.RUNNING) && (!Main.getInstance().spectationEnabled()))
      {
        paramPlayer.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + Main._l("errors.cantjoingame")));
        return false;
      }
    }
    return true;
  }
  
  public void onGameOver(GameOverTask paramGameOverTask)
  {
    if ((paramGameOverTask.getCounter() == paramGameOverTask.getStartCount()) && (paramGameOverTask.getWinner() != null))
    {
      getGame().broadcast(ChatColor.GOLD + Main._l("ingame.teamwon", ImmutableMap.of("team", new StringBuilder().append(paramGameOverTask.getWinner().getDisplayName()).append(ChatColor.GOLD).toString())));
      getGame().stopWorkers();
    }
    else if ((paramGameOverTask.getCounter() == paramGameOverTask.getStartCount()) && (paramGameOverTask.getWinner() == null))
    {
      getGame().broadcast(ChatColor.GOLD + Main._l("ingame.draw"));
    }
    if (paramGameOverTask.getCounter() == 0)
    {
      BedwarsGameEndEvent localBedwarsGameEndEvent = new BedwarsGameEndEvent(getGame());
      Main.getInstance().getServer().getPluginManager().callEvent(localBedwarsGameEndEvent);
      onGameEnds();
      paramGameOverTask.cancel();
    }
    else
    {
      getGame().broadcast(ChatColor.AQUA + Main._l("ingame.backtolobby", ImmutableMap.of("sec", new StringBuilder().append(ChatColor.YELLOW.toString()).append(paramGameOverTask.getCounter()).append(ChatColor.AQUA).toString())));
    }
    paramGameOverTask.decCounter();
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Game.SingleGameCycle
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */