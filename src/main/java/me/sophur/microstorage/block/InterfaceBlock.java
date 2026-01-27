package me.sophur.microstorage.block;

import com.mojang.serialization.MapCodec;
import me.sophur.microstorage.Blocks;
import me.sophur.microstorage.VariantTypes;
import me.sophur.microstorage.blockentity.InterfaceBlockEntity;
import me.sophur.microstorage.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.solid.brrp.v1.api.RuntimeResourcePack;

import static me.sophur.microstorage.Blocks.*;
import static me.sophur.microstorage.VariantTypes.DYE_COLOR_VARIANT;
import static me.sophur.microstorage.util.Util.getModID;
import static net.minecraft.data.recipes.RecipeProvider.*;

public class InterfaceBlock extends BaseEntityBlock implements ConnectingBlockUtil.ConnectingBlock, DataProvider<InterfaceBlock> {
    private final VariantUtil.VariantEntrySet<InterfaceBlock> variantEntrySet;
    private final VariantUtil.VariantSet variantSet;

    public InterfaceBlock(Properties properties, VariantUtil.VariantEntrySet<InterfaceBlock> variantEntrySet, VariantUtil.VariantSet variantSet) {
        super(properties);
        this.variantEntrySet = variantEntrySet;
        this.variantSet = variantSet;
        BlockState blockState = defaultBlockState();
        blockState = ConnectingBlockUtil.createDefaultBlockState(blockState);
        registerDefaultState(blockState);
    }

    public InterfaceBlock(VariantUtil.VariantEntrySet<InterfaceBlock> variantEntrySet, VariantUtil.VariantSet variantSet) {
        this(TrimBlock.PROPERTIES, variantEntrySet, variantSet);
    }

    @Override
    public @NotNull MutableComponent getName() {
        return variantEntrySet.getComponent("block", variantSet);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec((properties) -> new InterfaceBlock(properties, variantEntrySet, variantSet));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new InterfaceBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        ConnectingBlockUtil.createBlockStateDefinition(builder);
    }

    @Override
    protected @NotNull BlockState updateShape(BlockState blockState, Direction direction, BlockState neighborBlockState, LevelAccessor level, BlockPos blockPos, BlockPos neighborBlockPos) {
        blockState = ConnectingBlockUtil.updateShape(this, blockState, direction, neighborBlockState, level, blockPos, neighborBlockPos);
        return blockState;
    }

    @Override
    public @NotNull BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState blockState = defaultBlockState();
        blockState = ConnectingBlockUtil.getStateForPlacement(this, blockState, ctx);
        return blockState;
    }

    private static final TagKey<Block> blockTag = TagKey.create(Registries.BLOCK, getModID("interfaces"));

    @Override
    public void perItem(VariantUtil.VariantEntrySet<InterfaceBlock> variantEntrySet, VariantUtil.VariantSet variantSet, RuntimeResourcePack pack) {
        var dye = variantSet.getOrNull(DYE_COLOR_VARIANT);

        // create recipe
        addRecipe(pack, ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, this, 2)
                .define('C', Items.COMPARATOR)
                .define('R', Items.REDSTONE)
                .define('H', Items.HOPPER)
                .define('I', Items.IRON_INGOT)
                .define('G', Util.getItem(VariantTypes.getGlassID(dye)))
                .pattern("ICI")
                .pattern("GRG")
                .pattern("IHI")
                .group("microstorage_interface")
                .unlockedBy("has_hopper", has(Items.HOPPER)), this);

        // like stained-glass recipe
        if (dye != null)
            addRecipe(pack,
                    Util.recipeSurround8(RecipeCategory.REDSTONE, Blocks.INTERFACE_BLOCKS.getOnly(),
                            Util.getItem(VariantTypes.getDyeID(dye)), this).group("microstorage_interface"),
                    this, "glass");

        addBlockDrop(pack, this, v -> v.createSingleItemTable(this));
        addTagElement(BlockTags.MINEABLE_WITH_PICKAXE, Util.getID(this));
        addTagElement(blockTag, Util.getID(this));
    }

    // from TransparentBlock

    @Override
    protected boolean skipRendering(BlockState state, BlockState adjacentState, Direction direction) {
        return adjacentState.is(this) || super.skipRendering(state, adjacentState, direction);
    }

    @Override
    protected @NotNull VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0F;
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    public boolean canConnect(BlockState blockState, Direction direction, BlockState neighborBlockState, LevelAccessor level, BlockPos blockPos, BlockPos neighborBlockPos) {
        if (Util.getContainer(level, neighborBlockPos) != null) return true;
        return ConnectingBlockUtil.ConnectingBlock.super.canConnect(blockState, direction, neighborBlockState, level, blockPos, neighborBlockPos);
    }
}
