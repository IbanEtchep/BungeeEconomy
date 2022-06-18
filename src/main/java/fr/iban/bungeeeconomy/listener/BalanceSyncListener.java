package fr.iban.bungeeeconomy.listener;

import fr.iban.bukkitcore.event.CoreMessageEvent;
import fr.iban.bungeeeconomy.BungeeEconomyPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class BalanceSyncListener implements Listener {

    private final BungeeEconomyPlugin plugin;

    public BalanceSyncListener(BungeeEconomyPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMessage(CoreMessageEvent e) {
        if (!e.getMessage().getChannel().equals(plugin.SYNC_CHANNEL)) {
            return;
        }

        UUID balanceOwner = UUID.fromString(e.getMessage().getMessage());
        plugin.getEconomy().updateBalanceFromDb(balanceOwner);
    }

}
