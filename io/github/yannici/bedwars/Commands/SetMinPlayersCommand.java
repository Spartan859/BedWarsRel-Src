package io.github.yannici.bedwars.Commands;

import com.google.common.collect.ImmutableMap;
import io.github.yannici.bedwars.ChatWriter;
import io.github.yannici.bedwars.Game.Game;
import io.github.yannici.bedwars.Game.GameManager;
import io.github.yannici.bedwars.Game.GameState;
import io.github.yannici.bedwars.Main;
import io.github.yannici.bedwars.Utils;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class SetMinPlayersCommand
  extends BaseCommand
  implements ICommand
{
  public SetMinPlayersCommand(Main paramMain)
  {
    super(paramMain);
  }
  
  public String getCommand()
  {
    return "setminplayers";
  }
  
  public String getName()
  {
    return Main._l("commands.setminplayers.name");
  }
  
  public String getDescription()
  {
    return Main._l("commands.setminplayers.desc");
  }
  
  public String[] getArguments()
  {
    return new String[] { "game", "players" };
  }
  
  public boolean execute(CommandSender paramCommandSender, ArrayList<String> paramArrayList)
  {
    if (!paramCommandSender.hasPermission("bw." + getPermission())) {
      return false;
    }
    Game localGame = getPlugin().getGameManager().getGame((String)paramArrayList.get(0));
    String str = ((String)paramArrayList.get(1)).toString();
    if (localGame == null)
    {
      paramCommandSender.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + Main._l("errors.gamenotfound", ImmutableMap.of("game", ((String)paramArrayList.get(0)).toString()))));
      return false;
    }
    if (localGame.getState() == GameState.RUNNING)
    {
      paramCommandSender.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + Main._l("errors.notwhilegamerunning")));
      return false;
    }
    if (!Utils.isNumber(str))
    {
      paramCommandSender.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + Main._l("errors.minplayersnumeric")));
      return true;
    }
    localGame.setMinPlayers(Integer.valueOf(str).intValue());
    paramCommandSender.sendMessage(ChatWriter.pluginMessage(ChatColor.GREEN + Main._l("success.minplayersset")));
    return true;
  }
  
  public String getPermission()
  {
    return "setup";
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Commands.SetMinPlayersCommand
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */