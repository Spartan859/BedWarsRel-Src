package io.github.yannici.bedwars.Game;

import com.google.common.collect.ImmutableMap;
import io.github.yannici.bedwars.ChatWriter;
import io.github.yannici.bedwars.Main;
import io.github.yannici.bedwars.Utils;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.ScoreboardManager;

public class GameManager
{
  public static String gamesPath = "games";
  private ArrayList<Game> games = null;
  private Map<Player, Game> gamePlayer = null;
  
  public Game addGame(String paramString)
  {
    Game localGame1 = getGame(paramString);
    if (localGame1 != null) {
      return null;
    }
    Game localGame2 = new Game(paramString);
    this.games.add(localGame2);
    return localGame2;
  }
  
  public Game getGameOfPlayer(Player paramPlayer)
  {
    return (Game)this.gamePlayer.get(paramPlayer);
  }
  
  public void addGamePlayer(Player paramPlayer, Game paramGame)
  {
    if (this.gamePlayer.containsKey(paramPlayer)) {
      this.gamePlayer.remove(paramPlayer);
    }
    this.gamePlayer.put(paramPlayer, paramGame);
  }
  
  public void removeGamePlayer(Player paramPlayer)
  {
    this.gamePlayer.remove(paramPlayer);
  }
  
  public ArrayList<Game> getGames()
  {
    return this.games;
  }
  
  public Game getGame(String paramString)
  {
    Iterator localIterator = this.games.iterator();
    while (localIterator.hasNext())
    {
      Game localGame = (Game)localIterator.next();
      if (localGame.getName().equals(paramString)) {
        return localGame;
      }
    }
    return null;
  }
  
  public void reloadGames()
  {
    unloadGames();
    this.gamePlayer.clear();
    loadGames();
  }
  
  public void removeGame(Game paramGame)
  {
    if (paramGame == null) {
      return;
    }
    File localFile = new File(Main.getInstance().getDataFolder() + File.separator + gamesPath + File.separator + paramGame.getName());
    if (localFile.exists()) {
      localFile.delete();
    }
    this.games.remove(paramGame);
  }
  
  public void unloadGame(Game paramGame)
  {
    if (paramGame.getState() != GameState.STOPPED) {
      paramGame.stop();
    }
    paramGame.setState(GameState.STOPPED);
    paramGame.setScoreboard(Main.getInstance().getScoreboardManager().getNewScoreboard());
    paramGame.kickAllPlayers();
    paramGame.resetRegion();
    paramGame.updateSigns();
  }
  
  public void loadGames()
  {
    String str = Main.getInstance().getDataFolder() + File.separator + gamesPath;
    File localFile1 = new File(str);
    if (!localFile1.exists()) {
      return;
    }
    File[] arrayOfFile1 = localFile1.listFiles(new FileFilter()
    {
      public boolean accept(File paramAnonymousFile)
      {
        return paramAnonymousFile.isDirectory();
      }
    });
    if (arrayOfFile1.length > 0) {
      for (Object localObject2 : arrayOfFile1)
      {
        File[] arrayOfFile2 = localObject2.listFiles();
        for (File localFile2 : arrayOfFile2) {
          if ((localFile2.isFile()) && (localFile2.getName().equals("game.yml"))) {
            loadGame(localFile2);
          }
        }
      }
    }
    ??? = this.games.iterator();
    while (((Iterator)???).hasNext())
    {
      Game localGame = (Game)((Iterator)???).next();
      if (!localGame.run(Main.getInstance().getServer().getConsoleSender())) {
        Main.getInstance().getServer().getConsoleSender().sendMessage(ChatWriter.pluginMessage(ChatColor.RED + Main._l("errors.gamenotloaded")));
      } else {
        localGame.getCycle().onGameLoaded();
      }
    }
  }
  
  private void loadGame(File paramFile)
  {
    try
    {
      YamlConfiguration localYamlConfiguration = YamlConfiguration.loadConfiguration(paramFile);
      String str1 = localYamlConfiguration.get("name").toString();
      if (str1.isEmpty()) {
        return;
      }
      Game localGame = new Game(str1);
      localGame.setConfig(localYamlConfiguration);
      Object localObject1 = new HashMap();
      Object localObject2 = new HashMap();
      String str2 = null;
      if (localYamlConfiguration.contains("teams")) {
        localObject1 = localYamlConfiguration.getConfigurationSection("teams").getValues(false);
      }
      if (localYamlConfiguration.contains("spawner"))
      {
        if (localYamlConfiguration.isConfigurationSection("spawner"))
        {
          localObject2 = localYamlConfiguration.getConfigurationSection("spawner").getValues(false);
          localObject3 = ((Map)localObject2).values().iterator();
          while (((Iterator)localObject3).hasNext())
          {
            localObject4 = ((Iterator)localObject3).next();
            if ((localObject4 instanceof RessourceSpawner))
            {
              localObject5 = (RessourceSpawner)localObject4;
              ((RessourceSpawner)localObject5).setGame(localGame);
              localGame.addRessourceSpawner((RessourceSpawner)localObject5);
            }
          }
        }
        if (localYamlConfiguration.isList("spawner"))
        {
          localObject3 = localYamlConfiguration.getList("spawner").iterator();
          while (((Iterator)localObject3).hasNext())
          {
            localObject4 = ((Iterator)localObject3).next();
            if ((localObject4 instanceof RessourceSpawner))
            {
              localObject5 = (RessourceSpawner)localObject4;
              ((RessourceSpawner)localObject5).setGame(localGame);
              localGame.addRessourceSpawner((RessourceSpawner)localObject5);
            }
          }
        }
      }
      Object localObject3 = ((Map)localObject1).values().iterator();
      while (((Iterator)localObject3).hasNext())
      {
        localObject4 = ((Iterator)localObject3).next();
        if ((localObject4 instanceof Team)) {
          localGame.addTeam((Team)localObject4);
        }
      }
      localObject3 = Utils.locationDeserialize(localYamlConfiguration.get("loc1"));
      Object localObject4 = Utils.locationDeserialize(localYamlConfiguration.get("loc2"));
      Object localObject5 = new File(Main.getInstance().getDataFolder() + File.separator + gamesPath + File.separator + localGame.getName(), "sign.yml");
      List localList;
      Iterator localIterator;
      Object localObject7;
      if (((File)localObject5).exists())
      {
        localObject6 = YamlConfiguration.loadConfiguration((File)localObject5);
        localList = (List)((YamlConfiguration)localObject6).get("signs");
        localIterator = localList.iterator();
        while (localIterator.hasNext())
        {
          localObject7 = localIterator.next();
          Location localLocation = Utils.locationDeserialize(localObject7);
          if (localLocation != null)
          {
            localLocation.getChunk().load(true);
            Block localBlock = localLocation.getBlock();
            if ((localBlock.getState() instanceof Sign))
            {
              localBlock.getState().update(true, true);
              localGame.addJoinSign(localBlock.getLocation());
            }
          }
        }
      }
      localGame.setLoc((Location)localObject3, "loc1");
      localGame.setLoc((Location)localObject4, "loc2");
      localGame.setLobby(Utils.locationDeserialize(localYamlConfiguration.get("lobby")));
      Object localObject6 = ((Location)localObject3).getWorld().getName();
      if (localYamlConfiguration.contains("regionname")) {
        localObject6 = localYamlConfiguration.getString("regionname");
      }
      if ((localYamlConfiguration.contains("time")) && (localYamlConfiguration.isInt("time"))) {
        localGame.setTime(localYamlConfiguration.getInt("time"));
      }
      localGame.setRegionName((String)localObject6);
      localGame.setRegion(new Region((Location)localObject3, (Location)localObject4, (String)localObject6));
      if (localYamlConfiguration.contains("autobalance")) {
        localGame.setAutobalance(localYamlConfiguration.getBoolean("autobalance"));
      }
      if (localYamlConfiguration.contains("minplayers")) {
        localGame.setMinPlayers(localYamlConfiguration.getInt("minplayers"));
      }
      if (localYamlConfiguration.contains("mainlobby")) {
        localGame.setMainLobby(Utils.locationDeserialize(localYamlConfiguration.get("mainlobby")));
      }
      if (localYamlConfiguration.contains("record")) {
        localGame.setRecord(localYamlConfiguration.getInt("record", Main.getInstance().getMaxLength()));
      }
      if (localYamlConfiguration.contains("targetmaterial"))
      {
        str2 = localYamlConfiguration.getString("targetmaterial");
        if ((str2 != null) && (!str2.equals(""))) {
          localGame.setTargetMaterial(Utils.parseMaterial(str2));
        }
      }
      if (localYamlConfiguration.contains("builder")) {
        localGame.setBuilder(localYamlConfiguration.getString("builder"));
      }
      if (localYamlConfiguration.contains("record-holders"))
      {
        localList = localYamlConfiguration.getList("record-holders", new ArrayList());
        localIterator = localList.iterator();
        while (localIterator.hasNext())
        {
          localObject7 = localIterator.next();
          localGame.addRecordHolder(localObject7.toString());
        }
      }
      localGame.getFreePlayers().clear();
      localGame.updateSigns();
      this.games.add(localGame);
      Main.getInstance().getServer().getConsoleSender().sendMessage(ChatWriter.pluginMessage(ChatColor.GREEN + Main._l("success.gameloaded", ImmutableMap.of("game", localGame.getName()))));
    }
    catch (Exception localException)
    {
      Main.getInstance().getServer().getConsoleSender().sendMessage(ChatWriter.pluginMessage(ChatColor.RED + Main._l("errors.gameloaderror", ImmutableMap.of("game", paramFile.getParentFile().getName()))));
    }
  }
  
  public void unloadGames()
  {
    Iterator localIterator = this.games.iterator();
    while (localIterator.hasNext())
    {
      Game localGame = (Game)localIterator.next();
      unloadGame(localGame);
    }
    this.games.clear();
  }
  
  public Game getGameByLocation(Location paramLocation)
  {
    Iterator localIterator = this.games.iterator();
    while (localIterator.hasNext())
    {
      Game localGame = (Game)localIterator.next();
      if ((localGame.getRegion() != null) && (localGame.getRegion().getWorld() != null)) {
        if (localGame.getRegion().isInRegion(paramLocation)) {
          return localGame;
        }
      }
    }
    return null;
  }
  
  public Game getGameBySignLocation(Location paramLocation)
  {
    Iterator localIterator = this.games.iterator();
    while (localIterator.hasNext())
    {
      Game localGame = (Game)localIterator.next();
      if (localGame.getSigns().containsKey(paramLocation)) {
        return localGame;
      }
    }
    return null;
  }
  
  public List<Game> getGamesByWorld(World paramWorld)
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = this.games.iterator();
    while (localIterator.hasNext())
    {
      Game localGame = (Game)localIterator.next();
      if ((localGame.getRegion() != null) && (localGame.getRegion().getWorld() != null)) {
        if (localGame.getRegion().getWorld().equals(paramWorld)) {
          localArrayList.add(localGame);
        }
      }
    }
    return localArrayList;
  }
  
  public Game getGameByChunkLocation(int paramInt1, int paramInt2)
  {
    Iterator localIterator = this.games.iterator();
    while (localIterator.hasNext())
    {
      Game localGame = (Game)localIterator.next();
      if (localGame.getRegion().chunkIsInRegion(paramInt1, paramInt2)) {
        return localGame;
      }
    }
    return null;
  }
  
  public int getGamePlayerAmount()
  {
    return this.gamePlayer.size();
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Game.GameManager
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */