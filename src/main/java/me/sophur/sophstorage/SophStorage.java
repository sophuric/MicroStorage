package me.sophur.sophstorage;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class SophStorage implements ModInitializer {
    public static final String MOD_ID = "sophstorage";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static ModContainer MOD_CONTAINER;

    @Override
    public void onInitialize() {
        Optional<ModContainer> container = FabricLoader.getInstance().getModContainer(MOD_ID);
        if (container.isEmpty()) throw new RuntimeException("");
        MOD_CONTAINER = container.get();

        Blocks.initialise();
    }
}
