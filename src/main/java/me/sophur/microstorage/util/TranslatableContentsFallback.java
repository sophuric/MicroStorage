package me.sophur.microstorage.util;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.*;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.network.chat.contents.TranslatableFormatException;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

// this class allows another Component to be used as a fallback
public class TranslatableContentsFallback extends TranslatableContents {
    private static DataResult<Object> filterAllowedArguments(@Nullable Object input) {
        return !isAllowedPrimitiveArgument(input) ? DataResult.error(() -> "This value needs to be parsed as component") : DataResult.success(input);
    }

    public static final MapCodec<TranslatableContentsFallback> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                            Codec.STRING.fieldOf("translate").forGetter(translatableContents -> translatableContents.key),
                            ComponentSerialization.CODEC.lenientOptionalFieldOf("fallback").forGetter(translatableContents -> Optional.ofNullable(translatableContents.fallback)),
                            ARG_CODEC.listOf().optionalFieldOf("with").forGetter(translatableContents -> adjustArgs(translatableContents.args))
                    )
                    .apply(instance, (key, fallback, args) ->
                            new TranslatableContentsFallback(key, fallback.orElse(null), adjustArgs(args)))
    );
    public static final ComponentContents.Type<TranslatableContentsFallback> TYPE = new ComponentContents.Type<>(CODEC, "translatable_fallback");

    @Nullable
    private final Component fallback;

    public TranslatableContentsFallback(String key, @Nullable Component fallback, Object[] args) {
        super(key, null, args);
        this.fallback = fallback;
    }

    private void decompose() {
        Language language = Language.getInstance();
        if (language == this.decomposedWith) return;

        this.decomposedWith = language;
        // use Component if a fallback exists and the language does not have the key
        if (this.fallback != null && !language.has(this.key)) {
            this.decomposedParts = ImmutableList.of(this.fallback.copy());
            return;
        }

        String string = language.getOrDefault(this.key);

        try {
            ImmutableList.Builder<FormattedText> builder = ImmutableList.builder();
            this.decomposeTemplate(string, builder::add);
            this.decomposedParts = builder.build();
        } catch (TranslatableFormatException var4) {
            this.decomposedParts = ImmutableList.of(FormattedText.of(string));
        }
    }

    public <T> @NotNull Optional<T> visit(FormattedText.StyledContentConsumer<T> styledContentConsumer, Style style) {
        this.decompose();

        for (FormattedText formattedText : this.decomposedParts) {
            Optional<T> optional = formattedText.visit(styledContentConsumer, style);
            if (optional.isPresent()) return optional;
        }

        return Optional.empty();
    }

    public <T> @NotNull Optional<T> visit(FormattedText.ContentConsumer<T> contentConsumer) {
        this.decompose();

        for (FormattedText formattedText : this.decomposedParts) {
            Optional<T> optional = formattedText.visit(contentConsumer);
            if (optional.isPresent()) return optional;
        }

        return Optional.empty();
    }

    @Override
    public @NotNull MutableComponent resolve(@Nullable CommandSourceStack nbtPathPattern, @Nullable Entity entity, int recursionDepth) throws CommandSyntaxException {
        Object[] objects = new Object[this.args.length];

        for (int i = 0; i < objects.length; i++) {
            Object object = this.args[i];
            if (object instanceof Component component) {
                objects[i] = ComponentUtils.updateForEntity(nbtPathPattern, component, entity, recursionDepth);
            } else {
                objects[i] = object;
            }
        }

        return MutableComponent.create(new TranslatableContentsFallback(this.key, this.fallback, objects));
    }

    @Override
    public @NotNull Type<TranslatableContentsFallback> type() {
        return TYPE;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TranslatableContentsFallback that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(key, that.key) && Objects.equals(fallback, that.fallback) && Arrays.equals(args, that.args);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, fallback, Arrays.hashCode(args));
    }

    public @NotNull String toString() {
        return "translation{key='"
                + this.key
                + "'"
                + (this.fallback != null ? ", fallback='" + this.fallback + "'" : "")
                + ", args="
                + Arrays.toString(this.args)
                + "}";
    }

    @Override
    public @Nullable String getFallback() {
        throw new UnsupportedOperationException();
    }

    public @Nullable Component getComponentFallback() {
        return this.fallback;
    }
}
