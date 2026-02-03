package me.sophur.microstorage.mixin;

import com.mojang.serialization.MapCodec;
import me.sophur.microstorage.util.TranslatableContentsFallback;
import me.sophur.microstorage.util.Util;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.util.StringRepresentable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Function;

@Mixin(ComponentSerialization.class)
public abstract class ComponentSerializationMixin {
    @Shadow
    public static <T extends StringRepresentable, E> MapCodec<E> createLegacyComponentMatcher(T[] types, Function<T, MapCodec<? extends E>> codecGetter, Function<E, T> typeGetter, String typeFieldName) {
        return null;
    }

    private ComponentSerializationMixin() {
    }

    @Redirect(method = "createCodec(Lcom/mojang/serialization/Codec;)Lcom/mojang/serialization/Codec;", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/ComponentSerialization;createLegacyComponentMatcher([Lnet/minecraft/util/StringRepresentable;Ljava/util/function/Function;Ljava/util/function/Function;Ljava/lang/String;)Lcom/mojang/serialization/MapCodec;"))
    private static MapCodec<ComponentContents> redirectCreateLegacyComponentMatcher(StringRepresentable[] types, Function<StringRepresentable, MapCodec<? extends ComponentContents>> codecGetter, Function<ComponentContents, StringRepresentable> typeGetter, String typeFieldName) {
        // add TranslatableContentsFallback to the list of ComponentContents types that can be encoded/decoded
        types = Util.concat(StringRepresentable.class, new StringRepresentable[]{TranslatableContentsFallback.TYPE}, types);
        return createLegacyComponentMatcher(types, codecGetter, typeGetter, typeFieldName);
    }
}
