package fr.iban.bungeeeconomy.command;

import fr.iban.bungeeeconomy.BungeeEconomyPlugin;
import fr.iban.bungeeeconomy.economy.EconomyImpl;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class BungeeEconomyCMD implements CommandExecutor, TabCompleter {

    private BungeeEconomyPlugin plugin;
    private EconomyImpl economy;

    public BungeeEconomyCMD(BungeeEconomyPlugin plugin) {
        this.plugin = plugin;
        economy = plugin.getEconomy();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (sender.hasPermission("bungeeeconomy.admin")) {
            if (args.length == 3) {

                double amount = 0;
                OfflinePlayer target = Bukkit.getOfflinePlayerIfCached(args[1]);

                if (target == null) {
                    sender.sendMessage("§cCe joueur n'a jamais joué.");
                    return false;
                }

                try {
                    amount = Double.parseDouble(args[2]);
                } catch (NumberFormatException e) {

                }

                switch (args[0].toLowerCase()) {
                    case "give" -> {
                        economy.depositPlayer(target, amount);
                        sender.sendMessage("§a" + economy.format(amount) + " ont été donnés à " + target.getName() + ".");

                        if (target.isOnline()) {
                            target.getPlayer().sendMessage("§a" + economy.format(amount) + " ont été ajoutés à votre solde.");
                        }
                    }
                    case "take" -> {
                        EconomyResponse response = economy.withdrawPlayer(target, amount);

                        if (response.transactionSuccess()) {
                            sender.sendMessage("§c" + economy.format(amount) + " ont été retirés à " + target.getName() + ".");

                            if (target.isOnline()) {
                                target.getPlayer().sendMessage("§c" + economy.format(amount) + " ont été retirés de votre solde.");
                            }
                        } else {
                            sender.sendMessage(response.errorMessage);
                        }
                    }
                    case "set" -> {
                        economy.withdrawPlayer(target, economy.getBalance(target));
                        economy.depositPlayer(target, amount);
                        sender.sendMessage("§cLe solde de " + target.getName() + " a été redéfini à " + economy.format(amount) + ".");

                        if (target.isOnline()) {
                            target.getPlayer().sendMessage("§cVotre solde a été redéfini à " + economy.format(amount) + ".");
                        }
                    }

                    default -> {
                        sender.sendMessage("§7/bungeeeconomy give/take/set <player> <amount>");
                    }
                }
            }
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length == 1) {
            return Arrays.asList("give", "take", "set");
        }

        return null;
    }
}
