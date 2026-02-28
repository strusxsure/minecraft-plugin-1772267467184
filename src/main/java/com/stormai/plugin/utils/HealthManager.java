package com.stormai.plugin.utils;

import com.stormai.plugin.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HealthManager {
    private final Plugin plugin;
    private final ConfigManager configManager;
    private final Map<UUID, Integer> playerHealths;
    private final File dataFile;

    public HealthManager(Plugin plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.playerHealths = new HashMap<>();
        this.dataFile = new File(plugin.getDataFolder(), "playerdata.yml");

        // Load existing data
        loadAllPlayers();
    }

    public int getPlayerMaxHealth(Player player) {
        UUID uuid = player.getUniqueId();
        return playerHealths.getOrDefault(uuid, 20);
    }

    public void setPlayerMaxHealth(Player player, int health) {
        UUID uuid = player.getUniqueId();
        int clampedHealth = Math.max(2, Math.min(40, health));
        playerHealths.put(uuid, clampedHealth);
        player.setMaxHealth(clampedHealth);
        savePlayer(uuid);
    }

    public void loadAllPlayers() {
        if (!dataFile.exists()) return;

        FileConfiguration config = YamlConfiguration.loadConfiguration(dataFile);
        for (String uuidStr : config.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(uuidStr);
                int health = config.getInt(uuidStr);
                playerHealths.put(uuid, health);
            } catch (IllegalArgumentException e) {
                // Invalid UUID, skip
            }
        }
    }

    public void saveAllPlayers() {
        FileConfiguration config = YamlConfiguration.loadConfiguration(dataFile);
        for (Map.Entry<UUID, Integer> entry : playerHealths.entrySet()) {
            config.set(entry.getKey().toString(), entry.getValue());
        }
        try {
            config.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save player data: " + e.getMessage());
        }
    }

    public void savePlayer(UUID uuid) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(dataFile);
        config.set(uuid.toString(), playerHealths.get(uuid));
        try {
            config.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save player data: " + e.getMessage());
        }
    }

    public void loadPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        int health = playerHealths.getOrDefault(uuid, 20);
        player.setMaxHealth(health);
    }
}