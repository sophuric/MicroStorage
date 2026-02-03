package me.sophur.microstorage.menu;

import me.sophur.microstorage.BlockEntities;
import me.sophur.microstorage.blockentity.TerminalBlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TerminalMenu extends AbstractContainerMenu {
    public final TerminalBlockEntity blockEntity;
    private final Runnable closeFunc;

    public TerminalBlockEntity getBlockEntity() {
        return blockEntity;
    }

    public TerminalMenu(int containerId, Inventory playerInventory) {
        super(BlockEntities.TERMINAL_MENU_TYPE, containerId);
        blockEntity = null;
        closeFunc = null;
    }

    public TerminalMenu(int containerId, Inventory playerInventory, TerminalBlockEntity terminalBlockEntity, Runnable close) {
        super(BlockEntities.TERMINAL_MENU_TYPE, containerId);
        this.blockEntity = terminalBlockEntity;
        this.closeFunc = close;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int index) {
        // TODO
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        if (blockEntity == null) return false;
        return Container.stillValidBlockEntity(blockEntity, player);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (closeFunc != null) closeFunc.run();
    }
}
