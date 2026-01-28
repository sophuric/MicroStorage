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
import net.minecraft.world.level.block.state.properties.AttachFace;
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

    @Override
    public void initialise(RuntimeResourcePack pack, Collection<VariantUtil.VariantEntrySet<TerminalBlock>> blockVariants) {
        blockVariants.forEach(variants -> variants.forEach((variantSet, block) -> {
            var id = variants.getID(variantSet);

            var planksTexture = ClientUtil.getBlockModelID(
                    VariantTypes.getPlanksID(variantSet.get(VariantTypes.WOOD_TYPE_VARIANT)));

            // allow translucent pixels in texture
            BlockRenderLayerMap.INSTANCE.putBlock(block, RenderType.translucent());

            // add item model
            pack.addModel(getItemModelID(id), ModelJsonBuilder.create(getItemModelID("terminal")).addTexture("planks", planksTexture));

            // create terminal model with planks texture
            // a separate model for the parts that use the planks texture allows us to have separate textures with/without UV-lock
            pack.addModel(getBlockModelID(id), ModelJsonBuilder.create(getBlockModelID("terminal_planks")).addTexture("planks", planksTexture));

            for (boolean isOpen : List.of(false, true)) {
                // create opened/closed model
                pack.addModel(getBlockModelID(addOpenCloseSuffix(id, isOpen)),
                        ModelJsonBuilder.create(addOpenCloseSuffix(getBlockModelID("terminal_planks"), isOpen))
                                .addTexture("planks", planksTexture));
            }

            MultiPartGenerator blockState = MultiPartGenerator.multiPart(block);

            // loop all directions
            for (AttachFace face : TerminalBlock.FACE.getPossibleValues())
                for (Direction facing : TerminalBlock.FACING.getPossibleValues()) {
                    Condition.TerminalCondition cond = Condition.condition()
                            .term(TerminalBlock.FACE, face)
                            .term(TerminalBlock.FACING, facing);

                    // create variant for this direction
                    Variant variant = new Variant().with(MODEL, getBlockModelID(id));
                    rotateVariantFromNorth(variant, face, facing, true);
                    blockState.with(cond, variant);

                    variant = new Variant().with(MODEL, getBlockModelID("terminal"));
                    rotateVariantFromNorth(variant, face, facing, false);
                    blockState.with(cond, variant);

                    // create variants for opened and closed
                    for (boolean isOpen : List.of(false, true)) {
                        cond = Condition.condition()
                                .term(TerminalBlock.FACE, face)
                                .term(TerminalBlock.FACING, facing)
                                .term(TerminalBlock.OPEN, isOpen);

                        variant = new Variant().with(MODEL, addOpenCloseSuffix(getBlockModelID(id), isOpen));
                        rotateVariantFromNorth(variant, face, facing, true);
                        blockState.with(cond, variant);

                        variant = new Variant().with(MODEL, addOpenCloseSuffix(getBlockModelID("terminal"), isOpen));
                        rotateVariantFromNorth(variant, face, facing, false);
                        blockState.with(cond, variant);
                    }
                }

            pack.addBlockState(id, blockState);
        }));
    }
}
