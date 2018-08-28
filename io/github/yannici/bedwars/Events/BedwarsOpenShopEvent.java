package io.github.yannici.bedwars.Events;

import io.github.yannici.bedwars.Game.Game;
import io.github.yannici.bedwars.Villager.MerchantCategory;
import java.util.HashMap;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BedwarsOpenShopEvent
  extends Event
  implements Cancellable
{
  private static final HandlerList handlers = new HandlerList();
  private Player player = null;
  private Game game = null;
  private HashMap<Material, MerchantCategory> itemshop = null;
  private Entity clickedEntity = null;
  private boolean cancelled = false;
  
  public BedwarsOpenShopEvent(Game paramGame, Player paramPlayer, HashMap<Material, MerchantCategory> paramHashMap, Entity paramEntity)
  {
    this.player = paramPlayer;
    this.game = paramGame;
    this.itemshop = paramHashMap;
    this.clickedEntity = paramEntity;
  }
  
  public HandlerList getHandlers()
  {
    return handlers;
  }
  
  public static HandlerList getHandlerList()
  {
    return handlers;
  }
  
  public CommandSender getPlayer()
  {
    return this.player;
  }
  
  public Game getGame()
  {
    return this.game;
  }
  
  public HashMap<Material, MerchantCategory> getItemshop()
  {
    return this.itemshop;
  }
  
  public Entity getEntity()
  {
    return this.clickedEntity;
  }
  
  public boolean isCancelled()
  {
    return this.cancelled;
  }
  
  public void setCancelled(boolean paramBoolean)
  {
    this.cancelled = paramBoolean;
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Events.BedwarsOpenShopEvent
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */