package io.github.yannici.bedwars.Updater;

import io.github.yannici.bedwars.Main;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.bukkit.Server;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.YamlConfigurationOptions;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class PluginUpdater
{
  private static final String TITLE_VALUE = "name";
  private static final String LINK_VALUE = "downloadUrl";
  private static final String TYPE_VALUE = "releaseType";
  private static final String VERSION_VALUE = "gameVersion";
  private static final String QUERY = "/servermods/files?projectIds=";
  private static final String HOST = "https://api.curseforge.com";
  private static final String USER_AGENT = "Bedwars Plugin Updater";
  private static final int BYTE_SIZE = 1024;
  private static final String API_KEY_CONFIG_KEY = "api-key";
  private static final String DISABLE_CONFIG_KEY = "disable";
  private static final String API_KEY_DEFAULT = "c9012155bcd01874f55bfd2f38de962dc0dc353a";
  private static final boolean DISABLE_DEFAULT = false;
  private final Plugin plugin;
  private final UpdateType type;
  private final boolean announce;
  private final File file;
  private final File updateFolder;
  private final UpdateCallback callback;
  private int id = -1;
  private String apiKey = null;
  private String versionName;
  private String versionLink;
  private String versionType;
  private String versionGameVersion;
  private String versionCustom;
  private boolean lastVersionCheck = false;
  private URL url;
  private Thread thread;
  private UpdateResult result = UpdateResult.SUCCESS;
  
  public PluginUpdater(Plugin paramPlugin, int paramInt, File paramFile, UpdateType paramUpdateType, boolean paramBoolean)
  {
    this(paramPlugin, paramInt, paramFile, paramUpdateType, null, paramBoolean);
  }
  
  public PluginUpdater(Plugin paramPlugin, int paramInt, File paramFile, UpdateType paramUpdateType, UpdateCallback paramUpdateCallback)
  {
    this(paramPlugin, paramInt, paramFile, paramUpdateType, paramUpdateCallback, false);
  }
  
  public PluginUpdater(Plugin paramPlugin, int paramInt, File paramFile, UpdateType paramUpdateType, UpdateCallback paramUpdateCallback, boolean paramBoolean)
  {
    this.plugin = paramPlugin;
    this.type = paramUpdateType;
    this.announce = paramBoolean;
    this.file = paramFile;
    this.id = paramInt;
    this.updateFolder = this.plugin.getServer().getUpdateFolderFile();
    this.callback = paramUpdateCallback;
    File localFile1 = this.plugin.getDataFolder().getParentFile();
    File localFile2 = new File(localFile1, "Updater");
    File localFile3 = new File(localFile2, "bedwarsrel.yml");
    YamlConfiguration localYamlConfiguration = new YamlConfiguration();
    localYamlConfiguration.options().header("This configuration file affects all plugins using the Updater system (version 2+ - http://forums.bukkit.org/threads/96681/ )\nIf you wish to use your API key, read http://wiki.bukkit.org/ServerMods_API and place it below.\nSome updating systems will not adhere to the disabled value, but these may be turned off in their plugin's configuration.");
    localYamlConfiguration.addDefault("api-key", "c9012155bcd01874f55bfd2f38de962dc0dc353a");
    localYamlConfiguration.addDefault("disable", Boolean.valueOf(false));
    if (!localFile2.exists()) {
      fileIOOrError(localFile2, localFile2.mkdir(), true);
    }
    int i = !localFile3.exists() ? 1 : 0;
    try
    {
      if (i != 0)
      {
        fileIOOrError(localFile3, localFile3.createNewFile(), true);
        localYamlConfiguration.options().copyDefaults(true);
        localYamlConfiguration.save(localFile3);
      }
      else
      {
        localYamlConfiguration.load(localFile3);
      }
    }
    catch (Exception localException)
    {
      String str2;
      if (i != 0) {
        str2 = new StringBuilder().append("The updater could not create configuration at ").append(localFile2.getAbsolutePath()).toString();
      } else {
        str2 = new StringBuilder().append("The updater could not load configuration at ").append(localFile2.getAbsolutePath()).toString();
      }
      this.plugin.getLogger().log(Level.SEVERE, str2, localException);
    }
    if (localYamlConfiguration.getBoolean("disable"))
    {
      this.result = UpdateResult.DISABLED;
      return;
    }
    String str1 = localYamlConfiguration.getString("api-key");
    if (("c9012155bcd01874f55bfd2f38de962dc0dc353a".equalsIgnoreCase(str1)) || ("".equals(str1))) {
      str1 = null;
    }
    this.apiKey = str1;
    try
    {
      this.url = new URL(new StringBuilder().append("https://api.curseforge.com/servermods/files?projectIds=").append(this.id).toString());
    }
    catch (MalformedURLException localMalformedURLException)
    {
      this.plugin.getLogger().log(Level.SEVERE, new StringBuilder().append("The project ID provided for updating, ").append(this.id).append(" is invalid.").toString(), localMalformedURLException);
      this.result = UpdateResult.FAIL_BADID;
    }
    if (this.result != UpdateResult.FAIL_BADID)
    {
      this.thread = new Thread(new UpdateRunnable(null));
      this.thread.start();
    }
    else
    {
      runUpdater();
    }
  }
  
  public UpdateResult getResult()
  {
    waitForThread();
    return this.result;
  }
  
  public ReleaseType getLatestType()
  {
    waitForThread();
    if (this.versionType != null) {
      for (ReleaseType localReleaseType : ReleaseType.values()) {
        if (this.versionType.equalsIgnoreCase(localReleaseType.name())) {
          return localReleaseType;
        }
      }
    }
    return null;
  }
  
  public String getLatestGameVersion()
  {
    waitForThread();
    return this.versionGameVersion;
  }
  
  public String getLatestName()
  {
    waitForThread();
    return this.versionName;
  }
  
  public String getLatestFileLink()
  {
    waitForThread();
    return this.versionLink;
  }
  
  private void waitForThread()
  {
    if ((this.thread != null) && (this.thread.isAlive())) {
      try
      {
        this.thread.join();
      }
      catch (InterruptedException localInterruptedException)
      {
        this.plugin.getLogger().log(Level.SEVERE, null, localInterruptedException);
      }
    }
  }
  
  private void saveFile(String paramString)
  {
    File localFile1 = this.updateFolder;
    deleteOldFiles();
    if (!localFile1.exists()) {
      fileIOOrError(localFile1, localFile1.mkdir(), true);
    }
    downloadFile();
    File localFile2 = new File(localFile1.getAbsolutePath(), paramString);
    if (localFile2.getName().endsWith(".zip")) {
      unzip(localFile2.getAbsolutePath());
    }
    if (this.announce) {
      this.plugin.getLogger().info("Finished updating.");
    }
  }
  
  private void downloadFile()
  {
    BufferedInputStream localBufferedInputStream = null;
    FileOutputStream localFileOutputStream = null;
    try
    {
      URL localURL = new URL(this.versionLink);
      int i = localURL.openConnection().getContentLength();
      localBufferedInputStream = new BufferedInputStream(localURL.openStream());
      localFileOutputStream = new FileOutputStream(new File(this.updateFolder, this.file.getName()));
      byte[] arrayOfByte = new byte[1024];
      if (this.announce) {
        this.plugin.getLogger().info(new StringBuilder().append("About to download a new update: ").append(this.versionName).toString());
      }
      long l = 0L;
      int j;
      while ((j = localBufferedInputStream.read(arrayOfByte, 0, 1024)) != -1)
      {
        l += j;
        localFileOutputStream.write(arrayOfByte, 0, j);
        int k = (int)(l * 100L / i);
        if ((this.announce) && (k % 10 == 0)) {
          this.plugin.getLogger().info(new StringBuilder().append("Downloading update: ").append(k).append("% of ").append(i).append(" bytes.").toString());
        }
      }
      return;
    }
    catch (Exception localException)
    {
      this.plugin.getLogger().log(Level.WARNING, "The auto-updater tried to download a new update, but was unsuccessful.", localException);
      this.result = UpdateResult.FAIL_DOWNLOAD;
    }
    finally
    {
      try
      {
        if (localBufferedInputStream != null) {
          localBufferedInputStream.close();
        }
      }
      catch (IOException localIOException5)
      {
        this.plugin.getLogger().log(Level.SEVERE, null, localIOException5);
      }
      try
      {
        if (localFileOutputStream != null) {
          localFileOutputStream.close();
        }
      }
      catch (IOException localIOException6)
      {
        this.plugin.getLogger().log(Level.SEVERE, null, localIOException6);
      }
    }
  }
  
  private void deleteOldFiles()
  {
    File[] arrayOfFile1 = listFilesOrError(this.updateFolder);
    for (File localFile : arrayOfFile1) {
      if (localFile.getName().endsWith(".zip")) {
        fileIOOrError(localFile, localFile.mkdir(), true);
      }
    }
  }
  
  private void unzip(String paramString)
  {
    File localFile1 = new File(paramString);
    try
    {
      String str1 = paramString.substring(0, paramString.length() - 4);
      ZipFile localZipFile = new ZipFile(localFile1);
      Enumeration localEnumeration = localZipFile.entries();
      while (localEnumeration.hasMoreElements())
      {
        ZipEntry localZipEntry = (ZipEntry)localEnumeration.nextElement();
        File localFile2 = new File(str1, localZipEntry.getName());
        fileIOOrError(localFile2.getParentFile(), localFile2.getParentFile().mkdirs(), true);
        if (!localZipEntry.isDirectory())
        {
          BufferedInputStream localBufferedInputStream = new BufferedInputStream(localZipFile.getInputStream(localZipEntry));
          byte[] arrayOfByte = new byte[1024];
          FileOutputStream localFileOutputStream = new FileOutputStream(localFile2);
          BufferedOutputStream localBufferedOutputStream = new BufferedOutputStream(localFileOutputStream, 1024);
          int i;
          while ((i = localBufferedInputStream.read(arrayOfByte, 0, 1024)) != -1) {
            localBufferedOutputStream.write(arrayOfByte, 0, i);
          }
          localBufferedOutputStream.flush();
          localBufferedOutputStream.close();
          localBufferedInputStream.close();
          String str2 = localFile2.getName();
          if ((str2.endsWith(".jar")) && (pluginExists(str2)))
          {
            File localFile3 = new File(this.updateFolder, str2);
            fileIOOrError(localFile3, localFile2.renameTo(localFile3), true);
          }
        }
      }
      localZipFile.close();
      moveNewZipFiles(str1);
    }
    catch (IOException localIOException)
    {
      this.plugin.getLogger().log(Level.SEVERE, "The auto-updater tried to unzip a new update file, but was unsuccessful.", localIOException);
      this.result = UpdateResult.FAIL_DOWNLOAD;
    }
    finally
    {
      fileIOOrError(localFile1, localFile1.delete(), false);
    }
  }
  
  private void moveNewZipFiles(String paramString)
  {
    File[] arrayOfFile1 = listFilesOrError(new File(paramString));
    for (File localFile1 : arrayOfFile1)
    {
      if ((localFile1.isDirectory()) && (pluginExists(localFile1.getName())))
      {
        File localFile2 = new File(this.plugin.getDataFolder().getParent(), localFile1.getName());
        File[] arrayOfFile2 = listFilesOrError(localFile1);
        File[] arrayOfFile3 = listFilesOrError(localFile2);
        for (File localFile3 : arrayOfFile2)
        {
          int n = 0;
          for (Object localObject3 : arrayOfFile3) {
            if (localObject3.getName().equals(localFile3.getName()))
            {
              n = 1;
              break;
            }
          }
          if (n == 0)
          {
            ??? = new File(localFile2, localFile3.getName());
            fileIOOrError((File)???, localFile3.renameTo((File)???), true);
          }
          else
          {
            fileIOOrError(localFile3, localFile3.delete(), false);
          }
        }
      }
      fileIOOrError(localFile1, localFile1.delete(), false);
    }
    ??? = new File(paramString);
    fileIOOrError((File)???, ((File)???).delete(), false);
  }
  
  private boolean pluginExists(String paramString)
  {
    File[] arrayOfFile1 = listFilesOrError(new File("plugins"));
    for (File localFile : arrayOfFile1) {
      if (localFile.getName().equals(paramString)) {
        return true;
      }
    }
    return false;
  }
  
  private boolean versionCheck()
  {
    String str = this.versionCustom;
    if (str == null) {
      return false;
    }
    String[] arrayOfString1 = this.plugin.getDescription().getVersion().split("\\.");
    String[] arrayOfString2 = str.split("\\.");
    try
    {
      int i = Integer.valueOf(arrayOfString1[0]).intValue();
      int j = Integer.valueOf(arrayOfString2[0]).intValue();
      if (j > i) {
        return true;
      }
      if (i > j) {
        return false;
      }
      int k = Integer.valueOf(arrayOfString1[1]).intValue();
      int m = Integer.valueOf(arrayOfString2[1]).intValue();
      if (m > k) {
        return true;
      }
      if (k > m) {
        return false;
      }
      int n = Integer.valueOf(arrayOfString1[2]).intValue();
      int i1 = Integer.valueOf(arrayOfString2[2]).intValue();
      if (i1 > n) {
        return true;
      }
      if (n > i1) {
        return false;
      }
    }
    catch (Exception localException) {}
    return false;
  }
  
  public boolean shouldUpdate(String paramString1, String paramString2)
  {
    return !paramString1.equalsIgnoreCase(paramString2);
  }
  
  private String getCustomVersion()
  {
    String str = null;
    try
    {
      URL localURL = new URL("https://raw.githubusercontent.com/Yannici/bedwars-reloaded/master/release.txt");
      URLConnection localURLConnection = null;
      if (Main.getInstance().isMineshafterPresent()) {
        localURLConnection = localURL.openConnection(Proxy.NO_PROXY);
      } else {
        localURLConnection = localURL.openConnection();
      }
      localURLConnection.setUseCaches(false);
      localURLConnection.setDoOutput(true);
      BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(localURLConnection.getInputStream()));
      str = localBufferedReader.readLine();
      localBufferedReader.close();
    }
    catch (Exception localException)
    {
      this.plugin.getLogger().severe("The updater could not check version!");
    }
    return str;
  }
  
  private boolean read()
  {
    try
    {
      URLConnection localURLConnection = this.url.openConnection();
      localURLConnection.setConnectTimeout(5000);
      if (this.apiKey != null) {
        localURLConnection.addRequestProperty("X-API-Key", this.apiKey);
      }
      localURLConnection.addRequestProperty("User-Agent", "Bedwars Plugin Updater");
      localURLConnection.setDoOutput(true);
      BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(localURLConnection.getInputStream()));
      String str = localBufferedReader.readLine();
      JSONArray localJSONArray = (JSONArray)JSONValue.parse(str);
      if (localJSONArray.isEmpty())
      {
        this.plugin.getLogger().warning(new StringBuilder().append("The updater could not find any files for the project id ").append(this.id).toString());
        this.result = UpdateResult.FAIL_BADID;
        return false;
      }
      JSONObject localJSONObject = (JSONObject)localJSONArray.get(localJSONArray.size() - 1);
      this.versionName = ((String)localJSONObject.get("name"));
      this.versionLink = ((String)localJSONObject.get("downloadUrl"));
      this.versionType = ((String)localJSONObject.get("releaseType"));
      this.versionGameVersion = ((String)localJSONObject.get("gameVersion"));
      this.versionCustom = getCustomVersion();
      return true;
    }
    catch (IOException localIOException)
    {
      if (localIOException.getMessage().contains("HTTP response code: 403"))
      {
        this.plugin.getLogger().severe("dev.bukkit.org rejected the API key provided in plugins/Updater/config.yml");
        this.plugin.getLogger().severe("Please double-check your configuration to ensure it is correct.");
        this.result = UpdateResult.FAIL_APIKEY;
      }
      else
      {
        this.plugin.getLogger().severe("The updater could not contact dev.bukkit.org for updating.");
        this.plugin.getLogger().severe("If you have not recently modified your configuration and this is the first time you are seeing this message, the site may be experiencing temporary downtime.");
        this.result = UpdateResult.FAIL_DBO;
      }
      this.plugin.getLogger().log(Level.SEVERE, null, localIOException);
    }
    return false;
  }
  
  private void fileIOOrError(File paramFile, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (!paramBoolean1) {
      this.plugin.getLogger().severe(new StringBuilder().append("The updater could not ").append(paramBoolean2 ? "create" : "delete").append(" file at: ").append(paramFile.getAbsolutePath()).toString());
    }
  }
  
  private File[] listFilesOrError(File paramFile)
  {
    File[] arrayOfFile = paramFile.listFiles();
    if (arrayOfFile == null)
    {
      this.plugin.getLogger().severe(new StringBuilder().append("The updater could not access files at: ").append(this.updateFolder.getAbsolutePath()).toString());
      return new File[0];
    }
    return arrayOfFile;
  }
  
  private void runUpdater()
  {
    if ((this.url != null) && (read()) && (versionCheck()))
    {
      this.lastVersionCheck = true;
      if ((this.versionLink != null) && (this.type != UpdateType.NO_DOWNLOAD))
      {
        String str = this.file.getName();
        if (this.versionLink.endsWith(".zip")) {
          str = this.versionLink.substring(this.versionLink.lastIndexOf("/") + 1);
        }
        saveFile(str);
      }
      else
      {
        this.result = UpdateResult.UPDATE_AVAILABLE;
      }
    }
    else
    {
      this.result = UpdateResult.NO_UPDATE;
      this.lastVersionCheck = false;
    }
    if (this.callback != null) {
      new BukkitRunnable()
      {
        public void run()
        {
          PluginUpdater.this.runCallback();
        }
      }.runTask(this.plugin);
    }
  }
  
  private void runCallback()
  {
    this.callback.onFinish(this);
  }
  
  public boolean isLatestVersionCheck()
  {
    return this.lastVersionCheck;
  }
  
  private class UpdateRunnable
    implements Runnable
  {
    private UpdateRunnable() {}
    
    public void run()
    {
      PluginUpdater.this.runUpdater();
    }
  }
  
  public static abstract interface UpdateCallback
  {
    public abstract void onFinish(PluginUpdater paramPluginUpdater);
  }
  
  public static enum ReleaseType
  {
    ALPHA,  BETA,  RELEASE;
    
    private ReleaseType() {}
  }
  
  public static enum UpdateType
  {
    DEFAULT,  NO_VERSION_CHECK,  NO_DOWNLOAD;
    
    private UpdateType() {}
  }
  
  public static enum UpdateResult
  {
    SUCCESS,  NO_UPDATE,  DISABLED,  FAIL_DOWNLOAD,  FAIL_DBO,  FAIL_NOVERSION,  FAIL_BADID,  FAIL_APIKEY,  UPDATE_AVAILABLE;
    
    private UpdateResult() {}
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Updater.PluginUpdater
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */