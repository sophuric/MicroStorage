package me.sophur.microstorage.util;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Arrays;
import java.util.Collection;

public class BlockEntityUtil {
    private BlockEntityUtil() {
    }

    @SafeVarargs
    public static <T extends BlockEntity, U extends Block> BlockEntityType<T> register(ResourceLocation id, BlockEntityType.BlockEntitySupplier<T> factory, Class<U> blockClass, Collection<U>... blocks) {
        // allow supplying multiple lists to concat them together
        var flatBlocks = Util.toArray(blockClass, Arrays.stream(blocks).flatMap(Collection::stream).toList());
        return register(id, factory, flatBlocks);
    }

    @SafeVarargs
    public static <T extends BlockEntity, U extends Block> BlockEntityType<T> register(ResourceLocation id, BlockEntityType.BlockEntitySupplier<T> factory, U... blocks) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, BlockEntityType.Builder.of(factory, blocks).build());
    }
}
