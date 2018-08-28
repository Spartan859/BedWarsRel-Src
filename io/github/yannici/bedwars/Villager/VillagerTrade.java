package io.github.yannici.bedwars.Villager;

import org.bukkit.inventory.ItemStack;

public class VillagerTrade
{
  private ItemStack item1;
  private ItemStack item2;
  private ItemStack rewardItem;
  
  public VillagerTrade(ItemStack paramItemStack1, ItemStack paramItemStack2, ItemStack paramItemStack3)
  {
    this.item1 = paramItemStack1;
    this.item2 = paramItemStack2;
    this.rewardItem = paramItemStack3;
  }
  
  public VillagerTrade(ItemStack paramItemStack1, ItemStack paramItemStack2)
  {
    this(paramItemStack1, null, paramItemStack2);
  }
  
  public VillagerTrade(MerchantRecipe paramMerchantRecipe)
  {
    this.item1 = new CraftItemStack(paramMerchantRecipe.getItem1()).asBukkitCopy();
    this.item2 = (paramMerchantRecipe.getItem1() == null ? null : new CraftItemStack(paramMerchantRecipe.getItem2()).asBukkitCopy());
    this.rewardItem = new CraftItemStack(paramMerchantRecipe.getRewardItem()).asBukkitCopy();
  }
  
  public MerchantRecipe getHandle()
  {
    if (this.item2 == null) {
      return new MerchantRecipe(new CraftItemStack(this.item1).asNMSCopy(), new CraftItemStack(this.rewardItem).asNMSCopy());
    }
    return new MerchantRecipe(new CraftItemStack(this.item1).asNMSCopy(), new CraftItemStack(this.item2).asNMSCopy(), new CraftItemStack(this.rewardItem).asNMSCopy());
  }
  
  public boolean hasItem2()
  {
    return this.item2 != null;
  }
  
  public ItemStack getItem1()
  {
    return this.item1;
  }
  
  public ItemStack getItem2()
  {
    return this.item2;
  }
  
  public ItemStack getRewardItem()
  {
    return this.rewardItem;
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Villager.VillagerTrade
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */