package me.wolfmage1.puritymcffa.listener;

import me.wolfmage1.puritymcffa.FFA;
import me.wolfmage1.puritymcffa.player.PlayerManager;
import me.wolfmage1.puritymcffa.util.Message;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/*******************************************************************************
 * Copyright MartinItsLinda (c) 2016. All Rights Reserved.
 * Any code contained within this document, and any associated API's with similar branding
 * are the sole property of MartinItsLinda. Distribution, reproduction, taking snippets or
 * claiming any contents as your own will break the terms of the liscense and void any
 * agreements with you, the third party.
 ******************************************************************************/

public class ChatEvent implements Listener {

    private FFA plugin;

    public ChatEvent(FFA plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (plugin.getChat() != null) {
            String prefix = plugin.getChat()
                    .getGroupPrefix(event.getPlayer().getWorld(),
                            plugin.getChat().getPrimaryGroup(event.getPlayer()));

            event.setFormat(Message.get("chat_format")
                    .replace("%points%", Integer.toString(PlayerManager.getPlayer(event.getPlayer()).getPoints()))
                    .replace("%name%", (prefix == null ? "" : prefix) + event.getPlayer().getName())
                    .replace("%message%", event.getMessage()).colorize());
        }
    }

}
