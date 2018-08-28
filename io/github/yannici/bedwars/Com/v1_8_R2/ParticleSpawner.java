package io.github.yannici.bedwars.Com.v1_8_R2;

import java.util.Iterator;
import java.util.List;
import net.minecraft.server.v1_8_R2.EntityPlayer;
import net.minecraft.server.v1_8_R2.EnumParticle;
import net.minecraft.server.v1_8_R2.Packet;
import net.minecraft.server.v1_8_R2.PacketPlayOutWorldParticles;
import net.minecraft.server.v1_8_R2.PlayerConnection;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ParticleSpawner
{
  public static void spawnParticle(List<Player> paramList, String paramString, float paramFloat1, float paramFloat2, float paramFloat3)
  {
    Object localObject1 = EnumParticle.FIREWORKS_SPARK;
    CraftPlayer localCraftPlayer;
    for (localCraftPlayer : EnumParticle.values()) {
      if (localCraftPlayer.b().equals(paramString))
      {
        localObject1 = localCraftPlayer;
        break;
      }
    }
    ??? = new PacketPlayOutWorldParticles((EnumParticle)localObject1, false, paramFloat1, paramFloat2, paramFloat3, 0.0F, 0.0F, 0.0F, 0.0F, 1, new int[0]);
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      Player localPlayer = (Player)localIterator.next();
      localCraftPlayer = (CraftPlayer)localPlayer;
      localCraftPlayer.getHandle().playerConnection.sendPacket((Packet)???);
    }
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Com.v1_8_R2.ParticleSpawner
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */