package io.github.yannici.bedwars.Updater;

import io.github.yannici.bedwars.Database.DatabaseManager;
import io.github.yannici.bedwars.Main;
import java.util.Iterator;
import java.util.List;

public class DatabaseUpdater
{
  private List<DatabaseUpdate> updates = null;
  
  public void execute()
  {
    this.updates.add(new DatabaseUpdate("ALTER TABLE `" + DatabaseManager.DBPrefix + "stats_players" + "` ADD `name` VARCHAR(255) NOT NULL FIRST;"));
    this.updates.add(new DatabaseUpdate("ALTER TABLE `" + DatabaseManager.DBPrefix + "stats_players" + "` ADD `kd` DOUBLE NOT NULL;"));
    this.updates.add(new DatabaseUpdate("ALTER TABLE `" + DatabaseManager.DBPrefix + "stats_players" + "` ADD `games` INT(11) NOT NULL;"));
    executeUpdates();
  }
  
  private void executeUpdates()
  {
    Iterator localIterator = this.updates.iterator();
    while (localIterator.hasNext())
    {
      DatabaseUpdate localDatabaseUpdate = (DatabaseUpdate)localIterator.next();
      try
      {
        Main.getInstance().getDatabaseManager().execute(new String[] { localDatabaseUpdate.getSql() });
      }
      catch (Exception localException) {}
    }
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Updater.DatabaseUpdater
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */