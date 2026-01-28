package me.sophur.microstorage.mixin;

import me.sophur.microstorage.VariantTypes;
import me.sophur.microstorage.block.TrimBlock;
import me.sophur.microstorage.util.Util;
import me.sophur.microstorage.util.VariantUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Mixin(HalfTransparentBlock.class)
public class HalfTransparentBlockMixin {
    // this is to prevent rendering sides of vanilla glass that are connected to a trim/interface block
    @Inject(method = "skipRendering(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;)Z",
            at = @At("HEAD"), cancellable = true)
    public void injectSkipRendering(BlockState state, BlockState adjacentState, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        Block block = state.getBlock();

        // loop all dye colors, and get if this block is a stained-glass block, and if so, which dye color it is
        var dyeOpt = VariantTypes.DYE_COLOR_VARIANT.getVariants().stream().filter(dyeVariant ->
                Objects.equals(block, Util.getBlock(VariantTypes.getGlassID(dyeVariant.variant)))).findFirst();

        if (dyeOpt.isEmpty()) {
            // get if this block is a regular glass block, and return if it isn't
            if (!Objects.equals(block, Util.getBlock(VariantTypes.getGlassID(null)))) return;
        }

        // we have confirmed this is a glass block (stained or not)
        VariantUtil.Variant<DyeColor> dye = dyeOpt.orElse(null);
        if (TrimBlock.skipRenderingGlass(new VariantUtil.VariantSupplier<Block>() {
            @Override
            public VariantUtil.VariantEntrySet<Block> getVariantEntrySet() {
                return null;
            }

            @Override
            public VariantUtil.VariantSet getVariantSet() {
                return dye == null ? new VariantUtil.VariantSet() : new VariantUtil.VariantSet(dye);
            }
        }, state, adjacentState, direction)) {
            // prevent rendering this side
            cir.setReturnValue(true);
        }
    }
}
