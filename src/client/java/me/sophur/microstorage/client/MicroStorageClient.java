package me.sophur.microstorage.client;

import me.sophur.microstorage.Blocks;
import me.sophur.microstorage.block.InterfaceBlock;
import me.sophur.microstorage.block.TrimBlock;
import me.sophur.microstorage.client.block.TerminalClientBlock;
import me.sophur.microstorage.client.block.TrimInterfaceClientBlock;
import net.fabricmc.api.ClientModInitializer;
import pers.solid.brrp.v1.api.RuntimeResourcePack;
import pers.solid.brrp.v1.fabric.api.RRPCallback;

import java.util.List;

import static me.sophur.microstorage.util.Util.getModID;

public class MicroStorageClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        var packID = getModID("resource_pack");

        RRPCallback.AFTER_VANILLA.register(resources -> {
            try (var pack = RuntimeResourcePack.create(packID)) {
                new TerminalClientBlock().initialise(pack, List.of(Blocks.TERMINAL_BLOCKS));
                new TrimInterfaceClientBlock<TrimBlock>("trim").initialise(pack, List.of(Blocks.TRIM_BLOCKS, Blocks.STAINED_TRIM_BLOCKS));
                new TrimInterfaceClientBlock<InterfaceBlock>("interface").initialise(pack, List.of(Blocks.INTERFACE_BLOCKS, Blocks.STAINED_INTERFACE_BLOCKS));

                if (pack.numberOfRootResources() > 0)
                    throw new RuntimeException("Client pack cannot have root resources");
                if (pack.numberOfServerData() > 0)
                    throw new RuntimeException("Client pack cannot have server resources");
                resources.add(pack);
            }
        });
    }
}
