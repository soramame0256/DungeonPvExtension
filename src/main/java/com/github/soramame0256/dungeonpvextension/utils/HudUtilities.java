package com.github.soramame0256.dungeonpvextension.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class HudUtilities {
    private static final Minecraft mc = Minecraft.getMinecraft();
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
        if (Loader.instance().getMCVersionString().equals("Minecraft 1.12.2")){
            return ObfuscationReflectionHelper.getPrivateValue(GuiIngame.class, Minecraft.getMinecraft().ingameGUI, "field_73838_g");
        }else{
            return ObfuscationReflectionHelper.getPrivateValue(GuiIngame.class, Minecraft.getMinecraft().ingameGUI, "field_73838_g","");
        }
    }
    //https://forums.minecraftforge.net/topic/40032-how-to-get-the-title-and-sub-title-that-is-currently-on-screen/
    public static String getCurrentTitle(){
        if (Loader.instance().getMCVersionString().equals("Minecraft 1.12.2")){
            return ObfuscationReflectionHelper.getPrivateValue(GuiIngame.class, Minecraft.getMinecraft().ingameGUI, "field_175201_x");
        }else {
            return ObfuscationReflectionHelper.getPrivateValue(GuiIngame.class, Minecraft.getMinecraft().ingameGUI, "field_175201_x", "");
        }
    }
    public static String getCurrentSubTitle(){
        if (Loader.instance().getMCVersionString().equals("Minecraft 1.12.2")){
            return ObfuscationReflectionHelper.getPrivateValue(GuiIngame.class, Minecraft.getMinecraft().ingameGUI, "field_175200_y");
        }else{
            return ObfuscationReflectionHelper.getPrivateValue(GuiIngame.class, Minecraft.getMinecraft().ingameGUI, "field_175201_x","");
        }
    }
    public static String getCurrentGuiTitle(){
        Container container = mc.player.openContainer;
        if(container instanceof ContainerChest){
            IInventory iInventory = ((ContainerChest) container).getLowerChestInventory();
            return iInventory.getDisplayName().getUnformattedComponentText();
        }
        return "";
    }
}
