package io.github.yannici.bedwars.Commands;

import java.util.ArrayList;
import org.bukkit.command.CommandSender;

public abstract interface ICommand
{
  public abstract String getCommand();
  
  public abstract String getPermission();
  
  public abstract String getName();
  
  public abstract String getDescription();
  
  public abstract String[] getArguments();
  
  public abstract boolean hasPermission(CommandSender paramCommandSender);
  
  public abstract boolean execute(CommandSender paramCommandSender, ArrayList<String> paramArrayList);
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Commands.ICommand
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */