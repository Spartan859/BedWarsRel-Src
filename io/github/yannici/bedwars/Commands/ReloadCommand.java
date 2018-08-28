package io.github.yannici.bedwars.Commands;

import io.github.yannici.bedwars.ChatWriter;
import io.github.yannici.bedwars.Game.GameManager;
import io.github.yannici.bedwars.HolographicDisplaysInteraction;
import io.github.yannici.bedwars.Main;
import io.github.yannici.bedwars.Updater.ConfigUpdater;
import java.io.File;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.FileConfigurationOptions;

public class ReloadCommand
  extends BaseCommand
{
  public ReloadCommand(Main paramMain)
  {
    super(paramMain);
  }
  
  public String getPermission()
  {
    return "setup";
  }
  
  public String getCommand()
  {
    return "reload";
  }
  
  public String getName()
  {
    return Main._l("commands.reload.name");
  }
  
  public String getDescription()
  {
    return Main._l("commands.reload.desc");
  }
  
  public String[] getArguments()
  {
    return new String[0];
  }
  
  public boolean execute(CommandSender paramCommandSender, ArrayList<String> paramArrayList)
  {
    if (!paramCommandSender.hasPermission(getPermission())) {
      return false;
    }
    File localFile = new File(Main.getInstance().getDataFolder(), "config.yml");
    String str = "";
    if (paramArrayList.size() > 0) {
      str = (String)paramArrayList.get(0);
    } else {
      str = "all";
    }
    ConfigUpdater localConfigUpdater;
    if (str.equalsIgnoreCase("all"))
    {
      if (!localFile.exists()) {
        Main.getInstance().saveDefaultConfig();
      }
      Main.getInstance().loadConfigInUTF();
      Main.getInstance().getConfig().options().copyDefaults(true);
      Main.getInstance().getConfig().options().copyHeader(true);
      localConfigUpdater = new ConfigUpdater();
      localConfigUpdater.addConfigs();
      Main.getInstance().saveConfiguration();
      Main.getInstance().loadConfigInUTF();
      Main.getInstance().loadShop();
      if ((Main.getInstance().isHologramsEnabled()) && (Main.getInstance().getHolographicInteractor() != null)) {
        Main.getInstance().getHolographicInteractor().loadHolograms();
      }
      Main.getInstance().reloadLocalization();
      Main.getInstance().getGameManager().reloadGames();
    }
    else if (str.equalsIgnoreCase("shop"))
    {
      Main.getInstance().loadShop();
    }
    else if (str.equalsIgnoreCase("games"))
    {
      Main.getInstance().getGameManager().reloadGames();
    }
    else if (str.equalsIgnoreCase("holo"))
    {
      if (Main.getInstance().isHologramsEnabled()) {
        Main.getInstance().getHolographicInteractor().loadHolograms();
      }
    }
    else if (str.equalsIgnoreCase("config"))
    {
      if (!localFile.exists()) {
        Main.getInstance().saveDefaultConfig();
      }
      Main.getInstance().loadConfigInUTF();
      Main.getInstance().getConfig().options().copyDefaults(true);
      Main.getInstance().getConfig().options().copyHeader(true);
      localConfigUpdater = new ConfigUpdater();
      localConfigUpdater.addConfigs();
      Main.getInstance().saveConfiguration();
      Main.getInstance().loadConfigInUTF();
    }
    else if (str.equalsIgnoreCase("locale"))
    {
      Main.getInstance().reloadLocalization();
    }
    else
    {
      return false;
    }
    paramCommandSender.sendMessage(ChatWriter.pluginMessage(ChatColor.GREEN + Main._l("success.reloadconfig")));
    return true;
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Commands.ReloadCommand
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */