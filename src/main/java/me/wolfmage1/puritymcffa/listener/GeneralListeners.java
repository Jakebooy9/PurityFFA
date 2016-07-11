
package me.wolfmage1.puritymcffa.listener;

import me.wolfmage1.puritymcffa.FFA;
import me.wolfmage1.puritymcffa.kit.Kit;
import me.wolfmage1.puritymcffa.player.FFAPlayer;
import me.wolfmage1.puritymcffa.player.PlayerManager;
import me.wolfmage1.puritymcffa.util.Message;

import me.wolfmage1.puritymcffa.util.Util;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Team;

import java.util.*;

/*******************************************************************************
 * Copyright MartinItsLinda (c) 2016. All Rights Reserved.
 * Any code contained within this document, and any associated API's with similar branding
 * are the sole property of MartinItsLinda. Distribution, reproduction, taking snippets or
 * claiming any contents as your own will break the terms of the liscense and void any
 * agreements with you, the third party.
 ******************************************************************************/

public class GeneralListeners implements Listener {

    private FFA plugin;

    public GeneralListeners(FFA plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBoom(EntityExplodeEvent event) {
        event.blockList().clear();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        player.setGameMode(GameMode.SURVIVAL);

        player.setLevel(0);
        player.setExp(0);

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        player.setHealth(0);

        player.setScoreboard(plugin.getScoreboard());

        if (plugin.getChat() != null) {
            String group = plugin.getChat().getPrimaryGroup(player);
            String prefix = plugin.getChat().getGroupPrefix(player.getWorld().getName(), plugin.getChat().getPrimaryGroup(player));

            if (prefix != null && group != null) {
                if (plugin.getScoreboard().getTeam(group) != null) {
                    plugin.getScoreboard().getTeam(group).addPlayer(player);
                } else {
                    Team t = plugin.getScoreboard().registerNewTeam(group);
                    t.setPrefix(ChatColor.translateAlternateColorCodes('&', prefix));
                    plugin.getScoreboard().getTeam(group).addPlayer(player);
                }
            }
        }

        event.setJoinMessage(null);

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);

        FFAPlayer player = PlayerManager.getPlayer(event.getPlayer());
        Player pl = player.getBukkitPlayer();

        player.setTagger(null);
        player.setLastKilled(null);
        player.setKillStreak(0);

        PlayerManager.getPlayers().remove(pl.getUniqueId());

        if (plugin.getChat() != null) {
            String group = plugin.getChat().getPrimaryGroup(pl);

            if (group != null && plugin.getScoreboard().getTeam(group) != null) {
                plugin.getScoreboard().getTeam(group).removePlayer(pl);
            }
        }

    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        event.getDrops().clear();
        event.setDroppedExp(0);
    }

    @EventHandler
    public void onAchieve(PlayerAchievementAwardedEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {

            if (((Player)event.getEntity()).getGameMode() == GameMode.CREATIVE) {
                event.setCancelled(true);
                return;
            }

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> PlayerManager.getPlayer((Player) event.getEntity()).setTagger(event.getDamager().getUniqueId()));

            for (ItemStack item : ((Player) event.getEntity()).getInventory()) {
                if (item != null) item.setDurability((short)0);
            }

        }

    }

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onShoot(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            player.getItemInHand().setDurability((short)0);
            player.updateInventory();

        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity().getType() != EntityType.FISHING_HOOK) event.getEntity().remove();

    }

    @EventHandler
    public void onItemBreak(PlayerItemBreakEvent event) {
        event.getBrokenItem().setDurability((short) 0);
        event.getPlayer().updateInventory();
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) event.setCancelled(true);

    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (event.getBlock().getType() != Material.FIRE &&
                event.getPlayer().getGameMode() != GameMode.CREATIVE) event.setCancelled(true);

    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPickup(final PlayerPickupItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK &&
                event.getItem() != null &&
                event.getItem().getType() == Material.FLINT_AND_STEEL) {
            event.getPlayer().getInventory().remove(event.getItem());
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {

        Player pl = event.getPlayer();
        FileConfiguration config = plugin.getConfig();

        if (!(plugin.getSpawns().isEmpty())) {
            int spawn = new Random().nextInt(plugin.getSpawns().size());

            World world = Bukkit.getWorld(config.getString("spawns." + spawn + ".world"));
            double x = config.getDouble("spawns." + spawn + ".X");
            double y = config.getDouble("spawns." + spawn + ".Y");
            double z = config.getDouble("spawns." + spawn + ".Z");

            Location respawnLocation = new Location(world, x, y ,z);

            event.setRespawnLocation(respawnLocation);
        }

        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {

            plugin.giveKit(plugin.getKit(plugin.getConfig().getString("ffa.default_kit")), PlayerManager.getPlayer(pl));

        }, 20L); //1 tick after they spawn

    }

}
