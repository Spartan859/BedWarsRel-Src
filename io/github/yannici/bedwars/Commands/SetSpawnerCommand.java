package io.github.yannici.bedwars.Commands;

import com.google.common.collect.ImmutableMap;
import io.github.yannici.bedwars.ChatWriter;
import io.github.yannici.bedwars.Game.Game;
import io.github.yannici.bedwars.Game.GameManager;
import io.github.yannici.bedwars.Game.GameState;
import io.github.yannici.bedwars.Game.RessourceSpawner;
import io.github.yannici.bedwars.Main;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SetSpawnerCommand
  extends BaseCommand
{
  public SetSpawnerCommand(Main paramMain)
  {
    super(paramMain);
  }
  
  public String getCommand()
  {
    return "setspawner";
  }
  
  public String getName()
  {
    return Main._l("commands.setspawner.name");
  }
  
  public String getDescription()
  {
    return Main._l("commands.setspawner.desc");
  }
  
  public String[] getArguments()
  {
    return new String[] { "game", "ressource" };
  }
  
  private String[] getRessources()
  {
    ConfigurationSection localConfigurationSection = Main.getInstance().getConfig().getConfigurationSection("ressource");
    if (localConfigurationSection == null) {
      return new String[0];
    }
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = localConfigurationSection.getKeys(false).iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      localArrayList.add(str.toLowerCase());
    }
    return (String[])localArrayList.toArray(new String[localArrayList.size()]);
  }
  
  public boolean execute(CommandSender paramCommandSender, ArrayList<String> paramArrayList)
  {
    if (!super.hasPermission(paramCommandSender)) {
      return false;
    }
    Player localPlayer = (Player)paramCommandSender;
    ArrayList localArrayList = new ArrayList(Arrays.asList(getRessources()));
    String str = ((String)paramArrayList.get(1)).toString().toLowerCase();
    Game localGame = getPlugin().getGameManager().getGame((String)paramArrayList.get(0));
    if (localGame == null)
    {
      localPlayer.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + Main._l("errors.gamenotfound", ImmutableMap.of("game", ((String)paramArrayList.get(0)).toString()))));
      return false;
    }
    if (localGame.getState() == GameState.RUNNING)
    {
      paramCommandSender.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + Main._l("errors.notwhilegamerunning")));
      return false;
    }
    if (!localArrayList.contains(str))
    {
      localPlayer.sendMessage(ChatWriter.pluginMessage(ChatColor.RED + Main._l("errors.spawnerargument")));
      return false;
    }
    Object localObject = Main.getInstance().getConfig().get("ressource." + str);
    ItemStack localItemStack = RessourceSpawner.createSpawnerStackByConfig(localObject);
    Location localLocation = localPlayer.getLocation();
    RessourceSpawner localRessourceSpawner = new RessourceSpawner(localGame, str, localLocation);
    localGame.addRessourceSpawner(localRessourceSpawner);
    localPlayer.sendMessage(ChatWriter.pluginMessage(ChatColor.GREEN + Main._l("success.spawnerset", ImmutableMap.of("name", new StringBuilder().append(localItemStack.getItemMeta().getDisplayName()).append(ChatColor.GREEN).toString()))));
    return true;
  }
  
  public String getPermission()
  {
    return "setup";
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Commands.SetSpawnerCommand
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */