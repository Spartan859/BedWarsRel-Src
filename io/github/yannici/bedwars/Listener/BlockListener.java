package io.github.yannici.bedwars.Listener;

import io.github.yannici.bedwars.ChatWriter;
import io.github.yannici.bedwars.Game.Game;
import io.github.yannici.bedwars.Game.GameManager;
import io.github.yannici.bedwars.Game.GameState;
import io.github.yannici.bedwars.Game.Region;
import io.github.yannici.bedwars.Game.Team;
import io.github.yannici.bedwars.Main;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BlockListener
  extends BaseListener
{
  @EventHandler(priority=EventPriority.HIGH)
  public void onBurn(BlockBurnEvent paramBlockBurnEvent)
  {
    Block localBlock = paramBlockBurnEvent.getBlock();
    if (localBlock == null) {
      return;
    }
    Game localGame = Main.getInstance().getGameManager().getGameByLocation(localBlock.getLocation());
    if (localGame == null) {
      return;
    }
    if (localGame.getState() == GameState.STOPPED) {
      return;
    }
    paramBlockBurnEvent.setCancelled(true);
  }
  
  @EventHandler
  public void onSpread(BlockSpreadEvent paramBlockSpreadEvent)
  {
    if (paramBlockSpreadEvent.isCancelled()) {
      return;
    }
    if (paramBlockSpreadEvent.getBlock() == null) {
      return;
    }
    Game localGame = Main.getInstance().getGameManager().getGameByLocation(paramBlockSpreadEvent.getBlock().getLocation());
    if (localGame == null) {
      return;
    }
    if (localGame.getState() != GameState.RUNNING) {
      return;
    }
    if ((paramBlockSpreadEvent.getNewState() == null) || (paramBlockSpreadEvent.getSource() == null)) {
      return;
    }
    if (paramBlockSpreadEvent.getNewState().getType().equals(Material.FIRE))
    {
      paramBlockSpreadEvent.setCancelled(true);
      return;
    }
    if (localGame.getRegion().isPlacedBlock(paramBlockSpreadEvent.getSource())) {
      localGame.getRegion().addPlacedBlock(paramBlockSpreadEvent.getBlock(), paramBlockSpreadEvent.getBlock().getState());
    } else {
      localGame.getRegion().addPlacedUnbreakableBlock(paramBlockSpreadEvent.getBlock(), paramBlockSpreadEvent.getBlock().getState());
    }
  }
  
  @EventHandler
  public void onForm(BlockFormEvent paramBlockFormEvent)
  {
    if (paramBlockFormEvent.isCancelled()) {
      return;
    }
    if (paramBlockFormEvent.getNewState().getType() != Material.SNOW) {
      return;
    }
    Game localGame = Main.getInstance().getGameManager().getGameByLocation(paramBlockFormEvent.getBlock().getLocation());
    if (localGame == null) {
      return;
    }
    if (localGame.getState() == GameState.STOPPED) {
      return;
    }
    paramBlockFormEvent.setCancelled(true);
  }
  
  @EventHandler
  public void onGrow(BlockGrowEvent paramBlockGrowEvent)
  {
    if (paramBlockGrowEvent.isCancelled()) {
      return;
    }
    Game localGame = Main.getInstance().getGameManager().getGameByLocation(paramBlockGrowEvent.getBlock().getLocation());
    if (localGame == null) {
      return;
    }
    if (localGame.getState() != GameState.RUNNING) {
      return;
    }
    paramBlockGrowEvent.setCancelled(true);
  }
  
  @EventHandler
  public void onFade(BlockFadeEvent paramBlockFadeEvent)
  {
    if (paramBlockFadeEvent.isCancelled()) {
      return;
    }
    Game localGame = Main.getInstance().getGameManager().getGameByLocation(paramBlockFadeEvent.getBlock().getLocation());
    if (localGame == null) {
      return;
    }
    if (localGame.getState() == GameState.STOPPED) {
      return;
    }
    if (!localGame.getRegion().isPlacedBlock(paramBlockFadeEvent.getBlock())) {
      paramBlockFadeEvent.setCancelled(true);
    }
  }
  
  @EventHandler(priority=EventPriority.NORMAL)
  public void onBreak(BlockBreakEvent paramBlockBreakEvent)
  {
    if (paramBlockBreakEvent.isCancelled()) {
      return;
    }
    Player localPlayer = paramBlockBreakEvent.getPlayer();
    if (localPlayer == null)
    {
      localObject1 = paramBlockBreakEvent.getBlock();
      if (localObject1 == null) {
        return;
      }
      localObject2 = Main.getInstance().getGameManager().getGameByLocation(((Block)localObject1).getLocation());
      if (localObject2 == null) {
        return;
      }
      if (((Game)localObject2).getState() != GameState.RUNNING) {
        return;
      }
      paramBlockBreakEvent.setCancelled(true);
      return;
    }
    Object localObject1 = Main.getInstance().getGameManager().getGameOfPlayer(localPlayer);
    if (localObject1 == null)
    {
      localObject2 = paramBlockBreakEvent.getBlock();
      if (!(((Block)localObject2).getState() instanceof Sign)) {
        return;
      }
      if ((!localPlayer.hasPermission("bw.setup")) || (paramBlockBreakEvent.isCancelled())) {
        return;
      }
      localObject3 = Main.getInstance().getGameManager().getGameBySignLocation(((Block)localObject2).getLocation());
      if (localObject3 == null) {
        return;
      }
      ((Game)localObject3).removeJoinSign(((Block)localObject2).getLocation());
      return;
    }
    if ((((Game)localObject1).getState() != GameState.RUNNING) && (((Game)localObject1).getState() != GameState.WAITING)) {
      return;
    }
    if (((Game)localObject1).getState() == GameState.WAITING)
    {
      paramBlockBreakEvent.setCancelled(true);
      return;
    }
    if (((Game)localObject1).isSpectator(localPlayer))
    {
      paramBlockBreakEvent.setCancelled(true);
      return;
    }
    Object localObject2 = ((Game)localObject1).getTargetMaterial();
    if (paramBlockBreakEvent.getBlock().getType() == localObject2)
    {
      paramBlockBreakEvent.setCancelled(true);
      ((Game)localObject1).handleDestroyTargetMaterial(localPlayer, paramBlockBreakEvent.getBlock());
      return;
    }
    Object localObject3 = paramBlockBreakEvent.getBlock();
    if (!((Game)localObject1).getRegion().isPlacedBlock((Block)localObject3))
    {
      if (localObject3 == null)
      {
        paramBlockBreakEvent.setCancelled(true);
        return;
      }
      if (Main.getInstance().isBreakableType(((Block)localObject3).getType()))
      {
        ((Game)localObject1).getRegion().addBreakedBlock((Block)localObject3);
        paramBlockBreakEvent.setCancelled(false);
        return;
      }
      paramBlockBreakEvent.setCancelled(true);
    }
    else
    {
      Object localObject4;
      Object localObject5;
      Object localObject6;
      if (!Main.getInstance().getBooleanConfig("friendlybreak", true))
      {
        localObject4 = ((Game)localObject1).getPlayerTeam(localPlayer);
        localObject5 = ((Team)localObject4).getPlayers().iterator();
        while (((Iterator)localObject5).hasNext())
        {
          localObject6 = (Player)((Iterator)localObject5).next();
          if (!localObject6.equals(localPlayer)) {
            if (((Player)localObject6).getLocation().getBlock().getRelative(BlockFace.DOWN).equals(paramBlockBreakEvent.getBlock()))
            {
              localPlayer.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + Main._l("ingame.no-friendlybreak")));
              paramBlockBreakEvent.setCancelled(true);
              return;
            }
          }
        }
      }
      if (paramBlockBreakEvent.getBlock().getType() == Material.ENDER_CHEST)
      {
        localObject4 = ((Game)localObject1).getTeams().values().iterator();
        while (((Iterator)localObject4).hasNext())
        {
          localObject5 = (Team)((Iterator)localObject4).next();
          localObject6 = ((Team)localObject5).getChests();
          if (((List)localObject6).contains(localObject3))
          {
            ((Team)localObject5).removeChest((Block)localObject3);
            ((Game)localObject1).broadcast(Main._l("ingame.teamchestdestroy"), ((Team)localObject5).getPlayers());
            break;
          }
        }
        localObject4 = new ItemStack(Material.ENDER_CHEST, 1);
        localObject5 = ((ItemStack)localObject4).getItemMeta();
        ((ItemMeta)localObject5).setDisplayName(Main._l("ingame.teamchest"));
        ((ItemStack)localObject4).setItemMeta((ItemMeta)localObject5);
        paramBlockBreakEvent.setCancelled(true);
        ((Block)localObject3).getDrops().clear();
        ((Block)localObject3).setType(Material.AIR);
        ((Block)localObject3).getWorld().dropItemNaturally(((Block)localObject3).getLocation(), (ItemStack)localObject4);
      }
      ((Game)localObject1).getRegion().removePlacedBlock((Block)localObject3);
    }
  }
  
  @EventHandler
  public void onIgnite(BlockIgniteEvent paramBlockIgniteEvent)
  {
    if (paramBlockIgniteEvent.isCancelled()) {
      return;
    }
    if ((paramBlockIgniteEvent.getIgnitingBlock() == null) && (paramBlockIgniteEvent.getIgnitingEntity() == null)) {
      return;
    }
    Game localGame = null;
    if (paramBlockIgniteEvent.getIgnitingBlock() == null)
    {
      if ((paramBlockIgniteEvent.getIgnitingEntity() instanceof Player)) {
        localGame = Main.getInstance().getGameManager().getGameOfPlayer((Player)paramBlockIgniteEvent.getIgnitingEntity());
      } else {
        localGame = Main.getInstance().getGameManager().getGameByLocation(paramBlockIgniteEvent.getIgnitingEntity().getLocation());
      }
    }
    else {
      localGame = Main.getInstance().getGameManager().getGameByLocation(paramBlockIgniteEvent.getIgnitingBlock().getLocation());
    }
    if (localGame == null) {
      return;
    }
    if (localGame.getState() == GameState.STOPPED) {
      return;
    }
    if ((paramBlockIgniteEvent.getCause() == BlockIgniteEvent.IgniteCause.ENDER_CRYSTAL) || (paramBlockIgniteEvent.getCause() == BlockIgniteEvent.IgniteCause.LIGHTNING) || (paramBlockIgniteEvent.getCause() == BlockIgniteEvent.IgniteCause.SPREAD))
    {
      paramBlockIgniteEvent.setCancelled(true);
      return;
    }
    if (paramBlockIgniteEvent.getIgnitingEntity() == null)
    {
      paramBlockIgniteEvent.setCancelled(true);
      return;
    }
    if (localGame.getState() == GameState.WAITING) {
      return;
    }
    if ((!localGame.getRegion().isPlacedBlock(paramBlockIgniteEvent.getIgnitingBlock())) && (paramBlockIgniteEvent.getIgnitingBlock() != null)) {
      localGame.getRegion().addPlacedBlock(paramBlockIgniteEvent.getIgnitingBlock(), paramBlockIgniteEvent.getIgnitingBlock().getState());
    }
  }
  
  @EventHandler(priority=EventPriority.NORMAL)
  public void onPlace(BlockPlaceEvent paramBlockPlaceEvent)
  {
    Player localPlayer = paramBlockPlaceEvent.getPlayer();
    Game localGame = Main.getInstance().getGameManager().getGameOfPlayer(localPlayer);
    if (localGame == null) {
      return;
    }
    if (localGame.getState() == GameState.STOPPED) {
      return;
    }
    if (localGame.getState() == GameState.WAITING)
    {
      paramBlockPlaceEvent.setCancelled(true);
      paramBlockPlaceEvent.setBuild(false);
      return;
    }
    if (localGame.getState() == GameState.RUNNING)
    {
      if (localGame.isSpectator(localPlayer))
      {
        paramBlockPlaceEvent.setCancelled(true);
        paramBlockPlaceEvent.setBuild(false);
        return;
      }
      Block localBlock = paramBlockPlaceEvent.getBlockPlaced();
      BlockState localBlockState = paramBlockPlaceEvent.getBlockReplacedState();
      if (localBlock.getType() == localGame.getTargetMaterial())
      {
        paramBlockPlaceEvent.setCancelled(true);
        paramBlockPlaceEvent.setBuild(false);
        return;
      }
      if (!localGame.getRegion().isInRegion(localBlock.getLocation()))
      {
        paramBlockPlaceEvent.setCancelled(true);
        paramBlockPlaceEvent.setBuild(false);
        return;
      }
      if ((localBlockState != null) && (!Main.getInstance().getBooleanConfig("place-in-liquid", true)) && ((localBlockState.getType().equals(Material.WATER)) || (localBlockState.getType().equals(Material.STATIONARY_WATER)) || (localBlockState.getType().equals(Material.LAVA)) || (localBlockState.getType().equals(Material.STATIONARY_LAVA))))
      {
        paramBlockPlaceEvent.setCancelled(true);
        paramBlockPlaceEvent.setBuild(false);
        return;
      }
      if (localBlock.getType() == Material.ENDER_CHEST)
      {
        Team localTeam = localGame.getPlayerTeam(localPlayer);
        if (localTeam.getInventory() == null) {
          localTeam.createTeamInventory();
        }
        localTeam.addChest(localBlock);
      }
      if (!paramBlockPlaceEvent.isCancelled()) {
        localGame.getRegion().addPlacedBlock(localBlock, localBlockState.getType().equals(Material.AIR) ? null : localBlockState);
      }
    }
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Listener.BlockListener
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */