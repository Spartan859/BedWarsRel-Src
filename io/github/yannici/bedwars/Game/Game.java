package io.github.yannici.bedwars.Game;

import com.google.common.collect.ImmutableMap;
import io.github.yannici.bedwars.ChatWriter;
import io.github.yannici.bedwars.Events.BedwarsGameStartEvent;
import io.github.yannici.bedwars.Events.BedwarsPlayerJoinEvent;
import io.github.yannici.bedwars.Events.BedwarsPlayerJoinedEvent;
import io.github.yannici.bedwars.Events.BedwarsPlayerLeaveEvent;
import io.github.yannici.bedwars.Events.BedwarsSaveGameEvent;
import io.github.yannici.bedwars.HolographicDisplaysInteraction;
import io.github.yannici.bedwars.Main;
import io.github.yannici.bedwars.Shop.NewItemShop;
import io.github.yannici.bedwars.Shop.Specials.SpecialItem;
import io.github.yannici.bedwars.Statistics.PlayerStatistic;
import io.github.yannici.bedwars.Statistics.PlayerStatisticManager;
import io.github.yannici.bedwars.Utils;
import io.github.yannici.bedwars.Villager.MerchantCategory;
import io.github.yannici.bedwars.Villager.MerchantCategoryComparator;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.Bed;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.util.Vector;

public class Game
{
  private String name = null;
  private List<RessourceSpawner> resSpawner = null;
  private List<BukkitTask> runningTasks = null;
  private GameState state = null;
  private HashMap<String, Team> teams = null;
  private List<Team> playingTeams = null;
  private List<Player> freePlayers = null;
  private int minPlayers = 0;
  private Region region = null;
  private Location lobby = null;
  private HashMap<Player, PlayerStorage> storages = null;
  private Scoreboard scoreboard = null;
  private GameLobbyCountdown glc = null;
  private HashMap<Material, MerchantCategory> itemshop = null;
  private List<MerchantCategory> orderedItemshop = null;
  private GameCycle cycle = null;
  private Location mainLobby = null;
  private HashMap<Location, GameJoinSign> joinSigns = null;
  private int timeLeft = 0;
  private boolean isOver = false;
  private boolean isStopping = false;
  private Location hologramLocation = null;
  private boolean autobalance = false;
  private List<String> recordHolders = null;
  private int record = 0;
  private int length = 0;
  private String builder = null;
  private Map<Player, PlayerSettings> playerSettings = null;
  private List<SpecialItem> currentSpecials = null;
  private int time = 1000;
  private Map<Player, Player> playerDamages = null;
  private Map<Player, RespawnProtectionRunnable> respawnProtected = null;
  private String regionName = null;
  private HashMap<Player, NewItemShop> newItemShops = null;
  private List<Player> useOldItemShop = null;
  private YamlConfiguration config = null;
  private Location loc1 = null;
  private Location loc2 = null;
  private Material targetMaterial = null;
  
  public Game(String paramString)
  {
    this.name = paramString;
    this.runningTasks = new ArrayList();
    this.freePlayers = new ArrayList();
    this.resSpawner = new ArrayList();
    this.teams = new HashMap();
    this.playingTeams = new ArrayList();
    this.storages = new HashMap();
    this.state = GameState.STOPPED;
    this.scoreboard = Main.getInstance().getScoreboardManager().getNewScoreboard();
    this.glc = null;
    this.joinSigns = new HashMap();
    this.timeLeft = Main.getInstance().getMaxLength();
    this.isOver = false;
    this.newItemShops = new HashMap();
    this.useOldItemShop = new ArrayList();
    this.respawnProtected = new HashMap();
    this.playerDamages = new HashMap();
    this.currentSpecials = new ArrayList();
    this.record = Main.getInstance().getMaxLength();
    this.length = Main.getInstance().getMaxLength();
    this.recordHolders = new ArrayList();
    this.playerSettings = new HashMap();
    this.autobalance = Main.getInstance().getBooleanConfig("global-autobalance", false);
    if (Main.getInstance().isBungee()) {
      this.cycle = new BungeeGameCycle(this);
    } else {
      this.cycle = new SingleGameCycle(this);
    }
  }
  
  public static String getPlayerWithTeamString(Player paramPlayer, Team paramTeam, ChatColor paramChatColor)
  {
    if (Main.getInstance().getBooleanConfig("teamname-in-chat", true)) {
      return new StringBuilder().append(paramPlayer.getDisplayName()).append(paramChatColor).append(" (").append(paramTeam.getChatColor()).append(paramTeam.getDisplayName()).append(paramChatColor).append(")").toString();
    }
    return new StringBuilder().append(paramPlayer.getDisplayName()).append(paramChatColor).toString();
  }
  
  public static String getPlayerWithTeamString(Player paramPlayer, Team paramTeam, ChatColor paramChatColor, String paramString)
  {
    if (Main.getInstance().getBooleanConfig("teamname-in-chat", true)) {
      return new StringBuilder().append(paramPlayer.getDisplayName()).append(paramChatColor).append(paramString).append(paramChatColor).append(" (").append(paramTeam.getChatColor()).append(paramTeam.getDisplayName()).append(paramChatColor).append(")").toString();
    }
    return new StringBuilder().append(paramPlayer.getDisplayName()).append(paramChatColor).append(paramString).append(paramChatColor).toString();
  }
  
  public static String bedLostString()
  {
    return "✘";
  }
  
  public static String bedExistString()
  {
    return "✔";
  }
  
  public boolean run(CommandSender paramCommandSender)
  {
    if (this.state != GameState.STOPPED)
    {
      paramCommandSender.sendMessage(ChatWriter.pluginMessage(new StringBuilder().append(ChatColor.RED).append(Main._l("errors.cantstartagain")).toString()));
      return false;
    }
    GameCheckCode localGameCheckCode = checkGame();
    if (localGameCheckCode != GameCheckCode.OK)
    {
      paramCommandSender.sendMessage(ChatWriter.pluginMessage(new StringBuilder().append(ChatColor.RED).append(localGameCheckCode.getCodeMessage()).toString()));
      return false;
    }
    if ((paramCommandSender instanceof Player)) {
      paramCommandSender.sendMessage(ChatWriter.pluginMessage(new StringBuilder().append(ChatColor.GREEN).append(Main._l("success.gamerun")).toString()));
    }
    this.isStopping = false;
    this.state = GameState.WAITING;
    updateSigns();
    return true;
  }
  
  public boolean start(CommandSender paramCommandSender)
  {
    if (this.state != GameState.WAITING)
    {
      paramCommandSender.sendMessage(ChatWriter.pluginMessage(new StringBuilder().append(ChatColor.RED).append(Main._l("errors.startoutofwaiting")).toString()));
      return false;
    }
    BedwarsGameStartEvent localBedwarsGameStartEvent = new BedwarsGameStartEvent(this);
    Main.getInstance().getServer().getPluginManager().callEvent(localBedwarsGameStartEvent);
    if (localBedwarsGameStartEvent.isCancelled()) {
      return false;
    }
    this.isOver = false;
    broadcast(new StringBuilder().append(ChatColor.GREEN).append(Main._l("ingame.gamestarting")).toString());
    loadItemShopCategories();
    this.runningTasks.clear();
    cleanUsersInventory();
    clearProtections();
    moveFreePlayersToTeam();
    makeTeamsReady();
    this.cycle.onGameStart();
    startRessourceSpawners();
    getRegion().getWorld().setTime(this.time);
    teleportPlayersToTeamSpawn();
    this.state = GameState.RUNNING;
    updateScoreboard();
    if (Main.getInstance().getBooleanConfig("store-game-records", true)) {
      displayRecord();
    }
    startTimerCountdown();
    if (Main.getInstance().getBooleanConfig("titles.map.enabled", false)) {
      displayMapInfo();
    }
    updateSigns();
    if (Main.getInstance().getBooleanConfig("global-messages", true)) {
      Main.getInstance().getServer().broadcastMessage(ChatWriter.pluginMessage(new StringBuilder().append(ChatColor.GREEN).append(Main._l("ingame.gamestarted", ImmutableMap.of("game", getRegion().getName()))).toString()));
    }
    return true;
  }
  
  public boolean stop()
  {
    if (this.state == GameState.STOPPED) {
      return false;
    }
    this.isStopping = true;
    stopWorkers();
    clearProtections();
    kickAllPlayers();
    resetRegion();
    this.state = GameState.STOPPED;
    updateSigns();
    this.isStopping = false;
    return true;
  }
  
  public boolean isInGame(Player paramPlayer)
  {
    Iterator localIterator = this.teams.values().iterator();
    while (localIterator.hasNext())
    {
      Team localTeam = (Team)localIterator.next();
      if (localTeam.isInTeam(paramPlayer)) {
        return true;
      }
    }
    return this.freePlayers.contains(paramPlayer);
  }
  
  public void addRessourceSpawner(RessourceSpawner paramRessourceSpawner)
  {
    this.resSpawner.add(paramRessourceSpawner);
  }
  
  public List<RessourceSpawner> getRessourceSpawner()
  {
    return this.resSpawner;
  }
  
  public void setLoc(Location paramLocation, String paramString)
  {
    if (paramString.equalsIgnoreCase("loc1")) {
      this.loc1 = paramLocation;
    } else {
      this.loc2 = paramLocation;
    }
  }
  
  public Team getPlayerTeam(Player paramPlayer)
  {
    Iterator localIterator = getTeams().values().iterator();
    while (localIterator.hasNext())
    {
      Team localTeam = (Team)localIterator.next();
      if (localTeam.isInTeam(paramPlayer)) {
        return localTeam;
      }
    }
    return null;
  }
  
  public boolean saveGame(CommandSender paramCommandSender, boolean paramBoolean)
  {
    BedwarsSaveGameEvent localBedwarsSaveGameEvent = new BedwarsSaveGameEvent(this, paramCommandSender);
    Main.getInstance().getServer().getPluginManager().callEvent(localBedwarsSaveGameEvent);
    if (localBedwarsSaveGameEvent.isCancelled()) {
      return true;
    }
    GameCheckCode localGameCheckCode = checkGame();
    if (localGameCheckCode != GameCheckCode.OK)
    {
      paramCommandSender.sendMessage(ChatWriter.pluginMessage(new StringBuilder().append(ChatColor.RED).append(localGameCheckCode.getCodeMessage()).toString()));
      return false;
    }
    File localFile = new File(new StringBuilder().append(Main.getInstance().getDataFolder()).append("/").append(GameManager.gamesPath).append("/").append(this.name).append("/game.yml").toString());
    localFile.mkdirs();
    if (localFile.exists()) {
      localFile.delete();
    }
    saveRegion(paramBoolean);
    createGameConfig(localFile);
    return true;
  }
  
  public int getMaxPlayers()
  {
    int i = 0;
    Iterator localIterator = this.teams.values().iterator();
    while (localIterator.hasNext())
    {
      Team localTeam = (Team)localIterator.next();
      i += localTeam.getMaxPlayers();
    }
    return i;
  }
  
  public Team getTeamOfBed(Block paramBlock)
  {
    Iterator localIterator = getTeams().values().iterator();
    while (localIterator.hasNext())
    {
      Team localTeam = (Team)localIterator.next();
      if (localTeam.getFeetTarget() == null)
      {
        if (localTeam.getHeadTarget().equals(paramBlock)) {
          return localTeam;
        }
      }
      else if ((localTeam.getHeadTarget().equals(paramBlock)) || (localTeam.getFeetTarget().equals(paramBlock))) {
        return localTeam;
      }
    }
    return null;
  }
  
  public int getCurrentPlayerAmount()
  {
    int i = 0;
    Iterator localIterator = this.teams.values().iterator();
    while (localIterator.hasNext())
    {
      Team localTeam = (Team)localIterator.next();
      i += localTeam.getPlayers().size();
    }
    return i;
  }
  
  public int getPlayerAmount()
  {
    return getPlayers().size();
  }
  
  public boolean isFull()
  {
    return getMaxPlayers() <= getPlayerAmount();
  }
  
  public void addTeam(String paramString, TeamColor paramTeamColor, int paramInt)
  {
    org.bukkit.scoreboard.Team localTeam = this.scoreboard.registerNewTeam(paramString);
    localTeam.setDisplayName(paramString);
    localTeam.setPrefix(paramTeamColor.getChatColor().toString());
    Team localTeam1 = new Team(paramString, paramTeamColor, paramInt, localTeam);
    this.teams.put(paramString, localTeam1);
  }
  
  public boolean isAutobalanceEnabled()
  {
    if (Main.getInstance().getBooleanConfig("global-autobalance", false)) {
      return true;
    }
    return this.autobalance;
  }
  
  public void addTeam(Team paramTeam)
  {
    org.bukkit.scoreboard.Team localTeam = this.scoreboard.registerNewTeam(paramTeam.getName());
    localTeam.setDisplayName(paramTeam.getName());
    localTeam.setPrefix(paramTeam.getChatColor().toString());
    paramTeam.setScoreboardTeam(localTeam);
    this.teams.put(paramTeam.getName(), paramTeam);
  }
  
  public void toSpectator(Player paramPlayer)
  {
    final Player localPlayer = paramPlayer;
    Team localTeam = getPlayerTeam(paramPlayer);
    if (localTeam != null) {
      localTeam.removePlayer(paramPlayer);
    }
    if (!this.freePlayers.contains(paramPlayer)) {
      this.freePlayers.add(paramPlayer);
    }
    PlayerStorage localPlayerStorage = getPlayerStorage(paramPlayer);
    if (localPlayerStorage != null)
    {
      localPlayerStorage.clean();
    }
    else
    {
      localPlayerStorage = addPlayerStorage(paramPlayer);
      localPlayerStorage.store();
      localPlayerStorage.clean();
    }
    final Location localLocation = getPlayerTeleportLocation(localPlayer);
    if (!localPlayer.getLocation().getWorld().equals(localLocation.getWorld()))
    {
      getPlayerSettings(localPlayer).setTeleporting(true);
      if (Main.getInstance().isBungee()) {
        new BukkitRunnable()
        {
          public void run()
          {
            localPlayer.teleport(localLocation);
          }
        }.runTaskLater(Main.getInstance(), 10L);
      } else {
        localPlayer.teleport(localLocation);
      }
    }
    new BukkitRunnable()
    {
      public void run()
      {
        Game.this.setPlayerGameMode(localPlayer);
        Game.this.setPlayerVisibility(localPlayer);
      }
    }.runTaskLater(Main.getInstance(), 15L);
    ItemStack localItemStack1 = new ItemStack(Material.SLIME_BALL, 1);
    ItemMeta localItemMeta = localItemStack1.getItemMeta();
    localItemMeta.setDisplayName(Main._l("lobby.leavegame"));
    localItemStack1.setItemMeta(localItemMeta);
    localPlayer.getInventory().setItem(8, localItemStack1);
    if (((getCycle() instanceof BungeeGameCycle)) && (getCycle().isEndGameRunning()) && (Main.getInstance().getBooleanConfig("bungeecord.endgame-in-lobby", true)))
    {
      localPlayer.updateInventory();
      return;
    }
    ItemStack localItemStack2 = new ItemStack(Material.COMPASS, 1);
    localItemMeta = localItemStack2.getItemMeta();
    localItemMeta.setDisplayName(Main._l("ingame.spectate"));
    localItemStack2.setItemMeta(localItemMeta);
    localPlayer.getInventory().setItem(0, localItemStack2);
    localPlayer.updateInventory();
    updateScoreboard();
  }
  
  public Location getPlayerTeleportLocation(Player paramPlayer)
  {
    if ((isSpectator(paramPlayer)) && ((!(getCycle() instanceof BungeeGameCycle)) || (!getCycle().isEndGameRunning()) || (!Main.getInstance().getBooleanConfig("bungeecord.endgame-in-lobby", true)))) {
      return ((Team)this.teams.values().toArray()[Utils.randInt(0, this.teams.size() - 1)]).getSpawnLocation();
    }
    if ((getPlayerTeam(paramPlayer) != null) && ((!(getCycle() instanceof BungeeGameCycle)) || (!getCycle().isEndGameRunning()) || (!Main.getInstance().getBooleanConfig("bungeecord.endgame-in-lobby", true)))) {
      return getPlayerTeam(paramPlayer).getSpawnLocation();
    }
    return getLobby();
  }
  
  public void setPlayerGameMode(Player paramPlayer)
  {
    if ((isSpectator(paramPlayer)) && ((!(getCycle() instanceof BungeeGameCycle)) || (!getCycle().isEndGameRunning()) || (!Main.getInstance().getBooleanConfig("bungeecord.endgame-in-lobby", true))))
    {
      paramPlayer.setAllowFlight(true);
      paramPlayer.setFlying(true);
      try
      {
        paramPlayer.setGameMode(GameMode.valueOf("SPECTATOR"));
      }
      catch (Exception localException1)
      {
        paramPlayer.setGameMode(GameMode.SURVIVAL);
      }
    }
    else
    {
      GameMode localGameMode = null;
      try
      {
        localGameMode = GameMode.getByValue(Main.getInstance().getIntConfig("lobby-gamemode", 0));
      }
      catch (Exception localException2) {}
      if (localGameMode == null) {
        localGameMode = GameMode.SURVIVAL;
      }
      paramPlayer.setGameMode(localGameMode);
    }
  }
  
  public void setPlayerVisibility(Player paramPlayer)
  {
    ArrayList localArrayList = new ArrayList();
    localArrayList.addAll(getPlayers());
    Iterator localIterator;
    Player localPlayer;
    if ((this.state == GameState.RUNNING) && ((!(getCycle() instanceof BungeeGameCycle)) || (!getCycle().isEndGameRunning()) || (!Main.getInstance().getBooleanConfig("bungeecord.endgame-in-lobby", true))))
    {
      if (isSpectator(paramPlayer))
      {
        if (paramPlayer.getGameMode().equals(GameMode.SURVIVAL))
        {
          localIterator = localArrayList.iterator();
          while (localIterator.hasNext())
          {
            localPlayer = (Player)localIterator.next();
            localPlayer.hidePlayer(paramPlayer);
            paramPlayer.showPlayer(localPlayer);
          }
        }
        else
        {
          localIterator = getTeamPlayers().iterator();
          while (localIterator.hasNext())
          {
            localPlayer = (Player)localIterator.next();
            localPlayer.hidePlayer(paramPlayer);
            paramPlayer.showPlayer(localPlayer);
          }
          localIterator = getFreePlayers().iterator();
          while (localIterator.hasNext())
          {
            localPlayer = (Player)localIterator.next();
            localPlayer.showPlayer(paramPlayer);
            paramPlayer.showPlayer(localPlayer);
          }
        }
      }
      else
      {
        localIterator = localArrayList.iterator();
        while (localIterator.hasNext())
        {
          localPlayer = (Player)localIterator.next();
          localPlayer.showPlayer(paramPlayer);
          paramPlayer.showPlayer(localPlayer);
        }
      }
    }
    else
    {
      localIterator = localArrayList.iterator();
      while (localIterator.hasNext())
      {
        localPlayer = (Player)localIterator.next();
        if (!localPlayer.equals(paramPlayer))
        {
          localPlayer.showPlayer(paramPlayer);
          paramPlayer.showPlayer(localPlayer);
        }
      }
    }
  }
  
  public boolean isSpectator(Player paramPlayer)
  {
    return (getState() == GameState.RUNNING) && (this.freePlayers.contains(paramPlayer));
  }
  
  public boolean playerJoins(final Player paramPlayer)
  {
    if ((this.state == GameState.STOPPED) || ((this.state == GameState.RUNNING) && (!Main.getInstance().spectationEnabled())))
    {
      if ((this.cycle instanceof BungeeGameCycle)) {
        ((BungeeGameCycle)this.cycle).sendBungeeMessage(paramPlayer, ChatWriter.pluginMessage(new StringBuilder().append(ChatColor.RED).append(Main._l("errors.cantjoingame")).toString()));
      } else {
        paramPlayer.sendMessage(ChatWriter.pluginMessage(new StringBuilder().append(ChatColor.RED).append(Main._l("errors.cantjoingame")).toString()));
      }
      return false;
    }
    if (!this.cycle.onPlayerJoins(paramPlayer)) {
      return false;
    }
    BedwarsPlayerJoinEvent localBedwarsPlayerJoinEvent = new BedwarsPlayerJoinEvent(this, paramPlayer);
    Main.getInstance().getServer().getPluginManager().callEvent(localBedwarsPlayerJoinEvent);
    if (localBedwarsPlayerJoinEvent.isCancelled()) {
      return false;
    }
    Main.getInstance().getGameManager().addGamePlayer(paramPlayer, this);
    if (Main.getInstance().statisticsEnabled()) {
      Main.getInstance().getPlayerStatisticManager().getStatistic(paramPlayer);
    }
    this.playerDamages.put(paramPlayer, null);
    addPlayerSettings(paramPlayer);
    new BukkitRunnable()
    {
      public void run()
      {
        Iterator localIterator = Game.this.getPlayers().iterator();
        while (localIterator.hasNext())
        {
          Player localPlayer = (Player)localIterator.next();
          localPlayer.hidePlayer(paramPlayer);
          paramPlayer.hidePlayer(localPlayer);
        }
      }
    }.runTaskLater(Main.getInstance(), 5L);
    if (this.state == GameState.RUNNING)
    {
      toSpectator(paramPlayer);
      displayMapInfo(paramPlayer);
    }
    else
    {
      localObject1 = addPlayerStorage(paramPlayer);
      ((PlayerStorage)localObject1).store();
      ((PlayerStorage)localObject1).clean();
      if ((!Utils.isSupportingTitles()) || (!Main.getInstance().isBungee()))
      {
        localObject2 = getPlayerTeleportLocation(paramPlayer);
        if (!paramPlayer.getLocation().equals(localObject2))
        {
          getPlayerSettings(paramPlayer).setTeleporting(true);
          if (Main.getInstance().isBungee()) {
            new BukkitRunnable()
            {
              public void run()
              {
                paramPlayer.teleport(this.val$location);
              }
            }.runTaskLater(Main.getInstance(), 10L);
          } else {
            paramPlayer.teleport((Location)localObject2);
          }
        }
      }
      ((PlayerStorage)localObject1).loadLobbyInventory(this);
      new BukkitRunnable()
      {
        public void run()
        {
          Game.this.setPlayerGameMode(paramPlayer);
          Game.this.setPlayerVisibility(paramPlayer);
        }
      }.runTaskLater(Main.getInstance(), 15L);
      broadcast(new StringBuilder().append(ChatColor.GREEN).append(Main._l("lobby.playerjoin", ImmutableMap.of("player", new StringBuilder().append(paramPlayer.getDisplayName()).append(ChatColor.GREEN).toString()))).toString());
      if (!isAutobalanceEnabled())
      {
        this.freePlayers.add(paramPlayer);
      }
      else
      {
        localObject2 = getLowestTeam();
        ((Team)localObject2).addPlayer(paramPlayer);
      }
      if (Main.getInstance().getBooleanConfig("store-game-records", true)) {
        displayRecord(paramPlayer);
      }
      Object localObject2 = Main.getInstance().getLobbyCountdownRule();
      if ((localObject2 == GameLobbyCountdownRule.PLAYERS_IN_GAME) || (localObject2 == GameLobbyCountdownRule.ENOUGH_TEAMS_AND_PLAYERS)) {
        if (((GameLobbyCountdownRule)localObject2).isRuleMet(this))
        {
          if (this.glc == null)
          {
            this.glc = new GameLobbyCountdown(this);
            this.glc.setRule((GameLobbyCountdownRule)localObject2);
            this.glc.runTaskTimer(Main.getInstance(), 20L, 20L);
          }
        }
        else
        {
          int i = getMinPlayers() - getPlayerAmount();
          broadcast(new StringBuilder().append(ChatColor.GREEN).append(Main._l("lobby.moreplayersneeded", "count", ImmutableMap.of("count", String.valueOf(i)))).toString());
        }
      }
    }
    Object localObject1 = new BedwarsPlayerJoinedEvent(this, paramPlayer);
    Main.getInstance().getServer().getPluginManager().callEvent((Event)localObject1);
    updateScoreboard();
    updateSigns();
    return true;
  }
  
  public boolean playerLeave(Player paramPlayer, boolean paramBoolean)
  {
    getPlayerSettings(paramPlayer).setTeleporting(true);
    BedwarsPlayerLeaveEvent localBedwarsPlayerLeaveEvent = new BedwarsPlayerLeaveEvent(this, paramPlayer);
    Main.getInstance().getServer().getPluginManager().callEvent(localBedwarsPlayerLeaveEvent);
    Team localTeam = getPlayerTeam(paramPlayer);
    PlayerStatistic localPlayerStatistic = null;
    if (Main.getInstance().statisticsEnabled()) {
      localPlayerStatistic = Main.getInstance().getPlayerStatisticManager().getStatistic(paramPlayer);
    }
    if (isSpectator(paramPlayer))
    {
      if (!getCycle().isEndGameRunning())
      {
        localObject = getPlayers().iterator();
        while (((Iterator)localObject).hasNext())
        {
          Player localPlayer = (Player)((Iterator)localObject).next();
          if (!localPlayer.equals(paramPlayer))
          {
            localPlayer.showPlayer(paramPlayer);
            paramPlayer.showPlayer(localPlayer);
          }
        }
      }
    }
    else if ((this.state == GameState.RUNNING) && (!getCycle().isEndGameRunning()) && (!localTeam.isDead(this)) && (!paramPlayer.isDead()) && (Main.getInstance().statisticsEnabled()))
    {
      localPlayerStatistic.setLoses(localPlayerStatistic.getLoses() + 1);
      localPlayerStatistic.addCurrentScore(Main.getInstance().getIntConfig("statistics.scores.lose", 0));
    }
    Main.getInstance().getGameManager().removeGamePlayer(paramPlayer);
    if (isProtected(paramPlayer)) {
      removeProtection(paramPlayer);
    }
    this.playerDamages.remove(paramPlayer);
    if (localTeam != null)
    {
      localTeam.removePlayer(paramPlayer);
      if (paramBoolean) {
        broadcast(new StringBuilder().append(ChatColor.RED).append(Main._l("ingame.player.kicked", ImmutableMap.of("player", new StringBuilder().append(getPlayerWithTeamString(paramPlayer, localTeam, ChatColor.RED)).append(ChatColor.RED).toString()))).toString());
      } else {
        broadcast(new StringBuilder().append(ChatColor.RED).append(Main._l("ingame.player.left", ImmutableMap.of("player", new StringBuilder().append(getPlayerWithTeamString(paramPlayer, localTeam, ChatColor.RED)).append(ChatColor.RED).toString()))).toString());
      }
    }
    if (this.freePlayers.contains(paramPlayer)) {
      this.freePlayers.remove(paramPlayer);
    }
    if (Main.getInstance().isBungee()) {
      this.cycle.onPlayerLeave(paramPlayer);
    }
    if (Main.getInstance().statisticsEnabled())
    {
      localPlayerStatistic.setScore(localPlayerStatistic.getScore() + localPlayerStatistic.getCurrentScore());
      localPlayerStatistic.setCurrentScore(0);
      localPlayerStatistic.store();
      if ((Main.getInstance().isHologramsEnabled()) && (Main.getInstance().getHolographicInteractor() != null)) {
        Main.getInstance().getHolographicInteractor().updateHolograms(paramPlayer);
      }
      if (Main.getInstance().getBooleanConfig("statistics.show-on-game-end", true)) {
        Main.getInstance().getServer().dispatchCommand(paramPlayer, "bw stats");
      }
      Main.getInstance().getPlayerStatisticManager().unloadStatistic(paramPlayer);
    }
    Object localObject = (PlayerStorage)this.storages.get(paramPlayer);
    ((PlayerStorage)localObject).clean();
    ((PlayerStorage)localObject).restore();
    this.playerSettings.remove(paramPlayer);
    updateScoreboard();
    paramPlayer.setScoreboard(Main.getInstance().getScoreboardManager().getMainScoreboard());
    removeNewItemShop(paramPlayer);
    notUseOldShop(paramPlayer);
    if ((!Main.getInstance().isBungee()) && (paramPlayer.isOnline())) {
      if (paramBoolean) {
        paramPlayer.sendMessage(ChatWriter.pluginMessage(new StringBuilder().append(ChatColor.RED).append(Main._l("ingame.player.waskicked")).toString()));
      } else {
        paramPlayer.sendMessage(ChatWriter.pluginMessage(new StringBuilder().append(ChatColor.GREEN).append(Main._l("success.left")).toString()));
      }
    }
    if (!Main.getInstance().isBungee()) {
      this.cycle.onPlayerLeave(paramPlayer);
    }
    updateSigns();
    this.storages.remove(paramPlayer);
    return true;
  }
  
  public PlayerStorage addPlayerStorage(Player paramPlayer)
  {
    PlayerStorage localPlayerStorage = new PlayerStorage(paramPlayer);
    this.storages.put(paramPlayer, localPlayerStorage);
    return localPlayerStorage;
  }
  
  public void broadcast(String paramString)
  {
    Iterator localIterator = getPlayers().iterator();
    while (localIterator.hasNext())
    {
      Player localPlayer = (Player)localIterator.next();
      if (localPlayer.isOnline()) {
        localPlayer.sendMessage(ChatWriter.pluginMessage(paramString));
      }
    }
  }
  
  public void broadcastSound(Sound paramSound, float paramFloat1, float paramFloat2)
  {
    Iterator localIterator = getPlayers().iterator();
    while (localIterator.hasNext())
    {
      Player localPlayer = (Player)localIterator.next();
      if (localPlayer.isOnline()) {
        localPlayer.playSound(localPlayer.getLocation(), paramSound, paramFloat1, paramFloat2);
      }
    }
  }
  
  public void broadcastSound(Sound paramSound, float paramFloat1, float paramFloat2, List<Player> paramList)
  {
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      Player localPlayer = (Player)localIterator.next();
      if (localPlayer.isOnline()) {
        localPlayer.playSound(localPlayer.getLocation(), paramSound, paramFloat1, paramFloat2);
      }
    }
  }
  
  public void broadcast(String paramString, List<Player> paramList)
  {
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      Player localPlayer = (Player)localIterator.next();
      if (localPlayer.isOnline()) {
        localPlayer.sendMessage(ChatWriter.pluginMessage(paramString));
      }
    }
  }
  
  public void addRunningTask(BukkitTask paramBukkitTask)
  {
    this.runningTasks.add(paramBukkitTask);
  }
  
  public boolean handleDestroyTargetMaterial(Player paramPlayer, Block paramBlock)
  {
    Team localTeam1 = getPlayerTeam(paramPlayer);
    if (localTeam1 == null) {
      return false;
    }
    Team localTeam2 = null;
    Block localBlock = localTeam1.getHeadTarget();
    Object localObject1;
    if (paramBlock.getType().equals(Material.BED_BLOCK))
    {
      localObject1 = paramBlock;
      Object localObject2 = null;
      Bed localBed = (Bed)((Block)localObject1).getState().getData();
      if (!localBed.isHeadOfBed())
      {
        localObject2 = localObject1;
        localObject1 = Utils.getBedNeighbor((Block)localObject2);
      }
      else
      {
        localObject2 = Utils.getBedNeighbor((Block)localObject1);
      }
      if (localBlock.equals(localObject1))
      {
        paramPlayer.sendMessage(ChatWriter.pluginMessage(new StringBuilder().append(ChatColor.RED).append(Main._l("ingame.blocks.ownbeddestroy")).toString()));
        return false;
      }
      localTeam2 = getTeamOfBed((Block)localObject1);
      if (localTeam2 == null) {
        return false;
      }
      ((Block)localObject2).getDrops().clear();
      ((Block)localObject2).setType(Material.AIR);
      ((Block)localObject1).getDrops().clear();
      ((Block)localObject1).setType(Material.AIR);
    }
    else
    {
      if (localBlock.equals(paramBlock))
      {
        paramPlayer.sendMessage(ChatWriter.pluginMessage(new StringBuilder().append(ChatColor.RED).append(Main._l("ingame.blocks.ownbeddestroy")).toString()));
        return false;
      }
      localTeam2 = getTeamOfBed(paramBlock);
      if (localTeam2 == null) {
        return false;
      }
      paramBlock.getDrops().clear();
      paramBlock.setType(Material.AIR);
    }
    if (Main.getInstance().statisticsEnabled())
    {
      localObject1 = Main.getInstance().getPlayerStatisticManager().getStatistic(paramPlayer);
      ((PlayerStatistic)localObject1).setDestroyedBeds(((PlayerStatistic)localObject1).getDestroyedBeds() + 1);
      ((PlayerStatistic)localObject1).addCurrentScore(Main.getInstance().getIntConfig("statistics.scores.bed-destroy", 25));
    }
    if (Main.getInstance().getBooleanConfig("rewards.enabled", false))
    {
      localObject1 = Main.getInstance().getConfig().getStringList("rewards.player-destroy-bed");
      Main.getInstance().dispatchRewardCommands((List)localObject1, ImmutableMap.of("{player}", paramPlayer.getName(), "{score}", String.valueOf(Main.getInstance().getIntConfig("statistics.scores.bed-destroy", 25))));
    }
    broadcast(new StringBuilder().append(ChatColor.RED).append(Main._l("ingame.blocks.beddestroyed", ImmutableMap.of("team", new StringBuilder().append(localTeam2.getChatColor()).append(localTeam2.getName()).append(ChatColor.RED).toString(), "player", getPlayerWithTeamString(paramPlayer, localTeam1, ChatColor.RED)))).toString());
    broadcastSound(Sound.valueOf(Main.getInstance().getStringConfig("bed-sound", "ENDERDRAGON_GROWL").toUpperCase()), 30.0F, 10.0F);
    updateScoreboard();
    return true;
  }
  
  public void saveRecord()
  {
    File localFile = new File(new StringBuilder().append(Main.getInstance().getDataFolder()).append("/").append(GameManager.gamesPath).append("/").append(this.name).append("/game.yml").toString());
    if (!localFile.exists()) {
      return;
    }
    this.config.set("record", Integer.valueOf(this.record));
    if (Main.getInstance().getBooleanConfig("store-game-records-holder", true)) {
      this.config.set("record-holders", this.recordHolders);
    }
    try
    {
      this.config.save(localFile);
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace();
    }
  }
  
  public GameCheckCode checkGame()
  {
    if ((this.loc1 == null) || (this.loc2 == null)) {
      return GameCheckCode.LOC_NOT_SET_ERROR;
    }
    if (this.teams == null) {
      return GameCheckCode.TEAM_SIZE_LOW_ERROR;
    }
    if (this.teams.size() <= 1) {
      return GameCheckCode.TEAM_SIZE_LOW_ERROR;
    }
    GameCheckCode localGameCheckCode = checkTeams();
    if (localGameCheckCode != GameCheckCode.OK) {
      return localGameCheckCode;
    }
    if (getRessourceSpawner().size() == 0) {
      return GameCheckCode.NO_RES_SPAWNER_ERROR;
    }
    if (this.lobby == null) {
      return GameCheckCode.NO_LOBBY_SET;
    }
    if ((Main.getInstance().toMainLobby()) && (this.mainLobby == null)) {
      return GameCheckCode.NO_MAIN_LOBBY_SET;
    }
    return GameCheckCode.OK;
  }
  
  public void openSpectatorCompass(Player paramPlayer)
  {
    if (!isSpectator(paramPlayer)) {
      return;
    }
    int i = getTeamPlayers().size();
    int j = i % 9 == 0 ? 9 : i % 9;
    int k = i + (9 - j);
    Inventory localInventory = Bukkit.createInventory(null, k, Main._l("ingame.spectator"));
    Iterator localIterator1 = getTeams().values().iterator();
    while (localIterator1.hasNext())
    {
      Team localTeam = (Team)localIterator1.next();
      Iterator localIterator2 = localTeam.getPlayers().iterator();
      while (localIterator2.hasNext())
      {
        Player localPlayer = (Player)localIterator2.next();
        ItemStack localItemStack = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
        SkullMeta localSkullMeta = (SkullMeta)localItemStack.getItemMeta();
        localSkullMeta.setDisplayName(new StringBuilder().append(localTeam.getChatColor()).append(localPlayer.getDisplayName()).toString());
        localSkullMeta.setLore(Arrays.asList(new String[] { new StringBuilder().append(localTeam.getChatColor()).append(localTeam.getDisplayName()).toString() }));
        localSkullMeta.setOwner(localPlayer.getName());
        localItemStack.setItemMeta(localSkullMeta);
        localInventory.addItem(new ItemStack[] { localItemStack });
      }
    }
    paramPlayer.openInventory(localInventory);
  }
  
  public void nonFreePlayer(Player paramPlayer)
  {
    if (this.freePlayers.contains(paramPlayer)) {
      this.freePlayers.remove(paramPlayer);
    }
  }
  
  public void loadItemShopCategories()
  {
    this.itemshop = MerchantCategory.loadCategories(Main.getInstance().getShopConfig());
    this.orderedItemshop = loadOrderedItemShopCategories();
  }
  
  public NewItemShop openNewItemShop(Player paramPlayer)
  {
    NewItemShop localNewItemShop = new NewItemShop(this.orderedItemshop);
    this.newItemShops.put(paramPlayer, localNewItemShop);
    return localNewItemShop;
  }
  
  private List<MerchantCategory> loadOrderedItemShopCategories()
  {
    ArrayList localArrayList = new ArrayList(this.itemshop.values());
    Collections.sort(localArrayList, new MerchantCategoryComparator());
    return localArrayList;
  }
  
  public void kickAllPlayers()
  {
    Iterator localIterator = getPlayers().iterator();
    while (localIterator.hasNext())
    {
      Player localPlayer = (Player)localIterator.next();
      playerLeave(localPlayer, false);
    }
  }
  
  public Team getTeamOfEnderChest(Block paramBlock)
  {
    Iterator localIterator = this.teams.values().iterator();
    while (localIterator.hasNext())
    {
      Team localTeam = (Team)localIterator.next();
      if (localTeam.getChests().contains(paramBlock)) {
        return localTeam;
      }
    }
    return null;
  }
  
  public void resetRegion()
  {
    if (this.region == null) {
      return;
    }
    this.region.reset(this);
  }
  
  private String formatScoreboardTitle()
  {
    String str = Main.getInstance().getStringConfig("scoreboard.format-title", "&e$region$&f - $time$");
    str = str.replace("$region$", getRegion().getName());
    str = str.replace("$game$", this.name);
    str = str.replace("$time$", getFormattedTimeLeft());
    return ChatColor.translateAlternateColorCodes('&', str);
  }
  
  private String formatScoreboardTeam(Team paramTeam, boolean paramBoolean)
  {
    String str = null;
    if (paramTeam == null) {
      return "";
    }
    if (paramBoolean) {
      str = Main.getInstance().getStringConfig("scoreboard.format-bed-destroyed", "&c$status$ $team$");
    } else {
      str = Main.getInstance().getStringConfig("scoreboard.format-bed-alive", "&a$status$ $team$");
    }
    str = str.replace("$status$", paramBoolean ? bedLostString() : bedExistString());
    str = str.replace("$team$", new StringBuilder().append(paramTeam.getChatColor()).append(paramTeam.getName()).toString());
    return ChatColor.translateAlternateColorCodes('&', str);
  }
  
  private String formatLobbyScoreboardString(String paramString)
  {
    paramString = paramString.replace("$regionname$", this.region.getName());
    paramString = paramString.replace("$gamename$", this.name);
    paramString = paramString.replace("$players$", String.valueOf(getPlayerAmount()));
    paramString = paramString.replace("$maxplayers$", String.valueOf(getMaxPlayers()));
    return ChatColor.translateAlternateColorCodes('&', paramString);
  }
  
  private void updateLobbyScoreboard()
  {
    this.scoreboard.clearSlot(DisplaySlot.SIDEBAR);
    Objective localObjective = this.scoreboard.getObjective("lobby");
    if (localObjective != null) {
      localObjective.unregister();
    }
    localObjective = this.scoreboard.registerNewObjective("lobby", "dummy");
    localObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
    localObjective.setDisplayName(formatLobbyScoreboardString(Main.getInstance().getStringConfig("lobby-scoreboard.title", "&eBEDWARS")));
    List localList = Main.getInstance().getConfig().getStringList("lobby-scoreboard.content");
    int i = localList.size();
    if ((localList == null) || (localList.isEmpty())) {
      return;
    }
    Iterator localIterator = localList.iterator();
    Object localObject;
    while (localIterator.hasNext())
    {
      localObject = (String)localIterator.next();
      if (((String)localObject).trim().equals("")) {
        for (int j = 0; j <= i; j++) {
          localObject = new StringBuilder().append((String)localObject).append(" ").toString();
        }
      }
      Score localScore = localObjective.getScore(formatLobbyScoreboardString((String)localObject));
      localScore.setScore(i);
      i--;
    }
    localIterator = getPlayers().iterator();
    while (localIterator.hasNext())
    {
      localObject = (Player)localIterator.next();
      ((Player)localObject).setScoreboard(this.scoreboard);
    }
  }
  
  public void updateScoreboard()
  {
    if ((this.state == GameState.WAITING) && (Main.getInstance().getBooleanConfig("lobby-scoreboard.enabled", true)) && (Utils.isSupportingTitles()))
    {
      updateLobbyScoreboard();
      return;
    }
    Objective localObjective = this.scoreboard.getObjective("display");
    if (localObjective == null) {
      localObjective = this.scoreboard.registerNewObjective("display", "dummy");
    }
    localObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
    localObjective.setDisplayName(formatScoreboardTitle());
    Iterator localIterator = this.teams.values().iterator();
    Object localObject;
    while (localIterator.hasNext())
    {
      localObject = (Team)localIterator.next();
      this.scoreboard.resetScores(formatScoreboardTeam((Team)localObject, false));
      this.scoreboard.resetScores(formatScoreboardTeam((Team)localObject, true));
      boolean bool = (((Team)localObject).isDead(this)) && (getState() == GameState.RUNNING);
      Score localScore = localObjective.getScore(formatScoreboardTeam((Team)localObject, bool));
      localScore.setScore(((Team)localObject).getPlayers().size());
    }
    localIterator = getPlayers().iterator();
    while (localIterator.hasNext())
    {
      localObject = (Player)localIterator.next();
      ((Player)localObject).setScoreboard(this.scoreboard);
    }
  }
  
  public void setScoreboard(Scoreboard paramScoreboard)
  {
    this.scoreboard = paramScoreboard;
  }
  
  public Team isOver()
  {
    if ((this.isOver) || (this.state != GameState.RUNNING)) {
      return null;
    }
    ArrayList localArrayList1 = getTeamPlayers();
    ArrayList localArrayList2 = new ArrayList();
    if ((localArrayList1.size() == 0) || (localArrayList1.isEmpty())) {
      return null;
    }
    Iterator localIterator = localArrayList1.iterator();
    while (localIterator.hasNext())
    {
      Player localPlayer = (Player)localIterator.next();
      Team localTeam = getPlayerTeam(localPlayer);
      if (!localArrayList2.contains(localTeam)) {
        if (!localPlayer.isDead()) {
          localArrayList2.add(localTeam);
        } else if (!localTeam.isDead(this)) {
          localArrayList2.add(localTeam);
        }
      }
    }
    if (localArrayList2.size() == 1) {
      return (Team)localArrayList2.get(0);
    }
    return null;
  }
  
  public void resetScoreboard()
  {
    this.timeLeft = Main.getInstance().getMaxLength();
    this.length = this.timeLeft;
    this.scoreboard.clearSlot(DisplaySlot.SIDEBAR);
  }
  
  public void addJoinSign(Location paramLocation)
  {
    if (this.joinSigns.containsKey(paramLocation)) {
      this.joinSigns.remove(paramLocation);
    }
    this.joinSigns.put(paramLocation, new GameJoinSign(this, paramLocation));
    updateSignConfig();
  }
  
  public void removeJoinSign(Location paramLocation)
  {
    this.joinSigns.remove(paramLocation);
    updateSignConfig();
  }
  
  private void updateSignConfig()
  {
    try
    {
      File localFile = new File(new StringBuilder().append(Main.getInstance().getDataFolder()).append("/").append(GameManager.gamesPath).append("/").append(this.name).append("/sign.yml").toString());
      YamlConfiguration localYamlConfiguration = new YamlConfiguration();
      if (localFile.exists()) {
        localYamlConfiguration = YamlConfiguration.loadConfiguration(localFile);
      }
      ArrayList localArrayList = new ArrayList();
      Iterator localIterator = this.joinSigns.keySet().iterator();
      while (localIterator.hasNext())
      {
        Location localLocation = (Location)localIterator.next();
        localArrayList.add(Utils.locationSerialize(localLocation));
      }
      localYamlConfiguration.set("signs", localArrayList);
      localYamlConfiguration.save(localFile);
    }
    catch (Exception localException)
    {
      Main.getInstance().getServer().getConsoleSender().sendMessage(ChatWriter.pluginMessage(new StringBuilder().append(ChatColor.RED).append(Main._l("errors.savesign")).toString()));
    }
  }
  
  public void updateSigns()
  {
    int i = 0;
    Iterator localIterator = this.joinSigns.values().iterator();
    while (localIterator.hasNext())
    {
      GameJoinSign localGameJoinSign = (GameJoinSign)localIterator.next();
      Chunk localChunk = localGameJoinSign.getSign().getLocation().getChunk();
      if (!localChunk.isLoaded()) {
        localChunk.load(true);
      }
      if (localGameJoinSign.getSign() == null)
      {
        localIterator.remove();
        i = 1;
      }
      else
      {
        Block localBlock = localGameJoinSign.getSign().getLocation().getBlock();
        if (!(localBlock.getState() instanceof Sign))
        {
          localIterator.remove();
          i = 1;
        }
        else
        {
          localGameJoinSign.updateSign();
        }
      }
    }
    if (i != 0) {
      updateSignConfig();
    }
  }
  
  public void stopWorkers()
  {
    Iterator localIterator = this.runningTasks.iterator();
    while (localIterator.hasNext())
    {
      BukkitTask localBukkitTask = (BukkitTask)localIterator.next();
      try
      {
        localBukkitTask.cancel();
      }
      catch (Exception localException) {}
    }
    this.runningTasks.clear();
  }
  
  public boolean isProtected(Player paramPlayer)
  {
    return (this.respawnProtected.containsKey(paramPlayer)) && (getState() == GameState.RUNNING);
  }
  
  public void clearProtections()
  {
    Iterator localIterator = this.respawnProtected.values().iterator();
    while (localIterator.hasNext())
    {
      RespawnProtectionRunnable localRespawnProtectionRunnable = (RespawnProtectionRunnable)localIterator.next();
      try
      {
        localRespawnProtectionRunnable.cancel();
      }
      catch (Exception localException) {}
    }
    this.respawnProtected.clear();
  }
  
  public void removeTeam(Team paramTeam)
  {
    this.teams.remove(paramTeam.getName());
    updateSigns();
  }
  
  public void removeRunningTask(BukkitTask paramBukkitTask)
  {
    this.runningTasks.remove(paramBukkitTask);
  }
  
  public void removeRunningTask(BukkitRunnable paramBukkitRunnable)
  {
    this.runningTasks.remove(paramBukkitRunnable);
  }
  
  public String getFormattedRecord()
  {
    return Utils.getFormattedTime(this.record);
  }
  
  public void setTime(int paramInt)
  {
    this.time = paramInt;
  }
  
  public int getTime()
  {
    return this.time;
  }
  
  public int getTimeLeft()
  {
    return this.timeLeft;
  }
  
  public void setRecord(int paramInt)
  {
    this.record = paramInt;
  }
  
  public int getRecord()
  {
    return this.record;
  }
  
  public List<SpecialItem> getSpecialItems()
  {
    return this.currentSpecials;
  }
  
  public Map<Player, Player> getPlayerDamages()
  {
    return this.playerDamages;
  }
  
  public Player getPlayerDamager(Player paramPlayer)
  {
    return (Player)this.playerDamages.get(paramPlayer);
  }
  
  public void setPlayerDamager(Player paramPlayer1, Player paramPlayer2)
  {
    this.playerDamages.remove(paramPlayer1);
    this.playerDamages.put(paramPlayer1, paramPlayer2);
  }
  
  public void removeProtection(Player paramPlayer)
  {
    RespawnProtectionRunnable localRespawnProtectionRunnable = (RespawnProtectionRunnable)this.respawnProtected.get(paramPlayer);
    if (localRespawnProtectionRunnable == null) {
      return;
    }
    try
    {
      localRespawnProtectionRunnable.cancel();
    }
    catch (Exception localException) {}
    this.respawnProtected.remove(paramPlayer);
  }
  
  public RespawnProtectionRunnable addProtection(Player paramPlayer)
  {
    RespawnProtectionRunnable localRespawnProtectionRunnable = new RespawnProtectionRunnable(this, paramPlayer, Main.getInstance().getRespawnProtectionTime().intValue());
    this.respawnProtected.put(paramPlayer, localRespawnProtectionRunnable);
    return localRespawnProtectionRunnable;
  }
  
  public List<String> getRecordHolders()
  {
    return this.recordHolders;
  }
  
  public void addRecordHolder(String paramString)
  {
    this.recordHolders.add(paramString);
  }
  
  public boolean isStopping()
  {
    return this.isStopping;
  }
  
  public String getBuilder()
  {
    return this.builder;
  }
  
  public void setBuilder(String paramString)
  {
    this.builder = paramString;
  }
  
  public Material getTargetMaterial()
  {
    if (this.targetMaterial == null) {
      return Utils.getMaterialByConfig("game-block", Material.BED_BLOCK);
    }
    return this.targetMaterial;
  }
  
  public void setTargetMaterial(Material paramMaterial)
  {
    this.targetMaterial = paramMaterial;
  }
  
  public List<MerchantCategory> getOrderedItemShopCategories()
  {
    return this.orderedItemshop;
  }
  
  public void setGameLobbyCountdown(GameLobbyCountdown paramGameLobbyCountdown)
  {
    this.glc = paramGameLobbyCountdown;
  }
  
  public boolean isUsingOldShop(Player paramPlayer)
  {
    return this.useOldItemShop.contains(paramPlayer);
  }
  
  public void notUseOldShop(Player paramPlayer)
  {
    this.useOldItemShop.remove(paramPlayer);
  }
  
  public void useOldShop(Player paramPlayer)
  {
    this.useOldItemShop.add(paramPlayer);
  }
  
  public boolean isOverSet()
  {
    return this.isOver;
  }
  
  public HashMap<Location, GameJoinSign> getSigns()
  {
    return this.joinSigns;
  }
  
  public GameCycle getCycle()
  {
    return this.cycle;
  }
  
  public void setItemShopCategories(HashMap<Material, MerchantCategory> paramHashMap)
  {
    this.itemshop = paramHashMap;
  }
  
  public HashMap<Material, MerchantCategory> getItemShopCategories()
  {
    return this.itemshop;
  }
  
  public Team getTeamByDyeColor(DyeColor paramDyeColor)
  {
    Iterator localIterator = this.teams.values().iterator();
    while (localIterator.hasNext())
    {
      Team localTeam = (Team)localIterator.next();
      if (localTeam.getColor().getDyeColor().equals(paramDyeColor)) {
        return localTeam;
      }
    }
    return null;
  }
  
  public HashMap<String, Team> getTeams()
  {
    return this.teams;
  }
  
  public Region getRegion()
  {
    return this.region;
  }
  
  public ArrayList<Player> getTeamPlayers()
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = this.teams.values().iterator();
    while (localIterator.hasNext())
    {
      Team localTeam = (Team)localIterator.next();
      localArrayList.addAll(localTeam.getPlayers());
    }
    return localArrayList;
  }
  
  public ArrayList<Player> getPlayers()
  {
    ArrayList localArrayList = new ArrayList();
    localArrayList.addAll(this.freePlayers);
    Iterator localIterator = this.teams.values().iterator();
    while (localIterator.hasNext())
    {
      Team localTeam = (Team)localIterator.next();
      localArrayList.addAll(localTeam.getPlayers());
    }
    return localArrayList;
  }
  
  public List<Player> getNonVipPlayers()
  {
    ArrayList localArrayList = getPlayers();
    Iterator localIterator = localArrayList.iterator();
    while (localIterator.hasNext())
    {
      Player localPlayer = (Player)localIterator.next();
      if ((localPlayer.hasPermission("bw.vip.joinfull")) || (localPlayer.hasPermission("bw.vip.forcestart")) || (localPlayer.hasPermission("bw.vip"))) {
        localIterator.remove();
      }
    }
    return localArrayList;
  }
  
  public int getMinPlayers()
  {
    return this.minPlayers;
  }
  
  public GameState getState()
  {
    return this.state;
  }
  
  public void setState(GameState paramGameState)
  {
    this.state = paramGameState;
    updateSigns();
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public void setRegion(Region paramRegion)
  {
    this.region = paramRegion;
  }
  
  public void setMinPlayers(int paramInt)
  {
    int i = getMaxPlayers();
    if ((i < paramInt) && (i > 0)) {
      paramInt = i;
    }
    this.minPlayers = paramInt;
  }
  
  public Location getLobby()
  {
    return this.lobby;
  }
  
  public void setLobby(Player paramPlayer)
  {
    Location localLocation = paramPlayer.getLocation();
    if ((this.region != null) && (this.region.getWorld().equals(localLocation.getWorld())))
    {
      paramPlayer.sendMessage(ChatWriter.pluginMessage(new StringBuilder().append(ChatColor.RED).append(Main._l("errors.lobbyongameworld")).toString()));
      return;
    }
    this.lobby = localLocation;
    paramPlayer.sendMessage(ChatWriter.pluginMessage(new StringBuilder().append(ChatColor.GREEN).append(Main._l("success.lobbyset")).toString()));
  }
  
  public void setLobby(Location paramLocation)
  {
    if ((this.region != null) && (this.region.getWorld().equals(paramLocation.getWorld())))
    {
      Main.getInstance().getServer().getConsoleSender().sendMessage(ChatWriter.pluginMessage(new StringBuilder().append(ChatColor.RED).append(Main._l("errors.lobbyongameworld")).toString()));
      return;
    }
    this.lobby = paramLocation;
  }
  
  public Team getTeam(String paramString)
  {
    return (Team)this.teams.get(paramString);
  }
  
  public List<Team> getPlayingTeams()
  {
    return this.playingTeams;
  }
  
  public void removePlayerSettings(Player paramPlayer)
  {
    this.playerSettings.remove(paramPlayer);
  }
  
  public void addPlayerSettings(Player paramPlayer)
  {
    this.playerSettings.put(paramPlayer, new PlayerSettings(paramPlayer));
  }
  
  public PlayerSettings getPlayerSettings(Player paramPlayer)
  {
    return (PlayerSettings)this.playerSettings.get(paramPlayer);
  }
  
  public PlayerStorage getPlayerStorage(Player paramPlayer)
  {
    return (PlayerStorage)this.storages.get(paramPlayer);
  }
  
  public void setConfig(YamlConfiguration paramYamlConfiguration)
  {
    this.config = paramYamlConfiguration;
  }
  
  public YamlConfiguration getConfig()
  {
    return this.config;
  }
  
  public void addSpecialItem(SpecialItem paramSpecialItem)
  {
    this.currentSpecials.add(paramSpecialItem);
  }
  
  public void removeSpecialItem(SpecialItem paramSpecialItem)
  {
    this.currentSpecials.remove(paramSpecialItem);
  }
  
  public Location getMainLobby()
  {
    return this.mainLobby;
  }
  
  public void setMainLobby(Location paramLocation)
  {
    this.mainLobby = paramLocation;
  }
  
  public NewItemShop getNewItemShop(Player paramPlayer)
  {
    return (NewItemShop)this.newItemShops.get(paramPlayer);
  }
  
  public void removeNewItemShop(Player paramPlayer)
  {
    if (!this.newItemShops.containsKey(paramPlayer)) {
      return;
    }
    this.newItemShops.remove(paramPlayer);
  }
  
  public List<Player> getFreePlayers()
  {
    return this.freePlayers;
  }
  
  public List<Player> getFreePlayersClone()
  {
    ArrayList localArrayList = new ArrayList();
    if (this.freePlayers.size() > 0) {
      localArrayList.addAll(this.freePlayers);
    }
    return localArrayList;
  }
  
  public GameLobbyCountdown getLobbyCountdown()
  {
    return this.glc;
  }
  
  public void setLobbyCountdown(GameLobbyCountdown paramGameLobbyCountdown)
  {
    this.glc = paramGameLobbyCountdown;
  }
  
  public void setRegionName(String paramString)
  {
    this.regionName = paramString;
  }
  
  public int getLength()
  {
    return this.length;
  }
  
  public void setLength(int paramInt)
  {
    this.length = paramInt;
  }
  
  public void setAutobalance(boolean paramBoolean)
  {
    this.autobalance = paramBoolean;
  }
  
  private void displayMapInfo()
  {
    if (!Utils.isSupportingTitles()) {
      return;
    }
    Iterator localIterator = getPlayers().iterator();
    while (localIterator.hasNext())
    {
      Player localPlayer = (Player)localIterator.next();
      displayMapInfo(localPlayer);
    }
  }
  
  private void displayMapInfo(Player paramPlayer)
  {
    if (!Utils.isSupportingTitles()) {
      return;
    }
    try
    {
      Class localClass = Class.forName(new StringBuilder().append("io.github.yannici.bedwars.Com.").append(Main.getInstance().getCurrentVersion()).append(".Title").toString());
      Method localMethod1 = localClass.getMethod("showTitle", new Class[] { Player.class, String.class, Double.TYPE, Double.TYPE, Double.TYPE });
      double d1 = Main.getInstance().getConfig().getDouble("titles.map.title-fade-in");
      double d2 = Main.getInstance().getConfig().getDouble("titles.map.title-stay");
      double d3 = Main.getInstance().getConfig().getDouble("titles.map.title-fade-out");
      localMethod1.invoke(null, new Object[] { paramPlayer, getRegion().getName(), Double.valueOf(d1), Double.valueOf(d2), Double.valueOf(d3) });
      if (this.builder != null)
      {
        Method localMethod2 = localClass.getMethod("showSubTitle", new Class[] { Player.class, String.class, Double.TYPE, Double.TYPE, Double.TYPE });
        double d4 = Main.getInstance().getConfig().getDouble("titles.map.subtitle-fade-in");
        double d5 = Main.getInstance().getConfig().getDouble("titles.map.subtitle-stay");
        double d6 = Main.getInstance().getConfig().getDouble("titles.map.subtitle-fade-out");
        localMethod2.invoke(null, new Object[] { paramPlayer, Main._l("ingame.title.map-builder", ImmutableMap.of("builder", ChatColor.translateAlternateColorCodes('&', this.builder))), Double.valueOf(d4), Double.valueOf(d5), Double.valueOf(d6) });
      }
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
  
  private void displayRecord()
  {
    Iterator localIterator = getPlayers().iterator();
    while (localIterator.hasNext())
    {
      Player localPlayer = (Player)localIterator.next();
      displayRecord(localPlayer);
    }
  }
  
  private void displayRecord(Player paramPlayer)
  {
    boolean bool = Main.getInstance().getBooleanConfig("store-game-records-holder", true);
    if ((bool) && (getRecordHolders().size() > 0))
    {
      StringBuilder localStringBuilder = new StringBuilder();
      Iterator localIterator = this.recordHolders.iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        if (localStringBuilder.length() == 0) {
          localStringBuilder.append(new StringBuilder().append(ChatColor.WHITE).append(str).toString());
        } else {
          localStringBuilder.append(new StringBuilder().append(ChatColor.GOLD).append(", ").append(ChatColor.WHITE).append(str).toString());
        }
      }
      paramPlayer.sendMessage(ChatWriter.pluginMessage(Main._l("ingame.record-with-holders", ImmutableMap.of("record", getFormattedRecord(), "holders", localStringBuilder.toString()))));
    }
    else
    {
      paramPlayer.sendMessage(ChatWriter.pluginMessage(Main._l("ingame.record", ImmutableMap.of("record", getFormattedRecord()))));
    }
  }
  
  private void makeTeamsReady()
  {
    this.playingTeams.clear();
    Iterator localIterator = this.teams.values().iterator();
    while (localIterator.hasNext())
    {
      Team localTeam = (Team)localIterator.next();
      localTeam.getScoreboardTeam().setAllowFriendlyFire(Main.getInstance().getConfig().getBoolean("friendlyfire"));
      if (localTeam.getPlayers().size() == 0)
      {
        if (localTeam.getFeetTarget() != null) {
          localTeam.getFeetTarget().setType(Material.AIR);
        }
        localTeam.getHeadTarget().setType(Material.AIR);
      }
      else
      {
        this.playingTeams.add(localTeam);
      }
    }
    updateScoreboard();
  }
  
  private void cleanUsersInventory()
  {
    Iterator localIterator = this.storages.values().iterator();
    while (localIterator.hasNext())
    {
      PlayerStorage localPlayerStorage = (PlayerStorage)localIterator.next();
      localPlayerStorage.clean();
    }
  }
  
  private void createGameConfig(File paramFile)
  {
    YamlConfiguration localYamlConfiguration = new YamlConfiguration();
    localYamlConfiguration.set("name", this.name);
    localYamlConfiguration.set("world", getRegion().getWorld().getName());
    localYamlConfiguration.set("loc1", Utils.locationSerialize(this.loc1));
    localYamlConfiguration.set("loc2", Utils.locationSerialize(this.loc2));
    localYamlConfiguration.set("lobby", Utils.locationSerialize(this.lobby));
    localYamlConfiguration.set("minplayers", Integer.valueOf(getMinPlayers()));
    if (Main.getInstance().getBooleanConfig("store-game-records", true))
    {
      localYamlConfiguration.set("record", Integer.valueOf(this.record));
      if (Main.getInstance().getBooleanConfig("store-game-records-holder", true)) {
        localYamlConfiguration.set("record-holders", this.recordHolders);
      }
    }
    if (this.regionName == null) {
      this.regionName = this.region.getName();
    }
    localYamlConfiguration.set("regionname", this.regionName);
    localYamlConfiguration.set("time", Integer.valueOf(this.time));
    localYamlConfiguration.set("targetmaterial", getTargetMaterial().name());
    localYamlConfiguration.set("builder", this.builder);
    if (this.hologramLocation != null) {
      localYamlConfiguration.set("hololoc", Utils.locationSerialize(this.hologramLocation));
    }
    if (this.mainLobby != null) {
      localYamlConfiguration.set("mainlobby", Utils.locationSerialize(this.mainLobby));
    }
    localYamlConfiguration.set("autobalance", Boolean.valueOf(this.autobalance));
    localYamlConfiguration.set("spawner", this.resSpawner);
    localYamlConfiguration.createSection("teams", this.teams);
    try
    {
      localYamlConfiguration.save(paramFile);
      this.config = localYamlConfiguration;
    }
    catch (IOException localIOException)
    {
      Main.getInstance().getLogger().info(ChatWriter.pluginMessage(localIOException.getMessage()));
    }
  }
  
  private void saveRegion(boolean paramBoolean)
  {
    if ((this.region == null) || (paramBoolean))
    {
      if (this.regionName == null) {
        this.regionName = this.loc1.getWorld().getName();
      }
      this.region = new Region(this.loc1, this.loc2, this.regionName);
    }
    this.region.setVillagerNametag();
    updateSigns();
  }
  
  private void startRessourceSpawners()
  {
    Iterator localIterator = getRessourceSpawner().iterator();
    while (localIterator.hasNext())
    {
      RessourceSpawner localRessourceSpawner = (RessourceSpawner)localIterator.next();
      localRessourceSpawner.setGame(this);
      this.runningTasks.add(Main.getInstance().getServer().getScheduler().runTaskTimer(Main.getInstance(), localRessourceSpawner, 20L, Math.round(localRessourceSpawner.getInterval() / 1000.0D * 20.0D)));
    }
  }
  
  private void teleportPlayersToTeamSpawn()
  {
    Iterator localIterator1 = this.teams.values().iterator();
    while (localIterator1.hasNext())
    {
      Team localTeam = (Team)localIterator1.next();
      Iterator localIterator2 = localTeam.getPlayers().iterator();
      while (localIterator2.hasNext())
      {
        Player localPlayer = (Player)localIterator2.next();
        if (!localPlayer.getWorld().equals(localTeam.getSpawnLocation().getWorld())) {
          getPlayerSettings(localPlayer).setTeleporting(true);
        }
        localPlayer.setVelocity(new Vector(0, 0, 0));
        localPlayer.setFallDistance(0.0F);
        localPlayer.teleport(localTeam.getSpawnLocation());
        if (getPlayerStorage(localPlayer) != null) {
          getPlayerStorage(localPlayer).clean();
        }
      }
    }
  }
  
  private Team getLowestTeam()
  {
    Object localObject = null;
    Iterator localIterator = this.teams.values().iterator();
    while (localIterator.hasNext())
    {
      Team localTeam = (Team)localIterator.next();
      if (localObject == null) {
        localObject = localTeam;
      } else if (localTeam.getPlayers().size() < localObject.getPlayers().size()) {
        localObject = localTeam;
      }
    }
    return localObject;
  }
  
  private void moveFreePlayersToTeam()
  {
    Iterator localIterator = this.freePlayers.iterator();
    while (localIterator.hasNext())
    {
      Player localPlayer = (Player)localIterator.next();
      Team localTeam = getLowestTeam();
      localTeam.addPlayer(localPlayer);
    }
    this.freePlayers = new ArrayList();
    updateScoreboard();
  }
  
  private GameCheckCode checkTeams()
  {
    Iterator localIterator = this.teams.values().iterator();
    while (localIterator.hasNext())
    {
      Team localTeam = (Team)localIterator.next();
      if (localTeam.getSpawnLocation() == null) {
        return GameCheckCode.TEAMS_WITHOUT_SPAWNS;
      }
      Material localMaterial = getTargetMaterial();
      if (localMaterial.equals(Material.BED_BLOCK))
      {
        if ((localTeam.getHeadTarget() == null) || (localTeam.getFeetTarget() == null) || (!Utils.isBedBlock(localTeam.getHeadTarget())) || (!Utils.isBedBlock(localTeam.getFeetTarget()))) {
          return GameCheckCode.TEAM_NO_WRONG_BED;
        }
      }
      else
      {
        if (localTeam.getHeadTarget() == null) {
          return GameCheckCode.TEAM_NO_WRONG_TARGET;
        }
        if (!localTeam.getHeadTarget().getType().equals(localMaterial)) {
          return GameCheckCode.TEAM_NO_WRONG_TARGET;
        }
      }
    }
    return GameCheckCode.OK;
  }
  
  private void updateScoreboardTimer()
  {
    Objective localObjective = this.scoreboard.getObjective("display");
    if (localObjective == null) {
      localObjective = this.scoreboard.registerNewObjective("display", "dummy");
    }
    localObjective.setDisplayName(formatScoreboardTitle());
    Iterator localIterator = getPlayers().iterator();
    while (localIterator.hasNext())
    {
      Player localPlayer = (Player)localIterator.next();
      localPlayer.setScoreboard(this.scoreboard);
    }
  }
  
  private String getFormattedTimeLeft()
  {
    int i = 0;
    int j = 0;
    String str1 = "";
    String str2 = "";
    i = (int)Math.floor(this.timeLeft / 60);
    j = this.timeLeft % 60;
    str1 = i < 10 ? new StringBuilder().append("0").append(String.valueOf(i)).toString() : String.valueOf(i);
    str2 = j < 10 ? new StringBuilder().append("0").append(String.valueOf(j)).toString() : String.valueOf(j);
    return new StringBuilder().append(str1).append(":").append(str2).toString();
  }
  
  public void playerJoinTeam(Player paramPlayer, Team paramTeam)
  {
    if (paramTeam.getPlayers().size() >= paramTeam.getMaxPlayers())
    {
      paramPlayer.sendMessage(ChatWriter.pluginMessage(new StringBuilder().append(ChatColor.RED).append(Main._l("errors.teamfull")).toString()));
      return;
    }
    Object localObject2;
    if (paramTeam.addPlayer(paramPlayer))
    {
      nonFreePlayer(paramPlayer);
      localObject1 = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
      localObject2 = (LeatherArmorMeta)((ItemStack)localObject1).getItemMeta();
      ((LeatherArmorMeta)localObject2).setColor(paramTeam.getColor().getColor());
      ((LeatherArmorMeta)localObject2).setDisplayName(new StringBuilder().append(paramTeam.getChatColor()).append(paramTeam.getDisplayName()).toString());
      ((ItemStack)localObject1).setItemMeta((ItemMeta)localObject2);
      paramPlayer.getInventory().setItem(7, (ItemStack)localObject1);
      paramPlayer.updateInventory();
    }
    else
    {
      paramPlayer.sendMessage(ChatWriter.pluginMessage(new StringBuilder().append(ChatColor.RED).append(Main._l("errors.teamfull")).toString()));
      return;
    }
    updateScoreboard();
    Object localObject1 = Main.getInstance().getLobbyCountdownRule();
    if (((localObject1 == GameLobbyCountdownRule.TEAMS_HAVE_PLAYERS) || (localObject1 == GameLobbyCountdownRule.ENOUGH_TEAMS_AND_PLAYERS)) && (((GameLobbyCountdownRule)localObject1).isRuleMet(this)) && (getLobbyCountdown() == null))
    {
      localObject2 = new GameLobbyCountdown(this);
      ((GameLobbyCountdown)localObject2).setRule((GameLobbyCountdownRule)localObject1);
      ((GameLobbyCountdown)localObject2).runTaskTimer(Main.getInstance(), 20L, 20L);
      setLobbyCountdown((GameLobbyCountdown)localObject2);
    }
    paramTeam.equipPlayerWithLeather(paramPlayer);
    paramPlayer.sendMessage(ChatWriter.pluginMessage(new StringBuilder().append(ChatColor.GREEN).append(Main._l("lobby.teamjoined", ImmutableMap.of("team", new StringBuilder().append(paramTeam.getDisplayName()).append(ChatColor.GREEN).toString()))).toString()));
  }
  
  private void startTimerCountdown()
  {
    this.timeLeft = Main.getInstance().getMaxLength();
    this.length = Main.getInstance().getMaxLength();
    BukkitRunnable local6 = new BukkitRunnable()
    {
      public void run()
      {
        Game.this.updateScoreboardTimer();
        if (Game.this.timeLeft == 0)
        {
          Game.this.isOver = true;
          Game.this.getCycle().checkGameOver();
          cancel();
          return;
        }
        Game.access$110(Game.this);
      }
    };
    this.runningTasks.add(local6.runTaskTimer(Main.getInstance(), 0L, 20L));
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Game.Game
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */