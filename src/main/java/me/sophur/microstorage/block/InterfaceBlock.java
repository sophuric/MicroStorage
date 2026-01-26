package me.sophur.microstorage.block;

import com.mojang.serialization.MapCodec;
import me.sophur.microstorage.VariantTypes;
import me.sophur.microstorage.blockentity.InterfaceBlockEntity;
import me.sophur.microstorage.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
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
    private final VariantUtil.VariantSet variantSet;

    public InterfaceBlock(Properties properties, VariantUtil.VariantSet variantSet) {
        super(properties);
        this.variantSet = variantSet;
        BlockState blockState = defaultBlockState();
        blockState = ConnectingBlockUtil.createDefaultBlockState(blockState);
        registerDefaultState(blockState);
    }

    public InterfaceBlock(VariantUtil.VariantSet variantSet) {
        this(TrimBlock.PROPERTIES, variantSet);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec((properties) -> new TerminalBlock(properties, variantSet));
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
        // TODO: group recipes for each types in the recipe book

        DyeColor dye = null;
        if (variantSet.hasVariant(DYE_COLOR_VARIANT)) dye = variantSet.get(DYE_COLOR_VARIANT);
        Item glassItem = Util.getItem(VariantTypes.getGlassID(dye));

        // create recipe
        addRecipe(pack, ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, this, 2)
                .define('C', Items.COMPARATOR)
                .define('R', Items.REDSTONE)
                .define('H', Items.HOPPER)
                .define('I', Items.IRON_INGOT)
                .define('G', glassItem)
                .pattern("ICI")
                .pattern("GRG")
                .pattern("IHI")
                .unlockedBy("has_hopper", has(Items.HOPPER)), this);

        // like stained-glass recipe
        if (dye != null)
            addRecipe(pack, Util.recipeSurround8(RecipeCategory.REDSTONE, Util.getItem(VariantTypes.getGlassID(null)),
                    Util.getItem(VariantTypes.getDyeID(dye)), this), this, "glass");

        addBlockDrop(pack, this, v -> v.createSingleItemTable(this));
        addTagElement(BlockTags.MINEABLE_WITH_PICKAXE, Util.getID(this));
        addTagElement(blockTag, Util.getID(this));
    }

    // from TransparentBlock

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
}
