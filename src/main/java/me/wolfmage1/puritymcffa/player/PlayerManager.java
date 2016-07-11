package me.wolfmage1.puritymcffa.player;

import me.wolfmage1.puritymcffa.FFA;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static me.wolfmage1.puritymcffa.util.InventoryToBase64.fromBase64;

/*******************************************************************************
 * Copyright MartinItsLinda (c) 2016. All Rights Reserved.
 * Any code contained within this document, and any associated API's with similar branding
 * are the sole property of MartinItsLinda. Distribution, reproduction, taking snippets or
 * claiming any contents as your own will break the terms of the liscense and void any
 * agreements with you, the third party.
 ******************************************************************************/

public class PlayerManager {

    private static Map<UUID, FFAPlayer> players = new HashMap<>();
    private static FFA plugin = FFA.getInstance();

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
            ResultSet result = null;

            try {
                connection = plugin.getConnection();

                String query = "SELECT * FROM ffa_stats WHERE unique_id = ?;";

                statement = connection.prepareStatement(query);
                statement.setString(1, uuid.toString());
                result = statement.executeQuery();

                boolean found = false;

                while (result.next()) {
                    if (found) {
                        throw new SQLException("Found multiple results for " + uuid + ".");
                    }

                    found = true;

                    player.setId(result.getInt("player_id"));
                    player.setPoints(result.getInt("points"));
                    player.setKills(result.getInt("kills"));
                    player.setDeaths(result.getInt("deaths"));

                    if (player.getBukkitPlayer().hasPermission("ffa.saveinventory") &&
                            result.getString("inventoryContents") != null) {
                        try {
                            player.setInventoryContents(fromBase64(result.getString("inventoryContents")).getContents());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }

                if (!(found)) {



                }

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (result != null) {
                    try {
                        result.close();
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
