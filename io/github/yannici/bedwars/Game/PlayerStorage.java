package io.github.yannici.bedwars.Game;

import io.github.yannici.bedwars.Events.BedwarsOpenTeamSelectionEvent;
import io.github.yannici.bedwars.Main;
import io.github.yannici.bedwars.Utils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.potion.PotionEffect;

public class PlayerStorage
{
  private Player player = null;
  private ItemStack[] inventory = null;
  private ItemStack[] armor = null;
  private float xp = 0.0F;
  private Collection<PotionEffect> effects = null;
  private GameMode mode = null;
  private Location left = null;
  private int level = 0;
  private String displayName = null;
  private String listName = null;
  private int foodLevel = 0;
  
  public PlayerStorage(Player paramPlayer)
  {
    this.player = paramPlayer;
  }
  
  public void store()
  {
    this.inventory = this.player.getInventory().getContents();
    this.armor = this.player.getInventory().getArmorContents();
    this.xp = Float.valueOf(this.player.getExp()).floatValue();
    this.effects = this.player.getActivePotionEffects();
    this.mode = this.player.getGameMode();
    this.left = this.player.getLocation();
    this.level = this.player.getLevel();
    this.listName = this.player.getPlayerListName();
    this.displayName = this.player.getDisplayName();
    this.foodLevel = this.player.getFoodLevel();
  }
  
  public void clean()
  {
    PlayerInventory localPlayerInventory = this.player.getInventory();
    localPlayerInventory.setArmorContents(new ItemStack[4]);
    localPlayerInventory.setContents(new ItemStack[0]);
    this.player.setAllowFlight(false);
    this.player.setFlying(false);
    this.player.setExp(0.0F);
    this.player.setLevel(0);
    this.player.setSneaking(false);
    this.player.setSprinting(false);
    this.player.setFoodLevel(20);
    this.player.setMaxHealth(20.0D);
    this.player.setHealth(20.0D);
    this.player.setFireTicks(0);
    this.player.setGameMode(GameMode.SURVIVAL);
    boolean bool1 = Main.getInstance().getBooleanConfig("teamname-on-tab", true);
    boolean bool2 = Main.getInstance().getBooleanConfig("overwrite-names", false);
    Object localObject2;
    if (bool2)
    {
      localObject1 = Main.getInstance().getGameManager().getGameOfPlayer(this.player);
      if (localObject1 != null)
      {
        localObject2 = ((Game)localObject1).getPlayerTeam(this.player);
        if (localObject2 != null) {
          this.player.setDisplayName(((Team)localObject2).getChatColor() + ChatColor.stripColor(this.player.getName()));
        } else {
          this.player.setDisplayName(ChatColor.stripColor(this.player.getName()));
        }
      }
    }
    if ((bool1) && (Utils.isSupportingTitles()))
    {
      localObject1 = Main.getInstance().getGameManager().getGameOfPlayer(this.player);
      if (localObject1 != null)
      {
        localObject2 = ((Game)localObject1).getPlayerTeam(this.player);
        if (localObject2 != null) {
          this.player.setPlayerListName(((Team)localObject2).getChatColor() + ((Team)localObject2).getName() + ChatColor.WHITE + " | " + ((Team)localObject2).getChatColor() + ChatColor.stripColor(this.player.getDisplayName()));
        } else {
          this.player.setPlayerListName(ChatColor.stripColor(this.player.getDisplayName()));
        }
      }
    }
    if (this.player.isInsideVehicle()) {
      this.player.leaveVehicle();
    }
    Object localObject1 = this.player.getActivePotionEffects().iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (PotionEffect)((Iterator)localObject1).next();
      this.player.removePotionEffect(((PotionEffect)localObject2).getType());
    }
    this.player.updateInventory();
  }
  
  public void restore()
  {
    this.player.getInventory().setContents(this.inventory);
    this.player.getInventory().setArmorContents(this.armor);
    this.player.setGameMode(this.mode);
    if (this.mode == GameMode.CREATIVE) {
      this.player.setAllowFlight(true);
    }
    this.player.addPotionEffects(this.effects);
    this.player.setLevel(this.level);
    this.player.setExp(this.xp);
    this.player.setPlayerListName(this.listName);
    this.player.setDisplayName(this.displayName);
    this.player.setFoodLevel(this.foodLevel);
    Iterator localIterator = this.player.getActivePotionEffects().iterator();
    while (localIterator.hasNext())
    {
      PotionEffect localPotionEffect = (PotionEffect)localIterator.next();
      this.player.removePotionEffect(localPotionEffect.getType());
    }
    this.player.addPotionEffects(this.effects);
    this.player.updateInventory();
  }
  
  public Location getLeft()
  {
    return this.left;
  }
  
  public void loadLobbyInventory(Game paramGame)
  {
    ItemMeta localItemMeta = null;
    if (!paramGame.isAutobalanceEnabled())
    {
      localItemStack1 = new ItemStack(Material.BED, 1);
      localItemMeta = localItemStack1.getItemMeta();
      localItemMeta.setDisplayName(Main._l("lobby.chooseteam"));
      localItemStack1.setItemMeta(localItemMeta);
      this.player.getInventory().addItem(new ItemStack[] { localItemStack1 });
    }
    ItemStack localItemStack1 = new ItemStack(Material.SLIME_BALL, 1);
    localItemMeta = localItemStack1.getItemMeta();
    localItemMeta.setDisplayName(Main._l("lobby.leavegame"));
    localItemStack1.setItemMeta(localItemMeta);
    this.player.getInventory().setItem(8, localItemStack1);
    Team localTeam = paramGame.getPlayerTeam(this.player);
    ItemStack localItemStack2;
    if (localTeam != null)
    {
      localItemStack2 = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
      LeatherArmorMeta localLeatherArmorMeta = (LeatherArmorMeta)localItemStack2.getItemMeta();
      localLeatherArmorMeta.setDisplayName(localTeam.getChatColor() + localTeam.getDisplayName());
      localLeatherArmorMeta.setColor(localTeam.getColor().getColor());
      localItemStack2.setItemMeta(localLeatherArmorMeta);
      this.player.getInventory().setItem(7, localItemStack2);
      localTeam.equipPlayerWithLeather(this.player);
    }
    if ((this.player.hasPermission("bw.setup")) || (this.player.isOp()) || (this.player.hasPermission("bw.vip.forcestart")))
    {
      localItemStack2 = new ItemStack(Material.DIAMOND, 1);
      localItemMeta = localItemStack2.getItemMeta();
      localItemMeta.setDisplayName(Main._l("lobby.startgame"));
      localItemStack2.setItemMeta(localItemMeta);
      this.player.getInventory().addItem(new ItemStack[] { localItemStack2 });
    }
    this.player.updateInventory();
  }
  
  public void openTeamSelection(Game paramGame)
  {
    BedwarsOpenTeamSelectionEvent localBedwarsOpenTeamSelectionEvent = new BedwarsOpenTeamSelectionEvent(paramGame, this.player);
    Main.getInstance().getServer().getPluginManager().callEvent(localBedwarsOpenTeamSelectionEvent);
    if (localBedwarsOpenTeamSelectionEvent.isCancelled()) {
      return;
    }
    HashMap localHashMap = paramGame.getTeams();
    int i = localHashMap.size() % 9 == 0 ? 9 : localHashMap.size() % 9;
    Inventory localInventory = Bukkit.createInventory(this.player, localHashMap.size() + (9 - i), Main._l("lobby.chooseteam"));
    Iterator localIterator1 = localHashMap.values().iterator();
    while (localIterator1.hasNext())
    {
      Team localTeam = (Team)localIterator1.next();
      List localList = localTeam.getPlayers();
      if (localList.size() < localTeam.getMaxPlayers())
      {
        ItemStack localItemStack = new ItemStack(Material.WOOL, 1, (short)localTeam.getColor().getDyeColor().getData());
        ItemMeta localItemMeta = localItemStack.getItemMeta();
        localItemMeta.setDisplayName(localTeam.getChatColor() + localTeam.getName());
        ArrayList localArrayList = new ArrayList();
        int j = localTeam.getPlayers().size();
        int k = localTeam.getMaxPlayers();
        String str = "0";
        if (j >= k) {
          str = ChatColor.RED + String.valueOf(j);
        } else {
          str = ChatColor.YELLOW + String.valueOf(j);
        }
        localArrayList.add(ChatColor.GRAY + "(" + str + ChatColor.GRAY + "/" + ChatColor.YELLOW + String.valueOf(k) + ChatColor.GRAY + ")");
        localArrayList.add(ChatColor.WHITE + "---------");
        Iterator localIterator2 = localList.iterator();
        while (localIterator2.hasNext())
        {
          Player localPlayer = (Player)localIterator2.next();
          localArrayList.add(localTeam.getChatColor() + ChatColor.stripColor(localPlayer.getDisplayName()));
        }
        localItemMeta.setLore(localArrayList);
        localItemStack.setItemMeta(localItemMeta);
        localInventory.addItem(new ItemStack[] { localItemStack });
      }
    }
    this.player.openInventory(localInventory);
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Game.PlayerStorage
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */