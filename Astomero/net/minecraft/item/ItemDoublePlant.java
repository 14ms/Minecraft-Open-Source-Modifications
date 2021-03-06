package net.minecraft.item;

import com.google.common.base.*;
import net.minecraft.block.*;
import net.minecraft.world.*;

public class ItemDoublePlant extends ItemMultiTexture
{
    public ItemDoublePlant(final Block block, final Block block2, final Function<ItemStack, String> nameFunction) {
        super(block, block2, nameFunction);
    }
    
    @Override
    public int getColorFromItemStack(final ItemStack stack, final int renderPass) {
        final BlockDoublePlant.EnumPlantType blockdoubleplantenumplanttype = BlockDoublePlant.EnumPlantType.byMetadata(stack.getMetadata());
        return (blockdoubleplantenumplanttype != BlockDoublePlant.EnumPlantType.GRASS && blockdoubleplantenumplanttype != BlockDoublePlant.EnumPlantType.FERN) ? super.getColorFromItemStack(stack, renderPass) : ColorizerGrass.getGrassColor(0.5, 1.0);
    }
}
