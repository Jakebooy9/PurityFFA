/*
* This file is part of PurityMCFFA
*
* PurityMCFFA is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* PurityMCFFA is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with PurityMCFFA. If not, see <http://www.gnu.org/licenses/>
*/
package me.wolfmage1.puritymcffa.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

public class Message {

    private static Map<String, String> messages = new HashMap<>();
    private String message = null;

    private Message(String key) {
        message = messages.get(key);
    }

    public Message replace(CharSequence oldChar, CharSequence newChar) {
        message = message.replace(oldChar, newChar);
        return this;
    }

    public Message replace(CharSequence oldChar, Integer newChar) {
        message = message.replace(oldChar, Integer.toString(newChar));
        return this;
    }

    public Message replace(CharSequence oldChar, Double newChar) {
        message = message.replace(oldChar, Double.toString(newChar));
        return this;
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
        sender.sendMessage(getString("prefix") + " " + message);
    }

    public String toString() {
        return this.message;
    }

    public static Message get(String key) {
        return new Message(key);
    }

    public static String getString(String key) {
        return messages.get(key);
    }

    public static void load(FileConfiguration config) {
        messages.clear();

        for (String key : config.getConfigurationSection("messages").getKeys(true)) {
            messages.put(key, ChatColor.translateAlternateColorCodes('&', config.getString("messages." + key).replace("\\n", "\n")));
        }

    }
}
