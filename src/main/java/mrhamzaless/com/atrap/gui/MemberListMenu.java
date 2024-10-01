package mrhamzaless.com.atrap.gui;

import mrhamzaless.com.atrap.Atrap;
import mrhamzaless.com.atrap.managers.CacheManager;
import mrhamzaless.com.atrap.managers.TrapManager;
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
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static mrhamzaless.com.atrap.managers.TrapManager.colorize;

public class MemberListMenu implements Listener {
    private static final int ITEMS_PER_PAGE = 28;

    public static void open(Player player, int page) {
        CacheManager.reloadCache();
        FileConfiguration memberListConfig = Atrap.getInstance().getMemberListConfig();
        Inventory inv = Bukkit.getServer().createInventory(null, memberListConfig.getInt("Menu.Gui-Row", 6) * 9,
                ColorUtil.color(memberListConfig.getString("Menu.Gui-Name", "üʏᴇ ᴀʏᴀʀʟᴀʀı")));

        if (memberListConfig.getBoolean("Menu.Fill-Item.Status", true)) {
            for (int i = 0; i < inv.getSize(); i++) {
                inv.setItem(i,
                        ItemCreator.makeItem(memberListConfig.getString("Menu.Fill-Item.Material", "GRAY_STAINED_GLASS_PANE"), 1,
                                memberListConfig.getString("Menu.Fill-Item.Name", "&r")));
            }
        }

        if (page > 0) {
            inv.setItem(memberListConfig.getInt("Menu.Previous-Page.Slot", 46),
                    ItemCreator.makeItem(memberListConfig.getString("Menu.Previous-Page.Material", "BARRIER"), 1,
                            memberListConfig.getString("Menu.Previous-Page.Name", "&aöɴᴄᴇᴋɪ ѕᴀʏꜰᴀ")));
        }

        if ((page + 1) * ITEMS_PER_PAGE < TrapManager.getTrapMembers(player).size()) {
            inv.setItem(memberListConfig.getInt("Menu.Next-Page.Slot", 52),
                    ItemCreator.makeItem(memberListConfig.getString("Menu.Next-Page.Material", "BARRIER"), 1,
                            memberListConfig.getString("Menu.Next-Page.Name", "&aѕᴏɴʀᴀᴋɪ ѕᴀʏꜰᴀ")));
        }

        inv.setItem(memberListConfig.getInt("Menu.Go-Back.Slot", 49),
                ItemCreator.makeItem(memberListConfig.getString("Menu.Go-Back.Material", "BARRIER"), 1,
                        memberListConfig.getString("Menu.Go-Back.Name", "&cɢᴇʀɪ ᴅöɴ")));

        List<UUID> members = TrapManager.getTrapMembers(player);
        int startIndex = page * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, members.size());


        for (int i = startIndex; i < endIndex; i++) {
            UUID memberUUID = members.get(i);
            String playerName = Bukkit.getOfflinePlayer(memberUUID).getName();


            inv.setItem(memberListConfig.getInt("Menu.List." + (i - startIndex), 0),
                    ItemCreator.makeItem(memberListConfig.getString("Menu.Member-List.Material", "PLAYER_HEAD"), 1,
                            memberListConfig.getString("Menu.Member-List.Name", "&e%player%").replace("%player%", playerName),
                            loreGenerator(memberUUID, player)));
        }

        player.openInventory(inv);
    }


    public static List<String> loreGenerator(UUID uuid, Player player){
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("&cüʏᴇʏɪ ᴛʀᴀᴘᴛᴇɴ ᴀᴛᴍᴀᴋ ɪçɪɴ ѕᴀğ ᴛɪᴋʟᴀ");
        lore.add("&cüʏᴇɴɪɴ ʏᴇᴛᴋɪѕɪɴɪ ᴅᴇğɪşᴛɪʀᴍᴇᴋ ɪçɪɴ ѕᴏʟ ᴛɪᴋʟᴀ");
        String perm = CacheManager.getPlayerPermissionByUUID(uuid);
        String prefix = colorize("&f§x&c&6&d&e&f&1›>");
        String r1 = "ʏᴇɴi üʏᴇ";
        String r2 = "üʏᴇ";
        String r3 = "ᴍᴏᴅᴇʀᴀᴛöʀ";
        lore.add("");
        lore.add(ColorUtil.color("&a&lʏᴇᴛᴋɪ:"));
        assert perm != null;
        lore.add(ColorUtil.color(prefix + (perm.contains("1") ? "&a " : "&c ") + r1));
        lore.add(ColorUtil.color(prefix + (perm.contains("2") ? "&a " : "&c ") + r2));
        lore.add(ColorUtil.color(prefix + (perm.contains("3") ? "&a " : "&c ") + r3));
        lore.add("");
        lore.add("UUID:" + uuid);
        return lore;
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        FileConfiguration memberListConfig = Atrap.getInstance().getMemberListConfig();

        if (event.getView().getTitle().equals(ColorUtil.color(memberListConfig.getString("Menu.Gui-Name", "üʏᴇ ᴀʏᴀʀʟᴀʀı")))) {
            event.setCancelled(true);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || clickedItem.getType() == Material.AIR) {
                return;
            }

            ItemMeta meta = clickedItem.getItemMeta();
            if (meta == null) {
                return;
            }

            String displayName = meta.getDisplayName();

            if (displayName.equals(ColorUtil.color(memberListConfig.getString("Menu.Go-Back.Name", "&cɢᴇʀɪ ᴅöɴ")))) {
                SettingsMenu.open(player);
                return;
            }

            if (displayName.equals(ColorUtil.color(memberListConfig.getString("Menu.Previous-Page.Name", "&aöɴᴄᴇᴋɪ ѕᴀʏꜰᴀ")))) {
                int currentPage = getCurrentPage(event.getView());
                open(player, currentPage - 1);
                return;
            }

            if (displayName.equals(ColorUtil.color(memberListConfig.getString("Menu.Next-Page.Name", "&aѕᴏɴʀᴀᴋɪ ѕᴀʏꜰᴀ")))) {
                int currentPage = getCurrentPage(event.getView());
                open(player, currentPage + 1);
                return;
            }

            ItemStack clickedItemStack = event.getCurrentItem();
            if (clickedItemStack == null || clickedItemStack.getType() == Material.AIR || clickedItemStack.getItemMeta() == null) {

            }
            else{

                if (event.isLeftClick()){
                    ItemMeta clickedItemMeta = clickedItemStack.getItemMeta();
                    List<String> lore = clickedItemMeta.getLore();
                    if (lore != null) {
                        String uuid = lore.get(lore.size() - 1).replace("UUID:", "").trim();
                        UUID memberUUID = UUID.fromString(uuid);
                        String trap = TrapManager.getTrapAtLocation(player.getLocation());
                        String perm = TrapManager.getPermissionOfTrapMember(memberUUID, trap);
                        if (perm != null) {
                            int permValue = 0;

                            switch (perm) {
                                case "1":
                                    permValue = 2;
                                    break;
                                case "2":
                                    permValue = 3;
                                    break;
                                case "3":
                                    permValue = 1;
                                    break;
                            }
                            TrapManager.setPermissionOfTrapMember(memberUUID, trap, permValue);
                            open(player, getCurrentPage(event.getView()));
                        }
                    }
                } else if (event.isRightClick()){
                    ItemMeta clickedItemMeta = clickedItemStack.getItemMeta();
                    List<String> lore = clickedItemMeta.getLore();
                    if (lore != null) {
                        String uuid = lore.get(lore.size() - 1).replace("UUID:", "").trim();
                        UUID memberUUID = UUID.fromString(uuid);
                        TrapManager.removeMemberFromTrap(player, memberUUID);
                        open(player, getCurrentPage(event.getView()));
                    }
                }


            }

        }
    }



    private int getCurrentPage(InventoryView view) {
        String title = view.getTitle();
        try {
            return Integer.parseInt(title.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }


}
