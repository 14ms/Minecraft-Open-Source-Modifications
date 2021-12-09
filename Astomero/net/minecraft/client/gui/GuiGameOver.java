package net.minecraft.client.gui;

import net.minecraft.client.resources.*;
import java.util.*;
import java.io.*;
import com.initial.ui.*;
import net.minecraft.client.multiplayer.*;
import net.minecraft.client.renderer.*;
import net.minecraft.util.*;

public class GuiGameOver extends GuiScreen implements GuiYesNoCallback
{
    private int enableButtonsTimer;
    private boolean field_146346_f;
    
    public GuiGameOver() {
        this.field_146346_f = false;
    }
    
    @Override
    public void initGui() {
        this.buttonList.clear();
        if (this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled()) {
            if (this.mc.isIntegratedServerRunning()) {
                this.buttonList.add(new GuiButton(1, GuiGameOver.width / 2 - 100, GuiGameOver.height / 4 + 96, I18n.format("deathScreen.deleteWorld", new Object[0])));
            }
            else {
                this.buttonList.add(new GuiButton(1, GuiGameOver.width / 2 - 100, GuiGameOver.height / 4 + 96, I18n.format("deathScreen.leaveServer", new Object[0])));
            }
        }
        else {
            this.buttonList.add(new GuiButton(0, GuiGameOver.width / 2 - 100, GuiGameOver.height / 4 + 72, I18n.format("deathScreen.respawn", new Object[0])));
            this.buttonList.add(new GuiButton(1, GuiGameOver.width / 2 - 100, GuiGameOver.height / 4 + 96, I18n.format("deathScreen.titleScreen", new Object[0])));
            if (this.mc.getSession() == null) {
                this.buttonList.get(1).enabled = false;
            }
        }
        for (final GuiButton guibutton : this.buttonList) {
            guibutton.enabled = false;
        }
    }
    
    @Override
    protected void keyTyped(final char typedChar, final int keyCode) throws IOException {
    }
    
    @Override
    protected void actionPerformed(final GuiButton button) throws IOException {
        switch (button.id) {
            case 0: {
                this.mc.thePlayer.respawnPlayer();
                this.mc.displayGuiScreen(null);
                break;
            }
            case 1: {
                if (this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled()) {
                    this.mc.displayGuiScreen(new GuiCustomMainMenu());
                    break;
                }
                final GuiYesNo guiyesno = new GuiYesNo(this, I18n.format("deathScreen.quit.confirm", new Object[0]), "", I18n.format("deathScreen.titleScreen", new Object[0]), I18n.format("deathScreen.respawn", new Object[0]), 0);
                this.mc.displayGuiScreen(guiyesno);
                guiyesno.setButtonDelay(20);
                break;
            }
        }
    }
    
    @Override
    public void confirmClicked(final boolean result, final int id) {
        if (result) {
            this.mc.theWorld.sendQuittingDisconnectingPacket();
            this.mc.loadWorld(null);
            this.mc.displayGuiScreen(new GuiCustomMainMenu());
        }
        else {
            this.mc.thePlayer.respawnPlayer();
            this.mc.displayGuiScreen(null);
        }
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        Gui.drawGradientRect(0.0, 0.0, GuiGameOver.width, GuiGameOver.height, 1615855616, -1602211792);
        GlStateManager.pushMatrix();
        GlStateManager.scale(2.0f, 2.0f, 2.0f);
        final boolean flag = this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled();
        final String s = flag ? I18n.format("deathScreen.title.hardcore", new Object[0]) : I18n.format("deathScreen.title", new Object[0]);
        this.drawCenteredString(this.fontRendererObj, s, GuiGameOver.width / 2 / 2, 30, 16777215);
        GlStateManager.popMatrix();
        if (flag) {
            this.drawCenteredString(this.fontRendererObj, I18n.format("deathScreen.hardcoreInfo", new Object[0]), GuiGameOver.width / 2, 144, 16777215);
        }
        this.drawCenteredString(this.fontRendererObj, I18n.format("deathScreen.score", new Object[0]) + ": " + EnumChatFormatting.YELLOW + this.mc.thePlayer.getScore(), GuiGameOver.width / 2, 100, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
    
    @Override
    public void updateScreen() {
        super.updateScreen();
        ++this.enableButtonsTimer;
        if (this.enableButtonsTimer == 20) {
            for (final GuiButton guibutton : this.buttonList) {
                guibutton.enabled = true;
            }
        }
    }
}
