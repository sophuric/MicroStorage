package me.sophur.sophstorage.client;

import me.sophur.sophstorage.Blocks;
import me.sophur.sophstorage.block.InterfaceBlock;
import me.sophur.sophstorage.block.TerminalBlock;
import me.sophur.sophstorage.block.TrimBlock;
import me.sophur.sophstorage.client.block.TerminalClientBlock;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.data.models.blockstates.BlockStateGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import pers.solid.brrp.v1.api.RuntimeResourcePack;
import pers.solid.brrp.v1.fabric.api.RRPCallback;
import pers.solid.brrp.v1.model.ModelJsonBuilder;

import static me.sophur.sophstorage.Util.getID;
import static me.sophur.sophstorage.client.ClientUtil.*;

public class SophStorageClient implements ClientModInitializer {
    public static RuntimeResourcePack pack;

    @Override
    public void onInitializeClient() {
        var packID = getID("resource_pack");
        pack = RuntimeResourcePack.create(packID);

        TerminalClientBlock.INSTANCE.register();

        Blocks.getBlocks().forEach((wood, woodTypeBlocks) -> {
            for (Blocks.BlockID<Block> block : woodTypeBlocks.blocks()) {
                ResourceLocation planksTexture = getBlockModelID(ResourceLocation.parse(wood.name() + "_planks"));
                Block bl = block.block();

                BlockStateGenerator blockStateGenerator = null;
                ModelJsonBuilder itemModel = null;

                if (bl instanceof InterfaceBlock interfaceBlock) {
                } else if (bl instanceof TrimBlock trimBlock) {
                } else if (bl instanceof TerminalBlock terminalBlock) {
                    var b = block.toSubclass(terminalBlock);
                    blockStateGenerator = TerminalClientBlock.INSTANCE.generateBlockState(b, wood, woodTypeBlocks, planksTexture, pack);
                    itemModel = TerminalClientBlock.INSTANCE.generateItemModel(b, wood, woodTypeBlocks, planksTexture, pack);
                }

                if (blockStateGenerator != null) pack.addBlockState(block.blockID(), blockStateGenerator);
                if (itemModel != null) pack.addModel(getItemModelID(block.blockID()), itemModel);
            }
        });

        RRPCallback.BEFORE_VANILLA.register(resources -> resources.add(pack));
    }
}
