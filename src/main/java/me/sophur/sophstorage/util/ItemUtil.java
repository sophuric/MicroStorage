package me.sophur.sophstorage.util;

import me.sophur.sophstorage.SophStorage;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;

public class ItemUtil {
    private ItemUtil() {
    }

    public static <T extends Item> VariantUtil.VariantEntrySet<T> register(Util.TriFunction<VariantUtil.VariantEntrySet<T>, VariantUtil.VariantSet, ResourceLocation, T> itemFactory, ResourceLocation baseID, VariantUtil.VariantType<?>... variants) {
        return register(itemFactory, baseID, Arrays.stream(variants).toList());
    }

    public static <T extends Item> VariantUtil.VariantEntrySet<T> register(Util.TriFunction<VariantUtil.VariantEntrySet<T>, VariantUtil.VariantSet, ResourceLocation, T> itemFactory, ResourceLocation baseID, Collection<VariantUtil.VariantType<?>> variants) {
        return new VariantUtil.VariantEntrySet<>(baseID, (var, variantSet) -> {
            ResourceLocation id = var.getID(variantSet);

            T item = itemFactory.apply(var, variantSet, id);
            if (item == null) return null;

            item = Registry.register(BuiltInRegistries.ITEM, id, item);

            return item;
        }, variants);
    }
}
