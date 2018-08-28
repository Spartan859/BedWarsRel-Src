package io.github.yannici.bedwars.Game;

import io.github.yannici.bedwars.Main;
import io.github.yannici.bedwars.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;

@SerializableAs("Team")
public class Team
  implements ConfigurationSerializable
{
  private TeamColor color = null;
  private org.bukkit.scoreboard.Team scoreboardTeam = null;
  private String name = null;
  private int maxPlayers = 0;
  private Location spawnLocation = null;
  private Location targetHeadBlock = null;
  private Location targetFeetBlock = null;
  private Inventory inventory = null;
  private List<Block> chests = null;
  
  public Team(Map<String, Object> paramMap)
  {
    this.name = paramMap.get("name").toString();
    this.maxPlayers = Integer.parseInt(paramMap.get("maxplayers").toString());
    this.color = TeamColor.valueOf(paramMap.get("color").toString().toUpperCase());
    this.spawnLocation = Utils.locationDeserialize(paramMap.get("spawn"));
    this.chests = new ArrayList();
    if (paramMap.containsKey("bedhead"))
    {
      this.targetHeadBlock = Utils.locationDeserialize(paramMap.get("bedhead"));
      if ((this.targetHeadBlock != null) && (paramMap.containsKey("bedfeed")) && (this.targetHeadBlock.getBlock().getType().equals(Material.BED_BLOCK))) {
        this.targetFeetBlock = Utils.locationDeserialize(paramMap.get("bedfeed"));
      }
    }
  }
  
  public Team(String paramString, TeamColor paramTeamColor, int paramInt, org.bukkit.scoreboard.Team paramTeam)
  {
    this.name = paramString;
    this.color = paramTeamColor;
    this.maxPlayers = paramInt;
    this.scoreboardTeam = paramTeam;
    this.chests = new ArrayList();
  }
  
  public boolean addPlayer(Player paramPlayer)
  {
    if (this.scoreboardTeam.getPlayers().size() >= this.maxPlayers) {
      return false;
    }
    try
    {
      boolean bool1 = Main.getInstance().getBooleanConfig("overwrite-names", false);
      if (bool1)
      {
        paramPlayer.setDisplayName(getChatColor() + ChatColor.stripColor(paramPlayer.getName()));
        paramPlayer.setPlayerListName(getChatColor() + ChatColor.stripColor(paramPlayer.getName()));
      }
      boolean bool2 = Main.getInstance().getBooleanConfig("teamname-on-tab", true);
      if ((bool2) && (Utils.isSupportingTitles())) {
        paramPlayer.setPlayerListName(getChatColor() + getName() + ChatColor.WHITE + " | " + getChatColor() + ChatColor.stripColor(paramPlayer.getDisplayName()));
      }
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
      return false;
    }
    this.scoreboardTeam.addPlayer(paramPlayer);
    return true;
  }
  
  public org.bukkit.scoreboard.Team getScoreboardTeam()
  {
    return this.scoreboardTeam;
  }
  
  public int getMaxPlayers()
  {
    return this.maxPlayers;
  }
  
  public void setTargets(Block paramBlock1, Block paramBlock2)
  {
    this.targetHeadBlock = paramBlock1.getLocation();
    if (paramBlock2 != null) {
      this.targetFeetBlock = paramBlock2.getLocation();
    } else {
      this.targetFeetBlock = null;
    }
  }
  
  public Block getHeadTarget()
  {
    if (this.targetHeadBlock == null) {
      return null;
    }
    this.targetHeadBlock.getBlock().getChunk().load(true);
    return this.targetHeadBlock.getBlock();
  }
  
  public Block getFeetTarget()
  {
    if (this.targetFeetBlock == null) {
      return null;
    }
    this.targetFeetBlock.getBlock().getChunk().load(true);
    return this.targetFeetBlock.getBlock();
  }
  
  public void removePlayer(OfflinePlayer paramOfflinePlayer)
  {
    if (this.scoreboardTeam.hasPlayer(paramOfflinePlayer)) {
      this.scoreboardTeam.removePlayer(paramOfflinePlayer);
    }
    boolean bool = Main.getInstance().getBooleanConfig("overwrite-names", false);
    if ((bool) && (paramOfflinePlayer.isOnline()))
    {
      paramOfflinePlayer.getPlayer().setDisplayName(ChatColor.RESET + ChatColor.stripColor(paramOfflinePlayer.getPlayer().getName()));
      paramOfflinePlayer.getPlayer().setPlayerListName(ChatColor.RESET + paramOfflinePlayer.getPlayer().getName());
    }
  }
  
  public boolean isInTeam(Player paramPlayer)
  {
    return this.scoreboardTeam.hasPlayer(paramPlayer);
  }
  
  public void setScoreboardTeam(org.bukkit.scoreboard.Team paramTeam)
  {
    this.scoreboardTeam = paramTeam;
    paramTeam.setDisplayName(getChatColor() + this.name);
  }
  
  public TeamColor getColor()
  {
    return this.color;
  }
  
  public ChatColor getChatColor()
  {
    return this.color.getChatColor();
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public List<Player> getPlayers()
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = this.scoreboardTeam.getPlayers().iterator();
    while (localIterator.hasNext())
    {
      OfflinePlayer localOfflinePlayer = (OfflinePlayer)localIterator.next();
      if (localOfflinePlayer.isOnline()) {
        localArrayList.add(localOfflinePlayer.getPlayer());
      }
    }
    return localArrayList;
  }
  
  public List<OfflinePlayer> getTeamPlayers()
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = this.scoreboardTeam.getPlayers().iterator();
    while (localIterator.hasNext())
    {
      OfflinePlayer localOfflinePlayer = (OfflinePlayer)localIterator.next();
      localArrayList.add(localOfflinePlayer);
    }
    return localArrayList;
  }
  
  public Location getSpawnLocation()
  {
    return this.spawnLocation;
  }
  
  public void setSpawnLocation(Location paramLocation)
  {
    this.spawnLocation = paramLocation;
  }
  
  public boolean isDead(Game paramGame)
  {
    Material localMaterial = paramGame.getTargetMaterial();
    this.targetHeadBlock.getBlock().getChunk().load(true);
    if (this.targetFeetBlock == null) {
      return this.targetHeadBlock.getBlock().getType() != localMaterial;
    }
    this.targetFeetBlock.getBlock().getChunk().load(true);
    return (this.targetHeadBlock.getBlock().getType() != localMaterial) && (this.targetFeetBlock.getBlock().getType() != localMaterial);
  }
  
  public Map<String, Object> serialize()
  {
    HashMap localHashMap = new HashMap();
    localHashMap.put("name", this.name);
    localHashMap.put("color", this.color.toString());
    localHashMap.put("maxplayers", Integer.valueOf(this.maxPlayers));
    localHashMap.put("spawn", Utils.locationSerialize(this.spawnLocation));
    localHashMap.put("bedhead", Utils.locationSerialize(this.targetHeadBlock));
    if (this.targetFeetBlock != null) {
      localHashMap.put("bedfeed", Utils.locationSerialize(this.targetFeetBlock));
    }
    return localHashMap;
  }
  
  public String getDisplayName()
  {
    return this.scoreboardTeam.getDisplayName();
  }
  
  public void setInventory(Inventory paramInventory)
  {
    this.inventory = paramInventory;
  }
  
  public Inventory getInventory()
  {
    return this.inventory;
  }
  
  public void addChest(Block paramBlock)
  {
    this.chests.add(paramBlock);
  }
  
  public void removeChest(Block paramBlock)
  {
    this.chests.remove(paramBlock);
    if (this.chests.size() == 0) {
      this.inventory = null;
    }
  }
  
  public List<Block> getChests()
  {
    return this.chests;
  }
  
  public void createTeamInventory()
  {
    Inventory localInventory = Bukkit.createInventory(null, InventoryType.ENDER_CHEST, Main._l("ingame.teamchest"));
    this.inventory = localInventory;
  }
  
  public void equipPlayerWithLeather(Player paramPlayer)
  {
    ItemStack localItemStack1 = new ItemStack(Material.LEATHER_HELMET, 1);
    LeatherArmorMeta localLeatherArmorMeta = (LeatherArmorMeta)localItemStack1.getItemMeta();
    localLeatherArmorMeta.setColor(getColor().getColor());
    localItemStack1.setItemMeta(localLeatherArmorMeta);
    ItemStack localItemStack2 = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
    localLeatherArmorMeta = (LeatherArmorMeta)localItemStack2.getItemMeta();
    localLeatherArmorMeta.setColor(getColor().getColor());
    localItemStack2.setItemMeta(localLeatherArmorMeta);
    ItemStack localItemStack3 = new ItemStack(Material.LEATHER_LEGGINGS, 1);
    localLeatherArmorMeta = (LeatherArmorMeta)localItemStack3.getItemMeta();
    localLeatherArmorMeta.setColor(getColor().getColor());
    localItemStack3.setItemMeta(localLeatherArmorMeta);
    ItemStack localItemStack4 = new ItemStack(Material.LEATHER_BOOTS, 1);
    localLeatherArmorMeta = (LeatherArmorMeta)localItemStack4.getItemMeta();
    localLeatherArmorMeta.setColor(getColor().getColor());
    localItemStack4.setItemMeta(localLeatherArmorMeta);
    paramPlayer.getInventory().setHelmet(localItemStack1);
    paramPlayer.getInventory().setChestplate(localItemStack2);
    paramPlayer.getInventory().setLeggings(localItemStack3);
    paramPlayer.getInventory().setBoots(localItemStack4);
    paramPlayer.updateInventory();
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Game.Team
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */