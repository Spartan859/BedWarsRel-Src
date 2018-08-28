package io.github.yannici.bedwars.Com.v1_7_R1;

import io.github.yannici.bedwars.Game.Game;
import io.github.yannici.bedwars.Game.Region;
import io.github.yannici.bedwars.Game.Team;
import io.github.yannici.bedwars.Game.TeamColor;
import io.github.yannici.bedwars.Main;
import io.github.yannici.bedwars.Utils;
import io.github.yannici.bedwars.Villager.MerchantCategory;
import io.github.yannici.bedwars.Villager.VillagerTrade;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.server.v1_7_R1.EntityHuman;
import net.minecraft.server.v1_7_R1.EntityPlayer;
import net.minecraft.server.v1_7_R1.EntityVillager;
import net.minecraft.server.v1_7_R1.MerchantRecipeList;
import org.bukkit.craftbukkit.v1_7_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class VillagerItemShop
{
  private Game game = null;
  private Player player = null;
  private MerchantCategory category = null;
  
  public VillagerItemShop(Game paramGame, Player paramPlayer, MerchantCategory paramMerchantCategory)
  {
    this.game = paramGame;
    this.player = paramPlayer;
    this.category = paramMerchantCategory;
  }
  
  private EntityVillager createVillager()
  {
    try
    {
      EntityVillager localEntityVillager = new EntityVillager(((CraftWorld)this.game.getRegion().getWorld()).getHandle());
      return localEntityVillager;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    return null;
  }
  
  private EntityHuman getEntityHuman()
  {
    try
    {
      return ((CraftPlayer)this.player).getHandle();
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    return null;
  }
  
  public void openTrading()
  {
    new BukkitRunnable()
    {
      public void run()
      {
        try
        {
          EntityVillager localEntityVillager = VillagerItemShop.this.createVillager();
          EntityHuman localEntityHuman = VillagerItemShop.this.getEntityHuman();
          MerchantRecipeList localMerchantRecipeList = localEntityVillager.getOffers(localEntityHuman);
          localMerchantRecipeList.clear();
          Iterator localIterator = VillagerItemShop.this.category.getFilteredOffers().iterator();
          while (localIterator.hasNext())
          {
            VillagerTrade localVillagerTrade = (VillagerTrade)localIterator.next();
            ItemStack localItemStack = localVillagerTrade.getRewardItem();
            Method localMethod = Utils.getColorableMethod(localItemStack.getType());
            Object localObject;
            if (localMethod != null)
            {
              localObject = localItemStack.getItemMeta();
              localMethod.setAccessible(true);
              localMethod.invoke(localObject, new Object[] { VillagerItemShop.this.game.getPlayerTeam(VillagerItemShop.this.player).getColor().getColor() });
              localItemStack.setItemMeta((ItemMeta)localObject);
            }
            if ((localVillagerTrade.getHandle().getInstance() instanceof net.minecraft.server.v1_7_R1.MerchantRecipe))
            {
              localObject = (net.minecraft.server.v1_7_R1.MerchantRecipe)localVillagerTrade.getHandle().getInstance();
              ((net.minecraft.server.v1_7_R1.MerchantRecipe)localObject).a(1000);
              localMerchantRecipeList.add(localObject);
            }
          }
          localEntityVillager.a_(localEntityHuman);
          ((CraftPlayer)VillagerItemShop.this.player).getHandle().openTrade(localEntityVillager, VillagerItemShop.this.category.getName());
        }
        catch (Exception localException)
        {
          localException.printStackTrace();
        }
      }
    }.runTask(Main.getInstance());
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Com.v1_7_R1.VillagerItemShop
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */