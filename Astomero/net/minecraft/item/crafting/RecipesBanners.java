package net.minecraft.item.crafting;

import net.minecraft.item.*;
import net.minecraft.init.*;
import net.minecraft.inventory.*;
import net.minecraft.world.*;
import net.minecraft.tileentity.*;
import net.minecraft.nbt.*;

public class RecipesBanners
{
    void addRecipes(final CraftingManager p_179534_1_) {
        for (final EnumDyeColor enumdyecolor : EnumDyeColor.values()) {
            p_179534_1_.addRecipe(new ItemStack(Items.banner, 1, enumdyecolor.getDyeDamage()), "###", "###", " | ", '#', new ItemStack(Blocks.wool, 1, enumdyecolor.getMetadata()), '|', Items.stick);
        }
        p_179534_1_.addRecipe(new RecipeDuplicatePattern());
        p_179534_1_.addRecipe(new RecipeAddPattern());
    }
    
    static class RecipeAddPattern implements IRecipe
    {
        private RecipeAddPattern() {
        }
        
        @Override
        public boolean matches(final InventoryCrafting inv, final World worldIn) {
            boolean flag = false;
            for (int i = 0; i < inv.getSizeInventory(); ++i) {
                final ItemStack itemstack = inv.getStackInSlot(i);
                if (itemstack != null && itemstack.getItem() == Items.banner) {
                    if (flag) {
                        return false;
                    }
                    if (TileEntityBanner.getPatterns(itemstack) >= 6) {
                        return false;
                    }
                    flag = true;
                }
            }
            return flag && this.func_179533_c(inv) != null;
        }
        
        @Override
        public ItemStack getCraftingResult(final InventoryCrafting inv) {
            ItemStack itemstack = null;
            for (int i = 0; i < inv.getSizeInventory(); ++i) {
                final ItemStack itemstack2 = inv.getStackInSlot(i);
                if (itemstack2 != null && itemstack2.getItem() == Items.banner) {
                    itemstack = itemstack2.copy();
                    itemstack.stackSize = 1;
                    break;
                }
            }
            final TileEntityBanner.EnumBannerPattern tileentitybannerenumbannerpattern = this.func_179533_c(inv);
            if (tileentitybannerenumbannerpattern != null) {
                int k = 0;
                for (int j = 0; j < inv.getSizeInventory(); ++j) {
                    final ItemStack itemstack3 = inv.getStackInSlot(j);
                    if (itemstack3 != null && itemstack3.getItem() == Items.dye) {
                        k = itemstack3.getMetadata();
                        break;
                    }
                }
                final NBTTagCompound nbttagcompound1 = itemstack.getSubCompound("BlockEntityTag", true);
                NBTTagList nbttaglist = null;
                if (nbttagcompound1.hasKey("Patterns", 9)) {
                    nbttaglist = nbttagcompound1.getTagList("Patterns", 10);
                }
                else {
                    nbttaglist = new NBTTagList();
                    nbttagcompound1.setTag("Patterns", nbttaglist);
                }
                final NBTTagCompound nbttagcompound2 = new NBTTagCompound();
                nbttagcompound2.setString("Pattern", tileentitybannerenumbannerpattern.getPatternID());
                nbttagcompound2.setInteger("Color", k);
                nbttaglist.appendTag(nbttagcompound2);
            }
            return itemstack;
        }
        
        @Override
        public int getRecipeSize() {
            return 10;
        }
        
        @Override
        public ItemStack getRecipeOutput() {
            return null;
        }
        
        @Override
        public ItemStack[] getRemainingItems(final InventoryCrafting inv) {
            final ItemStack[] aitemstack = new ItemStack[inv.getSizeInventory()];
            for (int i = 0; i < aitemstack.length; ++i) {
                final ItemStack itemstack = inv.getStackInSlot(i);
                if (itemstack != null && itemstack.getItem().hasContainerItem()) {
                    aitemstack[i] = new ItemStack(itemstack.getItem().getContainerItem());
                }
            }
            return aitemstack;
        }
        
        private TileEntityBanner.EnumBannerPattern func_179533_c(final InventoryCrafting p_179533_1_) {
            for (final TileEntityBanner.EnumBannerPattern tileentitybannerenumbannerpattern : TileEntityBanner.EnumBannerPattern.values()) {
                if (tileentitybannerenumbannerpattern.hasValidCrafting()) {
                    boolean flag = true;
                    if (tileentitybannerenumbannerpattern.hasCraftingStack()) {
                        boolean flag2 = false;
                        boolean flag3 = false;
                        for (int i = 0; i < p_179533_1_.getSizeInventory() && flag; ++i) {
                            final ItemStack itemstack = p_179533_1_.getStackInSlot(i);
                            if (itemstack != null && itemstack.getItem() != Items.banner) {
                                if (itemstack.getItem() == Items.dye) {
                                    if (flag3) {
                                        flag = false;
                                        break;
                                    }
                                    flag3 = true;
                                }
                                else {
                                    if (flag2 || !itemstack.isItemEqual(tileentitybannerenumbannerpattern.getCraftingStack())) {
                                        flag = false;
                                        break;
                                    }
                                    flag2 = true;
                                }
                            }
                        }
                        if (!flag2) {
                            flag = false;
                        }
                    }
                    else if (p_179533_1_.getSizeInventory() == tileentitybannerenumbannerpattern.getCraftingLayers().length * tileentitybannerenumbannerpattern.getCraftingLayers()[0].length()) {
                        int j = -1;
                        for (int k = 0; k < p_179533_1_.getSizeInventory() && flag; ++k) {
                            final int l = k / 3;
                            final int i2 = k % 3;
                            final ItemStack itemstack2 = p_179533_1_.getStackInSlot(k);
                            if (itemstack2 != null && itemstack2.getItem() != Items.banner) {
                                if (itemstack2.getItem() != Items.dye) {
                                    flag = false;
                                    break;
                                }
                                if (j != -1 && j != itemstack2.getMetadata()) {
                                    flag = false;
                                    break;
                                }
                                if (tileentitybannerenumbannerpattern.getCraftingLayers()[l].charAt(i2) == ' ') {
                                    flag = false;
                                    break;
                                }
                                j = itemstack2.getMetadata();
                            }
                            else if (tileentitybannerenumbannerpattern.getCraftingLayers()[l].charAt(i2) != ' ') {
                                flag = false;
                                break;
                            }
                        }
                    }
                    else {
                        flag = false;
                    }
                    if (flag) {
                        return tileentitybannerenumbannerpattern;
                    }
                }
            }
            return null;
        }
    }
    
    static class RecipeDuplicatePattern implements IRecipe
    {
        private RecipeDuplicatePattern() {
        }
        
        @Override
        public boolean matches(final InventoryCrafting inv, final World worldIn) {
            ItemStack itemstack = null;
            ItemStack itemstack2 = null;
            for (int i = 0; i < inv.getSizeInventory(); ++i) {
                final ItemStack itemstack3 = inv.getStackInSlot(i);
                if (itemstack3 != null) {
                    if (itemstack3.getItem() != Items.banner) {
                        return false;
                    }
                    if (itemstack != null && itemstack2 != null) {
                        return false;
                    }
                    final int j = TileEntityBanner.getBaseColor(itemstack3);
                    final boolean flag = TileEntityBanner.getPatterns(itemstack3) > 0;
                    if (itemstack != null) {
                        if (flag) {
                            return false;
                        }
                        if (j != TileEntityBanner.getBaseColor(itemstack)) {
                            return false;
                        }
                        itemstack2 = itemstack3;
                    }
                    else if (itemstack2 != null) {
                        if (!flag) {
                            return false;
                        }
                        if (j != TileEntityBanner.getBaseColor(itemstack2)) {
                            return false;
                        }
                        itemstack = itemstack3;
                    }
                    else if (flag) {
                        itemstack = itemstack3;
                    }
                    else {
                        itemstack2 = itemstack3;
                    }
                }
            }
            return itemstack != null && itemstack2 != null;
        }
        
        @Override
        public ItemStack getCraftingResult(final InventoryCrafting inv) {
            for (int i = 0; i < inv.getSizeInventory(); ++i) {
                final ItemStack itemstack = inv.getStackInSlot(i);
                if (itemstack != null && TileEntityBanner.getPatterns(itemstack) > 0) {
                    final ItemStack itemstack2 = itemstack.copy();
                    itemstack2.stackSize = 1;
                    return itemstack2;
                }
            }
            return null;
        }
        
        @Override
        public int getRecipeSize() {
            return 2;
        }
        
        @Override
        public ItemStack getRecipeOutput() {
            return null;
        }
        
        @Override
        public ItemStack[] getRemainingItems(final InventoryCrafting inv) {
            final ItemStack[] aitemstack = new ItemStack[inv.getSizeInventory()];
            for (int i = 0; i < aitemstack.length; ++i) {
                final ItemStack itemstack = inv.getStackInSlot(i);
                if (itemstack != null) {
                    if (itemstack.getItem().hasContainerItem()) {
                        aitemstack[i] = new ItemStack(itemstack.getItem().getContainerItem());
                    }
                    else if (itemstack.hasTagCompound() && TileEntityBanner.getPatterns(itemstack) > 0) {
                        aitemstack[i] = itemstack.copy();
                        aitemstack[i].stackSize = 1;
                    }
                }
            }
            return aitemstack;
        }
    }
}
