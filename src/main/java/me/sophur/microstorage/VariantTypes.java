package me.sophur.microstorage;

import me.sophur.microstorage.util.Util;
import me.sophur.microstorage.util.VariantUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.state.properties.WoodType;

public class VariantTypes {
    private VariantTypes() {
    }

    public static VariantUtil.VariantType<WoodType> WOOD_TYPE_VARIANT;
    public static VariantUtil.VariantType<DyeColor> DYE_COLOR_VARIANT;
    public static VariantUtil.VariantType<Tiers> TIER_VARIANT;

    public static ResourceLocation getPlanksID(WoodType woodType) {
        return ResourceLocation.parse(woodType.name() + "_planks");
    }

    public static ResourceLocation getGlassID(DyeColor dyeColor) {
        return ResourceLocation.parse((dyeColor != null ? dyeColor.getName() + "_stained_" : "") + "glass");
    }

    public static ResourceLocation getDyeID(DyeColor dyeColor) {
        return ResourceLocation.parse(dyeColor.getName() + "_dye");
    }

    private static boolean init = false;

    public static void register() {
        if (init) return;
        init = true;

        WOOD_TYPE_VARIANT = new VariantUtil.VariantType<>(WoodType.class, WoodType::name, WoodType.values().toList(),
                wood -> Ingredient.of(Util.getItem(wood.name() + "_planks")));

        DYE_COLOR_VARIANT = new VariantUtil.VariantType<>(DyeColor.class, DyeColor::getName, DyeColor.values(),
                dye -> Ingredient.of(Util.getItem(dye.name() + "_dye")));

        TIER_VARIANT = new VariantUtil.VariantType<>(Tiers.class, t -> t.name().toLowerCase(), Tiers.values(),
                Tiers::getRepairIngredient);
    }
}
