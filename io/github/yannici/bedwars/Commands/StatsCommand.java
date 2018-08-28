package io.github.yannici.bedwars.Commands;

import com.google.common.collect.ImmutableMap;
import io.github.yannici.bedwars.ChatWriter;
import io.github.yannici.bedwars.Main;
import io.github.yannici.bedwars.Statistics.PlayerStatistic;
import io.github.yannici.bedwars.Statistics.PlayerStatisticManager;
import io.github.yannici.bedwars.UUIDFetcher;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatsCommand
  extends BaseCommand
  implements ICommand
{
  public StatsCommand(Main paramMain)
  {
    super(paramMain);
  }
  
  public String getCommand()
  {
    return "stats";
  }
  
  public String getName()
  {
    return Main._l("commands.stats.name");
  }
  
  public String getDescription()
  {
    return Main._l("commands.stats.desc");
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
    if ((!localPlayer.hasPermission("bw.otherstats")) && (paramArrayList.size() > 0)) {
      paramArrayList.clear();
    }
    localPlayer.sendMessage(ChatWriter.pluginMessage(ChatColor.GREEN + "----------- " + Main._l("stats.header") + " -----------"));
    Object localObject1;
    if (paramArrayList.size() == 1)
    {
      localObject1 = ((String)paramArrayList.get(0)).toString();
      Object localObject2 = Main.getInstance().getServer().getPlayerExact((String)localObject1);
      if (localObject2 != null)
      {
        localPlayer.sendMessage(ChatWriter.pluginMessage(ChatColor.GRAY + Main._l("stats.name") + ": " + ChatColor.YELLOW + ((OfflinePlayer)localObject2).getName()));
        localObject3 = Main.getInstance().getPlayerStatisticManager().getStatistic((OfflinePlayer)localObject2);
        if (localObject3 == null)
        {
          localPlayer.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + Main._l("stats.statsnotfound", ImmutableMap.of("player", localObject1))));
          return true;
        }
        sendStats(localPlayer, (PlayerStatistic)localObject3);
        return true;
      }
      Object localObject3 = null;
      try
      {
        localObject3 = UUIDFetcher.getUUIDOf((String)localObject1);
        if (localObject3 == null)
        {
          localPlayer.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + Main._l("stats.statsnotfound", ImmutableMap.of("player", localObject1))));
          return true;
        }
      }
      catch (Exception localException)
      {
        localException.printStackTrace();
      }
      localObject2 = Main.getInstance().getServer().getOfflinePlayer((UUID)localObject3);
      if (localObject2 == null)
      {
        localPlayer.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + Main._l("stats.statsnotfound", ImmutableMap.of("player", localObject1))));
        return true;
      }
      PlayerStatistic localPlayerStatistic = Main.getInstance().getPlayerStatisticManager().getStatistic((OfflinePlayer)localObject2);
      if (localPlayerStatistic == null)
      {
        localPlayer.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + Main._l("stats.statsnotfound", ImmutableMap.of("player", ((OfflinePlayer)localObject2).getName()))));
        return true;
      }
      sendStats(localPlayer, localPlayerStatistic);
      return true;
    }
    if (paramArrayList.size() == 0)
    {
      localObject1 = Main.getInstance().getPlayerStatisticManager().getStatistic(localPlayer);
      if (localObject1 == null)
      {
        localPlayer.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + Main._l("stats.statsnotfound", ImmutableMap.of("player", localPlayer.getName()))));
        return true;
      }
      sendStats(localPlayer, (PlayerStatistic)localObject1);
      return true;
    }
    return false;
  }
  
  private void sendStats(Player paramPlayer, PlayerStatistic paramPlayerStatistic)
  {
    Iterator localIterator = paramPlayerStatistic.createStatisticLines(false, ChatColor.GRAY, ChatColor.YELLOW).iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      paramPlayer.sendMessage(str);
    }
  }
  
  public String getPermission()
  {
    return "base";
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Commands.StatsCommand
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */