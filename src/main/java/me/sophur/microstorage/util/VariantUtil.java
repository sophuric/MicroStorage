package me.sophur.microstorage.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class VariantUtil {
    VariantUtil() {
    }

    public static final class Variant<T> {
        public final VariantType<T> type;
        public final T variant;

        private Variant(VariantType<T> type, T variant) {
            this.type = type;
            this.variant = variant;
        }

        public String getName() {
            return type.getName(variant);
        }

        public Ingredient getIngredient() {
            return type.getIngredient(variant);
        }
    }

    // wrapper type
    public static class VariantSet extends ArrayList<Variant<?>> {
        public VariantSet(Variant<?>... variantSet) {
            this(Arrays.stream(variantSet).toList());
        }

        public VariantSet(Collection<Variant<?>> variantSet) {
            super(variantSet);
        }

        public static void loopVariants(Collection<VariantType<?>> variantTypes, Consumer<VariantSet> consumer) {
            new VariantEntrySet<>(null, (ignored, variantSet) -> {
                consumer.accept(variantSet);
                return null;
            }, variantTypes);
        }

        @SuppressWarnings("unchecked")
        public final <U> Variant<U> getVariant(VariantType<U> variantType) {
            var varOpt = this.stream().filter(v -> v.type == variantType).findFirst();
            if (varOpt.isEmpty()) throw new NoSuchElementException("These variants do not include this variant");
            return (Variant<U>) varOpt.get();
        }

        public final <U> U get(VariantType<U> variantType) {
            return this.getVariant(variantType).variant;
        }

        public final <U> String getName(VariantType<U> variantType) {
            return this.getVariant(variantType).getName();
        }

        public final <U> Ingredient getIngredient(VariantType<U> variantType) {
            return this.getVariant(variantType).getIngredient();
        }
    }

    public static final class VariantType<T> {
        public final Class<T> clazz;

        // allows for easy creating new ones and easy calling getName and getVariants

        private final Function<T, String> getNameInternal;
        private final Function<T, Ingredient> getIngredientInternal;

        private final Map<Variant<T>, String> nameCache = new HashMap<>();
        private final Map<Variant<T>, Ingredient> ingredientCache = new HashMap<>();
        private final Map<T, Variant<T>> variantsCache = new HashMap<>();

        public VariantType(Class<T> clazz, Function<T, String> getName,
                           T[] variants, Function<T, Ingredient> getIngredient) {
            this(clazz, getName, Arrays.stream(variants).toList(), getIngredient);
        }

        public VariantType(Class<T> clazz, Function<T, String> getName,
                           List<T> variants, Function<T, Ingredient> getIngredient) {
            this.clazz = clazz;
            getNameInternal = getName;
            getIngredientInternal = getIngredient;

            addNew(variants);
        }

        private void addNew(Collection<T> variants) {
            variants.forEach(variant -> {
                if (variantsCache.containsKey(variant)) return;
                variantsCache.put(variant, new Variant<>(this, variant));
            });
        }

        public Variant<T> get(T t) {
            if (!variantsCache.containsKey(t)) throw new NoSuchElementException("This variant does not contain " + t);
            return variantsCache.get(t);
        }

        public String getName(T t) {
            var variant = get(t);
            if (!nameCache.containsKey(variant))
                nameCache.put(variant, getNameInternal.apply(variant.variant));
            return nameCache.get(variant);
        }

        public Ingredient getIngredient(T t) {
            var variant = get(t);
            if (!ingredientCache.containsKey(variant))
                ingredientCache.put(variant, getIngredientInternal.apply(variant.variant));
            return ingredientCache.get(variant);
        }

        public List<Variant<T>> getVariants() {
            return variantsCache.values().stream().toList();
        }

        public void registerEntry(VariantEntrySet<?> variant) {
            registeredEntries.add(variant);
        }

        // add more after the first few have been populated
        // this is used if another mod creates more WoodTypes AFTER we have already registered our own blocks for each WoodType for example
        public void addMore(Collection<T> moreVariants) {
            addNew(moreVariants);
            registeredEntries.forEach(entry -> entry.populateEntries(new HashSet<>(moreVariants)));
        }

        @SafeVarargs
        public final void addMore(T... moreVariants) {
            addMore(Arrays.stream(moreVariants).toList());
        }

        private final Set<VariantEntrySet<?>> registeredEntries = new HashSet<>();
    }

    // where T is Block or Item for example
    @SuppressWarnings("UnusedReturnValue")
    public static final class VariantEntrySet<T> {
        public ResourceLocation getID(VariantSet variantSet) {
            String variantID = variantSet.stream().map(Variant::getName).collect(Collectors.joining("_"));
            return Util.addPrefix(baseID, variantID + "_");
        }

        public T get(VariantSet variantSet) {
            return entries.get(variantSet);
        }

        public void forEach(BiConsumer<VariantSet, T> action) {
            entries.forEach(action);
        }

        public <U> Stream<U> mapToStream(BiFunction<VariantSet, T, U> mapper) {
            return entries.entrySet().stream().map(entry -> mapper.apply(entry.getKey(), entry.getValue()));
        }

        public <U> List<U> mapToList(BiFunction<VariantSet, T, U> mapper) {
            return mapToStream(mapper).toList();
        }

        public <U> VariantEntrySet<U> map(Util.TriFunction<VariantEntrySet<T>, VariantSet, T, U> mapper) {
            return new VariantEntrySet<>(baseID, (var, variantSet) ->
                    mapper.apply(this, variantSet, this.get(variantSet)), variantTypes);
        }

        private final List<VariantType<?>> variantTypes;
        public final ResourceLocation baseID;
        private final HashMap<VariantSet, T> entries;
        private final BiFunction<VariantEntrySet<T>, VariantSet, T> populateFunction;

        public List<VariantType<?>> getVariantTypes() {
            return List.copyOf(variantTypes);
        }

        public VariantEntrySet(ResourceLocation baseID, BiFunction<VariantEntrySet<T>, VariantSet, T> populateFunction, Collection<VariantType<?>> variantTypes) {
            this.baseID = baseID;
            if (Util.hasDuplicates(variantTypes))
                throw new IllegalArgumentException("Cannot have multiple of the same variant");
            this.variantTypes = List.copyOf(variantTypes);
            entries = new HashMap<>();
            this.populateFunction = populateFunction;

            // register this variety of blocks, items, etc. with the variants
            variantTypes.forEach(v -> v.registerEntry(this));

            populateEntries(null);
        }

        private static final VariantSet NULL_VARIANT = new VariantSet(List.of(new Variant<>(null, null)));

        private boolean addEntry(VariantSet variantSet) {
            if (entries.containsKey(variantSet)) return false;
            entries.put(variantSet, populateFunction.apply(this, variantSet));
            return true;
        }

        private int populateEntries(Set<?> filterVariants) {
            // no variants, populate only once
            if (this.variantTypes.isEmpty()) {
                addEntry(NULL_VARIANT);
                return 1;
            }

            if (entries.containsKey(NULL_VARIANT))
                throw new UnsupportedOperationException("Cannot add variant types after it has already been populated with no variant types");

            // loop blocks with n-depth, where n is the number of variants

            int entriesLooped = 0;
            var variants = this.variantTypes.stream().map(VariantType::getVariants).toList();
            List<Integer> lengths = variants.stream().map(List::size).toList();
            int variantsLen = lengths.size();
            int[] variantIndices = new int[variantsLen];
            while (true) {
                // map the array of indices to the list of variants
                VariantSet variantSet = new VariantSet(IntStream.range(0, variantsLen)
                        .mapToObj(index -> variants.get(index).get(variantIndices[index])).collect(Collectors.toList()));

                // skip this current variant set if it doesn't contain the new variant types
                if (filterVariants == null || filterVariants.stream().anyMatch(filterVariants::contains)) {
                    addEntry(variantSet);
                    ++entriesLooped;
                }

                // increment
                int i;
                for (i = variantsLen - 1; i >= 0; --i) {
                    // increment last index
                    ++variantIndices[i];
                    // loop back to the start and increment the index before
                    if (variantIndices[i] >= lengths.get(i)) variantIndices[i] = 0;
                    else break;
                }
                if (i == -1) return entriesLooped; // finished

            }
        }

        public Map<VariantSet, T> getEntries() {
            return entries;
        }

        public Collection<T> getValues() {
            return getEntries().values();
        }
    }
}
