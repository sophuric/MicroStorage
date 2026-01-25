package me.sophur.sophstorage.util;

import pers.solid.brrp.v1.api.RuntimeResourcePack;

import java.util.Optional;

public interface DataProvider<T> {
    default void beforeLoad(RuntimeResourcePack pack) {
    }

    void perItem(VariantUtil.VariantEntrySet<T> variantEntrySet, VariantUtil.VariantSet variantSet, RuntimeResourcePack pack);

    default void afterLoad(RuntimeResourcePack pack) {
    }

    static <T extends DataProvider<T>> void createData(VariantUtil.VariantEntrySet<T> variantEntrySet, RuntimeResourcePack pack) {
        Optional<T> first = variantEntrySet.getValues().stream().findFirst();
        createData(variantEntrySet, first.orElseThrow(), pack);
    }

    static <T extends DataProvider<T>> void createData(VariantUtil.VariantEntrySet<T> variantEntrySet, T instance, RuntimeResourcePack pack) {
        instance.beforeLoad(pack);
        variantEntrySet.forEach((variantSet, t) -> t.perItem(variantEntrySet, variantSet, pack));
        instance.afterLoad(pack);
    }
}
