package fr.iban.bungeeeconomy.command;

import fr.iban.bungeeeconomy.BungeeEconomyPlugin;
import fr.iban.bungeeeconomy.economy.EconomyImpl;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PayCommand implements CommandExecutor {

    private BungeeEconomyPlugin plugin;
    private EconomyImpl economy;

    public PayCommand(BungeeEconomyPlugin plugin) {
        this.plugin = plugin;
        this.economy = plugin.getEconomy();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (sender instanceof Player) {

            Player player = (Player) sender;

            if (args.length == 2) {
                OfflinePlayer target = Bukkit.getOfflinePlayerIfCached(args[0]);

                if (target != null) {
                    try {
                        double amount = Double.parseDouble(args[1]);

                        if (economy.has(player, amount)) {
                            economy.withdrawPlayer(player, amount);
                            economy.depositPlayer(target, amount);
                            player.sendMessage("§aVous avez envoyé " + economy.format(amount) + " à " + target.getName());
                            if (target.getPlayer() != null) {
                                target.getPlayer().sendMessage("§aVous avez reçu " + economy.format(amount) + " de " + player.getName());
                            }
                        } else {
                            player.sendMessage("§cVous n'avez pas assez d'argent !");
                        }
                    } catch (NumberFormatException e) {
                        sender.sendMessage("§cLe montant doit être un nombre !");
                    }
                } else {
                    player.sendMessage("§cLe joueur n'a pas été trouvé.");
                }
            } else {
                sender.sendMessage("§f/pay <pseudo> <montant>");
            }

        }

        return false;
    }
}
