package me.sophur.sophstorage.client;

import me.sophur.sophstorage.Blocks;
import net.minecraft.data.models.blockstates.BlockStateGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.WoodType;
import pers.solid.brrp.v1.api.RuntimeResourcePack;
import pers.solid.brrp.v1.model.ModelJsonBuilder;

public abstract class ClientBlock<T extends Block> {
    protected ClientBlock() {
    }

    public abstract BlockStateGenerator generateBlockState(Blocks.BlockID<T> block, WoodType wood, Blocks.WoodTypeBlocks woodTypeBlocks, ResourceLocation planksTexture, RuntimeResourcePack pack);

    public abstract ModelJsonBuilder generateItemModel(Blocks.BlockID<T> block, WoodType wood, Blocks.WoodTypeBlocks woodTypeBlocks, ResourceLocation planksTexture, RuntimeResourcePack pack);

    public abstract void register();
}
