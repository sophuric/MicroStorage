package me.sophur.microstorage.client;

import net.minecraft.core.Direction;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.resources.ResourceLocation;

import static me.sophur.microstorage.util.Util.*;
import static net.minecraft.data.models.blockstates.VariantProperties.*;

public class ClientUtil {
    private ClientUtil() {
    }

    public static VariantProperties.Rotation toVariantRotation(int turns) {
        return VariantProperties.Rotation.values()[moduloPositive(turns, 4)];
    }

    public static ResourceLocation getItemModelID(ResourceLocation location, String prefix, String suffix) {
        return addPrefixSuffix(location, "item/" + prefix, suffix);
    }

    public static ResourceLocation getItemModelID(ResourceLocation location, String suffix) {
        return getItemModelID(location, "", suffix);
    }

    public static ResourceLocation getItemModelID(ResourceLocation location) {
        return getItemModelID(location, "");
    }

    public static ResourceLocation getItemModelID(String path) {
        return getItemModelID(getModID(path));
    }

    public static ResourceLocation getBlockModelID(ResourceLocation location, String prefix, String suffix) {
        return addPrefixSuffix(location, "block/" + prefix, suffix);
    }

    public static ResourceLocation getBlockModelID(ResourceLocation location, String suffix) {
        return getBlockModelID(location, "", suffix);
    }

    public static ResourceLocation getBlockModelID(ResourceLocation location) {
        return getBlockModelID(location, "");
    }

    public static ResourceLocation getBlockModelID(String path) {
        return getBlockModelID(getModID(path));
    }

    public static Variant cloneVariant(Variant v) {
        return Variant.merge(v, new Variant());
    }

    public static Variant rotateVariantFromNorth(Variant v, Direction direction, boolean uvLock) {
        final int northData2D = Direction.NORTH.get2DDataValue();
        int data2D = direction.get2DDataValue();
        if (data2D != -1) v.with(Y_ROT, toVariantRotation(data2D - northData2D)); // horizontal
        else v.with(X_ROT, toVariantRotation(direction == Direction.DOWN ? 1 : -1)); // vertical
        v.with(UV_LOCK, uvLock);
        return v;
    }
}
