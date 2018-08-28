package io.github.yannici.bedwars.Database;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.METHOD})
public @interface DBGetField
{
  String name();
  
  String dbType();
  
  String defaultValue() default "";
  
  boolean notNull() default true;
  
  boolean autoInc() default false;
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Database.DBGetField
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */