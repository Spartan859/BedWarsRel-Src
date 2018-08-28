package io.github.yannici.bedwars.Listener;

import io.github.yannici.bedwars.Game.Game;
import io.github.yannici.bedwars.Game.GameManager;
import io.github.yannici.bedwars.Game.GameState;
import io.github.yannici.bedwars.Game.Region;
import io.github.yannici.bedwars.Main;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerListener
  extends BaseListener
{
  @EventHandler
  public void onServerListPing(ServerListPingEvent paramServerListPingEvent)
  {
    if (!Main.getInstance().isBungee()) {
      return;
    }
    if (Main.getInstance().getGameManager().getGames().size() == 0) {
      return;
    }
    Game localGame = (Game)Main.getInstance().getGameManager().getGames().get(0);
    switch (1.$SwitchMap$io$github$yannici$bedwars$Game$GameState[localGame.getState().ordinal()])
    {
    case 1: 
      paramServerListPingEvent.setMotd(replacePlaceholder(localGame, ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("bungeecord.motds.stopped"))));
      break;
    case 2: 
      if (localGame.isFull()) {
        paramServerListPingEvent.setMotd(replacePlaceholder(localGame, ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("bungeecord.motds.full"))));
      } else {
        paramServerListPingEvent.setMotd(replacePlaceholder(localGame, ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("bungeecord.motds.lobby"))));
      }
      break;
    case 3: 
      paramServerListPingEvent.setMotd(replacePlaceholder(localGame, ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("bungeecord.motds.running"))));
    }
  }
  
  private String replacePlaceholder(Game paramGame, String paramString)
  {
    paramString = paramString.replace("$title$", Main._l("sign.firstline"));
    paramString = paramString.replace("$gamename$", paramGame.getName());
    paramString = paramString.replace("$regionname$", paramGame.getRegion().getName());
    paramString = paramString.replace("$maxplayers$", getMaxPlayersString(paramGame));
    paramString = paramString.replace("$currentplayers$", getCurrentPlayersString(paramGame));
    paramString = paramString.replace("$status$", getStatus(paramGame));
    return paramString;
  }
  
  private String getMaxPlayersString(Game paramGame)
  {
    int i = paramGame.getMaxPlayers();
    int j = 0;
    if (paramGame.getState() == GameState.RUNNING) {
      j = paramGame.getTeamPlayers().size();
    } else if (paramGame.getState() == GameState.WAITING) {
      j = paramGame.getPlayers().size();
    } else {
      j = 0;
    }
    String str = String.valueOf(i);
    if (j >= i) {
      str = ChatColor.RED + str + ChatColor.WHITE;
    }
    return str;
  }
  
  private String getCurrentPlayersString(Game paramGame)
  {
    int i = paramGame.getMaxPlayers();
    int j = 0;
    if (paramGame.getState() == GameState.RUNNING) {
      j = paramGame.getTeamPlayers().size();
    } else if (paramGame.getState() == GameState.WAITING) {
      j = paramGame.getPlayers().size();
    } else {
      j = 0;
    }
    String str = "0";
    if (j >= i) {
      str = ChatColor.RED + String.valueOf(j) + ChatColor.WHITE;
    } else {
      str = String.valueOf(j);
    }
    return str;
  }
  
  private String getStatus(Game paramGame)
  {
    String str = null;
    if ((paramGame.getState() == GameState.WAITING) && (paramGame.isFull())) {
      str = ChatColor.RED + Main._l("sign.gamestate.full");
    } else {
      str = Main._l("sign.gamestate." + paramGame.getState().toString().toLowerCase());
    }
    return str;
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Listener.ServerListener
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */