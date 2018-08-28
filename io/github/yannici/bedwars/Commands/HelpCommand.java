package io.github.yannici.bedwars.Commands;

import com.google.common.collect.ImmutableMap;
import io.github.yannici.bedwars.Main;
import io.github.yannici.bedwars.Utils;
import java.util.ArrayList;
import java.util.Iterator;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.util.ChatPaginator;
import org.bukkit.util.ChatPaginator.ChatPage;

public class HelpCommand
  extends BaseCommand
{
  public HelpCommand(Main paramMain)
  {
    super(paramMain);
  }
  
  public String getCommand()
  {
    return "help";
  }
  
  public String getName()
  {
    return Main._l("commands.help.name");
  }
  
  public String getDescription()
  {
    return Main._l("commands.help.desc");
  }
  
  public String[] getArguments()
  {
    return new String[0];
  }
  
  public boolean execute(CommandSender paramCommandSender, ArrayList<String> paramArrayList)
  {
    if (!paramCommandSender.hasPermission(new StringBuilder().append("bw.").append(getPermission()).toString())) {
      return false;
    }
    int i = 1;
    String str1;
    if ((paramArrayList.size() == 0) || (paramArrayList.size() > 1))
    {
      str1 = "1";
    }
    else
    {
      str1 = (String)paramArrayList.get(0);
      if (str1.isEmpty()) {
        str1 = "1";
      }
      if (!Utils.isNumber(str1)) {
        str1 = "1";
      }
    }
    i = Integer.parseInt(str1);
    StringBuilder localStringBuilder = new StringBuilder();
    paramCommandSender.sendMessage(new StringBuilder().append(ChatColor.GREEN).append("---------- Bedwars Help ----------").toString());
    ArrayList localArrayList1 = Main.getInstance().getBaseCommands();
    ArrayList localArrayList2 = Main.getInstance().getSetupCommands();
    ArrayList localArrayList3 = Main.getInstance().getCommandsByPermission("kick");
    Object localObject1 = localArrayList1.iterator();
    Object localObject2;
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (BaseCommand)((Iterator)localObject1).next();
      appendCommand((BaseCommand)localObject2, localStringBuilder);
    }
    if (paramCommandSender.hasPermission("bw.kick"))
    {
      localObject1 = localArrayList3.iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (BaseCommand)((Iterator)localObject1).next();
        appendCommand((BaseCommand)localObject2, localStringBuilder);
      }
    }
    if (paramCommandSender.hasPermission("bw.setup"))
    {
      localStringBuilder.append(new StringBuilder().append(ChatColor.BLUE).append("------- Bedwars Admin Help -------\n").toString());
      localObject1 = localArrayList2.iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (BaseCommand)((Iterator)localObject1).next();
        appendCommand((BaseCommand)localObject2, localStringBuilder);
      }
    }
    localObject1 = ChatPaginator.paginate(localStringBuilder.toString(), i);
    for (String str2 : ((ChatPaginator.ChatPage)localObject1).getLines()) {
      paramCommandSender.sendMessage(str2);
    }
    paramCommandSender.sendMessage(new StringBuilder().append(ChatColor.GREEN).append("---------- ").append(Main._l("default.pages", ImmutableMap.of("current", String.valueOf(((ChatPaginator.ChatPage)localObject1).getPageNumber()), "max", String.valueOf(((ChatPaginator.ChatPage)localObject1).getTotalPages())))).append(" ----------").toString());
    return true;
  }
  
  private void appendCommand(BaseCommand paramBaseCommand, StringBuilder paramStringBuilder)
  {
    String str1 = "";
    for (String str2 : paramBaseCommand.getArguments()) {
      str1 = new StringBuilder().append(str1).append(" {").append(str2).append("}").toString();
    }
    if (paramBaseCommand.getCommand().equals("help")) {
      str1 = " {page?}";
    } else if (paramBaseCommand.getCommand().equalsIgnoreCase("list")) {
      str1 = " {page?}";
    } else if (paramBaseCommand.getCommand().equalsIgnoreCase("stats")) {
      str1 = " {player?}";
    } else if (paramBaseCommand.getCommand().equalsIgnoreCase("reload")) {
      str1 = " {config;locale;shop;games;all?}";
    }
    paramStringBuilder.append(new StringBuilder().append(ChatColor.YELLOW).append("/").append(Main.getInstance().getStringConfig("command-prefix", "bw")).append(" ").append(paramBaseCommand.getCommand()).append(str1).append(" - ").append(paramBaseCommand.getDescription()).append("\n").toString());
  }
  
  public String getPermission()
  {
    return "base";
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Commands.HelpCommand
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */