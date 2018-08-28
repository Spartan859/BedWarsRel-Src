package io.github.yannici.bedwars.Com.v1_7_R3;

import net.minecraft.server.v1_7_R3.EntityPlayer;
import net.minecraft.server.v1_7_R3.EnumClientCommand;
import net.minecraft.server.v1_7_R3.PacketPlayInClientCommand;
import net.minecraft.server.v1_7_R3.PlayerConnection;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PerformRespawnRunnable
  extends BukkitRunnable
{
  private Player player = null;
  
  public PerformRespawnRunnable(Player paramPlayer)
  {
    this.player = paramPlayer;
  }
  
  public void run()
  {
    PacketPlayInClientCommand localPacketPlayInClientCommand = new PacketPlayInClientCommand(EnumClientCommand.PERFORM_RESPAWN);
    CraftPlayer localCraftPlayer = (CraftPlayer)this.player;
    localCraftPlayer.getHandle().playerConnection.a(localPacketPlayInClientCommand);
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Com.v1_7_R3.PerformRespawnRunnable
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */