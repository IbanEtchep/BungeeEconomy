package fr.iban.bungeeeconomy;

import fr.iban.bungeeeconomy.command.*;
import fr.iban.bungeeeconomy.economy.EconomyImpl;
import fr.iban.bungeeeconomy.economy.VaultHook;
import fr.iban.bungeeeconomy.listener.BalanceSyncListener;
import fr.iban.bungeeeconomy.listener.QuickShopListeners;
import fr.iban.bungeeeconomy.listener.ZAhListeners;
import fr.iban.bungeeeconomy.pricelimit.PriceLimitManager;
import fr.iban.bungeeeconomy.sql.SqlStorage;
import fr.iban.bungeeeconomy.sql.SqlTables;
import fr.iban.bungeeeconomy.util.EconPlaceHolders;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.maxgamer.quickshop.api.QuickShopAPI;

public final class BungeeEconomyPlugin extends JavaPlugin {

    private EconomyImpl economy;
    private PriceLimitManager priceLimitManager;
    private VaultHook vaultHook;
    private QuickShopAPI quickShopAPI;
    public final String SYNC_CHANNEL = "SyncBalance";

    @Override
    public void onEnable() {
        saveDefaultConfig();
        SqlTables.createTables();
        SqlStorage sqlStorage = new SqlStorage(this);
        economy = new EconomyImpl(this, sqlStorage);
        vaultHook = new VaultHook(this);
        vaultHook.hook();

        this.priceLimitManager = new PriceLimitManager(this, sqlStorage);

        getCommand("balance").setExecutor(new BalanceCMD(this));
        getCommand("baltop").setExecutor(new BaltopCMD(this));
        getCommand("bungeeeconomy").setExecutor(new BungeeEconomyCMD(this));
        getCommand("pay").setExecutor(new PayCommand(this));
        getCommand("pricelimit").setExecutor(new PriceLimitCMD(this));
        getCommand("pricelimit").setTabCompleter(new PriceLimitCMD(this));

        Plugin quickshopPlugin = Bukkit.getPluginManager().getPlugin("QuickShop");
        if(quickshopPlugin != null && quickshopPlugin.isEnabled()){
            quickShopAPI = (QuickShopAPI)quickshopPlugin;
        }
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new EconPlaceHolders(this).register();
        }

        registerListeners(
                new ZAhListeners(this),
                new QuickShopListeners(this),
                new BalanceSyncListener(this)
        );
    }

    @Override
    public void onDisable() {
        vaultHook.unhook();
        economy.getExecutor().shutdown();
    }

    private void registerListeners(Listener... listeners) {

        PluginManager pm = Bukkit.getPluginManager();

        for (Listener listener : listeners) {
            pm.registerEvents(listener, this);
        }

    }


    public EconomyImpl getEconomy() {
        return economy;
    }

    public PriceLimitManager getPriceLimitManager() {
        return priceLimitManager;
    }

}
