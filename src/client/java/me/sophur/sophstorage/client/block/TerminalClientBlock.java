package me.sophur.sophstorage.client.block;

import me.sophur.sophstorage.VariantTypes;
import me.sophur.sophstorage.client.ClientUtil;
import me.sophur.sophstorage.block.TerminalBlock;
import me.sophur.sophstorage.client.ClientEntry;
import me.sophur.sophstorage.util.VariantUtil;
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

import static me.sophur.sophstorage.util.Util.addSuffix;
import static me.sophur.sophstorage.client.ClientUtil.*;
import static net.minecraft.data.models.blockstates.VariantProperties.MODEL;

public class TerminalClientBlock extends ClientEntry<TerminalBlock> {
    private static ResourceLocation addOpenCloseSuffix(ResourceLocation id, boolean isOpen) {
        return addSuffix(id, isOpen ? "_open" : "_close");
    }

    private static final ResourceLocation terminalBlockModel = getBlockModelID("terminal");

    @Override
    public void initialise(RuntimeResourcePack pack, Collection<VariantUtil.VariantEntrySet<TerminalBlock>> blockVariants) {
        blockVariants.forEach(variants -> variants.forEach((variantSet, block) -> {
            var id = variants.getID(variantSet);

            var planksTexture = ClientUtil.getBlockModelID(
                    VariantTypes.getPlanksID(variantSet.get(VariantTypes.WOOD_TYPE_VARIANT)));

            // allow translucent pixels in texture
            BlockRenderLayerMap.INSTANCE.putBlock(block, RenderType.translucent());

            // add item model
            pack.addModel(getItemModelID(id),
                    ModelJsonBuilder.create(getItemModelID("terminal")).addTexture("planks", planksTexture));

            // create terminal model with planks texture
            pack.addModel(getBlockModelID(id),
                    ModelJsonBuilder.create(terminalBlockModel)
                            .addTexture("planks", planksTexture));

            for (boolean isOpen : List.of(false, true)) {
                // create opened/closed model
                pack.addModel(getBlockModelID(addOpenCloseSuffix(id, isOpen)),
                        ModelJsonBuilder.create(addOpenCloseSuffix(terminalBlockModel, isOpen))
                                .addTexture("planks", planksTexture));
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
