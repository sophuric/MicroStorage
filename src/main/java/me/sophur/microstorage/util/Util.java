package me.sophur.microstorage.util;

import me.sophur.microstorage.blockentity.TerminalBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import static me.sophur.microstorage.MicroStorage.MOD_ID;

public class Util {
    private Util() {
    }

    public static <T> boolean hasDuplicates(Collection<T> collection) {
        Set<T> set = new HashSet<>();
        for (T t : collection) {
            if (!set.add(t)) return true;
        }
        return false;
    }

    public static BaseContainerBlockEntity getContainer(BlockGetter level, BlockPos blockPos) {
        BlockEntity entity = level.getBlockEntity(blockPos);
        if (entity instanceof TerminalBlockEntity) return null; // explicitly prevent itself
        if (entity instanceof BaseContainerBlockEntity container) return container;
        return null;
    }

    private static final HashMap<Object, MutableComponent> nameCache = new HashMap<>();

    public static <T> @NotNull MutableComponent getName(T entry, String type, String translationKey, VariantUtil.VariantEntrySet<T> variantEntrySet, VariantUtil.VariantSet variantSet) {
        if (!nameCache.containsKey(entry)) {
            MutableComponent fallback = variantEntrySet.getComponent(type, variantSet);
            // get the translation name directly, otherwise fallback to dynamically creating the translation from the variant set
            MutableComponent output = MutableComponent.create(new TranslatableContentsFallback(
                    translationKey, fallback, TranslatableContents.NO_ARGS));
            nameCache.put(entry, output);
        }
        return nameCache.get(entry).copy();
    }

    @FunctionalInterface
    public interface TriFunction<T1, T2, T3, R> {
        R apply(T1 t1, T2 t2, T3 t3);

        default <V> TriFunction<T1, T2, T3, V> andThen(Function<? super R, ? extends V> after) {
            Objects.requireNonNull(after);
            return (t1, t2, t3) -> after.apply(apply(t1, t2, t3));
        }
    }

    @FunctionalInterface
    public interface TriConsumer<T1, T2, T3> {
        void accept(T1 t1, T2 t2, T3 t3);

        default TriConsumer<T1, T2, T3> andThen(TriConsumer<? super T1, ? super T2, ? super T3> after) {
            Objects.requireNonNull(after);

            return (t1, t2, t3) -> {
                accept(t1, t2, t3);
                after.accept(t1, t2, t3);
            };
        }
    }

    public static ResourceLocation getModID(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static ResourceLocation getIDFromExistingID(ResourceLocation existingID) {
        return getModID(existingID.toString().replaceAll(":", "/"));
    }

    public static @NotNull ResourceLocation getIDFromExistingID(String existingID) {
        return getIDFromExistingID(ResourceLocation.parse(existingID));
    }

    public static <T> ResourceLocation getID(Registry<T> registry, T entry) {
        // DefaultedMappedRegistry doesn't return default for getResourceKey
        return registry.getResourceKey(entry).map(ResourceKey::location).orElse(null);
    }

    public static ResourceLocation addPrefixSuffix(ResourceLocation location, String prefix, String suffix) {
        return ResourceLocation.fromNamespaceAndPath(location.getNamespace(), prefix + location.getPath() + suffix);
    }

    public static ResourceLocation addSuffix(ResourceLocation location, String suffix) {
        return addPrefixSuffix(location, "", suffix);
    }

    public static ResourceLocation addPrefix(ResourceLocation location, String prefix) {
        return addPrefixSuffix(location, prefix, "");
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] toArray(Class<T> clazz, Collection<T> collection) {
        return collection.toArray((T[]) Array.newInstance(clazz, 0));
    }

    public static int moduloPositive(int dividend, int divisor) {
        if (divisor < 0) divisor = -divisor;
        dividend %= divisor;
        if (dividend < 0) dividend += divisor; // wraps number around to always be positive
        return dividend;
    }

    @SafeVarargs
    public static <T> Collection<T> concat(Collection<T>... collections) {
        return Arrays.stream(collections).collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll);
    }

    public static <T> int indexOf(List<T> list, Predicate<T> predicate) {
        return IntStream.range(0, list.size()).filter(index -> predicate.test(list.get(index))).findFirst().orElse(-1);
    }
}
