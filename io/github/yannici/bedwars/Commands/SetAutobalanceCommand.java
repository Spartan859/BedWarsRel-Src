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

public class SetAutobalanceCommand
  extends BaseCommand
  implements ICommand
{
  public SetAutobalanceCommand(Main paramMain)
  {
    super(paramMain);
  }
  
  public String getCommand()
  {
    return "setautobalance";
  }
  
  public String getName()
  {
    return Main._l("commands.setautobalance.name");
  }
  
  public String getDescription()
  {
    return Main._l("commands.setautobalance.desc");
  }
  
  public String[] getArguments()
  {
    return new String[] { "game", "value" };
  }
  
  public boolean execute(CommandSender paramCommandSender, ArrayList<String> paramArrayList)
  {
    if (!paramCommandSender.hasPermission("bw." + getPermission())) {
      return false;
    }
    Player localPlayer = (Player)paramCommandSender;
    Game localGame = getPlugin().getGameManager().getGame((String)paramArrayList.get(0));
    String str = ((String)paramArrayList.get(1)).toString().trim();
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
    if ((!str.equalsIgnoreCase("true")) && (!str.equalsIgnoreCase("false")) && (!str.equalsIgnoreCase("off")) && (!str.equalsIgnoreCase("on")) && (!str.equalsIgnoreCase("1")) && (!str.equalsIgnoreCase("0")))
    {
      localPlayer.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + Main._l("errors.wrongvalueonoff")));
      return true;
    }
    boolean bool = false;
    if ((str.equalsIgnoreCase("true")) || (str.equalsIgnoreCase("on")) || (str.equalsIgnoreCase("1"))) {
      bool = true;
    }
    localGame.setAutobalance(bool);
    if (bool) {
      localPlayer.sendMessage(ChatWriter.pluginMessage(ChatColor.GREEN + Main._l("success.autobalanceseton")));
    } else {
      localPlayer.sendMessage(ChatWriter.pluginMessage(ChatColor.GREEN + Main._l("success.autobalancesetoff")));
    }
    return true;
  }
  
  public String getPermission()
  {
    return "setup";
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Commands.SetAutobalanceCommand
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */