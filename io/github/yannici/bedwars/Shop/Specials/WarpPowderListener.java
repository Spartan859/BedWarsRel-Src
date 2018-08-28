package io.github.yannici.bedwars.Shop.Specials;

import io.github.yannici.bedwars.ChatWriter;
import io.github.yannici.bedwars.Game.Game;
import io.github.yannici.bedwars.Game.GameManager;
import io.github.yannici.bedwars.Game.GameState;
import io.github.yannici.bedwars.Main;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

public class WarpPowderListener
  implements Listener
{
  @EventHandler
  public void onInteract(PlayerInteractEvent paramPlayerInteractEvent)
  {
    Player localPlayer = paramPlayerInteractEvent.getPlayer();
    Game localGame = Main.getInstance().getGameManager().getGameOfPlayer(localPlayer);
    if (localGame == null) {
      return;
    }
    if (localGame.getState() != GameState.RUNNING) {
      return;
    }
    WarpPowder localWarpPowder1 = new WarpPowder();
    if ((!paramPlayerInteractEvent.getMaterial().equals(localWarpPowder1.getItemMaterial())) && (!paramPlayerInteractEvent.getMaterial().equals(localWarpPowder1.getActivatedMaterial()))) {
      return;
    }
    WarpPowder localWarpPowder2 = null;
    Iterator localIterator = localGame.getSpecialItems().iterator();
    while (localIterator.hasNext())
    {
      SpecialItem localSpecialItem = (SpecialItem)localIterator.next();
      if ((localSpecialItem instanceof WarpPowder))
      {
        localWarpPowder2 = (WarpPowder)localSpecialItem;
        if (localWarpPowder2.getPlayer().equals(localPlayer)) {
          break;
        }
        localWarpPowder2 = null;
      }
    }
    if (paramPlayerInteractEvent.getMaterial().equals(localWarpPowder1.getActivatedMaterial()))
    {
      if (paramPlayerInteractEvent.getItem().getItemMeta().getDisplayName() == null) {
        return;
      }
      if (!paramPlayerInteractEvent.getItem().getItemMeta().getDisplayName().equals(Main._l("ingame.specials.warp-powder.cancel"))) {
        return;
      }
      if (localWarpPowder2 != null)
      {
        localPlayer.getInventory().addItem(new ItemStack[] { localWarpPowder2.getStack() });
        localPlayer.updateInventory();
        localWarpPowder2.cancelTeleport(true, true);
        paramPlayerInteractEvent.setCancelled(true);
      }
      return;
    }
    if (localWarpPowder2 != null)
    {
      localPlayer.sendMessage(ChatWriter.pluginMessage(Main._l("ingame.specials.warp-powder.multiuse")));
      return;
    }
    if (localPlayer.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) {
      return;
    }
    localWarpPowder1.setPlayer(localPlayer);
    localWarpPowder1.setGame(localGame);
    localWarpPowder1.runTask();
    paramPlayerInteractEvent.setCancelled(true);
  }
  
  @EventHandler
  public void onMove(PlayerMoveEvent paramPlayerMoveEvent)
  {
    if (paramPlayerMoveEvent.isCancelled()) {
      return;
    }
    if (paramPlayerMoveEvent.getFrom().getBlock().equals(paramPlayerMoveEvent.getTo().getBlock())) {
      return;
    }
    Player localPlayer = paramPlayerMoveEvent.getPlayer();
    Game localGame = Main.getInstance().getGameManager().getGameOfPlayer(localPlayer);
    if (localGame == null) {
      return;
    }
    if (localGame.getState() != GameState.RUNNING) {
      return;
    }
    WarpPowder localWarpPowder = null;
    Iterator localIterator = localGame.getSpecialItems().iterator();
    while (localIterator.hasNext())
    {
      SpecialItem localSpecialItem = (SpecialItem)localIterator.next();
      if ((localSpecialItem instanceof WarpPowder))
      {
        localWarpPowder = (WarpPowder)localSpecialItem;
        if (localWarpPowder.getPlayer().equals(localPlayer)) {
          break;
        }
        localWarpPowder = null;
      }
    }
    if (localWarpPowder != null)
    {
      localPlayer.getInventory().addItem(new ItemStack[] { localWarpPowder.getStack() });
      localPlayer.updateInventory();
      localWarpPowder.cancelTeleport(true, true);
      return;
    }
  }
  
  @EventHandler
  public void onDamage(EntityDamageEvent paramEntityDamageEvent)
  {
    if (paramEntityDamageEvent.isCancelled()) {
      return;
    }
    if (!(paramEntityDamageEvent.getEntity() instanceof Player)) {
      return;
    }
    Player localPlayer = (Player)paramEntityDamageEvent.getEntity();
    Game localGame = Main.getInstance().getGameManager().getGameOfPlayer(localPlayer);
    if (localGame == null) {
      return;
    }
    if (localGame.getState() != GameState.RUNNING) {
      return;
    }
    if (localGame.isSpectator(localPlayer)) {
      return;
    }
    WarpPowder localWarpPowder = null;
    Iterator localIterator = localGame.getSpecialItems().iterator();
    while (localIterator.hasNext())
    {
      SpecialItem localSpecialItem = (SpecialItem)localIterator.next();
      if ((localSpecialItem instanceof WarpPowder))
      {
        localWarpPowder = (WarpPowder)localSpecialItem;
        if (localWarpPowder.getPlayer().equals(localPlayer)) {
          break;
        }
        localWarpPowder = null;
      }
    }
    if (localWarpPowder != null)
    {
      localWarpPowder.cancelTeleport(true, true);
      return;
    }
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Shop.Specials.WarpPowderListener
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */