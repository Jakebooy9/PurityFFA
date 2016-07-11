package com.puritymc.purityffa.player;

import com.puritymc.purityffa.PurityFFA;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static com.puritymc.purityffa.util.InventoryToBase64.fromBase64;

/*******************************************************************************
 * Copyright MartinItsLinda (c) 2016. All Rights Reserved.
 * Any code contained within this document, and any associated API's with similar branding
 * are the sole property of MartinItsLinda. Distribution, reproduction, taking snippets or
 * claiming any contents as your own will break the terms of the liscense and void any
 * agreements with you, the third party.
 ******************************************************************************/

public class PlayerManager {

    private static Map<UUID, FFAPlayer> players = new HashMap<>();
    private static PurityFFA plugin = PurityFFA.getInstance();

    public static Map<UUID, FFAPlayer> getPlayers() {
        return players;
    }

    public static FFAPlayer getPlayer(final Player pl) {
        return getPlayer(pl.getUniqueId());
    }

    public static FFAPlayer getPlayer(final UUID uuid) {
        if (players.get(uuid) != null) {
            return players.get(uuid);
        }

        Player pl = Bukkit.getPlayer(uuid);
        FFAPlayer player = new FFAPlayer(pl);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, ()-> {

            Connection connection = null;
            PreparedStatement statement = null;
            ResultSet set = null;

            try {
                connection = plugin.getConnection();

                String query = "SELECT * FROM ffa_stats WHERE unique_id = ?;";

                statement = connection.prepareStatement(query);
                statement.setString(1, uuid.toString());

                set = statement.executeQuery();

                boolean found = false;

                while (set.next()) {
                    if (found) {
                        throw new SQLException("Found multiple results for " + uuid + ".");
                    }

                    found = true;

                    player.setId(set.getInt("player_id"));
                    player.setPoints(set.getInt("points"));
                    player.setKills(set.getInt("kills"));
                    player.setDeaths(set.getInt("deaths"));

                    if (player.getBukkitPlayer().hasPermission("ffa.saveinventory") &&
                            set.getString("inventoryContents") != null) {
                        try {
                            player.setInventoryContents(fromBase64(set.getString("inventoryContents")).getContents());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }

                if (!(found)) {
                    query = "INSERT INTO ffa_stats (unique_id, username) VALUES (?, ?);";

                    statement = connection.prepareStatement(query);

                    statement.setString(1, uuid.toString());
                    statement.setString(2, pl.getName());

                    statement.executeUpdate();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (set != null) {
                    try {
                        set.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        players.put(uuid, player);

        return player;
    }

}
