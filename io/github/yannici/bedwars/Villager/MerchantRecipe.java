package io.github.yannici.bedwars.Villager;

import io.github.yannici.bedwars.Main;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class MerchantRecipe
{
  private Class merchantRecipe = null;
  private Object instance = null;
  
  public MerchantRecipe(Object paramObject)
  {
    this.instance = paramObject;
  }
  
  public MerchantRecipe(Object paramObject1, Object paramObject2, Object paramObject3)
  {
    this.merchantRecipe = Main.getInstance().getMinecraftServerClass("MerchantRecipe");
    Class localClass = Main.getInstance().getMinecraftServerClass("ItemStack");
    try
    {
      this.instance = this.merchantRecipe.getDeclaredConstructor(new Class[] { localClass, localClass, localClass }).newInstance(new Object[] { paramObject1, paramObject2, paramObject3 });
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
  
  public MerchantRecipe(Object paramObject1, Object paramObject2)
  {
    this(paramObject1, null, paramObject2);
  }
  
  public Object getInstance()
  {
    return this.instance;
  }
  
  public static Class getReflectionClass()
  {
    return Main.getInstance().getMinecraftServerClass("MerchantRecipe");
  }
  
  public Object getItem1()
  {
    try
    {
      Method localMethod = this.merchantRecipe.getDeclaredMethod("getBuyItem1", new Class[0]);
      localMethod.setAccessible(true);
      return localMethod.invoke(this.merchantRecipe, new Object[0]);
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    return null;
  }
  
  public Object getItem2()
  {
    try
    {
      Method localMethod = this.merchantRecipe.getDeclaredMethod("getBuyItem2", new Class[0]);
      localMethod.setAccessible(true);
      return localMethod.invoke(this.merchantRecipe, new Object[0]);
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    return null;
  }
  
  public Object getRewardItem()
  {
    try
    {
      Method localMethod = this.merchantRecipe.getDeclaredMethod("getBuyItem3", new Class[0]);
      localMethod.setAccessible(true);
      return localMethod.invoke(this.merchantRecipe, new Object[0]);
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    return null;
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Villager.MerchantRecipe
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */