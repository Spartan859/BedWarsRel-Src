package io.github.yannici.bedwars;

import io.github.yannici.bedwars.Commands.BaseCommand;
import io.github.yannici.bedwars.Events.BedwarsCommandExecutedEvent;
import io.github.yannici.bedwars.Events.BedwarsExecuteCommandEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;

public class BedwarsCommandExecutor
  implements CommandExecutor
{
  private Main plugin = null;
  
  public BedwarsCommandExecutor(Main paramMain)
  {
    this.plugin = paramMain;
  }
  
  public boolean onCommand(CommandSender paramCommandSender, Command paramCommand, String paramString, String[] paramArrayOfString)
  {
    if (!paramCommand.getName().equalsIgnoreCase("bw")) {
      return false;
    }
    if (paramArrayOfString.length < 1) {
      return false;
    }
    String str = paramArrayOfString[0].toString();
    ArrayList localArrayList = new ArrayList(Arrays.asList(paramArrayOfString));
    localArrayList.remove(0);
    Iterator localIterator = this.plugin.getCommands().iterator();
    while (localIterator.hasNext())
    {
      BaseCommand localBaseCommand = (BaseCommand)localIterator.next();
      if (localBaseCommand.getCommand().equalsIgnoreCase(str))
      {
        if (localBaseCommand.getArguments().length > localArrayList.size())
        {
          paramCommandSender.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + Main._l("errors.argumentslength")));
          return false;
        }
        BedwarsExecuteCommandEvent localBedwarsExecuteCommandEvent = new BedwarsExecuteCommandEvent(paramCommandSender, localBaseCommand, localArrayList);
        Main.getInstance().getServer().getPluginManager().callEvent(localBedwarsExecuteCommandEvent);
        if (localBedwarsExecuteCommandEvent.isCancelled()) {
          return true;
        }
        boolean bool = localBaseCommand.execute(paramCommandSender, localArrayList);
        BedwarsCommandExecutedEvent localBedwarsCommandExecutedEvent = new BedwarsCommandExecutedEvent(paramCommandSender, localBaseCommand, localArrayList, bool);
        Main.getInstance().getServer().getPluginManager().callEvent(localBedwarsCommandExecutedEvent);
        return bool;
      }
    }
    return false;
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.BedwarsCommandExecutor
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */