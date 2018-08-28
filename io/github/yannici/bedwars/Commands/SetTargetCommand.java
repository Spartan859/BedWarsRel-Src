package io.github.yannici.bedwars.Commands;

import com.google.common.collect.ImmutableMap;
import io.github.yannici.bedwars.ChatWriter;
import io.github.yannici.bedwars.Game.Game;
import io.github.yannici.bedwars.Game.GameManager;
import io.github.yannici.bedwars.Game.GameState;
import io.github.yannici.bedwars.Game.Team;
import io.github.yannici.bedwars.Main;
import io.github.yannici.bedwars.Utils;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.material.Bed;

public class SetTargetCommand
  extends BaseCommand
  implements ICommand
{
  public SetTargetCommand(Main paramMain)
  {
    super(paramMain);
  }
  
  public String getCommand()
  {
    return "settarget";
  }
  
  public String getName()
  {
    return Main._l("commands.settarget.name");
  }
  
  public String getDescription()
  {
    return Main._l("commands.settarget.desc");
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
    String str = (String)paramArrayList.get(1);
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
    Team localTeam = localGame.getTeam(str);
    if (localTeam == null)
    {
      localPlayer.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + Main._l("errors.teamnotfound")));
      return false;
    }
    HashSet localHashSet = new HashSet();
    localHashSet.add(Material.AIR);
    Class localClass = Utils.getGenericTypeOfParameter(localPlayer.getClass(), "getTargetBlock", 0);
    Method localMethod = null;
    Block localBlock1 = null;
    try
    {
      try
      {
        localMethod = localPlayer.getClass().getMethod("getTargetBlock", new Class[] { Set.class, Integer.TYPE });
      }
      catch (Exception localException1)
      {
        try
        {
          localMethod = localPlayer.getClass().getMethod("getTargetBlock", new Class[] { HashSet.class, Integer.TYPE });
        }
        catch (Exception localException3)
        {
          localException3.printStackTrace();
        }
      }
      if (localClass.equals(Byte.class)) {
        localBlock1 = (Block)localMethod.invoke(localPlayer, new Object[] { null, Integer.valueOf(15) });
      } else {
        localBlock1 = (Block)localMethod.invoke(localPlayer, new Object[] { localHashSet, Integer.valueOf(15) });
      }
    }
    catch (Exception localException2)
    {
      localException2.printStackTrace();
    }
    Block localBlock2 = localPlayer.getLocation().getBlock().getRelative(BlockFace.DOWN);
    if ((localBlock1 == null) || (localBlock2 == null))
    {
      localPlayer.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + Main._l("errors.bedtargeting")));
      return false;
    }
    Material localMaterial = localGame.getTargetMaterial();
    if ((localBlock1.getType() != localMaterial) && (localBlock2.getType() != localMaterial))
    {
      localPlayer.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + Main._l("errors.bedtargeting")));
      return false;
    }
    Block localBlock3 = null;
    if (localBlock1.getType() == localMaterial) {
      localBlock3 = localBlock1;
    } else {
      localBlock3 = localBlock2;
    }
    if (localMaterial.equals(Material.BED_BLOCK))
    {
      Block localBlock4 = null;
      Bed localBed = (Bed)localBlock3.getState().getData();
      if (!localBed.isHeadOfBed())
      {
        localBlock4 = localBlock3;
        localBlock3 = Utils.getBedNeighbor(localBlock4);
      }
      else
      {
        localBlock4 = Utils.getBedNeighbor(localBlock3);
      }
      localTeam.setTargets(localBlock3, localBlock4);
    }
    else
    {
      localTeam.setTargets(localBlock3, null);
    }
    localPlayer.sendMessage(ChatWriter.pluginMessage(ChatColor.GREEN + Main._l("success.bedset", ImmutableMap.of("team", new StringBuilder().append(localTeam.getChatColor()).append(localTeam.getName()).append(ChatColor.GREEN).toString()))));
    return true;
  }
  
  public String getPermission()
  {
    return "setup";
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Commands.SetTargetCommand
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */