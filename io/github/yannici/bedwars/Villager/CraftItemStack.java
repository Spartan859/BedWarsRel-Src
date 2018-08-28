package io.github.yannici.bedwars.Villager;

import java.lang.reflect.Method;
import org.bukkit.inventory.ItemStack;

public class CraftItemStack
{
  private Class craftItemStack = null;
  private Object stack = null;
  
  public CraftItemStack(ItemStack paramItemStack)
  {
    this.stack = paramItemStack;
  }
  
  public CraftItemStack(Object paramObject)
  {
    this.stack = paramObject;
  }
  
  public Object asNMSCopy()
  {
    try
    {
      Method localMethod = this.craftItemStack.getDeclaredMethod("asNMSCopy", new Class[] { ItemStack.class });
      localMethod.setAccessible(true);
      return localMethod.invoke(null, new Object[] { this.stack });
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    return null;
  }
  
  public ItemStack asBukkitCopy()
  {
    try
    {
      Method localMethod = this.craftItemStack.getDeclaredMethod("asBukkitCopy", new Class[] { ItemStack.class });
      localMethod.setAccessible(true);
      return (ItemStack)localMethod.invoke(null, new Object[] { this.stack });
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    return null;
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Villager.CraftItemStack
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */