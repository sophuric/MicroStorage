package me.sophur.sophstorage.client.block;

import me.sophur.sophstorage.Blocks;
import me.sophur.sophstorage.block.TerminalBlock;
import me.sophur.sophstorage.client.ClientBlock;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.data.models.blockstates.BlockStateGenerator;
import net.minecraft.data.models.blockstates.Condition;
import net.minecraft.data.models.blockstates.MultiPartGenerator;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.WoodType;
import pers.solid.brrp.v1.api.RuntimeResourcePack;
import pers.solid.brrp.v1.model.ModelJsonBuilder;

import java.util.List;

import static me.sophur.sophstorage.Util.addSuffix;
import static me.sophur.sophstorage.client.ClientUtil.*;
import static me.sophur.sophstorage.client.ClientUtil.getBlockModelID;
import static me.sophur.sophstorage.client.ClientUtil.rotateVariantFromNorth;
import static net.minecraft.data.models.blockstates.VariantProperties.MODEL;

public class TerminalClientBlock extends ClientBlock<TerminalBlock> {
    public static TerminalClientBlock INSTANCE = new TerminalClientBlock();

    private static ResourceLocation addOpenCloseSuffix(ResourceLocation id, boolean isOpen) {
        return addSuffix(id, isOpen ? "_open" : "_close");
    }

    @Override
    public ModelJsonBuilder generateItemModel(Blocks.BlockID<TerminalBlock> block, WoodType wood, Blocks.WoodTypeBlocks woodTypeBlocks, ResourceLocation planksTexture, RuntimeResourcePack pack) {
        return ModelJsonBuilder.create(getItemModelID("terminal")).addTexture("planks", planksTexture);
    }

    @Override
    public void register() {
    }

    private static final ResourceLocation terminalBlockModel = getBlockModelID("terminal");

    @Override
    public BlockStateGenerator generateBlockState(Blocks.BlockID<TerminalBlock> block, WoodType wood, Blocks.WoodTypeBlocks woodTypeBlocks, ResourceLocation planksTexture, RuntimeResourcePack pack) {
        // allow translucent pixels in texture
        BlockRenderLayerMap.INSTANCE.putBlock(block.block(), RenderType.translucent());

        // create terminal model with planks texture
        pack.addModel(getBlockModelID(block.blockID()),
                ModelJsonBuilder.create(terminalBlockModel)
                        .addTexture("planks", planksTexture));

        for (boolean isOpen : List.of(false, true)) {
            // create opened/closed model
            pack.addModel(getBlockModelID(addOpenCloseSuffix(block.blockID(), isOpen)),
                    ModelJsonBuilder.create(addOpenCloseSuffix(terminalBlockModel, isOpen))
                            .addTexture("planks", planksTexture));
        }

        MultiPartGenerator blockState = MultiPartGenerator.multiPart(block.block());

        // loop all directions
        for (Direction direction : TerminalBlock.DIRECTION.getPossibleValues()) {
            // create variant for this direction
            Variant variant = new Variant().with(MODEL, getBlockModelID(block.blockID()));
            rotateVariantFromNorth(variant, direction, true);
            Condition.TerminalCondition cond = Condition.condition().term(TerminalBlock.DIRECTION, direction);
            blockState.with(cond, variant);

            // create variants for opened and closed
            for (boolean isOpen : List.of(false, true)) {
                Variant variant2 = new Variant().with(MODEL, getBlockModelID(addOpenCloseSuffix(block.blockID(), isOpen)));
                rotateVariantFromNorth(variant2, direction, true);
                Condition.TerminalCondition cond2 = Condition.condition()
                        .term(TerminalBlock.DIRECTION, direction)
                        .term(TerminalBlock.OPEN, isOpen);
                blockState.with(cond2, variant2);
            }
        }
        return blockState;
    }
}
