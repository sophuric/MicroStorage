package me.sophur.sophstorage;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static me.sophur.sophstorage.SophStorage.MOD_ID;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

public class Util {
    private Util() {
    }

    public static ResourceLocation getID(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static ResourceLocation getIDFromExistingID(ResourceLocation existingID) {
        return getID(existingID.toString().replaceAll(":", "/"));
    }
    public static ResourceLocation getIDFromExistingID(String existingID) {
        return getIDFromExistingID(ResourceLocation.parse(existingID));
    }

    public static final Map<Direction, BooleanProperty> DirectionProperties = Map.of(
            Direction.NORTH, BlockStateProperties.NORTH,
            Direction.EAST, BlockStateProperties.EAST,
            Direction.SOUTH, BlockStateProperties.SOUTH,
            Direction.WEST, BlockStateProperties.WEST,
            Direction.UP, BlockStateProperties.UP,
            Direction.DOWN, BlockStateProperties.DOWN);

    public static ResourceLocation addPrefixSuffix(ResourceLocation location, String prefix, String suffix) {
        return ResourceLocation.fromNamespaceAndPath(location.getNamespace(), prefix + location.getPath() + suffix);
    }

    public static ResourceLocation addSuffix(ResourceLocation location, String suffix) {
        return addPrefixSuffix(location, "", suffix);
    }

    public static ResourceLocation addPrefix(ResourceLocation location, String prefix) {
        return addPrefixSuffix(location, prefix, "");
    }

    public static int moduloPositive(int dividend, int divisor) {
        if (divisor < 0) divisor = -divisor;
        dividend %= divisor;
        if (dividend < 0) dividend += divisor; // wraps number around to always be positive
        return dividend;
    }

    // https://wiki.fabricmc.net/tutorial:waterloggable

    public static @NotNull FluidState getFluidState(BlockState blockState) {
        return blockState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    public static BlockState waterlogBlockState(BlockState blockState, BlockPlaceContext ctx) {
        if (blockState == null) return null;
        return blockState.setValue(WATERLOGGED, ctx.getLevel().getFluidState(ctx.getClickedPos()).is(Fluids.WATER));
    }

    public static void updateShapeWaterlogged(BlockState blockState, LevelAccessor levelAccessor, BlockPos blockPos) {
        if (blockState.getValue(WATERLOGGED))
            levelAccessor.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
    }
}
