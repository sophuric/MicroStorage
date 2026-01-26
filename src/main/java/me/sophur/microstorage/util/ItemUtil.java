package me.sophur.microstorage.util;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.Arrays;
import java.util.Collection;

public class ItemUtil {
    private ItemUtil() {
    }

    public static <T extends Item> VariantUtil.VariantEntrySet<T> register(Util.TriFunction<VariantUtil.VariantEntrySet<T>, VariantUtil.VariantSet, ResourceLocation, T> itemFactory, ResourceLocation baseID, VariantUtil.VariantType<?>... variants) {
        return register(itemFactory, baseID, Arrays.stream(variants).toList());
    }

    public static <T extends Item> VariantUtil.VariantEntrySet<T> register(Util.TriFunction<VariantUtil.VariantEntrySet<T>, VariantUtil.VariantSet, ResourceLocation, T> itemFactory, ResourceLocation baseID, Collection<VariantUtil.VariantType<?>> variants) {
        VariantUtil.VariantEntrySet<T> v = new VariantUtil.VariantEntrySet<>(baseID, (variantEntrySet, variantSet) -> {
            ResourceLocation id = variantEntrySet.getID(variantSet);

            T item = itemFactory.apply(variantEntrySet, variantSet, id);
            if (item == null) return null;

            item = Registry.register(BuiltInRegistries.ITEM, id, item);

            return item;
        }, variants);
        v.registerWithVariantTypes();
        return v;
    }
}
