package io.github.yannici.bedwars;

import com.google.common.collect.ImmutableMap;
import io.github.yannici.bedwars.Commands.AddGameCommand;
import io.github.yannici.bedwars.Commands.AddHoloCommand;
import io.github.yannici.bedwars.Commands.AddTeamCommand;
import io.github.yannici.bedwars.Commands.AddTeamJoinCommand;
import io.github.yannici.bedwars.Commands.BaseCommand;
import io.github.yannici.bedwars.Commands.ClearSpawnerCommand;
import io.github.yannici.bedwars.Commands.GameTimeCommand;
import io.github.yannici.bedwars.Commands.HelpCommand;
import io.github.yannici.bedwars.Commands.JoinGameCommand;
import io.github.yannici.bedwars.Commands.KickCommand;
import io.github.yannici.bedwars.Commands.LeaveGameCommand;
import io.github.yannici.bedwars.Commands.ListGamesCommand;
import io.github.yannici.bedwars.Commands.RegionNameCommand;
import io.github.yannici.bedwars.Commands.ReloadCommand;
import io.github.yannici.bedwars.Commands.RemoveGameCommand;
import io.github.yannici.bedwars.Commands.RemoveHoloCommand;
import io.github.yannici.bedwars.Commands.RemoveTeamCommand;
import io.github.yannici.bedwars.Commands.SaveGameCommand;
import io.github.yannici.bedwars.Commands.SetAutobalanceCommand;
import io.github.yannici.bedwars.Commands.SetBedCommand;
import io.github.yannici.bedwars.Commands.SetBuilderCommand;
import io.github.yannici.bedwars.Commands.SetGameBlockCommand;
import io.github.yannici.bedwars.Commands.SetLobbyCommand;
import io.github.yannici.bedwars.Commands.SetMainLobbyCommand;
import io.github.yannici.bedwars.Commands.SetMinPlayersCommand;
import io.github.yannici.bedwars.Commands.SetRegionCommand;
import io.github.yannici.bedwars.Commands.SetSpawnCommand;
import io.github.yannici.bedwars.Commands.SetSpawnerCommand;
import io.github.yannici.bedwars.Commands.SetTargetCommand;
import io.github.yannici.bedwars.Commands.StartGameCommand;
import io.github.yannici.bedwars.Commands.StatsCommand;
import io.github.yannici.bedwars.Commands.StopGameCommand;
import io.github.yannici.bedwars.Database.DatabaseManager;
import io.github.yannici.bedwars.Game.Game;
import io.github.yannici.bedwars.Game.GameLobbyCountdownRule;
import io.github.yannici.bedwars.Game.GameManager;
import io.github.yannici.bedwars.Game.GameState;
import io.github.yannici.bedwars.Game.Region;
import io.github.yannici.bedwars.Game.RessourceSpawner;
import io.github.yannici.bedwars.Game.Team;
import io.github.yannici.bedwars.Listener.BlockListener;
import io.github.yannici.bedwars.Listener.ChunkListener;
import io.github.yannici.bedwars.Listener.Entity18Listener;
import io.github.yannici.bedwars.Listener.EntityListener;
import io.github.yannici.bedwars.Listener.HangingListener;
import io.github.yannici.bedwars.Listener.Player18Listener;
import io.github.yannici.bedwars.Listener.PlayerListener;
import io.github.yannici.bedwars.Listener.ServerListener;
import io.github.yannici.bedwars.Listener.SignListener;
import io.github.yannici.bedwars.Listener.WeatherListener;
import io.github.yannici.bedwars.Localization.LocalizationConfig;
import io.github.yannici.bedwars.Shop.Specials.SpecialItem;
import io.github.yannici.bedwars.Statistics.PlayerStatisticManager;
import io.github.yannici.bedwars.Statistics.StorageType;
import io.github.yannici.bedwars.Updater.ConfigUpdater;
import io.github.yannici.bedwars.Updater.DatabaseUpdater;
import io.github.yannici.bedwars.Updater.PluginUpdater;
import io.github.yannici.bedwars.Updater.PluginUpdater.UpdateCallback;
import io.github.yannici.bedwars.Updater.PluginUpdater.UpdateResult;
import io.github.yannici.bedwars.Updater.PluginUpdater.UpdateType;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.FileConfigurationOptions;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.ScoreboardManager;

public class Main
  extends JavaPlugin
{
  private static Main instance = null;
  public static int PROJECT_ID = 91743;
  private ArrayList<BaseCommand> commands = new ArrayList();
  private BukkitTask timeTask = null;
  private Package craftbukkit = null;
  private Package minecraft = null;
  private String version = null;
  private LocalizationConfig localization = null;
  private DatabaseManager dbManager = null;
  private BukkitTask updateChecker = null;
  private List<Material> breakableTypes = null;
  private YamlConfiguration shopConfig = null;
  private HolographicDisplaysInteraction holographicInteraction = null;
  private boolean isSpigot = false;
  private static Boolean locationSerializable = null;
  private PlayerStatisticManager playerStatisticManager = null;
  private ScoreboardManager scoreboardManager = null;
  private GameManager gameManager = null;
  
  public void onEnable()
  {
    instance = this;
    registerConfigurationClasses();
    saveDefaultConfig();
    loadConfigInUTF();
    getConfig().options().copyDefaults(true);
    getConfig().options().copyHeader(true);
    ConfigUpdater localConfigUpdater = new ConfigUpdater();
    localConfigUpdater.addConfigs();
    saveConfiguration();
    loadConfigInUTF();
    loadShop();
    this.isSpigot = getIsSpigot();
    loadDatabase();
    this.craftbukkit = getCraftBukkit();
    this.minecraft = getMinecraftPackage();
    this.version = loadVersion();
    registerCommands();
    registerListener();
    this.gameManager = new GameManager();
    if (getInstance().isBungee()) {
      getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }
    loadStatistics();
    this.localization = loadLocalization();
    checkUpdates();
    this.scoreboardManager = Bukkit.getScoreboardManager();
    this.gameManager.loadGames();
    startTimeListener();
    startMetricsIfEnabled();
    if (isHologramsEnabled())
    {
      this.holographicInteraction = new HolographicDisplaysInteraction();
      this.holographicInteraction.loadHolograms();
    }
  }
  
  public void onDisable()
  {
    stopTimeListener();
    this.gameManager.unloadGames();
    cleanDatabase();
    if ((isHologramsEnabled()) && (this.holographicInteraction != null)) {
      this.holographicInteraction.unloadHolograms();
    }
  }
  
  public void loadConfigInUTF()
  {
    File localFile = new File(getDataFolder(), "config.yml");
    if (!localFile.exists()) {
      return;
    }
    try
    {
      BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(localFile), "UTF-8"));
      getConfig().load(localBufferedReader);
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    if (getConfig() == null) {
      return;
    }
    this.breakableTypes = new ArrayList();
    Iterator localIterator = getConfig().getStringList("breakable-blocks").iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      if (!str.equalsIgnoreCase("none"))
      {
        Material localMaterial = Utils.parseMaterial(str);
        if ((localMaterial != null) && (!this.breakableTypes.contains(localMaterial))) {
          this.breakableTypes.add(localMaterial);
        }
      }
    }
  }
  
  public void loadShop()
  {
    File localFile = new File(getInstance().getDataFolder(), "shop.yml");
    if (!localFile.exists())
    {
      saveResource("shop.yml", false);
      try
      {
        Thread.sleep(100L);
      }
      catch (InterruptedException localInterruptedException)
      {
        localInterruptedException.printStackTrace();
      }
    }
    this.shopConfig = new YamlConfiguration();
    try
    {
      BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(localFile), "UTF-8"));
      this.shopConfig.load(localBufferedReader);
    }
    catch (Exception localException)
    {
      getServer().getConsoleSender().sendMessage(ChatWriter.pluginMessage(ChatColor.RED + "Couldn't load shop! Error in parsing shop!"));
      localException.printStackTrace();
    }
  }
  
  public void dispatchRewardCommands(List<String> paramList, Map<String, String> paramMap)
  {
    Iterator localIterator1 = paramList.iterator();
    while (localIterator1.hasNext())
    {
      String str = (String)localIterator1.next();
      str = str.trim();
      if (!str.equals(""))
      {
        if (str.equalsIgnoreCase("none")) {
          break;
        }
        if (str.startsWith("/")) {
          str = str.substring(1);
        }
        Iterator localIterator2 = paramMap.entrySet().iterator();
        while (localIterator2.hasNext())
        {
          Map.Entry localEntry = (Map.Entry)localIterator2.next();
          str = str.replace((CharSequence)localEntry.getKey(), (CharSequence)localEntry.getValue());
        }
        getInstance().getServer().dispatchCommand(getInstance().getServer().getConsoleSender(), str);
      }
    }
  }
  
  public void saveConfiguration()
  {
    File localFile = new File(getInstance().getDataFolder(), "config.yml");
    try
    {
      localFile.mkdirs();
      String str = getYamlDump((YamlConfiguration)getConfig());
      FileOutputStream localFileOutputStream = new FileOutputStream(localFile);
      OutputStreamWriter localOutputStreamWriter = new OutputStreamWriter(localFileOutputStream, "UTF-8");
      try
      {
        localOutputStreamWriter.write(str);
      }
      finally
      {
        localOutputStreamWriter.close();
        localFileOutputStream.close();
      }
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
  
  public Class<?> getVersionRelatedClass(String paramString)
  {
    try
    {
      Class localClass = Class.forName("io.github.yannici.bedwars.Com." + getCurrentVersion() + "." + paramString);
      return localClass;
    }
    catch (Exception localException)
    {
      getServer().getConsoleSender().sendMessage(ChatWriter.pluginMessage(ChatColor.RED + "Couldn't find version related class io.github.yannici.bedwars.Com." + getCurrentVersion() + "." + paramString));
    }
    return null;
  }
  
  public String getYamlDump(YamlConfiguration paramYamlConfiguration)
  {
    try
    {
      String str1 = paramYamlConfiguration.saveToString();
      String str2 = str1;
      str2 = Utils.unescape_perl_string(str1);
      return str2;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    return null;
  }
  
  public boolean isBreakableType(Material paramMaterial)
  {
    return this.breakableTypes.contains(paramMaterial);
  }
  
  public boolean isMineshafterPresent()
  {
    try
    {
      Class.forName("mineshafter.MineServer");
      return true;
    }
    catch (Exception localException) {}
    return false;
  }
  
  public PlayerStatisticManager getPlayerStatisticManager()
  {
    return this.playerStatisticManager;
  }
  
  private void checkUpdates()
  {
    try
    {
      if (getBooleanConfig("check-updates", true)) {
        this.updateChecker = new BukkitRunnable()
        {
          public void run()
          {
            final 1 local1 = this;
            PluginUpdater.UpdateCallback local11 = new PluginUpdater.UpdateCallback()
            {
              public void onFinish(PluginUpdater paramAnonymous2PluginUpdater)
              {
                if (paramAnonymous2PluginUpdater.getResult() == PluginUpdater.UpdateResult.SUCCESS) {
                  local1.cancel();
                }
              }
            };
            new PluginUpdater(Main.getInstance(), Main.PROJECT_ID, Main.getInstance().getFile(), PluginUpdater.UpdateType.DEFAULT, local11, Main.getInstance().getBooleanConfig("update-infos", true));
          }
        }.runTaskTimerAsynchronously(getInstance(), 40L, 36000L);
      }
    }
    catch (Exception localException)
    {
      getServer().getConsoleSender().sendMessage(ChatWriter.pluginMessage(ChatColor.RED + "Check for updates not successful: Error!"));
    }
  }
  
  private LocalizationConfig loadLocalization()
  {
    LocalizationConfig localLocalizationConfig = new LocalizationConfig();
    localLocalizationConfig.saveLocales(false);
    localLocalizationConfig.loadLocale(getConfig().getString("locale"), false);
    return localLocalizationConfig;
  }
  
  private void loadStatistics()
  {
    this.playerStatisticManager = new PlayerStatisticManager();
    this.playerStatisticManager.initialize();
  }
  
  private void loadDatabase()
  {
    if ((!getBooleanConfig("statistics.enabled", false)) || (!getStringConfig("statistics.storage", "yaml").equals("database"))) {
      return;
    }
    getServer().getConsoleSender().sendMessage(ChatWriter.pluginMessage(ChatColor.GREEN + "Initialize database ..."));
    loadingRequiredLibs();
    String str1 = getStringConfig("database.host", null);
    int i = getIntConfig("database.port", 3306);
    String str2 = getStringConfig("database.user", null);
    String str3 = getStringConfig("database.password", null);
    String str4 = getStringConfig("database.db", null);
    if ((str1 == null) || (str2 == null) || (str3 == null) || (str4 == null)) {
      return;
    }
    this.dbManager = new DatabaseManager(str1, i, str2, str3, str4);
    this.dbManager.initialize();
    getServer().getConsoleSender().sendMessage(ChatWriter.pluginMessage(ChatColor.GREEN + "Update database ..."));
    new DatabaseUpdater().execute();
    getServer().getConsoleSender().sendMessage(ChatWriter.pluginMessage(ChatColor.GREEN + "Done."));
  }
  
  public StorageType getStatisticStorageType()
  {
    String str = getStringConfig("statistics.storage", "yaml");
    return StorageType.getByName(str);
  }
  
  public boolean statisticsEnabled()
  {
    return getBooleanConfig("statistics.enabled", false);
  }
  
  private void cleanDatabase()
  {
    if (this.dbManager != null) {
      this.dbManager.cleanUp();
    }
  }
  
  private void loadingRequiredLibs()
  {
    try
    {
      File[] arrayOfFile1 = { new File(getDataFolder() + "/lib/", "c3p0-0.9.5.jar"), new File(getDataFolder() + "/lib/", "mchange-commons-java-0.2.9.jar") };
      File localFile;
      for (localFile : arrayOfFile1) {
        if (!localFile.exists()) {
          JarUtils.extractFromJar(localFile.getName(), localFile.getAbsolutePath());
        }
      }
      for (localFile : arrayOfFile1)
      {
        if (!localFile.exists())
        {
          getLogger().warning("There was a critical error loading bedwars plugin! Could not find lib: " + localFile.getName());
          Bukkit.getServer().getPluginManager().disablePlugin(this);
          return;
        }
        addClassPath(JarUtils.getJarUrl(localFile));
      }
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
  
  private void addClassPath(URL paramURL)
    throws IOException
  {
    URLClassLoader localURLClassLoader1 = (URLClassLoader)ClassLoader.getSystemClassLoader();
    URLClassLoader localURLClassLoader2 = URLClassLoader.class;
    try
    {
      Method localMethod = localURLClassLoader2.getDeclaredMethod("addURL", new Class[] { URL.class });
      localMethod.setAccessible(true);
      localMethod.invoke(localURLClassLoader1, new Object[] { paramURL });
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
      throw new IOException("Error adding " + paramURL + " to system classloader");
    }
  }
  
  public DatabaseManager getDatabaseManager()
  {
    return this.dbManager;
  }
  
  public boolean isSpigot()
  {
    return this.isSpigot;
  }
  
  private boolean getIsSpigot()
  {
    try
    {
      Package localPackage = Package.getPackage("org.spigotmc");
      return localPackage != null;
    }
    catch (Exception localException) {}
    return false;
  }
  
  public int getIntConfig(String paramString, int paramInt)
  {
    FileConfiguration localFileConfiguration = getConfig();
    if ((localFileConfiguration.contains(paramString)) && (localFileConfiguration.isInt(paramString))) {
      return localFileConfiguration.getInt(paramString);
    }
    return paramInt;
  }
  
  public String getStringConfig(String paramString1, String paramString2)
  {
    FileConfiguration localFileConfiguration = getConfig();
    if ((localFileConfiguration.contains(paramString1)) && (localFileConfiguration.isString(paramString1))) {
      return localFileConfiguration.getString(paramString1);
    }
    return paramString2;
  }
  
  public boolean getBooleanConfig(String paramString, boolean paramBoolean)
  {
    FileConfiguration localFileConfiguration = getConfig();
    if ((localFileConfiguration.contains(paramString)) && (localFileConfiguration.isBoolean(paramString))) {
      return localFileConfiguration.getBoolean(paramString);
    }
    return paramBoolean;
  }
  
  public LocalizationConfig getLocalization()
  {
    return this.localization;
  }
  
  private String loadVersion()
  {
    String str = Bukkit.getServer().getClass().getPackage().getName();
    return str.substring(str.lastIndexOf(46) + 1);
  }
  
  public String getCurrentVersion()
  {
    return this.version;
  }
  
  public boolean isBungee()
  {
    return getConfig().getBoolean("bungeecord.enabled");
  }
  
  public String getBungeeHub()
  {
    if (getConfig().contains("bungeecord.hubserver")) {
      return getConfig().getString("bungeecord.hubserver");
    }
    return null;
  }
  
  public Package getCraftBukkit()
  {
    try
    {
      if (this.craftbukkit == null) {
        return Package.getPackage("org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3]);
      }
      return this.craftbukkit;
    }
    catch (Exception localException)
    {
      getServer().getConsoleSender().sendMessage(ChatWriter.pluginMessage(ChatColor.RED + _l("errors.packagenotfound", ImmutableMap.of("package", "craftbukkit"))));
    }
    return null;
  }
  
  public Package getMinecraftPackage()
  {
    try
    {
      if (this.minecraft == null) {
        return Package.getPackage("net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3]);
      }
      return this.minecraft;
    }
    catch (Exception localException)
    {
      getServer().getConsoleSender().sendMessage(ChatWriter.pluginMessage(ChatColor.RED + _l("errors.packagenotfound", ImmutableMap.of("package", "minecraft server"))));
    }
    return null;
  }
  
  public Class getCraftBukkitClass(String paramString)
  {
    try
    {
      if (this.craftbukkit == null) {
        this.craftbukkit = getCraftBukkit();
      }
      return Class.forName(this.craftbukkit.getName() + "." + paramString);
    }
    catch (Exception localException)
    {
      getServer().getConsoleSender().sendMessage(ChatWriter.pluginMessage(ChatColor.RED + _l("errors.classnotfound", ImmutableMap.of("package", "craftbukkit", "class", paramString))));
    }
    return null;
  }
  
  public Class getMinecraftServerClass(String paramString)
  {
    try
    {
      if (this.minecraft == null) {
        this.minecraft = getMinecraftPackage();
      }
      return Class.forName(this.minecraft.getName() + "." + paramString);
    }
    catch (Exception localException)
    {
      getServer().getConsoleSender().sendMessage(ChatWriter.pluginMessage(ChatColor.RED + _l("errors.classnotfound", ImmutableMap.of("package", "minecraft server", "class", paramString))));
    }
    return null;
  }
  
  public GameLobbyCountdownRule getLobbyCountdownRule()
  {
    int i = 0;
    if ((getConfig().contains("lobbycountdown-rule")) && (getConfig().isInt("lobbycountdown-rule"))) {
      i = getConfig().getInt("lobbycountdown-rule");
    }
    return GameLobbyCountdownRule.getById(i);
  }
  
  public boolean metricsEnabled()
  {
    if ((getConfig().contains("plugin-metrics")) && (getConfig().isBoolean("plugin-metrics"))) {
      return getConfig().getBoolean("plugin-metrics");
    }
    return false;
  }
  
  public void startMetricsIfEnabled()
  {
    if (metricsEnabled()) {
      try
      {
        Metrics localMetrics = new Metrics(this);
        localMetrics.start();
      }
      catch (Exception localException)
      {
        getServer().getConsoleSender().sendMessage(ChatWriter.pluginMessage(ChatColor.RED + "Metrics are enabled, but couldn't send data!"));
      }
    }
  }
  
  public String getFallbackLocale()
  {
    return "en";
  }
  
  public boolean allPlayersBackToMainLobby()
  {
    if ((getConfig().contains("endgame.all-players-to-mainlobby")) && (getConfig().isBoolean("endgame.all-players-to-mainlobby"))) {
      return getConfig().getBoolean("endgame.all-players-to-mainlobby");
    }
    return false;
  }
  
  public List<String> getAllowedCommands()
  {
    FileConfiguration localFileConfiguration = getConfig();
    if ((localFileConfiguration.contains("allowed-commands")) && (localFileConfiguration.isList("allowed-commands"))) {
      return localFileConfiguration.getStringList("allowed-commands");
    }
    return new ArrayList();
  }
  
  public static Main getInstance()
  {
    return instance;
  }
  
  public ScoreboardManager getScoreboardManager()
  {
    return this.scoreboardManager;
  }
  
  private void registerListener()
  {
    new WeatherListener();
    new BlockListener();
    new PlayerListener();
    if (Utils.isSupportingTitles()) {
      new Player18Listener();
    }
    EntityListener localEntityListener = new EntityListener();
    if (Utils.isSupportingTitles()) {
      new Entity18Listener(localEntityListener);
    }
    new HangingListener();
    new ServerListener();
    new SignListener();
    new ChunkListener();
    SpecialItem.loadSpecials();
  }
  
  private void registerConfigurationClasses()
  {
    ConfigurationSerialization.registerClass(RessourceSpawner.class, "RessourceSpawner");
    ConfigurationSerialization.registerClass(Team.class, "Team");
  }
  
  private void registerCommands()
  {
    this.commands.add(new HelpCommand(this));
    this.commands.add(new SetSpawnerCommand(this));
    this.commands.add(new AddGameCommand(this));
    this.commands.add(new StartGameCommand(this));
    this.commands.add(new StopGameCommand(this));
    this.commands.add(new SetRegionCommand(this));
    this.commands.add(new AddTeamCommand(this));
    this.commands.add(new SaveGameCommand(this));
    this.commands.add(new JoinGameCommand(this));
    this.commands.add(new SetSpawnCommand(this));
    this.commands.add(new SetLobbyCommand(this));
    this.commands.add(new LeaveGameCommand(this));
    this.commands.add(new SetTargetCommand(this));
    this.commands.add(new SetBedCommand(this));
    this.commands.add(new ReloadCommand(this));
    this.commands.add(new SetMainLobbyCommand(this));
    this.commands.add(new ListGamesCommand(this));
    this.commands.add(new RegionNameCommand(this));
    this.commands.add(new RemoveTeamCommand(this));
    this.commands.add(new RemoveGameCommand(this));
    this.commands.add(new ClearSpawnerCommand(this));
    this.commands.add(new GameTimeCommand(this));
    this.commands.add(new StatsCommand(this));
    this.commands.add(new SetMinPlayersCommand(this));
    this.commands.add(new SetGameBlockCommand(this));
    this.commands.add(new SetBuilderCommand(this));
    this.commands.add(new SetAutobalanceCommand(this));
    this.commands.add(new KickCommand(this));
    this.commands.add(new AddTeamJoinCommand(this));
    this.commands.add(new AddHoloCommand(this));
    this.commands.add(new RemoveHoloCommand(this));
    getCommand(getStringConfig("command-prefix", "bw")).setExecutor(new BedwarsCommandExecutor(this));
  }
  
  public ArrayList<BaseCommand> getCommands()
  {
    return this.commands;
  }
  
  private ArrayList<BaseCommand> filterCommandsByPermission(ArrayList<BaseCommand> paramArrayList, String paramString)
  {
    Iterator localIterator = paramArrayList.iterator();
    while (localIterator.hasNext())
    {
      BaseCommand localBaseCommand = (BaseCommand)localIterator.next();
      if (!localBaseCommand.getPermission().equals(paramString)) {
        localIterator.remove();
      }
    }
    return paramArrayList;
  }
  
  public ArrayList<BaseCommand> getBaseCommands()
  {
    ArrayList localArrayList = (ArrayList)this.commands.clone();
    localArrayList = filterCommandsByPermission(localArrayList, "base");
    return localArrayList;
  }
  
  public ArrayList<BaseCommand> getSetupCommands()
  {
    ArrayList localArrayList = (ArrayList)this.commands.clone();
    localArrayList = filterCommandsByPermission(localArrayList, "setup");
    return localArrayList;
  }
  
  public ArrayList<BaseCommand> getCommandsByPermission(String paramString)
  {
    ArrayList localArrayList = (ArrayList)this.commands.clone();
    localArrayList = filterCommandsByPermission(localArrayList, paramString);
    return localArrayList;
  }
  
  public GameManager getGameManager()
  {
    return this.gameManager;
  }
  
  private void startTimeListener()
  {
    this.timeTask = getServer().getScheduler().runTaskTimer(this, new Runnable()
    {
      public void run()
      {
        Iterator localIterator = Main.getInstance().getGameManager().getGames().iterator();
        while (localIterator.hasNext())
        {
          Game localGame = (Game)localIterator.next();
          if (localGame.getState() == GameState.RUNNING) {
            localGame.getRegion().getWorld().setTime(localGame.getTime());
          }
        }
      }
    }, 100L, 100L);
  }
  
  public static String _l(String paramString1, String paramString2, Map<String, String> paramMap)
  {
    if (((String)paramMap.get(paramString2)).equals("1")) {
      return (String)getInstance().getLocalization().get(paramString1 + "-one", paramMap);
    }
    return (String)getInstance().getLocalization().get(paramString1, paramMap);
  }
  
  public static String _l(String paramString, Map<String, String> paramMap)
  {
    return (String)getInstance().getLocalization().get(paramString, paramMap);
  }
  
  public static String _l(String paramString)
  {
    return (String)getInstance().getLocalization().get(paramString);
  }
  
  private void stopTimeListener()
  {
    try
    {
      this.timeTask.cancel();
    }
    catch (Exception localException1) {}
    try
    {
      this.updateChecker.cancel();
    }
    catch (Exception localException2) {}
  }
  
  public void reloadLocalization()
  {
    this.localization.saveLocales(false);
    this.localization.loadLocale(getConfig().getString("locale"), false);
  }
  
  public boolean spectationEnabled()
  {
    if ((getConfig().contains("spectation-enabled")) && (getConfig().isBoolean("spectation-enabled"))) {
      return getConfig().getBoolean("spectation-enabled");
    }
    return true;
  }
  
  public boolean toMainLobby()
  {
    if (getConfig().contains("endgame.mainlobby-enabled")) {
      return getConfig().getBoolean("endgame.mainlobby-enabled");
    }
    return false;
  }
  
  public int getMaxLength()
  {
    if ((getConfig().contains("gamelength")) && (getConfig().isInt("gamelength"))) {
      return getConfig().getInt("gamelength") * 60;
    }
    return 3600;
  }
  
  public Integer getRespawnProtectionTime()
  {
    FileConfiguration localFileConfiguration = getConfig();
    if ((localFileConfiguration.contains("respawn-protection")) && (localFileConfiguration.isInt("respawn-protection"))) {
      return Integer.valueOf(localFileConfiguration.getInt("respawn-protection"));
    }
    return Integer.valueOf(0);
  }
  
  public boolean isLocationSerializable()
  {
    if (locationSerializable == null) {
      try
      {
        Location.class.getMethod("serialize", new Class[0]);
        locationSerializable = Boolean.valueOf(true);
      }
      catch (Exception localException)
      {
        locationSerializable = Boolean.valueOf(false);
      }
    }
    return locationSerializable.booleanValue();
  }
  
  public FileConfiguration getShopConfig()
  {
    return this.shopConfig;
  }
  
  public boolean isHologramsEnabled()
  {
    return getServer().getPluginManager().isPluginEnabled("HolographicDisplays");
  }
  
  public HolographicDisplaysInteraction getHolographicInteractor()
  {
    return this.holographicInteraction;
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Main
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */