package io.github.yannici.bedwars.Shop.Specials;

import io.github.yannici.bedwars.ChatWriter;
import io.github.yannici.bedwars.Events.BedwarsUseTNTSheepEvent;
import io.github.yannici.bedwars.Game.Game;
import io.github.yannici.bedwars.Game.GameState;
import io.github.yannici.bedwars.Game.Region;
import io.github.yannici.bedwars.Game.Team;
import io.github.yannici.bedwars.Game.TeamColor;
import io.github.yannici.bedwars.Main;
import java.util.ArrayList;
import java.util.Iterator;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

public class TNTSheep
  extends SpecialItem
{
  private Player player = null;
  private Game game = null;
  private ITNTSheep sheep = null;
  
  public Material getItemMaterial()
  {
    return Material.MONSTER_EGG;
  }
  
  public int getEntityTypeId()
  {
    return 91;
  }
  
  public Material getActivatedMaterial()
  {
    return null;
  }
  
  public void setPlayer(Player paramPlayer)
  {
    this.player = paramPlayer;
  }
  
  public void setGame(Game paramGame)
  {
    this.game = paramGame;
  }
  
  public Game getGame()
  {
    return this.game;
  }
  
  public Player getPlayer()
  {
    return this.player;
  }
  
  public ITNTSheep getSheep()
  {
    return this.sheep;
  }
  
  public void run(Location paramLocation)
  {
    ItemStack localItemStack = this.player.getItemInHand().clone();
    localItemStack.setAmount(1);
    this.player.getInventory().removeItem(new ItemStack[] { localItemStack });
    final Team localTeam = this.game.getPlayerTeam(this.player);
    Player localPlayer1 = findTargetPlayer(this.player);
    if (localPlayer1 == null)
    {
      this.player.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + Main._l("ingame.specials.tntsheep.no-target-found")));
      return;
    }
    BedwarsUseTNTSheepEvent localBedwarsUseTNTSheepEvent = new BedwarsUseTNTSheepEvent(this.game, this.player, localPlayer1, paramLocation);
    Main.getInstance().getServer().getPluginManager().callEvent(localBedwarsUseTNTSheepEvent);
    if (localBedwarsUseTNTSheepEvent.isCancelled()) {
      return;
    }
    final Player localPlayer2 = localBedwarsUseTNTSheepEvent.getTargetPlayer();
    final Location localLocation = localBedwarsUseTNTSheepEvent.getStartLocation();
    new BukkitRunnable()
    {
      public void run()
      {
        final TNTSheep localTNTSheep = TNTSheep.this;
        try
        {
          Class localClass = Main.getInstance().getVersionRelatedClass("TNTSheepRegister");
          ITNTSheepRegister localITNTSheepRegister = (ITNTSheepRegister)localClass.newInstance();
          TNTSheep.this.sheep = localITNTSheepRegister.spawnCreature(localTNTSheep, localLocation, TNTSheep.this.player, localPlayer2, localTeam.getColor().getDyeColor());
          new BukkitRunnable()
          {
            public void run()
            {
              localTNTSheep.getGame().getRegion().removeRemovingEntity(localTNTSheep.getSheep().getTNT().getVehicle());
              localTNTSheep.getGame().getRegion().removeRemovingEntity(localTNTSheep.getSheep().getTNT());
            }
          }.runTaskLater(Main.getInstance(), (Main.getInstance().getConfig().getDouble("specials.tntsheep.fuse-time", 8.0D) * 20.0D - 5.0D));
          new BukkitRunnable()
          {
            public void run()
            {
              localTNTSheep.getSheep().getTNT().remove();
              localTNTSheep.getSheep().remove();
              localTNTSheep.getGame().removeSpecialItem(localTNTSheep);
            }
          }.runTaskLater(Main.getInstance(), (Main.getInstance().getConfig().getDouble("specials.tntsheep.fuse-time", 8.0D) * 20.0D + 13.0D));
          TNTSheep.this.game.addSpecialItem(localTNTSheep);
        }
        catch (Exception localException)
        {
          localException.printStackTrace();
        }
      }
    }.runTask(Main.getInstance());
  }
  
  public void updateTNT()
  {
    new BukkitRunnable()
    {
      public void run()
      {
        final TNTSheep localTNTSheep = TNTSheep.this;
        if ((localTNTSheep.game.isStopping()) || (localTNTSheep.game.getState() != GameState.RUNNING)) {
          return;
        }
        if (localTNTSheep.sheep == null) {
          return;
        }
        if (localTNTSheep.sheep.getTNT() == null) {
          return;
        }
        TNTPrimed localTNTPrimed = localTNTSheep.sheep.getTNT();
        final int i = localTNTPrimed.getFuseTicks();
        if (i <= 0) {
          return;
        }
        final Entity localEntity = localTNTPrimed.getSource();
        final Location localLocation = localTNTPrimed.getLocation();
        final float f = localTNTPrimed.getYield();
        localTNTPrimed.leaveVehicle();
        localTNTPrimed.remove();
        new BukkitRunnable()
        {
          public void run()
          {
            TNTPrimed localTNTPrimed = (TNTPrimed)localTNTSheep.game.getRegion().getWorld().spawnEntity(localLocation, EntityType.PRIMED_TNT);
            localTNTPrimed.setFuseTicks(i);
            localTNTPrimed.setYield(f);
            localTNTPrimed.setIsIncendiary(false);
            localTNTSheep.sheep.setPassenger(localTNTPrimed);
            localTNTSheep.sheep.setTNT(localTNTPrimed);
            localTNTSheep.sheep.setTNTSource(localEntity);
            if (localTNTPrimed.getFuseTicks() >= 60) {
              localTNTSheep.updateTNT();
            }
          }
        }.runTaskLater(Main.getInstance(), 3L);
      }
    }.runTaskLater(Main.getInstance(), 60L);
  }
  
  private Player findTargetPlayer(Player paramPlayer)
  {
    Object localObject = null;
    double d1 = 1.7976931348623157E+308D;
    Team localTeam = this.game.getPlayerTeam(paramPlayer);
    ArrayList localArrayList = new ArrayList();
    localArrayList.addAll(this.game.getTeamPlayers());
    localArrayList.removeAll(localTeam.getPlayers());
    Iterator localIterator = localArrayList.iterator();
    while (localIterator.hasNext())
    {
      Player localPlayer = (Player)localIterator.next();
      double d2 = paramPlayer.getLocation().distance(localPlayer.getLocation());
      if (d2 < d1)
      {
        localObject = localPlayer;
        d1 = d2;
      }
    }
    return localObject;
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Shop.Specials.TNTSheep
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */