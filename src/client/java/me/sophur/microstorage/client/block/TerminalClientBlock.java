package me.sophur.microstorage.client.block;

import me.sophur.microstorage.VariantTypes;
import me.sophur.microstorage.client.ClientUtil;
import me.sophur.microstorage.block.TerminalBlock;
import me.sophur.microstorage.client.ClientEntry;
import me.sophur.microstorage.util.VariantUtil;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.data.models.blockstates.Condition;
import net.minecraft.data.models.blockstates.MultiPartGenerator;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.resources.ResourceLocation;
import pers.solid.brrp.v1.api.RuntimeResourcePack;
import pers.solid.brrp.v1.model.ModelJsonBuilder;

import java.util.Collection;
import java.util.List;

import static me.sophur.microstorage.util.Util.addSuffix;
import static me.sophur.microstorage.client.ClientUtil.*;
import static net.minecraft.data.models.blockstates.VariantProperties.MODEL;

public class TerminalClientBlock extends ClientEntry<TerminalBlock> {
    private static ResourceLocation addOpenCloseSuffix(ResourceLocation id, boolean isOpen) {
        return addSuffix(id, isOpen ? "_open" : "_close");
    }

    private final static String TYPE = "terminal";
    private final static String PLANKS = "planks";

    @Override
    public void initialise(RuntimeResourcePack pack, Collection<VariantUtil.VariantEntrySet<TerminalBlock>> blockVariants) {
        blockVariants.forEach(variants -> variants.forEach((variantSet, block) -> {
            var id = variants.getID(variantSet);

            var planksTexture = ClientUtil.getBlockModelID(
                    VariantTypes.getPlanksID(variantSet.get(VariantTypes.WOOD_TYPE_VARIANT)));

            // allow translucent pixels in texture
            BlockRenderLayerMap.INSTANCE.putBlock(block, RenderType.translucent());

            // add item model
            pack.addModel(getItemModelID(id), ModelJsonBuilder.create(getItemModelID(TYPE)).addTexture(PLANKS, planksTexture));

            // create terminal model with planks texture
            pack.addModel(getBlockModelID(id), ModelJsonBuilder.create(getBlockModelID(TYPE)).addTexture(PLANKS, planksTexture));

            for (boolean isOpen : List.of(false, true)) {
                // create opened/closed model
                pack.addModel(getBlockModelID(addOpenCloseSuffix(id, isOpen)),
                        ModelJsonBuilder.create(addOpenCloseSuffix(getBlockModelID(TYPE), isOpen))
                                .addTexture(PLANKS, planksTexture));
            }

            MultiPartGenerator blockState = MultiPartGenerator.multiPart(block);

            // loop all directions
            for (Direction direction : TerminalBlock.DIRECTION.getPossibleValues()) {
                // create variant for this direction
                Variant variant = new Variant().with(MODEL, getBlockModelID(id));
                rotateVariantFromNorth(variant, direction, true);
                Condition.TerminalCondition cond = Condition.condition().term(TerminalBlock.DIRECTION, direction);
                blockState.with(cond, variant);

                // create variants for opened and closed
                for (boolean isOpen : List.of(false, true)) {
                    Variant variant2 = new Variant().with(MODEL, getBlockModelID(addOpenCloseSuffix(id, isOpen)));
                    rotateVariantFromNorth(variant2, direction, true);
                    Condition.TerminalCondition cond2 = Condition.condition()
                            .term(TerminalBlock.DIRECTION, direction)
                            .term(TerminalBlock.OPEN, isOpen);
                    blockState.with(cond2, variant2);
                }
            }

            pack.addBlockState(id, blockState);
        }));
    }
}
