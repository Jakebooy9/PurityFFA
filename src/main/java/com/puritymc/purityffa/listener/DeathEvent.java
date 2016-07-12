
package com.puritymc.purityffa.listener;

import com.puritymc.purityffa.PurityFFA;
import com.puritymc.purityffa.player.FFAPlayer;
import com.puritymc.purityffa.player.PlayerManager;
import com.puritymc.purityffa.util.Message;

import com.puritymc.purityffa.util.Util;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/*******************************************************************************
 * Copyright MartinItsLinda (c) 2016. All Rights Reserved.
 * Any code contained within this document, and any associated API's with similar branding
 * are the sole property of MartinItsLinda. Distribution, reproduction, taking snippets or
 * claiming any contents as your own will break the terms of the liscense and void any
 * agreements with you, the third party.
 ******************************************************************************/

public class DeathEvent implements Listener {

    private PurityFFA plugin = PurityFFA.getInstance();

    private Map<UUID, Integer> kills = new HashMap<>();

    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent event) {

        event.setKeepInventory(false);
        event.setKeepLevel(false);
        event.setNewLevel(0);
        event.setNewExp(0);
        event.setNewTotalExp(0);
        event.setDroppedExp(0);
        event.setDeathMessage(null);
        event.getDrops().clear();

        if (event.getEntity().getKiller() != null) {

            FFAPlayer killed = PlayerManager.getPlayer(event.getEntity());
            FFAPlayer killer = PlayerManager.getPlayer(event.getEntity().getKiller());

            Player kld = killed.getBukkitPlayer();
            Player klr = killer.getBukkitPlayer();

            kills.putIfAbsent(klr.getUniqueId(), 1);

            int killsOnPlayer = kills.get(klr.getUniqueId());

            int gained = killed.getPoints() < 100 ? 5 :
                    (int) Math.round(killed.getPoints() * 0.05);

            int lost = killed.getPoints() <= 5 ? killed.getPoints() : gained;

            if (killer.getLastKilled() != null && killer.getLastKilled().equals(kld.getUniqueId())) {
                if (killsOnPlayer == 3) gained = lost = 0;
                else kills.put(klr.getUniqueId(), killsOnPlayer + 1);
            } else kills.put(klr.getUniqueId(), 1);


            if (killed.getKillStreak()>5) {
                Message.get("kill_streak_ended")
                        .replace("%player%", kld.getName())
                        .replace("%streak%", killed.getKillStreak())
                        .sendTo(Bukkit.getOnlinePlayers());
            }

            killed.setPoints(killed.getPoints() - lost);
            killed.setDeaths(killed.getDeaths() + 1);
            killed.setKillStreak(0);
            killed.setTagger(null);

            killer.setPoints(killer.getPoints() + gained);
            killer.setKills(killer.getKills() + 1);
            killer.setKillStreak(killer.getKillStreak() + 1);
            killer.setTagger(null);
            killer.setLastKilled(kld.getUniqueId());

            if (killer.getKillStreak() > killer.getHighestKillStreak())
                killer.setHighestKillStreak(killer.getKillStreak());


            klr.setLevel(killer.getKillStreak());

            klr.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 4));

            if (!(klr.getInventory().contains(Material.FLINT_AND_STEEL))) klr.getInventory()
                    .addItem(new ItemStack(Material.FLINT_AND_STEEL));


            klr.getInventory().addItem(new ItemStack(Material.ARROW));

            kld.getScoreboard().getObjective(DisplaySlot.BELOW_NAME)
                    .getScore(kld)
                    .setScore(0);

            klr.getScoreboard().getObjective(DisplaySlot.BELOW_NAME)
                    .getScore(klr)
                    .setScore(killer.getKillStreak());

            Util.spawnFirework(klr.getLocation());

            Message.get("death_message")
                    .replace("%health%", Util.format(killer.getBukkitPlayer().getHealth() / 2, 1))
                    .replace("%maxHealth%", Util.format(killer.getBukkitPlayer().getMaxHealth() / 2, 1))
                    .replace("%killer%", klr.getName())
                    .replace("%lost%", lost)
                    .replace("%total%", killed.getPoints())
                    .sendTo(kld);

            Message.get("killed_message")
                    .replace("%health%", Util.format(killer.getBukkitPlayer().getHealth() / 2, 1))
                    .replace("%maxHealth%", Util.format(killer.getBukkitPlayer().getMaxHealth() / 2, 1))
                    .replace("%killed%", kld.getName())
                    .replace("%gained%", gained)
                    .replace("%total%", killer.getPoints())
                    .sendTo(klr);

            Bukkit.getScheduler().runTaskAsynchronously(plugin, ()-> {
                if (!(killed.update())) Message.get("failed_to_update_stats").sendTo(killed.getBukkitPlayer());
                if (!(killer.update())) Message.get("failed_to_update_stats").sendTo(killer.getBukkitPlayer());
            });

        }

    }


}

