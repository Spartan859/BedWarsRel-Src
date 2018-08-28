package io.github.yannici.bedwars.Game;

import org.bukkit.scheduler.BukkitRunnable;

public class GameOverTask
  extends BukkitRunnable
{
  private int counter = 10;
  private int counterStart = 10;
  private GameCycle cycle = null;
  private Team winner = null;
  
  public GameOverTask(GameCycle paramGameCycle, int paramInt, Team paramTeam)
  {
    this.counterStart = paramInt;
    this.counter = paramInt;
    this.cycle = paramGameCycle;
    this.winner = paramTeam;
  }
  
  public void run()
  {
    this.cycle.onGameOver(this);
  }
  
  public int getStartCount()
  {
    return this.counterStart;
  }
  
  public Team getWinner()
  {
    return this.winner;
  }
  
  public int getCounter()
  {
    return this.counter;
  }
  
  public void setCounter(int paramInt)
  {
    this.counter = paramInt;
  }
  
  public void decCounter()
  {
    this.counter -= 1;
  }
  
  public GameCycle getCycle()
  {
    return this.cycle;
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Game.GameOverTask
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */