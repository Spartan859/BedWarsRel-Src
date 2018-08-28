package io.github.yannici.bedwars;

import io.github.yannici.bedwars.Game.Game;
import io.github.yannici.bedwars.Game.Team;
import io.github.yannici.bedwars.Game.TeamColor;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.util.Vector;

public final class Utils
{
  public static String implode(String paramString, ArrayList<String> paramArrayList)
  {
    if (paramArrayList.isEmpty()) {
      return "";
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append((String)paramArrayList.remove(0));
    Iterator localIterator = paramArrayList.iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      localStringBuilder.append(paramString);
      localStringBuilder.append(str);
    }
    return localStringBuilder.toString();
  }
  
  public static void equipArmorStand(LivingEntity paramLivingEntity, Team paramTeam)
  {
    if (!(paramLivingEntity instanceof ArmorStand)) {
      return;
    }
    ArmorStand localArmorStand = (ArmorStand)paramLivingEntity;
    ItemStack localItemStack1 = new ItemStack(Material.LEATHER_HELMET, 1);
    LeatherArmorMeta localLeatherArmorMeta = (LeatherArmorMeta)localItemStack1.getItemMeta();
    localLeatherArmorMeta.setColor(paramTeam.getColor().getColor());
    localItemStack1.setItemMeta(localLeatherArmorMeta);
    ItemStack localItemStack2 = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
    localLeatherArmorMeta = (LeatherArmorMeta)localItemStack2.getItemMeta();
    localLeatherArmorMeta.setColor(paramTeam.getColor().getColor());
    localItemStack2.setItemMeta(localLeatherArmorMeta);
    ItemStack localItemStack3 = new ItemStack(Material.LEATHER_LEGGINGS, 1);
    localLeatherArmorMeta = (LeatherArmorMeta)localItemStack3.getItemMeta();
    localLeatherArmorMeta.setColor(paramTeam.getColor().getColor());
    localItemStack3.setItemMeta(localLeatherArmorMeta);
    ItemStack localItemStack4 = new ItemStack(Material.LEATHER_BOOTS, 1);
    localLeatherArmorMeta = (LeatherArmorMeta)localItemStack4.getItemMeta();
    localLeatherArmorMeta.setColor(paramTeam.getColor().getColor());
    localItemStack4.setItemMeta(localLeatherArmorMeta);
    localArmorStand.setHelmet(localItemStack1);
    localArmorStand.setChestplate(localItemStack2);
    localArmorStand.setLeggings(localItemStack3);
    localArmorStand.setBoots(localItemStack4);
  }
  
  public static void createParticleInGame(Game paramGame, String paramString, Location paramLocation)
  {
    try
    {
      Class localClass = Class.forName(new StringBuilder().append("io.github.yannici.bedwars.Com.").append(Main.getInstance().getCurrentVersion()).append(".ParticleSpawner").toString());
      localObject = localClass.getDeclaredMethod("spawnParticle", new Class[] { List.class, String.class, Float.TYPE, Float.TYPE, Float.TYPE });
      ((Method)localObject).invoke(null, new Object[] { paramGame.getPlayers(), paramString, Float.valueOf((float)paramLocation.getX()), Float.valueOf((float)paramLocation.getY()), Float.valueOf((float)paramLocation.getZ()) });
    }
    catch (Exception localException1)
    {
      try
      {
        Object localObject = Class.forName("io.github.yannici.bedwars.Com.Fallback.ParticleSpawner");
        Method localMethod = ((Class)localObject).getDeclaredMethod("spawnParticle", new Class[] { List.class, String.class, Float.TYPE, Float.TYPE, Float.TYPE });
        localMethod.invoke(null, new Object[] { paramGame.getPlayers(), paramString, Float.valueOf((float)paramLocation.getX()), Float.valueOf((float)paramLocation.getY()), Float.valueOf((float)paramLocation.getZ()) });
      }
      catch (Exception localException2)
      {
        localException2.printStackTrace();
      }
    }
  }
  
  public static Location getDirectionLocation(Location paramLocation, int paramInt)
  {
    Location localLocation = paramLocation.clone();
    return localLocation.add(localLocation.getDirection().setY(0).normalize().multiply(paramInt));
  }
  
  public static Block getBedNeighbor(Block paramBlock)
  {
    if (isBedBlock(paramBlock.getRelative(BlockFace.EAST))) {
      return paramBlock.getRelative(BlockFace.EAST);
    }
    if (isBedBlock(paramBlock.getRelative(BlockFace.WEST))) {
      return paramBlock.getRelative(BlockFace.WEST);
    }
    if (isBedBlock(paramBlock.getRelative(BlockFace.SOUTH))) {
      return paramBlock.getRelative(BlockFace.SOUTH);
    }
    return paramBlock.getRelative(BlockFace.NORTH);
  }
  
  public static boolean isBedBlock(Block paramBlock)
  {
    if (paramBlock == null) {
      return false;
    }
    return (paramBlock.getType() == Material.BED) || (paramBlock.getType() == Material.BED_BLOCK);
  }
  
  public static Material getMaterialByConfig(String paramString, Material paramMaterial)
  {
    try
    {
      String str = Main.getInstance().getStringConfig(paramString, paramMaterial.name());
      if (isNumber(str)) {
        return Material.getMaterial(Integer.valueOf(str).intValue());
      }
      return Material.getMaterial(str.toUpperCase());
    }
    catch (Exception localException) {}
    return paramMaterial;
  }
  
  public static Object getCraftPlayer(Player paramPlayer)
  {
    try
    {
      Class localClass = Main.getInstance().getCraftBukkitClass("entity.CraftPlayer");
      Method localMethod = localClass.getMethod("getHandle", new Class[0]);
      localMethod.setAccessible(true);
      return localMethod.invoke(paramPlayer, new Object[0]);
    }
    catch (Exception localException) {}
    return null;
  }
  
  public static boolean isNumber(String paramString)
  {
    try
    {
      Integer.parseInt(paramString);
      return true;
    }
    catch (Exception localException) {}
    return false;
  }
  
  public static Method getColorableMethod(Material paramMaterial)
  {
    try
    {
      ItemStack localItemStack = new ItemStack(paramMaterial, 1);
      Method localMethod = localItemStack.getItemMeta().getClass().getMethod("setColor", new Class[] { Color.class });
      if (localMethod != null) {
        return localMethod;
      }
    }
    catch (Exception localException) {}
    return null;
  }
  
  public static boolean checkBungeePlugin()
  {
    try
    {
      Class.forName("net.md_5.bungee.BungeeCord");
      return true;
    }
    catch (Exception localException) {}
    return false;
  }
  
  public static String[] getResourceListing(Class<?> paramClass, String paramString)
    throws URISyntaxException, IOException
  {
    URL localURL = paramClass.getClassLoader().getResource(paramString);
    if ((localURL != null) && (localURL.getProtocol().equals("file"))) {
      return new File(localURL.toURI()).list();
    }
    String str1;
    if (localURL == null)
    {
      str1 = new StringBuilder().append(paramClass.getName().replace(".", "/")).append(".class").toString();
      localURL = paramClass.getClassLoader().getResource(str1);
    }
    if (localURL.getProtocol().equals("jar"))
    {
      str1 = localURL.getPath().substring(5, localURL.getPath().indexOf("!"));
      JarFile localJarFile = new JarFile(URLDecoder.decode(str1, "UTF-8"));
      Enumeration localEnumeration = localJarFile.entries();
      HashSet localHashSet = new HashSet();
      while (localEnumeration.hasMoreElements())
      {
        String str2 = ((JarEntry)localEnumeration.nextElement()).getName();
        if (str2.startsWith(paramString))
        {
          String str3 = str2.substring(paramString.length());
          int i = str3.indexOf("/");
          if (i >= 0) {
            str3 = str3.substring(0, i);
          }
          localHashSet.add(str3);
        }
      }
      return (String[])localHashSet.toArray(new String[localHashSet.size()]);
    }
    throw new UnsupportedOperationException(new StringBuilder().append("Cannot list files for URL ").append(localURL).toString());
  }
  
  public static int randInt(int paramInt1, int paramInt2)
  {
    Random localRandom = new Random();
    int i = localRandom.nextInt(paramInt2 - paramInt1 + 1) + paramInt1;
    return i;
  }
  
  public static Map<String, Object> locationSerialize(Location paramLocation)
  {
    HashMap localHashMap = new HashMap();
    localHashMap.put("x", Double.valueOf(paramLocation.getX()));
    localHashMap.put("y", Double.valueOf(paramLocation.getY()));
    localHashMap.put("z", Double.valueOf(paramLocation.getZ()));
    localHashMap.put("pitch", Double.valueOf(paramLocation.getPitch()));
    localHashMap.put("yaw", Double.valueOf(paramLocation.getYaw()));
    localHashMap.put("world", paramLocation.getWorld().getName());
    return localHashMap;
  }
  
  public static Location locationDeserialize(Object paramObject)
  {
    if ((paramObject instanceof Location)) {
      return (Location)paramObject;
    }
    Object localObject1 = new HashMap();
    Object localObject2;
    Iterator localIterator;
    String str;
    if ((paramObject instanceof MemorySection))
    {
      localObject2 = (MemorySection)paramObject;
      localIterator = ((MemorySection)localObject2).getKeys(false).iterator();
      while (localIterator.hasNext())
      {
        str = (String)localIterator.next();
        ((Map)localObject1).put(str, ((MemorySection)localObject2).get(str));
      }
    }
    else if ((paramObject instanceof ConfigurationSection))
    {
      localObject2 = (ConfigurationSection)paramObject;
      localIterator = ((ConfigurationSection)localObject2).getKeys(false).iterator();
      while (localIterator.hasNext())
      {
        str = (String)localIterator.next();
        ((Map)localObject1).put(str, ((ConfigurationSection)localObject2).get(str));
      }
    }
    else
    {
      localObject1 = (Map)paramObject;
    }
    try
    {
      if (localObject1 == null) {
        return null;
      }
      double d1 = Double.valueOf(((Map)localObject1).get("x").toString()).doubleValue();
      double d2 = Double.valueOf(((Map)localObject1).get("y").toString()).doubleValue();
      double d3 = Double.valueOf(((Map)localObject1).get("z").toString()).doubleValue();
      float f1 = Float.valueOf(((Map)localObject1).get("yaw").toString()).floatValue();
      float f2 = Float.valueOf(((Map)localObject1).get("pitch").toString()).floatValue();
      World localWorld = Main.getInstance().getServer().getWorld(((Map)localObject1).get("world").toString());
      if (localWorld == null) {
        return null;
      }
      return new Location(localWorld, d1, d2, d3, f1, f2);
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    return null;
  }
  
  public static Location locationDeserialize(String paramString, FileConfiguration paramFileConfiguration)
  {
    if (!paramFileConfiguration.contains(paramString)) {
      return null;
    }
    Object localObject = paramFileConfiguration.get(paramString);
    if ((localObject instanceof Location)) {
      return (Location)localObject;
    }
    try
    {
      Map localMap = (Map)paramFileConfiguration.get(paramString);
      if (localMap == null) {
        return null;
      }
      double d1 = Double.valueOf(localMap.get("x").toString()).doubleValue();
      double d2 = Double.valueOf(localMap.get("y").toString()).doubleValue();
      double d3 = Double.valueOf(localMap.get("z").toString()).doubleValue();
      float f1 = Float.valueOf(localMap.get("yaw").toString()).floatValue();
      float f2 = Float.valueOf(localMap.get("pitch").toString()).floatValue();
      World localWorld = Main.getInstance().getServer().getWorld(localMap.get("world").toString());
      if (localWorld == null) {
        return null;
      }
      return new Location(localWorld, d1, d2, d3, f1, f2);
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    return null;
  }
  
  public static Class<?> getPrimitiveWrapper(Class<?> paramClass)
  {
    if (!paramClass.isPrimitive()) {
      return paramClass;
    }
    if (paramClass.getSimpleName().equals("int")) {
      return Integer.class;
    }
    if (paramClass.getSimpleName().equals("long")) {
      return Long.class;
    }
    if (paramClass.getSimpleName().equals("short")) {
      return Short.class;
    }
    if (paramClass.getSimpleName().equals("byte")) {
      return Byte.class;
    }
    if (paramClass.getSimpleName().equals("float")) {
      return Float.class;
    }
    if (paramClass.getSimpleName().equals("boolean")) {
      return Boolean.class;
    }
    if (paramClass.getSimpleName().equals("char")) {
      return Character.class;
    }
    if (paramClass.getSimpleName().equals("double")) {
      return Double.class;
    }
    return paramClass;
  }
  
  public static Class<?> getGenericTypeOfParameter(Class<?> paramClass, String paramString, int paramInt)
  {
    try
    {
      Method localMethod = paramClass.getMethod(paramString, new Class[] { Set.class, Integer.TYPE });
      localObject = (ParameterizedType)localMethod.getGenericParameterTypes()[paramInt];
      return (Class)localObject.getActualTypeArguments()[0];
    }
    catch (Exception localException1)
    {
      try
      {
        Object localObject = paramClass.getMethod(paramString, new Class[] { HashSet.class, Integer.TYPE });
        ParameterizedType localParameterizedType = (ParameterizedType)localObject.getGenericParameterTypes()[paramInt];
        return (Class)localParameterizedType.getActualTypeArguments()[0];
      }
      catch (Exception localException2)
      {
        localException2.printStackTrace();
      }
    }
    return null;
  }
  
  public static final String unescape_perl_string(String paramString)
  {
    StringBuffer localStringBuffer = new StringBuffer(paramString.length());
    int i = 0;
    for (int j = 0; j < paramString.length(); j++)
    {
      int k = paramString.codePointAt(j);
      if (paramString.codePointAt(j) > 65535) {
        j++;
      }
      if (i == 0)
      {
        if (k == 92) {
          i = 1;
        } else {
          localStringBuffer.append(Character.toChars(k));
        }
      }
      else if (k == 92)
      {
        i = 0;
        localStringBuffer.append('\\');
        localStringBuffer.append('\\');
      }
      else
      {
        int m;
        int n;
        int i1;
        switch (k)
        {
        case 114: 
          localStringBuffer.append('\r');
          break;
        case 110: 
          localStringBuffer.append('\n');
          break;
        case 102: 
          localStringBuffer.append('\f');
          break;
        case 98: 
          localStringBuffer.append("\\b");
          break;
        case 116: 
          localStringBuffer.append('\t');
          break;
        case 97: 
          localStringBuffer.append('\007');
          break;
        case 101: 
          localStringBuffer.append('\033');
          break;
        case 99: 
          j++;
          if (j == paramString.length()) {
            die("trailing \\c");
          }
          k = paramString.codePointAt(j);
          if (k > 127) {
            die("expected ASCII after \\c");
          }
          localStringBuffer.append(Character.toChars(k ^ 0x40));
          break;
        case 56: 
        case 57: 
          die("illegal octal digit");
        case 49: 
        case 50: 
        case 51: 
        case 52: 
        case 53: 
        case 54: 
        case 55: 
          j--;
        case 48: 
          if (j + 1 == paramString.length())
          {
            localStringBuffer.append(Character.toChars(0));
          }
          else
          {
            j++;
            m = 0;
            for (n = 0; (n <= 2) && (j + n != paramString.length()); n++)
            {
              i1 = paramString.charAt(j + n);
              if ((i1 < 48) || (i1 > 55)) {
                break;
              }
              m++;
            }
            if (m == 0)
            {
              j--;
              localStringBuffer.append('\000');
            }
            else
            {
              i1 = 0;
              try
              {
                i1 = Integer.parseInt(paramString.substring(j, j + m), 8);
              }
              catch (NumberFormatException localNumberFormatException3)
              {
                die("invalid octal value for \\0 escape");
              }
              localStringBuffer.append(Character.toChars(i1));
              j += m - 1;
            }
          }
          break;
        case 120: 
          if (j + 2 > paramString.length()) {
            die("string too short for \\x escape");
          }
          j++;
          m = 0;
          if (paramString.charAt(j) == '{')
          {
            j++;
            m = 1;
          }
          for (n = 0; (n < 8) && ((m != 0) || (n != 2)); n++)
          {
            i1 = paramString.charAt(j + n);
            if (i1 > 127) {
              die("illegal non-ASCII hex digit in \\x escape");
            }
            if ((m != 0) && (i1 == 125)) {
              break;
            }
            if (((i1 < 48) || (i1 > 57)) && ((i1 < 97) || (i1 > 102)) && ((i1 < 65) || (i1 > 70))) {
              die(String.format("illegal hex digit #%d '%c' in \\x", new Object[] { Integer.valueOf(i1), Integer.valueOf(i1) }));
            }
          }
          if (n == 0) {
            die("empty braces in \\x{} escape");
          }
          i1 = 0;
          try
          {
            i1 = Integer.parseInt(paramString.substring(j, j + n), 16);
          }
          catch (NumberFormatException localNumberFormatException4)
          {
            die("invalid hex value for \\x escape");
          }
          localStringBuffer.append(Character.toChars(i1));
          if (m != 0) {
            n++;
          }
          j += n - 1;
          break;
        case 117: 
          if (j + 4 > paramString.length()) {
            die("string too short for \\u escape");
          }
          j++;
          for (m = 0; m < 4; m++) {
            if (paramString.charAt(j + m) > '') {
              die("illegal non-ASCII hex digit in \\u escape");
            }
          }
          n = 0;
          try
          {
            n = Integer.parseInt(paramString.substring(j, j + m), 16);
          }
          catch (NumberFormatException localNumberFormatException1)
          {
            die("invalid hex value for \\u escape");
          }
          localStringBuffer.append(Character.toChars(n));
          j += m - 1;
          break;
        case 85: 
          if (j + 8 > paramString.length()) {
            die("string too short for \\U escape");
          }
          j++;
          for (m = 0; m < 8; m++) {
            if (paramString.charAt(j + m) > '') {
              die("illegal non-ASCII hex digit in \\U escape");
            }
          }
          n = 0;
          try
          {
            n = Integer.parseInt(paramString.substring(j, j + m), 16);
          }
          catch (NumberFormatException localNumberFormatException2)
          {
            die("invalid hex value for \\U escape");
          }
          localStringBuffer.append(Character.toChars(n));
          j += m - 1;
          break;
        case 58: 
        case 59: 
        case 60: 
        case 61: 
        case 62: 
        case 63: 
        case 64: 
        case 65: 
        case 66: 
        case 67: 
        case 68: 
        case 69: 
        case 70: 
        case 71: 
        case 72: 
        case 73: 
        case 74: 
        case 75: 
        case 76: 
        case 77: 
        case 78: 
        case 79: 
        case 80: 
        case 81: 
        case 82: 
        case 83: 
        case 84: 
        case 86: 
        case 87: 
        case 88: 
        case 89: 
        case 90: 
        case 91: 
        case 92: 
        case 93: 
        case 94: 
        case 95: 
        case 96: 
        case 100: 
        case 103: 
        case 104: 
        case 105: 
        case 106: 
        case 107: 
        case 108: 
        case 109: 
        case 111: 
        case 112: 
        case 113: 
        case 115: 
        case 118: 
        case 119: 
        default: 
          localStringBuffer.append('\\');
          localStringBuffer.append(Character.toChars(k));
        }
        i = 0;
      }
    }
    if (i != 0) {
      localStringBuffer.append('\\');
    }
    return localStringBuffer.toString();
  }
  
  private static final void die(String paramString)
  {
    throw new IllegalArgumentException(paramString);
  }
  
  public static final String uniplus(String paramString)
  {
    if (paramString.length() == 0) {
      return "";
    }
    StringBuffer localStringBuffer = new StringBuffer(2 + 3 * paramString.length());
    localStringBuffer.append("U+");
    for (int i = 0; i < paramString.length(); i++)
    {
      localStringBuffer.append(String.format("%X", new Object[] { Integer.valueOf(paramString.codePointAt(i)) }));
      if (paramString.codePointAt(i) > 65535) {
        i++;
      }
      if (i + 1 < paramString.length()) {
        localStringBuffer.append(".");
      }
    }
    return localStringBuffer.toString();
  }
  
  public static BlockFace getCardinalDirection(Location paramLocation)
  {
    double d = (paramLocation.getYaw() - 90.0F) % 360.0F;
    if (d < 0.0D) {
      d += 360.0D;
    }
    if ((0.0D <= d) && (d < 22.5D)) {
      return BlockFace.NORTH;
    }
    if ((22.5D <= d) && (d < 67.5D)) {
      return BlockFace.NORTH_EAST;
    }
    if ((67.5D <= d) && (d < 112.5D)) {
      return BlockFace.EAST;
    }
    if ((112.5D <= d) && (d < 157.5D)) {
      return BlockFace.SOUTH_EAST;
    }
    if ((157.5D <= d) && (d < 202.5D)) {
      return BlockFace.SOUTH;
    }
    if ((202.5D <= d) && (d < 247.5D)) {
      return BlockFace.SOUTH_WEST;
    }
    if ((247.5D <= d) && (d < 292.5D)) {
      return BlockFace.WEST;
    }
    if ((292.5D <= d) && (d < 337.5D)) {
      return BlockFace.NORTH_WEST;
    }
    if ((337.5D <= d) && (d < 360.0D)) {
      return BlockFace.NORTH;
    }
    return BlockFace.NORTH;
  }
  
  public static boolean isSupportingTitles()
  {
    try
    {
      Class.forName(new StringBuilder().append("io.github.yannici.bedwars.Com.").append(Main.getInstance().getCurrentVersion()).append(".Title").toString());
      return true;
    }
    catch (Exception localException) {}
    return false;
  }
  
  public static String getFormattedTime(int paramInt)
  {
    int i = 0;
    int j = 0;
    int k = 0;
    String str1 = "";
    String str2 = "";
    String str3 = "";
    i = (int)Math.floor(paramInt / 60 / 60);
    j = (int)Math.floor(paramInt / 60) - i * 60;
    k = paramInt % 60;
    str3 = i < 10 ? new StringBuilder().append("0").append(String.valueOf(i)).toString() : String.valueOf(i);
    str1 = j < 10 ? new StringBuilder().append("0").append(String.valueOf(j)).toString() : String.valueOf(j);
    str2 = k < 10 ? new StringBuilder().append("0").append(String.valueOf(k)).toString() : String.valueOf(k);
    return new StringBuilder().append(str3).append(":").append(str1).append(":").append(str2).toString();
  }
  
  public static Material parseMaterial(String paramString)
  {
    try
    {
      if (isNumber(paramString)) {
        return Material.getMaterial(Integer.parseInt(paramString));
      }
      return Material.getMaterial(paramString.toUpperCase());
    }
    catch (Exception localException) {}
    return null;
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Utils
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */