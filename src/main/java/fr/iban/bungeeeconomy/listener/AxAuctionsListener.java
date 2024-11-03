package fr.iban.bungeeeconomy.listener;

import fr.iban.bungeeeconomy.BungeeEconomyPlugin;
import fr.iban.bungeeeconomy.pricelimit.PriceLimit;
import fr.iban.bungeeeconomy.pricelimit.PriceLimitManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Set;

public class AxAuctionsListener implements Listener {

    private final PriceLimitManager priceLimitManager;

    private final Set<String> auctionCommandAliases = Set.of(
            "axah", "ah", "auctionhouse", "axauction", "auction", "axauctionhouse", "hdv"
    );

    public AxAuctionsListener(@NotNull BungeeEconomyPlugin plugin) {
        this.priceLimitManager = plugin.getPriceLimitManager();
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String[] args = event.getMessage().substring(1).split(" ");

        if(args.length < 3) return;

        if(auctionCommandAliases.stream().anyMatch(alias -> args[0].equalsIgnoreCase(alias))
                && args[1].equalsIgnoreCase("sell")
                && isDouble(args[2]))
        {
            Player player = event.getPlayer();
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            PriceLimit priceLimit = priceLimitManager.getPriceLimit(itemInHand);
            double price = Double.parseDouble(args[2]);
            int amount = itemInHand.getAmount();

            if(args.length > 3 && isInteger(args[3])) {
                amount = Integer.parseInt(args[3]);
            }

            if (!priceLimit.isInLimits(price, amount)) {
                event.setCancelled(true);
                player.sendMessage(priceLimitManager.getLimitMessage(priceLimit));
            }
        }
    }

    private boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
