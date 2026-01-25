package me.sophur.sophstorage;

import me.sophur.sophstorage.block.*;
import me.sophur.sophstorage.util.BlockUtil;
import me.sophur.sophstorage.util.DataProvider;
import me.sophur.sophstorage.util.Util;
import me.sophur.sophstorage.util.VariantUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.sophur.sophstorage.VariantTypes.*;
import static me.sophur.sophstorage.util.BlockUtil.register;
import static me.sophur.sophstorage.util.Util.getModID;

public class Blocks {
    private static boolean init = false;

    public static VariantUtil.VariantEntrySet<InterfaceBlock> INTERFACE_BLOCKS;
    public static VariantUtil.VariantEntrySet<TerminalBlock> TERMINAL_BLOCKS;
    public static VariantUtil.VariantEntrySet<TrimBlock> TRIM_BLOCKS;

    public static void initialise() {
        if (init) return;
        init = true;

        INTERFACE_BLOCKS = register((variantEntrySet, variantSet, id) -> {
            var planks = getPlanksBlock(variantSet.get(WOOD_TYPE_VARIANT));
            if (planks == null) return null;
            return new InterfaceBlock(BlockBehaviour.Properties.ofFullCopy(planks));
        }, getModID("interface"), true, VariantTypes.WOOD_TYPE_VARIANT);

        TERMINAL_BLOCKS = register((variantEntrySet, variantSet, id) -> {
            var planks = getPlanksBlock(variantSet.get(WOOD_TYPE_VARIANT));
            if (planks == null) return null;
            return new TerminalBlock(BlockBehaviour.Properties.ofFullCopy(planks));
        }, getModID("terminal"), true, VariantTypes.WOOD_TYPE_VARIANT);

        TRIM_BLOCKS = register((variantEntrySet, variantSet, id) -> {
            var planks = getPlanksBlock(variantSet.get(WOOD_TYPE_VARIANT));
            if (planks == null) return null;
            return new TrimBlock(BlockBehaviour.Properties.ofFullCopy(planks));
        }, getModID("trim"), true, VariantTypes.WOOD_TYPE_VARIANT);

        DataProvider.createData(INTERFACE_BLOCKS, SophStorage.pack);
        DataProvider.createData(TERMINAL_BLOCKS, SophStorage.pack);
        DataProvider.createData(TRIM_BLOCKS, SophStorage.pack);

        // add block tags
        blockTagBuilderMap.forEach(SophStorage.pack::addTag);
    }

    private static final Map<TagKey<Block>, TagBuilder> blockTagBuilderMap = new HashMap<>();

    public static void addTagElement(TagKey<Block> blockTag, ResourceLocation elementLocation) {
        getTag(blockTag).addElement(elementLocation);
    }

    public static TagBuilder getTag(TagKey<Block> blockTag) {
        blockTagBuilderMap.putIfAbsent(blockTag, new TagBuilder());
        return blockTagBuilderMap.get(blockTag);
    }

    public static Collection<VariantUtil.VariantEntrySet<? extends Block>> getAll() {
        return List.of(INTERFACE_BLOCKS, TERMINAL_BLOCKS, TRIM_BLOCKS);
    }
}
