package me.sophur.microstorage.client.block;

import me.sophur.microstorage.VariantTypes;
import me.sophur.microstorage.client.ClientEntry;
import me.sophur.microstorage.client.ClientUtil;
import me.sophur.microstorage.util.ConnectingBlockUtil;
import me.sophur.microstorage.util.VariantUtil;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.data.models.blockstates.Condition;
import net.minecraft.data.models.blockstates.MultiPartGenerator;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import pers.solid.brrp.v1.api.RuntimeResourcePack;
import pers.solid.brrp.v1.model.ModelJsonBuilder;

import java.util.Collection;

import static me.sophur.microstorage.VariantTypes.DYE_COLOR_VARIANT;
import static me.sophur.microstorage.client.ClientUtil.*;
import static net.minecraft.data.models.blockstates.VariantProperties.MODEL;

public class TrimInterfaceClientBlock<T extends Block> extends ClientEntry<T> {
    private static final ResourceLocation trimBlockModel = getBlockModelID("trim");

    private final String baseID;

    public TrimInterfaceClientBlock(String type) {
        this.baseID = "base_" + type; // allows choosing between trim and interface
    }

    @Override
    public void initialise(RuntimeResourcePack pack, Collection<VariantUtil.VariantEntrySet<T>> blockVariants) {
        blockVariants.forEach(variants -> variants.forEach((variantSet, block) -> {
            var id = variants.getID(variantSet);

            var dye = variantSet.hasVariant(DYE_COLOR_VARIANT) ? variantSet.get(DYE_COLOR_VARIANT) : null;
            var glassTexture = ClientUtil.getBlockModelID(VariantTypes.getGlassID(dye));

            // allow translucent pixels in texture
            BlockRenderLayerMap.INSTANCE.putBlock(block, RenderType.translucent());
            // FIXME: fix graphical transparency issues with the item model

            // add item model
            pack.addModel(getItemModelID(id), ModelJsonBuilder.create(getItemModelID(baseID)).addTexture("glass", glassTexture));

            // create trim/interface model with glass texture
            pack.addModel(getBlockModelID(id), ModelJsonBuilder.create(getBlockModelID(baseID)).addTexture("glass", glassTexture));

            MultiPartGenerator blockState = MultiPartGenerator.multiPart(block);

            blockState.with(new Variant().with(MODEL, getBlockModelID(id)));

            // loop all directions
            ConnectingBlockUtil.DIRECTION_PROPERTIES.forEach((direction, prop) -> {
                Variant variant = new Variant().with(MODEL, getBlockModelID("trim_connector"));
                rotateVariantFromNorth(variant, direction, true);
                Condition.TerminalCondition cond = Condition.condition().term(prop, true);
                blockState.with(cond, variant);
            });

            pack.addBlockState(id, blockState);
        }));
    }
}
