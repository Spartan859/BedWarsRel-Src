package io.github.yannici.bedwars.Shop.Specials;

import io.github.yannici.bedwars.Game.Game;
import io.github.yannici.bedwars.Game.Region;
import io.github.yannici.bedwars.Game.Team;
import io.github.yannici.bedwars.Main;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class Trap
  extends SpecialItem
{
  private Game game = null;
  private Team team = null;
  private int duration = 10;
  private int amplifierBlindness = 1;
  private int amplifierSlowness = 1;
  private int amplifierWeakness = 1;
  private boolean activateBlindness = true;
  private boolean activateSlowness = true;
  private boolean activateWeakness = true;
  private boolean particles = true;
  private boolean playSound = true;
  private Location location = null;
  
  public Material getItemMaterial()
  {
    return Material.TRIPWIRE;
  }
  
  public Material getActivatedMaterial()
  {
    return null;
  }
  
  private Constructor<PotionEffect> getPotionConstructor()
  {
    Constructor localConstructor = null;
    try
    {
      localConstructor = PotionEffect.class.getConstructor(new Class[] { PotionEffectType.class, Integer.TYPE, Integer.TYPE, Boolean.TYPE, Boolean.TYPE });
      return localConstructor;
    }
    catch (Exception localException1)
    {
      try
      {
        localConstructor = PotionEffect.class.getConstructor(new Class[] { PotionEffectType.class, Integer.TYPE, Integer.TYPE, Boolean.TYPE });
      }
      catch (Exception localException2)
      {
        localException2.printStackTrace();
      }
    }
    return localConstructor;
  }
  
  public void activate(final Player paramPlayer)
  {
    try
    {
      ArrayList localArrayList = new ArrayList();
      Constructor localConstructor = getPotionConstructor();
      Object localObject;
      if (this.activateBlindness)
      {
        localObject = null;
        if (localConstructor.getParameterTypes().length == 5) {
          localObject = (PotionEffect)localConstructor.newInstance(new Object[] { PotionEffectType.BLINDNESS, Integer.valueOf(this.duration * 20), Integer.valueOf(this.amplifierBlindness), Boolean.valueOf(true), Boolean.valueOf(this.particles) });
        } else {
          localObject = (PotionEffect)localConstructor.newInstance(new Object[] { PotionEffectType.BLINDNESS, Integer.valueOf(this.duration * 20), Integer.valueOf(this.amplifierBlindness), Boolean.valueOf(true) });
        }
        localArrayList.add(localObject);
      }
      if (this.activateWeakness)
      {
        localObject = null;
        if (localConstructor.getParameterTypes().length == 5) {
          localObject = (PotionEffect)localConstructor.newInstance(new Object[] { PotionEffectType.WEAKNESS, Integer.valueOf(this.duration * 20), Integer.valueOf(this.amplifierWeakness), Boolean.valueOf(true), Boolean.valueOf(this.particles) });
        } else {
          localObject = (PotionEffect)localConstructor.newInstance(new Object[] { PotionEffectType.WEAKNESS, Integer.valueOf(this.duration * 20), Integer.valueOf(this.amplifierWeakness), Boolean.valueOf(true) });
        }
        localArrayList.add(localObject);
      }
      if (this.activateSlowness)
      {
        localObject = null;
        if (localConstructor.getParameterTypes().length == 5) {
          localObject = (PotionEffect)localConstructor.newInstance(new Object[] { PotionEffectType.SLOW, Integer.valueOf(this.duration * 20), Integer.valueOf(this.amplifierSlowness), Boolean.valueOf(true), Boolean.valueOf(this.particles) });
        } else {
          localObject = (PotionEffect)localConstructor.newInstance(new Object[] { PotionEffectType.SLOW, Integer.valueOf(this.duration * 20), Integer.valueOf(this.amplifierSlowness), Boolean.valueOf(true) });
        }
        localArrayList.add(localObject);
      }
      this.game.addRunningTask(new BukkitRunnable()
      {
        private int counter = 0;
        
        public void run()
        {
          if (this.counter >= Trap.this.duration)
          {
            Trap.this.game.removeRunningTask(this);
            cancel();
            return;
          }
          paramPlayer.playSound(paramPlayer.getLocation(), Sound.FUSE, 2.0F, 1.0F);
          this.counter += 1;
        }
      }.runTaskTimer(Main.getInstance(), 0L, 20L));
      if (localArrayList.size() > 0)
      {
        localObject = localArrayList.iterator();
        while (((Iterator)localObject).hasNext())
        {
          PotionEffect localPotionEffect = (PotionEffect)((Iterator)localObject).next();
          if (paramPlayer.hasPotionEffect(localPotionEffect.getType())) {
            paramPlayer.removePotionEffect(localPotionEffect.getType());
          }
          paramPlayer.addPotionEffect(localPotionEffect);
        }
      }
      this.game.broadcast(Main._l("ingame.specials.trap.trapped"), new ArrayList(this.team.getPlayers()));
      if (this.playSound) {
        this.game.broadcastSound(Sound.SHEEP_IDLE, 3.0F, 1.0F, this.team.getPlayers());
      }
      this.game.getRegion().removePlacedUnbreakableBlock(this.location.getBlock());
      this.location.getBlock().setType(Material.AIR);
      this.game.removeSpecialItem(this);
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
  
  public void create(Game paramGame, Team paramTeam, Location paramLocation)
  {
    this.game = paramGame;
    this.team = paramTeam;
    this.location = paramLocation;
    this.game.addSpecialItem(this);
  }
  
  public Game getGame()
  {
    return this.game;
  }
  
  public Location getLocation()
  {
    return this.location;
  }
  
  public void setLocation(Location paramLocation)
  {
    this.location = paramLocation;
  }
  
  public Team getPlacedTeam()
  {
    return this.team;
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Shop.Specials.Trap
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */