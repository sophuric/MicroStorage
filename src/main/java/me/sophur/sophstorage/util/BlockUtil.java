package me.sophur.sophstorage.util;

import me.sophur.sophstorage.SophStorage;
import me.sophur.sophstorage.util.VariantUtil.VariantType;
import me.sophur.sophstorage.util.VariantUtil.VariantEntrySet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import pers.solid.brrp.v1.api.RuntimeResourcePack;

import java.util.*;

import static me.sophur.sophstorage.util.Util.*;

public class BlockUtil {
    private BlockUtil() {
    }

    public static <T extends Block> VariantEntrySet<T> register(TriFunction<VariantEntrySet<T>, VariantUtil.VariantSet, ResourceLocation, T> blockFactory, ResourceLocation baseID, boolean shouldRegisterItem, VariantType<?>... variants) {
        return register(blockFactory, baseID, shouldRegisterItem, Arrays.stream(variants).toList());
    }

    public static <T extends Block> VariantEntrySet<T> register(TriFunction<VariantEntrySet<T>, VariantUtil.VariantSet, ResourceLocation, T> blockFactory, ResourceLocation baseID, boolean shouldRegisterItem, Collection<VariantType<?>> variants) {
        return new VariantEntrySet<>(baseID, (variantEntrySet, variantSet) -> {
            ResourceLocation id = variantEntrySet.getID(variantSet);

            T block = blockFactory.apply(variantEntrySet, variantSet, id);
            if (block == null) return null;

            block = Registry.register(BuiltInRegistries.BLOCK, id, block);

            if (shouldRegisterItem) {
                var props = new Item.Properties();
                Registry.register(BuiltInRegistries.ITEM, id, new BlockItem(block, props));
            }

            return block;
        }, variants);
    }
}
