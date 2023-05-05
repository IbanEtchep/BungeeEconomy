package fr.iban.bungeeeconomy.pricelimit;

import fr.iban.bungeeeconomy.BungeeEconomyPlugin;
import fr.iban.bungeeeconomy.sql.SqlStorage;
import fr.iban.bungeeeconomy.util.SerializationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.maxgamer.quickshop.api.QuickShopAPI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class PriceLimitManager {

    private SqlStorage sqlStorage;
    private BungeeEconomyPlugin plugin;
    private final Map<Material, PriceLimit> materialsPriceLimits = new HashMap<>();
    private final Map<Enchantment, PriceLimit> enchantsPriceLimits = new HashMap<>();

    public PriceLimitManager(BungeeEconomyPlugin plugin, SqlStorage sqlStorage) {
        this.plugin = plugin;
        this.sqlStorage = sqlStorage;
        loadPriceLimits();
    }

    public void loadPriceLimits() {
        materialsPriceLimits.clear();
        enchantsPriceLimits.clear();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Map<String, PriceLimit> limits = sqlStorage.getPriceLimits();

            for (Map.Entry<String, PriceLimit> entry : limits.entrySet()) {
                String key = entry.getKey();
                PriceLimit priceLimit = entry.getValue();
                try {
                    Material material = Material.valueOf(key);
                    materialsPriceLimits.put(material, priceLimit);
                    continue;
                } catch (IllegalArgumentException ignored) {
                }

                Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(key));
                if (enchantment != null) {
                    enchantsPriceLimits.put(enchantment, priceLimit);
                    continue;
                }

            }

            plugin.getLogger().info(materialsPriceLimits.size() + " material price loaded.");
            plugin.getLogger().info(enchantsPriceLimits.size() + " enchant price limits loaded.");
        });
    }

    public PriceLimit getPriceLimit(ItemStack itemStack) {
        PriceLimit priceLimit = new PriceLimit(0, 0);

        if (itemStack.getItemMeta() instanceof EnchantmentStorageMeta enchantmentStorageMeta) {
            for (Map.Entry<Enchantment, PriceLimit> entry : enchantsPriceLimits.entrySet()) {
                if(enchantmentStorageMeta.getStoredEnchants().containsKey(entry.getKey())) {
                    PriceLimit entryLimit = entry.getValue();
                    priceLimit.setMin(priceLimit.getMin() + entryLimit.getMin());
                    priceLimit.setMax(priceLimit.getMax() + entryLimit.getMax());
                }
            }
        }

        if(!itemStack.getEnchantments().isEmpty()) {
            for (Map.Entry<Enchantment, PriceLimit> entry : enchantsPriceLimits.entrySet()) {
                if(itemStack.containsEnchantment(entry.getKey())) {
                    PriceLimit entryLimit = entry.getValue();
                    priceLimit.setMin(priceLimit.getMin() + entryLimit.getMin());
                    priceLimit.setMax(priceLimit.getMax() + entryLimit.getMax());
                }
            }
        }

        PriceLimit materialLimit = materialsPriceLimits.get(itemStack.getType());
        if (materialLimit != null) {
            priceLimit.setMin(priceLimit.getMin() + materialLimit.getMin());
            priceLimit.setMax(priceLimit.getMax() + materialLimit.getMax());
        }

        return priceLimit;
    }

    public void setPriceLimit(String key, double min, double max) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> sqlStorage.updatePriceLimit(key, min, max));
        PriceLimit priceLimit = new PriceLimit(min, max);

        try {
            Material material = Material.valueOf(key);
            materialsPriceLimits.put(material, priceLimit);
            return;
        } catch (IllegalArgumentException ignored) {
        }

        Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(key));
        if (enchantment != null) {
            enchantsPriceLimits.put(enchantment, priceLimit);
        }
    }

    public String getLimitMessage(PriceLimit limit) {
        if (limit.getMin() == 0 && limit.getMax() > 0) {
            return "Le prix unitaire de cet objet ne doit pas dépasser " + plugin.getEconomy().format(limit.getMax()) + ".";
        }
        if (limit.getMin() > 0 && limit.getMax() == 0) {
            return "Le prix unitaire de cet objet doit être d'au moins " + plugin.getEconomy().format(limit.getMin()) + ".";
        }
        return "Le prix unitaire de cet objet doit être d'au moins " + plugin.getEconomy().format(limit.getMin()) + " et ne doit pas dépasser " + plugin.getEconomy().format(limit.getMax()) + ".";
    }

    public Map<Enchantment, PriceLimit> getEnchantsPriceLimits() {
        return enchantsPriceLimits;
    }

    public Map<Material, PriceLimit> getMaterialsPriceLimits() {
        return materialsPriceLimits;
    }

    public List<String> getValidKeys() {
        List<String> validKeys = new ArrayList<>();
        for (Material material : Material.values()) {
            validKeys.add(material.toString());
        }
        for (Enchantment enchantment : Enchantment.values()) {
            validKeys.add(enchantment.getKey().getKey());
        }
        validKeys.add("itemInHand");
        return validKeys;
    }


}
