package io.github.yannici.bedwars.Listener;

import com.google.common.collect.ImmutableMap;
import io.github.yannici.bedwars.ChatWriter;
import io.github.yannici.bedwars.Game.Game;
import io.github.yannici.bedwars.Game.GameManager;
import io.github.yannici.bedwars.Game.GameState;
import io.github.yannici.bedwars.Game.Region;
import io.github.yannici.bedwars.Game.Team;
import io.github.yannici.bedwars.Game.TeamJoinMetaDataValue;
import io.github.yannici.bedwars.Main;
import io.github.yannici.bedwars.Utils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.PluginManager;

public class EntityListener
  extends BaseListener
{
  @EventHandler
  public void onEntitySpawn(CreatureSpawnEvent paramCreatureSpawnEvent)
  {
    if (Main.getInstance().getGameManager() == null) {
      return;
    }
    if (paramCreatureSpawnEvent.getLocation() == null) {
      return;
    }
    if (paramCreatureSpawnEvent.getLocation().getWorld() == null) {
      return;
    }
    Game localGame = Main.getInstance().getGameManager().getGameByLocation(paramCreatureSpawnEvent.getLocation());
    if (localGame == null) {
      return;
    }
    if (localGame.getState() == GameState.STOPPED) {
      return;
    }
    if ((paramCreatureSpawnEvent.getEntityType().equals(EntityType.CREEPER)) || (paramCreatureSpawnEvent.getEntityType().equals(EntityType.CAVE_SPIDER)) || (paramCreatureSpawnEvent.getEntityType().equals(EntityType.SPIDER)) || (paramCreatureSpawnEvent.getEntityType().equals(EntityType.ZOMBIE)) || (paramCreatureSpawnEvent.getEntityType().equals(EntityType.SKELETON)) || (paramCreatureSpawnEvent.getEntityType().equals(EntityType.SILVERFISH))) {
      paramCreatureSpawnEvent.setCancelled(true);
    }
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void onRegainHealth(EntityRegainHealthEvent paramEntityRegainHealthEvent)
  {
    if (paramEntityRegainHealthEvent.getEntityType() != EntityType.PLAYER) {
      return;
    }
    Player localPlayer = (Player)paramEntityRegainHealthEvent.getEntity();
    Game localGame = Main.getInstance().getGameManager().getGameOfPlayer(localPlayer);
    if (localGame == null) {
      return;
    }
    if (localGame.getState() != GameState.RUNNING) {
      return;
    }
    if (localPlayer.getHealth() >= localPlayer.getMaxHealth()) {
      localGame.setPlayerDamager(localPlayer, null);
    }
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void onInteractEntity(PlayerInteractEntityEvent paramPlayerInteractEntityEvent)
  {
    if (paramPlayerInteractEntityEvent.getRightClicked() == null) {
      return;
    }
    Entity localEntity = paramPlayerInteractEntityEvent.getRightClicked();
    Player localPlayer = paramPlayerInteractEntityEvent.getPlayer();
    if (!localPlayer.hasMetadata("bw-addteamjoin"))
    {
      if (!(localEntity instanceof LivingEntity)) {
        return;
      }
      localObject1 = (LivingEntity)localEntity;
      localObject2 = Main.getInstance().getGameManager().getGameOfPlayer(localPlayer);
      if (localObject2 == null) {
        return;
      }
      if (((Game)localObject2).getState() != GameState.WAITING) {
        return;
      }
      localObject3 = ((Game)localObject2).getTeam(ChatColor.stripColor(((LivingEntity)localObject1).getCustomName()));
      if (localObject3 == null) {
        return;
      }
      ((Game)localObject2).playerJoinTeam(localPlayer, (Team)localObject3);
      paramPlayerInteractEntityEvent.setCancelled(true);
      return;
    }
    Object localObject1 = localPlayer.getMetadata("bw-addteamjoin");
    if ((localObject1 == null) || (((List)localObject1).size() == 0)) {
      return;
    }
    paramPlayerInteractEntityEvent.setCancelled(true);
    Object localObject2 = (TeamJoinMetaDataValue)((List)localObject1).get(0);
    if (!((Boolean)((TeamJoinMetaDataValue)localObject2).value()).booleanValue()) {
      return;
    }
    if (!(localEntity instanceof LivingEntity))
    {
      localPlayer.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + Main._l("errors.entitynotcompatible")));
      return;
    }
    Object localObject3 = (LivingEntity)localEntity;
    ((LivingEntity)localObject3).setRemoveWhenFarAway(false);
    ((LivingEntity)localObject3).setCanPickupItems(false);
    ((LivingEntity)localObject3).setCustomName(((TeamJoinMetaDataValue)localObject2).getTeam().getChatColor() + ((TeamJoinMetaDataValue)localObject2).getTeam().getDisplayName());
    ((LivingEntity)localObject3).setCustomNameVisible(Main.getInstance().getBooleanConfig("jointeam-entity.show-name", true));
    if ((Utils.isSupportingTitles()) && (((LivingEntity)localObject3).getType().equals(EntityType.valueOf("ARMOR_STAND")))) {
      Utils.equipArmorStand((LivingEntity)localObject3, ((TeamJoinMetaDataValue)localObject2).getTeam());
    }
    localPlayer.removeMetadata("bw-addteamjoin", Main.getInstance());
    localPlayer.sendMessage(ChatWriter.pluginMessage(ChatColor.GREEN + Main._l("success.teamjoinadded", ImmutableMap.of("team", new StringBuilder().append(((TeamJoinMetaDataValue)localObject2).getTeam().getChatColor()).append(((TeamJoinMetaDataValue)localObject2).getTeam().getDisplayName()).append(ChatColor.GREEN).toString()))));
  }
  
  @EventHandler(priority=EventPriority.HIGHEST)
  public void onEntityDamage(EntityDamageEvent paramEntityDamageEvent)
  {
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(EntityType.PLAYER);
    if ((Main.getInstance().getServer().getPluginManager().isPluginEnabled("AntiAura")) || (Main.getInstance().getServer().getPluginManager().isPluginEnabled("AAC"))) {
      localArrayList.add(EntityType.SQUID);
    }
    if (localArrayList.contains(paramEntityDamageEvent.getEntityType())) {
      return;
    }
    Game localGame = Main.getInstance().getGameManager().getGameByLocation(paramEntityDamageEvent.getEntity().getLocation());
    if (localGame == null) {
      return;
    }
    if (localGame.getState() == GameState.STOPPED) {
      return;
    }
    paramEntityDamageEvent.setCancelled(true);
  }
  
  @EventHandler(priority=EventPriority.HIGHEST)
  public void onEntityDamageByEntity(EntityDamageByEntityEvent paramEntityDamageByEntityEvent)
  {
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(EntityType.PLAYER);
    if ((Main.getInstance().getServer().getPluginManager().isPluginEnabled("AntiAura")) || (Main.getInstance().getServer().getPluginManager().isPluginEnabled("AAC"))) {
      localArrayList.add(EntityType.SQUID);
    }
    if (localArrayList.contains(paramEntityDamageByEntityEvent.getEntityType())) {
      return;
    }
    Game localGame = Main.getInstance().getGameManager().getGameByLocation(paramEntityDamageByEntityEvent.getEntity().getLocation());
    if (localGame == null) {
      return;
    }
    if (localGame.getState() == GameState.STOPPED) {
      return;
    }
    paramEntityDamageByEntityEvent.setCancelled(true);
  }
  
  @EventHandler
  public void onEntityInteract(EntityInteractEvent paramEntityInteractEvent)
  {
    if (!(paramEntityInteractEvent.getEntity() instanceof Player)) {
      return;
    }
    if ((paramEntityInteractEvent.getBlock().getType() != Material.SOIL) && (paramEntityInteractEvent.getBlock().getType() != Material.WHEAT)) {
      return;
    }
    Player localPlayer = (Player)paramEntityInteractEvent.getEntity();
    Game localGame = Main.getInstance().getGameManager().getGameOfPlayer(localPlayer);
    if (localGame == null) {
      return;
    }
    if (localGame.getState() == GameState.WAITING) {
      paramEntityInteractEvent.setCancelled(true);
    }
  }
  
  @EventHandler(priority=EventPriority.HIGHEST)
  public void onExplodeDestroy(EntityExplodeEvent paramEntityExplodeEvent)
  {
    if (paramEntityExplodeEvent.isCancelled()) {
      return;
    }
    if (paramEntityExplodeEvent.getEntity() == null) {
      return;
    }
    if (paramEntityExplodeEvent.getEntity().getWorld() == null) {
      return;
    }
    Game localGame = Main.getInstance().getGameManager().getGameByLocation(paramEntityExplodeEvent.getEntity().getLocation());
    if (localGame == null) {
      return;
    }
    if (localGame.getState() == GameState.STOPPED) {
      return;
    }
    Iterator localIterator = paramEntityExplodeEvent.blockList().iterator();
    boolean bool1 = Main.getInstance().getBooleanConfig("explodes.destroy-worldblocks", false);
    boolean bool2 = Main.getInstance().getBooleanConfig("explodes.destroy-beds", false);
    if (!Main.getInstance().getBooleanConfig("explodes.drop-blocks", false)) {
      paramEntityExplodeEvent.setYield(0.0F);
    }
    Material localMaterial = localGame.getTargetMaterial();
    while (localIterator.hasNext())
    {
      Block localBlock = (Block)localIterator.next();
      if (!localGame.getRegion().isInRegion(localBlock.getLocation())) {
        localIterator.remove();
      } else if (((!bool1) && (!bool2)) || ((!bool1) && (bool2) && (localBlock.getType() != Material.BED_BLOCK) && (localBlock.getType() != Material.BED)))
      {
        if (!localGame.getRegion().isPlacedBlock(localBlock))
        {
          if (Main.getInstance().isBreakableType(localBlock.getType())) {
            localGame.getRegion().addBreakedBlock(localBlock);
          } else {
            localIterator.remove();
          }
        }
        else {
          localGame.getRegion().removePlacedBlock(localBlock);
        }
      }
      else if (localGame.getRegion().isPlacedBlock(localBlock)) {
        localGame.getRegion().removePlacedBlock(localBlock);
      } else if (localBlock.getType().equals(localMaterial))
      {
        if (!bool2)
        {
          localIterator.remove();
        }
        else if ((!paramEntityExplodeEvent.getEntityType().equals(EntityType.PRIMED_TNT)) && (!paramEntityExplodeEvent.getEntityType().equals(EntityType.MINECART_TNT)))
        {
          localIterator.remove();
        }
        else
        {
          TNTPrimed localTNTPrimed = (TNTPrimed)paramEntityExplodeEvent.getEntity();
          if (!(localTNTPrimed.getSource() instanceof Player))
          {
            localIterator.remove();
          }
          else
          {
            Player localPlayer = (Player)localTNTPrimed.getSource();
            if (!localGame.handleDestroyTargetMaterial(localPlayer, localBlock)) {
              localIterator.remove();
            } else {}
          }
        }
      }
      else {
        localGame.getRegion().addBreakedBlock(localBlock);
      }
    }
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Listener.EntityListener
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */