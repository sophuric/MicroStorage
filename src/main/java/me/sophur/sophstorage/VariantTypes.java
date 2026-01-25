package me.sophur.sophstorage;

import me.sophur.sophstorage.util.Util;
import me.sophur.sophstorage.util.VariantUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.WoodType;

public class VariantTypes {
    private VariantTypes() {
    }

    public static VariantUtil.VariantType<WoodType> WOOD_TYPE_VARIANT;
    public static VariantUtil.VariantType<DyeColor> DYE_VARIANT;
    public static VariantUtil.VariantType<Tiers> TIER_VARIANT;

    public static Item getPlanksItem(WoodType woodType) {
        return Util.getItem(getPlanksID(woodType));
    }

    public static Block getPlanksBlock(WoodType woodType) {
        return Util.getBlock(getPlanksID(woodType));
    }

    public static ResourceLocation getPlanksID(WoodType woodType) {
        return ResourceLocation.parse(woodType.name() + "_planks");
    }

    private static boolean init = false;

    public static void initialise() {
        if (init) return;
        init = true;

        WOOD_TYPE_VARIANT = new VariantUtil.VariantType<>(WoodType.class, WoodType::name, WoodType.values().toList(),
                wood -> Ingredient.of(Util.getItem(wood.name() + "_planks")));

        DYE_VARIANT = new VariantUtil.VariantType<>(DyeColor.class, DyeColor::getName, DyeColor.values(),
                dye -> Ingredient.of(Util.getItem(dye.name() + "_dye")));

        TIER_VARIANT = new VariantUtil.VariantType<>(Tiers.class, t -> t.name().toLowerCase(), Tiers.values(),
                Tiers::getRepairIngredient);
    }
}
