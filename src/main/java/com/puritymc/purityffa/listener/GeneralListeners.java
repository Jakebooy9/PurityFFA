
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

import java.util.*;

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

        if (player.getTagger() != null) {

            FFAPlayer killer = PlayerManager.getPlayer(player.getTagger());

            Player klr = killer.getBukkitPlayer();

            int gained = player.getPoints() < 100 ? 5 :
                    (int) Math.round(player.getPoints() * 0.5);

            int lost = player.getPoints() <= 5 ? player.getPoints() : gained;

            if (player.getKillStreak()>5) {
                Message.get("kill_streak_ended")
                        .replace("%player%", player.getBukkitPlayer().getName())
                        .replace("%streak%", player.getKillStreak())
                        .sendTo(Bukkit.getOnlinePlayers());
            }

            player.setPoints(player.getPoints() - lost * 2);
            player.setDeaths(player.getDeaths() + 1);
            player.setKillStreak(0);
            player.setTagger(null);

            killer.setPoints(killer.getPoints() + gained * 2);
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

            Message.get("death_message")
                    .replace("%health%", Util.format(killer.getBukkitPlayer().getHealth() / 2, 1))
                    .replace("%maxHealth%", Util.format(killer.getBukkitPlayer().getMaxHealth() / 2, 1))
                    .replace("%killer%", klr.getName())
                    .replace("%lost%", lost)
                    .replace("%total%", player.getPoints())
                    .sendTo(pl);

            Message.get("killed_message")
                    .replace("%health%", Util.format(killer.getBukkitPlayer().getHealth() / 2, 1))
                    .replace("%maxHealth%", Util.format(killer.getBukkitPlayer().getMaxHealth() / 2, 1))
                    .replace("%killed%", pl.getName())
                    .replace("%gained%", gained)
                    .replace("%total%", killer.getPoints())
                    .sendTo(klr);

            Bukkit.getScheduler().runTaskAsynchronously(plugin, ()-> {
                player.update();
                if (!(killer.update())) Message.get("failed_to_update_stats").sendTo(killer.getBukkitPlayer());
            });

        }

        player.setTagger(null);
        player.setLastKilled(null);
        player.setKillStreak(0);

        PlayerManager.getPlayers().remove(pl.getUniqueId());

        pl.getScoreboard().getObjective(DisplaySlot.BELOW_NAME).getScore(pl).setScore(0);
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
        event.setExpLevelCost(0);
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

            Location respawnLocation = new Location(world, x, y ,z);

            event.setRespawnLocation(respawnLocation);
        }

        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {

            plugin.giveKit(plugin.getKit(plugin.getConfig().getString("ffa.default_kit")), PlayerManager.getPlayer(pl));

        }, 20L); //1 tick after they spawn

    }

}
