package mrhamzaless.com.atrap.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

public class ItemUtils {

    public static UUID getPlayerUUIDFromItem(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return null;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }

        List<String> lore = meta.getLore();
        if (lore != null) {
            for (String line : lore) {
                // UUID'yi bulmak için doğru formatı kontrol edin
                if (line.startsWith("UUID:")) {
                    String uuidString = line.substring(5).trim(); // "UUID:" kısmını çıkarın
                    try {
                        return UUID.fromString(uuidString); // UUID'yi döndürün
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return null;
    }

}
