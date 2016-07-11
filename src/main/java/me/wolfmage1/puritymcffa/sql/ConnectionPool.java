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
