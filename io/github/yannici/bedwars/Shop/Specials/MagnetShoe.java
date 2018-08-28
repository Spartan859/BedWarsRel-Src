package io.github.yannici.bedwars.Shop.Specials;

import io.github.yannici.bedwars.Main;
import io.github.yannici.bedwars.Utils;
import org.bukkit.Material;

public class MagnetShoe
  extends SpecialItem
{
  public Material getItemMaterial()
  {
    String str = Main.getInstance().getStringConfig("specials.magnetshoe.boots", "IRON_BOOTS");
    Material localMaterial = null;
    if (Utils.isNumber(str)) {
      localMaterial = Material.getMaterial(Integer.valueOf(str).intValue());
    } else {
      localMaterial = Material.getMaterial(str);
    }
    if (localMaterial == null) {
      return Material.IRON_BOOTS;
    }
    return localMaterial;
  }
  
  public Material getActivatedMaterial()
  {
    return null;
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Shop.Specials.MagnetShoe
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */