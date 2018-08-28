package io.github.yannici.bedwars.Commands;

import com.google.common.collect.ImmutableMap;
import io.github.yannici.bedwars.ChatWriter;
import io.github.yannici.bedwars.Game.Game;
import io.github.yannici.bedwars.Game.GameManager;
import io.github.yannici.bedwars.Main;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class StopGameCommand
  extends BaseCommand
  implements ICommand
{
  public StopGameCommand(Main paramMain)
  {
    super(paramMain);
  }
  
  public String getCommand()
  {
    return "stop";
  }
  
  public String getName()
  {
    return Main._l("commands.stop.name");
  }
  
  public String getDescription()
  {
    return Main._l("commands.stop.desc");
  }
  
  public String[] getArguments()
  {
    return new String[] { "game" };
  }
  
  public boolean execute(CommandSender paramCommandSender, ArrayList<String> paramArrayList)
  {
    if (!paramCommandSender.hasPermission("bw." + getPermission())) {
      return false;
    }
    Game localGame = getPlugin().getGameManager().getGame((String)paramArrayList.get(0));
    if (localGame == null)
    {
      paramCommandSender.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + Main._l("errors.gamenotfound", ImmutableMap.of("game", ((String)paramArrayList.get(0)).toString()))));
      return false;
    }
    if (!localGame.stop())
    {
      paramCommandSender.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + Main._l("errors.gamenotrunning")));
      return false;
    }
    paramCommandSender.sendMessage(ChatWriter.pluginMessage(ChatColor.GREEN + Main._l("success.stopped")));
    return true;
  }
  
  public String getPermission()
  {
    return "setup";
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Commands.StopGameCommand
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */