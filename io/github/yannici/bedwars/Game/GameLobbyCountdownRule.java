package io.github.yannici.bedwars.Game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public enum GameLobbyCountdownRule
{
  TEAMS_HAVE_PLAYERS(0),  PLAYERS_IN_GAME(1),  ENOUGH_TEAMS_AND_PLAYERS(2);
  
  private int type = 0;
  
  private GameLobbyCountdownRule(int paramInt)
  {
    this.type = paramInt;
  }
  
  public int getTypeId()
  {
    return this.type;
  }
  
  public boolean isRuleMet(Game paramGame)
  {
    switch (1.$SwitchMap$io$github$yannici$bedwars$Game$GameLobbyCountdownRule[ordinal()])
    {
    case 1: 
      Iterator localIterator1 = paramGame.getTeams().values().iterator();
      while (localIterator1.hasNext())
      {
        Team localTeam1 = (Team)localIterator1.next();
        if (localTeam1.getPlayers().size() == 0) {
          return false;
        }
      }
      break;
    case 2: 
      if (paramGame.getMinPlayers() > paramGame.getPlayers().size()) {
        return false;
      }
      break;
    case 3: 
      int i = 0;
      int j = 0;
      Iterator localIterator2 = paramGame.getTeams().values().iterator();
      while (localIterator2.hasNext())
      {
        Team localTeam2 = (Team)localIterator2.next();
        if (localTeam2.getPlayers().size() > 0) {
          i++;
        } else {
          j++;
        }
      }
      if ((paramGame.getMinPlayers() > paramGame.getPlayers().size()) || ((i == 1) && (j > paramGame.getFreePlayers().size()))) {
        return false;
      }
      break;
    }
    return true;
  }
  
  public static GameLobbyCountdownRule getById(int paramInt)
  {
    for (GameLobbyCountdownRule localGameLobbyCountdownRule : ) {
      if (localGameLobbyCountdownRule.getTypeId() == paramInt) {
        return localGameLobbyCountdownRule;
      }
    }
    return TEAMS_HAVE_PLAYERS;
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Game.GameLobbyCountdownRule
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */