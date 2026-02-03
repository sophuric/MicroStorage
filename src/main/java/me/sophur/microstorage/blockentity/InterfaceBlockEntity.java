package me.sophur.microstorage.blockentity;

import me.sophur.microstorage.BlockEntities;
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

public class InterfaceBlockEntity extends BaseContainerBlockEntity {
    public InterfaceBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntities.INTERFACE_BLOCK_ENTITY, pos, blockState);
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return getBlockState().getBlock().getName();
    }

    @Override
    public @NotNull NonNullList<ItemStack> getItems() {
        return NonNullList.create();
    }

    @Override
    public void setItems(NonNullList<ItemStack> items) {
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
