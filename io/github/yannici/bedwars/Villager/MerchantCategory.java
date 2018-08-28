package io.github.yannici.bedwars.Villager;

import io.github.yannici.bedwars.Game.Game;
import io.github.yannici.bedwars.Main;
import io.github.yannici.bedwars.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MerchantCategory
{
  private String name = null;
  private Material item = null;
  private List<String> lores = null;
  private ArrayList<VillagerTrade> offers = null;
  private int order = 0;
  private String permission = null;
  
  public MerchantCategory(String paramString, Material paramMaterial)
  {
    this(paramString, paramMaterial, new ArrayList(), new ArrayList(), 0, "bw.base");
  }
  
  public MerchantCategory(String paramString1, Material paramMaterial, ArrayList<VillagerTrade> paramArrayList, List<String> paramList, int paramInt, String paramString2)
  {
    this.name = paramString1;
    this.item = paramMaterial;
    this.offers = paramArrayList;
    this.lores = paramList;
    this.order = paramInt;
    this.permission = paramString2;
  }
  
  public List<String> getLores()
  {
    return this.lores;
  }
  
  public int getOrder()
  {
    return this.order;
  }
  
  public static HashMap<Material, MerchantCategory> loadCategories(FileConfiguration paramFileConfiguration)
  {
    if (paramFileConfiguration.getConfigurationSection("shop") == null) {
      return new HashMap();
    }
    HashMap localHashMap = new HashMap();
    ConfigurationSection localConfigurationSection = paramFileConfiguration.getConfigurationSection("shop");
    Iterator localIterator = localConfigurationSection.getKeys(false).iterator();
    while (localIterator.hasNext())
    {
      String str1 = (String)localIterator.next();
      String str2 = ChatColor.translateAlternateColorCodes('&', localConfigurationSection.getString(str1 + ".name"));
      Material localMaterial = null;
      ArrayList localArrayList = new ArrayList();
      String str3 = localConfigurationSection.get(str1 + ".item").toString();
      String str4 = "bw.base";
      int i = 0;
      if (!Utils.isNumber(str3)) {
        localMaterial = Material.getMaterial(localConfigurationSection.getString(str1 + ".item"));
      } else {
        localMaterial = Material.getMaterial(localConfigurationSection.getInt(str1 + ".item"));
      }
      if (localConfigurationSection.contains(str1 + ".lore"))
      {
        localObject1 = localConfigurationSection.getList(str1 + ".lore").iterator();
        while (((Iterator)localObject1).hasNext())
        {
          localObject2 = ((Iterator)localObject1).next();
          localArrayList.add(ChatColor.translateAlternateColorCodes('&', ((Object)localObject2).toString()));
        }
      }
      if ((localConfigurationSection.contains(str1 + ".order")) && (localConfigurationSection.isInt(str1 + ".order"))) {
        i = localConfigurationSection.getInt(str1 + ".order");
      }
      if (localConfigurationSection.contains(str1 + ".permission")) {
        str4 = localConfigurationSection.getString(str1 + ".permission", "bw.base");
      }
      Object localObject1 = new ArrayList();
      Object localObject2 = localConfigurationSection.getList(str1 + ".offers").iterator();
      while (((Iterator)localObject2).hasNext())
      {
        Object localObject3 = ((Iterator)localObject2).next();
        Object localObject4;
        if ((localObject3 instanceof String))
        {
          if ((localObject3.toString().equalsIgnoreCase("empty")) || (localObject3.toString().equalsIgnoreCase("null")) || (localObject3.toString().equalsIgnoreCase("e")))
          {
            localObject4 = new VillagerTrade(new ItemStack(Material.AIR, 1), new ItemStack(Material.AIR, 1));
            ((ArrayList)localObject1).add(localObject4);
          }
        }
        else
        {
          localObject4 = (LinkedHashMap)localObject3;
          if ((((LinkedHashMap)localObject4).containsKey("item1")) && (((LinkedHashMap)localObject4).containsKey("reward")))
          {
            ItemStack localItemStack1 = createItemStackByConfig(((LinkedHashMap)localObject4).get("item1"));
            ItemStack localItemStack2 = null;
            if (((LinkedHashMap)localObject4).containsKey("item2")) {
              localItemStack2 = createItemStackByConfig(((LinkedHashMap)localObject4).get("item2"));
            }
            ItemStack localItemStack3 = createItemStackByConfig(((LinkedHashMap)localObject4).get("reward"));
            if ((localItemStack1 != null) && (localItemStack3 != null))
            {
              VillagerTrade localVillagerTrade = null;
              if (localItemStack2 != null) {
                localVillagerTrade = new VillagerTrade(localItemStack1, localItemStack2, localItemStack3);
              } else {
                localVillagerTrade = new VillagerTrade(localItemStack1, localItemStack3);
              }
              ((ArrayList)localObject1).add(localVillagerTrade);
            }
          }
        }
      }
      localHashMap.put(localMaterial, new MerchantCategory(str2, localMaterial, (ArrayList)localObject1, localArrayList, i, str4));
    }
    return localHashMap;
  }
  
  public static ItemStack createItemStackByConfig(Object paramObject)
  {
    if (!(paramObject instanceof LinkedHashMap)) {
      return null;
    }
    try
    {
      LinkedHashMap localLinkedHashMap = (LinkedHashMap)paramObject;
      String str1 = localLinkedHashMap.get("item").toString();
      Material localMaterial = null;
      int i = 0;
      int j = 0;
      int k = 0;
      ItemStack localItemStack = null;
      int m = 1;
      short s = 0;
      if (Utils.isNumber(str1)) {
        localMaterial = Material.getMaterial(Integer.parseInt(str1));
      } else {
        localMaterial = Material.getMaterial(str1);
      }
      try
      {
        if (localLinkedHashMap.containsKey("amount")) {
          m = Integer.parseInt(localLinkedHashMap.get("amount").toString());
        }
      }
      catch (Exception localException2)
      {
        m = 1;
      }
      if (localLinkedHashMap.containsKey("meta")) {
        if (!localMaterial.equals(Material.POTION))
        {
          try
          {
            k = Byte.parseByte(localLinkedHashMap.get("meta").toString());
            i = 1;
          }
          catch (Exception localException3)
          {
            i = 0;
          }
        }
        else
        {
          j = 1;
          s = Short.parseShort(localLinkedHashMap.get("meta").toString());
        }
      }
      if (i != 0) {
        localItemStack = new ItemStack(localMaterial, m, (short)k);
      } else if (j != 0) {
        localItemStack = new ItemStack(localMaterial, m, s);
      } else {
        localItemStack = new ItemStack(localMaterial, m);
      }
      Object localObject1;
      Object localObject2;
      Object localObject3;
      Object localObject4;
      if (localLinkedHashMap.containsKey("lore"))
      {
        localObject1 = new ArrayList();
        localObject2 = localItemStack.getItemMeta();
        localObject3 = ((List)localLinkedHashMap.get("lore")).iterator();
        while (((Iterator)localObject3).hasNext())
        {
          localObject4 = ((Iterator)localObject3).next();
          ((List)localObject1).add(ChatColor.translateAlternateColorCodes('&', ((Object)localObject4).toString()));
        }
        ((ItemMeta)localObject2).setLore((List)localObject1);
        localItemStack.setItemMeta((ItemMeta)localObject2);
      }
      String str2;
      Object localObject5;
      if (localLinkedHashMap.containsKey("enchants"))
      {
        localObject1 = localLinkedHashMap.get("enchants");
        if ((localObject1 instanceof LinkedHashMap))
        {
          localObject2 = (LinkedHashMap)localObject1;
          localObject3 = ((LinkedHashMap)localObject2).keySet().iterator();
          while (((Iterator)localObject3).hasNext())
          {
            localObject4 = ((Iterator)localObject3).next();
            str2 = ((Object)localObject4).toString();
            if (localItemStack.getType() != Material.POTION)
            {
              localObject5 = null;
              int n = 0;
              if (Utils.isNumber(str2))
              {
                localObject5 = Enchantment.getById(Integer.parseInt(str2));
                n = Integer.parseInt(((LinkedHashMap)localObject2).get(Integer.valueOf(Integer.parseInt(str2))).toString());
              }
              else
              {
                localObject5 = Enchantment.getByName(str2.toUpperCase());
                n = Integer.parseInt(((LinkedHashMap)localObject2).get(str2).toString()) - 1;
              }
              if (localObject5 != null) {
                localItemStack.addUnsafeEnchantment((Enchantment)localObject5, n);
              }
            }
          }
        }
      }
      if (localLinkedHashMap.containsKey("name"))
      {
        localObject1 = ChatColor.translateAlternateColorCodes('&', localLinkedHashMap.get("name").toString());
        localObject2 = localItemStack.getItemMeta();
        ((ItemMeta)localObject2).setDisplayName((String)localObject1);
        localItemStack.setItemMeta((ItemMeta)localObject2);
      }
      else
      {
        localObject1 = localItemStack.getItemMeta();
        localObject2 = ((ItemMeta)localObject1).getDisplayName();
        localObject3 = Main.getInstance().getConfig().getConfigurationSection("ressource");
        localObject4 = ((ConfigurationSection)localObject3).getKeys(false).iterator();
        while (((Iterator)localObject4).hasNext())
        {
          str2 = (String)((Iterator)localObject4).next();
          localObject5 = null;
          String str3 = ((ConfigurationSection)localObject3).getString(str2 + ".item");
          if (Utils.isNumber(str3)) {
            localObject5 = Material.getMaterial(Integer.parseInt(str3));
          } else {
            localObject5 = Material.getMaterial(str3);
          }
          if (localItemStack.getType().equals(localObject5)) {
            localObject2 = ChatColor.translateAlternateColorCodes('&', ((ConfigurationSection)localObject3).getString(str2 + ".name"));
          }
        }
        ((ItemMeta)localObject1).setDisplayName((String)localObject2);
        localItemStack.setItemMeta((ItemMeta)localObject1);
      }
      return localItemStack;
    }
    catch (Exception localException1)
    {
      localException1.printStackTrace();
    }
    return null;
  }
  
  public static void openCategorySelection(Player paramPlayer, Game paramGame)
  {
    List localList = paramGame.getOrderedItemShopCategories();
    int i = localList.size() % 9 == 0 ? 9 : localList.size() % 9;
    int j = localList.size() + (9 - i) + 9;
    Inventory localInventory = Bukkit.createInventory(paramPlayer, j, Main._l("ingame.shop.name"));
    Object localObject1 = localList.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (MerchantCategory)((Iterator)localObject1).next();
      if ((paramPlayer == null) || (paramPlayer.hasPermission(((MerchantCategory)localObject2).getPermission())))
      {
        ItemStack localItemStack = new ItemStack(((MerchantCategory)localObject2).getMaterial(), 1);
        ItemMeta localItemMeta = localItemStack.getItemMeta();
        localItemMeta.setDisplayName(((MerchantCategory)localObject2).getName());
        localItemMeta.setLore(((MerchantCategory)localObject2).getLores());
        localItemStack.setItemMeta(localItemMeta);
        localInventory.addItem(new ItemStack[] { localItemStack });
      }
    }
    localObject1 = new ItemStack(Material.SNOW_BALL, 1);
    Object localObject2 = ((ItemStack)localObject1).getItemMeta();
    ((ItemMeta)localObject2).setDisplayName(Main._l("ingame.shop.newshop"));
    ((ItemMeta)localObject2).setLore(new ArrayList());
    ((ItemStack)localObject1).setItemMeta((ItemMeta)localObject2);
    localInventory.setItem(j - 5, (ItemStack)localObject1);
    paramPlayer.openInventory(localInventory);
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public Material getMaterial()
  {
    return this.item;
  }
  
  public ArrayList<VillagerTrade> getFilteredOffers()
  {
    ArrayList localArrayList = (ArrayList)this.offers.clone();
    Iterator localIterator = localArrayList.iterator();
    while (localIterator.hasNext())
    {
      VillagerTrade localVillagerTrade = (VillagerTrade)localIterator.next();
      if ((localVillagerTrade.getItem1().getType() == Material.AIR) && (localVillagerTrade.getRewardItem().getType() == Material.AIR)) {
        localIterator.remove();
      }
    }
    return localArrayList;
  }
  
  public ArrayList<VillagerTrade> getOffers()
  {
    return this.offers;
  }
  
  public String getPermission()
  {
    return this.permission;
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Villager.MerchantCategory
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */