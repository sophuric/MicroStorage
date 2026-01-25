package me.sophur.sophstorage;

import me.sophur.sophstorage.block.InterfaceBlock;
import me.sophur.sophstorage.block.TerminalBlock;
import me.sophur.sophstorage.blockentity.InterfaceBlockEntity;
import me.sophur.sophstorage.blockentity.TerminalBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import static me.sophur.sophstorage.util.Util.getModID;
import static me.sophur.sophstorage.util.BlockEntityUtil.register;

public class BlockEntities {
    public static BlockEntityType<InterfaceBlockEntity> INTERFACE_BLOCK_ENTITY;
    public static BlockEntityType<TerminalBlockEntity> TERMINAL_BLOCK_ENTITY;

    private static boolean init = false;

    public static void initialise() {
        if (init) return;
        init = true;

        INTERFACE_BLOCK_ENTITY = register(getModID("interface"), InterfaceBlockEntity::new, InterfaceBlock.class, Blocks.INTERFACE_BLOCKS.getValues());
        TERMINAL_BLOCK_ENTITY = register(getModID("terminal"), TerminalBlockEntity::new, TerminalBlock.class, Blocks.TERMINAL_BLOCKS.getValues());
    }
}
