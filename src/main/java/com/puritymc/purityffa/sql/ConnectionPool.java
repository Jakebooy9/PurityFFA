package com.puritymc.purityffa.sql;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/*******************************************************************************
 * Copyright MartinItsLinda (c) 2016. All Rights Reserved.
 * Any code contained within this document, and any associated API's with similar branding
 * are the sole property of MartinItsLinda. Distribution, reproduction, taking snippets or
 * claiming any contents as your own will break the terms of the liscense and void any
 * agreements with you, the third party.
 ******************************************************************************/

public class ConnectionPool {

    private HikariDataSource source;

    public ConnectionPool(String url, String user, String password) {
        source = new HikariDataSource();

        source.setJdbcUrl(url);
        source.setUsername(user);
        source.setPassword(password);
        source.setMaximumPoolSize(20);
        source.setLeakDetectionThreshold(5000);
    }

    public HikariDataSource getSource() {
        return source;
    }

    public Connection getConnection() throws SQLException {
        return source.getConnection();
    }

}
