package com.hawkfalcon.lprotect;

import com.hawkfalcon.lprotect.commands.*;
import com.hawkfalcon.lprotect.util.Lang;
import com.hawkfalcon.lprotect.util.Utilities;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.logging.Level;

public class LProtect extends JavaPlugin {

    public YamlConfiguration LANG;
    public File LANG_FILE;

    private WorldGuardPlugin worldGuard;
    private ProtectManager protectManager;
    private Utilities utils;
    private ConfigManager configManager;

    public void onEnable() {
        this.saveDefaultConfig();
        PluginManager pm = getServer().getPluginManager();

        configManager = new ConfigManager(this);
        protectManager = new ProtectManager(this);

        utils = new Utilities(this);
        pm.registerEvents(new PotionListener(this), this);
        registerCommands();
        worldGuard = WGBukkit.getPlugin();
        loadLang();
    }

    public void registerCommands() {
        CommandHandler handler = new CommandHandler();

        handler.register("protect", new ProtectCommand());

        handler.register("claim", new ClaimCommand(this));
        handler.register("list", new ListCommand(this));
        handler.register("potion", new PotionCommand(this));
        handler.register("remove", new RemoveCommand(this));

        handler.register("addplayer", new AddPlayerCommand(this));
        handler.register("removeplayer", new RemovePlayerCommand(this));
        handler.register("toggle", new ToggleCommand(this));


        getCommand("protect").setExecutor(handler);
    }

    private void loadLang() {
        File lang = new File(getDataFolder(), "lang.yml");
        OutputStream out = null;
        InputStream defLangStream = getResource("lang.yml");
        if (!lang.exists()) {
            try {
                getDataFolder().mkdir();
                lang.createNewFile();
                if (defLangStream != null) {
                    out = new FileOutputStream(lang);
                    int read = 0;
                    byte[] bytes = new byte[1024];

                    while ((read = defLangStream.read(bytes)) != -1) {
                        out.write(bytes, 0, read);
                    }
                    YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defLangStream);
                    Lang.setFile(defConfig);
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace(); // So they notice
                Bukkit.getLogger().severe("Couldn't create language file.");
                Bukkit.getLogger().severe("This is a fatal error. Now disabling");
                setEnabled(false); // Without it loaded, we can't send them messages
            } finally {
                if (defLangStream != null) {
                    try {
                        defLangStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
        YamlConfiguration conf = YamlConfiguration.loadConfiguration(lang);
        for (Lang item : Lang.values()) {
            if (conf.getString(item.getPath()) == null) {
                conf.set(item.getPath(), item.getDefault());
            }
        }
        Lang.setFile(conf);
        LANG = conf;
        LANG_FILE = lang;
        try {
            conf.save(getLangFile());
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.WARNING, "Failed to save lang.yml.");
            e.printStackTrace();
        }
    }

    /**
     * Gets the lang.yml config.
     *
     * @return The lang.yml config.
     */
    public YamlConfiguration getLang() {
        return LANG;
    }

    /**
     * Get the lang.yml file.
     *
     * @return The lang.yml file.
     */
    public File getLangFile() {
        return LANG_FILE;
    }

    public WorldGuardPlugin getWorldGuard() {
        return worldGuard;
    }

    public ProtectManager getProtectManager() {
        return protectManager;
    }

    public Utilities getUtils() {
        return utils;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}
