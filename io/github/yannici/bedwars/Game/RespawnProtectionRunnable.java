package io.github.yannici.bedwars.Game;

import com.google.common.collect.ImmutableMap;
import io.github.yannici.bedwars.ChatWriter;
import io.github.yannici.bedwars.Main;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class RespawnProtectionRunnable
  extends BukkitRunnable
{
  private Game game = null;
  private Player player = null;
  private int length = 0;
  
  public RespawnProtectionRunnable(Game paramGame, Player paramPlayer, int paramInt)
  {
    this.game = paramGame;
    this.player = paramPlayer;
    this.length = paramInt;
  }
  
  public void run()
  {
    if (this.length > 0) {
      this.player.sendMessage(ChatWriter.pluginMessage(Main._l("ingame.protectionleft", ImmutableMap.of("length", String.valueOf(this.length)))));
    }
    if (this.length <= 0)
    {
      this.player.sendMessage(ChatWriter.pluginMessage(Main._l("ingame.protectionend")));
      this.game.removeProtection(this.player);
    }
    this.length -= 1;
  }
  
  public void runProtection()
  {
    runTaskTimerAsynchronously(Main.getInstance(), 5L, 20L);
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Game.RespawnProtectionRunnable
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */