package mrhamzaless.com.atrap.gui;

import mrhamzaless.com.atrap.Atrap;
import mrhamzaless.com.atrap.managers.CacheManager;
import mrhamzaless.com.atrap.managers.TrapManager;
import mrhamzaless.com.atrap.utils.ColorUtil;
import mrhamzaless.com.atrap.utils.ItemCreator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TrapPermissionsMenu implements Listener {

    private static final int ITEMS_PER_PAGE = 28;

    public static void open(Player player) {
        CacheManager.reloadCache();
        FileConfiguration memberListConfig = Atrap.getInstance().getMemberListConfig();
        Inventory inv = Bukkit.getServer().createInventory(null, 5 * 9,
                ColorUtil.color("üʏᴇ iᴢiɴʟᴇʀiɴi ᴀʏᴀʀʟᴀ"));

        if (memberListConfig.getBoolean("Menu.Fill-Item.Status", true)) {
            for (int i = 0; i < inv.getSize(); i++) {
                inv.setItem(i,
                        ItemCreator.makeItem(memberListConfig.getString("Menu.Fill-Item.Material", "GRAY_STAINED_GLASS_PANE"), 1,
                                memberListConfig.getString("Menu.Fill-Item.Name", "&r")));
            }
        }

        String trap = TrapManager.getTrapAtLocation(player.getLocation());
        if (trap == null) {
            player.sendMessage("Trap bulunamadı!");
            return;
        }

        inv.setItem(0, ItemCreator.makeItem("RED_STAINED_GLASS_PANE", 1, "&cɢᴇʀɪ ᴅöɴ"));

        int breakBlock = Integer.parseInt(Objects.requireNonNull(CacheManager.getPermissionByPermissionName(
                trap, "permissions.block_place"
        )));

        inv.setItem(10, ItemCreator.makeItem("GRASS_BLOCK", 1, "Blok Koyma", loreGenerator(breakBlock, "Blok koymanıza izin verir.")));



        int blokKirma = Integer.parseInt(Objects.requireNonNull(CacheManager.getPermissionByPermissionName(
                trap, "permissions.block_break"
        )));
        inv.setItem(11, ItemCreator.makeItem("IRON_PICKAXE", 1, "Blok Kırma", loreGenerator(blokKirma, "Blok kırmanıza izin verir.")));

        int chestAccess = Integer.parseInt(Objects.requireNonNull(CacheManager.getPermissionByPermissionName(
                trap, "permissions.chest_access"
        )));
        inv.setItem(12, ItemCreator.makeItem("CHEST", 1, "Sandık Erişimi", loreGenerator(chestAccess, "Sandıklara erişmenize izin verir.")));

        int useBeacon = Integer.parseInt(Objects.requireNonNull(CacheManager.getPermissionByPermissionName(
                trap, "permissions.use_beacon"
        )));
        inv.setItem(13, ItemCreator.makeItem("BEACON", 1, "Beacon Kullanımı", loreGenerator(useBeacon, "Beacon kullanmanıza izin verir.")));

        int useAnvil = Integer.parseInt(Objects.requireNonNull(CacheManager.getPermissionByPermissionName(
                trap, "permissions.use_anvil"
        )));
        inv.setItem(14, ItemCreator.makeItem("ANVIL", 1, "Anvil Kullanımı", loreGenerator(useAnvil, "Anvil kullanmanıza izin verir.")));

        int useDoor = Integer.parseInt(Objects.requireNonNull(CacheManager.getPermissionByPermissionName(
                trap, "permissions.use_door"
        )));
        inv.setItem(15, ItemCreator.makeItem("OAK_DOOR", 1, "Kapı Kullanımı", loreGenerator(useDoor, "Kapı kullanmanıza izin verir.")));

        int useFurnace = Integer.parseInt(Objects.requireNonNull(CacheManager.getPermissionByPermissionName(
                trap, "permissions.use_furnace"
        )));
        inv.setItem(16, ItemCreator.makeItem("FURNACE", 1, "Fırın Kullanımı", loreGenerator(useFurnace, "Fırın kullanmanıza izin verir.")));

        int useCraftingTable = Integer.parseInt(Objects.requireNonNull(CacheManager.getPermissionByPermissionName(
                trap, "permissions.use_crafting"
        )));
        inv.setItem(19, ItemCreator.makeItem("CRAFTING_TABLE", 1, "Crafting Table Kullanımı", loreGenerator(useCraftingTable, "Crafting Table kullanmanıza izin verir.")));

        int useEnchantingTable = Integer.parseInt(Objects.requireNonNull(CacheManager.getPermissionByPermissionName(
                trap, "permissions.use_enchanting_table"
        )));
        inv.setItem(20, ItemCreator.makeItem("ENCHANTING_TABLE", 1, "Enchanting Table Kullanımı", loreGenerator(useEnchantingTable, "Enchanting Table kullanmanıza izin verir.")));

        int useBrewingStand = Integer.parseInt(Objects.requireNonNull(CacheManager.getPermissionByPermissionName(
                trap, "permissions.use_brewing_stand"
        )));
        inv.setItem(21, ItemCreator.makeItem("BREWING_STAND", 1, "İksir Standı Kullanımı", loreGenerator(useBrewingStand, "İksir standı kullanmanıza izin verir.")));


        int dropItems = Integer.parseInt(Objects.requireNonNull(CacheManager.getPermissionByPermissionName(
                trap, "permissions.drop_items"
        )));
        inv.setItem(23, ItemCreator.makeItem("REDSTONE", 1, "Eşyaları Bırakma", loreGenerator(dropItems, "Eşyaları bırakmanıza izin verir.")));

        int useDropper = Integer.parseInt(Objects.requireNonNull(CacheManager.getPermissionByPermissionName(
                trap, "permissions.use_dropper"
        )));
        inv.setItem(24, ItemCreator.makeItem("DROPPER", 1, "Bırakıcı Kullanımı", loreGenerator(useDropper, "Bırakıcı kullanmanıza izin verir.")));

        int useDispenser = Integer.parseInt(Objects.requireNonNull(CacheManager.getPermissionByPermissionName(
                trap, "permissions.use_dispenser"
        )));
        inv.setItem(25, ItemCreator.makeItem("DISPENSER", 1, "Fırlatıcı Kullanımı", loreGenerator(useDispenser, "Fırlatıcı kullanmanıza izin verir.")));

        int useHopper = Integer.parseInt(Objects.requireNonNull(CacheManager.getPermissionByPermissionName(
                trap, "permissions.use_hopper"
        )));
        inv.setItem(29, ItemCreator.makeItem("HOPPER", 1, "Hopper Kullanımı", loreGenerator(useHopper, "Hopper kullanmanıza izin verir.")));

        int useButton = Integer.parseInt(Objects.requireNonNull(CacheManager.getPermissionByPermissionName(
                trap, "permissions.use_button"
        )));
        inv.setItem(30, ItemCreator.makeItem("STONE_BUTTON", 1, "Buton Kullanımı", loreGenerator(useButton, "Buton kullanmanıza izin verir.")));

        int useLever = Integer.parseInt(Objects.requireNonNull(CacheManager.getPermissionByPermissionName(
                trap, "permissions.use_lever"
        )));
        inv.setItem(32, ItemCreator.makeItem("LEVER", 1, "Kolu Çevirme", loreGenerator(useLever, "Kolu çevirmenize izin verir.")));

        int usePressurePlate = Integer.parseInt(Objects.requireNonNull(CacheManager.getPermissionByPermissionName(
                trap, "permissions.use_pressure_plate"
        )));
        inv.setItem(33, ItemCreator.makeItem("STONE_PRESSURE_PLATE", 1, "Basınç Plakası Kullanımı", loreGenerator(usePressurePlate, "Basınç Plakası Kullanımı")));

        int breakSpawner = Integer.parseInt(Objects.requireNonNull(CacheManager.getPermissionByPermissionName(
                trap, "permissions.break_spawner"
        )));
        inv.setItem(22, ItemCreator.makeItem("SPAWNER", 1, "Spawner Kırma", loreGenerator(breakSpawner, "Spawner kırmanıza izin verir.")));








        player.openInventory(inv);
    }
    public static String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        FileConfiguration memberListConfig = Atrap.getInstance().getMemberListConfig();
        if (event.getView().getTitle().equals(ColorUtil.color("üʏᴇ iᴢiɴʟᴇʀiɴi ᴀʏᴀʀʟᴀ"))) {
            CacheManager.reloadCache();
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || clickedItem.getType() == Material.AIR) {
                return;
            }

            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);

            ItemMeta meta = clickedItem.getItemMeta();
            if (meta == null) {
                return;
            }

            String displayName = meta.getDisplayName();

            if (displayName.equals(ColorUtil.color("&cɢᴇʀɪ ᴅöɴ"))) {
                SettingsMenu.open(player);
                return;
            }

            if (displayName.equals("Blok Koyma")) {
                changePerm(player, "permissions.block_place");
                return;
            }

            if (displayName.equals("PVP")) {
                changePerm(player, "permissions.pvp");
                return;
            }

            if (displayName.equals("Blok Kırma")) {
                changePerm(player, "permissions.block_break");
                return;
            }

            if (displayName.equals("Sandık Erişimi")) {
                changePerm(player, "permissions.chest_access");
                return;
            }

            if (displayName.equals("Beacon Kullanımı")) {
                changePerm(player, "permissions.use_beacon");
                return;
            }

            if (displayName.equals("Kapı Kullanımı")) {
                changePerm(player, "permissions.use_door");
                return;
            }

            if (displayName.equals("Fırın Kullanımı")) {
                changePerm(player, "permissions.use_furnace");
                return;
            }

            if (displayName.equals("Crafting Table Kullanımı")) {
                changePerm(player, "permissions.use_crafting_table");
                return;
            }

            if (displayName.equals("Enchanting Table Kullanımı")) {
                changePerm(player, "permissions.use_enchanting_table");
                return;
            }

            if (displayName.equals("Brewing Stand Kullanımı")) {
                changePerm(player, "permissions.use_brewing_stand");
                return;
            }

            if (displayName.equals("Eşyaları Al")) {
                changePerm(player, "permissions.pick_items");
                return;
            }

            if (displayName.equals("Eşyaları Bırakma")) {
                changePerm(player, "permissions.drop_items");
                return;
            }

            if (displayName.equals("Bırakıcı Kullanımı")) {
                changePerm(player, "permissions.use_dropper");
                return;
            }

            if (displayName.equals("Fırlatıcı Kullanımı")) {
                changePerm(player, "permissions.use_dispenser");
                return;
            }

            if (displayName.equals("Hopper Kullanımı")) {
                changePerm(player, "permissions.use_hopper");
                return;
            }

            if (displayName.equals("Buton Kullanımı")) {
                changePerm(player, "permissions.use_button");
                return;
            }

            if (displayName.equals("Kolu Çevirme")) {
                changePerm(player, "permissions.use_lever");
                return;
            }

            if (displayName.equals("Basınç Plakası Kullanımı")) {
                changePerm(player, "permissions.use_pressure_plate");
                return;
            }

            if (displayName.equals("Spawner Kırma")) {
                changePerm(player, "permissions.break_spawner");
                return;
            }

            if (displayName.equals("Anvil Kullanımı")) {
                changePerm(player, "permissions.use_anvil");
                return;
            }
            event.setCancelled(true);
        }
    }

    public static List<String> loreGenerator(int perm, String title){

        String prefix = colorize("&f§x&c&6&d&e&f&1›>");
        String r1 = "ʏᴇɴi üʏᴇ";
        String r2 = "üʏᴇ";
        String r3 = "ᴍᴏᴅᴇʀᴀᴛöʀ";
        List<String> lore = new ArrayList<>();
        lore.add(ColorUtil.color("&7" + title));
        lore.add("");
        lore.add(ColorUtil.color("&a&lYetki:"));
        lore.add(ColorUtil.color(prefix + (perm == 1 ? "&a " : "&c ") + r1));
        lore.add(ColorUtil.color(prefix + (perm == 2 ? "&a " : "&c ") + r2));
        lore.add(ColorUtil.color(prefix + (perm == 3 ? "&a " : "&c ") + r3));
        return lore;

    }
    
    public static void changePerm(Player player, String perm) {
        String trap = TrapManager.getTrapAtLocation(player.getLocation());
        if (Objects.equals(CacheManager.getPermissionByPermissionName(trap, perm), "1")) {
            TrapManager.setTrapSetting(trap, perm, 2);
            open(player);
            return;
        }
        else if (Objects.equals(CacheManager.getPermissionByPermissionName(trap, perm), "2")) {
            TrapManager.setTrapSetting(trap, perm, 3);
            open(player);
            return;
        }
        else if (Objects.equals(CacheManager.getPermissionByPermissionName(trap, perm), "3")) {
            TrapManager.setTrapSetting(trap, perm, 1);
            open(player);
            return;
        }
    }

}
