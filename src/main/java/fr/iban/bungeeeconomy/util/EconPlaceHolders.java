package fr.iban.bungeeeconomy.util;

import fr.iban.bungeeeconomy.BungeeEconomyPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EconPlaceHolders extends PlaceholderExpansion {

    private final BungeeEconomyPlugin plugin;

    public EconPlaceHolders(BungeeEconomyPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "bungeeeconomy";
    }

    @Override
    public @NotNull String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {

        if (player == null) {
            return "";
        }

        if (identifier.equals("balance")) {
            return plugin.getEconomy().format(plugin.getEconomy().getBalance(player));
        }

        return null;
    }
}
