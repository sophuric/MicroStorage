package me.sophur.sophstorage.util;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Collection;

public class BlockEntityUtil {
    private BlockEntityUtil() {
    }

    public static <T extends BlockEntity, U extends Block> BlockEntityType<T> register(ResourceLocation id, BlockEntityType.BlockEntitySupplier<T> factory, Class<U> blockClass, Collection<U> blocks) {
        return register(id, factory, Util.toArray(blockClass, blocks));
    }

    @SafeVarargs
    public static <T extends BlockEntity, U extends Block> BlockEntityType<T> register(ResourceLocation id, BlockEntityType.BlockEntitySupplier<T> factory, U ...blocks) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, BlockEntityType.Builder.of(factory, blocks).build());
    }
}
