package io.github.yannici.bedwars.Database;

public abstract class DatabaseObject
{
  private long id = 0L;
  
  @DBGetField(name="id", dbType="INT(10) UNSIGNED", autoInc=true)
  public long getId()
  {
    return this.id;
  }
  
  @DBSetField(name="id")
  public void setId(long paramLong)
  {
    this.id = paramLong;
  }
  
  public boolean isNew()
  {
    return this.id == 0L;
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Database.DatabaseObject
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */