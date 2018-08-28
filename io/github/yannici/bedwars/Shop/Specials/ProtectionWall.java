package io.github.yannici.bedwars.Shop.Specials;

import com.google.common.collect.ImmutableMap;
import io.github.yannici.bedwars.ChatWriter;
import io.github.yannici.bedwars.Game.Game;
import io.github.yannici.bedwars.Game.Region;
import io.github.yannici.bedwars.Main;
import io.github.yannici.bedwars.Utils;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class ProtectionWall
  extends SpecialItem
{
  private List<Block> wallBlocks = null;
  private Player owner = null;
  private Game game = null;
  private int livingTime = 0;
  private BukkitTask task = null;
  
  public Material getItemMaterial()
  {
    return Utils.getMaterialByConfig("specials.protection-wall.item", Material.BRICK);
  }
  
  public List<Block> getWallBlocks()
  {
    return this.wallBlocks;
  }
  
  public Material getActivatedMaterial()
  {
    return null;
  }
  
  public Player getOwner()
  {
    return this.owner;
  }
  
  public Game getGame()
  {
    return this.game;
  }
  
  public int getLivingTime()
  {
    return this.livingTime;
  }
  
  public void create(Player paramPlayer, Game paramGame)
  {
    this.owner = paramPlayer;
    this.game = paramGame;
    int i = Main.getInstance().getIntConfig("specials.protection-wall.break-time", 0);
    int j = Main.getInstance().getIntConfig("specials.protection-wall.wait-time", 20);
    int k = Main.getInstance().getIntConfig("specials.protection-wall.width", 4);
    int m = Main.getInstance().getIntConfig("specials.protection-wall.height", 4);
    int n = Main.getInstance().getIntConfig("specials.protection-wall.distance", 2);
    boolean bool = Main.getInstance().getBooleanConfig("specials.protection-wall.can-break", true);
    Material localMaterial = Utils.getMaterialByConfig("specials.protection-wall.block", Material.SANDSTONE);
    if (k % 2 == 0) {
      try
      {
        throw new IllegalArgumentException("The width of a protection block has to be odd!");
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        localIllegalArgumentException.printStackTrace();
        return;
      }
    }
    if (paramPlayer.getEyeLocation().getBlock().getType() != Material.AIR)
    {
      paramPlayer.sendMessage(ChatWriter.pluginMessage(Main._l("ingame.specials.protection-wall.not-usable-here")));
      return;
    }
    if (j > 0)
    {
      localObject = getLivingWall();
      if (localObject != null)
      {
        int i1 = j - ((ProtectionWall)localObject).getLivingTime();
        paramPlayer.sendMessage(ChatWriter.pluginMessage(Main._l("ingame.specials.protection-wall.left", ImmutableMap.of("time", String.valueOf(i1)))));
        return;
      }
    }
    Object localObject = Utils.getDirectionLocation(paramPlayer.getLocation(), n);
    ItemStack localItemStack = paramPlayer.getInventory().getItemInHand();
    localItemStack.setAmount(localItemStack.getAmount() - 1);
    paramPlayer.getInventory().setItem(paramPlayer.getInventory().getHeldItemSlot(), localItemStack);
    paramPlayer.updateInventory();
    BlockFace localBlockFace = Utils.getCardinalDirection(paramPlayer.getLocation());
    int i2 = (int)Math.floor(k / 2.0D);
    for (int i3 = i2 * -1; i3 < k - i2; i3++) {
      for (int i4 = 0; i4 < m; i4++)
      {
        Location localLocation = ((Location)localObject).clone();
        switch (2.$SwitchMap$org$bukkit$block$BlockFace[localBlockFace.ordinal()])
        {
        case 1: 
        case 2: 
        case 3: 
          localLocation.add(0.0D, i4, i3);
          break;
        case 4: 
        case 5: 
          localLocation.add(i3, i4, 0.0D);
          break;
        case 6: 
          localLocation.add(i3, i4, i3);
          break;
        case 7: 
          localLocation.add(i3, i4, i3 * -1);
          break;
        case 8: 
          localLocation.add(i3 * -1, i4, i3);
          break;
        case 9: 
          localLocation.add(i3 * -1, i4, i3 * -1);
          break;
        }
        Block localBlock = localLocation.getBlock();
        if (localBlock.getType().equals(Material.AIR))
        {
          localBlock.setType(localMaterial);
          if (!bool) {
            paramGame.getRegion().addPlacedUnbreakableBlock(localLocation.getBlock(), null);
          } else {
            paramGame.getRegion().addPlacedBlock(localLocation.getBlock(), null);
          }
          this.wallBlocks.add(localBlock);
        }
      }
    }
    if ((i > 0) || (j > 0))
    {
      createTask(i, j);
      paramGame.addSpecialItem(this);
    }
  }
  
  private void createTask(final int paramInt1, final int paramInt2)
  {
    this.task = new BukkitRunnable()
    {
      public void run()
      {
        ProtectionWall.access$008(ProtectionWall.this);
        if ((paramInt1 > 0) && (ProtectionWall.this.livingTime == paramInt1))
        {
          Iterator localIterator = ProtectionWall.this.wallBlocks.iterator();
          while (localIterator.hasNext())
          {
            Block localBlock = (Block)localIterator.next();
            localBlock.getChunk().load(true);
            localBlock.setType(Material.AIR);
            ProtectionWall.this.game.getRegion().removePlacedUnbreakableBlock(localBlock);
          }
        }
        if ((ProtectionWall.this.livingTime >= paramInt2) && (paramInt2 > 0))
        {
          ProtectionWall.this.game.removeRunningTask(this);
          ProtectionWall.this.game.removeSpecialItem(ProtectionWall.this);
          ProtectionWall.this.task = null;
          cancel();
          return;
        }
        if ((paramInt1 > 0) && (paramInt2 <= 0) && (ProtectionWall.this.livingTime >= paramInt1))
        {
          ProtectionWall.this.game.removeRunningTask(this);
          ProtectionWall.this.game.removeSpecialItem(ProtectionWall.this);
          ProtectionWall.this.task = null;
          cancel();
          return;
        }
      }
    }.runTaskTimer(Main.getInstance(), 20L, 20L);
    this.game.addRunningTask(this.task);
  }
  
  private ProtectionWall getLivingWall()
  {
    Iterator localIterator = this.game.getSpecialItems().iterator();
    while (localIterator.hasNext())
    {
      SpecialItem localSpecialItem = (SpecialItem)localIterator.next();
      if ((localSpecialItem instanceof ProtectionWall))
      {
        ProtectionWall localProtectionWall = (ProtectionWall)localSpecialItem;
        if (localProtectionWall.getOwner().equals(getOwner())) {
          return localProtectionWall;
        }
      }
    }
    return null;
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Shop.Specials.ProtectionWall
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */