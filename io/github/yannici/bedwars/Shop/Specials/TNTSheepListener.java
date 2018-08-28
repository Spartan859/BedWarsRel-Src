package io.github.yannici.bedwars.Shop.Specials;

import io.github.yannici.bedwars.Game.Game;
import io.github.yannici.bedwars.Game.GameManager;
import io.github.yannici.bedwars.Game.GameState;
import io.github.yannici.bedwars.Main;
import io.github.yannici.bedwars.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.SpawnEgg;

public class TNTSheepListener
  implements Listener
{
  public TNTSheepListener()
  {
    try
    {
      Class localClass = Main.getInstance().getVersionRelatedClass("TNTSheepRegister");
      ITNTSheepRegister localITNTSheepRegister = (ITNTSheepRegister)localClass.newInstance();
      localITNTSheepRegister.registerEntities(Main.getInstance().getIntConfig("specials.tntsheep.entity-id", 91));
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
  
  @EventHandler(priority=EventPriority.HIGHEST)
  public void onInteract(PlayerInteractEvent paramPlayerInteractEvent)
  {
    if (paramPlayerInteractEvent.getPlayer() == null) {
      return;
    }
    Player localPlayer = paramPlayerInteractEvent.getPlayer();
    Game localGame = Main.getInstance().getGameManager().getGameOfPlayer(localPlayer);
    if (localGame == null) {
      return;
    }
    if ((localGame.getState() != GameState.RUNNING) && (!localGame.isStopping())) {
      return;
    }
    if (paramPlayerInteractEvent.getPlayer().getItemInHand() == null) {
      return;
    }
    TNTSheep localTNTSheep = new TNTSheep();
    ItemStack localItemStack = localPlayer.getItemInHand();
    if (localItemStack.getType() != localTNTSheep.getItemMaterial()) {
      return;
    }
    if (!(localItemStack.getData() instanceof SpawnEgg)) {
      return;
    }
    if (((SpawnEgg)localItemStack.getData()).getSpawnedType() != EntityType.SHEEP) {
      return;
    }
    if (localGame.isSpectator(localPlayer)) {
      return;
    }
    Location localLocation = null;
    if ((paramPlayerInteractEvent.getClickedBlock() == null) || (paramPlayerInteractEvent.getClickedBlock().getRelative(BlockFace.UP).getType() != Material.AIR)) {
      localLocation = localPlayer.getLocation().getBlock().getRelative(Utils.getCardinalDirection(localPlayer.getLocation())).getLocation();
    } else {
      localLocation = paramPlayerInteractEvent.getClickedBlock().getRelative(BlockFace.UP).getLocation();
    }
    localTNTSheep.setPlayer(localPlayer);
    localTNTSheep.setGame(localGame);
    localTNTSheep.run(localLocation);
    paramPlayerInteractEvent.setCancelled(true);
  }
  
  @EventHandler(priority=EventPriority.HIGHEST)
  public void onInteractOtherUser(PlayerInteractEntityEvent paramPlayerInteractEntityEvent)
  {
    if (paramPlayerInteractEntityEvent.getPlayer() == null) {
      return;
    }
    Player localPlayer = paramPlayerInteractEntityEvent.getPlayer();
    Game localGame = Main.getInstance().getGameManager().getGameOfPlayer(localPlayer);
    if (localGame == null) {
      return;
    }
    if (localGame.getState() != GameState.RUNNING) {
      return;
    }
    if (paramPlayerInteractEntityEvent.getRightClicked() == null) {
      return;
    }
    if ((paramPlayerInteractEntityEvent.getRightClicked() instanceof ITNTSheep))
    {
      paramPlayerInteractEntityEvent.setCancelled(true);
      return;
    }
    if ((paramPlayerInteractEntityEvent.getRightClicked().getVehicle() != null) && ((paramPlayerInteractEntityEvent.getRightClicked().getVehicle() instanceof ITNTSheep)))
    {
      paramPlayerInteractEntityEvent.setCancelled(true);
      return;
    }
  }
  
  @EventHandler(priority=EventPriority.HIGHEST)
  public void onDamageEntity(EntityDamageByEntityEvent paramEntityDamageByEntityEvent)
  {
    if ((paramEntityDamageByEntityEvent.getCause().equals(EntityDamageEvent.DamageCause.CUSTOM)) || (paramEntityDamageByEntityEvent.getCause().equals(EntityDamageEvent.DamageCause.VOID)) || (paramEntityDamageByEntityEvent.getCause().equals(EntityDamageEvent.DamageCause.FALL))) {
      return;
    }
    if ((paramEntityDamageByEntityEvent.getEntity() instanceof ITNTSheep))
    {
      paramEntityDamageByEntityEvent.setDamage(0.0D);
      return;
    }
    if (!(paramEntityDamageByEntityEvent.getEntity() instanceof Player)) {
      return;
    }
    if (!(paramEntityDamageByEntityEvent.getDamager() instanceof TNTPrimed)) {
      return;
    }
    TNTPrimed localTNTPrimed = (TNTPrimed)paramEntityDamageByEntityEvent.getDamager();
    if (!(localTNTPrimed.getSource() instanceof Player)) {
      return;
    }
    Player localPlayer1 = (Player)localTNTPrimed.getSource();
    Player localPlayer2 = (Player)paramEntityDamageByEntityEvent.getEntity();
    Game localGame = Main.getInstance().getGameManager().getGameOfPlayer(localPlayer2);
    if (localGame == null) {
      return;
    }
    if (localGame.getState() != GameState.RUNNING) {
      return;
    }
    if ((localGame.isSpectator(localPlayer1)) || (localGame.isSpectator(localPlayer2)))
    {
      paramEntityDamageByEntityEvent.setCancelled(true);
      return;
    }
    io.github.yannici.bedwars.Game.Team localTeam1 = localGame.getPlayerTeam(localPlayer1);
    io.github.yannici.bedwars.Game.Team localTeam2 = localGame.getPlayerTeam(localPlayer2);
    if ((localTeam1.equals(localTeam2)) && (!localTeam1.getScoreboardTeam().allowFriendlyFire()))
    {
      paramEntityDamageByEntityEvent.setCancelled(true);
      return;
    }
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Shop.Specials.TNTSheepListener
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */