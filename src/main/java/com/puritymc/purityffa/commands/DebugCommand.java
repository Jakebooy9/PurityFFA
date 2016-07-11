
package com.puritymc.purityffa.commands;

import com.puritymc.purityffa.PurityFFA;
import com.puritymc.purityffa.player.PlayerManager;
import com.puritymc.purityffa.player.FFAPlayer;
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

public class DebugCommand implements CommandExecutor {

    private PurityFFA plugin;

    public DebugCommand(PurityFFA plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("debug")) {
            if (!(sender.hasPermission("ffa.debug"))) {
                Message.get("insufficient_permissions").sendTo(sender);
                return true;
            }
            if (!(sender instanceof Player)) {
                Message.get("no_console_access").sendTo(sender);
                return true;
            }
            FFAPlayer player = PlayerManager.getPlayer(((Player) sender));
            player.setDebugging(!player.isDebugging());
            Message.get(player.isDebugging() ? "now_debugging" : "no_longer_debugging").sendTo(sender);
        }
        return false;
    }
}
