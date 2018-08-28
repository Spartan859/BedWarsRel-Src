package io.github.yannici.bedwars.Com.v1_8_R3;

import io.github.yannici.bedwars.Game.Game;
import io.github.yannici.bedwars.Game.Region;
import io.github.yannici.bedwars.Main;
import io.github.yannici.bedwars.Shop.Specials.ITNTSheep;
import io.github.yannici.bedwars.Shop.Specials.ITNTSheepRegister;
import java.lang.reflect.Field;
import java.util.HashMap;
import net.minecraft.server.v1_8_R3.EntityTNTPrimed;
import net.minecraft.server.v1_8_R3.EntityTypes;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftSheep;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftTNTPrimed;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.scheduler.BukkitRunnable;

public class TNTSheepRegister
  implements ITNTSheepRegister
{
  public void registerEntities(int paramInt)
  {
    try
    {
      EntityTypes localEntityTypes = EntityTypes.class;
      Field localField1 = localEntityTypes.getDeclaredField("c");
      localField1.setAccessible(true);
      HashMap localHashMap1 = (HashMap)localField1.get(null);
      localHashMap1.put("TNTSheep", TNTSheep.class);
      Field localField2 = localEntityTypes.getDeclaredField("d");
      localField2.setAccessible(true);
      HashMap localHashMap2 = (HashMap)localField2.get(null);
      localHashMap2.put(TNTSheep.class, "TNTSheep");
      Field localField3 = localEntityTypes.getDeclaredField("e");
      localField3.setAccessible(true);
      HashMap localHashMap3 = (HashMap)localField3.get(null);
      localHashMap3.put(Integer.valueOf(paramInt), TNTSheep.class);
      Field localField4 = localEntityTypes.getDeclaredField("f");
      localField4.setAccessible(true);
      HashMap localHashMap4 = (HashMap)localField4.get(null);
      localHashMap4.put(TNTSheep.class, Integer.valueOf(paramInt));
      Field localField5 = localEntityTypes.getDeclaredField("g");
      localField5.setAccessible(true);
      HashMap localHashMap5 = (HashMap)localField5.get(null);
      localHashMap5.put("TNTSheep", Integer.valueOf(paramInt));
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
  
  public ITNTSheep spawnCreature(final io.github.yannici.bedwars.Shop.Specials.TNTSheep paramTNTSheep, final Location paramLocation, final Player paramPlayer1, Player paramPlayer2, DyeColor paramDyeColor)
  {
    final TNTSheep localTNTSheep = new TNTSheep(paramLocation, paramPlayer2);
    ((CraftWorld)paramLocation.getWorld()).getHandle().addEntity(localTNTSheep, CreatureSpawnEvent.SpawnReason.NATURAL);
    localTNTSheep.setPosition(paramLocation.getX(), paramLocation.getY(), paramLocation.getZ());
    ((CraftSheep)localTNTSheep.getBukkitEntity()).setColor(paramDyeColor);
    new BukkitRunnable()
    {
      public void run()
      {
        TNTPrimed localTNTPrimed = (TNTPrimed)paramLocation.getWorld().spawnEntity(paramLocation.add(0.0D, 1.0D, 0.0D), EntityType.PRIMED_TNT);
        ((CraftSheep)localTNTSheep.getBukkitEntity()).setPassenger(localTNTPrimed);
        localTNTSheep.setTNT(localTNTPrimed);
        try
        {
          Field localField = EntityTNTPrimed.class.getDeclaredField("source");
          localField.setAccessible(true);
          localField.set(((CraftTNTPrimed)localTNTPrimed).getHandle(), ((CraftLivingEntity)paramPlayer1).getHandle());
        }
        catch (Exception localException)
        {
          localException.printStackTrace();
        }
        localTNTSheep.getTNT().setYield((float)(localTNTSheep.getTNT().getYield() * Main.getInstance().getConfig().getDouble("specials.tntsheep.explosion-factor", 1.0D)));
        localTNTSheep.getTNT().setFuseTicks((int)Math.round(Main.getInstance().getConfig().getDouble("specials.tntsheep.fuse-time", 8.0D) * 20.0D));
        localTNTSheep.getTNT().setIsIncendiary(false);
        paramTNTSheep.getGame().getRegion().addRemovingEntity(localTNTSheep.getTNT());
        paramTNTSheep.getGame().getRegion().addRemovingEntity(localTNTSheep.getBukkitEntity());
        paramTNTSheep.updateTNT();
      }
    }.runTaskLater(Main.getInstance(), 5L);
    return localTNTSheep;
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Com.v1_8_R3.TNTSheepRegister
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */