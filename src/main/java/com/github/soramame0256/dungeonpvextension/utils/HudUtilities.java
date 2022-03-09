package com.github.soramame0256.dungeonpvextension.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class HudUtilities {

    public static Integer getHealth() {
        String s = getActionbar();
        if (!getActionbar().contains(" ")){
            return 0;
        }
        return Integer.parseInt(s.split(" ")[1].split("/")[0]);
    }

    public static Integer getMaxHealth() {
        String s = getActionbar();
        if (!getActionbar().contains(" ")){
            return 0;
        }
        return Integer.parseInt(s.split(" ")[1].split("/")[1]);
    }

    public static Integer getCurrentCharacter() {
        String s = getActionbar();
        if (!s.contains(" ")){
            return 0;
        }
        return Integer.parseInt(s.split(" ")[3]);
    }
    public static String getActionbar() {
        try {
            return (String) ReflectionHelper.findField(GuiIngame.class, "displayedActionBar", "field_73838_g").get(Minecraft.getMinecraft().ingameGUI);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return "";
    }
    //https://forums.minecraftforge.net/topic/40032-how-to-get-the-title-and-sub-title-that-is-currently-on-screen/
    public static String getCurrentTitle(){
        try {
            return (String) ReflectionHelper.findField(GuiIngame.class, "displayedTitle", "field_175201_x").get(Minecraft.getMinecraft().ingameGUI);
        } catch (IllegalAccessException ignored) {}
        return "";
    }
    public static String getCurrentSubTitle(){
        try {
            return (String) ReflectionHelper.findField(GuiIngame.class, "displayedSubTitle", "field_175200_y").get(Minecraft.getMinecraft().ingameGUI);
        } catch (IllegalAccessException ignored) {}
        return "";
    }
}
