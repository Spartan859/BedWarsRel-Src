package io.github.yannici.bedwars.Database;

import java.lang.reflect.Method;

public class DBField
{
  private Method getter = null;
  private Method setter = null;
  
  public DBField()
  {
    this.getter = null;
    this.setter = null;
  }
  
  public DBField(Method paramMethod1, Method paramMethod2)
  {
    this.getter = paramMethod1;
    this.setter = paramMethod2;
  }
  
  public Method getGetter()
  {
    return this.getter;
  }
  
  public Method getSetter()
  {
    return this.setter;
  }
  
  public void setGetter(Method paramMethod)
  {
    this.getter = paramMethod;
  }
  
  public void setSetter(Method paramMethod)
  {
    this.setter = paramMethod;
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Database.DBField
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */