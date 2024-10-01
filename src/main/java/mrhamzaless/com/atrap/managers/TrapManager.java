package mrhamzaless.com.atrap.managers;

import mrhamzaless.com.atrap.Atrap;
import mrhamzaless.com.atrap.cache.TrapCache;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class TrapManager {

    public static List<UUID> getTrapMembers(Player player) {
        List<UUID> members = new ArrayList<>();
        String trapName = Atrap.getInstance().getTrapManager().getTrapAtLocation(player.getLocation());
        if (trapName != null) {
            File trapFile = new File(Atrap.getInstance().getDataFolder(), "DataBase/Traps/" + trapName + ".yml");
            if (trapFile.exists()) {
                FileConfiguration trapConfig = YamlConfiguration.loadConfiguration(trapFile);
                if (trapConfig.contains("members")) {
                    List<String> uuidStrings = trapConfig.getStringList("members");
                    for (String uuidString : uuidStrings) {
                        try {
                            UUID uuid = UUID.fromString(uuidString.split(":")[0]);
                            members.add(uuid);
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return members;
    }

    public static boolean isTrapOwner(Player player) {
        String trapName = Atrap.getInstance().getTrapManager().getTrapAtLocation(player.getLocation());
        if (trapName != null) {
            File trapFile = new File(Atrap.getInstance().getDataFolder(), "DataBase/Traps/" + trapName + ".yml");
            if (trapFile.exists()) {
                FileConfiguration trapConfig = YamlConfiguration.loadConfiguration(trapFile);
                UUID playerUUID = player.getUniqueId();
                return playerUUID.equals(trapConfig.get("owner"));
            }
        }
        return false;
    }

    public static void removeMemberFromTrap(Player owner, UUID memberUUID) {
        String trapName = TrapManager.getTrapAtLocation(owner.getLocation());
        if (trapName != null) {
            File trapFile = new File(Atrap.getInstance().getDataFolder(), "DataBase/Traps/" + trapName + ".yml");
            if (trapFile.exists()) {
                FileConfiguration trapConfig = YamlConfiguration.loadConfiguration(trapFile);
                List<String> members = trapConfig.getStringList("members");
                for (int i = 0; i < members.size(); i++) {
                    String uuidString = members.get(i).split(":")[0];
                    if (uuidString.equals(memberUUID.toString())) {
                        members.remove(i);
                        break;
                    }
                }
                trapConfig.set("members", members);
                try {
                    trapConfig.save(trapFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static File dataFolder;
    public static FileConfiguration languageConfig;

    public TrapManager(File dataFolder, FileConfiguration languageConfig) {
        TrapManager.dataFolder = new File(dataFolder, "DataBase/Traps");
        TrapManager.languageConfig = languageConfig;
        if (!TrapManager.dataFolder.exists()) {
            TrapManager.dataFolder.mkdirs();
        }
    }

    public static boolean trapExists(String name) {
        File trapFile = new File(dataFolder, name + ".yml");
        return trapFile.exists();
    }

    public static boolean createTrap(String name, Location location, Player owner) {
        if (trapExists(name)) {
            return true;
        }

        if (isChunkOccupied(location)) {
            return true;
        }

        File trapFile = new File(dataFolder, name + ".yml");
        YamlConfiguration trapConfig = YamlConfiguration.loadConfiguration(trapFile);
        trapConfig.set("name", name);
        trapConfig.set("chunks", Collections.singletonList(locationToString(location)));
        trapConfig.set("owner", null);

        trapConfig.set("permissions.block_place", 3);
        trapConfig.set("permissions.block_break", 3);
        trapConfig.set("permissions.chest_access", 3);
        trapConfig.set("permissions.use_beacon", 3);
        trapConfig.set("permissions.use_bucket", 3);
        trapConfig.set("permissions.use_crafting", 3);
        trapConfig.set("permissions.pick_items", 3);
        trapConfig.set("permissions.drop_items", 3);
        trapConfig.set("permissions.use_item_frames", 3);
        trapConfig.set("permissions.use_armor_stands", 3);
        trapConfig.set("permissions.use_hopper", 3);
        trapConfig.set("permissions.break_spawner", 3);
        trapConfig.set("permissions.use_button", 3);
        trapConfig.set("permissions.use_lever", 3);
        trapConfig.set("permissions.use_pressure_plate", 3);
        trapConfig.set("permissions.use_door", 3);
        trapConfig.set("permissions.use_dropper", 3);
        trapConfig.set("permissions.use_dispenser", 3);
        trapConfig.set("permissions.use_furnace", 3);
        trapConfig.set("permissions.use_brewing_stand", 3);
        trapConfig.set("permissions.use_enchanting_table", 3);
        trapConfig.set("permissions.use_anvil", 3);
        try {
            trapConfig.save(trapFile);
        } catch (IOException e) {
            e.printStackTrace();
            return true;
        }

        return false;
    }

    public static boolean addChunkToTrap(String name, Location location) {
        if (!trapExists(name)) {
            return false;
        }

        if (isChunkOccupied(location)) {
            return false;
        }

        File trapFile = new File(dataFolder, name + ".yml");
        YamlConfiguration trapConfig = YamlConfiguration.loadConfiguration(trapFile);

        List<String> chunks = trapConfig.getStringList("chunks");
        String newChunk = locationToString(location);
        if (chunks.contains(newChunk)) {
            return false;
        }

        chunks.add(newChunk);
        trapConfig.set("chunks", chunks);

        try {
            trapConfig.save(trapFile);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static String getTrapAtLocation(Location location) {
        File trapFile = getTrapFileAtLocation(location);
        if (trapFile != null) {
            YamlConfiguration trapConfig = YamlConfiguration.loadConfiguration(trapFile);
            return trapConfig.getString("name");
        }
        return null;
    }

    public static boolean isOwner(Player player, String trapName) {
        File trapFile = new File(dataFolder, trapName + ".yml");
        if (!trapFile.exists()) {
            return true;
        }

        YamlConfiguration trapConfig = YamlConfiguration.loadConfiguration(trapFile);
        String ownerUUID = trapConfig.getString("owner");

        return ownerUUID == null || !ownerUUID.equals(player.getUniqueId().toString());
    }

    public static boolean isMember(Player player, String trapName) {
        File trapFile = new File(dataFolder, trapName + ".yml");
        if (!trapFile.exists()) {
            return false;
        }

        YamlConfiguration trapConfig = YamlConfiguration.loadConfiguration(trapFile);
        List<String> members = trapConfig.getStringList("members");

        return members.contains(player.getUniqueId().toString());
    }

    public static void invitePlayerToTrap(String trapName, Player invitedPlayer) {
        File trapFile = new File(dataFolder, trapName + ".yml");
        if (!trapFile.exists()) {
            return;
        }

        YamlConfiguration trapConfig = YamlConfiguration.loadConfiguration(trapFile);
        List<String> invitedPlayers = trapConfig.getStringList("invitedPlayers");

        if (invitedPlayers == null) {
            invitedPlayers = new ArrayList<>();
        }

        if (!isMember(invitedPlayer, trapName)) {
            invitedPlayers.add(invitedPlayer.getUniqueId().toString());
            trapConfig.set("invitedPlayers", invitedPlayers);

            try {
                trapConfig.save(trapFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String acceptInvite(Player player, String trapName) {
        File trapFile = new File(dataFolder, trapName + ".yml");
        if (!trapFile.exists()) {
            return languageConfig.getString("error.trap_not_found", "&cTrap bulunamadı.");
        }

        YamlConfiguration trapConfig = YamlConfiguration.loadConfiguration(trapFile);
        List<String> invitedPlayers = trapConfig.getStringList("invitedPlayers");

        if (invitedPlayers == null || !invitedPlayers.contains(player.getUniqueId().toString())) {
            return languageConfig.getString("error.not_invited", "&cBu trap'e davet edilmediniz.");
        }

        invitedPlayers.remove(player.getUniqueId().toString());
        trapConfig.set("invitedPlayers", invitedPlayers);

        List<String> members = trapConfig.getStringList("members");
        if (members == null) {
            members = new ArrayList<>();
        }
        members.add(player.getUniqueId().toString() + ":1");
        trapConfig.set("members", members);

        try {
            trapConfig.save(trapFile);
        } catch (IOException e) {
            e.printStackTrace();
            return languageConfig.getString("error.save_failed", "&cTrap'e katılırken bir hata oluştu.");
        }

        sendJoinMessage(player, trapName);

        return "success";
    }

    public static void sendJoinMessage(Player newMember, String trapName) {
        File trapFile = new File(dataFolder, trapName + ".yml");
        if (!trapFile.exists()) {
            return;
        }

        YamlConfiguration trapConfig = YamlConfiguration.loadConfiguration(trapFile);
        List<String> members = trapConfig.getStringList("members");

        String prefix = languageConfig.getString("prefix", ":server_prefix: §x&c&6&d&e&f&1›>");

        String ownerUUID = trapConfig.getString("owner");
        if (ownerUUID != null && !ownerUUID.equals("none")) {
            Player owner = newMember.getServer().getPlayer(UUID.fromString(ownerUUID));
            if (owner != null) {
                owner.sendMessage(colorize("\n" + languageConfig.getString("info.player_joined_trap", "&a%player% adlı oyuncu trap'e katıldı!").replace("%player%", newMember.getName()) + "\n&r"));
            }
        }

        if (members != null) {
            for (String memberUUID : members) {
                Player member = newMember.getServer().getPlayer(UUID.fromString(memberUUID.split(":")[0]));
                if (member != null) {
                    member.sendMessage(colorize("\n" + languageConfig.getString("info.player_joined_trap", "&a%player% adlı oyuncu trap'e katıldı!").replace("%player%", newMember.getName()) + "\n&r"));
                }
            }
        }
    }

    public static  String colorize(String message) {
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', message);
    }

    public static int getMaxTrapRights(Player player) {
        String[] permissions = {"atrap.rights.3", "atrap.rights.2", "atrap.rights.1"};
        for (int i = 0; i < permissions.length; i++) {
            if (player.hasPermission(permissions[i])) {
                return Integer.parseInt(permissions[i].replace("atrap.rights.", ""));
            }
        }
        return 0;
    }

    public static boolean isInvited(Player player, String trapName) {
        File trapFile = new File(dataFolder, trapName + ".yml");
        if (!trapFile.exists()) {
            return false;
        }

        YamlConfiguration trapConfig = YamlConfiguration.loadConfiguration(trapFile);
        List<String> invitedPlayers = trapConfig.getStringList("invitedPlayers");

        return invitedPlayers.contains(player.getUniqueId().toString());
    }

    public static String buyTrap(Player player, String trapName) {
        File trapFile = new File(dataFolder, trapName + ".yml");
        if (!trapFile.exists()) {
            return "Trap bulunamadı";
        }

        YamlConfiguration trapConfig = YamlConfiguration.loadConfiguration(trapFile);
        String ownerUUIDString = trapConfig.getString("owner");

        if (ownerUUIDString != null && !ownerUUIDString.equals("none")) {
            try {
                UUID ownerUUID = UUID.fromString(ownerUUIDString);
                if (ownerUUID != null) {
                    return "Trap zaten bir oyuncuya ait";
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                return "Geçersiz UUID formatı";
            }
        }

        int maxTraps = getMaxTrapRights(player);
        if (maxTraps <= 0) {
            return "Yeterli hak yok";
        }

        File playerFile = new File(new File(dataFolder.getParentFile(), "Players"), player.getUniqueId() + ".yml");
        if (!playerFile.exists()) {
            return "Oyuncu dosyası bulunamadı";
        }

        YamlConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);
        int trapRights = playerConfig.getInt("trap_rights", 0);

        List<String> trapChunks = trapConfig.getStringList("chunks");
        int trapChunkCount = trapChunks.size();

        if (trapRights < trapChunkCount) {
            return languageConfig.getString("error.insufficient_rights");
        }

        int currentTrapCount = getTrapChunkCount(trapName);
        if (currentTrapCount > maxTraps) {
            return languageConfig.getString("error.trap_limit_exceeded")
                    .replace("%max%", String.valueOf(maxTraps));
        }

        trapConfig.set("owner", player.getUniqueId().toString());
        playerConfig.set("trap_rights", trapRights - trapChunkCount);

        try {
            trapConfig.save(trapFile);
            playerConfig.save(playerFile);
        } catch (IOException e) {
            e.printStackTrace();
            return "Dosya kaydedilirken hata oluştu";
        }

        return "Trap başarıyla sahiplenildi";
    }

    public static String getTrapOwner(String trapName) {
        File trapFile = new File(dataFolder, trapName + ".yml");
        if (trapFile.exists()) {
            YamlConfiguration trapConfig = YamlConfiguration.loadConfiguration(trapFile);
            return trapConfig.getString("owner");
        }
        return null;
    }

    public static int getTrapChunkCount(String trapName) {
        File trapFile = new File(dataFolder, trapName + ".yml");
        if (trapFile.exists()) {
            YamlConfiguration trapConfig = YamlConfiguration.loadConfiguration(trapFile);
            return trapConfig.getStringList("chunks").size();
        }
        return 0;
    }

    public static File getTrapFileAtLocation(Location location) {
        for (File file : dataFolder.listFiles()) {
            if (file.getName().endsWith(".yml")) {
                YamlConfiguration trapConfig = YamlConfiguration.loadConfiguration(file);
                List<String> chunks = trapConfig.getStringList("chunks");
                if (chunks.contains(locationToString(location))) {
                    return file;
                }
            }
        }
        return null;
    }

    public static String locationToString(Location location) {
        return location.getWorld().getName() + "," + location.getChunk().getX() + "," + location.getChunk().getZ();
    }

    public static boolean isChunkOccupied(Location location) {
        for (File file : Objects.requireNonNull(dataFolder.listFiles())) {
            if (file.getName().endsWith(".yml")) {
                YamlConfiguration trapConfig = YamlConfiguration.loadConfiguration(file);
                List<String> chunks = trapConfig.getStringList("chunks");
                if (chunks.contains(locationToString(location))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String renameTrap(Player player, String oldTrapName, String newTrapName) {
        File oldTrapFile = new File(dataFolder, oldTrapName + ".yml");
        if (!oldTrapFile.exists()) {
            return "Trap bulunamadı";
        }


        File newTrapFile = new File(dataFolder, newTrapName + ".yml");
        if (newTrapFile.exists()) {
            return "Bu isimde bir trap zaten mevcut";
        }

        YamlConfiguration oldTrapConfig = YamlConfiguration.loadConfiguration(oldTrapFile);
        String ownerUUIDString = oldTrapConfig.getString("owner");

        if (ownerUUIDString == null || !ownerUUIDString.equals(player.getUniqueId().toString())) {
            return "Yeterli hak yok";
        }

        try {
            Files.move(oldTrapFile.toPath(), newTrapFile.toPath());
            oldTrapConfig.set("name", newTrapName);
            oldTrapConfig.save(newTrapFile);
        } catch (IOException e) {
            e.printStackTrace();
            return "Dosya kaydedilirken hata oluştu";
        }

        System.out.println("İşlem başarılı");
        return "success";
    }

    public static String getTrapFileName(String trapName) {
        File file = new File(dataFolder, trapName + ".yml");
        if (file.exists()) {
            return file.getName();
        }
        return null;
    }
    public static HashMap<String, List<TrapCache>> getAllTrapsAndAllPermissions() {
        HashMap<String, List<TrapCache>> traps = new HashMap<>();
        for (File file : Objects.requireNonNull(dataFolder.listFiles())) {
            if (file.getName().endsWith(".yml")) {
                YamlConfiguration trapConfig = YamlConfiguration.loadConfiguration(file);
                String trapName = trapConfig.getString("name");
                List<TrapCache> permissions = new ArrayList<>();

                ConfigurationSection permissionsSection = trapConfig.getConfigurationSection("permissions");
                if (permissionsSection != null) {
                    for (String key : permissionsSection.getKeys(false)) {
                        String value = permissionsSection.getString(key);
                        permissions.add(new TrapCache(key, value));
                    }
                }

                traps.put(trapName, permissions);
            }
        }
        return traps;
    }


    public static boolean CheckAllTrapsAndLeave(Player player) {
        File[] files = dataFolder.listFiles();
        if (files == null) {
            return false;
        }

        for (File file : files) {
            if (file.getName().endsWith(".yml")) {
                YamlConfiguration trapConfig = YamlConfiguration.loadConfiguration(file);
                List<String> members = trapConfig.getStringList("members");
                if (members == null || members.isEmpty()) {
                    continue;
                }
                for (String member : members) {
                    String uuid = member.split(":")[0];
                    String owner = trapConfig.getString("owner");
                    if (owner == null) {
                        continue;
                    }

                    if (owner.equals(uuid)) {
                        return false;
                    } else if (uuid.equals(player.getUniqueId().toString())) {
                        members.remove(member);
                        trapConfig.set("members", members);
                        try {
                            trapConfig.save(file);
                            return true;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return false;
    }



    public static HashMap<String,String> getAllPlayersWithTheirPermissions(){
        HashMap<String, String> players = new HashMap<>();
        for (File file : Objects.requireNonNull(dataFolder.listFiles())) {
            if (file.getName().endsWith(".yml")) {
                YamlConfiguration trapConfig = YamlConfiguration.loadConfiguration(file);
                List<String> members = trapConfig.getStringList("members");
                for (String member : members) {
                    String uuid = member.split(":")[0];
                    String permission = member.split(":")[1];
                    players.put(uuid, permission);
                }
            }
        }
        return players;
    }

    public static int getCurrentPage(InventoryView view) {
        String title = view.getTitle();
        try {
            return Integer.parseInt(title.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

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
                if (line.startsWith("UUID:")) {
                    String uuidString = line.substring(5).trim();
                    try {
                        return UUID.fromString(uuidString);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return null;
    }



    public static void setTrapSetting(String trapName, String setting, Object value) {
        File trapFile = new File(dataFolder, trapName + ".yml");
        if (!trapFile.exists()) {
            return;
        }

        YamlConfiguration trapConfig = YamlConfiguration.loadConfiguration(trapFile);
        trapConfig.set(setting, value);

        try {
            trapConfig.save(trapFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getTrapPermission(String trapName, String setting) {
        File trapFile = new File(dataFolder, trapName + ".yml");

        // Debug mesajı: Dosyanın mevcut olup olmadığını kontrol et
        if (!trapFile.exists()) {
            Bukkit.getLogger().info("§cDebug: Trap dosyası bulunamadı - " + trapFile.getPath());
            return null;
        }

        // Dosyanın yüklendiğini doğrula
        YamlConfiguration trapConfig = YamlConfiguration.loadConfiguration(trapFile);

        // Ayar anahtarını kontrol et
        String permission = trapConfig.getString(setting);
        if (permission == null) {
            Bukkit.getLogger().info("§cDebug: Ayar bulunamadı - Anahtar: " + setting);
        } else {
            Bukkit.getLogger().info("§aDebug: Ayar bulundu - Anahtar: " + setting + ", Değer: " + permission);
        }

        // Ayar değerini döndür
        return permission;
    }

    public static String getPermissionOfTrapMember(UUID memberUUID, String trapName) {
        File trapFile = new File(dataFolder, trapName + ".yml");
        if (!trapFile.exists()) {
            return null;
        }

        YamlConfiguration trapConfig = YamlConfiguration.loadConfiguration(trapFile);
        List<String> members = trapConfig.getStringList("members");

        for (String member : members) {
            String parsed = member.split(":")[0];
            if (memberUUID.toString().equals(parsed)) {
                return member.split(":")[1];
            }
        }
        return null;
    }

    public static void setPermissionOfTrapMember(UUID member, String trap, int perm){
        File trapFile = new File(dataFolder, trap + ".yml");
        if (!trapFile.exists()) {
            return;
        }

        YamlConfiguration trapConfig = YamlConfiguration.loadConfiguration(trapFile);
        List<String> members = trapConfig.getStringList("members");

        for (int i = 0; i < members.size(); i++) {
            String parsed = members.get(i).split(":")[0];
            if (member.toString().equals(parsed)) {
                members.set(i, member.toString() + ":" + perm);
            }
        }
        trapConfig.set("members", members);

        try {
            trapConfig.save(trapFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static HashMap<String, UUID> getAllTrapsAndTheirOwners() {
        HashMap<String, UUID> traps = new HashMap<>();
        for (File file : Objects.requireNonNull(dataFolder.listFiles())) {
            if (file.getName().endsWith(".yml")) {
                YamlConfiguration trapConfig = YamlConfiguration.loadConfiguration(file);
                String trapName = trapConfig.getString("name");
                String ownerUUID = trapConfig.getString("owner");
                if (ownerUUID != null) {
                    traps.put(trapName, UUID.fromString(ownerUUID));
                }
            }
        }
        return traps;
    }
}
