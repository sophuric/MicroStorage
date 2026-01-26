package me.sophur.microstorage.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import java.util.Map;

public class ConnectingBlockUtil {
    private ConnectingBlockUtil() {
    }

    // util class for blocks that can connect on each 6 sides

    public static BlockState createDefaultBlockState(BlockState blockState) {
        DIRECTION_PROPERTIES.values().forEach(prop -> blockState.setValue(prop, false));
        return blockState;
    }

    public static void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        DIRECTION_PROPERTIES.values().forEach(builder::add);
    }

    public static BlockState updateShape(ConnectingBlock block, BlockState blockState, Direction direction, BlockState neighborBlockState, LevelAccessor level, BlockPos blockPos, BlockPos neighborBlockPos) {
        if (!neighborBlockPos.equals(blockPos.offset(direction.getNormal()))) return blockState;
        return blockState.setValue(DIRECTION_PROPERTIES.get(direction),
                block.canConnect(blockState, direction, neighborBlockState, level, blockPos, neighborBlockPos));
    }

    public final static Map<Direction, BooleanProperty> DIRECTION_PROPERTIES = Map.of(
            Direction.NORTH, BlockStateProperties.NORTH,
            Direction.EAST, BlockStateProperties.EAST,
            Direction.SOUTH, BlockStateProperties.SOUTH,
            Direction.WEST, BlockStateProperties.WEST,
            Direction.UP, BlockStateProperties.UP,
            Direction.DOWN, BlockStateProperties.DOWN);

    public static BlockState getStateForPlacement(ConnectingBlock block, BlockState blockState, BlockPlaceContext ctx) {
        DIRECTION_PROPERTIES.forEach((direction, prop) -> {
            BlockPos blockPos = ctx.getClickedPos();
            Level level = ctx.getLevel();
            BlockPos neighborBlockPos = blockPos.offset(direction.getNormal());
            block.canConnect(blockState, direction, level.getBlockState(neighborBlockPos), level, blockPos, neighborBlockPos);
        });
        return blockState;
    }

    public interface ConnectingBlock {
        default boolean canConnect(BlockState blockState, Direction direction, BlockState neighborBlockState, LevelAccessor level, BlockPos blockPos, BlockPos neighborBlockPos) {
            return false;
            // return blockState.getBlock() instanceof ConnectingBlock;
        }
    }
}
