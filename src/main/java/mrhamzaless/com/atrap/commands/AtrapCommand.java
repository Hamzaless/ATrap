package mrhamzaless.com.atrap.commands;

import mrhamzaless.com.atrap.Atrap;
import mrhamzaless.com.atrap.gui.SettingsMenu;
import mrhamzaless.com.atrap.managers.CacheManager;
import mrhamzaless.com.atrap.managers.TrapManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class AtrapCommand implements CommandExecutor {

    private final TrapManager trapManager;
    private FileConfiguration languageConfig;
    private FileConfiguration config;
    private String prefix;
    private File dataFolder;
    private FileConfiguration guiSettingsConfig;

    public AtrapCommand(TrapManager trapManager, FileConfiguration languageConfig, FileConfiguration config) {
        this.trapManager = trapManager;
        this.languageConfig = languageConfig;
        this.config = config;
        this.prefix = colorize(languageConfig.getString("prefix", ":server_prefix: §x&c&6&d&e&f&1›>"));
        this.dataFolder = new File(dataFolder, "DataBase/Traps");
        if (!this.dataFolder.exists()) {
            this.dataFolder.mkdirs();
        }
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(colorize("\n" + prefix + " " + languageConfig.getString("error.not_player", "&cBu komutu sadece oyuncular kullanabilir.") + "\n&r"));
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(colorize("\n" + prefix + " " + languageConfig.getString("error.no_argument", "&cBir argüman belirtmelisiniz: new, add, buy veya admin add.") + "\n&r"));
            return true;
        }

        String action = args[0].toLowerCase();

        switch (action) {
            case "add":
                handleAddChunk(player, args);
                break;
            case "buy":
                handleBuyTrap(player, args);
                break;
            case "admin":
                handleAdminCommand(player, args);
                break;
            case "reload":
                handleReloadCommand(player);
                break;
            case "rename":
                handleRenameTrap(player, args);
                break;
            case "invite":
                handleInviteCommand(player, args);
                break;
            case "accept":
                handleAcceptCommand(player, args);
                break;
            case "deny":
                handleDenyCommand(player, args);
                break;
            case "settings":
                handleSettingsMenu(player);
                break;
            case "leave":
                handleLeaveTrapCommand(player);
                break;
            default:
                player.sendMessage(colorize("\n" + prefix + " " + languageConfig.getString("error.invalid_argument", "&cGeçersiz argüman. Kullanılacak argümanlar: new, add, buy, admin add veya reload.") + "\n&r"));
                CacheManager.reloadCache();
                break;
        }

        return true;
    }

    private void handleSettingsMenu(Player player) {
        Location playerLocation = player.getLocation();
        String trapName = TrapManager.getTrapAtLocation(playerLocation);

        if (trapName == null) {
            player.sendMessage(colorize("\n" + prefix + " " + languageConfig.getString("error.no_trap_here", "&cBurada bir trap bulunmuyor.") + "\n&r"));
            return;
        }

        if (TrapManager.isOwner(player, trapName)) {
            player.sendMessage(colorize("\n" + prefix + " " + languageConfig.getString("error.not_owner", "&cBu trap sana ait değil!") + "\n&r"));
            return;
        }

        SettingsMenu.open(player);
        CacheManager.reloadCache();
    }



    private void handleDenyCommand(Player player, String[] args) {
        Atrap plugin = (Atrap) Bukkit.getPluginManager().getPlugin("Atrap");
        String prefix = languageConfig.getString("prefix", ":server_prefix: §x&c&6&d&e&f&1›>");

        if (args.length < 2) {
            player.sendMessage(colorize("\n" + prefix + " " + languageConfig.getString("error.no_trap_specified", "&cReddetmek istediğiniz trap'in adını belirtmelisiniz.") + "\n&r"));
            return;
        }

        String trapName = args[1];
        File trapFile = plugin.getTrapFile(trapName); // getDataFolder() kullanarak dosya yolunu al

        if (!trapFile.exists()) {
            player.sendMessage(colorize("\n" + prefix + " " + languageConfig.getString("error.trap_not_found", "&cTrap bulunamadı.") + "\n&r"));
            return;
        }

        YamlConfiguration trapConfig = YamlConfiguration.loadConfiguration(trapFile);
        List<String> invitedPlayers = trapConfig.getStringList("invitedPlayers");

        if (!invitedPlayers.contains(player.getUniqueId().toString())) {
            player.sendMessage(colorize("\n" + prefix + " " + languageConfig.getString("error.not_invited", "&cBu trap'e davet edilmediniz.") + "\n&r"));
            return;
        }

        invitedPlayers.remove(player.getUniqueId().toString());
        trapConfig.set("invitedPlayers", invitedPlayers);

        try {
            trapConfig.save(trapFile);
            player.sendMessage(colorize("\n" + prefix + " " + languageConfig.getString("info.invite_rejected", "&cTrap'e katılmayı reddettiniz.") + "\n&r"));
        } catch (IOException e) {
            e.printStackTrace();
            player.sendMessage(colorize("\n" + prefix + " " + languageConfig.getString("error.save_failed", "&cİşlem sırasında bir hata oluştu.") + "\n&r"));
        }
        CacheManager.reloadCache();
    }

    private void handleAcceptCommand(Player player, String[] args) {

        if (args.length < 2) {
            player.sendMessage(colorize("\n" + prefix + " " + languageConfig.getString("error.no_trap_specified", "&cKatılmak istediğiniz trap'in adını belirtmelisiniz.") + "\n&r"));
            return;
        }

        String trapName = args[1];
        String result = TrapManager.acceptInvite(player, trapName);

        if (result.equals("success")) {
            player.sendMessage(colorize("\n" + prefix + " " + languageConfig.getString("success.invite_accepted", "&aTrap'e başarıyla katıldınız!") + "\n&r"));
        } else {
            player.sendMessage(colorize("\n" + prefix + " " + result + "\n&r"));
        }
        CacheManager.reloadCache();
    }

    private void handleLeaveTrapCommand(Player player){
        if (TrapManager.CheckAllTrapsAndLeave(player))
        {
            player.sendMessage(colorize("\n" + prefix + " " + languageConfig.getString("success.left_trap", "&aTrap'ten başarıyla ayrıldınız!") + "\n&r"));
        }
        else{
            player.sendMessage(colorize("\n" + prefix + " " + languageConfig.getString("error.trap_left", "&cTrap'ten ayrılamadınız.") + "\n&r"));
        }
        CacheManager.reloadCache();
    }

    private void handleInviteCommand(Player player, String[] args) {

        String prefix = languageConfig.getString("prefix", ":server_prefix: §x§c§6§d§e§f§1›>");

        if (args.length < 2) {
            player.sendMessage(colorize("\n" + prefix + " " + languageConfig.getString("error.no_player_specified", "&cDavet etmek istediğiniz oyuncunun adını belirtmelisiniz.") + "\n&r"));
            return;
        }

        String invitedPlayerName = args[1];
        Player invitedPlayer = player.getServer().getPlayer(invitedPlayerName);

        if (invitedPlayer == null) {
            player.sendMessage(colorize("\n" + prefix + " " + languageConfig.getString("error.player_not_found", "&cOyuncu bulunamadı.") + "\n&r"));
            return;
        }

        if (invitedPlayer.equals(player)) {
            player.sendMessage(colorize("\n" + prefix + " " + languageConfig.getString("error.cannot_invite_self", "&cKendinizi trap'e davet edemezsiniz!") + "\n&r"));
            return;
        }

        Location playerLocation = player.getLocation();
        String trapName = TrapManager.getTrapAtLocation(playerLocation);

        if (trapName == null) {
            player.sendMessage(colorize("\n" + prefix + " " + languageConfig.getString("error.no_trap_here", "&cBurada bir trap bulunmuyor.") + "\n&r"));
            return;
        }

        // Trap'in sahibi olup olmadığını kontrol et
        if (TrapManager.isOwner(player, trapName)) {
            player.sendMessage(colorize("\n" + prefix + " " + languageConfig.getString("error.not_owner", "&cBu trap'e oyuncu davet edebilmek için trap'in sahibi olmalısınız.") + "\n&r"));
            return;
        }

        // Davet edilen oyuncunun zaten trap üyesi olup olmadığını kontrol et
        if (TrapManager.isMember(invitedPlayer, trapName)) {
            player.sendMessage(colorize("\n" + prefix + " " + languageConfig.getString("error.already_member", "&cBu oyuncu zaten trap'in üyesi!") + "\n&r"));
            return;
        }

        // Davet edilen oyuncunun zaten davet edilmiş olup olmadığını kontrol et
        if (TrapManager.isInvited(invitedPlayer, trapName)) {
            player.sendMessage(colorize("\n" + prefix + " " + languageConfig.getString("error.already_invited", "&cBu oyuncu zaten trap'e davet edilmiş!") + "\n&r"));
            return;
        }

        TrapManager.invitePlayerToTrap(trapName, invitedPlayer);
        player.sendMessage(colorize("\n" + prefix + " " + languageConfig.getString("success.invite_sent", "&aOyuncu başarıyla trap'e davet edildi!") + "\n&r"));

        // Tıklanabilir mesaj gönderme
        String inviteMessageText = colorize(languageConfig.getString("info.invited_to_trap", "&a%player% sizi trap'e davet etti: %trap%").replace("%player%", player.getName()).replace("%trap%", trapName));
        TextComponent inviteMessage = new TextComponent(inviteMessageText);

        TextComponent acceptButton = new TextComponent(colorize(languageConfig.getString("info.accept_button", "&a[kabul et]")));
        TextComponent rejectButton = new TextComponent(colorize(languageConfig.getString("info.reject_button", "&c[reddet]")));

        // Kabul et butonu
        acceptButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/atrap accept " + trapName));
        acceptButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Trap'e katılmayı kabul et")));

        // Reddet butonu
        rejectButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/atrap deny " + trapName));
        rejectButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Trap'e katılmayı reddet")));

        // Mesajı birleştirme
        TextComponent message = new TextComponent();
        message.addExtra("\n");
        message.addExtra(inviteMessage);
        message.addExtra(colorize("\n" + prefix + " ")); // Boşluk ekleyin
        message.addExtra(acceptButton);
        message.addExtra(" ");
        message.addExtra(rejectButton);
        message.addExtra("\n");

        invitedPlayer.spigot().sendMessage(message);
        CacheManager.reloadCache();
    }


    private void handleReloadCommand(Player player) {

        if (!player.hasPermission(config.getString("Admin-Permission"))) {
            player.sendMessage(colorize("\n" + prefix + " " + languageConfig.getString("error.no_permission", "&cBu komutu kullanma izniniz yok.") + "\n&r"));
            return;
        }

        long startTime = System.currentTimeMillis();

        FileConfiguration newLanguageConfig = YamlConfiguration.loadConfiguration(new File("plugins/Atrap/language.yml"));
        FileConfiguration newConfig = YamlConfiguration.loadConfiguration(new File("plugins/Atrap/config.yml"));
        FileConfiguration newGuiSettingsConfig = YamlConfiguration.loadConfiguration(new File("plugins/Atrap/gui/settings.yml"));

        this.languageConfig = newLanguageConfig;
        this.config = newConfig;
        this.guiSettingsConfig = newGuiSettingsConfig; // GUI yapılandırmasını güncelle

        this.prefix = colorize(languageConfig.getString("prefix", ":server_prefix: §x&c&6&d&e&f&1›>"));

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        player.sendMessage(colorize("\n" + prefix + " " + languageConfig.getString("success.reloaded", "&aEklenti başarıyla yeniden yüklendi. Süre: %time% ms").replace("%time%", String.valueOf(duration)) + "\n&r"));
        CacheManager.reloadCache();
    }


    private void handleNewTrap(Player player, String[] args) {

        if (!player.hasPermission(config.getString("Admin-Permission"))) {
            player.sendMessage(colorize("\n" + prefix + " " + languageConfig.getString("error.no_permission", "&cBu komutu kullanma izniniz yok.") + "\n&r"));
            return;
        }

        if (args.length < 2) {
            player.sendMessage(colorize("\n" + prefix + " " + languageConfig.getString("error.no_name", "&cBir isim belirtmelisiniz.") + "\n&r"));
            return;
        }

        String trapName = args[1];

        if (!trapName.matches("^[a-zA-Z0-9]+$")) {
            player.sendMessage(colorize("\n" + prefix + " " + languageConfig.getString("error.invalid_name", "&cTrap isimleri sadece harf ve rakam içerebilir.") + "\n&r"));
            return;
        }

        Location playerLocation = player.getLocation();

        if (TrapManager.createTrap(trapName, playerLocation, player)) {
            if (TrapManager.isChunkOccupied(playerLocation)) {
                player.sendMessage(colorize("\n" + prefix + " " + languageConfig.getString("error.chunk_occupied", "&cBu chunk zaten başka bir trap tarafından kullanılıyor.") + "\n&r"));
            } else {
                player.sendMessage(colorize("\n" + prefix + " " + languageConfig.getString("error.trap_exists", "&cBu isimde bir trap zaten mevcut.") + "\n&r"));
            }
            return;
        }

        player.sendMessage(colorize("\n" + prefix + " " + languageConfig.getString("success.trap_created", "&aTrap başarıyla oluşturuldu!") + "\n&r"));
        CacheManager.reloadCache();
    }

    private void handleRenameTrap(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(colorize("\n" + prefix + " " + getErrorMessage("not_player") + "\n&r"));
            return;
        }

        Location playerLocation = player.getLocation();
        String currentTrapName = TrapManager.getTrapAtLocation(playerLocation);

        if (currentTrapName == null) {
            sender.sendMessage(colorize("\n" + prefix + " " + getErrorMessage("no_trap_here") + "\n&r"));
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(colorize("\n" + prefix + " " + getErrorMessage("no_argument") + "\n&r"));
            return;
        }

        String newTrapName = args[1];
        if (!newTrapName.matches("[a-zA-Z0-9_]+")) {
            sender.sendMessage(colorize("\n" + prefix + " " + getErrorMessage("invalid_name") + "\n&r"));
            return;
        }

        File trapFile = new File("plugins/Atrap/DataBase/traps/" + currentTrapName + ".yml");
        if (!trapFile.exists()) {
            sender.sendMessage(colorize("\n" + prefix + " " + getErrorMessage("trap_not_found") + "\n&r"));
            return;
        }

        YamlConfiguration trapConfig = YamlConfiguration.loadConfiguration(trapFile);
        String trapOwnerUUID = trapConfig.getString("owner");
        if (trapOwnerUUID == null) {
            sender.sendMessage(colorize("\n" + prefix + " " + getErrorMessage("owner_not_found") + "\n&r"));
            return;
        }

        if (!trapOwnerUUID.equals(player.getUniqueId().toString())) {
            sender.sendMessage(colorize("\n" + prefix + " " + getErrorMessage("not_owner") + "\n&r"));
            return;
        }

        String result = TrapManager.renameTrap(player, currentTrapName, newTrapName);
        String successMessage = getSuccessMessage("trap_renamed");
        if (successMessage == null) {
            successMessage = "&aTrap ismi başarıyla değiştirildi! Yeni isim: %trap%";
        }

        if (result.equals("success")) {
            sender.sendMessage(colorize("\n" + prefix + " " + successMessage.replace("%trap%", newTrapName) + "\n&r"));
        } else {
            String errorMessage = getErrorMessage(result);
            if (errorMessage == null) {
                errorMessage = getErrorMessage("unknown_error");
            }
            sender.sendMessage(colorize("\n" + prefix + " " + errorMessage + "\n&r"));
        }
        CacheManager.reloadCache();
    }

    private String getErrorMessage(String key) {
        String message = languageConfig.getString("error." + key);
        return (message != null) ? message : "&cBir hata oluştu.";
    }

    private String getSuccessMessage(String key) {
        String message = languageConfig.getString("success." + key);
        return (message != null) ? message : "&aİşlem başarılı!";
    }

    private void handleAddChunk(Player player, String[] args) {

        if (!player.hasPermission(config.getString("Admin-Permission"))) {
            player.sendMessage(colorize("\n" + prefix + " " + languageConfig.getString("error.no_permission", "&cBu komutu kullanma izniniz yok.") + "\n&r"));
            return;
        }

        if (args.length < 2) {
            player.sendMessage(colorize("\n" + prefix + " " + languageConfig.getString("error.no_name", "&cChunk eklemek istediğiniz trap'in adını belirtmelisiniz.") + "\n&r"));
            return;
        }

        String trapName = args[1];
        Location playerLocation = player.getLocation();

        if (!TrapManager.addChunkToTrap(trapName, playerLocation)) {
            if (TrapManager.getTrapAtLocation(playerLocation) == null) {
                player.sendMessage(colorize("\n" + prefix + " " + languageConfig.getString("error.trap_not_found", "&cBurada trap bulunmuyor!") + "\n&r"));
            } else {
                player.sendMessage(colorize("\n" + prefix + " " + languageConfig.getString("error.chunk_owned_by_another_trap", "&cChunk zaten başka bir trap'e ait.") + "\n&r"));
            }
            return;
        }

        player.sendMessage(colorize("\n" + prefix + " " + languageConfig.getString("success.chunk_added", "&aChunk trap’e başarıyla eklendi!") + "\n&r"));
        CacheManager.reloadCache();
    }

    private void handleBuyTrap(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(colorize("\n" + prefix + " " + languageConfig.getString("error.not_player") + "\n&r"));
            return;
        }

        if (args.length < 2) {

            Location playerLocation = player.getLocation();
            String trapName = TrapManager.getTrapAtLocation(playerLocation);

            if (trapName == null) {
                player.sendMessage(colorize("\n" + prefix + " " + languageConfig.getString("error.no_trap_here") + "\n&r"));
                return;
            }

            args = new String[] { "buy", trapName };
        }

        String trapName = args[1];
        String result = TrapManager.buyTrap(player, trapName);

        String message;

        switch (result) {
            case "Trap bulunamadı":
                message = languageConfig.getString("error.trap_not_found");
                break;
            case "Trap zaten bir oyuncuya ait":
                message = languageConfig.getString("error.trap_already_owned");
                break;
            case "Yeterli hak yok":
                message = languageConfig.getString("error.insufficient_rights");
                break;
            case "Geçersiz UUID formatı":
                message = languageConfig.getString("error.invalid_uuid_format");
                break;
            case "Dosya kaydedilirken hata oluştu":
                message = languageConfig.getString("error.save_failed");
                break;
            case "Trap başarıyla sahiplenildi":
                message = languageConfig.getString("success.trap_claimed").replace("%trap%", trapName);
                break;
            default:
                message = languageConfig.getString("error.trap_not_claimed");
                break;
        }

        player.sendMessage(colorize("\n" + prefix + " " + message + "\n&r"));
        CacheManager.reloadCache();
    }

    private void handleAdminCommand(Player player, String[] args) {

        if (!player.hasPermission(config.getString("Admin-Permission"))) {
            player.sendMessage(colorize("\n" + prefix + " " + languageConfig.getString("error.no_permission", "&cBu komutu kullanma izniniz yok.") + "\n&r"));
            return;
        }

        if (args.length < 2) {
            player.sendMessage(colorize("\n" + prefix + " " + languageConfig.getString("error.missing_arguments", "&cEksik argümanlar. Kullanım: /atrap admin <komut> [ek argümanlar]") + "\n&r"));
            return;
        }

        if (args[1].equalsIgnoreCase("chunks") && Objects.equals(args[2], "add")) {
            if (args.length < 5) {
                player.sendMessage(colorize("\n" + prefix + " " + languageConfig.getString("error.missing_arguments", "&cEksik argümanlar. Kullanım: /atrap admin chunks add <oyuncu> <hak sayısı>") + "\n&r"));
                return;
            }

            try {
                Player targetPlayer = player.getServer().getPlayer(args[3]);
                if (targetPlayer == null) {
                    player.sendMessage(colorize("\n" + prefix + " " + languageConfig.getString("error.player_not_found", "&cOyuncu bulunamadı.") + "\n&r"));
                    return;
                }

                int additionalRights = Integer.parseInt(args[4]);
                File playerFile = new File("plugins/Atrap/DataBase/Players/" + targetPlayer.getUniqueId() + ".yml");
                YamlConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);
                int currentRights = playerConfig.getInt("trap_rights", 0);
                int maxRights = getMaxTrapRights(targetPlayer);

                if (currentRights + additionalRights > maxRights) {
                    player.sendMessage(colorize("\n" + prefix + " " + languageConfig.getString("error.rights_limit_exceeded", "&cBu oyuncunun alabileceği maksimum trap hakları aşıldı. Maksimum hak: %max%").replace("%max%", String.valueOf(maxRights)) + "\n&r"));
                    return;
                }

                List<String> ownedTraps = playerConfig.getStringList("Owned-Traps");
                if (ownedTraps == null) {
                    ownedTraps = new ArrayList<>();
                }
                playerConfig.set("trap_rights", currentRights + additionalRights);
                playerConfig.save(playerFile);

                String successMessage = languageConfig.getString("success.rights_given", "&aTrap hakları %player% için başarıyla verildi.");
                successMessage = successMessage.replace("%player%", targetPlayer.getName());
                player.sendMessage(colorize("\n" + prefix + " " + successMessage + "\n&r"));
            } catch (IOException exception) {
                exception.printStackTrace();
            } catch (NumberFormatException e) {
                player.sendMessage(colorize("\n" + prefix + " " + languageConfig.getString("error.invalid_number", "&cGeçersiz sayı formatı.") + "\n&r"));
            }
        }
        else if (args[1].equals("new")) {
            if (args.length < 3) {
                player.sendMessage(colorize("\n" + prefix + " " + languageConfig.getString("error.missing_arguments", "&cEksik argümanlar. Kullanım: /atrap admin new <trap adı>") + "\n&r"));
                return;
            }

            String trapName = args[2];

            if (!trapName.matches("^[a-zA-Z0-9]+$")) {
                player.sendMessage(colorize("\n" + prefix + " " + languageConfig.getString("error.invalid_name", "&cTrap isimleri sadece harf ve rakam içerebilir.") + "\n&r"));
                return;
            }

            Location playerLocation = player.getLocation();

            if (TrapManager.createTrap(trapName, playerLocation, player)) {
                if (TrapManager.isChunkOccupied(playerLocation)) {
                    player.sendMessage(colorize("\n" + prefix + " " + languageConfig.getString("error.chunk_occupied", "&cBu chunk zaten başka bir trap tarafından kullanılıyor.") + "\n&r"));
                } else {
                    player.sendMessage(colorize("\n" + prefix + " " + languageConfig.getString("error.trap_exists", "&cBu isimde bir trap zaten mevcut.") + "\n&r"));
                }
                return;
            }

            player.sendMessage(colorize("\n" + prefix + " " + languageConfig.getString("success.trap_created", "&aTrap başarıyla oluşturuldu!") + "\n&r"));
        }

        CacheManager.reloadCache();
    }


    private int getMaxTrapRights(Player player) {
        String[] permissions = {"atrap.rights.1", "atrap.rights.2", "atrap.rights.3"};
        for (int i = permissions.length - 1; i >= 0; i--) {
            if (player.hasPermission(permissions[i])) {
                return Integer.parseInt(permissions[i].replace("atrap.rights.", ""));
            }
        }
        return 0;
    }

    private String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}