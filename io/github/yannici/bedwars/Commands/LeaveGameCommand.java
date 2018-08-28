package io.github.yannici.bedwars.Commands;

import io.github.yannici.bedwars.Game.Game;
import io.github.yannici.bedwars.Game.GameManager;
import io.github.yannici.bedwars.Main;
import java.util.ArrayList;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaveGameCommand
  extends BaseCommand
{
  public LeaveGameCommand(Main paramMain)
  {
    super(paramMain);
  }
  
  public String getCommand()
  {
    return "leave";
  }
  
  public String getName()
  {
    return Main._l("commands.leave.name");
  }
  
  public String getDescription()
  {
    return Main._l("commands.leave.desc");
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
    Player localPlayer = (Player)paramCommandSender;
    Game localGame = Main.getInstance().getGameManager().getGameOfPlayer(localPlayer);
    if (localGame == null) {
      return true;
    }
    localGame.playerLeave(localPlayer, false);
    return true;
  }
  
  public String getPermission()
  {
    return "base";
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Commands.LeaveGameCommand
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */