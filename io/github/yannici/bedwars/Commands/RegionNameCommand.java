package io.github.yannici.bedwars.Commands;

import com.google.common.collect.ImmutableMap;
import io.github.yannici.bedwars.ChatWriter;
import io.github.yannici.bedwars.Game.Game;
import io.github.yannici.bedwars.Game.GameManager;
import io.github.yannici.bedwars.Game.GameState;
import io.github.yannici.bedwars.Main;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RegionNameCommand
  extends BaseCommand
  implements ICommand
{
  public RegionNameCommand(Main paramMain)
  {
    super(paramMain);
  }
  
  public String getCommand()
  {
    return "regionname";
  }
  
  public String getName()
  {
    return Main._l("commands.regionname.name");
  }
  
  public String getDescription()
  {
    return Main._l("commands.regionname.desc");
  }
  
  public String[] getArguments()
  {
    return new String[] { "game", "name" };
  }
  
  public boolean execute(CommandSender paramCommandSender, ArrayList<String> paramArrayList)
  {
    if (!paramCommandSender.hasPermission("bw." + getPermission())) {
      return false;
    }
    Player localPlayer = (Player)paramCommandSender;
    Game localGame = getPlugin().getGameManager().getGame((String)paramArrayList.get(0));
    String str = ((String)paramArrayList.get(1)).toString();
    if (localGame == null)
    {
      localPlayer.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + Main._l("errors.gamenotfound", ImmutableMap.of("game", ((String)paramArrayList.get(0)).toString()))));
      return false;
    }
    if (localGame.getState() == GameState.RUNNING)
    {
      paramCommandSender.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + Main._l("errors.notwhilegamerunning")));
      return false;
    }
    if (str.length() > 15)
    {
      localPlayer.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + Main._l("errors.toolongregionname")));
      return true;
    }
    localGame.setRegionName(str);
    localPlayer.sendMessage(ChatWriter.pluginMessage(ChatColor.GREEN + Main._l("success.regionnameset")));
    return true;
  }
  
  public String getPermission()
  {
    return "setup";
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Commands.RegionNameCommand
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */