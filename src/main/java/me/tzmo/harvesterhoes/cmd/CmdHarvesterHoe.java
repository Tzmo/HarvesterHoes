package me.tzmo.harvesterhoes.cmd;

import me.tzmo.harvesterhoes.HarvesterHoes;
import me.tzmo.harvesterhoes.item.HarvesterHoe;
import me.tzmo.harvesterhoes.util.MiscUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class CmdHarvesterHoe implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        // /harvesterhoe give <player> <multiplier>
        // /harvesterhoe reload

        if (args.length == 0)
        {
            sender.sendMessage(MiscUtil.parse("&c/harvesterhoe give <player> <multiplier>"));
            sender.sendMessage(MiscUtil.parse("&c/harvesterhoe reload"));
            return false;
        }

        switch (args[0].toUpperCase())
        {
            case "GIVE":
                handleGive(sender, args);
                return false;
            case "RELOAD":
                handleReload(sender);
                return false;
            default:
                return false;
        }
    }

    public void handleGive(CommandSender sender, String[] args)
    {
        if (args.length != 3)
        {
            sender.sendMessage(MiscUtil.parse("&c/harvesterhoe give <player> <multiplier>"));
            return;
        }

        if (!args[0].equalsIgnoreCase("GIVE"))
        {
            sender.sendMessage(MiscUtil.parse("&c/harvesterhoe give <player> <multiplier>"));
            return;
        }

        Player player = Bukkit.getPlayer(args[1]);

        if (player == null)
        {
            sender.sendMessage(MiscUtil.parse("&cInvalid player."));
            return;
        }

        double multiplier = Double.parseDouble(args[2]);

        if (multiplier < 1.0)
        {
            sender.sendMessage(MiscUtil.parse("&cMultiplier must be at least 1.0"));
            return;
        }

        ItemStack itemStack = HarvesterHoe.getHarvesterHoe(multiplier, 0.0, 0);
        player.getInventory().addItem(itemStack);
        sender.sendMessage(MiscUtil.parse(
            Objects.requireNonNull(HarvesterHoes.getCfg().getString("lang-given-harvester-hoe"))
                .replace("{player}", player.getName())
                .replace("{multiplier}", String.valueOf(multiplier))
        ));
    }

    public void handleReload(CommandSender sender)
    {
        HarvesterHoes.reloadCfg();
        sender.sendMessage(MiscUtil.parse(Objects.requireNonNull(HarvesterHoes.getCfg().getString("lang-reloaded-config"))));
    }
}
