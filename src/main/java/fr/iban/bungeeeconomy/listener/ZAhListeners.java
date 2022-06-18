package fr.iban.bungeeeconomy.listener;

import fr.iban.bungeeeconomy.BungeeEconomyPlugin;
import fr.iban.bungeeeconomy.pricelimit.PriceLimit;
import fr.iban.bungeeeconomy.pricelimit.PriceLimitManager;
import fr.maxlego08.zauctionhouse.api.event.events.AuctionPreSellEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.maxgamer.quickshop.api.event.ShopCreateEvent;
import org.maxgamer.quickshop.api.event.ShopPriceChangeEvent;
import org.maxgamer.quickshop.api.shop.Shop;

public class ZAhListeners implements Listener {

    private final PriceLimitManager priceLimitManager;

    public ZAhListeners(@NotNull BungeeEconomyPlugin plugin) {
        this.priceLimitManager = plugin.getPriceLimitManager();
    }

    @EventHandler
    public void onShopCreate(AuctionPreSellEvent e) {
        long price = e.getPrice()/e.getAmount();
        PriceLimit priceLimit = priceLimitManager.getPriceLimit(e.getItemStack());
        if (!priceLimit.isInLimits(price)) {
            e.setCancelled(true);
            Player creator = e.getPlayer();
            if (creator != null) {
                creator.sendMessage(priceLimitManager.getLimitMessage(priceLimit));
            }
        }
    }

}
