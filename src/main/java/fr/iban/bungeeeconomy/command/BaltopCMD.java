package fr.iban.bungeeeconomy.command;

import fr.iban.bungeeeconomy.baltop.BaltopPlayer;
import fr.iban.bungeeeconomy.BungeeEconomyPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;

public class BaltopCMD implements CommandExecutor {

    private BungeeEconomyPlugin plugin;

    public BaltopCMD(BungeeEconomyPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        plugin.getEconomy().getBaltop().thenAccept(baltop -> {
            int page = 1;
            int maxpages = (int) Math.ceil(baltop.getBaltopPlayers().size() / 10D);

            if (args.length == 1) {
                try {
                    page = Integer.parseInt(args[0]);
                    if (page > maxpages) {
                        sender.sendMessage("§cLe classement va jusqu'à la page " + maxpages);
                        return;
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cLa page doit être un nombre !");
                }
            }

            int startPos = (page - 1) * 10;
            int endPos = startPos + 10;

            sender.sendMessage(getCentered("§6§lClassement d'argent (§f§l" + page + "§6§l/§f§l " + maxpages + "§6§l)", 54));
            for (int i = startPos; i < endPos && i < baltop.getBaltopPlayers().size(); i++) {
                BaltopPlayer baltopPlayer = baltop.getBaltopPlayers().get(i);
                sender.sendMessage("§6§l" + (i+1) + " §f§l " + baltopPlayer.getName() + " §e - §f" + plugin.getEconomy().format(baltopPlayer.getBalance()));
            }
            sender.sendMessage("§7Dernière mise à jour à " + new SimpleDateFormat("HH:mm").format(baltop.getUpdatedAt()));
            sender.sendMessage(getLine(40));

        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });

        return false;
    }

    private String getLine(int length) {
        StringBuilder sb = new StringBuilder("§6§m");
        for (int i = 0; i < length; i++) {
            sb.append("-");
        }
        return sb.toString();
    }

    private String getCentered(String string, int lineLength) {
        StringBuilder sb = new StringBuilder("§6§m");
        int line = (lineLength - string.length()) / 2 + 2;
        sb.append("-".repeat(Math.max(0, line)));
        sb.append("§f ").append(string).append(" ");
        sb.append("§6§m");
        sb.append("-".repeat(Math.max(0, line)));
        return sb.toString();
    }


}
