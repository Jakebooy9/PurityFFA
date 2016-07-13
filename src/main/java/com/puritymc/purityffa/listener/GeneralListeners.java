
package com.puritymc.purityffa.listener;

import com.puritymc.purityffa.PurityFFA;
import com.puritymc.purityffa.player.FFAPlayer;
import com.puritymc.purityffa.player.PlayerManager;

import com.puritymc.purityffa.util.Message;
import com.puritymc.purityffa.util.Util;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Team;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

import static org.bukkit.Bukkit.getScheduler;

/*******************************************************************************
 * Copyright MartinItsLinda (c) 2016. All Rights Reserved.
 * Any code contained within this document, and any associated API's with similar branding
 * are the sole property of MartinItsLinda. Distribution, reproduction, taking snippets or
 * claiming any contents as your own will break the terms of the liscense and void any
 * agreements with you, the third party.
 ******************************************************************************/

public class GeneralListeners implements Listener {

    private PurityFFA plugin;

    public GeneralListeners(PurityFFA plugin) {
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

        Player pl = event.getPlayer();

        pl.setGameMode(GameMode.SURVIVAL);
        pl.setLevel(0);
        pl.setExp(0);
        pl.setFoodLevel(20);
        pl.setSaturation(20);
        pl.setHealth(20);
        pl.setMaxHealth(20);
        pl.setFireTicks(0);
        pl.setScoreboard(plugin.getScoreboard());

        event.setJoinMessage(null);

        getScheduler().runTaskAsynchronously(plugin, () -> {
            FFAPlayer player = PlayerManager.getPlayer(event.getPlayer());

            if (!(plugin.getSpawns().isEmpty())) {

                int spawn = new Random().nextInt(plugin.getSpawns().size());

                World world = Bukkit.getWorld(plugin.getConfig().getString("spawns." + spawn + ".world"));

                double x = plugin.getConfig().getDouble("spawns." + spawn + ".X");
                double y = plugin.getConfig().getDouble("spawns." + spawn + ".Y");
                double z = plugin.getConfig().getDouble("spawns." + spawn + ".Z");

                Location location = new Location(world, x, y, z);

                getScheduler().runTaskLater(plugin, () -> {
                    pl.teleport(location);
                    plugin.giveKit(plugin.getKit("default"), player);
                }, 20L);

            }

            try (Connection connection = plugin.getConnection();
                 PreparedStatement statement = connection.prepareStatement("update ffa_stats set username = ? where unique_id = ?")) {
                statement.setString(1, player.getBukkitPlayer().getName());
                statement.setString(2, player.getBukkitPlayer().getUniqueId().toString());
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        PlayerManager.getPlayers().values().stream().filter(FFAPlayer::isSpectating)
                .forEach(online -> pl.hidePlayer(online.getBukkitPlayer()));

        if (plugin.getChat() != null) {
            String group = plugin.getChat().getPrimaryGroup(pl);
            String prefix = plugin.getChat().getGroupPrefix(pl.getWorld().getName(), plugin.getChat().getPrimaryGroup(pl));

            if (prefix != null && group != null) {
                if (plugin.getScoreboard().getTeam(group) != null) {
                    plugin.getScoreboard().getTeam(group).addPlayer(pl);
                } else {
                    Team team = plugin.getScoreboard().registerNewTeam(group);
                    team.setPrefix(ChatColor.translateAlternateColorCodes('&', prefix));
                    plugin.getScoreboard().getTeam(group).addPlayer(pl);
                }
            }
        }

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);

        FFAPlayer player = PlayerManager.getPlayer(event.getPlayer());
        Player pl = player.getBukkitPlayer();

        if (player.getTagger() != null) {

            FFAPlayer killer = PlayerManager.getPlayer(player.getTagger());

            Player klr = killer.getBukkitPlayer();

            int gained = player.getPoints() <= 100 ? 10 :
                    (int) Math.round(player.getPoints() * 0.05) * 2;

            if (player.getKillStreak() > 5) {
                Message.get("kill_streak_ended")
                        .replace("%player%", player.getBukkitPlayer().getName())
                        .replace("%streak%", player.getKillStreak())
                        .sendTo(Bukkit.getOnlinePlayers());
            }

            player.setPoints(player.getPoints() - gained);
            player.setDeaths(player.getDeaths() + 1);
            player.setKillStreak(0);
            player.setTagger(null);

            killer.setPoints(killer.getPoints() + gained);
            killer.setKills(killer.getKills() + 1);
            killer.setKillStreak(killer.getKillStreak() + 1);
            killer.setTagger(null);
            killer.setLastKilled(pl.getUniqueId());

            if (killer.getKillStreak() > killer.getHighestKillStreak())
                killer.setHighestKillStreak(killer.getKillStreak());

            klr.setLevel(killer.getKillStreak());

            klr.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 4));

            if (!(klr.getInventory().contains(Material.FLINT_AND_STEEL))) klr.getInventory()
                    .addItem(new ItemStack(Material.FLINT_AND_STEEL));


            klr.getInventory().addItem(new ItemStack(Material.ARROW));

            klr.getScoreboard().getObjective(DisplaySlot.BELOW_NAME)
                    .getScore(klr)
                    .setScore(killer.getKillStreak());

            Util.spawnFirework(klr.getLocation());

            Message.get("killed_message")
                    .replace("%health%", Util.format(killer.getBukkitPlayer().getHealth() / 2, 1))
                    .replace("%maxHealth%", Util.format(killer.getBukkitPlayer().getMaxHealth() / 2, 1))
                    .replace("%killed%", pl.getName())
                    .replace("%gained%", gained)
                    .replace("%total%", killer.getPoints())
                    .sendTo(klr);

            getScheduler().runTaskAsynchronously(plugin, () -> {
                player.update();
                if (!(killer.update())) Message.get("failed_to_update_stats").sendTo(killer.getBukkitPlayer());
            });

        }

        pl.getScoreboard().getObjective(DisplaySlot.BELOW_NAME).getScore(pl).setScore(0);

        if (plugin.getChat() != null) {
            String group = plugin.getChat().getPrimaryGroup(pl);
            if (group != null && plugin.getScoreboard().getTeam(group) != null) {
                plugin.getScoreboard().getTeam(group).removePlayer(pl);
            }
        }

        if (player.isSpectating()) {
            Bukkit.getOnlinePlayers().forEach(online -> online.showPlayer(player.getBukkitPlayer()));
        }

        PlayerManager.getPlayers().remove(pl.getUniqueId());

    }

    @EventHandler
    public void onEntitySpawn(CreatureSpawnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onAchieve(PlayerAchievementAwardedEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {

        if (e.getEntity() instanceof Player) {

            FFAPlayer player = PlayerManager.getPlayer(((Player)e.getEntity()));

            if (player.isSpectating()) {
                e.setCancelled(true);
                return;
            }

            if (e instanceof EntityDamageByEntityEvent) {

                EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;

                if (event.getDamager() instanceof Player) {

                    if (((Player) event.getDamager()).getGameMode() == GameMode.CREATIVE) {
                        event.setCancelled(true);
                        return;
                    }

                    if (PlayerManager.getPlayer((Player) event.getDamager()).isSpectating()) {
                        event.setCancelled(true);
                        return;
                    }

                    if (player.getBukkitPlayer().getHealth() > 0) player.setTagger(event.getDamager().getUniqueId());


                    for (ItemStack item : ((Player) event.getEntity()).getInventory()) {
                        if (item != null) item.setDurability((short) 0);
                    }

                }

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
            player.getItemInHand().setDurability((short) 0);
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
    public void onPickup(PlayerPickupItemEvent event) {
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

            Location respawnLocation = new Location(world, x, y, z);

            event.setRespawnLocation(respawnLocation);
        }

        getScheduler().runTaskLaterAsynchronously(plugin, () -> {

            plugin.giveKit(plugin.getKit(plugin.getConfig().getString("ffa.default_kit")), PlayerManager.getPlayer(pl));

        }, 20L); //1 second after they spawn

    }

}
