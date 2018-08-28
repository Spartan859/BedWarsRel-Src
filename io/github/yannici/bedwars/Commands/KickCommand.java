package io.github.yannici.bedwars.Commands;

import io.github.yannici.bedwars.ChatWriter;
import io.github.yannici.bedwars.Game.Game;
import io.github.yannici.bedwars.Game.GameManager;
import io.github.yannici.bedwars.Main;
import java.util.ArrayList;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KickCommand
  extends BaseCommand
  implements ICommand
{
  public KickCommand(Main paramMain)
  {
    super(paramMain);
  }
  
  public String getCommand()
  {
    return "kick";
  }
  
  public String getName()
  {
    return Main._l("commands.kick.name");
  }
  
  public String getDescription()
  {
    return Main._l("commands.kick.desc");
  }
  
  public String[] getArguments()
  {
    return new String[] { "player" };
  }
  
  public boolean execute(CommandSender paramCommandSender, ArrayList<String> paramArrayList)
  {
    if ((!super.hasPermission(paramCommandSender)) && (!paramCommandSender.isOp())) {
      return false;
    }
    Player localPlayer1 = (Player)paramCommandSender;
    Game localGame = Main.getInstance().getGameManager().getGameOfPlayer(localPlayer1);
    Player localPlayer2 = Main.getInstance().getServer().getPlayer(((String)paramArrayList.get(0)).toString());
    if (localGame == null)
    {
      localPlayer1.sendMessage(ChatWriter.pluginMessage(Main._l("errors.notingameforkick")));
      return true;
    }
    if ((localPlayer2 == null) || (!localPlayer2.isOnline()))
    {
      localPlayer1.sendMessage(ChatWriter.pluginMessage(Main._l("errors.playernotfound")));
      return true;
    }
    if (!localGame.isInGame(localPlayer2))
    {
      localPlayer1.sendMessage(ChatWriter.pluginMessage(Main._l("errors.playernotingame")));
      return true;
    }
    localGame.playerLeave(localPlayer2, true);
    return true;
  }
  
  public String getPermission()
  {
    return "kick";
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Commands.KickCommand
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */