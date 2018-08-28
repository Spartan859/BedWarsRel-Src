package io.github.yannici.bedwars.Game;

import io.github.yannici.bedwars.Main;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public class TeamJoinMetaDataValue
  implements MetadataValue
{
  private boolean teamjoin = true;
  private Team team = null;
  
  public TeamJoinMetaDataValue(Team paramTeam)
  {
    this.team = paramTeam;
  }
  
  public boolean asBoolean()
  {
    return true;
  }
  
  public byte asByte()
  {
    return asBoolean() ? 1 : 0;
  }
  
  public double asDouble()
  {
    return asBoolean() ? 1.0D : 0.0D;
  }
  
  public float asFloat()
  {
    return asBoolean() ? 1.0F : 0.0F;
  }
  
  public int asInt()
  {
    return asBoolean() ? 1 : 0;
  }
  
  public long asLong()
  {
    return asBoolean() ? 1L : 0L;
  }
  
  public short asShort()
  {
    return asBoolean() ? 1 : 0;
  }
  
  public String asString()
  {
    return asBoolean() ? "true" : "false";
  }
  
  public Plugin getOwningPlugin()
  {
    return Main.getInstance();
  }
  
  public void invalidate()
  {
    this.teamjoin = false;
  }
  
  public Object value()
  {
    return Boolean.valueOf(this.teamjoin);
  }
  
  public Team getTeam()
  {
    return this.team;
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Game.TeamJoinMetaDataValue
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */