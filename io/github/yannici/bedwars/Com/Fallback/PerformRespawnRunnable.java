package io.github.yannici.bedwars.Com.Fallback;

import io.github.yannici.bedwars.ChatWriter;
import io.github.yannici.bedwars.Main;
import io.github.yannici.bedwars.Utils;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PerformRespawnRunnable
  extends BukkitRunnable
{
  private Player player = null;
  
  public PerformRespawnRunnable(Player paramPlayer)
  {
    this.player = paramPlayer;
  }
  
  public void run()
  {
    try
    {
      Object localObject1 = Main.getInstance().getMinecraftServerClass("EnumClientCommand");
      Class localClass1 = Main.getInstance().getMinecraftServerClass("PacketPlayInClientCommand");
      if (localObject1 == null) {
        for (localObject5 : localClass1.getDeclaredClasses()) {
          if (localObject5.getSimpleName().equals("EnumClientCommand"))
          {
            localObject1 = localObject5;
            break;
          }
        }
      }
      ??? = Arrays.asList(((Class)localObject1).getEnumConstants());
      Object localObject3 = null;
      Object localObject4 = ((List)???).iterator();
      while (((Iterator)localObject4).hasNext())
      {
        localObject5 = ((Iterator)localObject4).next();
        if (localObject5.toString().equals("PERFORM_RESPAWN"))
        {
          localObject3 = localObject5;
          break;
        }
      }
      localObject4 = getPacketObject("PacketPlayInClientCommand", new Class[] { localObject1 }, new Object[] { localObject3 });
      Object localObject5 = Utils.getCraftPlayer(this.player);
      Class localClass2 = Main.getInstance().getCraftBukkitClass("entity.CraftPlayer");
      Field localField = localClass2.getField("playerConnection");
      localField.setAccessible(true);
      Object localObject6 = localField.get(localObject5);
      Class localClass3 = Main.getInstance().getMinecraftServerClass("PlayerConnection");
      Method localMethod = localClass3.getMethod("a", new Class[] { localClass1 });
      localMethod.setAccessible(true);
      localMethod.invoke(localObject6, new Object[] { localObject4 });
    }
    catch (Exception localException)
    {
      Main.getInstance().getServer().getConsoleSender().sendMessage(ChatWriter.pluginMessage(ChatColor.RED + "Plugin not compatible with your server version!"));
    }
  }
  
  private Object getPacketObject(String paramString, Class<?>[] paramArrayOfClass, Object[] paramArrayOfObject)
  {
    try
    {
      Class localClass = Main.getInstance().getMinecraftServerClass(paramString);
      if (localClass == null) {
        throw new Exception("packet not found");
      }
      Constructor localConstructor = localClass.getDeclaredConstructor(paramArrayOfClass);
      if (localConstructor == null) {
        throw new Exception("constructor not found");
      }
      localConstructor.setAccessible(true);
      return localConstructor.newInstance(paramArrayOfObject);
    }
    catch (Exception localException)
    {
      Main.getInstance().getServer().getConsoleSender().sendMessage(ChatWriter.pluginMessage(ChatColor.RED + "Couldn't catch packet class " + ChatColor.YELLOW + paramString));
    }
    return null;
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Com.Fallback.PerformRespawnRunnable
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */