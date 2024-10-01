package mrhamzaless.com.atrap.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemCreator {


    public static ItemStack makeItem(String material, int amount, String name) {
        ItemStack item = new ItemStack(Material.valueOf(material), (short) amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ColorUtil.color(name));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack makeItem(String material, int amount, String name, List<String> lore) {
        ItemStack item = new ItemStack(Material.valueOf(material), (short) amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ColorUtil.color(name));
        meta.setLore(ColorUtil.color(lore));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack makeItem(Material material, int amount, String name) {
        ItemStack item = new ItemStack(material, (short) amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ColorUtil.color(name));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack makeItem(Material material, int amount, String name, List<String> lore) {
        ItemStack item = new ItemStack(material, (short) amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ColorUtil.color(name));
        meta.setLore(ColorUtil.color(lore));
        item.setItemMeta(meta);
        return item;
    }

}
