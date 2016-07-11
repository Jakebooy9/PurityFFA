package com.puritymc.purityffa.listener;

import com.puritymc.purityffa.PurityFFA;
import com.puritymc.purityffa.player.PlayerManager;
import com.puritymc.purityffa.util.Message;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/*******************************************************************************
 * Copyright MartinItsLinda (c) 2016. All Rights Reserved.
 * Any code contained within this document, and any associated API's with similar branding
 * are the sole property of MartinItsLinda. Distribution, reproduction, taking snippets or
 * claiming any contents as your own will break the terms of the license and void any
 * agreements with you, the third party.
 ******************************************************************************/

public class ChatEvent implements Listener {

    private PurityFFA plugin;

    public ChatEvent(PurityFFA plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        String prefix = plugin.getChat()
                .getGroupPrefix(event.getPlayer().getWorld(),
                        plugin.getChat().getPrimaryGroup(event.getPlayer()));

        event.setFormat(Message.get("chat_format")
                .replace("%points%", Integer.toString(PlayerManager.getPlayer(event.getPlayer()).getPoints()))
                .replace("%name%", (prefix == null ? "" : prefix) + event.getPlayer().getName())
                .replace("%message%", event.getMessage()).colorize());
    }

}
