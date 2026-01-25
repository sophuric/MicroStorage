package me.sophur.microstorage.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

public class WaterlogUtil {
    private WaterlogUtil() {
    }

    // https://wiki.fabricmc.net/tutorial:waterloggable

    public static @NotNull FluidState getFluidState(BlockState blockState) {
        return blockState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    public static BlockState getStateForPlacement(BlockState blockState, BlockPlaceContext ctx) {
        if (blockState == null) return null;
        blockState.setValue(WATERLOGGED, ctx.getLevel().getFluidState(ctx.getClickedPos()).is(Fluids.WATER));
        return blockState;
    }

    public static void updateShape(BlockState blockState, LevelAccessor level, BlockPos blockPos) {
        if (blockState.getValue(WATERLOGGED))
            level.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
    }
}
