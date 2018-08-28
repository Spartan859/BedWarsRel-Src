package io.github.yannici.bedwars.Game;

import io.github.yannici.bedwars.Main;
import org.bukkit.entity.Player;

public class PlayerSettings
{
  private Player player = null;
  private boolean oneStackPerShift = false;
  private Object hologram = null;
  private boolean isTeleporting = false;
  
  public PlayerSettings(Player paramPlayer)
  {
    this.player = paramPlayer;
    this.oneStackPerShift = Main.getInstance().getBooleanConfig("player-settings.one-stack-on-shift", false);
  }
  
  public Player getPlayer()
  {
    return this.player;
  }
  
  public Object getHologram()
  {
    return this.hologram;
  }
  
  public void setHologram(Object paramObject)
  {
    this.hologram = paramObject;
  }
  
  public boolean oneStackPerShift()
  {
    return this.oneStackPerShift;
  }
  
  public void setOneStackPerShift(boolean paramBoolean)
  {
    this.oneStackPerShift = paramBoolean;
  }
  
  public boolean isTeleporting()
  {
    return this.isTeleporting;
  }
  
  public void setTeleporting(boolean paramBoolean)
  {
    this.isTeleporting = paramBoolean;
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Game.PlayerSettings
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */