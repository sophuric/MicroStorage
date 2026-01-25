package me.sophur.sophstorage.client;

import me.sophur.sophstorage.util.VariantUtil;
import pers.solid.brrp.v1.api.RuntimeResourcePack;

import java.util.Arrays;
import java.util.Collection;

public abstract class ClientEntry<T> {
    protected ClientEntry() {
    }

    public abstract void initialise(RuntimeResourcePack pack, Collection<VariantUtil.VariantEntrySet<T>> blockVariants);

    @SafeVarargs
    public final void initialise(RuntimeResourcePack pack, VariantUtil.VariantEntrySet<T>... blockVariants) {
        initialise(pack, Arrays.stream(blockVariants).toList());
    }
}
