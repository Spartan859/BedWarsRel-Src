package io.github.yannici.bedwars.Game;

import io.github.yannici.bedwars.Main;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;

public class GameJoinSign
{
  private Game game = null;
  private Location signLocation = null;
  
  public GameJoinSign(Game paramGame, Location paramLocation)
  {
    this.game = paramGame;
    this.signLocation = paramLocation;
  }
  
  public void updateSign()
  {
    Sign localSign = (Sign)this.signLocation.getBlock().getState();
    String[] arrayOfString = getSignLines();
    for (int i = 0; i < arrayOfString.length; i++) {
      localSign.setLine(i, arrayOfString[i]);
    }
    localSign.update(true, true);
  }
  
  private String[] getSignLines()
  {
    String[] arrayOfString = new String[4];
    arrayOfString[0] = replacePlaceholder(ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("sign.first-line")));
    arrayOfString[1] = replacePlaceholder(ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("sign.second-line")));
    arrayOfString[2] = replacePlaceholder(ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("sign.third-line")));
    arrayOfString[3] = replacePlaceholder(ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString("sign.fourth-line")));
    return arrayOfString;
  }
  
  private String getMaxPlayersString()
  {
    int i = this.game.getMaxPlayers();
    int j = 0;
    if (this.game.getState() == GameState.RUNNING) {
      j = this.game.getTeamPlayers().size();
    } else if (this.game.getState() == GameState.WAITING) {
      j = this.game.getPlayers().size();
    } else {
      j = 0;
    }
    String str = String.valueOf(i);
    if (j >= i) {
      str = ChatColor.RED + str + ChatColor.WHITE;
    }
    return str;
  }
  
  private String getCurrentPlayersString()
  {
    int i = this.game.getMaxPlayers();
    int j = 0;
    if (this.game.getState() == GameState.RUNNING) {
      j = this.game.getTeamPlayers().size();
    } else if (this.game.getState() == GameState.WAITING) {
      j = this.game.getPlayers().size();
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
  
  private String getStatus()
  {
    String str = null;
    if ((this.game.getState() == GameState.WAITING) && (this.game.isFull())) {
      str = ChatColor.RED + Main._l("sign.gamestate.full");
    } else {
      str = Main._l("sign.gamestate." + this.game.getState().toString().toLowerCase());
    }
    return str;
  }
  
  private String replacePlaceholder(String paramString)
  {
    paramString = paramString.replace("$title$", Main._l("sign.firstline"));
    paramString = paramString.replace("$gamename$", this.game.getName());
    paramString = paramString.replace("$regionname$", this.game.getRegion().getName());
    paramString = paramString.replace("$maxplayers$", getMaxPlayersString());
    paramString = paramString.replace("$currentplayers$", getCurrentPlayersString());
    paramString = paramString.replace("$status$", getStatus());
    return paramString;
  }
  
  public Sign getSign()
  {
    BlockState localBlockState = this.signLocation.getBlock().getState();
    if (!(localBlockState instanceof Sign)) {
      return null;
    }
    return (Sign)localBlockState;
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Game.GameJoinSign
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */