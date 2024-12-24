package fr.iban.bungeeeconomy.economy;

import fr.iban.bukkitcore.CoreBukkitPlugin;
import fr.iban.bungeeeconomy.BungeeEconomyPlugin;
import fr.iban.bungeeeconomy.baltop.Baltop;
import fr.iban.bungeeeconomy.sql.SqlStorage;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EconomyImpl implements Economy {

    private final BungeeEconomyPlugin plugin;

    private final SqlStorage sqlStorage;
    private Map<UUID, Double> balances = new HashMap<>();
    private Baltop baltop;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();


    public EconomyImpl(BungeeEconomyPlugin plugin, SqlStorage sqlStorage) {
        this.plugin = plugin;
        this.sqlStorage = sqlStorage;
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                long start = System.currentTimeMillis();
                balances = sqlStorage.getAllBalances();
                plugin.getLogger().info(balances.size() + " users balances loaded in " + (System.currentTimeMillis() - start) + "ms.");
            }catch (Exception e) {
                plugin.getLogger().info("Error while loading balances.");
                plugin.unHookVault();
                e.printStackTrace();
            }
        });

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            baltop = sqlStorage.getBaltop();
        }, 0, plugin.getConfig().getLong("baltop-update-interval") * 20 * 60);
    }

    @Override
    public boolean isEnabled() {
        return plugin.isEnabled();
    }

    @Override
    public String getName() {
        return "BungeeEconomy";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return -1;
    }

    @Override
    public String format(double amount) {
        Locale locale = new Locale("fr", "FR");
        NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
        numberFormat.setMaximumFractionDigits(2);
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setGroupingSeparator(' ');
        dfs.setDecimalSeparator(',');
        ((DecimalFormat) numberFormat).setDecimalFormatSymbols(dfs);
        return ChatColor.GOLD + numberFormat.format(amount) + currencyNameSingular() + ChatColor.RESET;
    }

    @Override
    public String currencyNamePlural() {
        return currencyNameSingular();
    }

    @Override
    public String currencyNameSingular() {
        return plugin.getConfig().getString("currency-name");
    }

    @Override
    public boolean hasAccount(String playerName) {
        return hasAccount(Bukkit.getOfflinePlayerIfCached(playerName));
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return player != null && balances.containsKey(player.getUniqueId());
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return hasAccount(playerName);
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        return hasAccount(player);
    }

    @Override
    public double getBalance(String playerName) {
        return getBalance(Bukkit.getOfflinePlayer(playerName));
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        if (hasAccount(player)) {
            return balances.get(player.getUniqueId());
        }
        return 0;
    }

    @Override
    public double getBalance(String playerName, String world) {
        return getBalance(playerName);
    }

    @Override
    public double getBalance(OfflinePlayer player, String world) {
        return getBalance(player);
    }

    @Override
    public boolean has(String playerName, double amount) {
        return has(Bukkit.getOfflinePlayer(playerName), amount);
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return getBalance(player) >= amount;
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return has(playerName, amount);
    }

    @Override
    public boolean has(OfflinePlayer player, String worldName, double amount) {
        return has(player, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        return withdrawPlayer(Bukkit.getOfflinePlayer(playerName), amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        if (player == null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player cannot be null!");
        }
        if (amount < 0) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative funds!");
        }
        if (!hasAccount(player)) {
            createPlayerAccount(player);
        }

        UUID uuid = player.getUniqueId();
        balances.put(uuid, getBalance(player) - amount);

        runAsyncQueued(() -> {
            sqlStorage.updateBalance(uuid, getBalance(player));
            syncBalance(uuid);
            sqlStorage.addTransactionLog(uuid, (amount * -1));
        });

        return new EconomyResponse(0, getBalance(player), EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return withdrawPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        return withdrawPlayer(player, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        return depositPlayer(Bukkit.getOfflinePlayer(playerName), amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {

        if (player == null) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Player can not be null.");
        }

        if (amount < 0) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE, "Cannot deposit negative funds");
        }

        if (!hasAccount(player)) {
            createPlayerAccount(player);
        }

        UUID uuid = player.getUniqueId();
        balances.put(uuid, getBalance(player) + amount);

        runAsyncQueued(() -> {
            sqlStorage.updateBalance(uuid, getBalance(player));
            syncBalance(uuid);
            sqlStorage.addTransactionLog(uuid, amount);
        });


        return new EconomyResponse(0, getBalance(player), EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return depositPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        return depositPlayer(player, amount);
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return null;
    }

    @Override
    public EconomyResponse createBank(String name, OfflinePlayer player) {
        return null;
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return null;
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return null;
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String name, OfflinePlayer player) {
        return null;
    }

    @Override
    public List<String> getBanks() {
        return null;
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        return createPlayerAccount(Bukkit.getOfflinePlayer(playerName));
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        if (!hasAccount(player)) {
            double balance = plugin.getConfig().getDouble("starting-balance");
            balances.put(player.getUniqueId(), balance);
            depositPlayer(player, balance);
            return true;
        }
        return false;
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        return createPlayerAccount(playerName);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
        return createPlayerAccount(player);
    }

    public Baltop getBaltop() {
        return baltop;
    }

    /**
     * Syncs player's balance on connected servers.
     */
    private void syncBalance(UUID uuid) {
        CoreBukkitPlugin core = CoreBukkitPlugin.getInstance();
        core.getMessagingManager().sendMessage(plugin.SYNC_CHANNEL, uuid.toString());
    }

    public void updateBalanceFromDb(UUID uuid) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            double balance = sqlStorage.getBalance(uuid);
            balances.put(uuid, balance);
        });
    }

    public void runAsyncQueued(Runnable runnable) {
        executor.execute(runnable);
    }

    public ExecutorService getExecutor() {
        return executor;
    }
}
