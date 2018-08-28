package io.github.yannici.bedwars;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class ChatWriter
{
  public static String pluginMessage(String paramString)
  {
    return ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("chat-prefix", new StringBuilder().append(ChatColor.GRAY).append("[").append(ChatColor.AQUA).append("BedWars").append(ChatColor.GRAY).append("]").toString())) + " " + ChatColor.WHITE + paramString;
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.ChatWriter
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */