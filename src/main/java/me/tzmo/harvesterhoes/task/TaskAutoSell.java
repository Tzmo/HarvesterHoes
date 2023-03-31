package me.tzmo.harvesterhoes.task;

import me.tzmo.harvesterhoes.HarvesterHoes;
import me.tzmo.harvesterhoes.util.MiscUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class TaskAutoSell {

    public static void startAutoSellTask()
    {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(HarvesterHoes.getPlugin(), () ->
        {
            if (!HarvesterHoes.getCfg().getBoolean("enabled")) return;
            if (!HarvesterHoes.getCfg().getBoolean("auto-sell")) return;
            if (Bukkit.getOnlinePlayers().isEmpty()) return;

            double sellPrice = MiscUtil.getSellPrice(Material.SUGAR_CANE);
            Iterator<Map.Entry<Player, Integer>> it = HarvesterHoes.toSell.entrySet().iterator();

            while (it.hasNext())
            {
                Map.Entry<Player, Integer> entry = it.next();

                Player player = entry.getKey();
                int amount = entry.getValue();

                if (!player.isOnline()) continue;

                double sellAmount = amount * sellPrice;

                HarvesterHoes.getEconomy().depositPlayer(player, sellAmount);
                player.sendMessage(MiscUtil.parse(
                    Objects.requireNonNull(HarvesterHoes.getCfg().getString("lang-autosell-sell-player-message"))
                        .replace("{amount}", HarvesterHoes.df.format(amount))
                        .replace("{sell_amount}", HarvesterHoes.df.format(amount * sellPrice))
                ));

                if (HarvesterHoes.getCfg().getBoolean("auto-sell-log-to-console"))
                {
                    HarvesterHoes.getPlugin().getLogger().info(
                        Objects.requireNonNull(HarvesterHoes.getCfg().getString("lang-autosell-sell-console-message"))
                            .replace("{player}", player.getName())
                            .replace("{amount}", HarvesterHoes.df.format(amount))
                            .replace("{sell_amount}", HarvesterHoes.df.format(amount * sellPrice))
                    );
                }

                it.remove();
            }
        }, HarvesterHoes.getCfg().getInt("auto-sell-interval-seconds") * 20L, HarvesterHoes.getCfg().getInt("auto-sell-interval-seconds") * 20L);
    }
}
