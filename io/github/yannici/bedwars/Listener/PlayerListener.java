package io.github.yannici.bedwars.Listener;

import io.github.yannici.bedwars.ChatWriter;
import io.github.yannici.bedwars.Events.BedwarsOpenShopEvent;
import io.github.yannici.bedwars.Game.BungeeGameCycle;
import io.github.yannici.bedwars.Game.Game;
import io.github.yannici.bedwars.Game.GameCycle;
import io.github.yannici.bedwars.Game.GameLobbyCountdownRule;
import io.github.yannici.bedwars.Game.GameManager;
import io.github.yannici.bedwars.Game.GameState;
import io.github.yannici.bedwars.Game.PlayerSettings;
import io.github.yannici.bedwars.Game.PlayerStorage;
import io.github.yannici.bedwars.Game.Region;
import io.github.yannici.bedwars.Game.Team;
import io.github.yannici.bedwars.HolographicDisplaysInteraction;
import io.github.yannici.bedwars.Main;
import io.github.yannici.bedwars.Shop.NewItemShop;
import io.github.yannici.bedwars.Utils;
import io.github.yannici.bedwars.Villager.MerchantCategory;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Player.Spigot;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerListener
  extends BaseListener
{
  @EventHandler(priority=EventPriority.HIGHEST)
  public void onJoin(PlayerJoinEvent paramPlayerJoinEvent)
  {
    if (Main.getInstance().isBungee())
    {
      paramPlayerJoinEvent.setJoinMessage("");
      ArrayList localArrayList = Main.getInstance().getGameManager().getGames();
      if (localArrayList.size() == 0) {
        return;
      }
      final Player localPlayer = paramPlayerJoinEvent.getPlayer();
      final Game localGame = (Game)localArrayList.get(0);
      if ((localGame.getState() == GameState.STOPPED) && (localPlayer.hasPermission("bw.setup"))) {
        return;
      }
      if (!localGame.playerJoins(localPlayer)) {
        new BukkitRunnable()
        {
          public void run()
          {
            if ((localGame.getCycle() instanceof BungeeGameCycle)) {
              ((BungeeGameCycle)localGame.getCycle()).bungeeSendToServer(Main.getInstance().getBungeeHub(), localPlayer, true);
            }
          }
        }.runTaskLater(Main.getInstance(), 5L);
      }
    }
    if ((Main.getInstance().isHologramsEnabled()) && (Main.getInstance().getHolographicInteractor() != null)) {
      Main.getInstance().getHolographicInteractor().updateHolograms(paramPlayerJoinEvent.getPlayer(), 60L);
    }
  }
  
  @EventHandler
  public void onSwitchWorld(PlayerChangedWorldEvent paramPlayerChangedWorldEvent)
  {
    Game localGame = Main.getInstance().getGameManager().getGameOfPlayer(paramPlayerChangedWorldEvent.getPlayer());
    if (localGame != null) {
      if (localGame.getState() == GameState.RUNNING)
      {
        if (!localGame.getCycle().isEndGameRunning()) {
          if (!localGame.getPlayerSettings(paramPlayerChangedWorldEvent.getPlayer()).isTeleporting()) {
            localGame.playerLeave(paramPlayerChangedWorldEvent.getPlayer(), false);
          } else {
            localGame.getPlayerSettings(paramPlayerChangedWorldEvent.getPlayer()).setTeleporting(false);
          }
        }
      }
      else if (localGame.getState() == GameState.WAITING) {
        if (!localGame.getPlayerSettings(paramPlayerChangedWorldEvent.getPlayer()).isTeleporting()) {
          localGame.playerLeave(paramPlayerChangedWorldEvent.getPlayer(), false);
        } else {
          localGame.getPlayerSettings(paramPlayerChangedWorldEvent.getPlayer()).setTeleporting(false);
        }
      }
    }
    if ((!Main.getInstance().isHologramsEnabled()) || (Main.getInstance().getHolographicInteractor() == null)) {
      return;
    }
    Main.getInstance().getHolographicInteractor().updateHolograms(paramPlayerChangedWorldEvent.getPlayer());
  }
  
  private void inGameInteractEntity(PlayerInteractEntityEvent paramPlayerInteractEntityEvent, Game paramGame, Player paramPlayer)
  {
    if ((paramPlayerInteractEntityEvent.getPlayer().getItemInHand().getType().equals(Material.MONSTER_EGG)) || (paramPlayerInteractEntityEvent.getPlayer().getItemInHand().getType().equals(Material.MONSTER_EGGS)) || (paramPlayerInteractEntityEvent.getPlayer().getItemInHand().getType().equals(Material.DRAGON_EGG)))
    {
      paramPlayerInteractEntityEvent.setCancelled(true);
      return;
    }
    if ((paramPlayerInteractEntityEvent.getRightClicked() != null) && (!paramPlayerInteractEntityEvent.getRightClicked().getType().equals(EntityType.VILLAGER)))
    {
      localObject = Arrays.asList(new EntityType[] { EntityType.ITEM_FRAME });
      try
      {
        ((List)localObject).add(EntityType.valueOf("ARMOR_STAND"));
      }
      catch (Exception localException) {}
      if (((List)localObject).contains(paramPlayerInteractEntityEvent.getRightClicked().getType())) {
        paramPlayerInteractEntityEvent.setCancelled(true);
      }
      return;
    }
    paramPlayerInteractEntityEvent.setCancelled(true);
    if (paramGame.isSpectator(paramPlayer)) {
      return;
    }
    Object localObject = new BedwarsOpenShopEvent(paramGame, paramPlayer, paramGame.getItemShopCategories(), paramPlayerInteractEntityEvent.getRightClicked());
    Main.getInstance().getServer().getPluginManager().callEvent((Event)localObject);
    if (((BedwarsOpenShopEvent)localObject).isCancelled()) {
      return;
    }
    if (paramGame.isUsingOldShop(paramPlayer))
    {
      MerchantCategory.openCategorySelection(paramPlayer, paramGame);
    }
    else
    {
      NewItemShop localNewItemShop = paramGame.getNewItemShop(paramPlayer);
      if (localNewItemShop == null) {
        localNewItemShop = paramGame.openNewItemShop(paramPlayer);
      }
      localNewItemShop.setCurrentCategory(null);
      localNewItemShop.openCategoryInventory(paramPlayer);
    }
  }
  
  @EventHandler
  public void openInventory(InventoryOpenEvent paramInventoryOpenEvent)
  {
    if (!(paramInventoryOpenEvent.getPlayer() instanceof Player)) {
      return;
    }
    Player localPlayer = (Player)paramInventoryOpenEvent.getPlayer();
    Game localGame = Main.getInstance().getGameManager().getGameOfPlayer(localPlayer);
    if (localGame == null) {
      return;
    }
    if (localGame.getState() != GameState.RUNNING) {
      return;
    }
    if ((paramInventoryOpenEvent.getInventory().getType() == InventoryType.ENCHANTING) || (paramInventoryOpenEvent.getInventory().getType() == InventoryType.BREWING) || ((paramInventoryOpenEvent.getInventory().getType() == InventoryType.CRAFTING) && (!Main.getInstance().getBooleanConfig("allow-crafting", false))))
    {
      paramInventoryOpenEvent.setCancelled(true);
      return;
    }
    if ((paramInventoryOpenEvent.getInventory().getType() == InventoryType.CRAFTING) && (Main.getInstance().getBooleanConfig("allow-crafting", false))) {
      return;
    }
    if (localGame.isSpectator(localPlayer))
    {
      if (paramInventoryOpenEvent.getInventory().getName().equals(Main._l("ingame.spectator"))) {
        return;
      }
      paramInventoryOpenEvent.setCancelled(true);
    }
    if (paramInventoryOpenEvent.getInventory().getHolder() == null) {
      return;
    }
    if (localGame.getRegion().getInventories().contains(paramInventoryOpenEvent.getInventory())) {
      return;
    }
    InventoryHolder localInventoryHolder = paramInventoryOpenEvent.getInventory().getHolder();
    for (Class localClass1 : localInventoryHolder.getClass().getInterfaces())
    {
      if (localClass1.equals(BlockState.class))
      {
        localGame.getRegion().addInventory(paramInventoryOpenEvent.getInventory());
        return;
      }
      for (Class localClass2 : localClass1.getInterfaces()) {
        if (localClass2.equals(BlockState.class))
        {
          localGame.getRegion().addInventory(paramInventoryOpenEvent.getInventory());
          return;
        }
      }
    }
  }
  
  @EventHandler
  public void onCraft(CraftItemEvent paramCraftItemEvent)
  {
    Player localPlayer = (Player)paramCraftItemEvent.getWhoClicked();
    Game localGame = Main.getInstance().getGameManager().getGameOfPlayer(localPlayer);
    if (localGame == null) {
      return;
    }
    if (localGame.getState() == GameState.STOPPED) {
      return;
    }
    if (Main.getInstance().getBooleanConfig("allow-crafting", false)) {
      return;
    }
    paramCraftItemEvent.setCancelled(true);
  }
  
  @EventHandler(priority=EventPriority.HIGHEST)
  public void onPlayerRespawn(PlayerRespawnEvent paramPlayerRespawnEvent)
  {
    Player localPlayer = paramPlayerRespawnEvent.getPlayer();
    Game localGame = Main.getInstance().getGameManager().getGameOfPlayer(localPlayer);
    if (localGame == null) {
      return;
    }
    if (localGame.getState() == GameState.RUNNING)
    {
      localGame.getCycle().onPlayerRespawn(paramPlayerRespawnEvent, localPlayer);
      return;
    }
    if (localGame.getState() == GameState.WAITING) {
      paramPlayerRespawnEvent.setRespawnLocation(localGame.getLobby());
    }
  }
  
  @EventHandler(priority=EventPriority.HIGHEST)
  public void onPlayerDie(PlayerDeathEvent paramPlayerDeathEvent)
  {
    final Player localPlayer1 = paramPlayerDeathEvent.getEntity();
    Game localGame = Main.getInstance().getGameManager().getGameOfPlayer(localPlayer1);
    if (localGame == null) {
      return;
    }
    if (localGame.getState() == GameState.RUNNING)
    {
      paramPlayerDeathEvent.setDroppedExp(0);
      paramPlayerDeathEvent.setDeathMessage(null);
      if (!Main.getInstance().getBooleanConfig("player-drops", false)) {
        paramPlayerDeathEvent.getDrops().clear();
      }
      try
      {
        if (!Main.getInstance().isSpigot())
        {
          Class localClass = null;
          try
          {
            localClass = Class.forName(new StringBuilder().append("io.github.yannici.bedwars.Com.").append(Main.getInstance().getCurrentVersion()).append(".PerformRespawnRunnable").toString());
          }
          catch (ClassNotFoundException localClassNotFoundException)
          {
            localClass = Class.forName("io.github.yannici.bedwars.Com.Fallback.PerformRespawnRunnable");
          }
          BukkitRunnable localBukkitRunnable = (BukkitRunnable)localClass.getDeclaredConstructor(new Class[] { Player.class }).newInstance(new Object[] { localPlayer1 });
          localBukkitRunnable.runTaskLater(Main.getInstance(), 20L);
        }
        else
        {
          new BukkitRunnable()
          {
            public void run()
            {
              localPlayer1.spigot().respawn();
            }
          }.runTaskLater(Main.getInstance(), 20L);
        }
      }
      catch (Exception localException1)
      {
        localException1.printStackTrace();
      }
      try
      {
        paramPlayerDeathEvent.getClass().getMethod("setKeepInventory", new Class[] { Boolean.TYPE });
        paramPlayerDeathEvent.setKeepInventory(false);
      }
      catch (Exception localException2)
      {
        localPlayer1.getInventory().clear();
      }
      Player localPlayer2 = localPlayer1.getKiller();
      if (localPlayer2 == null) {
        localPlayer2 = localGame.getPlayerDamager(localPlayer1);
      }
      localGame.getCycle().onPlayerDies(localPlayer1, localPlayer2);
    }
  }
  
  @EventHandler
  public void onInventoryClick(InventoryClickEvent paramInventoryClickEvent)
  {
    Player localPlayer = (Player)paramInventoryClickEvent.getWhoClicked();
    Game localGame = Main.getInstance().getGameManager().getGameOfPlayer(localPlayer);
    if (localGame == null) {
      return;
    }
    if (localGame.getState() == GameState.WAITING) {
      onLobbyInventoryClick(paramInventoryClickEvent, localPlayer, localGame);
    }
    if (localGame.getState() == GameState.RUNNING) {
      onIngameInventoryClick(paramInventoryClickEvent, localPlayer, localGame);
    }
  }
  
  private void onIngameInventoryClick(InventoryClickEvent paramInventoryClickEvent, Player paramPlayer, Game paramGame)
  {
    Object localObject1;
    Object localObject2;
    if (!paramInventoryClickEvent.getInventory().getName().equals(Main._l("ingame.shop.name")))
    {
      if ((paramGame.isSpectator(paramPlayer)) || (((paramGame.getCycle() instanceof BungeeGameCycle)) && (paramGame.getCycle().isEndGameRunning()) && (Main.getInstance().getBooleanConfig("bungeecord.endgame-in-lobby", true))))
      {
        localItemStack = paramInventoryClickEvent.getCurrentItem();
        if (localItemStack == null) {
          return;
        }
        if (paramInventoryClickEvent.getInventory().getName().equals(Main._l("ingame.spectator")))
        {
          paramInventoryClickEvent.setCancelled(true);
          if (!localItemStack.getType().equals(Material.SKULL_ITEM)) {
            return;
          }
          localObject1 = (SkullMeta)localItemStack.getItemMeta();
          localObject2 = Main.getInstance().getServer().getPlayer(((SkullMeta)localObject1).getOwner());
          if (localObject2 == null) {
            return;
          }
          if (!paramGame.isInGame((Player)localObject2)) {
            return;
          }
          paramPlayer.teleport((Entity)localObject2);
          paramPlayer.closeInventory();
          return;
        }
        localObject1 = paramInventoryClickEvent.getCurrentItem().getType();
        if (((Material)localObject1).equals(Material.SLIME_BALL)) {
          paramGame.playerLeave(paramPlayer, false);
        }
        if (((Material)localObject1).equals(Material.COMPASS)) {
          paramGame.openSpectatorCompass(paramPlayer);
        }
      }
      return;
    }
    paramInventoryClickEvent.setCancelled(true);
    ItemStack localItemStack = paramInventoryClickEvent.getCurrentItem();
    if (localItemStack == null) {
      return;
    }
    if (paramGame.isUsingOldShop(paramPlayer)) {
      try
      {
        if (localItemStack.getType() == Material.SNOW_BALL)
        {
          paramGame.notUseOldShop(paramPlayer);
          localObject1 = paramGame.openNewItemShop(paramPlayer);
          ((NewItemShop)localObject1).setCurrentCategory(null);
          ((NewItemShop)localObject1).openCategoryInventory(paramPlayer);
          return;
        }
        localObject1 = (MerchantCategory)paramGame.getItemShopCategories().get(localItemStack.getType());
        if (localObject1 == null) {
          return;
        }
        localObject2 = Class.forName(new StringBuilder().append("io.github.yannici.bedwars.Com.").append(Main.getInstance().getCurrentVersion()).append(".VillagerItemShop").toString());
        Object localObject3 = ((Class)localObject2).getDeclaredConstructor(new Class[] { Game.class, Player.class, MerchantCategory.class }).newInstance(new Object[] { paramGame, paramPlayer, localObject1 });
        Method localMethod = ((Class)localObject2).getDeclaredMethod("openTrading", new Class[0]);
        localMethod.invoke(localObject3, new Object[0]);
      }
      catch (Exception localException)
      {
        localException.printStackTrace();
      }
    } else {
      paramGame.getNewItemShop(paramPlayer).handleInventoryClick(paramInventoryClickEvent, paramGame, paramPlayer);
    }
  }
  
  private String getChatFormat(String paramString, Team paramTeam, boolean paramBoolean1, boolean paramBoolean2)
  {
    String str = paramString;
    if (paramBoolean2) {
      str = str.replace("$all$", new StringBuilder().append(Main._l("ingame.all")).append(ChatColor.RESET).toString());
    }
    str = str.replace("$player$", new StringBuilder().append((!paramBoolean1) && (paramTeam != null) ? paramTeam.getChatColor() : "").append("%1$s").append(ChatColor.RESET).toString());
    str = str.replace("$msg$", "%2$s");
    if (paramBoolean1) {
      str = str.replace("$team$", Main._l("ingame.spectator"));
    } else if (paramTeam != null) {
      str = str.replace("$team$", new StringBuilder().append(paramTeam.getDisplayName()).append(ChatColor.RESET).toString());
    }
    return ChatColor.translateAlternateColorCodes('&', str);
  }
  
  @EventHandler(priority=EventPriority.HIGHEST)
  public void onChat(AsyncPlayerChatEvent paramAsyncPlayerChatEvent)
  {
    if (paramAsyncPlayerChatEvent.isCancelled()) {
      return;
    }
    Player localPlayer1 = paramAsyncPlayerChatEvent.getPlayer();
    Game localGame = Main.getInstance().getGameManager().getGameOfPlayer(localPlayer1);
    if (localGame == null)
    {
      boolean bool1 = Main.getInstance().getBooleanConfig("seperate-game-chat", true);
      if (!bool1) {
        return;
      }
      localObject1 = paramAsyncPlayerChatEvent.getRecipients().iterator();
      while (((Iterator)localObject1).hasNext())
      {
        Player localPlayer2 = (Player)((Iterator)localObject1).next();
        localObject2 = Main.getInstance().getGameManager().getGameOfPlayer(localPlayer2);
        if (localObject2 != null) {
          ((Iterator)localObject1).remove();
        }
      }
      return;
    }
    if (localGame.getState() == GameState.STOPPED) {
      return;
    }
    Team localTeam = localGame.getPlayerTeam(localPlayer1);
    Object localObject1 = paramAsyncPlayerChatEvent.getMessage();
    boolean bool2 = localGame.isSpectator(localPlayer1);
    if (Main.getInstance().getBooleanConfig("overwrite-names", false)) {
      if ((localTeam == null) || (bool2))
      {
        localPlayer1.setDisplayName(ChatColor.stripColor(localPlayer1.getName()));
        localPlayer1.setPlayerListName(ChatColor.stripColor(localPlayer1.getName()));
      }
      else
      {
        localPlayer1.setDisplayName(new StringBuilder().append(localTeam.getChatColor()).append(ChatColor.stripColor(localPlayer1.getName())).toString());
        localPlayer1.setPlayerListName(new StringBuilder().append(localTeam.getChatColor()).append(ChatColor.stripColor(localPlayer1.getName())).toString());
      }
    }
    if ((Main.getInstance().getBooleanConfig("teamname-on-tab", false)) && (Utils.isSupportingTitles())) {
      if ((localTeam == null) || (bool2)) {
        localPlayer1.setPlayerListName(ChatColor.stripColor(localPlayer1.getDisplayName()));
      } else {
        localPlayer1.setPlayerListName(new StringBuilder().append(localTeam.getChatColor()).append(localTeam.getName()).append(ChatColor.WHITE).append(" | ").append(localTeam.getChatColor()).append(ChatColor.stripColor(localPlayer1.getDisplayName())).toString());
      }
    }
    Object localObject3;
    if ((localGame.getState() != GameState.RUNNING) && (localGame.getState() == GameState.WAITING))
    {
      localObject2 = null;
      if (localTeam == null) {
        localObject2 = getChatFormat(Main.getInstance().getStringConfig("lobby-chatformat", "$player$: $msg$"), null, false, true);
      } else {
        localObject2 = getChatFormat(Main.getInstance().getStringConfig("ingame-chatformat", "<$team$>$player$: $msg$"), localTeam, false, true);
      }
      paramAsyncPlayerChatEvent.setFormat((String)localObject2);
      if (!Main.getInstance().getBooleanConfig("seperate-game-chat", true)) {
        return;
      }
      Iterator localIterator1 = paramAsyncPlayerChatEvent.getRecipients().iterator();
      while (localIterator1.hasNext())
      {
        localObject3 = (Player)localIterator1.next();
        if (!localGame.isInGame((Player)localObject3)) {
          localIterator1.remove();
        }
      }
      return;
    }
    Object localObject2 = Main.getInstance().getConfig().getString("chat-to-all-prefix", "@");
    if ((((String)localObject1).trim().startsWith((String)localObject2)) || (bool2) || ((localGame.getCycle().isEndGameRunning()) && (Main.getInstance().getBooleanConfig("global-chat-after-end", true))))
    {
      boolean bool3 = Main.getInstance().getBooleanConfig("seperate-spectator-chat", false);
      localObject1 = ((String)localObject1).trim();
      localObject3 = null;
      if ((!bool2) && ((!localGame.getCycle().isEndGameRunning()) || (!Main.getInstance().getBooleanConfig("global-chat-after-end", true))))
      {
        paramAsyncPlayerChatEvent.setMessage(((String)localObject1).substring(1, ((String)localObject1).length()));
        localObject3 = getChatFormat(Main.getInstance().getStringConfig("ingame-chatformat-all", "[$all$] <$team$>$player$: $msg$"), localTeam, false, true);
      }
      else
      {
        paramAsyncPlayerChatEvent.setMessage((String)localObject1);
        localObject3 = getChatFormat(Main.getInstance().getStringConfig("ingame-chatformat", "<$team$>$player$: $msg$"), localTeam, bool2, true);
      }
      paramAsyncPlayerChatEvent.setFormat((String)localObject3);
      if ((!Main.getInstance().isBungee()) || (bool3))
      {
        Iterator localIterator3 = paramAsyncPlayerChatEvent.getRecipients().iterator();
        while (localIterator3.hasNext())
        {
          Player localPlayer3 = (Player)localIterator3.next();
          if (!localGame.isInGame(localPlayer3)) {
            localIterator3.remove();
          } else if ((bool3) && ((!localGame.getCycle().isEndGameRunning()) || (!Main.getInstance().getBooleanConfig("global-chat-after-end", true)))) {
            if ((bool2) && (!localGame.isSpectator(localPlayer3))) {
              localIterator3.remove();
            } else if ((!bool2) && (localGame.isSpectator(localPlayer3))) {
              localIterator3.remove();
            }
          }
        }
      }
    }
    else
    {
      localObject1 = ((String)localObject1).trim();
      paramAsyncPlayerChatEvent.setMessage((String)localObject1);
      paramAsyncPlayerChatEvent.setFormat(getChatFormat(Main.getInstance().getStringConfig("ingame-chatformat", "<$team$>$player$: $msg$"), localTeam, false, false));
      Iterator localIterator2 = paramAsyncPlayerChatEvent.getRecipients().iterator();
      while (localIterator2.hasNext())
      {
        localObject3 = (Player)localIterator2.next();
        if ((!localGame.isInGame((Player)localObject3)) || (!localTeam.isInTeam((Player)localObject3))) {
          localIterator2.remove();
        }
      }
    }
  }
  
  @EventHandler
  public void onPickup(PlayerPickupItemEvent paramPlayerPickupItemEvent)
  {
    Player localPlayer = paramPlayerPickupItemEvent.getPlayer();
    Game localGame = Main.getInstance().getGameManager().getGameOfPlayer(localPlayer);
    if (localGame == null) {
      return;
    }
    if (localGame.getState() != GameState.WAITING)
    {
      if (localGame.isSpectator(localPlayer)) {
        paramPlayerPickupItemEvent.setCancelled(true);
      }
      return;
    }
    paramPlayerPickupItemEvent.setCancelled(true);
  }
  
  @EventHandler(priority=EventPriority.HIGHEST)
  public void onCommand(PlayerCommandPreprocessEvent paramPlayerCommandPreprocessEvent)
  {
    Player localPlayer = paramPlayerCommandPreprocessEvent.getPlayer();
    Game localGame = Main.getInstance().getGameManager().getGameOfPlayer(localPlayer);
    if (localGame == null) {
      return;
    }
    if (localGame.getState() == GameState.STOPPED) {
      return;
    }
    String str1 = paramPlayerCommandPreprocessEvent.getMessage();
    if (!str1.startsWith(new StringBuilder().append("/").append(Main.getInstance().getStringConfig("command-prefix", "bw")).toString()))
    {
      Iterator localIterator = Main.getInstance().getAllowedCommands().iterator();
      while (localIterator.hasNext())
      {
        String str2 = (String)localIterator.next();
        if (!str2.startsWith("/")) {
          str2 = new StringBuilder().append("/").append(str2).toString();
        }
        if (str1.startsWith(str2.trim())) {
          return;
        }
      }
      if (localPlayer.hasPermission("bw.cmd")) {
        return;
      }
      paramPlayerCommandPreprocessEvent.setCancelled(true);
      return;
    }
  }
  
  @EventHandler
  public void onSleep(PlayerBedEnterEvent paramPlayerBedEnterEvent)
  {
    Player localPlayer = paramPlayerBedEnterEvent.getPlayer();
    Game localGame = Main.getInstance().getGameManager().getGameOfPlayer(localPlayer);
    if (localGame == null) {
      return;
    }
    if (localGame.getState() == GameState.STOPPED) {
      return;
    }
    paramPlayerBedEnterEvent.setCancelled(true);
  }
  
  @EventHandler
  public void onInteractEntity(PlayerInteractEntityEvent paramPlayerInteractEntityEvent)
  {
    Player localPlayer = paramPlayerInteractEntityEvent.getPlayer();
    Game localGame = Main.getInstance().getGameManager().getGameOfPlayer(localPlayer);
    if (localGame == null) {
      return;
    }
    if (localGame.getState() == GameState.WAITING)
    {
      paramPlayerInteractEntityEvent.setCancelled(true);
      return;
    }
    if (localGame.getState() == GameState.RUNNING) {
      inGameInteractEntity(paramPlayerInteractEntityEvent, localGame, localPlayer);
    }
  }
  
  @EventHandler(priority=EventPriority.HIGHEST)
  public void onFly(PlayerToggleFlightEvent paramPlayerToggleFlightEvent)
  {
    Player localPlayer = paramPlayerToggleFlightEvent.getPlayer();
    Game localGame = Main.getInstance().getGameManager().getGameOfPlayer(localPlayer);
    if (localGame == null) {
      return;
    }
    if (localGame.getState() == GameState.STOPPED) {
      return;
    }
    if ((localGame.getState() == GameState.RUNNING) && (localGame.isSpectator(localPlayer)))
    {
      paramPlayerToggleFlightEvent.setCancelled(false);
      return;
    }
    paramPlayerToggleFlightEvent.setCancelled(true);
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void onHunger(FoodLevelChangeEvent paramFoodLevelChangeEvent)
  {
    if (!(paramFoodLevelChangeEvent.getEntity() instanceof Player)) {
      return;
    }
    Player localPlayer = (Player)paramFoodLevelChangeEvent.getEntity();
    Game localGame = Main.getInstance().getGameManager().getGameOfPlayer(localPlayer);
    if (localGame == null) {
      return;
    }
    if (localGame.getState() == GameState.RUNNING)
    {
      if (localGame.isSpectator(localPlayer))
      {
        paramFoodLevelChangeEvent.setCancelled(true);
        return;
      }
      paramFoodLevelChangeEvent.setCancelled(false);
      return;
    }
    paramFoodLevelChangeEvent.setCancelled(true);
  }
  
  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent paramPlayerInteractEvent)
  {
    Player localPlayer = paramPlayerInteractEvent.getPlayer();
    Game localGame = Main.getInstance().getGameManager().getGameOfPlayer(localPlayer);
    if (localGame == null)
    {
      if ((paramPlayerInteractEvent.getAction() != Action.RIGHT_CLICK_BLOCK) && (paramPlayerInteractEvent.getAction() != Action.RIGHT_CLICK_AIR)) {
        return;
      }
      localObject1 = paramPlayerInteractEvent.getClickedBlock();
      if (localObject1 == null) {
        return;
      }
      if (!(((Block)localObject1).getState() instanceof Sign)) {
        return;
      }
      localObject2 = Main.getInstance().getGameManager().getGameBySignLocation(((Block)localObject1).getLocation());
      if (localObject2 == null) {
        return;
      }
      if (((Game)localObject2).playerJoins(localPlayer)) {
        localPlayer.sendMessage(ChatWriter.pluginMessage(new StringBuilder().append(ChatColor.GREEN).append(Main._l("success.joined")).toString()));
      }
      return;
    }
    if (localGame.getState() == GameState.STOPPED) {
      return;
    }
    Object localObject1 = paramPlayerInteractEvent.getMaterial();
    Object localObject2 = paramPlayerInteractEvent.getClickedBlock();
    Object localObject3;
    if (localGame.getState() == GameState.RUNNING)
    {
      if ((paramPlayerInteractEvent.getAction() == Action.PHYSICAL) && (localObject2 != null) && ((((Block)localObject2).getType() == Material.WHEAT) || (((Block)localObject2).getType() == Material.SOIL)))
      {
        paramPlayerInteractEvent.setCancelled(true);
        return;
      }
      if ((paramPlayerInteractEvent.getAction() != Action.RIGHT_CLICK_BLOCK) && (paramPlayerInteractEvent.getAction() != Action.RIGHT_CLICK_AIR)) {
        return;
      }
      if ((localObject2 != null) && (((Block)localObject2).getType() == Material.LEVER) && (!localGame.isSpectator(localPlayer)) && (paramPlayerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK))
      {
        if (!localGame.getRegion().isPlacedUnbreakableBlock((Block)localObject2)) {
          localGame.getRegion().addPlacedUnbreakableBlock((Block)localObject2, ((Block)localObject2).getState());
        }
        return;
      }
      if ((localGame.isSpectator(localPlayer)) || (((localGame.getCycle() instanceof BungeeGameCycle)) && (localGame.getCycle().isEndGameRunning()) && (Main.getInstance().getBooleanConfig("bungeecord.endgame-in-lobby", true))))
      {
        if (localObject1 == Material.SLIME_BALL)
        {
          localGame.playerLeave(localPlayer, false);
          return;
        }
        if (localObject1 == Material.COMPASS)
        {
          localGame.openSpectatorCompass(localPlayer);
          paramPlayerInteractEvent.setCancelled(true);
          return;
        }
      }
      Object localObject4;
      Object localObject5;
      if (localObject2 != null)
      {
        try
        {
          GameMode.valueOf("SPECTATOR");
        }
        catch (Exception localException)
        {
          localObject4 = localGame.getFreePlayers().iterator();
        }
        while (((Iterator)localObject4).hasNext())
        {
          localObject5 = (Player)((Iterator)localObject4).next();
          if (localGame.getRegion().isInRegion(((Player)localObject5).getLocation())) {
            if (paramPlayerInteractEvent.getClickedBlock().getLocation().distance(((Player)localObject5).getLocation()) < 2.0D)
            {
              Location localLocation = ((Player)localObject5).getLocation();
              if (localLocation.getY() >= paramPlayerInteractEvent.getClickedBlock().getLocation().getY()) {
                localLocation.setY(localLocation.getY() + 2.0D);
              } else {
                localLocation.setY(localLocation.getY() - 2.0D);
              }
              ((Player)localObject5).teleport(localLocation);
            }
          }
        }
      }
      if ((localObject2 != null) && (((Block)localObject2).getType() == Material.ENDER_CHEST) && (!localGame.isSpectator(localPlayer)))
      {
        paramPlayerInteractEvent.setCancelled(true);
        localObject3 = paramPlayerInteractEvent.getClickedBlock();
        localObject4 = localGame.getTeamOfEnderChest((Block)localObject3);
        localObject5 = localGame.getPlayerTeam(localPlayer);
        if (localObject4 == null) {
          return;
        }
        if (localObject4.equals(localObject5)) {
          localPlayer.openInventory(((Team)localObject4).getInventory());
        } else {
          localPlayer.sendMessage(ChatWriter.pluginMessage(new StringBuilder().append(ChatColor.RED).append(Main._l("ingame.noturteamchest")).toString()));
        }
        return;
      }
      return;
    }
    if (localGame.getState() == GameState.WAITING)
    {
      if (localObject1 == null)
      {
        paramPlayerInteractEvent.setCancelled(true);
        return;
      }
      if ((paramPlayerInteractEvent.getAction() == Action.PHYSICAL) && (localObject2 != null) && ((((Block)localObject2).getType() == Material.WHEAT) || (((Block)localObject2).getType() == Material.SOIL)))
      {
        paramPlayerInteractEvent.setCancelled(true);
        return;
      }
      if ((paramPlayerInteractEvent.getAction() != Action.RIGHT_CLICK_BLOCK) && (paramPlayerInteractEvent.getAction() != Action.RIGHT_CLICK_AIR)) {
        return;
      }
      switch (3.$SwitchMap$org$bukkit$Material[localObject1.ordinal()])
      {
      case 1: 
        paramPlayerInteractEvent.setCancelled(true);
        if (!localGame.isAutobalanceEnabled()) {
          localGame.getPlayerStorage(localPlayer).openTeamSelection(localGame);
        }
        break;
      case 2: 
        paramPlayerInteractEvent.setCancelled(true);
        if ((localPlayer.isOp()) || (localPlayer.hasPermission("bw.setup")))
        {
          localGame.start(localPlayer);
        }
        else if (localPlayer.hasPermission("bw.vip.forcestart"))
        {
          localObject3 = Main.getInstance().getLobbyCountdownRule();
          if (((GameLobbyCountdownRule)localObject3).isRuleMet(localGame)) {
            localGame.start(localPlayer);
          } else if ((localObject3 == GameLobbyCountdownRule.PLAYERS_IN_GAME) || (localObject3 == GameLobbyCountdownRule.ENOUGH_TEAMS_AND_PLAYERS)) {
            localPlayer.sendMessage(ChatWriter.pluginMessage(new StringBuilder().append(ChatColor.RED).append(Main._l("lobby.notenoughplayers-rule0")).toString()));
          } else {
            localPlayer.sendMessage(ChatWriter.pluginMessage(new StringBuilder().append(ChatColor.RED).append(Main._l("lobby.notenoughplayers-rule1")).toString()));
          }
        }
        break;
      case 3: 
        paramPlayerInteractEvent.setCancelled(true);
        localGame.playerLeave(localPlayer, false);
        break;
      case 4: 
        paramPlayerInteractEvent.setCancelled(true);
        localPlayer.updateInventory();
        break;
      }
    }
  }
  
  private void onLobbyInventoryClick(InventoryClickEvent paramInventoryClickEvent, Player paramPlayer, Game paramGame)
  {
    Inventory localInventory = paramInventoryClickEvent.getInventory();
    ItemStack localItemStack = paramInventoryClickEvent.getCurrentItem();
    if (!localInventory.getTitle().equals(Main._l("lobby.chooseteam")))
    {
      paramInventoryClickEvent.setCancelled(true);
      return;
    }
    if (localItemStack == null)
    {
      paramInventoryClickEvent.setCancelled(true);
      return;
    }
    if (localItemStack.getType() != Material.WOOL)
    {
      paramInventoryClickEvent.setCancelled(true);
      return;
    }
    paramInventoryClickEvent.setCancelled(true);
    Team localTeam = paramGame.getTeamByDyeColor(DyeColor.getByData(localItemStack.getData().getData()));
    if (localTeam == null) {
      return;
    }
    paramGame.playerJoinTeam(paramPlayer, localTeam);
    paramPlayer.closeInventory();
  }
  
  @EventHandler
  public void onDrop(PlayerDropItemEvent paramPlayerDropItemEvent)
  {
    Player localPlayer = paramPlayerDropItemEvent.getPlayer();
    Game localGame = Main.getInstance().getGameManager().getGameOfPlayer(localPlayer);
    if (localGame == null) {
      return;
    }
    if (localGame.getState() != GameState.WAITING)
    {
      if (localGame.isSpectator(localPlayer)) {
        paramPlayerDropItemEvent.setCancelled(true);
      }
      return;
    }
    paramPlayerDropItemEvent.setCancelled(true);
  }
  
  @EventHandler(priority=EventPriority.HIGHEST)
  public void onQuit(PlayerQuitEvent paramPlayerQuitEvent)
  {
    Player localPlayer = paramPlayerQuitEvent.getPlayer();
    if ((Main.getInstance().isHologramsEnabled()) && (Main.getInstance().getHolographicInteractor() != null)) {
      Main.getInstance().getHolographicInteractor().unloadAllHolograms(localPlayer);
    }
    Game localGame = Main.getInstance().getGameManager().getGameOfPlayer(localPlayer);
    if (localGame == null) {
      return;
    }
    localGame.playerLeave(localPlayer, false);
  }
  
  @EventHandler
  public void onDamage(EntityDamageEvent paramEntityDamageEvent)
  {
    Object localObject3;
    if (!(paramEntityDamageEvent.getEntity() instanceof Player))
    {
      if (!(paramEntityDamageEvent instanceof EntityDamageByEntityEvent)) {
        return;
      }
      localObject1 = (EntityDamageByEntityEvent)paramEntityDamageEvent;
      if ((((EntityDamageByEntityEvent)localObject1).getDamager() == null) || (!(((EntityDamageByEntityEvent)localObject1).getDamager() instanceof Player))) {
        return;
      }
      localObject2 = (Player)((EntityDamageByEntityEvent)localObject1).getDamager();
      localObject3 = Main.getInstance().getGameManager().getGameOfPlayer((Player)localObject2);
      if (localObject3 == null) {
        return;
      }
      if (((Game)localObject3).getState() == GameState.WAITING) {
        paramEntityDamageEvent.setCancelled(true);
      }
      return;
    }
    Object localObject1 = (Player)paramEntityDamageEvent.getEntity();
    Object localObject2 = Main.getInstance().getGameManager().getGameOfPlayer((Player)localObject1);
    if (localObject2 == null) {
      return;
    }
    if (((Game)localObject2).getState() == GameState.STOPPED) {
      return;
    }
    if (((Game)localObject2).getState() == GameState.RUNNING)
    {
      if (((Game)localObject2).isSpectator((Player)localObject1))
      {
        paramEntityDamageEvent.setCancelled(true);
        return;
      }
      if ((((Game)localObject2).isProtected((Player)localObject1)) && (paramEntityDamageEvent.getCause() != EntityDamageEvent.DamageCause.VOID))
      {
        paramEntityDamageEvent.setCancelled(true);
        return;
      }
      if ((Main.getInstance().getBooleanConfig("die-on-void", false)) && (paramEntityDamageEvent.getCause() == EntityDamageEvent.DamageCause.VOID))
      {
        paramEntityDamageEvent.setCancelled(true);
        ((Player)localObject1).setHealth(0.0D);
        return;
      }
      if ((paramEntityDamageEvent instanceof EntityDamageByEntityEvent))
      {
        localObject3 = (EntityDamageByEntityEvent)paramEntityDamageEvent;
        Object localObject4;
        if ((((EntityDamageByEntityEvent)localObject3).getDamager() instanceof Player))
        {
          localObject4 = (Player)((EntityDamageByEntityEvent)localObject3).getDamager();
          if (((Game)localObject2).isSpectator((Player)localObject4))
          {
            paramEntityDamageEvent.setCancelled(true);
            return;
          }
          ((Game)localObject2).setPlayerDamager((Player)localObject1, (Player)localObject4);
        }
        else if (((EntityDamageByEntityEvent)localObject3).getDamager().getType().equals(EntityType.ARROW))
        {
          localObject4 = (Arrow)((EntityDamageByEntityEvent)localObject3).getDamager();
          if ((((Arrow)localObject4).getShooter() instanceof Player))
          {
            Player localPlayer = (Player)((Arrow)localObject4).getShooter();
            if (((Game)localObject2).isSpectator(localPlayer))
            {
              paramEntityDamageEvent.setCancelled(true);
              return;
            }
            ((Game)localObject2).setPlayerDamager((Player)localObject1, (Player)((Arrow)localObject4).getShooter());
          }
        }
      }
      if (!((Game)localObject2).getCycle().isEndGameRunning()) {
        return;
      }
      if (paramEntityDamageEvent.getCause() == EntityDamageEvent.DamageCause.VOID) {
        ((Player)localObject1).teleport(((Game)localObject2).getPlayerTeam((Player)localObject1).getSpawnLocation());
      }
    }
    else if ((((Game)localObject2).getState() == GameState.WAITING) && (paramEntityDamageEvent.getCause() == EntityDamageEvent.DamageCause.VOID))
    {
      ((Player)localObject1).teleport(((Game)localObject2).getLobby());
    }
    paramEntityDamageEvent.setCancelled(true);
  }
}


/* Location:           C:\myserver\mcoasis\spigot\plugins\[起床战争]BedwarsRel-1.2.8.jar
 * Qualified Name:     io.github.yannici.bedwars.Listener.PlayerListener
 * JD-Core Version:    0.7.0-SNAPSHOT-20130630
 */