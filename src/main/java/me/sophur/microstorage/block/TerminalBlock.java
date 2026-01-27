package me.sophur.microstorage.block;

import com.mojang.serialization.MapCodec;
import me.sophur.microstorage.blockentity.TerminalBlockEntity;
import me.sophur.microstorage.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.solid.brrp.v1.api.RuntimeResourcePack;

import java.util.EnumMap;
import java.util.Map;

import static me.sophur.microstorage.Blocks.*;
import static me.sophur.microstorage.VariantTypes.WOOD_TYPE_VARIANT;
import static me.sophur.microstorage.VariantTypes.getPlanksID;
import static me.sophur.microstorage.util.Util.getModID;
import static net.minecraft.data.recipes.RecipeProvider.has;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

public class TerminalBlock extends BaseEntityBlock implements SimpleWaterloggedBlock, DataProvider<TerminalBlock> {
    public static final EnumProperty<Direction> DIRECTION = EnumProperty.create("direction", Direction.class, BlockStateProperties.FACING.getPossibleValues());
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;

    private final VariantUtil.VariantSet variantSet;

    public TerminalBlock(Properties properties, VariantUtil.VariantSet variantSet) {
        super(properties);
        this.variantSet = variantSet;
        registerDefaultState(defaultBlockState()
                .setValue(DIRECTION, Direction.NORTH)
                .setValue(OPEN, false)
                .setValue(WATERLOGGED, false));
    }

    public TerminalBlock(VariantUtil.VariantSet variantSet) {
        // copy planks block
        this(BlockBehaviour.Properties.ofFullCopy(Util.getBlock(getPlanksID(variantSet.get(WOOD_TYPE_VARIANT)))), variantSet);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec((properties) -> new TerminalBlock(properties, variantSet));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TerminalBlockEntity(pos, state);
    }

    @Override
    protected @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(DIRECTION, OPEN, WATERLOGGED);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof TerminalBlockEntity terminalBlockEntity) {
                player.openMenu(terminalBlockEntity);
                PiglinAi.angerNearbyPiglins(player, true);
            }

            return InteractionResult.CONSUME;
        }
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
    protected @NotNull VoxelShape getShape(BlockState blockState, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE_MAP.get(blockState.getValue(DIRECTION));
    }

    private BaseContainerBlockEntity getContainer(BlockState blockState, BlockGetter level, BlockPos pos) {
        BlockPos containerPos = pos.offset(blockState.getValue(DIRECTION).getNormal());
        BlockEntity entity = level.getBlockEntity(containerPos);
        if (entity instanceof TerminalBlockEntity) return null; // explicitly prevent itself
        if (entity instanceof BaseContainerBlockEntity container) return container;
        return null;
    }

    @Override
    protected boolean canSurvive(BlockState blockState, LevelReader level, BlockPos pos) {
        return getContainer(blockState, level, pos) != null;
    }

    @Override
    protected @NotNull BlockState updateShape(BlockState blockState, Direction direction, BlockState neighborBlockState, LevelAccessor level, BlockPos blockPos, BlockPos neighborBlockPos) {
        if (!canSurvive(blockState, level, blockPos)) {
            return Blocks.AIR.defaultBlockState();
        }
        WaterlogUtil.updateShape(blockState, level, blockPos);
        return super.updateShape(blockState, direction, neighborBlockState, level, blockPos, neighborBlockPos);
    }

    @Override
    public @NotNull BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState blockState = defaultBlockState().setValue(DIRECTION, ctx.getClickedFace().getOpposite());
        return WaterlogUtil.getStateForPlacement(blockState, ctx);
    }

    @Override
    protected @NotNull FluidState getFluidState(BlockState state) {
        return WaterlogUtil.getFluidState(state);
    }

    private static final TagKey<Block> blockTag = TagKey.create(Registries.BLOCK, getModID("terminals"));

    @Override
    public void perItem(VariantUtil.VariantEntrySet<TerminalBlock> variantEntrySet, VariantUtil.VariantSet variantSet, RuntimeResourcePack pack) {
        // create recipe
        addRecipe(pack, ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, this)
                .define('R', Items.REDSTONE)
                .define('H', Items.HOPPER)
                .define('P', Util.getItem(getPlanksID(variantSet.get(WOOD_TYPE_VARIANT))))
                .pattern("RHR")
                .pattern("PPP")
                .group("microstorage_terminal")
                .unlockedBy("has_hopper", has(Items.HOPPER)), this);

        addBlockDrop(pack, this, v -> v.createSingleItemTable(this));
        addTagElement(BlockTags.MINEABLE_WITH_AXE, Util.getID(this));
        addTagElement(blockTag, Util.getID(this));
    }
}
