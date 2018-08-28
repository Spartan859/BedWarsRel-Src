package io.github.yannici.bedwars;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.YamlConfigurationOptions;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

public class Metrics
{
  private static final int REVISION = 7;
  private static final String BASE_URL = "http://report.mcstats.org";
  private static final String REPORT_URL = "/plugin/%s";
  private static final int PING_INTERVAL = 15;
  private final Plugin plugin;
  private final Set<Graph> graphs = Collections.synchronizedSet(new HashSet());
  private final YamlConfiguration configuration;
  private final File configurationFile;
  private final String guid;
  private final boolean debug;
  private final Object optOutLock = new Object();
  private volatile BukkitTask task = null;
  
  public Metrics(Plugin paramPlugin)
    throws IOException
  {
    if (paramPlugin == null) {
      throw new IllegalArgumentException("Plugin cannot be null");
    }
    this.plugin = paramPlugin;
    this.configurationFile = getConfigFile();
    this.configuration = YamlConfiguration.loadConfiguration(this.configurationFile);
    this.configuration.addDefault("opt-out", Boolean.valueOf(false));
    this.configuration.addDefault("guid", UUID.randomUUID().toString());
    this.configuration.addDefault("debug", Boolean.valueOf(false));
    if (this.configuration.get("guid", null) == null)
    {
      this.configuration.options().header("http://mcstats.org").copyDefaults(true);
      this.configuration.save(this.configurationFile);
    }
    this.guid = this.configuration.getString("guid");
    this.debug = this.configuration.getBoolean("debug", false);
  }
  
  public Graph createGraph(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("Graph name cannot be null");
    }
    Graph localGraph = new Graph(paramString, null);
    this.graphs.add(localGraph);
    return localGraph;
  }
  
  public void addGraph(Graph paramGraph)
  {
    if (paramGraph == null) {
      throw new IllegalArgumentException("Graph cannot be null");
    }
    this.graphs.add(paramGraph);
  }
  
  public boolean start()
  {
    synchronized (this.optOutLock)
    {
      if (isOptOut()) {
        return false;
      }
      if (this.task != null) {
        return true;
      }
      this.task = this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, new Object()
      {
        private boolean firstPost = true;
        
        public void run()
        {
          try
          {
            synchronized (Metrics.this.optOutLock)
            {
              if ((Metrics.this.isOptOut()) && (Metrics.this.task != null))
              {
                Metrics.this.task.cancel();
                Metrics.this.task = null;
                Iterator localIterator = Metrics.this.graphs.iterator();
                while (localIterator.hasNext())
                {
                  Metrics.Graph localGraph = (Metrics.Graph)localIterator.next();
                  localGraph.onOptOut();
                }
              }
            }
            Metrics.this.postPlugin(!this.firstPost);
            this.firstPost = false;
          }
          catch (IOException localIOException)
          {
            if (Metrics.this.debug) {
              Bukkit.getLogger().log(Level.INFO, "[Metrics] " + localIOException.getMessage());
            }
          }
        }
      }, 0L, 18000L);
      return true;
    }
  }
  
  public boolean isOptOut()
  {
    synchronized (this.optOutLock)
    {
      try
      {
        this.configuration.load(getConfigFile());
      }
      catch (IOException localIOException)
      {
        if (this.debug) {
          Bukkit.getLogger().log(Level.INFO, new StringBuilder().append("[Metrics] ").append(localIOException.getMessage()).toString());
        }
        return true;
      }
      catch (InvalidConfigurationException localInvalidConfigurationException)
      {
        if (this.debug) {
          Bukkit.getLogger().log(Level.INFO, new StringBuilder().append("[Metrics] ").append(localInvalidConfigurationException.getMessage()).toString());
        }
        return true;
      }
      return this.configuration.getBoolean("opt-out", false);
    }
  }
  
  public void enable()
    throws IOException
  {
    synchronized (this.optOutLock)
    {
      if (isOptOut())
      {
        this.configuration.set("opt-out", Boolean.valueOf(false));
        this.configuration.save(this.configurationFile);
      }
      if (this.task == null) {
        start();
      }
    }
  }
  
  public void disable()
    throws IOException
  {
    synchronized (this.optOutLock)
    {
      if (!isOptOut())
      {
        this.configuration.set("opt-out", Boolean.valueOf(true));
        this.configuration.save(this.configurationFile);
      }
      if (this.task != null)
      {
        this.task.cancel();
        this.task = null;
      }
    }
  }
  
  public File getConfigFile()
  {
    File localFile = this.plugin.getDataFolder().getParentFile();
    return new File(new File(localFile, "PluginMetrics"), "config.yml");
  }
  
  private int getOnlinePlayers()
  {
    try
    {
      Method localMethod = Server.class.getMethod("getOnlinePlayers", new Class[0]);
      if (localMethod.getReturnType().equals(Collection.class)) {
        return ((Collection)localMethod.invoke(Bukkit.getServer(), new Object[0])).size();
      }
      return ((Player[])localMethod.invoke(Bukkit.getServer(), new Object[0])).length;
    }
    catch (Exception localException)
    {
      if (this.debug) {
        Bukkit.getLogger().log(Level.INFO, new StringBuilder().append("[Metrics] ").append(localException.getMessage()).toString());
      }
    }
    return 0;
  }
  
  private void postPlugin(boolean paramBoolean)
    throws IOException
  {
    PluginDescriptionFile localPluginDescriptionFile = this.plugin.getDescription();
    String str1 = localPluginDescriptionFile.getName();
    boolean bool = Bukkit.getServer().getOnlineMode();
    String str2 = localPluginDescriptionFile.getVersion();
    String str3 = Bukkit.getVersion();
    int i = getOnlinePlayers();
    StringBuilder localStringBuilder = new StringBuilder(1024);
    localStringBuilder.append('{');
    appendJSONPair(localStringBuilder, "guid", this.guid);
    appendJSONPair(localStringBuilder, "plugin_version", str2);
    appendJSONPair(localStringBuilder, "server_version", str3);
    appendJSONPair(localStringBuilder, "players_online", Integer.toString(i));
    String str4 = System.getProperty("os.name");
    String str5 = System.getProperty("os.arch");
    String str6 = System.getProperty("os.version");
    String str7 = System.getProperty("java.version");
    int j = Runtime.getRuntime().availableProcessors();
    if (str5.equals("amd64")) {
      str5 = "x86_64";
    }
    appendJSONPair(localStringBuilder, "osname", str4);
    appendJSONPair(localStringBuilder, "osarch", str5);
    appendJSONPair(localStringBuilder, "osversion", str6);
    appendJSONPair(localStringBuilder, "cores", Integer.toString(j));
    appendJSONPair(localStringBuilder, "auth_mode", bool ? "1" : "0");
    appendJSONPair(localStringBuilder, "java_version", str7);
    if (paramBoolean) {
      appendJSONPair(localStringBuilder, "ping", "1");
    }
    if (this.graphs.size() > 0) {
      synchronized (this.graphs)
      {
        localStringBuilder.append(',');
        localStringBuilder.append('"');
        localStringBuilder.append("graphs");
        localStringBuilder.append('"');
        localStringBuilder.append(':');
        localStringBuilder.append('{');
        int k = 1;
        localObject1 = this.graphs.iterator();
        while (((Iterator)localObject1).hasNext())
        {
          localObject2 = (Graph)((Iterator)localObject1).next();
          localObject3 = new StringBuilder();
          ((StringBuilder)localObject3).append('{');
          localObject4 = ((Graph)localObject2).getPlotters().iterator();
          while (((Iterator)localObject4).hasNext())
          {
            localObject5 = (Plotter)((Iterator)localObject4).next();
            appendJSONPair((StringBuilder)localObject3, ((Plotter)localObject5).getColumnName(), Integer.toString(((Plotter)localObject5).getValue()));
          }
          ((StringBuilder)localObject3).append('}');
          if (k == 0) {
            localStringBuilder.append(',');
          }
          localStringBuilder.append(escapeJSON(((Graph)localObject2).getName()));
          localStringBuilder.append(':');
          localStringBuilder.append((CharSequence)localObject3);
          k = 0;
        }
        localStringBuilder.append('}');
      }
    }
    localStringBuilder.append('}');
    ??? = new URL(new StringBuilder().append("http://report.mcstats.org").append(String.format("/plugin/%s", new Object[] { urlEncode(str1) })).toString());
    URLConnection localURLConnection;
    if (isMineshafterPresent()) {
      localURLConnection = ((URL)???).openConnection(Proxy.NO_PROXY);
    } else {
      localURLConnection = ((URL)???).openConnection();
    }
    Object localObject1 = localStringBuilder.toString().getBytes();
    Object localObject2 = gzip(localStringBuilder.toString());
    localURLConnection.addRequestProperty("User-Agent", "MCStats/7");
    localURLConnection.addRequestProperty("Content-Type", "application/json");
    localURLConnection.addRequestProperty("Content-Encoding", "gzip");
    localURLConnection.addRequestProperty("Content-Length", Integer.toString(localObject2.length));
    localURLConnection.addRequestProperty("Accept", "application/json");
    localURLConnection.addRequestProperty("Connection", "close");
    localURLConnection.setDoOutput(true);
    if (this.debug) {
      System.out.println(new StringBuilder().append("[Metrics] Prepared request for ").append(str1).append(" uncompressed=").append(localObject1.length).append(" compressed=").append(localObject2.length).toString());
    }
    Object localObject3 = localURLConnection.getOutputStream();
    ((OutputStream)localObject3).write((byte[])localObject2);
    ((OutputStream)localObject3).flush();
    Object localObject4 = new BufferedReader(new InputStreamReader(localURLConnection.getInputStream()));
    Object localObject5 = ((BufferedReader)localObject4).readLine();
    ((OutputStream)localObject3).close();
    ((BufferedReader)localObject4).close();
    if ((localObject5 == null) || (((String)localObject5).startsWith("ERR")) || (((String)localObject5).startsWith("7")))
    {
      if (localObject5 == null) {
        localObject5 = "null";
      } else if (((String)localObject5).startsWith("7")) {
        localObject5 = ((String)localObject5).substring(((String)localObject5).startsWith("7,") ? 2 : 1);
      }
      throw new IOException((String)localObject5);
    }
    if ((((String)localObject5).equals("1")) || (((String)localObject5).contains("This is your first update this hour"))) {
      synchronized (this.graphs)
      {
        Iterator localIterator1 = this.graphs.iterator();
        while (localIterator1.hasNext())
        {
          Graph localGraph = (Graph)localIterator1.next();
          Iterator localIterator2 = localGraph.getPlotters().iterator();
          while (localIterator2.hasNext())
          {
            Plotter localPlotter = (Plotter)localIterator2.next();
            localPlotter.reset();
          }
        }
      }
    }
  }
  
  public static byte[] gzip(String paramString)
  {
    localByteArrayOutputStream = new ByteArrayOutputStream();
    GZIPOutputStream localGZIPOutputStream = null;
    try
    {
      localGZIPOutputStream = new GZIPOutputStream(localByteArrayOutputStream);
      localGZIPOutputStream.write(paramString.getBytes("UTF-8"));
      return localByteArrayOutputStream.toByteArray();
    }
    catch (IOException localIOException2)
    {
      localIOException2.printStackTrace();
    }
    finally
    {
      if (localGZIPOutputStream != null) {
        try
        {
          localGZIPOutputStream.close();
        }
        catch (IOException localIOException4) {}
      }
    }
  }
  
  private boolean isMineshafterPresent()
  {
    try
    {
      Class.forName("mineshafter.MineServer");
      return true;
    }
    catch (Exception localException) {}
    return false;
  }
  
  private static void appendJSONPair(StringBuilder paramStringBuilder, String paramString1, String paramString2)
    throws UnsupportedEncodingException
  {
    int i = 0;
    try
    {
      if ((paramString2.equals("0")) || (!paramString2.endsWith("0")))
      {
        Double.parseDouble(paramString2);
        i = 1;
      }
    }
    catch (NumberFormatException localNumberFormatException)
    {
      i = 0;
    }
    if (paramStringBuilder.charAt(paramStringBuilder.length() - 1) != '{') {
      paramStringBuilder.append(',');
    }
    paramStringBuilder.append(escapeJSON(paramString1));
    paramStringBuilder.append(':');
    if (i != 0) {
      paramStringBuilder.append(paramString2);
    } else {
      paramStringBuilder.append(escapeJSON(paramString2));
    }
  }
  
  private static String escapeJSON(String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append('"');
    for (int i = 0; i < paramString.length(); i++)
    {
      char c = paramString.charAt(i);
      switch (c)
      {
      case '"': 
      case '\\': 
        localStringBuilder.append('\\');
        localStringBuilder.append(c);
        break;
      case '\b': 
        localStringBuilder.append("\\b");
        break;
      case '\t': 
        localStringBuilder.append("\\t");
        break;
      case '\n': 
        localStringBuilder.append("\\n");
        break;
      case '\r': 
        localStringBuilder.append("\\r");
        break;
      default: 
        if (c < ' ')
        {
          String str = new StringBuilder().append("000").append(Integer.toHexString(c)).toString();
          localStringBuilder.append(new StringBuilder().append("\\u").append(str.substring(str.length() - 4)).toString());
        }
        else
        {
          localStringBuilder.append(c);
        }
        break;
      }
    }
    localStringBuilder.append('"');
    return localStringBuilder.toString();
  }
  
  private static String urlEncode(String paramString)
    throws UnsupportedEncodingException
  {
    return URLEncoder.encode(paramString, "UTF-8");
  }
  
  public static abstract class Plotter
  {
    private final String name;
    
    public Plotter()
    {
      this("Default");
    }
    
    public Plotter(String paramString)
    {
      this.name = paramString;
    }
    
    public abstract int getValue();
    
    public String getColumnName()
    {
      return this.name;
    }
    
    public void reset() {}
    
    public int hashCode()
    {
      return getColumnName().hashCode();
    }
    
    public boolean equals(Object paramObject)
    {
      if (!(paramObject instanceof Plotter)) {
        return false;
      }
      Plotter localPlotter = (Plotter)paramObject;
      return (localPlotter.name.equals(this.name)) && (localPlotter.getValue() == getValue());
    }
  }
  
  public static class Graph
  {
    private final String name;
    private final Set<Metrics.Plotter> plotters = new LinkedHashSet();
    
    private Graph(String paramString)
    {
      this.name = paramString;
    }
    
    public String getName()
    {
      return this.name;
    }
    
    public void addPlotter(Metrics.Plotter paramPlotter)
    {
      this.plotters.add(paramPlotter);
    }
    
    public void removePlotter(Metrics.Plotter paramPlotter)
    {
      this.plotters.remove(paramPlotter);
    }
    
    public Set<Metrics.Plotter> getPlotters()
    {
      return Collections.unmodifiableSet(this.plotters);
    }
    
    public int hashCode()
    {
      return this.name.hashCode();
    }
    
    public boolean equals(Object paramObject)
    {
      if (!(paramObject instanceof Graph)) {
        return false;
      }
      Graph localGraph = (Graph)paramObject;
      return localGraph.name.equals(this.name);
    }
    
    protected void onOptOut() {}
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Metrics
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */