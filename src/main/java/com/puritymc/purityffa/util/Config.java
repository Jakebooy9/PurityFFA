
package com.puritymc.purityffa.util;

import com.puritymc.purityffa.PurityFFA;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

/*******************************************************************************
 * Copyright MartinItsLinda (c) 2016. All Rights Reserved.
 * Any code contained within this document, and any associated API's with similar branding
 * are the sole property of MartinItsLinda. Distribution, reproduction, taking snippets or
 * claiming any contents as your own will break the terms of the liscense and void any
 * agreements with you, the third party.
 ******************************************************************************/

public class Config {

    public static void load(PurityFFA plugin) {
        Map<String, Object> cfg = new HashMap<>();
        FileConfiguration config = plugin.getConfig();

        cfg.put("messages.prefix", "&8[&7FFA&8]");
        cfg.put("messages.death_message", "&cYou have been killed by %killer%, you lost %lost%, you now have %total%");
        cfg.put("messages.killed_message", "&aYou have killed %killed%, you gained %gained%, you now have %total%, (%health%/%maxHealth%)");
        cfg.put("messages.stats", Arrays.asList("&e%player%'s statistics", "&eRank: &c%ranking%", "&ePoints: &c%points%", "&eKills: &c%kills%", "&eDeaths: &c%deaths%", "Highest Kill Streak: %highestKillStreak%", "&eK/D: &c%kd%"));
        cfg.put("messages.correct_usage", "/%command% %usage%");
        cfg.put("messages.insufficient_permissions", "&cYou don't have permission to do that.");
        cfg.put("messages.stats_reset", "&aStats reset for %player%");
        cfg.put("messages.player_not_found", "&cPlayer not found %player%");
        cfg.put("messages.spawn_location_set", "&aSpawn location set.");
        cfg.put("messages.kill_streak_started", "&a%player% is on a %streak% kill streak!");
        cfg.put("messages.kill_streak_ended", "&c%player%'s kill streak has ended with %streak% kills.");
        cfg.put("messages.kit_added", "&aThe kit %kit% is now available.");
        cfg.put("messages.kit_received", "&aYou have been given the %kit% kit.");
        cfg.put("messages.unknown_kit", "&cUnknown kit %kit%.");
        cfg.put("messages.no_available_kits", "&cThere're no available kits at this time.");
        cfg.put("messages.no_console_access", "&cThe console cannot use this command.");
        cfg.put("messages.inventory_saved", "&aYour inventory has been saved.");
        cfg.put("messages.chat_format", "&8[&7%points%&8]&8[&7%prefix%&8] &f%name%&7: &f%message%.");
        cfg.put("messages.player_fixed", "&cFixed %player%.");
        cfg.put("messages.failed_to_update_stats", "&cFailed to update statistics.");
        cfg.put("messages.now_debugging", "&aYou're now debugging (chat may get very spammy).");
        cfg.put("messages.no_longer_debugging", "&cYou're no longer debugging.");
        cfg.put("messages.debug_message", "&f[%class%] %message%.");
        cfg.put("messages.no_kit_equipped", "&cYou must have a kit equipped.");
        cfg.put("messages.kit_not_complete", "&cYour kit must be complete to use \"/saveinventory\".");
        cfg.put("messages.leaderboard_format", "&e%position%. %player%: %kills%");
        cfg.put("messages.spectator_mode_enabled", "&aSpectator mode enabled.");
        cfg.put("messages.no_spectator_currently_tagged", "&cCannot enable spectator mode whilst combat tagged.");
        cfg.put("messages.spectator_mode_disabled", "&cSpectator mode disabled");

        cfg.put("ffa.default_kit", "KIT_NAME");
        cfg.put("ffa.mysql.user", "username");
        cfg.put("ffa.mysql.password", "password");
        cfg.put("ffa.mysql.host", "localhost");
        cfg.put("ffa.mysql.database", "MineCraft");
        cfg.put("ffa.mysql.port", 3306);
        cfg.put("ffa.tag_duration", 10);
        cfg.put("ffa.starting_points", 100);

        cfg.entrySet().stream().filter(e -> !(config.contains(e.getKey()))).forEachOrdered(e -> config.set(e.getKey(), e.getValue()));

        plugin.saveConfig();

    }

}
