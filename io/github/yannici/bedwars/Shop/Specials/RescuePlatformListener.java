package io.github.yannici.bedwars.Shop.Specials;

import com.google.common.collect.ImmutableMap;
import io.github.yannici.bedwars.ChatWriter;
import io.github.yannici.bedwars.Game.Game;
import io.github.yannici.bedwars.Game.GameManager;
import io.github.yannici.bedwars.Game.GameState;
import io.github.yannici.bedwars.Game.Region;
import io.github.yannici.bedwars.Game.Team;
import io.github.yannici.bedwars.Game.TeamColor;
import io.github.yannici.bedwars.Main;
import io.github.yannici.bedwars.Utils;
import java.util.Iterator;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class RescuePlatformListener
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
    if ((paramPlayerInteractEvent.getAction().equals(Action.LEFT_CLICK_AIR)) || (paramPlayerInteractEvent.getAction().equals(Action.LEFT_CLICK_BLOCK))) {
      return;
    }
    RescuePlatform localRescuePlatform = new RescuePlatform();
    if (!paramPlayerInteractEvent.getMaterial().equals(localRescuePlatform.getItemMaterial())) {
      return;
    }
    Iterator localIterator = localGame.getSpecialItems().iterator();
    while (localIterator.hasNext())
    {
      localObject1 = (SpecialItem)localIterator.next();
      if ((localObject1 instanceof RescuePlatform))
      {
        localObject2 = (RescuePlatform)localObject1;
        if (((RescuePlatform)localObject2).getPlayer().equals(localPlayer))
        {
          int i = Main.getInstance().getConfig().getInt("specials.rescue-platform.using-wait-time", 20) - ((RescuePlatform)localObject2).getLivingTime();
          localPlayer.sendMessage(ChatWriter.pluginMessage(Main._l("ingame.specials.rescue-platform.left", ImmutableMap.of("time", String.valueOf(i)))));
          return;
        }
      }
    }
    boolean bool = Main.getInstance().getBooleanConfig("specials.rescue-platform.can-break", false);
    if (localPlayer.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR)
    {
      localPlayer.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + Main._l("errors.notinair")));
      return;
    }
    Object localObject1 = localPlayer.getLocation().clone();
    ((Location)localObject1).setY(((Location)localObject1).getY() - 1.0D);
    Object localObject2 = localGame.getPlayerTeam(localPlayer);
    ItemStack localItemStack = localPlayer.getInventory().getItemInHand();
    localItemStack.setAmount(localItemStack.getAmount() - 1);
    localPlayer.getInventory().setItem(localPlayer.getInventory().getHeldItemSlot(), localItemStack);
    localPlayer.updateInventory();
    for (BlockFace localBlockFace : BlockFace.values()) {
      if ((!localBlockFace.equals(BlockFace.DOWN)) && (!localBlockFace.equals(BlockFace.UP)))
      {
        Block localBlock = ((Location)localObject1).getBlock().getRelative(localBlockFace);
        if (localBlock.getType() == Material.AIR)
        {
          Material localMaterial = Utils.getMaterialByConfig("specials.rescue-platform.block", Material.STAINED_GLASS);
          localBlock.setType(localMaterial);
          if ((localMaterial.equals(Material.STAINED_GLASS)) || (localMaterial.equals(Material.WOOL))) {
            localBlock.setData(((Team)localObject2).getColor().getDyeColor().getData());
          }
          if (!bool) {
            localGame.getRegion().addPlacedUnbreakableBlock(localBlock, null);
          } else {
            localGame.getRegion().addPlacedBlock(localBlock, null);
          }
          localRescuePlatform.addPlatformBlock(localBlock);
        }
      }
    }
    localRescuePlatform.setActivatedPlayer(localPlayer);
    localRescuePlatform.setGame(localGame);
    localRescuePlatform.runTask();
    localGame.addSpecialItem(localRescuePlatform);
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Shop.Specials.RescuePlatformListener
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */