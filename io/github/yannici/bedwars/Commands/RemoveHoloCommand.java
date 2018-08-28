package io.github.yannici.bedwars.Commands;

import io.github.yannici.bedwars.ChatWriter;
import io.github.yannici.bedwars.Main;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitScheduler;

public class RemoveHoloCommand
  extends BaseCommand
  implements ICommand
{
  public RemoveHoloCommand(Main paramMain)
  {
    super(paramMain);
  }
  
  public String getCommand()
  {
    return "removeholo";
  }
  
  public String getName()
  {
    return Main._l("commands.removeholo.name");
  }
  
  public String getDescription()
  {
    return Main._l("commands.removeholo.desc");
  }
  
  public String[] getArguments()
  {
    return new String[0];
  }
  
  public boolean execute(CommandSender paramCommandSender, ArrayList<String> paramArrayList)
  {
    if (!super.hasPermission(paramCommandSender)) {
      return false;
    }
    final Player localPlayer = (Player)paramCommandSender;
    localPlayer.setMetadata("bw-remove-holo", new FixedMetadataValue(Main.getInstance(), Boolean.valueOf(true)));
    localPlayer.sendMessage(ChatWriter.pluginMessage(ChatColor.GREEN + Main._l("commands.removeholo.explain")));
    Main.getInstance().getServer().getScheduler().runTaskLater(Main.getInstance(), new Runnable()
    {
      public void run()
      {
        if (localPlayer.hasMetadata("bw-remove-holo")) {
          localPlayer.removeMetadata("bw-remove-holo", Main.getInstance());
        }
      }
    }, 200L);
    return true;
  }
  
  public String getPermission()
  {
    return "setup";
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Commands.RemoveHoloCommand
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */