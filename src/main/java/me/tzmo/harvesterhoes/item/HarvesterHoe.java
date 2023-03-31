package me.tzmo.harvesterhoes.item;

import de.tr7zw.nbtapi.NBTItem;
import me.tzmo.harvesterhoes.HarvesterHoes;
import me.tzmo.harvesterhoes.util.MiscUtil;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HarvesterHoe {

    public static ItemStack getHarvesterHoe(double multi, double multiExtra, int mined)
    {
        ItemStack itemStack = new ItemStack(Material.DIAMOND_HOE, 1);
        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.setBoolean("harvesterhoe", true);
        nbtItem.setDouble("harvesterhoe_multi", multi);
        nbtItem.setDouble("harvesterhoe_multi_extra", multiExtra);
        nbtItem.setInteger("harvesterhoe_mined", mined);
        nbtItem.applyNBT(itemStack);

        List<String> itemLore = new ArrayList<>();

        for (String lore : HarvesterHoes.getCfg().getStringList("item-lore"))
        {
            lore = lore
                .replace("{multiplier}", String.valueOf(multi))
                .replace("{extra_multiplier}", String.valueOf(multiExtra))
                .replace("{amount}", String.valueOf(mined));
            itemLore.add(MiscUtil.parse(lore));
        }

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(MiscUtil.parse(Objects.requireNonNull(HarvesterHoes.getCfg().getString("item-display-name"))));
        itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        itemMeta.setUnbreakable(true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        itemMeta.setLore(itemLore);
        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public static boolean isHarvesterHoe(ItemStack itemStack)
    {
        if (!MiscUtil.isValidItem(itemStack)) return false;

        NBTItem nbtItem = new NBTItem(itemStack);

        return nbtItem.hasKey("harvesterhoe");
    }

}
