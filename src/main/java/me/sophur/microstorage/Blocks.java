package me.sophur.microstorage;

import me.sophur.microstorage.block.*;
import me.sophur.microstorage.util.BlockUtil;
import me.sophur.microstorage.util.DataProvider;
import me.sophur.microstorage.util.Util;
import me.sophur.microstorage.util.VariantUtil;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.storage.loot.LootTable;
import pers.solid.brrp.v1.api.RuntimeResourcePack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static me.sophur.microstorage.VariantTypes.*;
import static me.sophur.microstorage.util.Util.getModID;

public class Blocks {
    private Blocks() {
    }

    private static boolean init = false;

    public static VariantUtil.VariantEntrySet<TerminalBlock> TERMINAL_BLOCKS;
    public static VariantUtil.VariantEntrySet<InterfaceBlock> INTERFACE_BLOCKS;
    public static VariantUtil.VariantEntrySet<TrimBlock> TRIM_BLOCKS;
    public static VariantUtil.VariantEntrySet<InterfaceBlock> STAINED_INTERFACE_BLOCKS;
    public static VariantUtil.VariantEntrySet<TrimBlock> STAINED_TRIM_BLOCKS;

    public static void register() {
        if (init) return;
        init = true;

        TERMINAL_BLOCKS = BlockUtil.register((variantEntrySet, variantSet, id) ->
                new TerminalBlock(variantSet), getModID("terminal"), true, WOOD_TYPE_VARIANT);
        INTERFACE_BLOCKS = BlockUtil.register((variantEntrySet, variantSet, id) ->
                new InterfaceBlock(variantSet), getModID("interface"), true);
        TRIM_BLOCKS = BlockUtil.register((variantEntrySet, variantSet, id) ->
                new TrimBlock(variantSet), getModID("trim"), true);
        STAINED_INTERFACE_BLOCKS = BlockUtil.register((variantEntrySet, variantSet, id) ->
                new InterfaceBlock(variantSet), getModID("stained_interface"), true, DYE_COLOR_VARIANT);
        STAINED_TRIM_BLOCKS = BlockUtil.register((variantEntrySet, variantSet, id) ->
                new TrimBlock(variantSet), getModID("stained_trim"), true, DYE_COLOR_VARIANT);
    }

    public static void createData(RuntimeResourcePack pack) {
        blockTagBuilderMap = new HashMap<>();

        DataProvider.createData(TERMINAL_BLOCKS, pack);
        DataProvider.createData(INTERFACE_BLOCKS, pack);
        DataProvider.createData(TRIM_BLOCKS, pack);
        DataProvider.createData(STAINED_INTERFACE_BLOCKS, pack);
        DataProvider.createData(STAINED_TRIM_BLOCKS, pack);

        // add block tags
        blockTagBuilderMap.forEach(pack::addTag);
        blockTagBuilderMap = null;
    }

    private static Map<TagKey<Block>, TagBuilder> blockTagBuilderMap = null;

    public static void addTagElement(TagKey<Block> blockTag, ResourceLocation elementLocation) {
        getTag(blockTag).addElement(elementLocation);
    }

    public static TagBuilder getTag(TagKey<Block> blockTag) {
        blockTagBuilderMap.putIfAbsent(blockTag, new TagBuilder());
        return blockTagBuilderMap.get(blockTag);
    }

    public static void addBlockDropBuilt(RuntimeResourcePack pack, BlockBehaviour block, Function<BlockLootSubProvider, LootTable> function) {
        pack.addLootTable(block.getLootTable().location(), function.apply(pack.getBlockLootTableGenerator()));
    }

    public static void addBlockDrop(RuntimeResourcePack pack, BlockBehaviour block, Function<BlockLootSubProvider, LootTable.Builder> function) {
        addBlockDropBuilt(pack, block, provider -> function.apply(provider).build());
    }

    public static void addRecipe(RuntimeResourcePack pack, RecipeBuilder recipeBuilder, ItemLike itemLike, String suffix) {
        // helper method to add a suffix to avoid duplicate recipe IDs
        if (!suffix.isEmpty()) suffix = "_" + suffix;
        recipeBuilder.save(pack.getRecipeExporter(), Util.addSuffix(Util.getItemID(itemLike), suffix));
    }

    public static void addRecipe(RuntimeResourcePack pack, RecipeBuilder recipeBuilder, ItemLike itemLike) {
        addRecipe(pack, recipeBuilder, itemLike, "");
    }
}
