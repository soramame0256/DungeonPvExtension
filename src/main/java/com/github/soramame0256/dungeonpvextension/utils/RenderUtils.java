package com.github.soramame0256.dungeonpvextension.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

public class RenderUtils {
    private static final String FORMATTING_CODE_REGEX = "§[0-9a-fk-or]";
    private static final FontRenderer fRender = Minecraft.getMinecraft().fontRenderer;
    private static final ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());

    public static void drawStringWithFullShadow(String text, float x, float y, int color) {
        String uncoloured = text.replaceAll(FORMATTING_CODE_REGEX,"");
        fRender.drawString(uncoloured, x + 1, y, 0, false);
        fRender.drawString(uncoloured, x - 1, y, 0, false);
        fRender.drawString(uncoloured, x, y + 1, 0, false);
        fRender.drawString(uncoloured, x, y - 1, 0, false);
        fRender.drawString(text, x, y, color, false);
    }

    public static void drawStringWithFullShadow(String text, float x, float y, int color, int alpha) {
        String uncoloured = text.replaceAll(FORMATTING_CODE_REGEX,"");
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        fRender.drawString(uncoloured, x + 1, y, alpha << 24, false);
        fRender.drawString(uncoloured, x - 1, y, alpha << 24, false);
        fRender.drawString(uncoloured, x, y + 1, alpha << 24, false);
        fRender.drawString(uncoloured, x, y - 1, alpha << 24, false);
        fRender.drawString(text, x, y, color | alpha<<24, false);
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
    }

    public static void drawStringWithShadow(String text, float x, float y, int color) {
        fRender.drawString(text, x, y, color, true);
    }
    public static void drawString(String text, float x, float y, int color, boolean dropShadow) {
        fRender.drawString(text, x, y, color, dropShadow);
    }

    public static void drawString(String text, float x, float y, int color) {
        fRender.drawString(text, x, y, color, false);
    }
}