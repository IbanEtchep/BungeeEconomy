package fr.iban.bungeeeconomy.sql;

import fr.iban.common.data.sql.DbAccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SqlTables {

    public static void createTables() {
        createTable("CREATE TABLE IF NOT EXISTS bungeeecon_balances(" +
                "   id INTEGER PRIMARY KEY AUTO_INCREMENT," +
                "   uuid VARCHAR(36) UNIQUE," +
                "   balance DOUBLE" +
                ");");

        createTable("CREATE TABLE IF NOT EXISTS bungeeecon_transactions(" +
                "   id BIGINT PRIMARY KEY AUTO_INCREMENT," +
                "   balance_id INTEGER," +
				"   amount DOUBLE," +
				"   createdAt DATETIME DEFAULT CURRENT_TIMESTAMP," +
				"   FOREIGN KEY (balance_id) REFERENCES bungeeecon_balances(id)" +
                ");");

        createTable("CREATE TABLE IF NOT EXISTS bungeeecon_price_limits(" +
                "   id INTEGER PRIMARY KEY AUTO_INCREMENT," +
                "   limit_key VARCHAR(255) UNIQUE," +
                "   max DOUBLE DEFAULT 0," +
                "   min DOUBLE DEFAULT 0" +
                ");");
    }

    private static void createTable(String statement) {
        try (Connection connection = DbAccess.getDataSource().getConnection()) {
            try (PreparedStatement preparedStatemente = connection.prepareStatement(statement)) {
                preparedStatemente.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
