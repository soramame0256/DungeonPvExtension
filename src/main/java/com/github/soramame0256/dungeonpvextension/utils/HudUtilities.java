package com.github.soramame0256.dungeonpvextension.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.network.NetworkPlayerInfo;
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
            String actionBar = (String) ReflectionHelper.findField(GuiIngame.class, "displayedActionBar", "field_73838_g").get(Minecraft.getMinecraft().ingameGUI);
            return actionBar;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return "";
    }
}
