
package com.puritymc.purityffa.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

/*******************************************************************************
 * Copyright MartinItsLinda (c) 2016. All Rights Reserved.
 * Any code contained within this document, and any associated API's with similar branding
 * are the sole property of MartinItsLinda. Distribution, reproduction, taking snippets or
 * claiming any contents as your own will break the terms of the liscense and void any
 * agreements with you, the third party.
 ******************************************************************************/

public class Message {

    private static Map<String, String> messages = new HashMap<>();
    private String message = null;

    private Message(String key) {
        message = messages.get(key);
    }

    public Message replace(CharSequence oldChar, CharSequence newChar) {
        this.message = message.replace(oldChar, newChar);
        return this;
    }

    public Message replace(CharSequence oldChar, Integer newChar) {
        return replace(oldChar, Integer.toString(newChar));
    }

    public Message replace(CharSequence oldChar, Double newChar) {
        return replace(oldChar, Double.toString(newChar));
    }

    public String colorize() {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public void sendTo(Collection<? extends Player> players) {
        for (Player player : players) {
            player.sendMessage(getString("prefix") + " " + message);
        }
    }

    public void sendTo(Player player) {
        if (player == null) {
            return;
        }
        player.sendMessage(getString("prefix") + " " + message);
    }

    public void sendTo(CommandSender sender) {
        sender.sendMessage(getString("prefix") + " " + toString());
    }

    public String toString() {
        return this.message;
    }

    public static Message get(String key) {
        return new Message(key);
    }

    public static String getString(String key) {
        return get(key).toString();
    }

    public static void load(FileConfiguration config) {
        messages.clear();

        for (String key : config.getConfigurationSection("messages").getKeys(true)) {
            messages.put(key, ChatColor.translateAlternateColorCodes('&', config.getString("messages." + key).replace("\\n", "\n")));
        }

    }
}
