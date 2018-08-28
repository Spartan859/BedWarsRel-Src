package io.github.yannici.bedwars.Commands;

import com.google.common.collect.ImmutableMap;
import io.github.yannici.bedwars.ChatWriter;
import io.github.yannici.bedwars.Game.Game;
import io.github.yannici.bedwars.Game.GameManager;
import io.github.yannici.bedwars.Game.GameState;
import io.github.yannici.bedwars.Game.Team;
import io.github.yannici.bedwars.Main;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class RemoveTeamCommand
  extends BaseCommand
{
  public RemoveTeamCommand(Main paramMain)
  {
    super(paramMain);
  }
  
  public String getCommand()
  {
    return "removeteam";
  }
  
  public String getName()
  {
    return Main._l("commands.removeteam.name");
  }
  
  public String getDescription()
  {
    return Main._l("commands.removeteam.desc");
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
    Game localGame = getPlugin().getGameManager().getGame((String)paramArrayList.get(0));
    String str = (String)paramArrayList.get(1);
    if (localGame == null)
    {
      paramCommandSender.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + Main._l("errors.gamenotfound", ImmutableMap.of("game", ((String)paramArrayList.get(0)).toString()))));
      return false;
    }
    if (localGame.getState() != GameState.STOPPED)
    {
      paramCommandSender.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + Main._l("errors.notwhilegamerunning")));
      return false;
    }
    Team localTeam = localGame.getTeam(str);
    if (localTeam == null)
    {
      paramCommandSender.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + Main._l("errors.teamnotfound")));
      return false;
    }
    localGame.removeTeam(localTeam);
    paramCommandSender.sendMessage(ChatWriter.pluginMessage(ChatColor.GREEN + Main._l("success.teamremoved")));
    return true;
  }
  
  public String getPermission()
  {
    return "setup";
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Commands.RemoveTeamCommand
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */