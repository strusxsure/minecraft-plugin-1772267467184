package com.stormai.plugin.listeners;

import com.stormai.plugin.Main;
import com.stormai.plugin.utils.HealthManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class LifeStealListener implements Listener {
    private final Main plugin;
    private final HealthManager healthManager;

    public LifeStealListener(Main plugin, HealthManager healthManager) {
        this.plugin = plugin;
        this.healthManager = healthManager;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player)) return;

        Player victim = (Player) entity;
        Player killer = null;

        // Find the actual killer (player) if it's a projectile
        EntityDamageEvent lastDamage = victim.getLastDamageCause();
        if (lastDamage instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) lastDamage;
            Entity damager = damageEvent.getDamager();

            if (damager instanceof Player) {
                killer = (Player) damager;
            } else if (damager instanceof Projectile) {
                Projectile projectile = (Projectile) damager;
                if (projectile.getShooter() instanceof Player) {
                    killer = (Player) projectile.getShooter();
                }
            }
        }

        // Only process if killer is a player
        if (killer != null && killer instanceof Player) {
            int victimHealth = healthManager.getPlayerMaxHealth(victim);
            int killerHealth = healthManager.getPlayerMaxHealth(killer);

            // Transfer health from victim to killer
            if (victimHealth > 2) {
                healthManager.setPlayerMaxHealth(victim, victimHealth - 2);
                if (killerHealth < 40) {
                    healthManager.setPlayerMaxHealth(killer, killerHealth + 2);
                    killer.sendMessage("§aYou stole 1 heart from " + victim.getName());
                }
            }

            // Check if victim should be eliminated
            if (victimHealth <= 2) {
                victim.spigot().respawn();
                victim.setHealth(0);
                victim.setGameMode(org.bukkit.GameMode.SPECTATOR);
                Bukkit.broadcastMessage("§c" + victim.getName() + " has been eliminated from the game!");
            }
        }
    }

    @EventHandler
    public void onPlayerConsumeHeart(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        if (HeartItem.isHeartItem(item)) {
            Player player = event.getPlayer();
            int currentHealth = healthManager.getPlayerMaxHealth(player);
            if (currentHealth < 40) {
                healthManager.setPlayerMaxHealth(player, currentHealth + 2);
                player.sendMessage("§aYou gained 1 heart!");
            } else {
                player.sendMessage("§cYou already have maximum health!");
            }
        }
    }
}