package me.tzmo.harvesterhoes.util;

import net.brcdev.shopgui.ShopGuiPlusApi;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MiscUtil {

    public static String parse(String string)
    {
        return string.replace("&", "§");
    }

    public static boolean isValidItem(ItemStack itemStack)
    {
        return itemStack != null && itemStack.getType() != Material.AIR && itemStack.getAmount() > 0;
    }

    public static List<Block> getSugarCaneBrokenAmount(Block block)
    {
        Location blockAbove = block.getLocation().clone().add(0.0, 1.0, 0.0);
        List<Block> blocks = new ArrayList<>();
        blocks.add(block);

        while (blockAbove.getBlock().getType() == Material.SUGAR_CANE)
        {
            blocks.add(blockAbove.getBlock());
            blockAbove.add(0.0, 1.0, 0.0);
        }

        return blocks;
    }

    public static double getSellPrice(Material item)
    {
        ItemStack itemStack = new ItemStack(item, 1);

        double price = ShopGuiPlusApi.getItemStackPriceSell(itemStack);

        if (price == -1.0)
        {
            price = 0.0;
        }

        return price;
    }
}
