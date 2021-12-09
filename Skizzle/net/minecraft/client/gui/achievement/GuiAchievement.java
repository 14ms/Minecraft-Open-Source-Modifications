/*
 * Decompiled with CFR 0.150.
 */
package net.minecraft.client.gui.achievement;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.stats.Achievement;
import net.minecraft.util.ResourceLocation;

public class GuiAchievement
extends Gui {
    private static final ResourceLocation achievementBg = new ResourceLocation("textures/gui/achievement/achievement_background.png");
    private Minecraft mc;
    private int width;
    private int height;
    private String achievementTitle;
    private String achievementDescription;
    private Achievement theAchievement;
    private long notificationTime;
    private RenderItem renderItem;
    private boolean permanentNotification;
    private static final String __OBFID = "CL_00000721";

    public GuiAchievement(Minecraft mc) {
        this.mc = mc;
        this.renderItem = mc.getRenderItem();
    }

    public void displayAchievement(Achievement p_146256_1_) {
        this.achievementTitle = I18n.format("achievement.get", new Object[0]);
        this.achievementDescription = p_146256_1_.getStatName().getUnformattedText();
        this.notificationTime = Minecraft.getSystemTime();
        this.theAchievement = p_146256_1_;
        this.permanentNotification = false;
    }

    public void displayUnformattedAchievement(Achievement p_146255_1_) {
        this.achievementTitle = p_146255_1_.getStatName().getUnformattedText();
        this.achievementDescription = p_146255_1_.getDescription();
        this.notificationTime = Minecraft.getSystemTime() + 2500L;
        this.theAchievement = p_146255_1_;
        this.permanentNotification = true;
    }

    private void updateAchievementWindowScale() {
        GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        this.width = this.mc.displayWidth;
        this.height = this.mc.displayHeight;
        ScaledResolution var1 = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
        this.width = var1.getScaledWidth();
        this.height = var1.getScaledHeight();
        GlStateManager.clear(256);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0, this.width, this.height, 0.0, 1000.0, 3000.0);
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        GlStateManager.translate(0.0f, 0.0f, -2000.0f);
    }

    public void updateAchievementWindow() {
        if (this.theAchievement != null && this.notificationTime != 0L && Minecraft.getMinecraft().thePlayer != null) {
            double var1 = (double)(Minecraft.getSystemTime() - this.notificationTime) / 3000.0;
            if (!this.permanentNotification) {
                if (var1 < 0.0 || var1 > 1.0) {
                    this.notificationTime = 0L;
                    return;
                }
            } else if (var1 > 0.5) {
                var1 = 0.5;
            }
            this.updateAchievementWindowScale();
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
            double var3 = var1 * 2.0;
            if (var3 > 1.0) {
                var3 = 2.0 - var3;
            }
            var3 *= 4.0;
            if ((var3 = 1.0 - var3) < 0.0) {
                var3 = 0.0;
            }
            var3 *= var3;
            var3 *= var3;
            int var5 = this.width - 160;
            int var6 = 0 - (int)(var3 * 36.0);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.enableTexture2D();
            this.mc.getTextureManager().bindTexture(achievementBg);
            GlStateManager.disableLighting();
            this.drawTexturedModalRect(var5, var6, 96, 202, 160, 32);
            if (this.permanentNotification) {
                this.mc.fontRendererObj.drawSplitString(this.achievementDescription, var5 + 30, var6 + 7, 120, -1);
            } else {
                this.mc.fontRendererObj.drawStringNormal(this.achievementTitle, var5 + 30, var6 + 7, -256);
                this.mc.fontRendererObj.drawStringNormal(this.achievementDescription, var5 + 30, var6 + 18, -1);
            }
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableColorMaterial();
            GlStateManager.enableLighting();
            this.renderItem.renderItemOnScreen(this.theAchievement.theItemStack, var5 + 8, var6 + 8);
            GlStateManager.disableLighting();
            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
        }
    }

    public void clearAchievements() {
        this.theAchievement = null;
        this.notificationTime = 0L;
    }
}

