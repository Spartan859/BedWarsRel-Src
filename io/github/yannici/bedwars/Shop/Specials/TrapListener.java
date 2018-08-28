package io.github.yannici.bedwars.Shop.Specials;

import io.github.yannici.bedwars.Game.Game;
import io.github.yannici.bedwars.Game.GameManager;
import io.github.yannici.bedwars.Game.GameState;
import io.github.yannici.bedwars.Game.Region;
import io.github.yannici.bedwars.Game.Team;
import io.github.yannici.bedwars.Main;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class TrapListener
  implements Listener
{
  @EventHandler
  public void onMove(PlayerMoveEvent paramPlayerMoveEvent)
  {
    if (paramPlayerMoveEvent.isCancelled()) {
      return;
    }
    double d1 = Math.abs(paramPlayerMoveEvent.getFrom().getX() - paramPlayerMoveEvent.getTo().getX());
    double d2 = Math.abs(paramPlayerMoveEvent.getFrom().getZ() - paramPlayerMoveEvent.getTo().getZ());
    if ((d1 == 0.0D) && (d2 == 0.0D)) {
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
    if (localGame.isSpectator(localPlayer)) {
      return;
    }
    Trap localTrap1 = new Trap();
    if (!paramPlayerMoveEvent.getTo().getBlock().getType().equals(localTrap1.getItemMaterial())) {
      return;
    }
    Team localTeam = localGame.getPlayerTeam(localPlayer);
    if (localTeam == null) {
      return;
    }
    Iterator localIterator = localGame.getSpecialItems().iterator();
    while (localIterator.hasNext())
    {
      SpecialItem localSpecialItem = (SpecialItem)localIterator.next();
      if ((localSpecialItem instanceof Trap))
      {
        Trap localTrap2 = (Trap)localSpecialItem;
        if ((localTrap2.getLocation().equals(localPlayer.getLocation().getBlock().getLocation())) && (localTrap2.getPlacedTeam() != null))
        {
          if (localTrap2.getPlacedTeam().equals(localTeam)) {
            return;
          }
          localTrap2.activate(localPlayer);
          return;
        }
      }
    }
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void onBreak(BlockBreakEvent paramBlockBreakEvent)
  {
    if (paramBlockBreakEvent.isCancelled()) {
      return;
    }
    Object localObject1 = paramBlockBreakEvent.getBlock();
    if (paramBlockBreakEvent.getBlock().getType() != Material.TRIPWIRE)
    {
      localObject2 = paramBlockBreakEvent.getBlock().getRelative(BlockFace.UP);
      if (!((Block)localObject2).getType().equals(Material.TRIPWIRE)) {
        return;
      }
      localObject1 = localObject2;
    }
    Object localObject2 = paramBlockBreakEvent.getPlayer();
    Game localGame = Main.getInstance().getGameManager().getGameOfPlayer((Player)localObject2);
    if (localGame == null) {
      return;
    }
    if (localGame.getState() != GameState.RUNNING) {
      return;
    }
    if (paramBlockBreakEvent.getBlock().equals(localObject1))
    {
      paramBlockBreakEvent.setCancelled(true);
      return;
    }
    ((Block)localObject1).setType(Material.AIR);
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void onPlace(BlockPlaceEvent paramBlockPlaceEvent)
  {
    if (paramBlockPlaceEvent.isCancelled()) {
      return;
    }
    Player localPlayer = paramBlockPlaceEvent.getPlayer();
    Game localGame = Main.getInstance().getGameManager().getGameOfPlayer(localPlayer);
    if (localGame == null) {
      return;
    }
    if (localGame.getState() != GameState.RUNNING) {
      return;
    }
    Team localTeam = localGame.getPlayerTeam(localPlayer);
    if (localTeam == null)
    {
      paramBlockPlaceEvent.setCancelled(true);
      paramBlockPlaceEvent.setBuild(false);
      return;
    }
    Trap localTrap = new Trap();
    localTrap.create(localGame, localTeam, paramBlockPlaceEvent.getBlockPlaced().getLocation());
    localGame.getRegion().addPlacedUnbreakableBlock(paramBlockPlaceEvent.getBlockPlaced(), null);
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Shop.Specials.TrapListener
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */