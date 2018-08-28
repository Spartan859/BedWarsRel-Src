package io.github.yannici.bedwars.Commands;

import io.github.yannici.bedwars.HolographicDisplaysInteraction;
import io.github.yannici.bedwars.Main;
import java.util.ArrayList;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddHoloCommand
  extends BaseCommand
  implements ICommand
{
  public AddHoloCommand(Main paramMain)
  {
    super(paramMain);
  }
  
  public String getCommand()
  {
    return "addholo";
  }
  
  public String getName()
  {
    return Main._l("commands.addholo.name");
  }
  
  public String getDescription()
  {
    return Main._l("commands.addholo.desc");
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
    if ((!Main.getInstance().isHologramsEnabled()) && (Main.getInstance().getHolographicInteractor() != null)) {
      return true;
    }
    Player localPlayer = (Player)paramCommandSender;
    Main.getInstance().getHolographicInteractor().addHologramLocation(localPlayer.getEyeLocation());
    Main.getInstance().getHolographicInteractor().updateHolograms();
    return true;
  }
  
  public String getPermission()
  {
    return "setup";
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Commands.AddHoloCommand
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */