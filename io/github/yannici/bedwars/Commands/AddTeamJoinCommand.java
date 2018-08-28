package io.github.yannici.bedwars.Commands;

import com.google.common.collect.ImmutableMap;
import io.github.yannici.bedwars.ChatWriter;
import io.github.yannici.bedwars.Game.Game;
import io.github.yannici.bedwars.Game.GameManager;
import io.github.yannici.bedwars.Game.GameState;
import io.github.yannici.bedwars.Game.Team;
import io.github.yannici.bedwars.Game.TeamJoinMetaDataValue;
import io.github.yannici.bedwars.Main;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class AddTeamJoinCommand
  extends BaseCommand
{
  public AddTeamJoinCommand(Main paramMain)
  {
    super(paramMain);
  }
  
  public String getPermission()
  {
    return "setup";
  }
  
  public String getCommand()
  {
    return "addteamjoin";
  }
  
  public String getName()
  {
    return Main._l("commands.addteamjoin.name");
  }
  
  public String getDescription()
  {
    return Main._l("commands.addteamjoin.desc");
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
    Player localPlayer1 = (Player)paramCommandSender;
    String str = (String)paramArrayList.get(1);
    Game localGame = getPlugin().getGameManager().getGame((String)paramArrayList.get(0));
    if (localGame == null)
    {
      localPlayer1.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + Main._l("errors.gamenotfound", ImmutableMap.of("game", ((String)paramArrayList.get(0)).toString()))));
      return false;
    }
    if (localGame.getState() == GameState.RUNNING)
    {
      paramCommandSender.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + Main._l("errors.notwhilegamerunning")));
      return false;
    }
    Team localTeam = localGame.getTeam(str);
    if (localTeam == null)
    {
      localPlayer1.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + Main._l("errors.teamnotfound")));
      return false;
    }
    if ((localGame.getLobby() == null) || (!localPlayer1.getWorld().equals(localGame.getLobby().getWorld())))
    {
      localPlayer1.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + Main._l("errors.mustbeinlobbyworld")));
      return false;
    }
    if (localPlayer1.hasMetadata("bw-addteamjoin")) {
      localPlayer1.removeMetadata("bw-addteamjoin", Main.getInstance());
    }
    localPlayer1.setMetadata("bw-addteamjoin", new TeamJoinMetaDataValue(localTeam));
    final Player localPlayer2 = localPlayer1;
    new BukkitRunnable()
    {
      public void run()
      {
        try
        {
          if (!localPlayer2.hasMetadata("bw-addteamjoin")) {
            return;
          }
          localPlayer2.removeMetadata("bw-addteamjoin", Main.getInstance());
        }
        catch (Exception localException) {}
      }
    }.runTaskLater(Main.getInstance(), 200L);
    localPlayer1.sendMessage(ChatWriter.pluginMessage(ChatColor.GREEN + Main._l("success.selectteamjoinentity")));
    return true;
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Commands.AddTeamJoinCommand
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */