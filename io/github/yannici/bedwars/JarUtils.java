package io.github.yannici.bedwars;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarUtils
{
  private static boolean RUNNING_FROM_JAR = false;
  
  public static boolean extractFromJar(String paramString1, String paramString2)
    throws IOException
  {
    if (getRunningJar() == null) {
      return false;
    }
    File localFile = new File(paramString2);
    if (localFile.isDirectory())
    {
      localFile.mkdir();
      return false;
    }
    if (!localFile.exists()) {
      localFile.getParentFile().mkdirs();
    }
    JarFile localJarFile = getRunningJar();
    Enumeration localEnumeration = localJarFile.entries();
    while (localEnumeration.hasMoreElements())
    {
      JarEntry localJarEntry = (JarEntry)localEnumeration.nextElement();
      if (localJarEntry.getName().contains(paramString1))
      {
        BufferedInputStream localBufferedInputStream = new BufferedInputStream(localJarFile.getInputStream(localJarEntry));
        BufferedOutputStream localBufferedOutputStream = new BufferedOutputStream(new FileOutputStream(localFile));
        copyInputStream(localBufferedInputStream, localBufferedOutputStream);
        localJarFile.close();
        return true;
      }
    }
    localJarFile.close();
    return false;
  }
  
  private static final void copyInputStream(InputStream paramInputStream, OutputStream paramOutputStream)
    throws IOException
  {
    try
    {
      byte[] arrayOfByte = new byte[4096];
      int i;
      while ((i = paramInputStream.read(arrayOfByte)) > 0) {
        paramOutputStream.write(arrayOfByte, 0, i);
      }
    }
    finally
    {
      paramOutputStream.flush();
      paramOutputStream.close();
      paramInputStream.close();
    }
  }
  
  public static URL getJarUrl(File paramFile)
    throws IOException
  {
    return new URL("jar:" + paramFile.toURI().toURL().toExternalForm() + "!/");
  }
  
  public static JarFile getRunningJar()
    throws IOException
  {
    if (!RUNNING_FROM_JAR) {
      return null;
    }
    String str = new File(JarUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getAbsolutePath();
    str = URLDecoder.decode(str, "UTF-8");
    return new JarFile(str);
  }
  
  static
  {
    URL localURL = JarUtils.class.getClassLoader().getResource("plugin.yml");
    if (localURL != null) {
      RUNNING_FROM_JAR = true;
    }
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.JarUtils
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */