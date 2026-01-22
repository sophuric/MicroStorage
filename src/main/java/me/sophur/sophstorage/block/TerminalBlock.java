package me.sophur.sophstorage.block;

import me.sophur.sophstorage.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

public class TerminalBlock extends Block implements SimpleWaterloggedBlock {
    public static final EnumProperty<Direction> DIRECTION = EnumProperty.create("direction", Direction.class, BlockStateProperties.FACING.getPossibleValues());
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;

    public TerminalBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
                .setValue(DIRECTION, Direction.NORTH)
                .setValue(OPEN, false)
                .setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(DIRECTION, OPEN, WATERLOGGED);
    }

    private static final double THICKNESS = 2 / 16.0;
    private static final EnumMap<Direction, VoxelShape> SHAPE_MAP = new EnumMap<>(Map.of(
            Direction.WEST, Shapes.box(0, 0, 0, THICKNESS, 1, 1),
            Direction.DOWN, Shapes.box(0, 0, 0, 1, THICKNESS, 1),
            Direction.NORTH, Shapes.box(0, 0, 0, 1, 1, THICKNESS),
            Direction.EAST, Shapes.box(1 - THICKNESS, 0, 0, 1, 1, 1),
            Direction.UP, Shapes.box(0, 1 - THICKNESS, 0, 1, 1, 1),
            Direction.SOUTH, Shapes.box(0, 0, 1 - THICKNESS, 1, 1, 1)
    ));

    @Override
    protected @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE_MAP.get(state.getValue(DIRECTION));
    }

    @Override
    protected @NotNull BlockState updateShape(BlockState blockState, Direction direction, BlockState neighborBlockState, LevelAccessor level, BlockPos blockPos, BlockPos neighborBlockPos) {
        Util.updateShapeWaterlogged(blockState, level, blockPos);
        return super.updateShape(blockState, direction, neighborBlockState, level, blockPos, neighborBlockPos);
    }

    @Override
    public @NotNull BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState blockState = defaultBlockState().setValue(DIRECTION, ctx.getClickedFace().getOpposite());
        return Util.waterlogBlockState(blockState, ctx);
    }

    @Override
    protected @NotNull FluidState getFluidState(BlockState state) {
        return Util.getFluidState(state);
    }
}
