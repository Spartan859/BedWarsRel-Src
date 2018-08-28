package io.github.yannici.bedwars.Commands;

import io.github.yannici.bedwars.ChatWriter;
import io.github.yannici.bedwars.Main;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class BaseCommand
  implements ICommand
{
  private Main plugin = null;
  
  public BaseCommand(Main paramMain)
  {
    this.plugin = paramMain;
  }
  
  protected Main getPlugin()
  {
    return this.plugin;
  }
  
  public abstract String getCommand();
  
  public abstract String getName();
  
  public abstract String getDescription();
  
  public abstract String[] getArguments();
  
  public abstract boolean execute(CommandSender paramCommandSender, ArrayList<String> paramArrayList);
  
  public boolean hasPermission(CommandSender paramCommandSender)
  {
    if (!(paramCommandSender instanceof Player))
    {
      paramCommandSender.sendMessage(ChatWriter.pluginMessage("Only players should execute this command!"));
      return false;
    }
    if (!paramCommandSender.hasPermission("bw." + getPermission()))
    {
      paramCommandSender.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + "You don't have permission to execute this command!"));
      return false;
    }
    return true;
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Commands.BaseCommand
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */