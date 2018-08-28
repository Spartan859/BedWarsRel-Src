package io.github.yannici.bedwars.Shop.Specials;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TNTPrimed;

public abstract interface ITNTSheep
{
  public abstract Location getLocation();
  
  public abstract void setTNT(TNTPrimed paramTNTPrimed);
  
  public abstract TNTPrimed getTNT();
  
  public abstract void setPassenger(TNTPrimed paramTNTPrimed);
  
  public abstract void remove();
  
  public abstract void setTNTSource(Entity paramEntity);
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Shop.Specials.ITNTSheep
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */