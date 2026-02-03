package me.sophur.microstorage.blockentity;

import me.sophur.microstorage.BlockEntities;
import me.sophur.microstorage.block.TerminalBlock;
import me.sophur.microstorage.menu.TerminalMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.world.level.block.entity.BaseContainerBlockEntity.canUnlock;

public class TerminalBlockEntity extends BlockEntity implements MenuProvider, Nameable {
    private final ContainerOpenersCounter openersCounter;

    public LockCode lockKey;
    @Nullable
    public Component name;

    public TerminalBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntities.TERMINAL_BLOCK_ENTITY, pos, blockState);
        this.lockKey = LockCode.NO_LOCK;
        this.openersCounter = new ContainerOpenersCounter() {
            protected void onOpen(Level level, BlockPos blockPos, BlockState state) {
                level.setBlock(blockPos, state.setValue(TerminalBlock.OPEN, true), 3);
                playSound(level, blockPos, state, SoundEvents.BARREL_OPEN);
            }

            protected void onClose(Level level, BlockPos blockPos, BlockState state) {
                level.setBlock(blockPos, state.setValue(TerminalBlock.OPEN, false), 3);
                playSound(level, blockPos, state, SoundEvents.BARREL_CLOSE);
            }

            protected void openerCountChanged(Level level, BlockPos blockPos, BlockState state, int count, int openCount) {
            }

            protected boolean isOwnContainer(Player player) {
                if (player.containerMenu instanceof TerminalMenu menu) {
                    return menu.getBlockEntity() == TerminalBlockEntity.this;
                }
                return false;
            }
        };
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.lockKey = LockCode.fromTag(tag);
        if (tag.contains("CustomName", CompoundTag.TAG_STRING))
            this.name = parseCustomNameSafe(tag.getString("CustomName"), registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        this.lockKey.addToTag(tag);
        if (this.name != null)
            tag.putString("CustomName", Component.Serializer.toJson(this.name, registries));
    }

    protected void applyImplicitComponents(BlockEntity.DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        this.lockKey = componentInput.getOrDefault(DataComponents.LOCK, LockCode.NO_LOCK);
        this.name = componentInput.get(DataComponents.CUSTOM_NAME);
    }

    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(DataComponents.CUSTOM_NAME, this.name);
        if (!this.lockKey.equals(LockCode.NO_LOCK)) components.set(DataComponents.LOCK, this.lockKey);
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        if (!canUnlock(player, this.lockKey, this.getDisplayName())) return null;
        open(player);
        return new TerminalMenu(containerId, playerInventory, this, () -> close(player));
    }

    // most of this is taken from BarrelBlockEntity

    private void open(Player player) {
        if (this.remove || player.isSpectator()) return;
        openersCounter.incrementOpeners(player, level, worldPosition, getBlockState());
    }

    private void close(Player player) {
        if (this.remove || player.isSpectator()) return;
        openersCounter.decrementOpeners(player, level, worldPosition, getBlockState());
    }

    public void tick() {
        if (this.remove) return;
        openersCounter.recheckOpeners(level, worldPosition, getBlockState());
    }

    private static void playSound(Level level, BlockPos blockPos, BlockState state, SoundEvent sound) {
        if (level == null) return;
        Vec3 pos = blockPos.getCenter().relative(TerminalBlock.getDirection(state), 1);
        level.playSound(null, pos.x, pos.y, pos.z, sound, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return getName();
    }

    @Override
    public @NotNull Component getName() {
        Component name = getCustomName();
        if (name == null) return getBlockState().getBlock().getName();
        return name;
    }

    @Override
    public @Nullable Component getCustomName() {
        return this.name;
    }
}
