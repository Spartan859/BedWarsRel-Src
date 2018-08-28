package io.github.yannici.bedwars.Statistics;

public enum StorageType
{
  DATABASE("database"),  YAML("yaml");
  
  private String name = null;
  
  private StorageType(String paramString)
  {
    this.name = paramString;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public static StorageType getByName(String paramString)
  {
    for (StorageType localStorageType : ) {
      if (localStorageType.getName().equals(paramString)) {
        return localStorageType;
      }
    }
    return YAML;
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Statistics.StorageType
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */