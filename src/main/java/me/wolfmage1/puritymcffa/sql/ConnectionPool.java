
package me.wolfmage1.puritymcffa.sql;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionPool {

    private HikariDataSource source;

    public ConnectionPool(String url, String user, String password) {
        source = new HikariDataSource();

        source.setJdbcUrl(url);
        source.setUsername(user);
        source.setPassword(password);
    }

    public HikariDataSource getSource() {
        return source;
    }

    public Connection getConnection() throws SQLException {
        return source.getConnection();
    }

}
