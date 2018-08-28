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
import org.bukkit.entity.Player;

public class SetSpawnCommand
  extends BaseCommand
  implements ICommand
{
  public SetSpawnCommand(Main paramMain)
  {
    super(paramMain);
  }
  
  public String getCommand()
  {
    return "setspawn";
  }
  
  public String getName()
  {
    return Main._l("commands.setspawn.name");
  }
  
  public String getDescription()
  {
    return Main._l("commands.setspawn.desc");
  }
  
  public String[] getArguments()
  {
    return new String[] { "game", "team" };
  }
  
  public boolean execute(CommandSender paramCommandSender, ArrayList<String> paramArrayList)
  {
    if (!super.hasPermission(paramCommandSender)) {
      return false;
    }
    Player localPlayer = (Player)paramCommandSender;
    Game localGame = getPlugin().getGameManager().getGame((String)paramArrayList.get(0));
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
    Team localTeam = localGame.getTeam((String)paramArrayList.get(1));
    if (localTeam == null)
    {
      localPlayer.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + Main._l("errors.teamnotfound")));
      return false;
    }
    localTeam.setSpawnLocation(localPlayer.getLocation());
    localPlayer.sendMessage(ChatWriter.pluginMessage(ChatColor.GREEN + Main._l("success.spawnset", ImmutableMap.of("team", new StringBuilder().append(localTeam.getChatColor()).append(localTeam.getDisplayName()).append(ChatColor.GREEN).toString()))));
    return true;
  }
  
  public String getPermission()
  {
    return "setup";
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Commands.SetSpawnCommand
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */