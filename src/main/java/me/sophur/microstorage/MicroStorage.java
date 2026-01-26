package me.sophur.microstorage;

import net.fabricmc.api.ModInitializer;
import net.minecraft.world.item.CreativeModeTab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.solid.brrp.v1.api.RuntimeResourcePack;
import pers.solid.brrp.v1.fabric.api.RRPCallback;

import static me.sophur.microstorage.util.Util.getModID;

public class MicroStorage implements ModInitializer {
    public static final String MOD_ID = "microstorage";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static CreativeModeTab CREATIVE_TAB;

    @Override
    public void onInitialize() {
        VariantTypes.register();
        Blocks.register();
        BlockEntities.register();

        CreativeTab.register();

        RRPCallback.AFTER_VANILLA.register(resources -> {
            try (var pack = RuntimeResourcePack.create(getModID("resource_pack"))) {
                Blocks.createData(pack);

                if (pack.numberOfRootResources() > 0)
                    throw new RuntimeException("Server pack cannot have root resources");
                if (pack.numberOfClientResources() > 0)
                    throw new RuntimeException("Server pack cannot have client resources");
                resources.add(pack);
            }
        });
    }
}
