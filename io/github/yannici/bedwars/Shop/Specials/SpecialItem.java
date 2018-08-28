package io.github.yannici.bedwars.Shop.Specials;

import io.github.yannici.bedwars.Game.Game;
import io.github.yannici.bedwars.Game.GameManager;
import io.github.yannici.bedwars.Game.GameState;
import io.github.yannici.bedwars.Main;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

public abstract class SpecialItem
{
  private static List<Class<? extends SpecialItem>> availableSpecials = new ArrayList();
  
  public abstract Material getItemMaterial();
  
  public abstract Material getActivatedMaterial();
  
  public boolean returnPlayerEvent(Player paramPlayer)
  {
    if ((!paramPlayer.getItemInHand().getType().equals(getItemMaterial())) && (!paramPlayer.getItemInHand().getType().equals(getActivatedMaterial())) && (getActivatedMaterial() != null)) {
      return true;
    }
    Game localGame = Main.getInstance().getGameManager().getGameOfPlayer(paramPlayer);
    if (localGame == null) {
      return true;
    }
    if (localGame.getState() != GameState.RUNNING) {
      return true;
    }
    return localGame.isSpectator(paramPlayer);
  }
  
  public static void loadSpecials()
  {
    availableSpecials.add(RescuePlatform.class);
    availableSpecials.add(Trap.class);
    availableSpecials.add(MagnetShoe.class);
    availableSpecials.add(ProtectionWall.class);
    availableSpecials.add(WarpPowder.class);
    availableSpecials.add(TNTSheep.class);
    availableSpecials.add(Tracker.class);
    Main.getInstance().getServer().getPluginManager().registerEvents(new RescuePlatformListener(), Main.getInstance());
    Main.getInstance().getServer().getPluginManager().registerEvents(new TrapListener(), Main.getInstance());
    Main.getInstance().getServer().getPluginManager().registerEvents(new MagnetShoeListener(), Main.getInstance());
    Main.getInstance().getServer().getPluginManager().registerEvents(new ProtectionWallListener(), Main.getInstance());
    Main.getInstance().getServer().getPluginManager().registerEvents(new WarpPowderListener(), Main.getInstance());
    Main.getInstance().getServer().getPluginManager().registerEvents(new TNTSheepListener(), Main.getInstance());
    Main.getInstance().getServer().getPluginManager().registerEvents(new TrackerListener(), Main.getInstance());
  }
  
  public static List<Class<? extends SpecialItem>> getSpecials()
  {
    return availableSpecials;
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Shop.Specials.SpecialItem
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */