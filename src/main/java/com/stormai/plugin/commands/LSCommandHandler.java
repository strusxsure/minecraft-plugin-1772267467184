package com.stormai.plugin.commands;

import com.stormai.plugin.utils.HealthManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LSCommandHandler implements CommandExecutor {
    private final HealthManager healthManager;

    public LSCommandHandler(HealthManager healthManager) {
        this.healthManager = healthManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("lifesteal")) return false;

        if (args.length == 0) {
            sender.sendMessage("§6/Lifesteal withdraw <amount> - Convert health to Heart items");
            return true;
        }

        if (args[0].equalsIgnoreCase("withdraw")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cOnly players can use this command!");
                return true;
            }

            if (args.length < 2) {
                sender.sendMessage("§cUsage: /lifesteal withdraw <amount>");
                return true;
            }

            Player player = (Player) sender;
            int amount;
            try {
                amount = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid amount!");
                return true;
            }

            if (amount <= 0) {
                sender.sendMessage("§cAmount must be positive!");
                return true;
            }

            int currentHealth = healthManager.getPlayerMaxHealth(player);
            int maxHearts = 20;
            int availableHearts = (currentHealth / 2) - 2; // Minimum 1 heart (2 HP)

            if (amount > availableHearts) {
                sender.sendMessage("§cYou can only withdraw up to " + availableHearts + " hearts!");
                return true;
            }

            // Convert health to items
            for (int i = 0; i < amount; i++) {
                player.getInventory().addItem(HeartItem.createHeartItem());
            }

            // Reduce max health
            healthManager.setPlayerMaxHealth(player, currentHealth - (amount * 2));
            player.sendMessage("§aWithdrew " + amount + " heart" + (amount == 1 ? "" : "s"));
            return true;
        }

        return true;
    }
}