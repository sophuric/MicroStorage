package me.sophur.microstorage.mixin;

import me.sophur.microstorage.VariantTypes;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WoodType.class)
public class WoodTypeMixin {
    @Inject(method = "register", at = @At("RETURN"))
    private static void injectRegister(WoodType woodType, CallbackInfoReturnable<WoodType> cir) {
        if (VariantTypes.WOOD_TYPE_VARIANT != null) VariantTypes.WOOD_TYPE_VARIANT.addMore(woodType);
    }
}
