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

public class BalanceCMD implements CommandExecutor {

    private BungeeEconomyPlugin plugin;
    private EconomyImpl economy;

    public BalanceCMD(BungeeEconomyPlugin plugin) {
        this.plugin = plugin;
        economy = plugin.getEconomy();
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        OfflinePlayer target = null;

        if (args.length == 0) {
            if (sender instanceof Player) {
                target = (OfflinePlayer) sender;
                sender.sendMessage("§aVotre solde : §f§l" + economy.format(economy.getBalance(target)));
            } else {
                sender.sendMessage("§c§/balance <pseudo>");
            }
            return false;
        }

        target = Bukkit.getOfflinePlayerIfCached(args[0]);
        if (target == null) {
            sender.sendMessage("§cLe joueur n'a pas été trouvé.");
            return false;
        }


        sender.sendMessage("§aSolde de " + target.getName() + " : §f§l" + economy.format(economy.getBalance(target)));

        return false;
    }
}
