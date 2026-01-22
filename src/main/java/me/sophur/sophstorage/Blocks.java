package me.sophur.sophstorage;

import me.sophur.sophstorage.block.*;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.WoodType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static me.sophur.sophstorage.SophStorage.LOGGER;
import static me.sophur.sophstorage.Util.*;

public class Blocks {
    private Blocks() {
    }

    public static <T extends Block> BlockID<T> register(T block, ResourceLocation id) {
        return register(block, id, true);
    }

    public static <T extends Block> BlockID<T> register(T block, ResourceLocation id, boolean shouldRegisterItem) {
        block = Registry.register(BuiltInRegistries.BLOCK, id, block);

        BlockItem blockItem = null;

        if (shouldRegisterItem) {
            var props = new Item.Properties();
            blockItem = Registry.register(BuiltInRegistries.ITEM, id, new BlockItem(block, props));
        }

        return new BlockID<>(block, blockItem, id);
    }

    private static final Map<WoodType, WoodTypeBlocks> BLOCKS = new HashMap<>();

    public static Map<WoodType, WoodTypeBlocks> getBlocks() {
        return Map.copyOf(BLOCKS);
    }

    public record BlockID<T extends Block>(T block, BlockItem blockItem, ResourceLocation blockID) {
        // useful after calling instanceof, in order to still have the BlockID for a subclass of the block
        public <N extends T> BlockID<N> toSubclass(N newBlock) {
            if (newBlock != block) throw new UnsupportedOperationException("newBlock is not the same as block");
            return new BlockID<>(newBlock, blockItem, blockID);
        }
    }

    public record WoodTypeBlocks(WoodType woodType, String baseID, List<BlockID<Block>> blocks) {
    }

    public static WoodTypeBlocks addWoodType(WoodType wood) {
        if (!BLOCKS.containsKey(wood)) {
            Optional<Block> optPlanksBlock = BuiltInRegistries.BLOCK.getOptional(ResourceLocation.parse(wood.name() + "_planks"));
            if (optPlanksBlock.isEmpty()) {
                LOGGER.warn("Couldn't find planks block for wood type: {}", wood.name());
                LOGGER.warn("Consider making a bug report to SophStorage if you would like this wood type added");
                return null;
            }

            Block planksBlock = optPlanksBlock.get();
            ResourceLocation baseName = Util.getIDFromExistingID(wood.name());
            BlockBehaviour.Properties blockProps = BlockBehaviour.Properties.ofFullCopy(planksBlock);

            BLOCKS.put(wood, new WoodTypeBlocks(wood, baseName.getPath(), List.of(
                    register(new TrimBlock(blockProps), addSuffix(baseName, "_trim")),
                    register(new TerminalBlock(blockProps), addSuffix(baseName, "_terminal")),
                    register(new InterfaceBlock(blockProps), addSuffix(baseName, "_interface"))
            )));
        }
        return BLOCKS.get(wood);
    }

    public static void initialise() {
        WoodType.values().forEach(Blocks::addWoodType);
    }
}
