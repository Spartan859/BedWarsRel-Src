package io.github.yannici.bedwars.Game;

import com.google.common.collect.ImmutableMap;
import io.github.yannici.bedwars.Events.BedwarsGameOverEvent;
import io.github.yannici.bedwars.Events.BedwarsPlayerKilledEvent;
import io.github.yannici.bedwars.Main;
import io.github.yannici.bedwars.Shop.Specials.RescuePlatform;
import io.github.yannici.bedwars.Shop.Specials.SpecialItem;
import io.github.yannici.bedwars.Statistics.PlayerStatistic;
import io.github.yannici.bedwars.Statistics.PlayerStatisticManager;
import io.github.yannici.bedwars.Utils;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class GameCycle
{
  private Game game = null;
  private boolean endGameRunning = false;
  
  public GameCycle(Game paramGame)
  {
    this.game = paramGame;
  }
  
  public Game getGame()
  {
    return this.game;
  }
  
  public abstract void onGameStart();
  
  public abstract void onGameEnds();
  
  public abstract void onPlayerLeave(Player paramPlayer);
  
  public abstract void onGameLoaded();
  
  public abstract boolean onPlayerJoins(Player paramPlayer);
  
  public abstract void onGameOver(GameOverTask paramGameOverTask);
  
  private boolean storeRecords(boolean paramBoolean, Team paramTeam)
  {
    int i = getGame().getLength() - getGame().getTimeLeft();
    int j = 0;
    if (i <= getGame().getRecord())
    {
      Iterator localIterator = getGame().getPlayingTeams().iterator();
      Object localObject;
      while (localIterator.hasNext())
      {
        localObject = (Team)localIterator.next();
        if (((Team)localObject).isDead(getGame()))
        {
          j = 1;
          break;
        }
      }
      if (j == 0)
      {
        getGame().broadcast(Main._l("ingame.record-nobeddestroy"));
        return false;
      }
      if (paramBoolean)
      {
        if (i < getGame().getRecord()) {
          getGame().getRecordHolders().clear();
        }
        localIterator = paramTeam.getPlayers().iterator();
        while (localIterator.hasNext())
        {
          localObject = (Player)localIterator.next();
          getGame().addRecordHolder(((Player)localObject).getName());
        }
      }
      getGame().setRecord(i);
      getGame().saveRecord();
      getGame().broadcast(Main._l("ingame.newrecord", ImmutableMap.of("record", getGame().getFormattedRecord(), "team", paramTeam.getChatColor() + paramTeam.getDisplayName())));
      return true;
    }
    return false;
  }
  
  private String winTitleReplace(String paramString, Team paramTeam)
  {
    int i = getGame().getLength() - getGame().getTimeLeft();
    String str = Utils.getFormattedTime(i);
    paramString = paramString.replace("$time$", str);
    if (paramTeam == null) {
      return paramString;
    }
    paramString = paramString.replace("$team$", paramTeam.getChatColor() + paramTeam.getDisplayName());
    return paramString;
  }
  
  private void runGameOver(Team paramTeam)
  {
    BedwarsGameOverEvent localBedwarsGameOverEvent = new BedwarsGameOverEvent(getGame(), paramTeam);
    Main.getInstance().getServer().getPluginManager().callEvent(localBedwarsGameOverEvent);
    if (localBedwarsGameOverEvent.isCancelled()) {
      return;
    }
    getGame().stopWorkers();
    setEndGameRunning(true);
    boolean bool1 = Main.getInstance().getBooleanConfig("store-game-records", true);
    boolean bool2 = Main.getInstance().getBooleanConfig("store-game-records-holder", true);
    boolean bool3 = false;
    if ((bool1) && (paramTeam != null)) {
      bool3 = storeRecords(bool2, paramTeam);
    }
    int i = Main.getInstance().getConfig().getInt("gameoverdelay");
    String str1 = winTitleReplace(Main._l("ingame.title.win-title"), paramTeam);
    String str2 = winTitleReplace(Main._l("ingame.title.win-subtitle"), paramTeam);
    boolean bool4 = Utils.isSupportingTitles();
    if ((Main.getInstance().statisticsEnabled()) || (Main.getInstance().getBooleanConfig("rewards.enabled", false)) || ((Main.getInstance().getBooleanConfig("titles.win.enabled", true)) && (bool4) && ((!str1.equals("")) || (!str2.equals("")))))
    {
      Player localPlayer;
      Object localObject2;
      if (paramTeam != null)
      {
        localObject1 = paramTeam.getPlayers().iterator();
        while (((Iterator)localObject1).hasNext())
        {
          localPlayer = (Player)((Iterator)localObject1).next();
          if ((Main.getInstance().getBooleanConfig("titles.win.enabled", true)) && (bool4) && ((!str1.equals("")) || (!str2.equals("")))) {
            try
            {
              Class localClass = Class.forName("io.github.yannici.bedwars.Com." + Main.getInstance().getCurrentVersion() + ".Title");
              double d1;
              double d2;
              double d3;
              Method localMethod;
              if (!str1.equals(""))
              {
                d1 = Main.getInstance().getConfig().getDouble("titles.win.title-fade-in", 1.5D);
                d2 = Main.getInstance().getConfig().getDouble("titles.win.title-stay", 5.0D);
                d3 = Main.getInstance().getConfig().getDouble("titles.win.title-fade-out", 2.0D);
                localMethod = localClass.getDeclaredMethod("showTitle", new Class[] { Player.class, String.class, Double.TYPE, Double.TYPE, Double.TYPE });
                localMethod.invoke(null, new Object[] { localPlayer, str1, Double.valueOf(d1), Double.valueOf(d2), Double.valueOf(d3) });
              }
              if (!str2.equals(""))
              {
                d1 = Main.getInstance().getConfig().getDouble("titles.win.subtitle-fade-in", 1.5D);
                d2 = Main.getInstance().getConfig().getDouble("titles.win.subtitle-stay", 5.0D);
                d3 = Main.getInstance().getConfig().getDouble("titles.win.subtitle-fade-out", 2.0D);
                localMethod = localClass.getDeclaredMethod("showSubTitle", new Class[] { Player.class, String.class, Double.TYPE, Double.TYPE, Double.TYPE });
                localMethod.invoke(null, new Object[] { localPlayer, str2, Double.valueOf(d1), Double.valueOf(d2), Double.valueOf(d3) });
              }
            }
            catch (Exception localException)
            {
              localException.printStackTrace();
            }
          }
          if (Main.getInstance().getBooleanConfig("rewards.enabled", false))
          {
            localObject2 = new ArrayList();
            localObject2 = Main.getInstance().getConfig().getList("rewards.player-win");
            Main.getInstance().dispatchRewardCommands((List)localObject2, getRewardPlaceholders(localPlayer));
          }
          if (Main.getInstance().statisticsEnabled())
          {
            localObject2 = Main.getInstance().getPlayerStatisticManager().getStatistic(localPlayer);
            ((PlayerStatistic)localObject2).setWins(((PlayerStatistic)localObject2).getWins() + 1);
            ((PlayerStatistic)localObject2).addCurrentScore(Main.getInstance().getIntConfig("statistics.scores.win", 50));
            if (bool3) {
              ((PlayerStatistic)localObject2).addCurrentScore(Main.getInstance().getIntConfig("statistics.scores.record", 100));
            }
          }
        }
      }
      localObject1 = this.game.getPlayers().iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localPlayer = (Player)((Iterator)localObject1).next();
        if (!this.game.isSpectator(localPlayer)) {
          if (Main.getInstance().getBooleanConfig("rewards.enabled", false))
          {
            localObject2 = new ArrayList();
            localObject2 = Main.getInstance().getConfig().getList("rewards.player-end-game");
            Main.getInstance().dispatchRewardCommands((List)localObject2, getRewardPlaceholders(localPlayer));
          }
        }
      }
    }
    getGame().getPlayingTeams().clear();
    Object localObject1 = new GameOverTask(this, i, paramTeam);
    ((GameOverTask)localObject1).runTaskTimer(Main.getInstance(), 0L, 20L);
  }
  
  private Map<String, String> getRewardPlaceholders(Player paramPlayer)
  {
    HashMap localHashMap = new HashMap();
    localHashMap.put("{player}", paramPlayer.getName());
    if (Main.getInstance().statisticsEnabled())
    {
      PlayerStatistic localPlayerStatistic = Main.getInstance().getPlayerStatisticManager().getStatistic(paramPlayer);
      localHashMap.put("{score}", String.valueOf(localPlayerStatistic.getCurrentScore()));
    }
    return localHashMap;
  }
  
  public void checkGameOver()
  {
    if (!Main.getInstance().isEnabled()) {
      return;
    }
    Team localTeam = getGame().isOver();
    if (localTeam != null)
    {
      if (!isEndGameRunning()) {
        runGameOver(localTeam);
      }
    }
    else if (((getGame().getTeamPlayers().size() == 0) || (getGame().isOverSet())) && (!isEndGameRunning())) {
      runGameOver(null);
    }
  }
  
  public void onPlayerRespawn(PlayerRespawnEvent paramPlayerRespawnEvent, Player paramPlayer)
  {
    Team localTeam = getGame().getPlayerTeam(paramPlayer);
    getGame().setPlayerDamager(paramPlayer, null);
    Object localObject;
    if (localTeam == null)
    {
      if (getGame().isSpectator(paramPlayer))
      {
        localObject = getGame().getTeams().values();
        paramPlayerRespawnEvent.setRespawnLocation(((Team)localObject.toArray()[Utils.randInt(0, localObject.size() - 1)]).getSpawnLocation());
      }
      return;
    }
    if (localTeam.isDead(getGame()))
    {
      localObject = getGame().getPlayerStorage(paramPlayer);
      if (Main.getInstance().statisticsEnabled())
      {
        PlayerStatistic localPlayerStatistic = Main.getInstance().getPlayerStatisticManager().getStatistic(paramPlayer);
        localPlayerStatistic.setLoses(localPlayerStatistic.getLoses() + 1);
      }
      if (Main.getInstance().spectationEnabled())
      {
        if ((localObject != null) && (((PlayerStorage)localObject).getLeft() != null)) {
          paramPlayerRespawnEvent.setRespawnLocation(localTeam.getSpawnLocation());
        }
        getGame().toSpectator(paramPlayer);
      }
      else
      {
        if ((this.game.getCycle() instanceof BungeeGameCycle))
        {
          getGame().playerLeave(paramPlayer, false);
          return;
        }
        if (!Main.getInstance().toMainLobby())
        {
          if ((localObject != null) && (((PlayerStorage)localObject).getLeft() != null)) {
            paramPlayerRespawnEvent.setRespawnLocation(((PlayerStorage)localObject).getLeft());
          }
        }
        else if (getGame().getMainLobby() != null) {
          paramPlayerRespawnEvent.setRespawnLocation(getGame().getMainLobby());
        } else if ((localObject != null) && (((PlayerStorage)localObject).getLeft() != null)) {
          paramPlayerRespawnEvent.setRespawnLocation(((PlayerStorage)localObject).getLeft());
        }
        getGame().playerLeave(paramPlayer, false);
      }
    }
    else
    {
      if (Main.getInstance().getRespawnProtectionTime().intValue() > 0)
      {
        localObject = getGame().addProtection(paramPlayer);
        ((RespawnProtectionRunnable)localObject).runProtection();
      }
      paramPlayerRespawnEvent.setRespawnLocation(localTeam.getSpawnLocation());
    }
    new BukkitRunnable()
    {
      public void run()
      {
        GameCycle.this.checkGameOver();
      }
    }.runTaskLater(Main.getInstance(), 20L);
  }
  
  public void onPlayerDies(Player paramPlayer1, Player paramPlayer2)
  {
    if (isEndGameRunning()) {
      return;
    }
    BedwarsPlayerKilledEvent localBedwarsPlayerKilledEvent = new BedwarsPlayerKilledEvent(getGame(), paramPlayer1, paramPlayer2);
    Main.getInstance().getServer().getPluginManager().callEvent(localBedwarsPlayerKilledEvent);
    PlayerStatistic localPlayerStatistic1 = null;
    PlayerStatistic localPlayerStatistic2 = null;
    Iterator localIterator = this.game.getSpecialItems().iterator();
    while (localIterator.hasNext())
    {
      localObject1 = (SpecialItem)localIterator.next();
      if ((localObject1 instanceof RescuePlatform))
      {
        RescuePlatform localRescuePlatform = (RescuePlatform)localObject1;
        if (localRescuePlatform.getPlayer().equals(paramPlayer1)) {
          localIterator.remove();
        }
      }
    }
    Object localObject1 = getGame().getPlayerTeam(paramPlayer1);
    if (Main.getInstance().statisticsEnabled())
    {
      localPlayerStatistic1 = Main.getInstance().getPlayerStatisticManager().getStatistic(paramPlayer1);
      boolean bool1 = Main.getInstance().getBooleanConfig("statistics.bed-destroyed-kills", false);
      boolean bool2 = ((Team)localObject1).isDead(getGame());
      if (((bool1) && (bool2)) || (!bool1))
      {
        localPlayerStatistic1.setDeaths(localPlayerStatistic1.getDeaths() + 1);
        localPlayerStatistic1.addCurrentScore(Main.getInstance().getIntConfig("statistics.scores.die", 0));
      }
      if ((paramPlayer2 != null) && (((bool1) && (bool2)) || (!bool1)))
      {
        localPlayerStatistic2 = Main.getInstance().getPlayerStatisticManager().getStatistic(paramPlayer2);
        if (localPlayerStatistic2 != null)
        {
          localPlayerStatistic2.setKills(localPlayerStatistic2.getKills() + 1);
          localPlayerStatistic2.addCurrentScore(Main.getInstance().getIntConfig("statistics.scores.kill", 10));
        }
      }
      if ((Main.getInstance().getBooleanConfig("rewards.enabled", false)) && (paramPlayer2 != null) && (((bool1) && (bool2)) || (!bool1)))
      {
        localObject2 = Main.getInstance().getConfig().getStringList("rewards.player-kill");
        Main.getInstance().dispatchRewardCommands((List)localObject2, ImmutableMap.of("{player}", paramPlayer2.getName(), "{score}", String.valueOf(Main.getInstance().getIntConfig("statistics.scores.kill", 10))));
      }
    }
    if (paramPlayer2 == null)
    {
      getGame().broadcast(ChatColor.GOLD + Main._l("ingame.player.died", ImmutableMap.of("player", Game.getPlayerWithTeamString(paramPlayer1, (Team)localObject1, ChatColor.GOLD))));
      sendTeamDeadMessage((Team)localObject1);
      checkGameOver();
      return;
    }
    Team localTeam = getGame().getPlayerTeam(paramPlayer2);
    if (localTeam == null)
    {
      getGame().broadcast(ChatColor.GOLD + Main._l("ingame.player.died", ImmutableMap.of("player", Game.getPlayerWithTeamString(paramPlayer1, (Team)localObject1, ChatColor.GOLD))));
      sendTeamDeadMessage((Team)localObject1);
      checkGameOver();
      return;
    }
    String str = "";
    Object localObject2 = new DecimalFormat("#.#");
    double d = paramPlayer2.getHealth() / paramPlayer2.getMaxHealth() * paramPlayer2.getHealthScale();
    if (Main.getInstance().getBooleanConfig("hearts-on-death", true)) {
      str = "[" + ChatColor.RED + "❤" + ((DecimalFormat)localObject2).format(d) + ChatColor.GOLD + "]";
    }
    getGame().broadcast(ChatColor.GOLD + Main._l("ingame.player.killed", ImmutableMap.of("killer", Game.getPlayerWithTeamString(paramPlayer2, localTeam, ChatColor.GOLD, str), "player", Game.getPlayerWithTeamString(paramPlayer1, (Team)localObject1, ChatColor.GOLD))));
    sendTeamDeadMessage((Team)localObject1);
    checkGameOver();
  }
  
  private void sendTeamDeadMessage(Team paramTeam)
  {
    if ((paramTeam.getPlayers().size() == 1) && (paramTeam.isDead(getGame()))) {
      getGame().broadcast(ChatColor.RED + Main._l("ingame.team-dead", ImmutableMap.of("team", new StringBuilder().append(paramTeam.getChatColor()).append(paramTeam.getDisplayName()).append(ChatColor.RED).toString())));
    }
  }
  
  public void setEndGameRunning(boolean paramBoolean)
  {
    this.endGameRunning = paramBoolean;
  }
  
  public boolean isEndGameRunning()
  {
    return this.endGameRunning;
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Game.GameCycle
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */