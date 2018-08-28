package io.github.yannici.bedwars.Listener;

import io.github.yannici.bedwars.Game.Game;
import io.github.yannici.bedwars.Game.GameManager;
import io.github.yannici.bedwars.Main;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.SignChangeEvent;

public class SignListener
  extends BaseListener
{
  @EventHandler
  public void onSignChange(SignChangeEvent paramSignChangeEvent)
  {
    String str1 = paramSignChangeEvent.getLine(0).trim();
    if (!str1.equals("[bw]")) {
      return;
    }
    Player localPlayer = paramSignChangeEvent.getPlayer();
    if (!localPlayer.hasPermission("bw.setup")) {
      return;
    }
    String str2 = paramSignChangeEvent.getLine(1).trim();
    Game localGame = Main.getInstance().getGameManager().getGame(str2);
    if (localGame == null)
    {
      String str3 = Main._l("errors.gamenotfoundsimple");
      if (str3.length() > 16)
      {
        String[] arrayOfString = str3.split(" ", 4);
        for (int i = 0; i < arrayOfString.length; i++) {
          paramSignChangeEvent.setLine(i, ChatColor.RED + arrayOfString[i]);
        }
      }
      else
      {
        paramSignChangeEvent.setLine(0, ChatColor.RED + str3);
        paramSignChangeEvent.setLine(1, "");
        paramSignChangeEvent.setLine(2, "");
        paramSignChangeEvent.setLine(3, "");
      }
      return;
    }
    paramSignChangeEvent.setCancelled(true);
    localGame.addJoinSign(paramSignChangeEvent.getBlock().getLocation());
    localGame.updateSigns();
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Listener.SignListener
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */