package mrhamzaless.com.atrap.gui;

import mrhamzaless.com.atrap.Atrap;
import mrhamzaless.com.atrap.managers.CacheManager;
import mrhamzaless.com.atrap.utils.ColorUtil;
import mrhamzaless.com.atrap.utils.ItemCreator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class SettingsMenu implements Listener {

    // Bu metod, güncel konfigürasyonu her seferinde yeniden yükler
    @SuppressWarnings("deprecation")
    public static void open(Player player) {
        CacheManager.reloadCache();
        FileConfiguration guiSettingsConfig = Atrap.getInstance().getGuiSettingsConfig();

        Inventory inv = Bukkit.getServer().createInventory(null, (guiSettingsConfig.getInt("Menu.Gui-Row", 3) * 9), ColorUtil.color(guiSettingsConfig.getString("Menu.Gui-Name", "Trap Ayarları")));

        if (Atrap.getInstance().getGuiSettingsConfig().getBoolean("Menu.Fill-Item.Status", true)) {
            for (int i = 0; i < inv.getSize(); i++) {
                inv.setItem(i,
                        ItemCreator.makeItem(guiSettingsConfig.getString("Menu.Fill-Item.Material", "GRAY_STAINED_GLASS_PANE"), 1,
                                guiSettingsConfig.getString("Menu.Fill-Item.Name", "&r")));
            }
        }

        inv.setItem(guiSettingsConfig.getInt("Menu.Member-List.Slot", 11),
                ItemCreator.makeItem(guiSettingsConfig.getString("Menu.Member-List.Material", "PLAYER_HEAD"), 1,
                        guiSettingsConfig.getString("Menu.Member-List.Name", "&eÜye Ayarları"),
                        guiSettingsConfig.getStringList("Menu.Member-List.Lore")));

        inv.setItem(guiSettingsConfig.getInt("Menu.Authorisation-Settings.Slot", 15),
                ItemCreator.makeItem(guiSettingsConfig.getString("Menu.Authorisation-Settings.Material", "PLAYER_HEAD"), 1,
                        guiSettingsConfig.getString("Menu.Authorisation-Settings.Name", "&eYetki Ayarları"),
                        guiSettingsConfig.getStringList("Menu.Authorisation-Settings.Lore")));

        player.openInventory(inv);
    }

    @EventHandler
    @SuppressWarnings("deprecation")
    public void onInvClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getView().getTitle().equals(ColorUtil.color(Atrap.getInstance().getGuiSettingsConfig().getString("Menu.Gui-Name", "Trap Ayarları")))) {

            if ((event.getCurrentItem() == null) || (event.getCurrentItem().getType().equals(Material.AIR))) {
                return;
            }
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
            if (event.getCurrentItem().getItemMeta().getDisplayName().equals(ColorUtil.color(Atrap.getInstance().getGuiSettingsConfig().getString("Menu.Member-List.Name", "&eÜye Ayarları")))) {
                MemberListMenu.open(player, 0);
            }
            if (event.getCurrentItem().getItemMeta().getDisplayName().equals(ColorUtil.color(Atrap.getInstance().getGuiSettingsConfig().getString("Menu.Authorisation-Settings.Name", "&eYetki Ayarları")))) {
                TrapPermissionsMenu.open(player);
            }
            event.setCancelled(true);
        }
    }
}
