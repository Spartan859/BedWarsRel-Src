package io.github.yannici.bedwars.Villager;

import java.util.Comparator;

public class MerchantCategoryComparator
  implements Comparator<MerchantCategory>
{
  public int compare(MerchantCategory paramMerchantCategory1, MerchantCategory paramMerchantCategory2)
  {
    int i = paramMerchantCategory1.getOrder();
    int j = paramMerchantCategory2.getOrder();
    return Integer.valueOf(i).compareTo(Integer.valueOf(j));
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Villager.MerchantCategoryComparator
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */