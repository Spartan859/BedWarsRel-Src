package io.github.yannici.bedwars.Game;

import io.github.yannici.bedwars.Main;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.material.Bed;
import org.bukkit.material.Directional;
import org.bukkit.material.Lever;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Redstone;

public class Region
{
  public static final int CHUNK_SIZE = 16;
  private Location minCorner = null;
  private Location maxCorner = null;
  private World world = null;
  private String name = null;
  private List<Block> placedBlocks = null;
  private List<Block> breakedBlocks = null;
  private HashMap<Block, Integer> breakedBlockTypes = null;
  private HashMap<Block, Byte> breakedBlockData = null;
  private List<Block> placedUnbreakableBlocks = null;
  private HashMap<Block, Boolean> breakedBlockPower = null;
  private HashMap<Block, BlockFace> breakedBlockFace = null;
  private List<Entity> removingEntities = null;
  private List<Inventory> inventories = null;
  
  public Region(Location paramLocation1, Location paramLocation2, String paramString)
  {
    if ((paramLocation1 == null) || (paramLocation2 == null)) {
      return;
    }
    if (!paramLocation1.getWorld().getName().equals(paramLocation2.getWorld().getName())) {
      return;
    }
    this.world = paramLocation1.getWorld();
    setMinMax(paramLocation1, paramLocation2);
    this.placedBlocks = new ArrayList();
    this.breakedBlocks = new ArrayList();
    this.breakedBlockTypes = new HashMap();
    this.breakedBlockData = new HashMap();
    this.breakedBlockFace = new HashMap();
    this.placedUnbreakableBlocks = new ArrayList();
    this.breakedBlockPower = new HashMap();
    this.inventories = new ArrayList();
    this.removingEntities = new ArrayList();
    this.name = paramString;
  }
  
  public Region(World paramWorld, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    this(new Location(paramWorld, paramInt1, paramInt2, paramInt3), new Location(paramWorld, paramInt4, paramInt5, paramInt6), paramWorld.getName());
  }
  
  public boolean check()
  {
    return (this.minCorner != null) && (this.maxCorner != null) && (this.world != null);
  }
  
  private void setMinMax(Location paramLocation1, Location paramLocation2)
  {
    this.minCorner = getMinimumCorner(paramLocation1, paramLocation2);
    this.maxCorner = getMaximumCorner(paramLocation1, paramLocation2);
  }
  
  private Location getMinimumCorner(Location paramLocation1, Location paramLocation2)
  {
    return new Location(this.world, Math.min(paramLocation1.getBlockX(), paramLocation2.getBlockX()), Math.min(paramLocation1.getBlockY(), paramLocation2.getBlockY()), Math.min(paramLocation1.getBlockZ(), paramLocation2.getBlockZ()));
  }
  
  private Location getMaximumCorner(Location paramLocation1, Location paramLocation2)
  {
    return new Location(this.world, Math.max(paramLocation1.getBlockX(), paramLocation2.getBlockX()), Math.max(paramLocation1.getBlockY(), paramLocation2.getBlockY()), Math.max(paramLocation1.getBlockZ(), paramLocation2.getBlockZ()));
  }
  
  public List<Inventory> getInventories()
  {
    return this.inventories;
  }
  
  public void addInventory(Inventory paramInventory)
  {
    this.inventories.add(paramInventory);
  }
  
  public boolean isInRegion(Location paramLocation)
  {
    if (!paramLocation.getWorld().equals(this.world)) {
      return false;
    }
    return (paramLocation.getBlockX() >= this.minCorner.getBlockX()) && (paramLocation.getBlockX() <= this.maxCorner.getBlockX()) && (paramLocation.getBlockY() >= this.minCorner.getBlockY()) && (paramLocation.getBlockY() <= this.maxCorner.getBlockY()) && (paramLocation.getBlockZ() >= this.minCorner.getBlockZ()) && (paramLocation.getBlockZ() <= this.maxCorner.getBlockZ());
  }
  
  public boolean chunkIsInRegion(Chunk paramChunk)
  {
    return (paramChunk.getX() >= this.minCorner.getX()) && (paramChunk.getX() <= this.maxCorner.getX()) && (paramChunk.getZ() >= this.minCorner.getZ()) && (paramChunk.getZ() <= this.maxCorner.getZ());
  }
  
  public boolean chunkIsInRegion(double paramDouble1, double paramDouble2)
  {
    return (paramDouble1 >= this.minCorner.getX()) && (paramDouble1 <= this.maxCorner.getX()) && (paramDouble2 >= this.minCorner.getZ()) && (paramDouble2 <= this.maxCorner.getZ());
  }
  
  public void addRemovingEntity(Entity paramEntity)
  {
    this.removingEntities.add(paramEntity);
  }
  
  public void removeRemovingEntity(Entity paramEntity)
  {
    this.removingEntities.remove(paramEntity);
  }
  
  public void reset(Game paramGame)
  {
    loadChunks();
    Object localObject1 = this.inventories.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (Inventory)((Iterator)localObject1).next();
      ((Inventory)localObject2).clear();
    }
    localObject1 = this.placedBlocks.iterator();
    Object localObject3;
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (Block)((Iterator)localObject1).next();
      localObject3 = this.world.getBlockAt(((Block)localObject2).getLocation());
      if (((Block)localObject3).getType() != Material.AIR) {
        if (localObject3.equals(localObject2)) {
          ((Block)localObject3).setType(Material.AIR);
        }
      }
    }
    this.placedBlocks.clear();
    localObject1 = this.placedUnbreakableBlocks.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (Block)((Iterator)localObject1).next();
      localObject3 = this.world.getBlockAt(((Block)localObject2).getLocation());
      if (((Block)localObject3).getType() != Material.AIR) {
        if (((Block)localObject3).getLocation().equals(((Block)localObject2).getLocation())) {
          ((Block)localObject3).setType(Material.AIR);
        }
      }
    }
    this.placedUnbreakableBlocks.clear();
    localObject1 = this.breakedBlocks.iterator();
    Object localObject4;
    Object localObject5;
    BlockState localBlockState1;
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (Block)((Iterator)localObject1).next();
      localObject3 = getWorld().getBlockAt(((Block)localObject2).getLocation());
      ((Block)localObject3).setTypeId(((Integer)this.breakedBlockTypes.get(localObject2)).intValue());
      ((Block)localObject3).setData(((Byte)this.breakedBlockData.get(localObject2)).byteValue());
      if (this.breakedBlockFace.containsKey(localObject3))
      {
        localObject4 = ((Block)localObject3).getState().getData();
        if ((localObject4 instanceof Directional))
        {
          ((Directional)localObject4).setFacingDirection((BlockFace)this.breakedBlockFace.get(localObject2));
          ((Block)localObject3).getState().setData((MaterialData)localObject4);
        }
      }
      if ((((Block)localObject3).getState().getData() instanceof Lever))
      {
        localObject4 = (Lever)((Block)localObject3).getState().getData();
        localObject5 = ((Block)localObject3).getState();
        localBlockState1 = ((Block)localObject3).getState();
        ((Lever)localObject4).setPowered(((Boolean)this.breakedBlockPower.get(localObject2)).booleanValue());
        ((Block)localObject3).getState().setData((MaterialData)localObject4);
        ((BlockState)localObject5).setType(Material.AIR);
        ((BlockState)localObject5).update(true, false);
        localBlockState1.update(true);
      }
      else
      {
        ((Block)localObject3).getState().update(true, true);
      }
    }
    this.breakedBlocks.clear();
    localObject1 = paramGame.getTargetMaterial();
    Object localObject2 = paramGame.getTeams().values().iterator();
    while (((Iterator)localObject2).hasNext())
    {
      localObject3 = (Team)((Iterator)localObject2).next();
      if (((Team)localObject3).getHeadTarget() != null) {
        if (((((Material)localObject1).equals(Material.BED_BLOCK)) || (((Material)localObject1).equals(Material.BED))) && (((Team)localObject3).getFeetTarget() != null))
        {
          localObject4 = this.world.getBlockAt(((Team)localObject3).getHeadTarget().getLocation());
          localObject5 = this.world.getBlockAt(((Team)localObject3).getFeetTarget().getLocation());
          localBlockState1 = ((Block)localObject4).getState();
          BlockState localBlockState2 = ((Block)localObject5).getState();
          localBlockState1.setType(Material.BED_BLOCK);
          localBlockState2.setType(Material.BED_BLOCK);
          localBlockState1.setRawData((byte)0);
          localBlockState2.setRawData((byte)8);
          localBlockState2.update(true, false);
          localBlockState1.update(true, false);
          Bed localBed1 = (Bed)localBlockState1.getData();
          localBed1.setHeadOfBed(true);
          localBed1.setFacingDirection(((Block)localObject4).getFace((Block)localObject5).getOppositeFace());
          Bed localBed2 = (Bed)localBlockState2.getData();
          localBed2.setHeadOfBed(false);
          localBed2.setFacingDirection(((Block)localObject5).getFace((Block)localObject4));
          localBlockState2.update(true, false);
          localBlockState1.update(true, true);
        }
        else
        {
          localObject4 = this.world.getBlockAt(((Team)localObject3).getHeadTarget().getLocation());
          localObject5 = ((Block)localObject4).getState();
          ((BlockState)localObject5).setType((Material)localObject1);
          ((BlockState)localObject5).update(true, true);
        }
      }
    }
    localObject2 = paramGame.getRessourceSpawner().iterator();
    while (((Iterator)localObject2).hasNext())
    {
      localObject3 = (RessourceSpawner)((Iterator)localObject2).next();
      ((RessourceSpawner)localObject3).getLocation().getChunk().load();
    }
    localObject2 = this.removingEntities.iterator();
    while (((Iterator)localObject2).hasNext())
    {
      localObject3 = (Entity)((Iterator)localObject2).next();
      ((Entity)localObject3).remove();
    }
    localObject2 = this.world.getEntities().iterator();
    while (((Iterator)localObject2).hasNext())
    {
      localObject3 = (Entity)((Iterator)localObject2).next();
      if ((!this.removingEntities.contains(localObject3)) && (isInRegion(((Entity)localObject3).getLocation()))) {
        if ((localObject3 instanceof Item))
        {
          ((Entity)localObject3).remove();
        }
        else if ((((Entity)localObject3).getType().equals(EntityType.CREEPER)) || (((Entity)localObject3).getType().equals(EntityType.CAVE_SPIDER)) || (((Entity)localObject3).getType().equals(EntityType.SPIDER)) || (((Entity)localObject3).getType().equals(EntityType.ZOMBIE)) || (((Entity)localObject3).getType().equals(EntityType.SKELETON)) || (((Entity)localObject3).getType().equals(EntityType.SILVERFISH)) || (((Entity)localObject3).getType().equals(EntityType.ARROW)))
        {
          ((Entity)localObject3).remove();
        }
        else if ((localObject3 instanceof LivingEntity))
        {
          localObject4 = (LivingEntity)localObject3;
          ((LivingEntity)localObject4).setRemoveWhenFarAway(false);
        }
      }
    }
    this.removingEntities.clear();
  }
  
  public World getWorld()
  {
    return this.minCorner.getWorld();
  }
  
  public boolean isPlacedBlock(Block paramBlock)
  {
    return this.placedBlocks.contains(paramBlock);
  }
  
  public void addPlacedBlock(Block paramBlock, BlockState paramBlockState)
  {
    this.placedBlocks.add(paramBlock);
    if (paramBlockState != null)
    {
      if ((paramBlockState.getData() instanceof Directional)) {
        this.breakedBlockFace.put(paramBlockState.getBlock(), ((Directional)paramBlockState.getData()).getFacing());
      }
      this.breakedBlockTypes.put(paramBlockState.getBlock(), Integer.valueOf(paramBlockState.getTypeId()));
      this.breakedBlockData.put(paramBlockState.getBlock(), Byte.valueOf(paramBlockState.getData().getData()));
      this.breakedBlocks.add(paramBlockState.getBlock());
    }
  }
  
  public void removePlacedBlock(Block paramBlock)
  {
    this.placedBlocks.remove(paramBlock);
  }
  
  public String getName()
  {
    if (this.name == null) {
      this.name = this.world.getName();
    }
    return this.name;
  }
  
  public void addBreakedBlock(Block paramBlock)
  {
    if ((paramBlock.getState().getData() instanceof Directional)) {
      this.breakedBlockFace.put(paramBlock, ((Directional)paramBlock.getState().getData()).getFacing());
    }
    this.breakedBlockTypes.put(paramBlock, Integer.valueOf(paramBlock.getTypeId()));
    this.breakedBlockData.put(paramBlock, Byte.valueOf(paramBlock.getData()));
    if ((paramBlock.getState().getData() instanceof Redstone)) {
      this.breakedBlockPower.put(paramBlock, Boolean.valueOf(((Redstone)paramBlock.getState().getData()).isPowered()));
    }
    this.breakedBlocks.add(paramBlock);
  }
  
  public void addPlacedUnbreakableBlock(Block paramBlock, BlockState paramBlockState)
  {
    this.placedUnbreakableBlocks.add(paramBlock);
    if (paramBlockState != null)
    {
      if ((paramBlockState.getData() instanceof Directional)) {
        this.breakedBlockFace.put(paramBlockState.getBlock(), ((Directional)paramBlockState.getData()).getFacing());
      }
      this.breakedBlockTypes.put(paramBlockState.getBlock(), Integer.valueOf(paramBlockState.getTypeId()));
      this.breakedBlockData.put(paramBlockState.getBlock(), Byte.valueOf(paramBlockState.getData().getData()));
      this.breakedBlocks.add(paramBlockState.getBlock());
      if ((paramBlockState.getData() instanceof Redstone)) {
        this.breakedBlockPower.put(paramBlock, Boolean.valueOf(((Redstone)paramBlockState.getData()).isPowered()));
      }
    }
  }
  
  public void removePlacedUnbreakableBlock(Block paramBlock)
  {
    this.placedUnbreakableBlocks.remove(paramBlock);
  }
  
  public void loadChunks()
  {
    int i = (int)Math.floor(this.minCorner.getX());
    int j = (int)Math.ceil(this.maxCorner.getX());
    int k = (int)Math.floor(this.minCorner.getZ());
    int m = (int)Math.ceil(this.maxCorner.getZ());
    for (int n = i; n <= j; n += 16) {
      for (int i1 = k; i1 <= m; i1 += 16)
      {
        Chunk localChunk = this.world.getChunkAt(n, i1);
        if (!localChunk.isLoaded()) {
          localChunk.load();
        }
      }
    }
  }
  
  public boolean isPlacedUnbreakableBlock(Block paramBlock)
  {
    return this.placedUnbreakableBlocks.contains(paramBlock);
  }
  
  public void setVillagerNametag()
  {
    Iterator localIterator = this.world.getEntities().iterator();
    while (localIterator.hasNext())
    {
      Entity localEntity = (Entity)localIterator.next();
      if (isInRegion(localEntity.getLocation())) {
        if (localEntity.getType() == EntityType.VILLAGER)
        {
          LivingEntity localLivingEntity = (LivingEntity)localEntity;
          localLivingEntity.setCustomNameVisible(false);
          localLivingEntity.setCustomName(Main._l("ingame.shop.name"));
        }
      }
    }
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Game.Region
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */