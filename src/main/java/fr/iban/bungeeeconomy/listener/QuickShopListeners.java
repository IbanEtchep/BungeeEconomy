package fr.iban.bungeeeconomy.listener;

import com.ghostchu.quickshop.api.event.ShopCreateEvent;
import com.ghostchu.quickshop.api.event.ShopItemChangeEvent;
import com.ghostchu.quickshop.api.event.ShopLoadEvent;
import com.ghostchu.quickshop.api.event.ShopPriceChangeEvent;
import com.ghostchu.quickshop.api.shop.Shop;
import fr.iban.bungeeeconomy.BungeeEconomyPlugin;
import fr.iban.bungeeeconomy.pricelimit.PriceLimit;
import fr.iban.bungeeeconomy.pricelimit.PriceLimitManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

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
            e.setCancelled(true, "§cLe prix de cet item est en dehors des limites autorisées.");
            Player creator = Bukkit.getPlayer(e.getCreator().getUniqueId());
            if (creator != null) {
                creator.sendMessage(priceLimitManager.getLimitMessage(priceLimit));
            }
        }
    }

    @EventHandler
    public void onShopItemChange(ShopItemChangeEvent e) {
        Shop shop = e.getShop();
        ItemStack newItem = e.getNewItem();
        PriceLimit priceLimit = priceLimitManager.getPriceLimit(newItem);
        if (!priceLimit.isInLimits(shop.getPrice())) {
            Player creator = Bukkit.getPlayer(e.getShop().getOwner().getUniqueId());
            if (creator != null) {
                creator.sendMessage(priceLimitManager.getLimitMessage(priceLimit));
            }
            shop.setPrice(priceLimit.getMin());
        }
    }

    @EventHandler
    public void onPriceChange(ShopPriceChangeEvent e) {
        Shop shop = e.getShop();
        PriceLimit priceLimit = priceLimitManager.getPriceLimit(shop.getItem());
        if (!priceLimit.isInLimits(e.getNewPrice())) {
            e.setCancelled(true, "§cLe prix de cet item est en dehors des limites autorisées.");
            Player creator = Bukkit.getPlayer(e.getShop().getOwner().getUniqueId());
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
