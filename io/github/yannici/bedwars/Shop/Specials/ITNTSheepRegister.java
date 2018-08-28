package io.github.yannici.bedwars.Shop.Specials;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract interface ITNTSheepRegister
{
  public abstract void registerEntities(int paramInt);
  
  public abstract ITNTSheep spawnCreature(TNTSheep paramTNTSheep, Location paramLocation, Player paramPlayer1, Player paramPlayer2, DyeColor paramDyeColor);
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Shop.Specials.ITNTSheepRegister
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */