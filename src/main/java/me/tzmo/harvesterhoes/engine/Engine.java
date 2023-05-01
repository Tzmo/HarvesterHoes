package me.tzmo.harvesterhoes.engine;

import de.tr7zw.nbtapi.NBTItem;
import me.tzmo.harvesterhoes.HarvesterHoes;
import me.tzmo.harvesterhoes.item.HarvesterHoe;
import me.tzmo.harvesterhoes.util.MiscUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

public class Engine implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public static void handleBlockBreak(BlockBreakEvent event)
    {
        Player player = event.getPlayer();
        ItemStack currentHoeItemStack = player.getItemInHand();

        if (!HarvesterHoe.isHarvesterHoe(currentHoeItemStack)) return;

        if (!HarvesterHoes.getCfg().getBoolean("enabled"))
        {
            player.sendMessage(MiscUtil.parse(Objects.requireNonNull(HarvesterHoes.getCfg().getString("lang-disabled"))));
            event.setCancelled(true);
            return;
        }

        if (event.getBlock().getType() != Material.SUGAR_CANE)
        {
            player.sendMessage(MiscUtil.parse(Objects.requireNonNull(HarvesterHoes.getCfg().getString("lang-incorrect-block"))));
            event.setCancelled(true);
            return;
        }

        NBTItem nbtItem = new NBTItem(currentHoeItemStack);
        double multi = nbtItem.getDouble("harvesterhoe_multi");
        double multiExtra = (HarvesterHoes.getCfg().getBoolean("upgradable-multiplier")) ? nbtItem.getDouble("harvesterhoe_multi_extra") : 0.0;
        int alreadyMined = nbtItem.getInteger("harvesterhoe_mined");
        Location blockBelow = event.getBlock().getLocation().clone().add(0.0, -1.0, 0.0);

        if (blockBelow.getBlock().getType() != Material.SUGAR_CANE)
        {
            player.sendMessage(MiscUtil.parse(Objects.requireNonNull(HarvesterHoes.getCfg().getString("lang-cannot-break-bottom-block"))));
            event.setCancelled(true);
            return;
        }

        List<Block> blocksBroken = MiscUtil.getSugarCaneBrokenAmount(event.getBlock());
        ListIterator<Block> iterator = blocksBroken.listIterator(blocksBroken.size());

        while (iterator.hasPrevious())
        {
            Block listElement = iterator.previous();
            listElement.setType(Material.AIR);
        }

        if (alreadyMined > 0 && HarvesterHoes.getCfg().getBoolean("upgradable-multiplier"))
        {
            multiExtra = new BigDecimal(
                    (double) (alreadyMined / HarvesterHoes.getCfg().getInt("upgradable-multiplier-mine-amount")) * HarvesterHoes.getCfg().getDouble("upgradable-multiplier-increase-amount")
            ).setScale(2, RoundingMode.HALF_UP).doubleValue();

            if (multiExtra > HarvesterHoes.getCfg().getDouble("upgradable-multiplier-max-extra-multiplier"))
            {
                multiExtra = HarvesterHoes.getCfg().getDouble("upgradable-multiplier-max-extra-multiplier");
            }
        }

        if (!HarvesterHoes.caneMined.containsKey(player))
        {
            HarvesterHoes.caneMined.put(player, 1);
        } else
        {
            int amount = HarvesterHoes.caneMined.get(player);
            int newAmount = amount + 1;
            HarvesterHoes.caneMined.put(player, newAmount);
        }

        if (!player.hasMetadata("harvesterhoe_autosell"))
        {
            player.getInventory().addItem(new ItemStack(Material.SUGAR_CANE, (int) (blocksBroken.size() * (multi + multiExtra))));
        } else
        {
            if (!HarvesterHoes.toSell.containsKey(player))
            {
                HarvesterHoes.toSell.put(player,  (int) (blocksBroken.size() * (multi + multiExtra)));
            } else
            {
                int amount = HarvesterHoes.toSell.get(player);
                int newAmount = amount +  (int) (blocksBroken.size() * (multi + multiExtra));
                HarvesterHoes.toSell.put(player, newAmount);
            }
        }

        if (player.hasMetadata("harvesterhoe_lastupdate"))
        {
            long span = System.currentTimeMillis() - player.getMetadata("harvesterhoe_lastupdate").get(0).asLong();

            if (span >= ((long) HarvesterHoes.getCfg().getInt("item-update-interval-seconds") * 1000))
            {
                int recentlyMined = HarvesterHoes.caneMined.getOrDefault(player, 0);
                int newTotalAmountMined =  alreadyMined + recentlyMined;
                ItemStack newHoeItemStack = HarvesterHoe.getHarvesterHoe(multi, multiExtra, newTotalAmountMined);

                player.setItemInHand(newHoeItemStack);
                player.removeMetadata("harvesterhoe_lastupdate", HarvesterHoes.getPlugin());
                player.setMetadata("harvesterhoe_lastupdate", new FixedMetadataValue(HarvesterHoes.getPlugin(), System.currentTimeMillis()));
                HarvesterHoes.caneMined.remove(player);
            }
        } else
        {
            int recentlyMined = HarvesterHoes.caneMined.getOrDefault(player, 0);
            int newTotalAmountMined = alreadyMined + recentlyMined;
            ItemStack newHoeItemStack = HarvesterHoe.getHarvesterHoe(multi, multiExtra, newTotalAmountMined);

            player.setItemInHand(newHoeItemStack);
            player.setMetadata("harvesterhoe_lastupdate", new FixedMetadataValue(HarvesterHoes.getPlugin(), System.currentTimeMillis()));
            HarvesterHoes.caneMined.remove(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public static void handleBlockPlace(BlockPlaceEvent event)
    {
        if (event.getBlock().getType() != Material.SUGAR_CANE) return;

        Player player = event.getPlayer();
        Location blockBelow = event.getBlock().getLocation().clone().add(0.0, -1.0, 0.0);

        if (blockBelow.getBlock().getType() != Material.SUGAR_CANE) return;
        if (player.isOp()) return;

        player.sendMessage(MiscUtil.parse(Objects.requireNonNull(HarvesterHoes.getCfg().getString("lang-cannot-place-ontop-of-sugarcane"))));
        event.setCancelled(true);
    }
}
