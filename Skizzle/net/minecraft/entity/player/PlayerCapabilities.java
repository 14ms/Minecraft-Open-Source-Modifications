/*
 * Decompiled with CFR 0.150.
 */
package net.minecraft.entity.player;

import net.minecraft.nbt.NBTTagCompound;

public class PlayerCapabilities {
    public boolean disableDamage;
    public boolean isFlying;
    public boolean allowFlying;
    public boolean isCreativeMode;
    public boolean allowEdit = true;
    private float flySpeed = 0.05f;
    private float walkSpeed = 0.1f;
    private static final String __OBFID = "CL_00001708";

    public void writeCapabilitiesToNBT(NBTTagCompound p_75091_1_) {
        NBTTagCompound var2 = new NBTTagCompound();
        var2.setBoolean("invulnerable", this.disableDamage);
        var2.setBoolean("flying", this.isFlying);
        var2.setBoolean("mayfly", this.allowFlying);
        var2.setBoolean("instabuild", this.isCreativeMode);
        var2.setBoolean("mayBuild", this.allowEdit);
        var2.setFloat("flySpeed", this.flySpeed);
        var2.setFloat("walkSpeed", this.walkSpeed);
        p_75091_1_.setTag("abilities", var2);
    }

    public void readCapabilitiesFromNBT(NBTTagCompound p_75095_1_) {
        if (p_75095_1_.hasKey("abilities", 10)) {
            NBTTagCompound var2 = p_75095_1_.getCompoundTag("abilities");
            this.disableDamage = var2.getBoolean("invulnerable");
            this.isFlying = var2.getBoolean("flying");
            this.allowFlying = var2.getBoolean("mayfly");
            this.isCreativeMode = var2.getBoolean("instabuild");
            if (var2.hasKey("flySpeed", 99)) {
                this.flySpeed = var2.getFloat("flySpeed");
                this.walkSpeed = var2.getFloat("walkSpeed");
            }
            if (var2.hasKey("mayBuild", 1)) {
                this.allowEdit = var2.getBoolean("mayBuild");
            }
        }
    }

    public float getFlySpeed() {
        return this.flySpeed;
    }

    public void setFlySpeed(float p_75092_1_) {
        this.flySpeed = p_75092_1_;
    }

    public float getWalkSpeed() {
        return this.walkSpeed;
    }

    public void setPlayerWalkSpeed(float p_82877_1_) {
        this.walkSpeed = p_82877_1_;
    }
}

