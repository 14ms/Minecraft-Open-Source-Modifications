/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.lwjgl.input.Mouse
 */
package net.minecraft.client.gui.achievement;

import java.io.IOException;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.IProgressMeter;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

public class GuiAchievements
extends GuiScreen
implements IProgressMeter {
    private static final int field_146572_y = AchievementList.minDisplayColumn * 24 - 112;
    private static final int field_146571_z = AchievementList.minDisplayRow * 24 - 112;
    private static final int field_146559_A = AchievementList.maxDisplayColumn * 24 - 77;
    private static final int field_146560_B = AchievementList.maxDisplayRow * 24 - 77;
    private static final ResourceLocation field_146561_C = new ResourceLocation("textures/gui/achievement/achievement_background.png");
    protected GuiScreen parentScreen;
    protected int field_146555_f = 256;
    protected int field_146557_g = 202;
    protected int field_146563_h;
    protected int field_146564_i;
    protected float field_146570_r = 1.0f;
    protected double field_146569_s;
    protected double field_146568_t;
    protected double field_146567_u;
    protected double field_146566_v;
    protected double field_146565_w;
    protected double field_146573_x;
    private int field_146554_D;
    private StatFileWriter statFileWriter;
    private boolean loadingAchievements = true;
    private static final String __OBFID = "CL_00000722";

    public GuiAchievements(GuiScreen p_i45026_1_, StatFileWriter p_i45026_2_) {
        this.parentScreen = p_i45026_1_;
        this.statFileWriter = p_i45026_2_;
        int var3 = 141;
        int var4 = 141;
        this.field_146567_u = this.field_146565_w = (double)(AchievementList.openInventory.displayColumn * 24 - var3 / 2 - 12);
        this.field_146569_s = this.field_146565_w;
        this.field_146566_v = this.field_146573_x = (double)(AchievementList.openInventory.displayRow * 24 - var4 / 2);
        this.field_146568_t = this.field_146573_x;
    }

    @Override
    public void initGui() {
        this.mc.getNetHandler().addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.REQUEST_STATS));
        this.buttonList.clear();
        this.buttonList.add(new GuiOptionButton(1, this.width / 2 + 24, this.height / 2 + 74, 80, 20, I18n.format("gui.done", new Object[0])));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (!this.loadingAchievements && button.id == 1) {
            this.mc.displayGuiScreen(this.parentScreen);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
            this.mc.displayGuiScreen(null);
            this.mc.setIngameFocus();
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (this.loadingAchievements) {
            this.drawDefaultBackground();
            this.drawCenteredString(this.fontRendererObj, I18n.format("multiplayer.downloadingStats", new Object[0]), this.width / 2, this.height / 2, 0xFFFFFF);
            this.drawCenteredString(this.fontRendererObj, lanSearchStates[(int)(Minecraft.getSystemTime() / 150L % (long)lanSearchStates.length)], this.width / 2, this.height / 2 + this.fontRendererObj.FONT_HEIGHT * 2, 0xFFFFFF);
        } else {
            int var4;
            if (Mouse.isButtonDown((int)0)) {
                var4 = (this.width - this.field_146555_f) / 2;
                int var5 = (this.height - this.field_146557_g) / 2;
                int var6 = var4 + 8;
                int var7 = var5 + 17;
                if ((this.field_146554_D == 0 || this.field_146554_D == 1) && mouseX >= var6 && mouseX < var6 + 224 && mouseY >= var7 && mouseY < var7 + 155) {
                    if (this.field_146554_D == 0) {
                        this.field_146554_D = 1;
                    } else {
                        this.field_146567_u -= (double)((float)(mouseX - this.field_146563_h) * this.field_146570_r);
                        this.field_146566_v -= (double)((float)(mouseY - this.field_146564_i) * this.field_146570_r);
                        this.field_146565_w = this.field_146569_s = this.field_146567_u;
                        this.field_146573_x = this.field_146568_t = this.field_146566_v;
                    }
                    this.field_146563_h = mouseX;
                    this.field_146564_i = mouseY;
                }
            } else {
                this.field_146554_D = 0;
            }
            var4 = Mouse.getDWheel();
            float var11 = this.field_146570_r;
            if (var4 < 0) {
                this.field_146570_r += 0.25f;
            } else if (var4 > 0) {
                this.field_146570_r -= 0.25f;
            }
            this.field_146570_r = MathHelper.clamp_float(this.field_146570_r, 1.0f, 2.0f);
            if (this.field_146570_r != var11) {
                float var12 = var11 * (float)this.field_146555_f;
                float var8 = var11 * (float)this.field_146557_g;
                float var9 = this.field_146570_r * (float)this.field_146555_f;
                float var10 = this.field_146570_r * (float)this.field_146557_g;
                this.field_146567_u -= (double)((var9 - var12) * 0.5f);
                this.field_146566_v -= (double)((var10 - var8) * 0.5f);
                this.field_146565_w = this.field_146569_s = this.field_146567_u;
                this.field_146573_x = this.field_146568_t = this.field_146566_v;
            }
            if (this.field_146565_w < (double)field_146572_y) {
                this.field_146565_w = field_146572_y;
            }
            if (this.field_146573_x < (double)field_146571_z) {
                this.field_146573_x = field_146571_z;
            }
            if (this.field_146565_w >= (double)field_146559_A) {
                this.field_146565_w = field_146559_A - 1;
            }
            if (this.field_146573_x >= (double)field_146560_B) {
                this.field_146573_x = field_146560_B - 1;
            }
            this.drawDefaultBackground();
            this.drawAchievementScreen(mouseX, mouseY, partialTicks);
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            this.drawTitle();
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
        }
    }

    @Override
    public void doneLoading() {
        if (this.loadingAchievements) {
            this.loadingAchievements = false;
        }
    }

    @Override
    public void updateScreen() {
        if (!this.loadingAchievements) {
            this.field_146569_s = this.field_146567_u;
            this.field_146568_t = this.field_146566_v;
            double var1 = this.field_146565_w - this.field_146567_u;
            double var3 = this.field_146573_x - this.field_146566_v;
            if (var1 * var1 + var3 * var3 < 4.0) {
                this.field_146567_u += var1;
                this.field_146566_v += var3;
            } else {
                this.field_146567_u += var1 * 0.85;
                this.field_146566_v += var3 * 0.85;
            }
        }
    }

    protected void drawTitle() {
        int var1 = (this.width - this.field_146555_f) / 2;
        int var2 = (this.height - this.field_146557_g) / 2;
        this.fontRendererObj.drawStringNormal(I18n.format("gui.achievements", new Object[0]), var1 + 15, var2 + 5, 0x404040);
    }

    protected void drawAchievementScreen(int p_146552_1_, int p_146552_2_, float p_146552_3_) {
        int var42;
        int var41;
        int var40;
        int var25;
        int var24;
        float var23;
        int var4 = MathHelper.floor_double(this.field_146569_s + (this.field_146567_u - this.field_146569_s) * (double)p_146552_3_);
        int var5 = MathHelper.floor_double(this.field_146568_t + (this.field_146566_v - this.field_146568_t) * (double)p_146552_3_);
        if (var4 < field_146572_y) {
            var4 = field_146572_y;
        }
        if (var5 < field_146571_z) {
            var5 = field_146571_z;
        }
        if (var4 >= field_146559_A) {
            var4 = field_146559_A - 1;
        }
        if (var5 >= field_146560_B) {
            var5 = field_146560_B - 1;
        }
        int var6 = (this.width - this.field_146555_f) / 2;
        int var7 = (this.height - this.field_146557_g) / 2;
        int var8 = var6 + 16;
        int var9 = var7 + 17;
        this.zLevel = 0.0f;
        GlStateManager.depthFunc(518);
        GlStateManager.pushMatrix();
        GlStateManager.translate(var8, var9, -200.0f);
        GlStateManager.scale(1.0f / this.field_146570_r, 1.0f / this.field_146570_r, 0.0f);
        GlStateManager.enableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableColorMaterial();
        int var10 = var4 + 288 >> 4;
        int var11 = var5 + 288 >> 4;
        int var12 = (var4 + 288) % 16;
        int var13 = (var5 + 288) % 16;
        Random var19 = new Random();
        float var20 = 16.0f / this.field_146570_r;
        float var21 = 16.0f / this.field_146570_r;
        int var22 = 0;
        while ((float)var22 * var20 - (float)var13 < 155.0f) {
            var23 = 0.6f - (float)(var11 + var22) / 25.0f * 0.3f;
            GlStateManager.color(var23, var23, var23, 1.0f);
            var24 = 0;
            while ((float)var24 * var21 - (float)var12 < 224.0f) {
                var19.setSeed(this.mc.getSession().getPlayerID().hashCode() + var10 + var24 + (var11 + var22) * 16);
                var25 = var19.nextInt(1 + var11 + var22) + (var11 + var22) / 2;
                TextureAtlasSprite var26 = this.func_175371_a(Blocks.sand);
                if (var25 <= 37 && var11 + var22 != 35) {
                    if (var25 == 22) {
                        var26 = var19.nextInt(2) == 0 ? this.func_175371_a(Blocks.diamond_ore) : this.func_175371_a(Blocks.redstone_ore);
                    } else if (var25 == 10) {
                        var26 = this.func_175371_a(Blocks.iron_ore);
                    } else if (var25 == 8) {
                        var26 = this.func_175371_a(Blocks.coal_ore);
                    } else if (var25 > 4) {
                        var26 = this.func_175371_a(Blocks.stone);
                    } else if (var25 > 0) {
                        var26 = this.func_175371_a(Blocks.dirt);
                    }
                } else {
                    Block var27 = Blocks.bedrock;
                    var26 = this.func_175371_a(var27);
                }
                this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
                this.func_175175_a(var24 * 16 - var12, var22 * 16 - var13, var26, 16, 16);
                ++var24;
            }
            ++var22;
        }
        GlStateManager.enableDepth();
        GlStateManager.depthFunc(515);
        this.mc.getTextureManager().bindTexture(field_146561_C);
        for (var22 = 0; var22 < AchievementList.achievementList.size(); ++var22) {
            Achievement var34 = (Achievement)AchievementList.achievementList.get(var22);
            if (var34.parentAchievement == null) continue;
            var24 = var34.displayColumn * 24 - var4 + 11;
            var25 = var34.displayRow * 24 - var5 + 11;
            int var37 = var34.parentAchievement.displayColumn * 24 - var4 + 11;
            var40 = var34.parentAchievement.displayRow * 24 - var5 + 11;
            boolean var28 = this.statFileWriter.hasAchievementUnlocked(var34);
            boolean var29 = this.statFileWriter.canUnlockAchievement(var34);
            int var30 = this.statFileWriter.func_150874_c(var34);
            if (var30 > 4) continue;
            int var31 = -16777216;
            if (var28) {
                var31 = -6250336;
            } else if (var29) {
                var31 = -16711936;
            }
            this.drawHorizontalLine(var24, var37, var25, var31);
            this.drawVerticalLine(var37, var25, var40, var31);
            if (var24 > var37) {
                this.drawTexturedModalRect(var24 - 11 - 7, var25 - 5, 114, 234, 7, 11);
                continue;
            }
            if (var24 < var37) {
                this.drawTexturedModalRect(var24 + 11, var25 - 5, 107, 234, 7, 11);
                continue;
            }
            if (var25 > var40) {
                this.drawTexturedModalRect(var24 - 5, var25 - 11 - 7, 96, 234, 11, 7);
                continue;
            }
            if (var25 >= var40) continue;
            this.drawTexturedModalRect(var24 - 5, var25 + 11, 96, 241, 11, 7);
        }
        Achievement var33 = null;
        var23 = (float)(p_146552_1_ - var8) * this.field_146570_r;
        float var35 = (float)(p_146552_2_ - var9) * this.field_146570_r;
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableColorMaterial();
        for (var25 = 0; var25 < AchievementList.achievementList.size(); ++var25) {
            float var43;
            Achievement var38 = (Achievement)AchievementList.achievementList.get(var25);
            var40 = var38.displayColumn * 24 - var4;
            var41 = var38.displayRow * 24 - var5;
            if (var40 < -24 || var41 < -24 || !((float)var40 <= 224.0f * this.field_146570_r) || !((float)var41 <= 155.0f * this.field_146570_r)) continue;
            var42 = this.statFileWriter.func_150874_c(var38);
            if (this.statFileWriter.hasAchievementUnlocked(var38)) {
                var43 = 0.75f;
                GlStateManager.color(var43, var43, var43, 1.0f);
            } else if (this.statFileWriter.canUnlockAchievement(var38)) {
                var43 = 1.0f;
                GlStateManager.color(var43, var43, var43, 1.0f);
            } else if (var42 < 3) {
                var43 = 0.3f;
                GlStateManager.color(var43, var43, var43, 1.0f);
            } else if (var42 == 3) {
                var43 = 0.2f;
                GlStateManager.color(var43, var43, var43, 1.0f);
            } else {
                if (var42 != 4) continue;
                var43 = 0.1f;
                GlStateManager.color(var43, var43, var43, 1.0f);
            }
            this.mc.getTextureManager().bindTexture(field_146561_C);
            if (var38.getSpecial()) {
                this.drawTexturedModalRect(var40 - 2, var41 - 2, 26, 202, 26, 26);
            } else {
                this.drawTexturedModalRect(var40 - 2, var41 - 2, 0, 202, 26, 26);
            }
            if (!this.statFileWriter.canUnlockAchievement(var38)) {
                var43 = 0.1f;
                GlStateManager.color(var43, var43, var43, 1.0f);
                this.itemRender.func_175039_a(false);
            }
            GlStateManager.enableLighting();
            GlStateManager.enableCull();
            this.itemRender.renderItemOnScreen(var38.theItemStack, var40 + 3, var41 + 3);
            GlStateManager.blendFunc(770, 771);
            GlStateManager.disableLighting();
            if (!this.statFileWriter.canUnlockAchievement(var38)) {
                this.itemRender.func_175039_a(true);
            }
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            if (!(var23 >= (float)var40) || !(var23 <= (float)(var40 + 22)) || !(var35 >= (float)var41) || !(var35 <= (float)(var41 + 22))) continue;
            var33 = var38;
        }
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.popMatrix();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.getTextureManager().bindTexture(field_146561_C);
        this.drawTexturedModalRect(var6, var7, 0, 0, this.field_146555_f, this.field_146557_g);
        this.zLevel = 0.0f;
        GlStateManager.depthFunc(515);
        GlStateManager.disableDepth();
        GlStateManager.enableTexture2D();
        super.drawScreen(p_146552_1_, p_146552_2_, p_146552_3_);
        if (var33 != null) {
            String var36 = var33.getStatName().getUnformattedText();
            String var39 = var33.getDescription();
            var40 = p_146552_1_ + 12;
            var41 = p_146552_2_ - 4;
            var42 = this.statFileWriter.func_150874_c(var33);
            if (this.statFileWriter.canUnlockAchievement(var33)) {
                int var30 = Math.max(this.fontRendererObj.getStringWidth(var36), 120);
                int var31 = this.fontRendererObj.splitStringWidth(var39, var30);
                if (this.statFileWriter.hasAchievementUnlocked(var33)) {
                    var31 += 12;
                }
                this.drawGradientRect(var40 - 3, var41 - 3, var40 + var30 + 3, var41 + var31 + 3 + 12, -1073741824, -1073741824);
                this.fontRendererObj.drawSplitString(var39, var40, var41 + 12, var30, -6250336);
                if (this.statFileWriter.hasAchievementUnlocked(var33)) {
                    this.fontRendererObj.drawStringWithShadow(I18n.format("achievement.taken", new Object[0]), var40, var41 + var31 + 4, -7302913);
                }
            } else if (var42 == 3) {
                var36 = I18n.format("achievement.unknown", new Object[0]);
                int var30 = Math.max(this.fontRendererObj.getStringWidth(var36), 120);
                String var44 = new ChatComponentTranslation("achievement.requires", var33.parentAchievement.getStatName()).getUnformattedText();
                int var32 = this.fontRendererObj.splitStringWidth(var44, var30);
                this.drawGradientRect(var40 - 3, var41 - 3, var40 + var30 + 3, var41 + var32 + 12 + 3, -1073741824, -1073741824);
                this.fontRendererObj.drawSplitString(var44, var40, var41 + 12, var30, -9416624);
            } else if (var42 < 3) {
                int var30 = Math.max(this.fontRendererObj.getStringWidth(var36), 120);
                String var44 = new ChatComponentTranslation("achievement.requires", var33.parentAchievement.getStatName()).getUnformattedText();
                int var32 = this.fontRendererObj.splitStringWidth(var44, var30);
                this.drawGradientRect(var40 - 3, var41 - 3, var40 + var30 + 3, var41 + var32 + 12 + 3, -1073741824, -1073741824);
                this.fontRendererObj.drawSplitString(var44, var40, var41 + 12, var30, -9416624);
            } else {
                var36 = null;
            }
            if (var36 != null) {
                this.fontRendererObj.drawStringWithShadow(var36, var40, var41, this.statFileWriter.canUnlockAchievement(var33) ? (var33.getSpecial() ? -128 : -1) : (var33.getSpecial() ? -8355776 : -8355712));
            }
        }
        GlStateManager.enableDepth();
        GlStateManager.enableLighting();
        RenderHelper.disableStandardItemLighting();
    }

    private TextureAtlasSprite func_175371_a(Block p_175371_1_) {
        return Minecraft.getMinecraft().getBlockRendererDispatcher().func_175023_a().func_178122_a(p_175371_1_.getDefaultState());
    }

    @Override
    public boolean doesGuiPauseGame() {
        return !this.loadingAchievements;
    }
}

