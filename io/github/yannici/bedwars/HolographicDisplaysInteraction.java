package io.github.yannici.bedwars;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.VisibilityManager;
import com.gmail.filoghost.holographicdisplays.api.handler.TouchHandler;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import io.github.yannici.bedwars.Statistics.PlayerStatistic;
import io.github.yannici.bedwars.Statistics.PlayerStatisticManager;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

public class HolographicDisplaysInteraction
{
  private List<Location> hologramLocations = null;
  private Map<Player, List<Hologram>> holograms = null;
  
  public void unloadHolograms()
  {
    if (Main.getInstance().isHologramsEnabled())
    {
      Iterator localIterator = HologramsAPI.getHolograms(Main.getInstance()).iterator();
      while (localIterator.hasNext()) {
        ((Hologram)localIterator.next()).delete();
      }
    }
  }
  
  public List<Location> getHologramLocations()
  {
    return this.hologramLocations;
  }
  
  public void loadHolograms()
  {
    if (!Main.getInstance().isHologramsEnabled()) {
      return;
    }
    if ((this.holograms != null) && (this.hologramLocations != null)) {
      unloadHolograms();
    }
    this.holograms = new HashMap();
    this.hologramLocations = new ArrayList();
    File localFile = new File(Main.getInstance().getDataFolder(), "holodb.yml");
    if (localFile.exists())
    {
      YamlConfiguration localYamlConfiguration = YamlConfiguration.loadConfiguration(localFile);
      List localList = (List)localYamlConfiguration.get("locations");
      Iterator localIterator = localList.iterator();
      while (localIterator.hasNext())
      {
        Object localObject = localIterator.next();
        Location localLocation = Utils.locationDeserialize(localObject);
        if (localLocation != null) {
          this.hologramLocations.add(localLocation);
        }
      }
    }
    if (this.hologramLocations.size() == 0) {
      return;
    }
    updateHolograms();
  }
  
  public void updateHolograms()
  {
    Iterator localIterator = Main.getInstance().getServer().getOnlinePlayers().iterator();
    while (localIterator.hasNext())
    {
      final Player localPlayer = (Player)localIterator.next();
      Main.getInstance().getServer().getScheduler().runTask(Main.getInstance(), new Runnable()
      {
        public void run()
        {
          Iterator localIterator = HolographicDisplaysInteraction.this.hologramLocations.iterator();
          while (localIterator.hasNext())
          {
            Location localLocation = (Location)localIterator.next();
            HolographicDisplaysInteraction.this.updatePlayerHologram(localPlayer, localLocation);
          }
        }
      });
    }
  }
  
  public void updateHolograms(final Player paramPlayer)
  {
    Main.getInstance().getServer().getScheduler().runTask(Main.getInstance(), new Runnable()
    {
      public void run()
      {
        Iterator localIterator = HolographicDisplaysInteraction.this.hologramLocations.iterator();
        while (localIterator.hasNext())
        {
          Location localLocation = (Location)localIterator.next();
          HolographicDisplaysInteraction.this.updatePlayerHologram(paramPlayer, localLocation);
        }
      }
    });
  }
  
  public void updateHolograms(final Player paramPlayer, long paramLong)
  {
    Main.getInstance().getServer().getScheduler().runTaskLater(Main.getInstance(), new Runnable()
    {
      public void run()
      {
        HolographicDisplaysInteraction.this.updateHolograms(paramPlayer);
      }
    }, paramLong);
  }
  
  private void updatePlayerHologram(Player paramPlayer, Location paramLocation)
  {
    List localList = null;
    if (!this.holograms.containsKey(paramPlayer)) {
      this.holograms.put(paramPlayer, new ArrayList());
    }
    localList = (List)this.holograms.get(paramPlayer);
    Hologram localHologram = getHologramByLocation(localList, paramLocation);
    if ((localHologram == null) && (paramPlayer.getWorld() == paramLocation.getWorld())) {
      localList.add(createPlayerStatisticHologram(paramPlayer, paramLocation));
    } else if (localHologram != null) {
      if (localHologram.getLocation().getWorld() == paramPlayer.getWorld())
      {
        updatePlayerStatisticHologram(paramPlayer, localHologram);
      }
      else
      {
        localList.remove(localHologram);
        localHologram.delete();
      }
    }
  }
  
  public List<Hologram> getHolograms(Player paramPlayer)
  {
    return (List)this.holograms.get(paramPlayer);
  }
  
  public Map<Player, List<Hologram>> getHolograms()
  {
    return this.holograms;
  }
  
  private void updateHologramDatabase()
  {
    try
    {
      File localFile = new File(Main.getInstance().getDataFolder(), "holodb.yml");
      YamlConfiguration localYamlConfiguration = new YamlConfiguration();
      ArrayList localArrayList = new ArrayList();
      Iterator localIterator = this.hologramLocations.iterator();
      while (localIterator.hasNext())
      {
        Location localLocation = (Location)localIterator.next();
        localArrayList.add(Utils.locationSerialize(localLocation));
      }
      if (!localFile.exists()) {
        localFile.createNewFile();
      }
      localYamlConfiguration.set("locations", localArrayList);
      localYamlConfiguration.save(localFile);
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
  
  public void addHologramLocation(Location paramLocation)
  {
    this.hologramLocations.add(paramLocation);
    updateHologramDatabase();
  }
  
  private void onHologramTouch(final Player paramPlayer, final Hologram paramHologram)
  {
    if ((!paramPlayer.hasMetadata("bw-remove-holo")) || ((!paramPlayer.isOp()) && (!paramPlayer.hasPermission("bw.setup")))) {
      return;
    }
    paramPlayer.removeMetadata("bw-remove-holo", Main.getInstance());
    Main.getInstance().getServer().getScheduler().runTask(Main.getInstance(), new Runnable()
    {
      public void run()
      {
        Object localObject = Main.getInstance().getHolographicInteractor().getHolograms().entrySet().iterator();
        while (((Iterator)localObject).hasNext())
        {
          Map.Entry localEntry = (Map.Entry)((Iterator)localObject).next();
          Iterator localIterator = ((List)localEntry.getValue()).iterator();
          while (localIterator.hasNext())
          {
            Hologram localHologram = (Hologram)localIterator.next();
            if ((localHologram.getX() == paramHologram.getX()) && (localHologram.getY() == paramHologram.getY()) && (localHologram.getZ() == paramHologram.getZ()))
            {
              localHologram.delete();
              localIterator.remove();
            }
          }
        }
        localObject = HolographicDisplaysInteraction.this.getHologramLocationByLocation(paramHologram.getLocation());
        if (localObject != null)
        {
          HolographicDisplaysInteraction.this.hologramLocations.remove(localObject);
          HolographicDisplaysInteraction.this.updateHologramDatabase();
        }
        paramPlayer.sendMessage(ChatWriter.pluginMessage(ChatColor.GREEN + Main._l("success.holoremoved")));
      }
    });
  }
  
  private void updatePlayerStatisticHologram(Player paramPlayer, final Hologram paramHologram)
  {
    PlayerStatistic localPlayerStatistic = Main.getInstance().getPlayerStatisticManager().getStatistic(paramPlayer);
    paramHologram.clearLines();
    String str1 = ChatColor.GRAY.toString();
    String str2 = ChatColor.YELLOW.toString();
    try
    {
      str1 = ChatColor.translateAlternateColorCodes('&', Main.getInstance().getStringConfig("holographic-stats.name-color", "&7"));
      str2 = ChatColor.translateAlternateColorCodes('&', Main.getInstance().getStringConfig("holographic-stats.value-color", "&e"));
    }
    catch (Exception localException) {}
    List localList = localPlayerStatistic.createStatisticLines(Main.getInstance().getBooleanConfig("holographic-stats.show-prefix", false), str1, str2);
    String str3 = Main.getInstance().getStringConfig("holographic-stats.head-line", "Your &eBEDWARS&f stats");
    if (!str3.trim().isEmpty()) {
      localList.add(0, ChatColor.translateAlternateColorCodes('&', str3));
    }
    Iterator localIterator = localList.iterator();
    while (localIterator.hasNext())
    {
      String str4 = (String)localIterator.next();
      TextLine localTextLine = paramHologram.appendTextLine(str4);
      localTextLine.setTouchHandler(new TouchHandler()
      {
        public void onTouch(Player paramAnonymousPlayer)
        {
          HolographicDisplaysInteraction.this.onHologramTouch(paramAnonymousPlayer, paramHologram);
        }
      });
    }
  }
  
  private Hologram getHologramByLocation(List<Hologram> paramList, Location paramLocation)
  {
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      Hologram localHologram = (Hologram)localIterator.next();
      if ((localHologram.getLocation().getX() == paramLocation.getX()) && (localHologram.getLocation().getY() == paramLocation.getY()) && (localHologram.getLocation().getZ() == paramLocation.getZ())) {
        return localHologram;
      }
    }
    return null;
  }
  
  private Location getHologramLocationByLocation(Location paramLocation)
  {
    Iterator localIterator = this.hologramLocations.iterator();
    while (localIterator.hasNext())
    {
      Location localLocation = (Location)localIterator.next();
      if ((localLocation.getX() == paramLocation.getX()) && (localLocation.getY() == paramLocation.getY()) && (localLocation.getZ() == paramLocation.getZ())) {
        return localLocation;
      }
    }
    return null;
  }
  
  private Hologram createPlayerStatisticHologram(Player paramPlayer, Location paramLocation)
  {
    Hologram localHologram = HologramsAPI.createHologram(Main.getInstance(), paramLocation);
    localHologram.getVisibilityManager().setVisibleByDefault(false);
    localHologram.getVisibilityManager().showTo(paramPlayer);
    updatePlayerStatisticHologram(paramPlayer, localHologram);
    return localHologram;
  }
  
  public void unloadAllHolograms(Player paramPlayer)
  {
    if (!this.holograms.containsKey(paramPlayer)) {
      return;
    }
    Iterator localIterator = ((List)this.holograms.get(paramPlayer)).iterator();
    while (localIterator.hasNext())
    {
      Hologram localHologram = (Hologram)localIterator.next();
      localHologram.delete();
    }
    this.holograms.remove(paramPlayer);
  }
  
  public void removeHologramPlayer(Player paramPlayer)
  {
    this.holograms.remove(paramPlayer);
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.HolographicDisplaysInteraction
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */