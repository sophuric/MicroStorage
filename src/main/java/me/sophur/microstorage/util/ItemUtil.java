package me.sophur.microstorage.util;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;

import static net.minecraft.data.recipes.RecipeProvider.has;

public class ItemUtil {
    private ItemUtil() {
    }

    public static <T extends Item> VariantUtil.VariantEntrySet<T> register(Util.TriFunction<VariantUtil.VariantEntrySet<T>, VariantUtil.VariantSet, ResourceLocation, T> itemFactory, ResourceLocation baseID, VariantUtil.VariantType<?>... variants) {
        return register(itemFactory, baseID, Arrays.stream(variants).toList());
    }

    public static <T extends Item> VariantUtil.VariantEntrySet<T> register(Util.TriFunction<VariantUtil.VariantEntrySet<T>, VariantUtil.VariantSet, ResourceLocation, T> itemFactory, ResourceLocation baseID, Collection<VariantUtil.VariantType<?>> variants) {
        VariantUtil.VariantEntrySet<T> v = new VariantUtil.VariantEntrySet<>(baseID, (variantEntrySet, variantSet) -> {
            ResourceLocation id = variantEntrySet.getID(variantSet);

            T item = itemFactory.apply(variantEntrySet, variantSet, id);
            if (item == null) return null;

            item = Registry.register(BuiltInRegistries.ITEM, id, item);

            return item;
        }, variants);
        v.registerWithVariantTypes();
        return v;
    }

    public static Item getFirstIngredientItem(VariantUtil.Variant<?> ingredient) {
        return getFirstIngredientItem(ingredient.getIngredient());
    }

    public static Item getFirstIngredientItem(Ingredient ingredient) {
        if (ingredient == null) return null;
        var itemStackOpt = Arrays.stream(ingredient.getItems()).findFirst();
        return itemStackOpt.map(ItemStack::getItem).orElse(null);
    }

    public static int getNumericID(Item item) {
        return BuiltInRegistries.ITEM.getId(item);
    }

    public static <T extends Item> @NotNull MutableComponent getItemName(T item, VariantUtil.VariantEntrySet<T> variantEntrySet, VariantUtil.VariantSet variantSet) {
        if (getID(item) == null) return Component.empty();
        return Util.getName(item, "item", item.getDescriptionId(), variantEntrySet, variantSet);
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

    public static ResourceLocation getID(Item item) {
        return Util.getID(BuiltInRegistries.ITEM, item);
    }

    public static ResourceLocation getID(ItemLike item) {
        return getID(item.asItem());
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

    public static CreativeModeTab getCreativeTab(Item item) {
        return CreativeModeTabs.allTabs().stream().filter(tab ->
                tab.getSearchTabDisplayItems().stream().anyMatch(itemStack -> itemStack.is(item))
        ).findFirst().orElse(null);
    }

    public static CreativeModeTab getCreativeTab(ItemStack itemStack) {
        return CreativeModeTabs.allTabs().stream().filter(tab ->
                tab.getSearchTabDisplayItems().contains(itemStack)
        ).findFirst().orElse(null);
    }

    public static int getIndexInTab(CreativeModeTab tab, ItemStack item) {
        return Util.indexOf(tab.getSearchTabDisplayItems().stream().toList(), item::equals);
    }

    public static int getIndexInTab(CreativeModeTab tab, Item item) {
        return Util.indexOf(tab.getSearchTabDisplayItems().stream().toList(), i -> item.equals(i.getItem()));
    }

    public static int getCreativeTabIndex(CreativeModeTab tab) {
        return CreativeModeTabs.allTabs().indexOf(tab);
    }

    // compares items in the creative inventory, otherwise compare their IDs in the registry
    public static int compareCreativeTabItems(Item a, Item b) {
        if (Objects.equals(a, b)) return 0;
        var tabA = getCreativeTab(a);
        var tabB = getCreativeTab(b);
        if (tabA != null && tabB != null) {
            // compare tab indices
            if (!Objects.equals(tabA, tabB))
                return Integer.compare(getCreativeTabIndex(tabA), getCreativeTabIndex(tabB));

            // compare item indices in tab
            var indexA = getIndexInTab(tabA, a);
            var indexB = getIndexInTab(tabB, b);
            if (indexA != -1 && indexB != -1) return Integer.compare(indexA, indexB);
        }
        // compare in registry, flip for some reason
        return Comparator.comparingInt(ItemUtil::getNumericID).compare(a, b);
    }
}
