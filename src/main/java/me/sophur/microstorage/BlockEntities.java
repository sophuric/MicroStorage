package me.sophur.microstorage;

import me.sophur.microstorage.block.InterfaceBlock;
import me.sophur.microstorage.block.TerminalBlock;
import me.sophur.microstorage.blockentity.InterfaceBlockEntity;
import me.sophur.microstorage.blockentity.TerminalBlockEntity;
import me.sophur.microstorage.util.BlockEntityUtil;
import net.minecraft.world.level.block.entity.BlockEntityType;

import static me.sophur.microstorage.util.Util.getModID;

public class BlockEntities {
    private BlockEntities() {
    }

    public static BlockEntityType<InterfaceBlockEntity> INTERFACE_BLOCK_ENTITY;
    public static BlockEntityType<TerminalBlockEntity> TERMINAL_BLOCK_ENTITY;

    private static boolean init = false;

    public static void register() {
        if (init) return;
        init = true;

        INTERFACE_BLOCK_ENTITY = BlockEntityUtil.register(getModID("interface"), InterfaceBlockEntity::new, InterfaceBlock.class,
                Blocks.INTERFACE_BLOCKS.getValues(), Blocks.STAINED_INTERFACE_BLOCKS.getValues());
        TERMINAL_BLOCK_ENTITY = BlockEntityUtil.register(getModID("terminal"), TerminalBlockEntity::new, TerminalBlock.class,
                Blocks.TERMINAL_BLOCKS.getValues());
    }
}
