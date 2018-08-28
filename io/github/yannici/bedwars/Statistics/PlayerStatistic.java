package io.github.yannici.bedwars.Statistics;

import io.github.yannici.bedwars.ChatWriter;
import io.github.yannici.bedwars.Database.DBGetField;
import io.github.yannici.bedwars.Database.DBSetField;
import io.github.yannici.bedwars.Main;
import io.github.yannici.bedwars.UUIDFetcher;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PlayerStatistic
  extends StoringTable
{
  public static final String tableName = "stats_players";
  private OfflinePlayer player = null;
  private UUID uuid = null;
  private int kills = 0;
  private int deaths = 0;
  private int destroyedBeds = 0;
  private int wins = 0;
  private int loses = 0;
  private int score = 0;
  private int currentScore = 0;
  private boolean once = false;
  
  public PlayerStatistic() {}
  
  public PlayerStatistic(OfflinePlayer paramOfflinePlayer)
  {
    this.player = paramOfflinePlayer;
  }
  
  public List<String> createStatisticLines(boolean paramBoolean, ChatColor paramChatColor1, ChatColor paramChatColor2)
  {
    return createStatisticLines(paramBoolean, paramChatColor1.toString(), paramChatColor2.toString());
  }
  
  public List<String> createStatisticLines(boolean paramBoolean, String paramString1, String paramString2)
  {
    ArrayList localArrayList1 = new ArrayList();
    HashMap localHashMap = new HashMap();
    ArrayList localArrayList2 = new ArrayList();
    Object localObject2;
    Object localObject3;
    for (localObject2 : getClass().getMethods()) {
      if (((Method)localObject2).isAnnotationPresent(StatField.class))
      {
        localObject3 = (StatField)((Method)localObject2).getAnnotation(StatField.class);
        if (localObject3 != null)
        {
          localHashMap.put(localObject3, localObject2);
          localArrayList2.add(localObject3);
        }
      }
    }
    ??? = null;
    ??? = new Comparator()
    {
      public int compare(StatField paramAnonymousStatField1, StatField paramAnonymousStatField2)
      {
        return Integer.valueOf(paramAnonymousStatField1.order()).compareTo(Integer.valueOf(paramAnonymousStatField2.order()));
      }
    };
    Collections.sort(localArrayList2, (Comparator)???);
    Iterator localIterator = localArrayList2.iterator();
    while (localIterator.hasNext())
    {
      StatField localStatField = (StatField)localIterator.next();
      localObject2 = (Method)localHashMap.get(localStatField);
      try
      {
        localObject3 = ((Method)localObject2).invoke(this, new Object[0]);
        if (localStatField.name().equals("kd")) {
          localObject3 = BigDecimal.valueOf(Double.valueOf(((Object)localObject3).toString()).doubleValue()).setScale(2, 4).toPlainString();
        }
        if (paramBoolean) {
          localArrayList1.add(ChatWriter.pluginMessage(paramString1 + Main._l(new StringBuilder().append("stats.").append(localStatField.name()).toString()) + ": " + paramString2 + ((Object)localObject3).toString()));
        } else {
          localArrayList1.add(paramString1 + Main._l(new StringBuilder().append("stats.").append(localStatField.name()).toString()) + ": " + paramString2 + ((Object)localObject3).toString());
        }
      }
      catch (Exception localException)
      {
        localException.printStackTrace();
      }
    }
    return localArrayList1;
  }
  
  public OfflinePlayer getPlayer()
  {
    return this.player;
  }
  
  public int getCurrentScore()
  {
    return this.currentScore;
  }
  
  public void addCurrentScore(int paramInt)
  {
    this.currentScore += paramInt;
  }
  
  public void setCurrentScore(int paramInt)
  {
    this.currentScore = paramInt;
  }
  
  @DBGetField(name="uuid", dbType="VARCHAR(255)")
  public String getUUID()
    throws Exception
  {
    if (this.uuid == null) {
      try
      {
        if (this.player.isOnline()) {
          this.uuid = this.player.getPlayer().getUniqueId();
        } else {
          this.uuid = this.player.getUniqueId();
        }
      }
      catch (Exception localException)
      {
        this.uuid = UUIDFetcher.getUUIDOf(this.player.getName());
      }
    }
    return this.uuid.toString();
  }
  
  @DBGetField(name="name", dbType="VARCHAR(255)")
  public String getName()
  {
    return this.player.getName();
  }
  
  @DBGetField(name="kills", dbType="INT(11)", defaultValue="0")
  @StatField(name="kills", order=10)
  public int getKills()
  {
    return this.kills;
  }
  
  @DBSetField(name="kills")
  public void setKills(int paramInt)
  {
    this.kills = paramInt;
  }
  
  @DBGetField(name="deaths", dbType="INT(11)", defaultValue="0")
  @StatField(name="deaths", order=20)
  public int getDeaths()
  {
    return this.deaths;
  }
  
  @StatField(name="kd", order=25)
  @DBGetField(name="kd", dbType="DOUBLE", defaultValue="0.0")
  public double getKD()
  {
    double d = 0.0D;
    if (getDeaths() == 0) {
      d = getKills();
    } else if (getKills() == 0) {
      d = 0.0D;
    } else {
      d = getKills() / getDeaths();
    }
    return d;
  }
  
  @DBSetField(name="deaths")
  public void setDeaths(int paramInt)
  {
    this.deaths = paramInt;
  }
  
  @DBGetField(name="destroyedBeds", dbType="INT(11)", defaultValue="0")
  @StatField(name="destroyedBeds", order=30)
  public int getDestroyedBeds()
  {
    return this.destroyedBeds;
  }
  
  @DBSetField(name="destroyedBeds")
  public void setDestroyedBeds(int paramInt)
  {
    this.destroyedBeds = paramInt;
  }
  
  @DBGetField(name="wins", dbType="INT(11)", defaultValue="0")
  @StatField(name="wins", order=40)
  public int getWins()
  {
    return this.wins;
  }
  
  @DBSetField(name="wins")
  public void setWins(int paramInt)
  {
    this.wins = paramInt;
  }
  
  @DBGetField(name="loses", dbType="INT(11)", defaultValue="0")
  @StatField(name="loses", order=50)
  public int getLoses()
  {
    return this.loses;
  }
  
  @DBSetField(name="loses")
  public void setLoses(int paramInt)
  {
    this.loses = paramInt;
  }
  
  @DBGetField(name="games", dbType="INT(11)", defaultValue="0")
  @StatField(name="games", order=60)
  public int getGames()
  {
    return this.wins + this.loses;
  }
  
  @DBGetField(name="score", dbType="INT(11)", defaultValue="0")
  @StatField(name="score", order=70)
  public int getScore()
  {
    return this.score;
  }
  
  @DBSetField(name="score")
  public void setScore(int paramInt)
  {
    this.score = paramInt;
  }
  
  public String getKeyField()
  {
    return "uuid";
  }
  
  public void load()
  {
    Main.getInstance().getPlayerStatisticManager().loadStatistic(this);
  }
  
  public void setOnce(boolean paramBoolean)
  {
    this.once = paramBoolean;
  }
  
  public boolean isOnce()
  {
    return this.once;
  }
  
  public void store()
  {
    Main.getInstance().getPlayerStatisticManager().storeStatistic(this);
  }
  
  public void setDefault()
  {
    this.kills = 0;
    this.deaths = 0;
    this.destroyedBeds = 0;
    this.loses = 0;
    this.wins = 0;
    this.score = 0;
  }
  
  public String getTableName()
  {
    return "stats_players";
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Statistics.PlayerStatistic
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */