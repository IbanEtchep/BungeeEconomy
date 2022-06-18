package fr.iban.bungeeeconomy.command;

import fr.iban.bungeeeconomy.BungeeEconomyPlugin;
import fr.iban.bungeeeconomy.pricelimit.PriceLimit;
import fr.iban.bungeeeconomy.pricelimit.PriceLimitManager;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class PriceLimitCMD implements CommandExecutor, TabCompleter {

    private final PriceLimitManager priceLimitManager;

    public PriceLimitCMD(BungeeEconomyPlugin plugin) {
        this.priceLimitManager = plugin.getPriceLimitManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        // pricelimit edit enchantName/materialName min max
        // pricelimit list
        // pricelimit reload
        if (sender.hasPermission("bungeeeconomy.admin")) {

            if (args.length >= 1) {

                switch (args[0].toLowerCase()) {

                    case "reload" -> {
                        priceLimitManager.loadPriceLimits();
                        sender.sendMessage("§aPrice limits reloaded.");
                    }

                    case "list" -> {
                        sender.sendMessage("§lVoici les limites de prix :");
                        for (Map.Entry<Material, PriceLimit> entry : priceLimitManager.getMaterialsPriceLimits().entrySet()) {
                            sender.sendMessage("- " + entry.getKey().toString() + " : "
                                    + " min: " + entry.getValue().getMin()
                                    + " max: " + entry.getValue().getMax());
                        }
                        for (Map.Entry<Enchantment, PriceLimit> entry : priceLimitManager.getEnchantsPriceLimits().entrySet()) {
                            sender.sendMessage("- " + entry.getKey().getKey().getKey() + " : "
                                    + " min: " + entry.getValue().getMin()
                                    + " max: " + entry.getValue().getMax());
                        }
                    }

                    case "edit" -> {
                        if(args.length == 4) {
                            double min = 0;
                            double max = 0;
                            try {
                                min = Double.parseDouble(args[2]);
                                max = Double.parseDouble(args[3]);
                            }catch (NumberFormatException e) {
                                sender.sendMessage("Le min et max doivent être des nombres.");
                                return false;
                            }

                            String key = args[1];

                            if(priceLimitManager.getValidKeys().contains(key)) {
                                priceLimitManager.setPriceLimit(key, min, max);
                                sender.sendMessage("§aLimite de prix ajoutée.");
                            }

                        }
                    }

                    default -> {
                        sender.sendMessage("/pricelimit edit enchantName/materialName min max");
                        sender.sendMessage("mettre 0 et 0 pour retirer la limite. 0 = pas de limite");
                        sender.sendMessage("/pricelimit reload");

                    }
                }
            } else {
                sender.sendMessage("/pricelimit help");
            }

        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 2) {
            return priceLimitManager.getValidKeys().stream().filter(key -> key.startsWith(args[1])).toList();
        }
        return null;
    }
}
