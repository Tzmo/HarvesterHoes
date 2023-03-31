package me.tzmo.harvesterhoes.cmd;

import me.tzmo.harvesterhoes.HarvesterHoes;
import me.tzmo.harvesterhoes.util.MiscUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Objects;

public class CmdAutoSell implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (!(sender instanceof Player))
        {
            sender.sendMessage(MiscUtil.parse("&4&l[!] &cOnly players can run this command."));
            return false;
        }

        if (!HarvesterHoes.getCfg().getBoolean("auto-sell"))
        {
            sender.sendMessage(MiscUtil.parse(
                    Objects.requireNonNull(HarvesterHoes.getCfg().getString("lang-toggle-autosell-mode"))
            ));
            return false;
        }

        Player player = (Player) sender;

        if ((player.hasMetadata("harvesterhoe_autosell")))
        {
            player.removeMetadata("harvesterhoe_autosell", HarvesterHoes.getPlugin());
        } else
        {
            player.setMetadata("harvesterhoe_autosell", new FixedMetadataValue(HarvesterHoes.getPlugin(), true));
        }

        String mode = (player.hasMetadata("harvesterhoe_autosell")) ? "&aon" : "&coff";
        player.sendMessage(MiscUtil.parse(
                Objects.requireNonNull(HarvesterHoes.getCfg().getString("lang-toggle-autosell-mode"))
                    .replace("{mode}", mode)
        ));
        return false;
    }
}
