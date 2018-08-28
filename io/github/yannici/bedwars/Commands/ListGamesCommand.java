package io.github.yannici.bedwars.Commands;

import com.google.common.collect.ImmutableMap;
import io.github.yannici.bedwars.Game.Game;
import io.github.yannici.bedwars.Game.GameCheckCode;
import io.github.yannici.bedwars.Game.GameManager;
import io.github.yannici.bedwars.Game.GameState;
import io.github.yannici.bedwars.Game.Region;
import io.github.yannici.bedwars.Main;
import io.github.yannici.bedwars.Utils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.util.ChatPaginator;
import org.bukkit.util.ChatPaginator.ChatPage;

public class ListGamesCommand
  extends BaseCommand
{
  public ListGamesCommand(Main paramMain)
  {
    super(paramMain);
  }
  
  public String getCommand()
  {
    return "list";
  }
  
  public String getName()
  {
    return Main._l("commands.list.name");
  }
  
  public String getDescription()
  {
    return Main._l("commands.list.desc");
  }
  
  public String[] getArguments()
  {
    return new String[0];
  }
  
  public boolean execute(CommandSender paramCommandSender, ArrayList<String> paramArrayList)
  {
    if (!paramCommandSender.hasPermission(new StringBuilder().append("bw.").append(getPermission()).toString())) {
      return false;
    }
    int i = 1;
    ArrayList localArrayList1 = new ArrayList();
    String str1;
    if (paramArrayList != null)
    {
      if ((paramArrayList.size() == 0) || (paramArrayList.size() > 1))
      {
        str1 = "1";
      }
      else
      {
        str1 = (String)paramArrayList.get(0);
        if (str1.isEmpty()) {
          str1 = "1";
        }
        if (!Utils.isNumber(str1)) {
          str1 = "1";
        }
      }
    }
    else {
      str1 = "1";
    }
    i = Integer.parseInt(str1);
    StringBuilder localStringBuilder = new StringBuilder();
    paramCommandSender.sendMessage(new StringBuilder().append(ChatColor.GREEN).append("---------- Bedwars Games ----------").toString());
    ArrayList localArrayList2 = Main.getInstance().getGameManager().getGames();
    Object localObject1 = localArrayList2.iterator();
    Object localObject2;
    int k;
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (Game)((Iterator)localObject1).next();
      GameCheckCode localGameCheckCode = ((Game)localObject2).checkGame();
      if ((localGameCheckCode == GameCheckCode.OK) || (paramCommandSender.hasPermission("bw.setup")))
      {
        localArrayList1.add(localObject2);
        k = 0;
        if (((Game)localObject2).getState() == GameState.RUNNING) {
          k = ((Game)localObject2).getCurrentPlayerAmount();
        } else {
          k = ((Game)localObject2).getPlayers().size();
        }
        localStringBuilder.append(new StringBuilder().append(ChatColor.YELLOW).append(localGameCheckCode != GameCheckCode.OK ? new StringBuilder().append(ChatColor.RED).append(((Game)localObject2).getName()).append(ChatColor.YELLOW).toString() : ((Game)localObject2).getName()).append(" - ").append(((Game)localObject2).getRegion().getName()).append(" - ").append(Main._l(new StringBuilder().append("sign.gamestate.").append(((Game)localObject2).getState().toString().toLowerCase()).toString())).append(ChatColor.YELLOW).append(" - ").append(Main._l("sign.players")).append(": ").append(ChatColor.WHITE).append("[").append(ChatColor.YELLOW).append(k).append(ChatColor.WHITE).append("/").append(ChatColor.YELLOW).append(((Game)localObject2).getMaxPlayers()).append(ChatColor.WHITE).append("]\n").toString());
      }
    }
    if (localArrayList1.size() == 0) {
      localStringBuilder.append(new StringBuilder().append(ChatColor.RED).append(Main._l("errors.nogames")).toString());
    }
    localObject1 = ChatPaginator.paginate(localStringBuilder.toString(), i);
    for (String str2 : ((ChatPaginator.ChatPage)localObject1).getLines()) {
      paramCommandSender.sendMessage(str2);
    }
    paramCommandSender.sendMessage(new StringBuilder().append(ChatColor.GREEN).append("---------- ").append(Main._l("default.pages", ImmutableMap.of("current", String.valueOf(((ChatPaginator.ChatPage)localObject1).getPageNumber()), "max", String.valueOf(((ChatPaginator.ChatPage)localObject1).getTotalPages())))).append(" ----------").toString());
    return true;
  }
  
  public String getPermission()
  {
    return "base";
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Commands.ListGamesCommand
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */