package mrhamzaless.com.atrap;

import mrhamzaless.com.atrap.commands.AtrapCommand;
import mrhamzaless.com.atrap.commands.tabcomplete.TrapTabComplete;
import mrhamzaless.com.atrap.gui.MemberListMenu;
import mrhamzaless.com.atrap.gui.SettingsMenu;
import mrhamzaless.com.atrap.gui.TrapPermissionsMenu;
import mrhamzaless.com.atrap.listeners.PlayerActionListener;
import mrhamzaless.com.atrap.managers.CacheManager;
import mrhamzaless.com.atrap.managers.TrapManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Atrap extends JavaPlugin {

    private static Atrap instance;

    private Map<UUID, Invite> inviteMap = new HashMap<>();

    public TrapManager getTrapManager() {
        return trapManager;
    }

    private class Invite {
        UUID trapOwner;
        String trapName;
        long timestamp;

        Invite(UUID trapOwner, String trapName) {
            this.trapOwner = trapOwner;
            this.trapName = trapName;
            this.timestamp = System.currentTimeMillis();
        }
    }

    private TrapManager trapManager;
    private FileConfiguration languageConfig;
    private FileConfiguration guiSettingsConfig;
    private FileConfiguration memberListConfig;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        createDefaultLanguageFile();
        createDefaultmemberListConfigFile();
        createDefaultGuiSettingsFile();

        languageConfig = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "language.yml"));
        guiSettingsConfig = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "gui/settings.yml"));
        memberListConfig = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "gui/memberlist.yml"));

        trapManager = new TrapManager(getDataFolder(), languageConfig);

        AtrapCommand atrapCommand = new AtrapCommand(trapManager, languageConfig, getConfig());
        getCommand("atrap").setExecutor(atrapCommand);
        getCommand("atrap").setTabCompleter(new TrapTabComplete());

        TrapPlaceholder placeholder = new TrapPlaceholder(trapManager, languageConfig);
        placeholder.register();

        PluginManager pm = Bukkit.getServer().getPluginManager();

        // listener
        pm.registerEvents(new SettingsMenu(), this);
        pm.registerEvents(new MemberListMenu(), this);
        pm.registerEvents(new TrapPermissionsMenu(), this);
        pm.registerEvents(new PlayerActionListener(), this);

        CacheManager.CreateCache();
    }

    @Override
    public void onDisable() {
    }

    private void createDefaultLanguageFile() {
        File languageFile = new File(getDataFolder(), "language.yml");
        if (!languageFile.exists()) {
            saveResource("language.yml", false);
        }
    }

    private void createDefaultGuiSettingsFile() {
        File file = new File(getDataFolder(), "gui/settings.yml");
        if (!file.exists()) {
            saveResource("gui/settings.yml", false);
        }
    }

    private void createDefaultmemberListConfigFile() {
        File file = new File(getDataFolder(), "gui/memberlist.yml");
        if (!file.exists()) {
            saveResource("gui/memberlist.yml", false);
        }
    }

    public File getTrapFile(String trapName) {
        return new File(getDataFolder(), "DataBase/Traps/" + trapName + ".yml");
    }

    public FileConfiguration getGuiSettingsConfig() {
        return guiSettingsConfig;
    }

    public FileConfiguration getMemberListConfig() {
        return memberListConfig;
    }

    public static Atrap getInstance() {
        return instance;
    }
}
