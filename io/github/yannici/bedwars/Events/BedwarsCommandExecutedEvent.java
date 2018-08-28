package io.github.yannici.bedwars.Events;

import io.github.yannici.bedwars.Commands.BaseCommand;
import java.util.ArrayList;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BedwarsCommandExecutedEvent
  extends Event
{
  private static final HandlerList handlers = new HandlerList();
  private CommandSender sender = null;
  private BaseCommand command = null;
  private boolean result = false;
  private ArrayList<String> params = null;
  
  public BedwarsCommandExecutedEvent(CommandSender paramCommandSender, BaseCommand paramBaseCommand, ArrayList<String> paramArrayList, boolean paramBoolean)
  {
    this.sender = paramCommandSender;
    this.command = paramBaseCommand;
    this.params = paramArrayList;
    this.result = paramBoolean;
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
  
  public boolean isSuccess()
  {
    return this.result;
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Events.BedwarsCommandExecutedEvent
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */