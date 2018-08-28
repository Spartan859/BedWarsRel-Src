package io.github.yannici.bedwars.Localization;

import io.github.yannici.bedwars.ChatWriter;
import io.github.yannici.bedwars.Main;
import io.github.yannici.bedwars.Utils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class LocalizationConfig
  extends YamlConfiguration
{
  private LocalizationConfig fallback = null;
  
  public String getPlayerLocale(Player paramPlayer)
  {
    try
    {
      Method localMethod = Main.getInstance().getCraftBukkitClass("entity.CraftPlayer").getMethod("getHandle", new Class[0]);
      localMethod.setAccessible(true);
      Object localObject = localMethod.invoke(paramPlayer, new Object[0]);
      Field localField = localObject.getClass().getDeclaredField("locale");
      localField.setAccessible(true);
      return localField.get(localObject).toString().split("_")[0].toLowerCase();
    }
    catch (Exception localException) {}
    return Main.getInstance().getFallbackLocale();
  }
  
  public void loadLocale(String paramString, boolean paramBoolean)
  {
    File localFile = new File(Main.getInstance().getDataFolder().getPath() + "/locale/" + paramString + ".yml");
    if (!localFile.exists()) {
      localFile = new File(Main.getInstance().getDataFolder().getPath() + "/locale/" + Main.getInstance().getFallbackLocale() + ".yml");
    }
    BufferedReader localBufferedReader = null;
    try
    {
      localBufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(localFile), "UTF-8"));
      load(localBufferedReader);
      if (localBufferedReader != null) {
        try
        {
          localBufferedReader.close();
        }
        catch (IOException localIOException1)
        {
          localIOException1.printStackTrace();
        }
      }
      if (paramBoolean) {
        return;
      }
    }
    catch (Exception localException1)
    {
      Main.getInstance().getServer().getConsoleSender().sendMessage(ChatWriter.pluginMessage(ChatColor.RED + "Failed to load localization language!"));
      return;
    }
    finally
    {
      if (localBufferedReader != null) {
        try
        {
          localBufferedReader.close();
        }
        catch (IOException localIOException6)
        {
          localIOException6.printStackTrace();
        }
      }
    }
    this.fallback = new LocalizationConfig();
    InputStream localInputStream = null;
    try
    {
      localInputStream = Main.getInstance().getResource("locale/" + paramString + ".yml");
      if (localInputStream == null)
      {
        this.fallback = null;
        return;
      }
      localBufferedReader = new BufferedReader(new InputStreamReader(localInputStream, "UTF-8"));
      this.fallback.load(localBufferedReader);
      return;
    }
    catch (Exception localException2) {}finally
    {
      if (localBufferedReader != null) {
        try
        {
          localBufferedReader.close();
          localInputStream.close();
        }
        catch (IOException localIOException7) {}
      }
    }
  }
  
  public Object get(String paramString)
  {
    return getString(paramString);
  }
  
  public Object get(String paramString, Map<String, String> paramMap)
  {
    return getFormatString(paramString, paramMap);
  }
  
  public String getString(String paramString)
  {
    if (super.get(paramString) == null)
    {
      if (this.fallback == null) {
        return "LOCALE_NOT_FOUND";
      }
      return this.fallback.getString(paramString);
    }
    return ChatColor.translateAlternateColorCodes('&', super.getString(paramString));
  }
  
  public String getFormatString(String paramString, Map<String, String> paramMap)
  {
    String str1 = getString(paramString);
    Iterator localIterator = paramMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str2 = (String)localIterator.next();
      str1 = str1.replace("$" + str2.toLowerCase() + "$", (CharSequence)paramMap.get(str2));
    }
    return ChatColor.translateAlternateColorCodes('&', str1);
  }
  
  public void saveLocales(boolean paramBoolean)
  {
    try
    {
      for (String str : Utils.getResourceListing(getClass(), "locale/"))
      {
        File localFile = new File(Main.getInstance().getDataFolder() + "/locale", str);
        if ((!localFile.exists()) || (paramBoolean)) {
          Main.getInstance().saveResource("locale/" + str, paramBoolean);
        }
      }
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Localization.LocalizationConfig
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */