package com.github.soramame0256.dungeonpvextension.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ItemUtilities {
    public static void changeName(ItemStack i, String s){
        NBTTagCompound NBT1 = i.getTagCompound();
        NBTTagCompound NBT2 = i.getSubCompound("display");
        NBT2.setTag("Name", new NBTTagString(s));
        NBT1.setTag("display", NBT2);
        i.setTagCompound(NBT1);
    }
    public static void changeLore(ItemStack i, List<String> lore){
        NBTTagCompound NBT1 = i.getTagCompound();
        NBTTagCompound NBT2 = i.getSubCompound("display");
        NBTTagList tag = new NBTTagList();
        lore.forEach(s -> tag.appendTag(new NBTTagString(s)));
        try {
            NBT2.setTag("Lore", tag);
            NBT1.setTag("display", NBT2);
        }catch(NullPointerException e){
            return;
        }
        NBT1.setBoolean("dpeModded", true);
        i.setTagCompound(NBT1);
    }
    public static boolean isWeapon(List<String> lore){
        AtomicReference<Boolean> is = new AtomicReference<>();
        is.set(false);
        lore.forEach(str ->{
            if (str.contains("武器アイテム")){
                is.set(true);
            }
        });
        return is.get();
    }
    public static boolean isArmor(List<String> lore){
        AtomicReference<Boolean> is = new AtomicReference<>();
        is.set(false);
        lore.forEach(str ->{
            if (str.contains("防具アイテム")){
                is.set(true);
            }
        });
        return is.get();
    }
    public static boolean isModded(ItemStack is){
        if (is.getTagCompound() != null && !is.getTagCompound().hasKey("dpeModded")){
            return false;
        }
        return is.getTagCompound().getBoolean("dpeModded");
    }
}
