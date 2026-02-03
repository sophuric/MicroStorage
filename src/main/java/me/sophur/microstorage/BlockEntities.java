package me.sophur.microstorage;

import me.sophur.microstorage.block.InterfaceBlock;
import me.sophur.microstorage.block.TerminalBlock;
import me.sophur.microstorage.blockentity.InterfaceBlockEntity;
import me.sophur.microstorage.blockentity.TerminalBlockEntity;
import me.sophur.microstorage.menu.TerminalMenu;
import me.sophur.microstorage.util.BlockEntityUtil;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntityType;

import static me.sophur.microstorage.util.Util.getModID;

public class BlockEntities {
    private BlockEntities() {
    }

    public static BlockEntityType<InterfaceBlockEntity> INTERFACE_BLOCK_ENTITY;
    public static BlockEntityType<TerminalBlockEntity> TERMINAL_BLOCK_ENTITY;

    public static MenuType<TerminalMenu> TERMINAL_MENU_TYPE;

    private static boolean init = false;

    public static void register() {
        if (init) return;
        init = true;

        INTERFACE_BLOCK_ENTITY = BlockEntityUtil.register(getModID("interface"), InterfaceBlockEntity::new, InterfaceBlock.class,
                Blocks.INTERFACE_BLOCKS.getValues(), Blocks.STAINED_INTERFACE_BLOCKS.getValues());
        TERMINAL_BLOCK_ENTITY = BlockEntityUtil.register(getModID("terminal"), TerminalBlockEntity::new, TerminalBlock.class,
                Blocks.TERMINAL_BLOCKS.getValues());

        TERMINAL_MENU_TYPE = Registry.register(BuiltInRegistries.MENU, getModID("terminal"),
                new MenuType<>(TerminalMenu::new, FeatureFlags.DEFAULT_FLAGS));
    }
}
