package mrhamzaless.com.atrap;

import java.io.File;
import java.util.UUID;

import mrhamzaless.com.atrap.managers.TrapManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TrapPlaceholder extends PlaceholderExpansion {
    private final TrapManager trapManager;
    private final FileConfiguration languageConfig;

    public TrapPlaceholder(TrapManager trapManager, FileConfiguration languageConfig) {
        this.trapManager = trapManager;
        this.languageConfig = languageConfig;
    }

    public @NotNull String getIdentifier() {
        return "atrap";
    }

    public @NotNull String getAuthor() {
        return "StayLing";
    }

    public @NotNull String getVersion() {
        return "1.0";
    }

    public String onRequest(OfflinePlayer player, String identifier) {
        if (player != null && player.isOnline()) {
            Player onlinePlayer = player.getPlayer();
            Location playerLocation = onlinePlayer.getLocation();
            String trapName;
            if (identifier.equalsIgnoreCase("trap")) {
                trapName = TrapManager.getTrapAtLocation(playerLocation);
                return trapName != null ? trapName : this.colorize(this.languageConfig.getString("placeholder.not_in_trap", "Trapte değilsin"));
            } else if (identifier.equalsIgnoreCase("trap_owner")) {
                trapName = TrapManager.getTrapAtLocation(playerLocation);
                if (trapName != null) {
                    String ownerUUID = TrapManager.getTrapOwner(trapName);
                    if (ownerUUID != null && !ownerUUID.equals("none")) {
                        OfflinePlayer owner = Bukkit.getOfflinePlayer(UUID.fromString(ownerUUID));
                        return owner.getName() != null ? this.colorize(owner.getName()) : this.colorize(this.languageConfig.getString("placeholder.trap_no_owner", "Bilinmeyen"));
                    } else {
                        return this.colorize(this.languageConfig.getString("placeholder.trap_no_owner", "Sahip yok"));
                    }
                } else {
                    return this.colorize(this.languageConfig.getString("placeholder.not_in_trap", "Trapte değilsin"));
                }
            } else if (identifier.equalsIgnoreCase("trap_chunks")) {
                trapName = TrapManager.getTrapAtLocation(playerLocation);
                if (trapName != null) {
                    int chunkCount = TrapManager.getTrapChunkCount(trapName);
                    return String.valueOf(chunkCount);
                } else {
                    return this.colorize(this.languageConfig.getString("placeholder.not_in_trap", "Trapte değilsin"));
                }
            } else if (identifier.equalsIgnoreCase("trap_rights")) {
                File playerFile = new File("plugins/Atrap/DataBase/Players/" + String.valueOf(player.getUniqueId()) + ".yml");
                if (playerFile.exists()) {
                    FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);
                    int trapRights = playerConfig.getInt("trap_rights", 0);
                    return String.valueOf(trapRights);
                } else {
                    return this.colorize(this.languageConfig.getString("placeholder.error_player_not_found", "Oyuncu dosyası bulunamadı"));
                }
            } else if (identifier.equalsIgnoreCase("trap_file")) {
                trapName = this.trapManager.getTrapAtLocation(onlinePlayer.getLocation());
                return trapName != null ? this.colorize(this.trapManager.getTrapFileName(trapName)) : this.colorize(this.languageConfig.getString("placeholder.not_in_trap", "Trapte değilsin"));
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
