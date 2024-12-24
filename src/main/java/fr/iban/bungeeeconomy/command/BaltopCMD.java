package fr.iban.bungeeeconomy.command;

import fr.iban.bungeeeconomy.baltop.BaltopPlayer;
import fr.iban.bungeeeconomy.BungeeEconomyPlugin;
import revxrsal.commands.annotation.*;
import revxrsal.commands.command.CommandActor;

import java.text.SimpleDateFormat;

public class BaltopCMD {

    private final BungeeEconomyPlugin plugin;

    public BaltopCMD(BungeeEconomyPlugin plugin) {
        this.plugin = plugin;
    }

    @Command({"baltop", "balancetop", "moneytop"})
    public void baltop(CommandActor sender, @Default("1") @Range(min = 1) @Named("page") int selectedPage) {
        var baltop = plugin.getEconomy().getBaltop();
        int maxpages = (int) Math.ceil(baltop.getBaltopPlayers().size() / 10D);

        if (selectedPage > maxpages) {
            sender.reply("§cLe classement va jusqu'à la page " + maxpages);
            return;
        }

        int startPos = (selectedPage - 1) * 10;
        int endPos = startPos + 10;

        sender.reply(getCentered("§6§lClassement d'argent (§f§l" + selectedPage + "§6§l/§f§l " + maxpages + "§6§l)"));
        for (int i = startPos; i < endPos && i < baltop.getBaltopPlayers().size(); i++) {
            BaltopPlayer baltopPlayer = baltop.getBaltopPlayers().get(i);
            sender.reply("§6§l" + (i + 1) + " §f§l " + baltopPlayer.getName() + " §e - §f" + plugin.getEconomy().format(baltopPlayer.getBalance()));
        }
        sender.reply("§7Dernière mise à jour à " + new SimpleDateFormat("HH:mm").format(baltop.getUpdatedAt()));
        sender.reply(getLine());
    }

    private String getLine() {
        return "§6§m" + "-".repeat(40);
    }

    private String getCentered(String string) {
        StringBuilder sb = new StringBuilder("§6§m");
        int line = (54 - string.length()) / 2 + 2;
        sb.append("-".repeat(Math.max(0, line)));
        sb.append("§f ").append(string).append(" ");
        sb.append("§6§m");
        sb.append("-".repeat(Math.max(0, line)));
        return sb.toString();
    }


}
