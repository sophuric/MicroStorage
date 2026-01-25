package me.sophur.sophstorage.blockentity;

import me.sophur.sophstorage.BlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class TerminalBlockEntity extends BaseContainerBlockEntity {
    public TerminalBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntities.TERMINAL_BLOCK_ENTITY, pos, blockState);
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return getBlockState().getBlock().getName();
    }

    @Override
    protected @NotNull NonNullList<ItemStack> getItems() {
        // TODO
        return NonNullList.create();
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return ChestMenu.threeRows(containerId, inventory, this);
    }

    @Override
    public int getContainerSize() {
        return 0;
    }
}
