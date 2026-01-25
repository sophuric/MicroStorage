package me.sophur.sophstorage.block;

import com.mojang.serialization.MapCodec;
import me.sophur.sophstorage.Blocks;
import me.sophur.sophstorage.VariantTypes;
import me.sophur.sophstorage.blockentity.InterfaceBlockEntity;
import me.sophur.sophstorage.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.solid.brrp.v1.api.RuntimeResourcePack;

import static me.sophur.sophstorage.util.Util.getModID;
import static net.minecraft.data.recipes.RecipeProvider.*;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

public class InterfaceBlock extends BaseEntityBlock implements SimpleWaterloggedBlock, ConnectingBlockUtil.ConnectingBlock, DataProvider<InterfaceBlock> {
    public InterfaceBlock(Properties properties) {
        super(properties);
        BlockState blockState = defaultBlockState().setValue(WATERLOGGED, false);
        ConnectingBlockUtil.createDefaultBlockState(blockState);
        registerDefaultState(blockState);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(InterfaceBlock::new);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new InterfaceBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED);
        ConnectingBlockUtil.createBlockStateDefinition(builder);
    }

    @Override
    protected @NotNull BlockState updateShape(BlockState blockState, Direction direction, BlockState neighborBlockState, LevelAccessor level, BlockPos blockPos, BlockPos neighborBlockPos) {
        WaterlogUtil.updateShape(blockState, level, blockPos);
        ConnectingBlockUtil.updateShape(this, blockState, direction, neighborBlockState, level, blockPos, neighborBlockPos);
        return super.updateShape(blockState, direction, neighborBlockState, level, blockPos, neighborBlockPos);
    }

    @Override
    public @NotNull BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState blockState = defaultBlockState();
        WaterlogUtil.getStateForPlacement(blockState, ctx);
        ConnectingBlockUtil.getStateForPlacement(this, blockState, ctx);
        return blockState;
    }

    @Override
    protected @NotNull FluidState getFluidState(BlockState state) {
        return WaterlogUtil.getFluidState(state);
    }

    public static void createOtherData(VariantUtil.VariantSet variantSet, InterfaceBlock block, RuntimeResourcePack pack) {
    }
    private static final TagKey<Block> blockTag = TagKey.create(Registries.BLOCK, getModID("interfaces"));

    @Override
    public void perItem(VariantUtil.VariantEntrySet<InterfaceBlock> variantEntrySet, VariantUtil.VariantSet variantSet, RuntimeResourcePack pack) {
        // TODO: group recipes for each types in the recipe book

        // create recipe
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, this)
                .define('C', Items.COMPARATOR)
                .define('R', Items.REDSTONE)
                .define('H', Items.HOPPER)
                .define('P', VariantTypes.getPlanksItem(variantSet.get(VariantTypes.WOOD_TYPE_VARIANT)))
                .pattern("PCP")
                .pattern("PRP")
                .pattern("PHP")
                .unlockedBy("has_hopper", has(Items.HOPPER))
                .save(pack.getRecipeExporter());

        pack.getBlockLootTableGenerator().dropSelf(this);
        Blocks.addTagElement(BlockTags.MINEABLE_WITH_AXE, Util.getID(this));
        Blocks.addTagElement(blockTag, Util.getID(this));
    }
}
