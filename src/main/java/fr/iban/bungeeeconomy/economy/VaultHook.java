package fr.iban.bungeeeconomy.economy;

import fr.iban.bungeeeconomy.BungeeEconomyPlugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;

public class VaultHook {

    private BungeeEconomyPlugin plugin;
    private Economy provider;

    public VaultHook(BungeeEconomyPlugin plugin) {
        this.plugin = plugin;
    }

    public void hook() {
        provider = plugin.getEconomy();
        Bukkit.getServicesManager().register(Economy.class, this.provider, this.plugin, ServicePriority.High);
        plugin.getLogger().info("VaultAPI hooked into " + plugin.getName());
    }

    public void unhook() {
        Bukkit.getServicesManager().unregister(Economy.class, provider);
        plugin.getLogger().info("VaultAPI unhooked from " + plugin.getName());
    }

}
