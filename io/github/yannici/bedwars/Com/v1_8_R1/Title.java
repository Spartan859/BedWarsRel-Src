package io.github.yannici.bedwars.Com.v1_8_R1;

import net.minecraft.server.v1_8_R1.ChatSerializer;
import net.minecraft.server.v1_8_R1.EntityPlayer;
import net.minecraft.server.v1_8_R1.EnumTitleAction;
import net.minecraft.server.v1_8_R1.IChatBaseComponent;
import net.minecraft.server.v1_8_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R1.PlayerConnection;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Title
{
  public static void showTitle(Player paramPlayer, String paramString, double paramDouble1, double paramDouble2, double paramDouble3)
  {
    IChatBaseComponent localIChatBaseComponent = ChatSerializer.a("{\"text\": \"" + paramString + "\"}");
    PacketPlayOutTitle localPacketPlayOutTitle1 = new PacketPlayOutTitle(EnumTitleAction.TITLE, localIChatBaseComponent);
    PacketPlayOutTitle localPacketPlayOutTitle2 = new PacketPlayOutTitle(EnumTitleAction.TIMES, null, (int)Math.round(paramDouble1 * 20.0D), (int)Math.round(paramDouble2 * 20.0D), (int)Math.round(paramDouble3 * 20.0D));
    ((CraftPlayer)paramPlayer).getHandle().playerConnection.sendPacket(localPacketPlayOutTitle2);
    ((CraftPlayer)paramPlayer).getHandle().playerConnection.sendPacket(localPacketPlayOutTitle1);
  }
  
  public static void showSubTitle(Player paramPlayer, String paramString, double paramDouble1, double paramDouble2, double paramDouble3)
  {
    IChatBaseComponent localIChatBaseComponent = ChatSerializer.a("{\"text\": \"" + paramString + "\"}");
    PacketPlayOutTitle localPacketPlayOutTitle1 = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, localIChatBaseComponent);
    PacketPlayOutTitle localPacketPlayOutTitle2 = new PacketPlayOutTitle(EnumTitleAction.TIMES, null, (int)Math.round(paramDouble1 * 20.0D), (int)Math.round(paramDouble2 * 20.0D), (int)Math.round(paramDouble3 * 20.0D));
    ((CraftPlayer)paramPlayer).getHandle().playerConnection.sendPacket(localPacketPlayOutTitle2);
    ((CraftPlayer)paramPlayer).getHandle().playerConnection.sendPacket(localPacketPlayOutTitle1);
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Com.v1_8_R1.Title
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */