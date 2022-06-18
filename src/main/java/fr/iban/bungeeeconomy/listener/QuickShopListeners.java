package fr.iban.bungeeeconomy.listener;

import fr.iban.bungeeeconomy.BungeeEconomyPlugin;
import fr.iban.bungeeeconomy.pricelimit.PriceLimit;
import fr.iban.bungeeeconomy.pricelimit.PriceLimitManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.maxgamer.quickshop.api.event.ShopCreateEvent;
import org.maxgamer.quickshop.api.event.ShopLoadEvent;
import org.maxgamer.quickshop.api.event.ShopPriceChangeEvent;
import org.maxgamer.quickshop.api.shop.Shop;

public class QuickShopListeners implements Listener {

    private final PriceLimitManager priceLimitManager;

    public QuickShopListeners(@NotNull BungeeEconomyPlugin plugin) {
        this.priceLimitManager = plugin.getPriceLimitManager();
    }

    @EventHandler
    public void onShopCreate(ShopCreateEvent e) {
        Shop shop = e.getShop();
        PriceLimit priceLimit = priceLimitManager.getPriceLimit(shop.getItem());
        if (!priceLimit.isInLimits(shop.getPrice())) {
            e.setCancelled(true);
            Player creator = Bukkit.getPlayer(e.getCreator());
            if (creator != null) {
                creator.sendMessage(priceLimitManager.getLimitMessage(priceLimit));
            }
        }
    }

    @EventHandler
    public void onPriceChange(ShopPriceChangeEvent e) {
        Shop shop = e.getShop();
        PriceLimit priceLimit = priceLimitManager.getPriceLimit(shop.getItem());
        if (!priceLimit.isInLimits(e.getNewPrice())) {
            e.setCancelled(true);
            Player creator = Bukkit.getPlayer(e.getShop().getOwner());
            if (creator != null) {
                creator.sendMessage(priceLimitManager.getLimitMessage(priceLimit));
            }
        }
    }

    @EventHandler
    public void onShopLoad(ShopLoadEvent e) {
        Shop shop = e.getShop();
        PriceLimit priceLimit = priceLimitManager.getPriceLimit(shop.getItem());
        if (priceLimit.getMax() != 0 && shop.getPrice() > priceLimit.getMax()) {
            shop.setPrice(priceLimit.getMax());
        }
        if (shop.getPrice() < priceLimit.getMin()) {
            shop.setPrice(priceLimit.getMin());
        }
    }

}
