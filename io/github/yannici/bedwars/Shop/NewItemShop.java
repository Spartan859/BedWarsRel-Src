package io.github.yannici.bedwars.Shop;

import io.github.yannici.bedwars.ChatWriter;
import io.github.yannici.bedwars.Game.Game;
import io.github.yannici.bedwars.Game.GameManager;
import io.github.yannici.bedwars.Game.PlayerSettings;
import io.github.yannici.bedwars.Game.Team;
import io.github.yannici.bedwars.Game.TeamColor;
import io.github.yannici.bedwars.Main;
import io.github.yannici.bedwars.Utils;
import io.github.yannici.bedwars.Villager.MerchantCategory;
import io.github.yannici.bedwars.Villager.VillagerTrade;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

public class NewItemShop
{
  private List<MerchantCategory> categories = null;
  private MerchantCategory currentCategory = null;
  
  public NewItemShop(List<MerchantCategory> paramList)
  {
    this.categories = paramList;
  }
  
  public List<MerchantCategory> getCategories()
  {
    return this.categories;
  }
  
  public boolean hasOpenCategory()
  {
    return this.currentCategory != null;
  }
  
  public boolean hasOpenCategory(MerchantCategory paramMerchantCategory)
  {
    if (this.currentCategory == null) {
      return false;
    }
    return this.currentCategory.equals(paramMerchantCategory);
  }
  
  private int getCategoriesSize(Player paramPlayer)
  {
    int i = 0;
    Iterator localIterator = this.categories.iterator();
    while (localIterator.hasNext())
    {
      MerchantCategory localMerchantCategory = (MerchantCategory)localIterator.next();
      if ((localMerchantCategory.getMaterial() != null) && ((paramPlayer == null) || (paramPlayer.hasPermission(localMerchantCategory.getPermission())))) {
        i++;
      }
    }
    return i;
  }
  
  public void openCategoryInventory(Player paramPlayer)
  {
    int i = getCategoriesSize(paramPlayer);
    int j = i % 9 == 0 ? 9 : i % 9;
    int k = i + (9 - j) + 9;
    Inventory localInventory = Bukkit.createInventory(paramPlayer, k, Main._l("ingame.shop.name"));
    addCategoriesToInventory(localInventory, paramPlayer);
    ItemStack localItemStack1 = new ItemStack(Material.SLIME_BALL, 1);
    ItemMeta localItemMeta1 = localItemStack1.getItemMeta();
    localItemMeta1.setDisplayName(Main._l("ingame.shop.oldshop"));
    localItemMeta1.setLore(new ArrayList());
    localItemStack1.setItemMeta(localItemMeta1);
    Game localGame = Main.getInstance().getGameManager().getGameOfPlayer(paramPlayer);
    ItemStack localItemStack2 = null;
    if (localGame != null)
    {
      ItemMeta localItemMeta2;
      if (localGame.getPlayerSettings(paramPlayer).oneStackPerShift())
      {
        localItemStack2 = new ItemStack(Material.BUCKET, 1);
        localItemMeta2 = localItemStack2.getItemMeta();
        localItemMeta2.setDisplayName(ChatColor.AQUA + Main._l("default.currently") + ": " + ChatColor.WHITE + Main._l("ingame.shop.onestackpershift"));
        localItemMeta2.setLore(new ArrayList());
        localItemStack2.setItemMeta(localItemMeta2);
      }
      else
      {
        localItemStack2 = new ItemStack(Material.LAVA_BUCKET, 1);
        localItemMeta2 = localItemStack2.getItemMeta();
        localItemMeta2.setDisplayName(ChatColor.AQUA + Main._l("default.currently") + ": " + ChatColor.WHITE + Main._l("ingame.shop.fullstackpershift"));
        localItemMeta2.setLore(new ArrayList());
        localItemStack2.setItemMeta(localItemMeta2);
      }
      if (localItemStack2 != null) {
        localInventory.setItem(k - 4, localItemStack2);
      }
    }
    if (localItemStack2 == null) {
      localInventory.setItem(k - 5, localItemStack1);
    } else {
      localInventory.setItem(k - 6, localItemStack1);
    }
    paramPlayer.openInventory(localInventory);
  }
  
  private void addCategoriesToInventory(Inventory paramInventory, Player paramPlayer)
  {
    Iterator localIterator = this.categories.iterator();
    while (localIterator.hasNext())
    {
      MerchantCategory localMerchantCategory = (MerchantCategory)localIterator.next();
      if (localMerchantCategory.getMaterial() == null)
      {
        Main.getInstance().getServer().getConsoleSender().sendMessage(ChatWriter.pluginMessage(ChatColor.RED + "Careful: Not supported material in shop category '" + localMerchantCategory.getName() + "'"));
      }
      else if ((paramPlayer == null) || (paramPlayer.hasPermission(localMerchantCategory.getPermission())))
      {
        ItemStack localItemStack = new ItemStack(localMerchantCategory.getMaterial(), 1);
        ItemMeta localItemMeta = localItemStack.getItemMeta();
        if ((this.currentCategory != null) && (this.currentCategory.equals(localMerchantCategory))) {
          localItemMeta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
        }
        localItemMeta.setDisplayName(localMerchantCategory.getName());
        localItemMeta.setLore(localMerchantCategory.getLores());
        localItemStack.setItemMeta(localItemMeta);
        paramInventory.addItem(new ItemStack[] { localItemStack });
      }
    }
  }
  
  private int getInventorySize(int paramInt)
  {
    int i = paramInt % 9 == 0 ? 9 : paramInt % 9;
    return paramInt + (9 - i);
  }
  
  public void handleInventoryClick(InventoryClickEvent paramInventoryClickEvent, Game paramGame, Player paramPlayer)
  {
    if (!hasOpenCategory()) {
      handleCategoryInventoryClick(paramInventoryClickEvent, paramGame, paramPlayer);
    } else {
      handleBuyInventoryClick(paramInventoryClickEvent, paramGame, paramPlayer);
    }
  }
  
  private void changeToOldShop(Game paramGame, Player paramPlayer)
  {
    paramGame.useOldShop(paramPlayer);
    paramPlayer.playSound(paramPlayer.getLocation(), Sound.CLICK, 10.0F, 1.0F);
    MerchantCategory.openCategorySelection(paramPlayer, paramGame);
  }
  
  private void handleCategoryInventoryClick(InventoryClickEvent paramInventoryClickEvent, Game paramGame, Player paramPlayer)
  {
    int i = getCategoriesSize(paramPlayer);
    int j = getInventorySize(i) + 9;
    int k = paramInventoryClickEvent.getRawSlot();
    if ((k >= getInventorySize(i)) && (k < j))
    {
      paramInventoryClickEvent.setCancelled(true);
      if (paramInventoryClickEvent.getCurrentItem().getType() == Material.SLIME_BALL)
      {
        changeToOldShop(paramGame, paramPlayer);
        return;
      }
      if (paramInventoryClickEvent.getCurrentItem().getType() == Material.BUCKET)
      {
        paramGame.getPlayerSettings(paramPlayer).setOneStackPerShift(false);
        paramPlayer.playSound(paramPlayer.getLocation(), Sound.CLICK, 10.0F, 1.0F);
        openCategoryInventory(paramPlayer);
        return;
      }
      if (paramInventoryClickEvent.getCurrentItem().getType() == Material.LAVA_BUCKET)
      {
        paramGame.getPlayerSettings(paramPlayer).setOneStackPerShift(true);
        paramPlayer.playSound(paramPlayer.getLocation(), Sound.CLICK, 10.0F, 1.0F);
        openCategoryInventory(paramPlayer);
        return;
      }
    }
    if (k >= j)
    {
      if (paramInventoryClickEvent.isShiftClick())
      {
        paramInventoryClickEvent.setCancelled(true);
        return;
      }
      paramInventoryClickEvent.setCancelled(false);
      return;
    }
    MerchantCategory localMerchantCategory = getCategoryByMaterial(paramInventoryClickEvent.getCurrentItem().getType());
    if (localMerchantCategory == null)
    {
      if (paramInventoryClickEvent.isShiftClick())
      {
        paramInventoryClickEvent.setCancelled(true);
        return;
      }
      paramInventoryClickEvent.setCancelled(false);
      return;
    }
    openBuyInventory(localMerchantCategory, paramPlayer, paramGame);
  }
  
  private void openBuyInventory(MerchantCategory paramMerchantCategory, Player paramPlayer, Game paramGame)
  {
    ArrayList localArrayList = paramMerchantCategory.getOffers();
    int i = getCategoriesSize(paramPlayer);
    int j = localArrayList.size();
    int k = getBuyInventorySize(i, j);
    paramPlayer.playSound(paramPlayer.getLocation(), Sound.CLICK, 10.0F, 1.0F);
    this.currentCategory = paramMerchantCategory;
    Inventory localInventory = Bukkit.createInventory(paramPlayer, k, Main._l("ingame.shop.name"));
    addCategoriesToInventory(localInventory, paramPlayer);
    for (int m = 0; m < localArrayList.size(); m++)
    {
      VillagerTrade localVillagerTrade = (VillagerTrade)localArrayList.get(m);
      if ((localVillagerTrade.getItem1().getType() != Material.AIR) || (localVillagerTrade.getRewardItem().getType() != Material.AIR))
      {
        int n = getInventorySize(i) + m;
        ItemStack localItemStack = toItemStack(localVillagerTrade, paramPlayer, paramGame);
        localInventory.setItem(n, localItemStack);
      }
    }
    paramPlayer.openInventory(localInventory);
  }
  
  private int getBuyInventorySize(int paramInt1, int paramInt2)
  {
    return getInventorySize(paramInt1) + getInventorySize(paramInt2);
  }
  
  private ItemStack toItemStack(VillagerTrade paramVillagerTrade, Player paramPlayer, Game paramGame)
  {
    ItemStack localItemStack1 = paramVillagerTrade.getRewardItem().clone();
    Method localMethod = Utils.getColorableMethod(localItemStack1.getType());
    ItemMeta localItemMeta = localItemStack1.getItemMeta();
    ItemStack localItemStack2 = paramVillagerTrade.getItem1();
    ItemStack localItemStack3 = paramVillagerTrade.getItem2();
    if (localMethod != null)
    {
      localMethod.setAccessible(true);
      try
      {
        localMethod.invoke(localItemMeta, new Object[] { paramGame.getPlayerTeam(paramPlayer).getColor().getColor() });
      }
      catch (Exception localException)
      {
        localException.printStackTrace();
      }
    }
    Object localObject = localItemMeta.getLore();
    if (localObject == null) {
      localObject = new ArrayList();
    }
    ((List)localObject).add(ChatColor.WHITE + String.valueOf(localItemStack2.getAmount()) + " " + localItemStack2.getItemMeta().getDisplayName());
    if (localItemStack3 != null) {
      ((List)localObject).add(ChatColor.WHITE + String.valueOf(localItemStack3.getAmount()) + " " + localItemStack3.getItemMeta().getDisplayName());
    }
    localItemMeta.setLore((List)localObject);
    localItemStack1.setItemMeta(localItemMeta);
    return localItemStack1;
  }
  
  private void handleBuyInventoryClick(InventoryClickEvent paramInventoryClickEvent, Game paramGame, Player paramPlayer)
  {
    int i = getCategoriesSize(paramPlayer);
    ArrayList localArrayList = this.currentCategory.getOffers();
    int j = localArrayList.size();
    int k = getBuyInventorySize(i, j);
    ItemStack localItemStack = paramInventoryClickEvent.getCurrentItem();
    int m = 0;
    int n = 0;
    boolean bool = paramGame.getPlayerSettings(paramPlayer).oneStackPerShift();
    if (this.currentCategory == null)
    {
      paramPlayer.closeInventory();
      return;
    }
    if (paramInventoryClickEvent.getRawSlot() < i)
    {
      paramInventoryClickEvent.setCancelled(true);
      if (localItemStack == null) {
        return;
      }
      if (localItemStack.getType().equals(this.currentCategory.getMaterial()))
      {
        this.currentCategory = null;
        openCategoryInventory(paramPlayer);
      }
      else
      {
        handleCategoryInventoryClick(paramInventoryClickEvent, paramGame, paramPlayer);
      }
    }
    else if (paramInventoryClickEvent.getRawSlot() < k)
    {
      paramInventoryClickEvent.setCancelled(true);
      if ((localItemStack == null) || (localItemStack.getType() == Material.AIR)) {
        return;
      }
      MerchantCategory localMerchantCategory = this.currentCategory;
      VillagerTrade localVillagerTrade = getTradingItem(localMerchantCategory, localItemStack, paramGame, paramPlayer);
      if (localVillagerTrade == null) {
        return;
      }
      paramPlayer.playSound(paramPlayer.getLocation(), Sound.ITEM_PICKUP, 10.0F, 1.0F);
      if (!hasEnoughRessource(paramPlayer, localVillagerTrade))
      {
        paramPlayer.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + Main._l("errors.notenoughress")));
        return;
      }
      if (paramInventoryClickEvent.isShiftClick())
      {
        while ((hasEnoughRessource(paramPlayer, localVillagerTrade)) && (m == 0))
        {
          m = !buyItem(localVillagerTrade, paramInventoryClickEvent.getCurrentItem(), paramPlayer) ? 1 : 0;
          if ((m == 0) && (bool))
          {
            n += localItemStack.getAmount();
            m = n + localItemStack.getAmount() > 64 ? 1 : 0;
          }
        }
        n = 0;
      }
      else
      {
        buyItem(localVillagerTrade, paramInventoryClickEvent.getCurrentItem(), paramPlayer);
      }
    }
    else
    {
      if (paramInventoryClickEvent.isShiftClick()) {
        paramInventoryClickEvent.setCancelled(true);
      } else {
        paramInventoryClickEvent.setCancelled(false);
      }
      return;
    }
  }
  
  private boolean buyItem(VillagerTrade paramVillagerTrade, ItemStack paramItemStack, Player paramPlayer)
  {
    PlayerInventory localPlayerInventory = paramPlayer.getInventory();
    boolean bool = true;
    int i = paramVillagerTrade.getItem1().getAmount();
    Iterator localIterator = localPlayerInventory.all(paramVillagerTrade.getItem1().getType()).entrySet().iterator();
    int j = localPlayerInventory.first(paramVillagerTrade.getItem1());
    if (j > -1) {
      localPlayerInventory.clear(j);
    } else {
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        ItemStack localItemStack2 = (ItemStack)localEntry.getValue();
        int n = localItemStack2.getAmount() - i;
        if (n < 0) {
          n = 0;
        }
        i -= localItemStack2.getAmount();
        localItemStack2.setAmount(n);
        localPlayerInventory.setItem(((Integer)localEntry.getKey()).intValue(), localItemStack2);
        if (i <= 0) {
          break;
        }
      }
    }
    if (paramVillagerTrade.getItem2() != null)
    {
      int k = paramVillagerTrade.getItem2().getAmount();
      localIterator = localPlayerInventory.all(paramVillagerTrade.getItem2().getType()).entrySet().iterator();
      int m = localPlayerInventory.first(paramVillagerTrade.getItem2());
      if (m > -1) {
        localPlayerInventory.clear(m);
      } else {
        while (localIterator.hasNext())
        {
          localObject1 = (Map.Entry)localIterator.next();
          localObject2 = (ItemStack)((Map.Entry)localObject1).getValue();
          int i1 = ((ItemStack)localObject2).getAmount() - k;
          if (i1 < 0) {
            i1 = 0;
          }
          k -= ((ItemStack)localObject2).getAmount();
          ((ItemStack)localObject2).setAmount(i1);
          localPlayerInventory.setItem(((Integer)((Map.Entry)localObject1).getKey()).intValue(), (ItemStack)localObject2);
          if (k <= 0) {
            break;
          }
        }
      }
    }
    ItemStack localItemStack1 = paramItemStack.clone();
    ItemMeta localItemMeta = localItemStack1.getItemMeta();
    Object localObject1 = localItemMeta.getLore();
    if (((List)localObject1).size() > 0)
    {
      ((List)localObject1).remove(((List)localObject1).size() - 1);
      if (paramVillagerTrade.getItem2() != null) {
        ((List)localObject1).remove(((List)localObject1).size() - 1);
      }
    }
    localItemMeta.setLore((List)localObject1);
    localItemStack1.setItemMeta(localItemMeta);
    Object localObject2 = localPlayerInventory.addItem(new ItemStack[] { localItemStack1 });
    if (((HashMap)localObject2).size() > 0)
    {
      ItemStack localItemStack3 = (ItemStack)((HashMap)localObject2).get(Integer.valueOf(0));
      int i2 = localItemStack1.getAmount() - localItemStack3.getAmount();
      localItemStack1.setAmount(i2);
      localPlayerInventory.removeItem(new ItemStack[] { localItemStack1 });
      localPlayerInventory.addItem(new ItemStack[] { paramVillagerTrade.getItem1() });
      if (paramVillagerTrade.getItem2() != null) {
        localPlayerInventory.addItem(new ItemStack[] { paramVillagerTrade.getItem2() });
      }
      bool = false;
    }
    paramPlayer.updateInventory();
    return bool;
  }
  
  private boolean hasEnoughRessource(Player paramPlayer, VillagerTrade paramVillagerTrade)
  {
    ItemStack localItemStack1 = paramVillagerTrade.getItem1();
    ItemStack localItemStack2 = paramVillagerTrade.getItem2();
    PlayerInventory localPlayerInventory = paramPlayer.getInventory();
    if (localItemStack2 != null)
    {
      if ((!localPlayerInventory.contains(localItemStack1.getType(), localItemStack1.getAmount())) || (!localPlayerInventory.contains(localItemStack2.getType(), localItemStack2.getAmount()))) {
        return false;
      }
    }
    else if (!localPlayerInventory.contains(localItemStack1.getType(), localItemStack1.getAmount())) {
      return false;
    }
    return true;
  }
  
  private VillagerTrade getTradingItem(MerchantCategory paramMerchantCategory, ItemStack paramItemStack, Game paramGame, Player paramPlayer)
  {
    Iterator localIterator = paramMerchantCategory.getOffers().iterator();
    while (localIterator.hasNext())
    {
      VillagerTrade localVillagerTrade = (VillagerTrade)localIterator.next();
      if ((localVillagerTrade.getItem1().getType() != Material.AIR) || (localVillagerTrade.getRewardItem().getType() != Material.AIR))
      {
        ItemStack localItemStack = toItemStack(localVillagerTrade, paramPlayer, paramGame);
        if (localItemStack.equals(paramItemStack)) {
          return localVillagerTrade;
        }
        if ((localItemStack.getType() == Material.ENDER_CHEST) && (paramItemStack.getType() == Material.ENDER_CHEST)) {
          return localVillagerTrade;
        }
      }
    }
    return null;
  }
  
  private MerchantCategory getCategoryByMaterial(Material paramMaterial)
  {
    Iterator localIterator = this.categories.iterator();
    while (localIterator.hasNext())
    {
      MerchantCategory localMerchantCategory = (MerchantCategory)localIterator.next();
      if (localMerchantCategory.getMaterial() == paramMaterial) {
        return localMerchantCategory;
      }
    }
    return null;
  }
  
  public void setCurrentCategory(MerchantCategory paramMerchantCategory)
  {
    this.currentCategory = paramMerchantCategory;
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Shop.NewItemShop
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */