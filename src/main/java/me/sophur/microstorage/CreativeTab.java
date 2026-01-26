package me.sophur.microstorage;

import me.sophur.microstorage.util.VariantUtil;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.WoodType;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

import static me.sophur.microstorage.Blocks.*;
import static me.sophur.microstorage.util.Util.getModID;

public class CreativeTab {
    private CreativeTab() {
    }

    public static CreativeModeTab register() {
        CreativeModeTab.Builder creativeTabBuilder = FabricItemGroup.builder().title(Component.translatable("itemGroup.microStorage"));
        creativeTabBuilder.icon(() -> new ItemStack(
                TERMINAL_BLOCKS.get(
                        new VariantUtil.VariantSet(
                                VariantTypes.WOOD_TYPE_VARIANT.get(WoodType.OAK)
                        ))));

        creativeTabBuilder.displayItems((params, output) -> {
            Set<ItemLike> acceptedItems = new HashSet<>();
            BiConsumer<VariantUtil.VariantSet, Block> func = (variantSet, item) -> {
                if (acceptedItems.add(item)) output.accept(item);
            };

            VariantUtil.VariantEntrySet.loopVariantEntrySets(func, TERMINAL_BLOCKS);
            VariantUtil.VariantEntrySet.loopVariantEntrySets(func, TRIM_BLOCKS, INTERFACE_BLOCKS);
            VariantUtil.VariantEntrySet.loopVariantEntrySets(func, STAINED_TRIM_BLOCKS, STAINED_INTERFACE_BLOCKS);
        });

        // FIXME: wood types are not registered in the creative inventory in the correct order
        return Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, getModID("creative_tab"), creativeTabBuilder.build());
    }
}
