
package com.puritymc.purityffa.commands;

import com.puritymc.purityffa.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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

public class FixCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("fix")) {
            if (!(sender instanceof Player)) {
                Message.get("no_console_access").sendTo(sender);
                return true;
            }
            if (args.length < 1) {
                Player target = (Player) sender; //To lazy to change variable names

                Location location = target.getLocation();

                location.setX(location.getBlockX());
                location.setY(location.getBlockY());
                location.setZ(location.getBlockZ());

                target.teleport(location);

                Message.get("player_fixed").replace("%player%", target.getName()).sendTo(sender);
                return true;
            }
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                Message.get("player_not_found").replace("%player%", args[0]);
                return true;
            }

            Location location = target.getLocation();

            location.setX(location.getBlockX());
            location.setY(location.getBlockY());
            location.setZ(location.getBlockZ());

            target.teleport(location);

            Message.get("player_fixed").replace("%player%", target.getName()).sendTo(sender);
        }
        return false;
    }
}
