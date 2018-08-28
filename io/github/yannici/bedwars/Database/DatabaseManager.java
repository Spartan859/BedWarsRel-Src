package io.github.yannici.bedwars.Database;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;
import io.github.yannici.bedwars.ChatWriter;
import io.github.yannici.bedwars.Main;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;

public class DatabaseManager
{
  private String host = null;
  private int port = 3306;
  private String user = null;
  private String password = null;
  private String database = null;
  private ComboPooledDataSource dataSource = null;
  private static DatabaseManager instance = null;
  public static String DBPrefix = "bw_";
  
  public DatabaseManager(String paramString1, int paramInt, String paramString2, String paramString3, String paramString4)
  {
    this.host = paramString1;
    this.port = paramInt;
    this.user = paramString2;
    this.password = paramString3;
    this.database = paramString4;
  }
  
  public void initialize()
  {
    initializePooledDataSource(getMinPoolSizeConfig(), getMaxPoolSizeConfig());
    instance = this;
  }
  
  public static DatabaseManager getInstance()
  {
    return instance;
  }
  
  private int getMinPoolSizeConfig()
  {
    return Main.getInstance().getIntConfig("database.connection-pooling.min-pool-size", 3);
  }
  
  private int getMaxPoolSizeConfig()
  {
    return Main.getInstance().getIntConfig("database.connection-pooling.max-pool-size", 15);
  }
  
  private void initializePooledDataSource(int paramInt1, int paramInt2)
  {
    try
    {
      this.dataSource = new ComboPooledDataSource();
      this.dataSource.setDriverClass("com.mysql.jdbc.Driver");
      this.dataSource.setJdbcUrl("jdbc:mysql://" + this.host + ":" + String.valueOf(this.port) + "/" + this.database);
      this.dataSource.setUser(this.user);
      this.dataSource.setPassword(this.password);
      this.dataSource.setMaxIdleTime(600);
      this.dataSource.setMinPoolSize(paramInt1);
      this.dataSource.setMaxPoolSize(paramInt2);
    }
    catch (Exception localException)
    {
      Main.getInstance().getServer().getConsoleSender().sendMessage(ChatWriter.pluginMessage(ChatColor.RED + "Couldn't create pooled datasource: " + localException.getMessage()));
    }
  }
  
  public Connection getDataSourceConnection()
  {
    try
    {
      return this.dataSource.getConnection();
    }
    catch (SQLException localSQLException)
    {
      Main.getInstance().getServer().getConsoleSender().sendMessage(ChatWriter.pluginMessage(ChatColor.RED + "Couldn't get a pooled connection: " + localSQLException.getMessage()));
    }
    return null;
  }
  
  public static Connection getConnection()
  {
    return instance.getDataSourceConnection();
  }
  
  public void cleanUp()
  {
    if (this.dataSource != null) {
      try
      {
        this.dataSource.setMinPoolSize(0);
        this.dataSource.setInitialPoolSize(0);
        DataSources.destroy(this.dataSource);
      }
      catch (SQLException localSQLException) {}
    }
  }
  
  public void clean(Connection paramConnection)
  {
    try
    {
      if (paramConnection == null) {
        return;
      }
      if (!paramConnection.isClosed()) {
        paramConnection.close();
      }
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
  
  public void cleanStatement(Statement paramStatement)
  {
    try
    {
      if (paramStatement == null) {
        return;
      }
      if (!paramStatement.isClosed()) {
        paramStatement.close();
      }
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
  
  public void cleanResult(ResultSet paramResultSet)
  {
    try
    {
      if (paramResultSet == null) {
        return;
      }
      if (!paramResultSet.isClosed()) {
        paramResultSet.close();
      }
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
  
  public void execute(String... paramVarArgs)
    throws SQLException
  {
    Connection localConnection = null;
    Statement localStatement = null;
    if (paramVarArgs.length == 0) {
      return;
    }
    try
    {
      localConnection = getDataSourceConnection();
      localStatement = localConnection.createStatement();
      if (paramVarArgs.length == 1)
      {
        localStatement.execute(paramVarArgs[0]);
      }
      else
      {
        for (String str : paramVarArgs) {
          localStatement.addBatch(str);
        }
        localStatement.executeBatch();
      }
    }
    finally
    {
      clean(localConnection);
    }
  }
  
  public ResultSet query(String paramString)
  {
    Connection localConnection = null;
    Statement localStatement = null;
    ResultSet localResultSet = null;
    try
    {
      localConnection = getDataSourceConnection();
      localStatement = localConnection.createStatement();
      localResultSet = localStatement.executeQuery(paramString);
      return localResultSet;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
      clean(localConnection);
    }
    return null;
  }
  
  public int getRowCount(ResultSet paramResultSet)
  {
    int i = 0;
    try
    {
      paramResultSet.last();
      i = paramResultSet.getRow();
      paramResultSet.beforeFirst();
      return i;
    }
    catch (Exception localException) {}
    return 0;
  }
  
  public void update(String paramString)
  {
    Connection localConnection = null;
    Statement localStatement = null;
    try
    {
      localConnection = getDataSourceConnection();
      localStatement = localConnection.createStatement();
      localStatement.executeUpdate(paramString);
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    finally
    {
      clean(localConnection);
      cleanStatement(localStatement);
    }
  }
  
  public void insert(String paramString)
  {
    update(paramString);
  }
  
  public void delete(String paramString)
  {
    update(paramString);
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Database.DatabaseManager
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */