package com.puritymc.purityffa.commands;

import com.puritymc.purityffa.PurityFFA;
import com.puritymc.purityffa.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/*******************************************************************************
 * Copyright MartinItsLinda (c) 2016. All Rights Reserved.
 * Any code contained within this document, and any associated API's with similar branding
 * are the sole property of MartinItsLinda. Distribution, reproduction, taking snippets or
 * claiming any contents as your own will break the terms of the liscense and void any
 * agreements with you, the third party.
 ******************************************************************************/

public class LeaderboardCommand implements CommandExecutor {

    private final PurityFFA plugin;

    public LeaderboardCommand(PurityFFA plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {

        if (command.getName().equalsIgnoreCase("leaderboard")) {

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try (Connection connection = plugin.getConnection();
                     PreparedStatement stmt = connection.prepareStatement("select * from `ffa_stats` order by `kills` desc limit 0, 10;");
                     ResultSet set = stmt.executeQuery()) {

                    int i = 1;

                    while (set.next() && i <= 10) {

                        Message message = Message.get("leaderboard_format");

                        message.replace("%position%", i);
                        message.replace("%player%", Bukkit.getOfflinePlayer(UUID.fromString(set.getString("unique_id"))).getName());
                        message.replace("%kills%", Integer.toString(set.getInt("kills")));

                        message.sendTo(commandSender);

                        i++;
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

        }

        return false;
    }
}
