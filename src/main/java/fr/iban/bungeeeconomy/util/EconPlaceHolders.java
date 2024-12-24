package fr.iban.bungeeeconomy.util;

import fr.iban.bungeeeconomy.BungeeEconomyPlugin;
import fr.iban.bungeeeconomy.baltop.Baltop;
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
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
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

        Baltop baltop = plugin.getEconomy().getBaltop();

        for (int i = 0; i < baltop.getBaltopPlayers().size(); i++) {
            if(identifier.equalsIgnoreCase("top_" + (i+1) + "_name")){
                return baltop.getBaltopPlayers().get(i).getName();
            }

            if(identifier.equalsIgnoreCase("top_" + (i+1) + "_balance")){
                return plugin.getEconomy().format(baltop.getBaltopPlayers().get(i).getBalance());
            }
        }

        return null;
    }
}
