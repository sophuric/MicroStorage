package me.sophur.microstorage;

import me.sophur.microstorage.util.Util;
import me.sophur.microstorage.util.VariantUtil;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.solid.brrp.v1.api.RuntimeResourcePack;
import pers.solid.brrp.v1.fabric.api.RRPCallback;

import java.util.*;

import static me.sophur.microstorage.Blocks.*;
import static me.sophur.microstorage.util.Util.getModID;

public class MicroStorage implements ModInitializer {
    public static final String MOD_ID = "microstorage";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static RuntimeResourcePack pack;

    public static CreativeModeTab CREATIVE_TAB;

    @Override
    public void onInitialize() {
        pack = RuntimeResourcePack.create(getModID("resource_pack"));

        VariantTypes.initialise();
        Blocks.initialise();
        BlockEntities.initialise();

        CreativeModeTab.Builder creativeTabBuilder = FabricItemGroup.builder().title(Component.translatable("itemGroup.microStorage"));
        ItemStack icon = new ItemStack(
                TERMINAL_BLOCKS.get(
                        new VariantUtil.VariantSet(
                                VariantTypes.WOOD_TYPE_VARIANT.get(WoodType.OAK)
                        )));
        creativeTabBuilder.icon(() -> icon);
        creativeTabBuilder.displayItems((params, output) -> {
            Set<ItemLike> acceptedItems = new HashSet<>();
            List<VariantUtil.VariantEntrySet<? extends ItemLike>> items = List.copyOf(Blocks.getAll());
            Util.loopVariantEntrySets((variantSet, item) -> {
                if (acceptedItems.add(item)) output.accept(item);
            }, items);
        });

        // FIXME: wood types are not registered in the creative inventory in the correct order

        CREATIVE_TAB = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, getModID("creative_tab"), creativeTabBuilder.build());

        RRPCallback.BEFORE_VANILLA.register(resources -> {
            if (pack.numberOfRootResources() > 0) throw new RuntimeException("Server pack cannot have root resources");
            if (pack.numberOfClientResources() > 0) throw new RuntimeException("Server pack cannot have client resources");
            resources.add(pack);
        });
    }
}
