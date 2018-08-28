package io.github.yannici.bedwars.Com.v1_7_R1;

import java.util.Iterator;
import java.util.List;
import net.minecraft.server.v1_7_R1.EntityPlayer;
import net.minecraft.server.v1_7_R1.PacketPlayOutWorldParticles;
import net.minecraft.server.v1_7_R1.PlayerConnection;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ParticleSpawner
{
  public static void spawnParticle(List<Player> paramList, String paramString, float paramFloat1, float paramFloat2, float paramFloat3)
  {
    PacketPlayOutWorldParticles localPacketPlayOutWorldParticles = new PacketPlayOutWorldParticles(paramString, paramFloat1, paramFloat2, paramFloat3, 0.0F, 0.0F, 0.0F, 0.0F, 1);
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      Player localPlayer = (Player)localIterator.next();
      CraftPlayer localCraftPlayer = (CraftPlayer)localPlayer;
      localCraftPlayer.getHandle().playerConnection.sendPacket(localPacketPlayOutWorldParticles);
    }
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Com.v1_7_R1.ParticleSpawner
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */