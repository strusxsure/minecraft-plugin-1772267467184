package com.stormai.plugin;

import com.stormai.plugin.commands.LSCommandHandler;
import com.stormai.plugin.listeners.LifeStealListener;
import com.stormai.plugin.utils.ConfigManager;
import com.stormai.plugin.utils.HealthManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private static Main instance;
    private HealthManager healthManager;
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        instance = this;

        // Initialize managers
        configManager = new ConfigManager(this);
        healthManager = new HealthManager(this, configManager);

        // Register events
        getServer().getPluginManager().registerEvents(new LifeStealListener(this, healthManager), this);

        // Register commands
        getCommand("lifesteal").setExecutor(new LSCommandHandler(healthManager));

        // Register Heart item recipe
        getServer().addRecipe(HeartItem.createHeartRecipe());

        getLogger().info("LifeSteal plugin enabled!");
    }

    @Override
    public void onDisable() {
        // Save all player data
        healthManager.saveAllPlayers();
        getLogger().info("LifeSteal plugin disabled!");
    }

    public static Main getInstance() {
        return instance;
    }

    public HealthManager getHealthManager() {
        return healthManager;
    }
}