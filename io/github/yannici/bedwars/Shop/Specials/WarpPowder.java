package io.github.yannici.bedwars.Shop.Specials;

import com.google.common.collect.ImmutableMap;
import io.github.yannici.bedwars.ChatWriter;
import io.github.yannici.bedwars.Game.Game;
import io.github.yannici.bedwars.Game.Team;
import io.github.yannici.bedwars.Main;
import io.github.yannici.bedwars.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class WarpPowder
  extends SpecialItem
{
  private BukkitTask teleportingTask = null;
  private double teleportingTime = 6.0D;
  private Player player = null;
  private Game game = null;
  private int fullTeleportingTime = 6;
  private ItemStack stack = null;
  
  public Material getItemMaterial()
  {
    return Material.SULPHUR;
  }
  
  public Material getActivatedMaterial()
  {
    return Material.GLOWSTONE_DUST;
  }
  
  public Player getPlayer()
  {
    return this.player;
  }
  
  public void cancelTeleport(boolean paramBoolean1, boolean paramBoolean2)
  {
    try
    {
      this.teleportingTask.cancel();
    }
    catch (Exception localException) {}
    this.teleportingTime = Main.getInstance().getIntConfig("specials.warp-powder.teleport-time", 6);
    this.game.removeRunningTask(this.teleportingTask);
    this.player.setLevel(0);
    if (paramBoolean1) {
      this.game.removeSpecialItem(this);
    }
    if (paramBoolean2) {
      this.player.sendMessage(ChatWriter.pluginMessage(Main._l("ingame.specials.warp-powder.cancelled")));
    }
    this.player.getInventory().removeItem(new ItemStack[] { getCancelItemStack() });
    this.player.updateInventory();
  }
  
  private ItemStack getCancelItemStack()
  {
    ItemStack localItemStack = new ItemStack(getActivatedMaterial(), 1);
    ItemMeta localItemMeta = localItemStack.getItemMeta();
    localItemMeta.setDisplayName(Main._l("ingame.specials.warp-powder.cancel"));
    localItemStack.setItemMeta(localItemMeta);
    return localItemStack;
  }
  
  public void runTask()
  {
    ItemStack localItemStack = this.player.getInventory().getItemInHand();
    this.stack = localItemStack.clone();
    this.stack.setAmount(1);
    localItemStack.setAmount(localItemStack.getAmount() - 1);
    this.player.getInventory().setItem(this.player.getInventory().getHeldItemSlot(), localItemStack);
    this.player.getInventory().addItem(new ItemStack[] { getCancelItemStack() });
    this.player.updateInventory();
    this.teleportingTime = Main.getInstance().getIntConfig("specials.warp-powder.teleport-time", 6);
    this.player.sendMessage(ChatWriter.pluginMessage(Main._l("ingame.specials.warp-powder.start", ImmutableMap.of("time", String.valueOf(this.fullTeleportingTime)))));
    this.teleportingTask = new BukkitRunnable()
    {
      public double through = 0.0D;
      public String particle = Main.getInstance().getStringConfig("specials.warp-powder.particle", "fireworksSpark");
      public boolean showParticle = Main.getInstance().getBooleanConfig("specials.warp-powder.show-particles", true);
      
      public void run()
      {
        try
        {
          int i = 20;
          double d1 = 1.0D;
          double d2 = 1.0D;
          double d3 = 15.0D;
          double d4 = WarpPowder.this.fullTeleportingTime;
          double d5 = WarpPowder.this.teleportingTime;
          double d6 = Math.ceil(2.0D / d3 * (d4 * 20.0D / d3)) / 20.0D;
          WarpPowder.this.teleportingTime = (d5 - d6);
          Team localTeam = WarpPowder.this.game.getPlayerTeam(WarpPowder.this.player);
          Location localLocation1 = localTeam.getSpawnLocation();
          if (WarpPowder.this.teleportingTime <= 1.0D)
          {
            WarpPowder.this.player.teleport(localTeam.getSpawnLocation());
            WarpPowder.this.cancelTeleport(true, false);
            return;
          }
          WarpPowder.this.player.setLevel((int)WarpPowder.this.teleportingTime);
          if (!this.showParticle) {
            return;
          }
          Location localLocation2 = WarpPowder.this.player.getLocation();
          double d7 = d2 / d3 * this.through;
          for (int j = 0; j < 20; j++)
          {
            double d8 = 360.0D / i * j;
            double d9 = d1 * Math.sin(Math.toRadians(d8));
            double d10 = d1 * Math.cos(Math.toRadians(d8));
            Location localLocation3 = new Location(localLocation2.getWorld(), localLocation2.getX() + d9, localLocation2.getY() + d7, localLocation2.getZ() + d10);
            Utils.createParticleInGame(WarpPowder.this.game, this.particle, localLocation3);
            Location localLocation4 = new Location(localLocation1.getWorld(), localLocation1.getX() + d9, localLocation1.getY() + d7, localLocation1.getZ() + d10);
            Utils.createParticleInGame(WarpPowder.this.game, this.particle, localLocation4);
          }
          this.through += 1.0D;
        }
        catch (Exception localException)
        {
          localException.printStackTrace();
          cancel();
          WarpPowder.this.cancelTeleport(true, false);
        }
      }
    }.runTaskTimer(Main.getInstance(), 0L, Math.ceil(0.1333333333333333D * (this.fullTeleportingTime * 20 / 15)));
    this.game.addRunningTask(this.teleportingTask);
    this.game.addSpecialItem(this);
  }
  
  public void setPlayer(Player paramPlayer)
  {
    this.player = paramPlayer;
  }
  
  public void setGame(Game paramGame)
  {
    this.game = paramGame;
  }
  
  public ItemStack getStack()
  {
    return this.stack;
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Shop.Specials.WarpPowder
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */