package com.coco.Config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.coco.Main.Main;

public class ConfigManager {

    private List<FileConfiguration> configs = new ArrayList<>();
    private List<File> files = new ArrayList<>();

    private final Main main;
    private final String[] customConfigFiles = {
        "data-regions.yml"
    };

    public ConfigManager(Main instance) {
        main = instance;
    }

    @Nonnull
    public int getConfigFileIndex(String configName) {
        for (int i = 0; i < customConfigFiles.length; i++) {
            if (customConfigFiles[i].equalsIgnoreCase(configName)) {
                return i;
            }
        }
        return 0;
    }

    @Nonnull
    public void setupEnvironment() {
        if (!main.getDataFolder().exists()) 
            main.getDataFolder().mkdir();
        
        for (String customConfigFile : customConfigFiles) {
            File file = new File(main.getDataFolder(), customConfigFile);
            FileConfiguration config = null;

            if (!file.exists()) {
                try {
                    file.createNewFile();
                    config = YamlConfiguration.loadConfiguration(file);

                    // load defaults
                    if (customConfigFile.equalsIgnoreCase("data-regions.yml"))
                        config.createSection("regions");
                    
                    config.save(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                config = YamlConfiguration.loadConfiguration(file);
            }

            configs.add(config);
            files.add(file);
        }
    }

    @Nonnull
    public void reloadConfig(String configName) {
        if (!main.getDataFolder().exists()) 
            main.getDataFolder().mkdir();
        
        for (int i = 0; i < customConfigFiles.length; i++) {
            if (customConfigFiles[i].equalsIgnoreCase(configName)) {
                File file = new File(main.getDataFolder(), configName);
                configs.set(i, YamlConfiguration.loadConfiguration(file));
                if (configName.equalsIgnoreCase(customConfigFiles[0]))
                    main.dataRegions = configs.get(i);
                
            }
        }
    }

    @Nonnull
    public FileConfiguration getConfigByName(String configName) {
        for (int i = 0; i < customConfigFiles.length; i++) 
            if (customConfigFiles[i].equalsIgnoreCase(configName)) 
                return configs.get(i);
        return null;
    }

    @Nonnull
    public File getFileByName(String configName) {
        try {
            for (int i = 0; i < customConfigFiles.length; i++) 
                if (customConfigFiles[i].equalsIgnoreCase(configName)) 
                    return files.get(i);
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    @Nonnull
    public void saveConfigByName(String configName) {
        try {
            for (int i = 0; i < customConfigFiles.length; i++) {
                if (customConfigFiles[i].equalsIgnoreCase(configName)) {
                    File file = new File(main.getDataFolder(), configName);
                    configs.get(i).save(file);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Nonnull
    public void saveAndReloadConfig(String configName) {
        saveConfigByName(configName);
        reloadConfig(configName);
    }
}