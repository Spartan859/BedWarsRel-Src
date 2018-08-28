package io.github.yannici.bedwars.Statistics;

import io.github.yannici.bedwars.ChatWriter;
import io.github.yannici.bedwars.Database.DBField;
import io.github.yannici.bedwars.Database.DBGetField;
import io.github.yannici.bedwars.Database.DBSetField;
import io.github.yannici.bedwars.Database.DatabaseObject;
import io.github.yannici.bedwars.Main;
import java.lang.reflect.Method;
import java.util.Map;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;

public abstract class StoringTable
  extends DatabaseObject
{
  private Map<String, DBField> fields = null;
  
  public StoringTable()
  {
    loadFields();
  }
  
  private void loadFields()
  {
    this.fields.clear();
    for (Method localMethod : getClass().getMethods())
    {
      DBGetField localDBGetField = (DBGetField)localMethod.getAnnotation(DBGetField.class);
      DBSetField localDBSetField = (DBSetField)localMethod.getAnnotation(DBSetField.class);
      if ((localDBGetField != null) || (localDBSetField != null))
      {
        String str = localDBGetField != null ? localDBGetField.name() : localDBSetField.name();
        DBField localDBField;
        if (this.fields.containsKey(str))
        {
          localDBField = (DBField)this.fields.get(str);
          if (localDBGetField == null) {
            localDBField.setSetter(localMethod);
          } else {
            localDBField.setGetter(localMethod);
          }
        }
        else
        {
          localDBField = new DBField();
          if (localDBGetField == null) {
            localDBField.setSetter(localMethod);
          } else {
            localDBField.setGetter(localMethod);
          }
          this.fields.put(str, localDBField);
        }
      }
    }
  }
  
  public Map<String, DBField> getFields()
  {
    return this.fields;
  }
  
  public abstract String getTableName();
  
  public abstract String getKeyField();
  
  public abstract void load();
  
  public abstract void store();
  
  public abstract void setDefault();
  
  public Object getValue(String paramString)
  {
    try
    {
      Method localMethod = ((DBField)this.fields.get(paramString)).getGetter();
      localMethod.setAccessible(true);
      return localMethod.invoke(this, new Object[0]);
    }
    catch (Exception localException)
    {
      Main.getInstance().getServer().getConsoleSender().sendMessage(ChatWriter.pluginMessage("Couldn't fetch value of field: " + paramString));
    }
    return null;
  }
  
  public void setValue(String paramString, Object paramObject)
  {
    try
    {
      Method localMethod1 = ((DBField)this.fields.get(paramString)).getSetter();
      if (localMethod1 == null) {
        return;
      }
      localMethod1.setAccessible(true);
      Class localClass1 = localMethod1.getParameterTypes()[0];
      try
      {
        if ((paramObject instanceof Number))
        {
          String str = paramObject.getClass().getSimpleName().toLowerCase();
          if (paramObject.getClass().equals(Integer.class)) {
            str = "int";
          }
          Method localMethod2 = paramObject.getClass().getMethod(str + "Value", new Class[0]);
          localMethod2.setAccessible(true);
          Class localClass2 = localMethod2.getReturnType();
          if (!localClass1.equals(localClass2))
          {
            localMethod1.invoke(this, new Object[] { localClass1.cast(localMethod2.invoke(paramObject, new Object[0])) });
            return;
          }
          localMethod1.invoke(this, new Object[] { localMethod2.invoke(paramObject, new Object[0]) });
          return;
        }
        paramObject = localClass1.cast(paramObject);
        localMethod1.invoke(this, new Object[] { paramObject });
      }
      catch (Exception localException2)
      {
        Main.getInstance().getServer().getConsoleSender().sendMessage(ChatWriter.pluginMessage("Couldn't cast value for field: " + paramString));
      }
    }
    catch (Exception localException1)
    {
      Main.getInstance().getServer().getConsoleSender().sendMessage(ChatWriter.pluginMessage("Couldn't set value of field: " + paramString));
    }
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Statistics.StoringTable
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */