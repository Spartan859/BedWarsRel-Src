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

public class JoinGameCommand
  extends BaseCommand
{
  public JoinGameCommand(Main paramMain)
  {
    super(paramMain);
  }
  
  public String getCommand()
  {
    return "join";
  }
  
  public String getName()
  {
    return Main._l("commands.join.name");
  }
  
  public String getDescription()
  {
    return Main._l("commands.join.desc");
  }
  
  public String[] getArguments()
  {
    return new String[] { "game" };
  }
  
  public boolean execute(CommandSender paramCommandSender, ArrayList<String> paramArrayList)
  {
    if (!super.hasPermission(paramCommandSender)) {
      return false;
    }
    Player localPlayer = (Player)paramCommandSender;
    Game localGame1 = getPlugin().getGameManager().getGame((String)paramArrayList.get(0));
    Game localGame2 = Main.getInstance().getGameManager().getGameOfPlayer(localPlayer);
    if (localGame2 != null)
    {
      if (localGame2.getState() == GameState.RUNNING)
      {
        paramCommandSender.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + Main._l("errors.notwhileingame")));
        return false;
      }
      if (localGame2.getState() == GameState.WAITING) {
        localGame2.playerLeave(localPlayer, false);
      }
    }
    if (localGame1 == null)
    {
      paramCommandSender.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + Main._l("errors.gamenotfound", ImmutableMap.of("game", ((String)paramArrayList.get(0)).toString()))));
      return false;
    }
    if (localGame1.playerJoins(localPlayer)) {
      paramCommandSender.sendMessage(ChatWriter.pluginMessage(ChatColor.GREEN + Main._l("success.joined")));
    }
    return true;
  }
  
  public String getPermission()
  {
    return "base";
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Commands.JoinGameCommand
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */