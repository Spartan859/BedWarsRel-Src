package io.github.yannici.bedwars.Com.v1_7_R3;

import net.minecraft.server.v1_7_R3.Entity;
import net.minecraft.server.v1_7_R3.EntityCreature;
import net.minecraft.server.v1_7_R3.Navigation;
import net.minecraft.server.v1_7_R3.PathfinderGoalMeleeAttack;

public class PathfinderGoalBedwarsPlayer
  extends PathfinderGoalMeleeAttack
{
  private EntityCreature creature = null;
  
  public PathfinderGoalBedwarsPlayer(EntityCreature paramEntityCreature, Class<? extends Entity> paramClass, double paramDouble, boolean paramBoolean)
  {
    super(paramEntityCreature, paramClass, paramDouble, paramBoolean);
    this.creature = paramEntityCreature;
  }
  
  public PathfinderGoalBedwarsPlayer(EntityCreature paramEntityCreature, double paramDouble, boolean paramBoolean)
  {
    super(paramEntityCreature, paramDouble, paramBoolean);
    this.creature = paramEntityCreature;
  }
  
  public void e()
  {
    this.creature.getNavigation().a(this.creature.getGoalTarget());
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Com.v1_7_R3.PathfinderGoalBedwarsPlayer
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */