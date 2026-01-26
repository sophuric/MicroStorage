package me.sophur.microstorage.block;

import me.sophur.microstorage.VariantTypes;
import me.sophur.microstorage.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import org.jetbrains.annotations.NotNull;
import pers.solid.brrp.v1.api.RuntimeResourcePack;

import static me.sophur.microstorage.Blocks.*;
import static me.sophur.microstorage.VariantTypes.*;
import static me.sophur.microstorage.util.Util.getModID;
import static net.minecraft.data.recipes.RecipeProvider.has;

public class TrimBlock extends TransparentBlock implements ConnectingBlockUtil.ConnectingBlock, DataProvider<TrimBlock> {
    // mainly copy of glass block
    public static final BlockBehaviour.Properties PROPERTIES =
            BlockBehaviour.Properties.of()
                    .instrument(NoteBlockInstrument.HAT)
                    .strength(1.0F)
                    .sound(SoundType.GLASS)
                    .noOcclusion()
                    .isValidSpawn(net.minecraft.world.level.block.Blocks::never)
                    .isRedstoneConductor(net.minecraft.world.level.block.Blocks::never)
                    .isSuffocating(net.minecraft.world.level.block.Blocks::never)
                    .isViewBlocking(net.minecraft.world.level.block.Blocks::never);

    public TrimBlock(Properties properties, VariantUtil.VariantSet variantSet) {
        super(properties);
        BlockState blockState = defaultBlockState();
        ConnectingBlockUtil.createDefaultBlockState(blockState);
        registerDefaultState(blockState);
    }

    public TrimBlock(VariantUtil.VariantSet variantSet) {
        this(PROPERTIES, variantSet);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        ConnectingBlockUtil.createBlockStateDefinition(builder);
    }

    @Override
    protected @NotNull BlockState updateShape(BlockState blockState, Direction direction, BlockState neighborBlockState, LevelAccessor level, BlockPos blockPos, BlockPos neighborBlockPos) {
        ConnectingBlockUtil.updateShape(this, blockState, direction, neighborBlockState, level, blockPos, neighborBlockPos);
        return super.updateShape(blockState, direction, neighborBlockState, level, blockPos, neighborBlockPos);
    }

    @Override
    public @NotNull BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState blockState = defaultBlockState();
        ConnectingBlockUtil.getStateForPlacement(this, blockState, ctx);
        return blockState;
    }

    private static final TagKey<Block> blockTag = TagKey.create(Registries.BLOCK, getModID("trims"));

    @Override
    public void perItem(VariantUtil.VariantEntrySet<TrimBlock> variantEntrySet, VariantUtil.VariantSet variantSet, RuntimeResourcePack pack) {
        DyeColor dye = null;
        if (variantSet.hasVariant(DYE_COLOR_VARIANT)) dye = variantSet.get(DYE_COLOR_VARIANT);
        Item glassItem = Util.getItem(VariantTypes.getGlassID(dye));

        // create recipe
        addRecipe(pack, ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, this, 2)
                .define('R', Items.REDSTONE)
                .define('I', Items.IRON_INGOT)
                .define('G', glassItem)
                .pattern("I I")
                .pattern("GRG")
                .pattern("I I")
                .unlockedBy("has_hopper", has(Items.HOPPER)), this);

        // like stained-glass recipe
        if (dye != null)
            addRecipe(pack, Util.recipeSurround8(RecipeCategory.REDSTONE, Util.getItem(VariantTypes.getGlassID(null)),
                    Util.getItem(VariantTypes.getDyeID(dye)), this), this, "glass");

        addBlockDrop(pack, this, v -> v.createSingleItemTable(this));
        addTagElement(BlockTags.MINEABLE_WITH_PICKAXE, Util.getID(this));
        addTagElement(blockTag, Util.getID(this));
    }
}
