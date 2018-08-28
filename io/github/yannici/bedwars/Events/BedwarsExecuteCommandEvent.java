package io.github.yannici.bedwars.Events;

import io.github.yannici.bedwars.Commands.BaseCommand;
import java.util.ArrayList;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BedwarsExecuteCommandEvent
  extends Event
  implements Cancellable
{
  private static final HandlerList handlers = new HandlerList();
  private CommandSender sender = null;
  private BaseCommand command = null;
  private ArrayList<String> params = null;
  private boolean cancelled = false;
  
  public BedwarsExecuteCommandEvent(CommandSender paramCommandSender, BaseCommand paramBaseCommand, ArrayList<String> paramArrayList)
  {
    this.sender = paramCommandSender;
    this.command = paramBaseCommand;
    this.params = paramArrayList;
  }
  
  public HandlerList getHandlers()
  {
    return handlers;
  }
  
  public static HandlerList getHandlerList()
  {
    return handlers;
  }
  
  public CommandSender getSender()
  {
    return this.sender;
  }
  
  public BaseCommand getCommand()
  {
    return this.command;
  }
  
  public ArrayList<String> getParameter()
  {
    return this.params;
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
 * Qualified Name:     io.github.yannici.bedwars.Events.BedwarsExecuteCommandEvent
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */