package me.sophur.microstorage.client;

import me.sophur.microstorage.Blocks;
import me.sophur.microstorage.client.block.TerminalClientBlock;
import net.fabricmc.api.ClientModInitializer;
import pers.solid.brrp.v1.api.RuntimeResourcePack;
import pers.solid.brrp.v1.fabric.api.RRPCallback;

import java.util.List;

import static me.sophur.microstorage.util.Util.getModID;

public class MicroStorageClient implements ClientModInitializer {
    public static RuntimeResourcePack pack;

    @Override
    public void onInitializeClient() {
        var packID = getModID("resource_pack");
        pack = RuntimeResourcePack.create(packID);

        new TerminalClientBlock().initialise(pack, List.of(Blocks.TERMINAL_BLOCKS));

        RRPCallback.BEFORE_VANILLA.register(resources -> {
            if (pack.numberOfRootResources() > 0) throw new RuntimeException("Client pack cannot have root resources");
            if (pack.numberOfServerData() > 0) throw new RuntimeException("Client pack cannot have server resources");
            resources.add(pack);
        });
    }
}
