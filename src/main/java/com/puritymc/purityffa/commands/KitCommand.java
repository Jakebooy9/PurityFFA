
package com.puritymc.purityffa.commands;

import com.puritymc.purityffa.PurityFFA;
import com.puritymc.purityffa.kit.Kit;
import com.puritymc.purityffa.player.PlayerManager;
import com.puritymc.purityffa.util.Message;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

/*******************************************************************************
 * Copyright MartinItsLinda (c) 2016. All Rights Reserved.
 * Any code contained within this document, and any associated API's with similar branding
 * are the sole property of MartinItsLinda. Distribution, reproduction, taking snippets or
 * claiming any contents as your own will break the terms of the liscense and void any
 * agreements with you, the third party.
 ******************************************************************************/

public class KitCommand implements CommandExecutor, TabExecutor {

    private PurityFFA plugin;

    public KitCommand(PurityFFA plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("kit")) {
            if (!(sender instanceof Player)) {
                Message.get("no_console_access")
                        .sendTo(sender);
                return true;
            }

            if (args.length < 1) {
                Message.get("correct_usage")
                        .replace("%command%", command.getName())
                        .replace("%usage%", "<kit>")
                        .sendTo(sender);
                return false;
            }

            if (plugin.getKits().size() == 0) {
                Message.get("no_available_kits")
                        .sendTo(sender);
                return false;
            }

            Kit kit = plugin.getKit(args[0]);

            if (kit == null) {
                Message.get("unknown_kit")
                        .replace("%kit%", args[0])
                        .sendTo(sender);
                return true;
            }

            plugin.giveKit(kit, PlayerManager.getPlayer(((Player) sender)));

            Message.get("kit_received")
                    .replace("%kit%", kit.getName())
                    .sendTo(sender);
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return plugin.getKits().stream().map(Kit::getName).collect(Collectors.toList());
    }
}
