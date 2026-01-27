package me.sophur.microstorage.util;

import me.sophur.microstorage.blockentity.TerminalBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;

import static me.sophur.microstorage.MicroStorage.MOD_ID;
import static net.minecraft.data.recipes.RecipeProvider.has;

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

    public static Block getBlock(ResourceLocation id) {
        return BuiltInRegistries.BLOCK.getOptional(id).orElse(null); // bypasses returning the "default" value, see net.minecraft.core.DefaultedMappedRegistry::get
    }

    public static Block getBlock(String id) {
        return getBlock(ResourceLocation.parse(id));
    }

    public static Item getItem(ResourceLocation id) {
        return BuiltInRegistries.ITEM.getOptional(id).orElse(null);
    }

    public static Item getItem(String id) {
        return getItem(ResourceLocation.parse(id));
    }

    public static BlockItem getItem(Block block) {
        Item item = block.asItem();
        if (item == Items.AIR) return null;
        if (item instanceof BlockItem blockItem) return blockItem;
        return null;
    }

    public static ResourceLocation getID(BlockState blockState) {
        return getID(blockState.getBlock());
    }

    public static ResourceLocation getID(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block);
    }

    public static ResourceLocation getID(Item item) {
        return BuiltInRegistries.ITEM.getKey(item);
    }

    // rename to avoid possible mixup, since Block implements ItemLike
    public static ResourceLocation getItemID(ItemLike item) {
        return getID(item.asItem());
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

    public static RecipeBuilder recipeSurround8(RecipeCategory category, ItemLike input, ItemLike dye, ItemLike output) {
        // similar to VanillaRecipeProvider::stainedGlassFromGlassAndDye
        return ShapedRecipeBuilder.shaped(category, output, 8)
                .define('#', input)
                .define('X', dye)
                .pattern("###")
                .pattern("#X#")
                .pattern("###")
                .group("stained_glass")
                .unlockedBy("has_glass", has(input));
    }

}
