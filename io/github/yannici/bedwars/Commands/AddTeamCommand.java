package io.github.yannici.bedwars.Commands;

import com.google.common.collect.ImmutableMap;
import io.github.yannici.bedwars.ChatWriter;
import io.github.yannici.bedwars.Game.Game;
import io.github.yannici.bedwars.Game.GameManager;
import io.github.yannici.bedwars.Game.GameState;
import io.github.yannici.bedwars.Game.TeamColor;
import io.github.yannici.bedwars.Main;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class AddTeamCommand
  extends BaseCommand
{
  public AddTeamCommand(Main paramMain)
  {
    super(paramMain);
  }
  
  public String getCommand()
  {
    return "addteam";
  }
  
  public String getName()
  {
    return Main._l("commands.addteam.name");
  }
  
  public String getDescription()
  {
    return Main._l("commands.addteam.desc");
  }
  
  public String[] getArguments()
  {
    return new String[] { "game", "name", "color", "maxplayers" };
  }
  
  public boolean execute(CommandSender paramCommandSender, ArrayList<String> paramArrayList)
  {
    if (!paramCommandSender.hasPermission("bw." + getPermission())) {
      return false;
    }
    Game localGame = getPlugin().getGameManager().getGame((String)paramArrayList.get(0));
    String str1 = (String)paramArrayList.get(1);
    String str2 = (String)paramArrayList.get(2);
    String str3 = (String)paramArrayList.get(3);
    TeamColor localTeamColor = TeamColor.valueOf(str2.toUpperCase());
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
    int i = Integer.parseInt(str3);
    if ((i < 1) || (i > 24))
    {
      paramCommandSender.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + Main._l("errors.playeramount")));
      return false;
    }
    if (localTeamColor == null)
    {
      paramCommandSender.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + Main._l("errors.teamcolornotallowed")));
      return false;
    }
    if ((str1.length() < 3) || (str1.length() > 20))
    {
      paramCommandSender.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + Main._l("errors.teamnamelength")));
      return false;
    }
    if (localGame.getTeam(str1) != null)
    {
      paramCommandSender.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + Main._l("errors.teamnameinuse")));
      return false;
    }
    localGame.addTeam(str1, localTeamColor, i);
    paramCommandSender.sendMessage(ChatWriter.pluginMessage(ChatColor.GREEN + Main._l("success.teamadded", ImmutableMap.of("team", str1))));
    return true;
  }
  
  public String getPermission()
  {
    return "setup";
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Commands.AddTeamCommand
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */