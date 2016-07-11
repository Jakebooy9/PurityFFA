
package com.puritymc.purityffa.commands;

import com.puritymc.purityffa.PurityFFA;
import com.puritymc.purityffa.util.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/*******************************************************************************
 * Copyright MartinItsLinda (c) 2016. All Rights Reserved.
 * Any code contained within this document, and any associated API's with similar branding
 * are the sole property of MartinItsLinda. Distribution, reproduction, taking snippets or
 * claiming any contents as your own will break the terms of the liscense and void any
 * agreements with you, the third party.
 ******************************************************************************/

public class SetSpawnCommand implements CommandExecutor {

    private PurityFFA plugin;

    public SetSpawnCommand(PurityFFA plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("setspawn")) {
            if (!(sender.hasPermission("ffa.setspawn"))) {
                Message.get("insufficient_permissions").sendTo(sender);
                return true;
            }
            if (!(sender instanceof Player)) {
                Message.get("no_console_access").sendTo(sender);
                return true;
            }
            Player player = (Player) sender;
            int i = plugin.getSpawns().size();
            plugin.getConfig().set("spawns." + i + ".world", player.getWorld().getName());
            plugin.getConfig().set("spawns." + i + ".X", player.getLocation().getBlockX());
            plugin.getConfig().set("spawns." + i + ".Y", player.getLocation().getBlockY());
            plugin.getConfig().set("spawns." + i + ".Z", player.getLocation().getBlockZ());
            plugin.getConfig().set("spawns." + i + ".yaw", player.getLocation().getYaw());
            plugin.getConfig().set("spawns." + i + ".pitch", player.getLocation().getPitch());

            plugin.saveConfig();
            plugin.reloadConfig();

            Message.get("spawn_location_set").sendTo(sender);
        }
        return true;
    }
}
