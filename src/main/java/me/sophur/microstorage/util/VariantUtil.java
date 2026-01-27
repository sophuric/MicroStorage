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
            if (type == null) return null;
            return type.getName(variant);
        }

        public Ingredient getIngredient() {
            if (type == null) return null;
            return type.getIngredient(variant);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Variant<?> variant1)) return false;
            return Objects.equals(type, variant1.type) && Objects.equals(variant, variant1.variant);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, variant);
        }
    }

    // wrapper type, there is one of these per block/item/etc
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

        public final <U> boolean hasVariant(VariantType<U> variantType) {
            return this.stream().anyMatch(v -> v.type == variantType);
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
        private final Class<T> variantClass;

        public Class<T> getVariantClass() {
            return variantClass;
        }

        // allows for easy creating new ones and easy calling getName and getVariants

        private final Function<T, String> getNameInternal;
        private final Function<T, Ingredient> getIngredientInternal;

        private final Map<Variant<T>, String> nameCache = new HashMap<>();
        private final Map<Variant<T>, Ingredient> ingredientCache = new HashMap<>();
        private final Map<T, Variant<T>> variantsCache = new HashMap<>();

        public VariantType(Class<T> variantClass, Function<T, String> getName,
                           T[] variants, Function<T, Ingredient> getIngredient) {
            this(variantClass, getName, Arrays.stream(variants).toList(), getIngredient);
        }

        public VariantType(Class<T> variantClass, Function<T, String> getName,
                           List<T> variants, Function<T, Ingredient> getIngredient) {
            this.variantClass = variantClass;
            getNameInternal = getName;
            getIngredientInternal = getIngredient;

            addNew(variants);
        }

        public VariantType(VariantType<T> variantType) {
            this(variantType.variantClass, variantType.getNameInternal, variantType.getActualVariants(), variantType.getIngredientInternal);
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

        public List<T> getActualVariants() {
            return variantsCache.values().stream().map(v -> v.variant).toList();
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
            if (variantSet == NULL_VARIANT_SET) return baseID;
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

            populateEntries(null);
        }

        public void registerWithVariantTypes() {
            // register this variety of blocks, items, etc. with the variants
            variantTypes.forEach(v -> v.registerEntry(this));
        }

        private static final VariantSet NULL_VARIANT_SET = new VariantSet(List.of());

        private boolean addEntry(VariantSet variantSet) {
            if (entries.containsKey(variantSet)) return false;
            entries.put(variantSet, populateFunction.apply(this, variantSet));
            return true;
        }

        private int populateEntries(Set<?> filterVariants) {
            // no variants, populate only once
            if (this.variantTypes.isEmpty()) {
                addEntry(NULL_VARIANT_SET);
                return 1;
            }

            if (entries.containsKey(NULL_VARIANT_SET))
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

        // loop the union of variants of multiple entry sets
        // this is useful if you want to loop VariantEntrySet-major order instead of VariantSet-major order
        // i.e. oak planks, oak stairs, oak slab, birch planks, birch stairs birch slab, rather than
        // oak planks, birch planks, oak stairs, birch stairs, oak slab, birch slab
        public static <T> void loopVariantEntrySets(BiConsumer<VariantUtil.VariantSet, ? super T> consumer, Collection<VariantUtil.VariantEntrySet<? extends T>> variantEntrySets) {
            ArrayList<VariantEntrySet<? extends T>> variantEntrySetsReversed = new ArrayList<>(variantEntrySets);
            Collections.reverse(variantEntrySetsReversed);

            Set<VariantUtil.VariantType<?>> allVariantTypes = variantEntrySetsReversed.stream()
                    .collect(LinkedHashSet::new, (variantTypes, variantEntrySet) ->
                            variantTypes.addAll(variantEntrySet.getVariantTypes()), LinkedHashSet::addAll);

            VariantUtil.VariantSet.loopVariants(allVariantTypes, variantSet ->
                    variantEntrySets.forEach((variantEntrySet) -> {
                        // get subset of allVariantTypes that has all of variantEntrySet
                        VariantUtil.VariantSet subset = new VariantUtil.VariantSet(variantSet.stream().filter(variant ->
                                variantEntrySet.getVariantTypes().contains(variant.type)).toList());
                        var entry = variantEntrySet.get(subset);
                        if (entry != null) consumer.accept(subset, entry);
                    }));
        }

        @SafeVarargs
        public static <T> void loopVariantEntrySets(BiConsumer<VariantUtil.VariantSet, ? super T> consumer, VariantUtil.VariantEntrySet<? extends T>... variantEntrySets) {
            loopVariantEntrySets(consumer, Arrays.stream(variantEntrySets).toList());
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof VariantEntrySet<?> that)) return false;
            return Objects.equals(variantTypes, that.variantTypes) && Objects.equals(baseID, that.baseID) && Objects.equals(entries, that.entries);
        }

        @Override
        public int hashCode() {
            return Objects.hash(variantTypes, baseID, entries);
        }

        // gets the only entry, fails if there is not exactly one entry
        public T getOnly() {
            Collection<T> entries = getValues();
            if (entries.isEmpty()) throw new NoSuchElementException("There are no entries in this VariantEntrySet");
            if (entries.size() != 1)
                throw new IllegalStateException("There are multiple entries in this VariantEntrySet");
            return entries.stream().findFirst().get();
        }
    }
}
