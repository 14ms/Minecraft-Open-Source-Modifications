/*
 * Decompiled with CFR 0.150.
 */
package net.minecraft.entity.monster;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntitySnowman
extends EntityGolem
implements IRangedAttackMob {
    private static final String __OBFID = "CL_00001650";

    public EntitySnowman(World worldIn) {
        super(worldIn);
        this.setSize(0.7f, 1.9f);
        ((PathNavigateGround)this.getNavigator()).func_179690_a(true);
        this.tasks.addTask(1, new EntityAIArrowAttack(this, 1.25, 20, 10.0f));
        this.tasks.addTask(2, new EntityAIWander(this, 1.0));
        this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0f));
        this.tasks.addTask(4, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityLiving.class, 10, true, false, IMob.mobSelector));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(4.0);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.2f);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (!this.worldObj.isRemote) {
            int var1 = MathHelper.floor_double(this.posX);
            int var2 = MathHelper.floor_double(this.posY);
            int var3 = MathHelper.floor_double(this.posZ);
            if (this.isWet()) {
                this.attackEntityFrom(DamageSource.drown, 1.0f);
            }
            BlockPos blockPos = new BlockPos(var1, 0, var3);
            BlockPos blockPos2 = new BlockPos(var1, var2, var3);
            if (this.worldObj.getBiomeGenForCoords(blockPos).func_180626_a(blockPos2) > 1.0f) {
                this.attackEntityFrom(DamageSource.onFire, 1.0f);
            }
            for (int var4 = 0; var4 < 4; ++var4) {
                var1 = MathHelper.floor_double(this.posX + (double)((float)(var4 % 2 * 2 - 1) * 0.25f));
                if (this.worldObj.getBlockState(new BlockPos(var1, var2 = MathHelper.floor_double(this.posY), var3 = MathHelper.floor_double(this.posZ + (double)((float)(var4 / 2 % 2 * 2 - 1) * 0.25f)))).getBlock().getMaterial() != Material.air) continue;
                BlockPos blockPos3 = new BlockPos(var1, 0, var3);
                BlockPos blockPos4 = new BlockPos(var1, var2, var3);
                if (!(this.worldObj.getBiomeGenForCoords(blockPos3).func_180626_a(blockPos4) < 0.8f) || !Blocks.snow_layer.canPlaceBlockAt(this.worldObj, new BlockPos(var1, var2, var3))) continue;
                this.worldObj.setBlockState(new BlockPos(var1, var2, var3), Blocks.snow_layer.getDefaultState());
            }
        }
    }

    @Override
    protected Item getDropItem() {
        return Items.snowball;
    }

    @Override
    protected void dropFewItems(boolean p_70628_1_, int p_70628_2_) {
        int var3 = this.rand.nextInt(16);
        for (int var4 = 0; var4 < var3; ++var4) {
            this.dropItem(Items.snowball, 1);
        }
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase p_82196_1_, float p_82196_2_) {
        EntitySnowball var3 = new EntitySnowball(this.worldObj, this);
        double var4 = p_82196_1_.posY + (double)p_82196_1_.getEyeHeight() - (double)1.1f;
        double var6 = p_82196_1_.posX - this.posX;
        double var8 = var4 - var3.posY;
        double var10 = p_82196_1_.posZ - this.posZ;
        float var12 = MathHelper.sqrt_double(var6 * var6 + var10 * var10) * 0.2f;
        var3.setThrowableHeading(var6, var8 + (double)var12, var10, 1.6f, 12.0f);
        this.playSound("random.bow", 1.0f, 1.0f / (this.getRNG().nextFloat() * 0.4f + 0.8f));
        this.worldObj.spawnEntityInWorld(var3);
    }

    @Override
    public float getEyeHeight() {
        return 1.7f;
    }
}

