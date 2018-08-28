package io.github.yannici.bedwars.Game;

import io.github.yannici.bedwars.Main;
import io.github.yannici.bedwars.Utils;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

@SerializableAs("RessourceSpawner")
public class RessourceSpawner
  implements Runnable, ConfigurationSerializable
{
  private Game game = null;
  private Location location = null;
  private int interval = 1000;
  private ItemStack itemstack = null;
  private String name = null;
  private double spread = 1.0D;
  
  public RessourceSpawner(Map<String, Object> paramMap)
  {
    this.location = Utils.locationDeserialize(paramMap.get("location"));
    if (paramMap.containsKey("name"))
    {
      this.name = paramMap.get("name").toString();
      if (!Main.getInstance().getConfig().contains("ressource." + this.name))
      {
        this.itemstack = ((ItemStack)paramMap.get("itemstack"));
        this.interval = Integer.parseInt(paramMap.get("interval").toString());
        if (paramMap.containsKey("spread")) {
          this.spread = Double.parseDouble(paramMap.get("spread").toString());
        }
      }
      else
      {
        this.itemstack = createSpawnerStackByConfig(Main.getInstance().getConfig().get("ressource." + this.name));
        this.interval = Main.getInstance().getIntConfig("ressource." + this.name + ".spawn-interval", 1000);
        this.spread = Main.getInstance().getConfig().getDouble("ressource." + this.name + ".spread", 1.0D);
      }
    }
    else
    {
      ItemStack localItemStack = (ItemStack)paramMap.get("itemstack");
      this.name = getNameByMaterial(localItemStack.getType());
      if (this.name == null)
      {
        this.itemstack = localItemStack;
        this.interval = Integer.parseInt(paramMap.get("interval").toString());
        if (paramMap.containsKey("spread")) {
          this.spread = Double.parseDouble(paramMap.get("spread").toString());
        }
      }
      else
      {
        this.itemstack = createSpawnerStackByConfig(Main.getInstance().getConfig().get("ressource." + this.name));
        this.interval = Main.getInstance().getIntConfig("ressource." + this.name + ".spawn-interval", 1000);
        this.spread = Main.getInstance().getConfig().getDouble("ressource." + this.name + ".spread", 1.0D);
      }
    }
  }
  
  public RessourceSpawner(Game paramGame, String paramString, Location paramLocation)
  {
    this.game = paramGame;
    this.name = paramString;
    this.interval = Main.getInstance().getIntConfig("ressource." + this.name + ".spawn-interval", 1000);
    this.location = paramLocation;
    this.itemstack = createSpawnerStackByConfig(Main.getInstance().getConfig().get("ressource." + this.name));
    this.spread = Main.getInstance().getConfig().getDouble("ressource." + this.name + ".spread", 1.0D);
  }
  
  private String getNameByMaterial(Material paramMaterial)
  {
    Iterator localIterator = Main.getInstance().getConfig().getConfigurationSection("ressource").getKeys(true).iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      ConfigurationSection localConfigurationSection = Main.getInstance().getConfig().getConfigurationSection("ressource." + str);
      if ((localConfigurationSection != null) && (localConfigurationSection.contains("item")))
      {
        Material localMaterial = Utils.parseMaterial(localConfigurationSection.getString("item"));
        if (localMaterial.equals(paramMaterial)) {
          return str;
        }
      }
    }
    return null;
  }
  
  public int getInterval()
  {
    return this.interval;
  }
  
  public void setGame(Game paramGame)
  {
    this.game = paramGame;
  }
  
  public void run()
  {
    Location localLocation = this.location;
    Item localItem = this.game.getRegion().getWorld().dropItemNaturally(localLocation, this.itemstack);
    localItem.setPickupDelay(0);
    if (this.spread != 1.0D) {
      localItem.setVelocity(localItem.getVelocity().multiply(this.spread));
    }
  }
  
  public Map<String, Object> serialize()
  {
    HashMap localHashMap = new HashMap();
    localHashMap.put("location", Utils.locationSerialize(this.location));
    localHashMap.put("name", this.name);
    return localHashMap;
  }
  
  public ItemStack getItemStack()
  {
    return this.itemstack;
  }
  
  public Location getLocation()
  {
    return this.location;
  }
  
  public static ItemStack createSpawnerStackByConfig(Object paramObject)
  {
    LinkedHashMap localLinkedHashMap = new LinkedHashMap();
    Object localObject1;
    Object localObject2;
    Object localObject3;
    if (!(paramObject instanceof LinkedHashMap))
    {
      localObject1 = (ConfigurationSection)paramObject;
      localObject2 = ((ConfigurationSection)localObject1).getKeys(false).iterator();
      while (((Iterator)localObject2).hasNext())
      {
        localObject3 = (String)((Iterator)localObject2).next();
        localLinkedHashMap.put(localObject3, ((ConfigurationSection)localObject1).get((String)localObject3));
      }
    }
    else
    {
      localLinkedHashMap = (LinkedHashMap)paramObject;
    }
    try
    {
      localObject1 = localLinkedHashMap;
      localObject2 = ((LinkedHashMap)localObject1).get("item").toString();
      localObject3 = null;
      int i = 0;
      int j = 0;
      int k = 0;
      ItemStack localItemStack = null;
      int m = 1;
      short s = 0;
      if (Utils.isNumber((String)localObject2)) {
        localObject3 = Material.getMaterial(Integer.parseInt((String)localObject2));
      } else {
        localObject3 = Material.getMaterial((String)localObject2);
      }
      try
      {
        if (((LinkedHashMap)localObject1).containsKey("amount")) {
          m = Integer.parseInt(((LinkedHashMap)localObject1).get("amount").toString());
        }
      }
      catch (Exception localException2)
      {
        m = 1;
      }
      if (((LinkedHashMap)localObject1).containsKey("meta")) {
        if (!((Material)localObject3).equals(Material.POTION))
        {
          try
          {
            k = Byte.parseByte(((LinkedHashMap)localObject1).get("meta").toString());
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
          s = Short.parseShort(((LinkedHashMap)localObject1).get("meta").toString());
        }
      }
      if (i != 0) {
        localItemStack = new ItemStack((Material)localObject3, m, (short)k);
      } else if (j != 0) {
        localItemStack = new ItemStack((Material)localObject3, m, s);
      } else {
        localItemStack = new ItemStack((Material)localObject3, m);
      }
      Object localObject4;
      Object localObject5;
      if (((LinkedHashMap)localObject1).containsKey("enchants"))
      {
        localObject4 = ((LinkedHashMap)localObject1).get("enchants");
        if ((localObject4 instanceof LinkedHashMap))
        {
          localObject5 = (LinkedHashMap)localObject4;
          Iterator localIterator = ((LinkedHashMap)localObject5).keySet().iterator();
          while (localIterator.hasNext())
          {
            Object localObject6 = localIterator.next();
            String str = localObject6.toString();
            if (localItemStack.getType() != Material.POTION)
            {
              Enchantment localEnchantment = null;
              int n = 0;
              if (Utils.isNumber(str))
              {
                localEnchantment = Enchantment.getById(Integer.parseInt(str));
                n = Integer.parseInt(((LinkedHashMap)localObject5).get(Integer.valueOf(Integer.parseInt(str))).toString());
              }
              else
              {
                localEnchantment = Enchantment.getByName(str.toUpperCase());
                n = Integer.parseInt(((LinkedHashMap)localObject5).get(str).toString()) - 1;
              }
              if (localEnchantment != null) {
                localItemStack.addUnsafeEnchantment(localEnchantment, n);
              }
            }
          }
        }
      }
      if (((LinkedHashMap)localObject1).containsKey("name"))
      {
        localObject4 = ChatColor.translateAlternateColorCodes('&', ((LinkedHashMap)localObject1).get("name").toString());
        localObject5 = localItemStack.getItemMeta();
        ((ItemMeta)localObject5).setDisplayName((String)localObject4);
        localItemStack.setItemMeta((ItemMeta)localObject5);
      }
      return localItemStack;
    }
    catch (Exception localException1)
    {
      localException1.printStackTrace();
    }
    return null;
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Game.RessourceSpawner
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */