package io.github.yannici.bedwars.Updater;

import io.github.yannici.bedwars.Main;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigUpdater
{
  public void addConfigs()
  {
    Main.getInstance().getConfig().addDefault("check-updates", Boolean.valueOf(true));
    Main.getInstance().getConfig().addDefault("sign.first-line", "$title$");
    Main.getInstance().getConfig().addDefault("sign.second-line", "$regionname$");
    Main.getInstance().getConfig().addDefault("sign.third-line", "Players &7[&b$currentplayers$&7/&b$maxplayers$&7]");
    Main.getInstance().getConfig().addDefault("sign.fourth-line", "$status$");
    Main.getInstance().getConfig().addDefault("specials.rescue-platform.break-time", Integer.valueOf(10));
    Main.getInstance().getConfig().addDefault("specials.rescue-platform.using-wait-time", Integer.valueOf(20));
    Main.getInstance().getConfig().addDefault("explodes.destroy-worldblocks", Boolean.valueOf(false));
    Main.getInstance().getConfig().addDefault("explodes.destroy-beds", Boolean.valueOf(false));
    Main.getInstance().getConfig().addDefault("explodes.drop-blocking", Boolean.valueOf(false));
    Main.getInstance().getConfig().addDefault("rewards.enabled", Boolean.valueOf(false));
    ArrayList localArrayList = new ArrayList();
    localArrayList.add("/example {player} {score}");
    Main.getInstance().getConfig().addDefault("rewards.player-win", localArrayList);
    Main.getInstance().getConfig().addDefault("rewards.player-end-game", localArrayList);
    Main.getInstance().getConfig().addDefault("global-messages", Boolean.valueOf(true));
    Main.getInstance().getConfig().addDefault("player-settings.one-stack-on-shift", Boolean.valueOf(false));
    Main.getInstance().getConfig().addDefault("seperate-game-chat", Boolean.valueOf(true));
    Main.getInstance().getConfig().addDefault("seperate-spectator-chat", Boolean.valueOf(false));
    Main.getInstance().getConfig().addDefault("specials.trap.duration", Integer.valueOf(10));
    Main.getInstance().getConfig().addDefault("specials.trap.blindness.amplifier", Integer.valueOf(2));
    Main.getInstance().getConfig().addDefault("specials.trap.slowness.amplifier", Integer.valueOf(2));
    Main.getInstance().getConfig().addDefault("specials.trap.weakness.amplifier", Integer.valueOf(2));
    Main.getInstance().getConfig().addDefault("specials.trap.blindness.enabled", Boolean.valueOf(true));
    Main.getInstance().getConfig().addDefault("specials.trap.slowness.enabled", Boolean.valueOf(true));
    Main.getInstance().getConfig().addDefault("specials.trap.weakness.enabled", Boolean.valueOf(true));
    Main.getInstance().getConfig().addDefault("specials.trap.show-particles", Boolean.valueOf(true));
    Main.getInstance().getConfig().addDefault("specials.trap.play-sound", Boolean.valueOf(true));
    Main.getInstance().getConfig().addDefault("specials.magnetshoe.probability", Integer.valueOf(75));
    Main.getInstance().getConfig().addDefault("specials.magnetshoe.boots", "IRON_BOOTS");
    Main.getInstance().getConfig().addDefault("specials.rescue-platform.block", "GLASS");
    Main.getInstance().getConfig().addDefault("specials.rescue-platform.block", "BLAZE_ROD");
    Main.getInstance().getConfig().addDefault("ingame-chatformat-all", "[$all$] <$team$>$player$: $msg$");
    Main.getInstance().getConfig().addDefault("ingame-chatformat", "<$team$>$player$: $msg$");
    Main.getInstance().getConfig().addDefault("overwrite-names", Boolean.valueOf(false));
    Main.getInstance().getConfig().addDefault("specials.protection-wall.break-time", Integer.valueOf(0));
    Main.getInstance().getConfig().addDefault("specials.protection-wall.wait-time", Integer.valueOf(20));
    Main.getInstance().getConfig().addDefault("specials.protection-wall.can-break", Boolean.valueOf(true));
    Main.getInstance().getConfig().addDefault("specials.protection-wall.item", "BRICK");
    Main.getInstance().getConfig().addDefault("specials.protection-wall.block", "SANDSTONE");
    Main.getInstance().getConfig().addDefault("specials.protection-wall.width", Integer.valueOf(4));
    Main.getInstance().getConfig().addDefault("specials.protection-wall.height", Integer.valueOf(4));
    Main.getInstance().getConfig().addDefault("specials.protection-wall.distance", Integer.valueOf(2));
    Main.getInstance().getConfig().addDefault("bed-sound", "ENDERDRAGON_GROWL");
    Main.getInstance().getConfig().addDefault("store-game-records", Boolean.valueOf(true));
    Main.getInstance().getConfig().addDefault("store-game-records-holder", Boolean.valueOf(true));
    Main.getInstance().getConfig().addDefault("statistics.scores.record", Integer.valueOf(100));
    Main.getInstance().getConfig().addDefault("game-block", "BED_BLOCK");
    Main.getInstance().getConfig().addDefault("titles.win.enabled", Boolean.valueOf(true));
    Main.getInstance().getConfig().addDefault("titles.win.title-fade-in", Double.valueOf(1.5D));
    Main.getInstance().getConfig().addDefault("titles.win.title-stay", Double.valueOf(5.0D));
    Main.getInstance().getConfig().addDefault("titles.win.title-fade-out", Double.valueOf(2.0D));
    Main.getInstance().getConfig().addDefault("titles.win.subtitle-fade-in", Double.valueOf(1.5D));
    Main.getInstance().getConfig().addDefault("titles.win.subtitle-stay", Double.valueOf(5.0D));
    Main.getInstance().getConfig().addDefault("titles.win.subtitle-fade-out", Double.valueOf(2.0D));
    Main.getInstance().getConfig().addDefault("titles.map.enabled", Boolean.valueOf(false));
    Main.getInstance().getConfig().addDefault("titles.map.title-fade-in", Double.valueOf(1.5D));
    Main.getInstance().getConfig().addDefault("titles.map.title-stay", Double.valueOf(5.0D));
    Main.getInstance().getConfig().addDefault("titles.map.title-fade-out", Double.valueOf(2.0D));
    Main.getInstance().getConfig().addDefault("titles.map.subtitle-fade-in", Double.valueOf(1.5D));
    Main.getInstance().getConfig().addDefault("titles.map.subtitle-stay", Double.valueOf(5.0D));
    Main.getInstance().getConfig().addDefault("titles.map.subtitle-fade-out", Double.valueOf(2.0D));
    Main.getInstance().getConfig().addDefault("player-drops", Boolean.valueOf(false));
    Main.getInstance().getConfig().addDefault("bungeecord.spigot-restart", Boolean.valueOf(true));
    Main.getInstance().getConfig().addDefault("place-in-liquid", Boolean.valueOf(true));
    Main.getInstance().getConfig().addDefault("friendlybreak", Boolean.valueOf(true));
    Main.getInstance().getConfig().addDefault("breakable-blocks", Arrays.asList(new String[] { "none" }));
    Main.getInstance().getConfig().addDefault("update-infos", Boolean.valueOf(true));
    Main.getInstance().getConfig().addDefault("lobby-chatformat", "$player$: $msg$");
    excludeShop();
    Main.getInstance().getConfig().addDefault("statistics.bed-destroyed-kills", Boolean.valueOf(false));
    Main.getInstance().getConfig().addDefault("rewards.player-destroy-bed", Arrays.asList(new String[] { "/example {player} {score}" }));
    Main.getInstance().getConfig().addDefault("rewards.player-kill", Arrays.asList(new String[] { "/example {player} 10" }));
    Main.getInstance().getConfig().addDefault("specials.tntsheep.fuse-time", Double.valueOf(8.0D));
    Main.getInstance().getConfig().addDefault("titles.countdown.enabled", Boolean.valueOf(true));
    Main.getInstance().getConfig().addDefault("titles.countdown.format", "&3{countdown}");
    Main.getInstance().getConfig().addDefault("specials.tntsheep.speed", Double.valueOf(0.4D));
    Main.getInstance().getConfig().addDefault("global-autobalance", Boolean.valueOf(false));
    Main.getInstance().getConfig().addDefault("scoreboard.format-bed-destroyed", "&c$status$ $team$");
    Main.getInstance().getConfig().addDefault("scoreboard.format-bed-alive", "&a$status$ $team$");
    Main.getInstance().getConfig().addDefault("scoreboard.format-title", "&e$region$&f - $time$");
    Main.getInstance().getConfig().addDefault("teamname-on-tab", Boolean.valueOf(false));
    Main.getInstance().getConfig().addDefault("bungeecord.motds.full", "&c[Full]");
    Main.getInstance().getConfig().addDefault("teamname-in-chat", Boolean.valueOf(false));
    Main.getInstance().getConfig().addDefault("hearts-on-death", Boolean.valueOf(true));
    Main.getInstance().getConfig().addDefault("lobby-scoreboard.title", "&eBEDWARS");
    Main.getInstance().getConfig().addDefault("lobby-scoreboard.enabled", Boolean.valueOf(true));
    Main.getInstance().getConfig().addDefault("lobby-scoreboard.content", Arrays.asList(new String[] { "", "&fMap: &2$regionname$", "&fPlayers: &2$players$&f/&2$maxplayers$", "", "&fWaiting ...", "" }));
    Main.getInstance().getConfig().addDefault("jointeam-entity.show-name", Boolean.valueOf(true));
    Main.getInstance().getConfig().addDefault("die-on-void", Boolean.valueOf(false));
    Main.getInstance().getConfig().addDefault("global-chat-after-end", Boolean.valueOf(true));
    Main.getInstance().getConfig().addDefault("overwrite-display-names", Boolean.valueOf(true));
    Main.getInstance().getConfig().addDefault("holographic-stats.show-prefix", Boolean.valueOf(false));
    Main.getInstance().getConfig().addDefault("holographic-stats.name-color", "&7");
    Main.getInstance().getConfig().addDefault("holographic-stats.value-color", "&e");
    Main.getInstance().getConfig().addDefault("holographic-stats.head-line", "Your &eBEDWARS&f stats");
    Main.getInstance().getConfig().addDefault("lobby-gamemode", Integer.valueOf(0));
    Main.getInstance().getConfig().addDefault("statistics.show-on-game-end", Boolean.valueOf(true));
    Main.getInstance().getConfig().addDefault("allow-crafting", Boolean.valueOf(false));
    Main.getInstance().getConfig().addDefault("command-prefix", "bw");
    Main.getInstance().getConfig().addDefault("database.connection-pooling.max-pool-size", Integer.valueOf(50));
    Main.getInstance().getConfig().addDefault("specials.tntsheep.explosion-factor", Double.valueOf(1.0D));
    Main.getInstance().getConfig().addDefault("bungeecord.full-restart", Boolean.valueOf(true));
    Main.getInstance().getConfig().addDefault("lobbytime-full", Integer.valueOf(15));
    Main.getInstance().getConfig().addDefault("bungeecord.endgame-in-lobby", Boolean.valueOf(true));
  }
  
  private void excludeShop()
  {
    if (Main.getInstance().getConfig().contains("shop"))
    {
      ConfigurationSection localConfigurationSection = Main.getInstance().getConfig().getConfigurationSection("shop");
      File localFile = new File(Main.getInstance().getDataFolder(), "shop.yml");
      if (localFile.exists())
      {
        removeShopSection();
        return;
      }
      try
      {
        localFile.createNewFile();
      }
      catch (IOException localIOException)
      {
        localIOException.printStackTrace();
        return;
      }
      YamlConfiguration localYamlConfiguration = new YamlConfiguration();
      localYamlConfiguration.set("shop", localConfigurationSection);
      saveShopFile(localYamlConfiguration, localFile);
      removeShopSection();
    }
  }
  
  private void saveShopFile(YamlConfiguration paramYamlConfiguration, File paramFile)
  {
    try
    {
      String str = Main.getInstance().getYamlDump(paramYamlConfiguration);
      FileOutputStream localFileOutputStream = new FileOutputStream(paramFile);
      OutputStreamWriter localOutputStreamWriter = new OutputStreamWriter(localFileOutputStream, "UTF-8");
      try
      {
        localOutputStreamWriter.write(str);
      }
      finally
      {
        localOutputStreamWriter.close();
        localFileOutputStream.close();
      }
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
  
  private void removeShopSection()
  {
    Main.getInstance().getConfig().set("shop", null);
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Updater.ConfigUpdater
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */