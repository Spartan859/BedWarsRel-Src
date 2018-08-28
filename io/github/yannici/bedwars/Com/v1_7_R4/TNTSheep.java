package io.github.yannici.bedwars.Com.v1_7_R4;

import io.github.yannici.bedwars.Main;
import io.github.yannici.bedwars.Shop.Specials.ITNTSheep;
import java.lang.reflect.Field;
import java.util.ArrayList;
import net.minecraft.server.v1_7_R4.AttributeInstance;
import net.minecraft.server.v1_7_R4.EntityHuman;
import net.minecraft.server.v1_7_R4.EntitySheep;
import net.minecraft.server.v1_7_R4.EntityTNTPrimed;
import net.minecraft.server.v1_7_R4.GenericAttributes;
import net.minecraft.server.v1_7_R4.Navigation;
import net.minecraft.server.v1_7_R4.PathfinderGoalSelector;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftTNTPrimed;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;

public class TNTSheep
  extends EntitySheep
  implements ITNTSheep
{
  private org.bukkit.World world = null;
  private TNTPrimed primedTnt = null;
  
  public TNTSheep(net.minecraft.server.v1_7_R4.World paramWorld)
  {
    super(paramWorld);
  }
  
  public TNTSheep(Location paramLocation, Player paramPlayer)
  {
    super(((CraftWorld)paramLocation.getWorld()).getHandle());
    this.world = paramLocation.getWorld();
    this.locX = paramLocation.getX();
    this.locY = paramLocation.getY();
    this.locZ = paramLocation.getZ();
    try
    {
      Field localField1 = this.goalSelector.getClass().getDeclaredField("b");
      localField1.setAccessible(true);
      localField1.set(this.goalSelector, new ArrayList());
      Field localField2 = Navigation.class.getDeclaredField("e");
      localField2.setAccessible(true);
      AttributeInstance localAttributeInstance = (AttributeInstance)localField2.get(getNavigation());
      localAttributeInstance.setValue(128.0D);
      getAttributeInstance(GenericAttributes.b).setValue(128.0D);
      getAttributeInstance(GenericAttributes.d).setValue(Main.getInstance().getConfig().getDouble("specials.tntsheep.speed", 0.4D));
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    this.goalSelector.a(0, new PathfinderGoalBedwarsPlayer(this, EntityHuman.class, 1.0D, false));
    setTarget(((CraftEntity)paramPlayer).getHandle());
    ((Creature)getBukkitEntity()).setTarget(paramPlayer);
  }
  
  public Location getLocation()
  {
    return new Location(this.world, this.locX, this.locY, this.locZ);
  }
  
  public void setTNT(TNTPrimed paramTNTPrimed)
  {
    this.primedTnt = paramTNTPrimed;
  }
  
  public TNTPrimed getTNT()
  {
    return this.primedTnt;
  }
  
  public void setPassenger(TNTPrimed paramTNTPrimed)
  {
    getBukkitEntity().setPassenger(paramTNTPrimed);
  }
  
  public void remove()
  {
    getBukkitEntity().remove();
  }
  
  public void setTNTSource(Entity paramEntity)
  {
    if (paramEntity == null) {
      return;
    }
    try
    {
      Field localField = EntityTNTPrimed.class.getDeclaredField("source");
      localField.setAccessible(true);
      localField.set(((CraftTNTPrimed)this.primedTnt).getHandle(), ((CraftEntity)paramEntity).getHandle());
    }
    catch (Exception localException) {}
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Com.v1_7_R4.TNTSheep
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */