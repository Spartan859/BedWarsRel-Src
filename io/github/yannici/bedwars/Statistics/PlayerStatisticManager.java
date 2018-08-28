package io.github.yannici.bedwars.Statistics;

import io.github.yannici.bedwars.ChatWriter;
import io.github.yannici.bedwars.Database.DBField;
import io.github.yannici.bedwars.Database.DBGetField;
import io.github.yannici.bedwars.Database.DBSetField;
import io.github.yannici.bedwars.Database.DatabaseManager;
import io.github.yannici.bedwars.Game.Game;
import io.github.yannici.bedwars.Game.GameManager;
import io.github.yannici.bedwars.Main;
import java.io.File;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class PlayerStatisticManager
{
  private Map<OfflinePlayer, PlayerStatistic> playerStatistic = null;
  private FileConfiguration fileDatabase = null;
  private File databaseFile = null;
  
  public void initialize()
  {
    if (!Main.getInstance().getBooleanConfig("statistics.enabled", false)) {
      return;
    }
    if (Main.getInstance().getStatisticStorageType() == StorageType.YAML)
    {
      File localFile = new File(new StringBuilder().append(Main.getInstance().getDataFolder()).append("/database/").append(DatabaseManager.DBPrefix).append("stats_players").append(".yml").toString());
      loadYml(localFile);
    }
    if (Main.getInstance().getStatisticStorageType() == StorageType.DATABASE) {
      initializeDatabase();
    }
  }
  
  public void initializeDatabase()
  {
    Main.getInstance().getServer().getConsoleSender().sendMessage(ChatWriter.pluginMessage(new StringBuilder().append(ChatColor.GREEN).append("Loading Statistics from Database ...").toString()));
    String str = getTableSql(new PlayerStatistic());
    try
    {
      Main.getInstance().getDatabaseManager().execute(new String[] { str });
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    Main.getInstance().getServer().getConsoleSender().sendMessage(ChatWriter.pluginMessage(new StringBuilder().append(ChatColor.GREEN).append("Done.").toString()));
  }
  
  private String getTableSql(StoringTable paramStoringTable)
  {
    StringBuilder localStringBuilder = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
    String str = new StringBuilder().append(DatabaseManager.DBPrefix).append(paramStoringTable.getTableName()).toString();
    localStringBuilder.append(new StringBuilder().append("`").append(str).append("` (").toString());
    Iterator localIterator = paramStoringTable.getFields().values().iterator();
    while (localIterator.hasNext())
    {
      DBField localDBField = (DBField)localIterator.next();
      Method localMethod = localDBField.getGetter();
      DBGetField localDBGetField = (DBGetField)localMethod.getAnnotation(DBGetField.class);
      if (localDBGetField != null)
      {
        localStringBuilder.append(new StringBuilder().append("`").append(localDBGetField.name()).append("` ").toString());
        localStringBuilder.append(new StringBuilder().append(localDBGetField.dbType()).append(" ").toString());
        localStringBuilder.append(localDBGetField.notNull() ? "NOT NULL" : "NULL");
        if (!localDBGetField.defaultValue().equals("")) {
          localStringBuilder.append(new StringBuilder().append(" DEFAULT '").append(localDBGetField.defaultValue()).append("' ").toString());
        }
        if (localDBGetField.autoInc()) {
          localStringBuilder.append(" AUTO_INCREMENT");
        }
        localStringBuilder.append(",");
      }
    }
    if (paramStoringTable.getKeyField() != null) {
      localStringBuilder.append(new StringBuilder().append("UNIQUE (").append(paramStoringTable.getKeyField()).append("),").toString());
    }
    localStringBuilder.append("PRIMARY KEY (id)");
    localStringBuilder.append(");");
    return localStringBuilder.toString();
  }
  
  public void storeStatistic(PlayerStatistic paramPlayerStatistic)
  {
    if (Main.getInstance().getStatisticStorageType() == StorageType.YAML) {
      storeYamlStatistic(paramPlayerStatistic);
    } else {
      storeDatabaseStatistic(paramPlayerStatistic);
    }
  }
  
  private synchronized void storeYamlStatistic(PlayerStatistic paramPlayerStatistic)
  {
    String str1 = String.valueOf(paramPlayerStatistic.getValue(paramPlayerStatistic.getKeyField()));
    if ((str1 == null) || (str1.equals("null"))) {
      return;
    }
    try
    {
      Iterator localIterator = paramPlayerStatistic.getFields().keySet().iterator();
      while (localIterator.hasNext())
      {
        String str2 = (String)localIterator.next();
        this.fileDatabase.set(new StringBuilder().append("data.").append(str1).append(".").append(str2).toString(), paramPlayerStatistic.getValue(str2));
      }
      this.fileDatabase.save(this.databaseFile);
    }
    catch (Exception localException)
    {
      Main.getInstance().getServer().getConsoleSender().sendMessage(ChatWriter.pluginMessage(new StringBuilder().append(ChatColor.RED).append("Couldn't store statistic data for player with uuid: ").append(str1).toString()));
    }
  }
  
  private void storeDatabaseStatistic(PlayerStatistic paramPlayerStatistic)
  {
    if (!this.playerStatistic.containsKey(paramPlayerStatistic.getPlayer())) {
      return;
    }
    Object localObject1;
    if (paramPlayerStatistic.isNew())
    {
      localObject1 = null;
      try
      {
        String str1 = paramPlayerStatistic.getUUID();
        String str2 = new StringBuilder().append("SELECT id FROM `").append(DatabaseManager.DBPrefix).append(paramPlayerStatistic.getTableName()).append("` WHERE `uuid` = '").append(str1).append("'").toString();
        localObject1 = Main.getInstance().getDatabaseManager().query(str2);
        int i = Main.getInstance().getDatabaseManager().getRowCount((ResultSet)localObject1);
        if (i > 0)
        {
          ((ResultSet)localObject1).first();
          paramPlayerStatistic.setId(((ResultSet)localObject1).getLong(0));
        }
        if (localObject1 != null) {
          try
          {
            Main.getInstance().getDatabaseManager().clean(((ResultSet)localObject1).getStatement().getConnection());
          }
          catch (Exception localException1)
          {
            localException1.printStackTrace();
          }
        }
        localObject1 = getStoreSQL(paramPlayerStatistic);
      }
      catch (Exception localException2) {}finally
      {
        if (localObject1 != null) {
          try
          {
            Main.getInstance().getDatabaseManager().clean(((ResultSet)localObject1).getStatement().getConnection());
          }
          catch (Exception localException4)
          {
            localException4.printStackTrace();
          }
        }
      }
    }
    Main.getInstance().getDatabaseManager().update((String)localObject1);
  }
  
  private String getStoreSQL(StoringTable paramStoringTable)
  {
    StringBuilder localStringBuilder1 = new StringBuilder();
    StringBuilder localStringBuilder2;
    Object localObject1;
    Object localObject2;
    Object localObject3;
    if (paramStoringTable.isNew())
    {
      localStringBuilder2 = new StringBuilder();
      localObject1 = new StringBuilder();
      localStringBuilder1.append(new StringBuilder().append("INSERT INTO `").append(DatabaseManager.DBPrefix).append(paramStoringTable.getTableName()).append("` (").toString());
      localObject2 = paramStoringTable.getFields().values().iterator();
      while (((Iterator)localObject2).hasNext())
      {
        localObject3 = (DBField)((Iterator)localObject2).next();
        DBGetField localDBGetField = (DBGetField)((DBField)localObject3).getGetter().getAnnotation(DBGetField.class);
        if ((localDBGetField != null) && (!localDBGetField.name().equals("id")))
        {
          localStringBuilder2.append(new StringBuilder().append("`").append(localDBGetField.name()).append("`,").toString());
          ((StringBuilder)localObject1).append(new StringBuilder().append("'").append(paramStoringTable.getValue(localDBGetField.name())).append("',").toString());
        }
      }
      localObject2 = localStringBuilder2.toString();
      localObject3 = ((StringBuilder)localObject1).toString();
      localObject2 = ((String)localObject2).trim().substring(0, ((String)localObject2).length() - 1);
      localObject3 = ((String)localObject3).trim().substring(0, ((String)localObject3).length() - 1);
      localStringBuilder1.append(new StringBuilder().append((String)localObject2).append(") VALUES (").toString());
      localStringBuilder1.append(new StringBuilder().append((String)localObject3).append(")").toString());
    }
    else
    {
      localStringBuilder2 = new StringBuilder();
      localStringBuilder1.append(new StringBuilder().append("UPDATE `").append(DatabaseManager.DBPrefix).append(paramStoringTable.getTableName()).append("` SET ").toString());
      localObject1 = paramStoringTable.getFields().values().iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (DBField)((Iterator)localObject1).next();
        localObject3 = (DBGetField)((DBField)localObject2).getGetter().getAnnotation(DBGetField.class);
        if ((localObject3 != null) && (!((DBGetField)localObject3).name().equals("id")) && (!((DBGetField)localObject3).name().equals(paramStoringTable.getKeyField()))) {
          localStringBuilder2.append(new StringBuilder().append("`").append(((DBGetField)localObject3).name()).append("` = '").append(paramStoringTable.getValue(((DBGetField)localObject3).name())).append("',").toString());
        }
      }
      localObject1 = localStringBuilder2.toString().trim();
      localObject1 = ((String)localObject1).substring(0, ((String)localObject1).length() - 1);
      localStringBuilder1.append(new StringBuilder().append((String)localObject1).append(" WHERE `").append(paramStoringTable.getKeyField()).append("` = '").append(paramStoringTable.getValue(paramStoringTable.getKeyField())).append("'").toString());
    }
    return localStringBuilder1.toString();
  }
  
  public void loadStatistic(PlayerStatistic paramPlayerStatistic)
  {
    if (Main.getInstance().getStatisticStorageType() == StorageType.YAML) {
      loadYamlStatistic(paramPlayerStatistic);
    } else {
      loadDatabaseStatistic(paramPlayerStatistic);
    }
  }
  
  private void loadYamlStatistic(PlayerStatistic paramPlayerStatistic)
  {
    String str;
    if (this.playerStatistic.containsKey(paramPlayerStatistic.getPlayer()))
    {
      localObject = (PlayerStatistic)this.playerStatistic.get(paramPlayerStatistic.getPlayer());
      localIterator = paramPlayerStatistic.getFields().keySet().iterator();
      while (localIterator.hasNext())
      {
        str = (String)localIterator.next();
        if (!str.equalsIgnoreCase("id")) {
          paramPlayerStatistic.setValue(str, ((PlayerStatistic)localObject).getValue(str));
        }
      }
      paramPlayerStatistic.setId(1L);
      return;
    }
    Object localObject = paramPlayerStatistic.getValue(paramPlayerStatistic.getKeyField()).toString();
    paramPlayerStatistic.setDefault();
    if (this.fileDatabase == null)
    {
      this.playerStatistic.put(paramPlayerStatistic.getPlayer(), paramPlayerStatistic);
      return;
    }
    if (!this.fileDatabase.contains(new StringBuilder().append("data.").append((String)localObject).toString()))
    {
      this.playerStatistic.put(paramPlayerStatistic.getPlayer(), paramPlayerStatistic);
      return;
    }
    Iterator localIterator = paramPlayerStatistic.getFields().keySet().iterator();
    while (localIterator.hasNext())
    {
      str = (String)localIterator.next();
      if ((!str.equalsIgnoreCase("id")) && (this.fileDatabase.contains(new StringBuilder().append("data.").append((String)localObject).append(".").append(str).toString()))) {
        paramPlayerStatistic.setValue(str, this.fileDatabase.get(new StringBuilder().append("data.").append((String)localObject).append(".").append(str).toString()));
      }
    }
    paramPlayerStatistic.setId(1L);
    this.playerStatistic.put(paramPlayerStatistic.getPlayer(), paramPlayerStatistic);
  }
  
  public void unloadStatistic(OfflinePlayer paramOfflinePlayer)
  {
    if (Main.getInstance().getStatisticStorageType() != StorageType.YAML) {
      this.playerStatistic.remove(paramOfflinePlayer);
    }
  }
  
  private void loadDatabaseStatistic(PlayerStatistic paramPlayerStatistic)
  {
    if (this.playerStatistic.containsKey(paramPlayerStatistic.getPlayer())) {
      return;
    }
    ResultSet localResultSet = null;
    Game localGame = null;
    try
    {
      localResultSet = Main.getInstance().getDatabaseManager().query(new StringBuilder().append("SELECT * FROM `").append(DatabaseManager.DBPrefix).append(paramPlayerStatistic.getTableName()).append("`").append(" WHERE `").append(paramPlayerStatistic.getKeyField()).append("` = '").append(paramPlayerStatistic.getValue(paramPlayerStatistic.getKeyField())).append("'").toString());
      if (Main.getInstance().getDatabaseManager().getRowCount(localResultSet) == 0)
      {
        paramPlayerStatistic.setDefault();
        this.playerStatistic.put(paramPlayerStatistic.getPlayer(), paramPlayerStatistic);
        return;
      }
      localResultSet.first();
      Iterator localIterator = paramPlayerStatistic.getFields().values().iterator();
      while (localIterator.hasNext())
      {
        DBField localDBField = (DBField)localIterator.next();
        if (localDBField.getSetter() != null)
        {
          DBSetField localDBSetField = (DBSetField)localDBField.getSetter().getAnnotation(DBSetField.class);
          if ((localDBSetField != null) && (localDBField.getSetter().getParameterTypes().length != 0)) {
            paramPlayerStatistic.setValue(localDBSetField.name(), localResultSet.getObject(localDBSetField.name()));
          }
        }
      }
      try
      {
        Main.getInstance().getDatabaseManager().clean(localResultSet.getStatement().getConnection());
      }
      catch (Exception localException2)
      {
        localException2.printStackTrace();
      }
      if (!paramPlayerStatistic.getPlayer().isOnline()) {
        break label423;
      }
    }
    catch (Exception localException3)
    {
      localException3.printStackTrace();
      return;
    }
    finally
    {
      try
      {
        Main.getInstance().getDatabaseManager().clean(localResultSet.getStatement().getConnection());
      }
      catch (Exception localException5)
      {
        localException5.printStackTrace();
      }
    }
    Player localPlayer = paramPlayerStatistic.getPlayer().getPlayer();
    localGame = Main.getInstance().getGameManager().getGameOfPlayer(localPlayer);
    label423:
    if (localGame == null)
    {
      paramPlayerStatistic.setOnce(true);
      return;
    }
    this.playerStatistic.put(paramPlayerStatistic.getPlayer(), paramPlayerStatistic);
  }
  
  public PlayerStatistic getStatistic(OfflinePlayer paramOfflinePlayer)
  {
    if (paramOfflinePlayer == null) {
      return null;
    }
    if (!this.playerStatistic.containsKey(paramOfflinePlayer))
    {
      PlayerStatistic localPlayerStatistic = new PlayerStatistic(paramOfflinePlayer);
      localPlayerStatistic.load();
      if (localPlayerStatistic.isOnce()) {
        return localPlayerStatistic;
      }
    }
    return (PlayerStatistic)this.playerStatistic.get(paramOfflinePlayer);
  }
  
  public FileConfiguration getDatabaseFile()
  {
    return this.fileDatabase;
  }
  
  private void loadYml(File paramFile)
  {
    try
    {
      Main.getInstance().getServer().getConsoleSender().sendMessage(ChatWriter.pluginMessage(new StringBuilder().append(ChatColor.GREEN).append("Loading statistics from YAML-File ...").toString()));
      YamlConfiguration localYamlConfiguration = null;
      HashMap localHashMap = new HashMap();
      this.databaseFile = paramFile;
      if (!paramFile.exists())
      {
        paramFile.getParentFile().mkdirs();
        paramFile.createNewFile();
        localYamlConfiguration = new YamlConfiguration();
        localYamlConfiguration.createSection("data");
        localYamlConfiguration.save(paramFile);
      }
      else
      {
        localYamlConfiguration = YamlConfiguration.loadConfiguration(paramFile);
      }
      this.fileDatabase = localYamlConfiguration;
      ConfigurationSection localConfigurationSection1 = localYamlConfiguration.getConfigurationSection("data");
      Iterator localIterator1 = localConfigurationSection1.getKeys(false).iterator();
      while (localIterator1.hasNext())
      {
        String str1 = (String)localIterator1.next();
        PlayerStatistic localPlayerStatistic = new PlayerStatistic();
        ConfigurationSection localConfigurationSection2 = localConfigurationSection1.getConfigurationSection(str1);
        Iterator localIterator2 = localPlayerStatistic.getFields().keySet().iterator();
        while (localIterator2.hasNext())
        {
          String str2 = (String)localIterator2.next();
          if ((!str2.equalsIgnoreCase("id")) && (localConfigurationSection2.contains(str2)))
          {
            Object localObject = localConfigurationSection2.get(str2);
            localPlayerStatistic.setValue(str2, localObject);
          }
        }
        localPlayerStatistic.setId(1L);
        localHashMap.put(Main.getInstance().getServer().getOfflinePlayer(UUID.fromString(str1)), localPlayerStatistic);
      }
      this.playerStatistic = localHashMap;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    Main.getInstance().getServer().getConsoleSender().sendMessage(ChatWriter.pluginMessage(new StringBuilder().append(ChatColor.GREEN).append("Done!").toString()));
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Statistics.PlayerStatisticManager
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */