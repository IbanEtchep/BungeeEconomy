package fr.iban.bungeeeconomy.sql;

import fr.iban.bungeeeconomy.BungeeEconomyPlugin;
import fr.iban.bungeeeconomy.baltop.Baltop;
import fr.iban.bungeeeconomy.baltop.BaltopPlayer;
import fr.iban.bungeeeconomy.pricelimit.PriceLimit;
import fr.iban.common.data.sql.DbAccess;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class SqlStorage {

    /*
    Tables :

    bungeeecon_money (_id_, uuid, balance)
    bungeeecon_transactions(#_player_id_, amount, createdAt)

     */
    private BungeeEconomyPlugin plugin;
    private DataSource ds = DbAccess.getDataSource();

    public SqlStorage(BungeeEconomyPlugin plugin) {
        this.plugin = plugin;
    }

    public Map<UUID, Double> getAllBalances() {
        String sql = "SELECT uuid, balance FROM bungeeecon_balances;";
        Map<UUID, Double> balances = new HashMap<>();

        try (Connection connection = ds.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    UUID uuid = UUID.fromString(rs.getString("uuid"));
                    double balance = rs.getDouble("balance");
                    balances.put(uuid, balance);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return balances;
    }

    public double getBalance(UUID uuid) {
        String sql = "SELECT balance FROM bungeeecon_balances WHERE uuid=?;";

        try (Connection connection = ds.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, uuid.toString());
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return rs.getDouble("balance");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public void updateBalance(UUID uuid, double newBalace) {
        String sql = "INSERT INTO bungeeecon_balances (uuid, balance) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE balance=VALUES(balance);";

        try (Connection connection = ds.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, uuid.toString());
                ps.setDouble(2, newBalace);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Baltop getBaltop() {
        String sql = "SELECT uuid, balance FROM bungeeecon_balances ORDER BY balance DESC LIMIT ?;";
        List<BaltopPlayer> baltopPlayerList = new ArrayList<>();

        try (Connection connection = ds.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, plugin.getConfig().getInt("baltop-max-players"));
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    UUID uuid = UUID.fromString(rs.getString("uuid"));
                    double balance = rs.getDouble("balance");
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                    baltopPlayerList.add(new BaltopPlayer(offlinePlayer.getName(), balance));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new Baltop(baltopPlayerList);
    }

    public void addTransactionLog(UUID uuid, double amount) {
        String sql = "INSERT INTO bungeeecon_transactions (balance_id, amount)  VALUES ( (SELECT id FROM bungeeecon_balances WHERE uuid=?), ?);";

        try (Connection connection = ds.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, uuid.toString());
                ps.setDouble(2, amount);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<String, PriceLimit> getPriceLimits() {
        String sql = "SELECT limit_key, max, min FROM bungeeecon_price_limits;";
        Map<String, PriceLimit> limits = new HashMap<>();

        try (Connection connection = ds.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    String key = rs.getString("limit_key");
                    double max = rs.getDouble("max");
                    double min = rs.getDouble("min");
                    limits.put(key, new PriceLimit(min, max));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return limits;
    }

    public void updatePriceLimit(String key, double min, double max) {
        String sql = "INSERT INTO bungeeecon_price_limits (limit_key, min, max) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE min=VALUES(min), max=VALUES(max);";

        if (min == 0 && max == 0) {
            removePriceLimit(key);
            return;
        }

        try (Connection connection = ds.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, key);
                ps.setDouble(2, min);
                ps.setDouble(3, max);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removePriceLimit(String key) {
        String sql = "DELETE FROM bungeeecon_price_limits WHERE limit_key=?";

        try (Connection connection = ds.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, key);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
