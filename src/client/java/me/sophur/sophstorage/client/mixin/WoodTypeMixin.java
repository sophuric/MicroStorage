package me.sophur.sophstorage.client.mixin;

import me.sophur.sophstorage.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WoodType.class)
public class WoodTypeMixin {
    @Inject(method = "register", at = @At("RETURN"))
    private static void injectRegister(WoodType woodType, CallbackInfoReturnable<WoodType> cir) {
        Minecraft instance = Minecraft.getInstance();
        //noinspection ConstantValue // removing this causes NPE
        if (instance == null) return;
        instance.submit(() -> Blocks.addWoodType(woodType));
    }
}
