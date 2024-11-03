package fr.iban.bungeeeconomy;

import fr.iban.bukkitcore.CoreBukkitPlugin;
import fr.iban.bungeeeconomy.command.*;
import fr.iban.bungeeeconomy.economy.EconomyImpl;
import fr.iban.bungeeeconomy.economy.VaultHook;
import fr.iban.bungeeeconomy.listener.AxAuctionsListener;
import fr.iban.bungeeeconomy.listener.BalanceSyncListener;
import fr.iban.bungeeeconomy.listener.QuickShopListeners;
import fr.iban.bungeeeconomy.listener.ZAuctionHouseListener;
import fr.iban.bungeeeconomy.pricelimit.PriceLimitManager;
import fr.iban.bungeeeconomy.sql.SqlStorage;
import fr.iban.bungeeeconomy.sql.SqlTables;
import fr.iban.bungeeeconomy.util.EconPlaceHolders;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.bukkit.BukkitCommandHandler;

public final class BungeeEconomyPlugin extends JavaPlugin {

    private EconomyImpl economy;
    private PriceLimitManager priceLimitManager;
    private VaultHook vaultHook;
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
        getCommand("bungeeeconomy").setExecutor(new BungeeEconomyCMD(this));
        getCommand("pay").setExecutor(new PayCommand(this));
        getCommand("pricelimit").setExecutor(new PriceLimitCMD(this));
        getCommand("pricelimit").setTabCompleter(new PriceLimitCMD(this));

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new EconPlaceHolders(this).register();
        }

        if(Bukkit.getPluginManager().getPlugin("QuickShop-Hikari") != null){
            getLogger().info("QuickShop hooked.");
            registerListeners(new QuickShopListeners(this));
        }

        if(Bukkit.getPluginManager().getPlugin("ZAuctionHouse") != null){
            getLogger().info("ZAuctionHouse hooked.");
            registerListeners(new ZAuctionHouseListener(this));
        }

        if (Bukkit.getPluginManager().getPlugin("AxAuctions") != null) {
            getLogger().info("AxAuctions hooked.");
            registerListeners(new AxAuctionsListener(this));
        }

        registerListeners(new BalanceSyncListener(this));

        registerCommands();
    }

    @Override
    public void onDisable() {
        unHookVault();
        economy.getExecutor().shutdown();
    }

    public void unHookVault() {
        vaultHook.unhook();
    }

    private void registerListeners(Listener... listeners) {

        PluginManager pm = Bukkit.getPluginManager();

        for (Listener listener : listeners) {
            pm.registerEvents(listener, this);
        }

    }

    private void registerCommands() {
        BukkitCommandHandler commandHandler = BukkitCommandHandler.create(this);
        commandHandler.accept(CoreBukkitPlugin.getInstance().getCommandHandlerVisitor());

        commandHandler.register(new BaltopCMD(this));
        commandHandler.registerBrigadier();
    }


    public EconomyImpl getEconomy() {
        return economy;
    }

    public PriceLimitManager getPriceLimitManager() {
        return priceLimitManager;
    }

}
