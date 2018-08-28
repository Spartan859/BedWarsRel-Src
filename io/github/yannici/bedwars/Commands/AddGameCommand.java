package io.github.yannici.bedwars.Commands;

import com.google.common.collect.ImmutableMap;
import io.github.yannici.bedwars.ChatWriter;
import io.github.yannici.bedwars.Game.Game;
import io.github.yannici.bedwars.Game.GameManager;
import io.github.yannici.bedwars.Main;
import io.github.yannici.bedwars.Utils;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class AddGameCommand
  extends BaseCommand
{
  public AddGameCommand(Main paramMain)
  {
    super(paramMain);
  }
  
  public String getCommand()
  {
    return "addgame";
  }
  
  public String getName()
  {
    return Main._l("commands.addgame.name");
  }
  
  public String getDescription()
  {
    return Main._l("commands.addgame.desc");
  }
  
  public String[] getArguments()
  {
    return new String[] { "name", "minplayers" };
  }
  
  public boolean execute(CommandSender paramCommandSender, ArrayList<String> paramArrayList)
  {
    if (!paramCommandSender.hasPermission("bw." + getPermission())) {
      return false;
    }
    Game localGame = getPlugin().getGameManager().addGame((String)paramArrayList.get(0));
    String str = (String)paramArrayList.get(1);
    if (!Utils.isNumber(str))
    {
      paramCommandSender.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + Main._l("errors.minplayersmustnumber")));
      return false;
    }
    if (localGame == null)
    {
      paramCommandSender.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + Main._l("errors.gameexists")));
      return false;
    }
    int i = Integer.parseInt(str);
    if (i <= 0) {
      i = 1;
    }
    localGame.setMinPlayers(i);
    paramCommandSender.sendMessage(ChatWriter.pluginMessage(ChatColor.GREEN + Main._l("success.gameadded", ImmutableMap.of("game", ((String)paramArrayList.get(0)).toString()))));
    return true;
  }
  
  public String getPermission()
  {
    return "setup";
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Commands.AddGameCommand
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */