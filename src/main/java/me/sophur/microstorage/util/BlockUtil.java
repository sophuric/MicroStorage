package me.sophur.microstorage.util;

import me.sophur.microstorage.util.VariantUtil.VariantType;
import me.sophur.microstorage.util.VariantUtil.VariantEntrySet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static me.sophur.microstorage.util.Util.*;

public class BlockUtil {
    private BlockUtil() {
    }

    public static <T extends Block> VariantEntrySet<T> register(TriFunction<VariantEntrySet<T>, VariantUtil.VariantSet, ResourceLocation, T> blockFactory, ResourceLocation baseID, boolean shouldRegisterItem, VariantType<?>... variants) {
        return register(blockFactory, baseID, shouldRegisterItem, Arrays.stream(variants).toList());
    }

    public static <T extends Block> VariantEntrySet<T> register(TriFunction<VariantEntrySet<T>, VariantUtil.VariantSet, ResourceLocation, T> blockFactory, ResourceLocation baseID, boolean shouldRegisterItem, Collection<VariantType<?>> variants) {
        VariantEntrySet<T> v = new VariantEntrySet<>(baseID, (variantEntrySet, variantSet) -> {
            ResourceLocation id = variantEntrySet.getID(variantSet);

            T block = blockFactory.apply(variantEntrySet, variantSet, id);
            if (block == null) return null;

            block = Registry.register(BuiltInRegistries.BLOCK, id, block);

            if (shouldRegisterItem) {
                var props = new Item.Properties();
                T finalBlock = block; // Java shenanigans
                Registry.register(BuiltInRegistries.ITEM, id, new BlockItem(finalBlock, props) {
                    @Override
                    public @NotNull Component getName(ItemStack stack) {
                        // allows overriding the getName method
                        return finalBlock.getName();
                    }
                });
            }

            return block;
        }, variants);
        v.registerWithVariantTypes();
        return v;
    }
}
