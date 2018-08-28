package io.github.yannici.bedwars.Shop.Specials;

import io.github.yannici.bedwars.Game.Game;
import io.github.yannici.bedwars.Game.Region;
import io.github.yannici.bedwars.Main;
import io.github.yannici.bedwars.Utils;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class RescuePlatform
  extends SpecialItem
{
  private int livingTime = 0;
  private Player activatedPlayer = null;
  private List<Block> platformBlocks = null;
  private BukkitTask task = null;
  private Game game = null;
  
  public Material getItemMaterial()
  {
    return Utils.getMaterialByConfig("specials.rescue-platform.item", Material.BLAZE_ROD);
  }
  
  public int getLivingTime()
  {
    return this.livingTime;
  }
  
  public Player getPlayer()
  {
    return this.activatedPlayer;
  }
  
  public void addPlatformBlock(Block paramBlock)
  {
    this.platformBlocks.add(paramBlock);
  }
  
  public void runTask()
  {
    if ((Main.getInstance().getConfig().getInt("specials.rescue-platform.break-time", 10) == 0) && (Main.getInstance().getConfig().getInt("specials.rescue-platform.using-wait-time", 20) == 0)) {
      return;
    }
    this.task = new BukkitRunnable()
    {
      public void run()
      {
        RescuePlatform.access$008(RescuePlatform.this);
        if (RescuePlatform.this.livingTime == Main.getInstance().getConfig().getInt("specials.rescue-platform.break-time", 10))
        {
          Iterator localIterator = RescuePlatform.this.platformBlocks.iterator();
          while (localIterator.hasNext())
          {
            Block localBlock = (Block)localIterator.next();
            localBlock.getChunk().load(true);
            localBlock.setType(Material.AIR);
            RescuePlatform.this.game.getRegion().removePlacedUnbreakableBlock(localBlock);
          }
        }
        int i = Main.getInstance().getConfig().getInt("specials.rescue-platform.using-wait-time", 20);
        if ((RescuePlatform.this.livingTime >= i) && (i > 0))
        {
          RescuePlatform.this.game.removeRunningTask(this);
          RescuePlatform.this.game.removeSpecialItem(RescuePlatform.this);
          RescuePlatform.this.task = null;
          cancel();
          return;
        }
        if ((i <= 0) && (RescuePlatform.this.livingTime >= Main.getInstance().getConfig().getInt("specials.rescue-platform.break-time", 10)))
        {
          RescuePlatform.this.game.removeRunningTask(this);
          RescuePlatform.this.game.removeSpecialItem(RescuePlatform.this);
          RescuePlatform.this.task = null;
          cancel();
          return;
        }
      }
    }.runTaskTimer(Main.getInstance(), 20L, 20L);
    this.game.addRunningTask(this.task);
  }
  
  public Material getActivatedMaterial()
  {
    return null;
  }
  
  public void setActivatedPlayer(Player paramPlayer)
  {
    this.activatedPlayer = paramPlayer;
  }
  
  public void setGame(Game paramGame)
  {
    this.game = paramGame;
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Shop.Specials.RescuePlatform
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */