package com.github.soramame0256.dungeonpvextension.utils;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.github.soramame0256.dungeonpvextension.utils.StringUtilities.clearColor;

public class ItemUtilities {
    public static void changeName(ItemStack i, String s){
        NBTTagCompound NBT1 = i.getTagCompound();
        NBTTagCompound NBT2 = i.getSubCompound("display");
        if(NBT1 != null && NBT2 != null) {
            NBT2.setTag("Name", new NBTTagString(s));
            NBT1.setTag("display", NBT2);
            setTempModded(i);
            i.setTagCompound(NBT1);
        }
    }
    public static void changeLore(ItemStack i, List<String> lore){
        NBTTagCompound NBT1 = i.getTagCompound();
        NBTTagCompound NBT2 = i.getSubCompound("display");
        NBTTagList tag = new NBTTagList();
        lore.forEach(s -> tag.appendTag(new NBTTagString(s)));
        if(NBT1 != null && NBT2 != null) {
            NBT2.setTag("Lore", tag);
            NBT1.setTag("display", NBT2);
            setTempModded(i);
            i.setTagCompound(NBT1);
        }
    }
    public static String[] getNonModdedLore(ItemStack i){
        List<String> lore = new ArrayList<>();
        if (i.getTagCompound() != null && i.getTagCompound().hasKey("dpeExtraAttributes") && i.getSubCompound("dpeExtraAttributes").hasKey("lore")){
            NBTTagCompound dpeExtraAttributes = i.getSubCompound("dpeExtraAttributes");
            for (NBTBase a : dpeExtraAttributes.getTagList("lore", 8)) {
                lore.add(a.toString().substring(1, a.toString().length() - 1));
            }
            return lore.toArray(new String[0]);
        }else{
            return getLore(i);
        }
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
    public static boolean isScrap(List<String> lore){
        AtomicReference<Boolean> is = new AtomicReference<>();
        is.set(false);
        lore.forEach(str ->{
            if(str.contains("解体アイテム")){
                is.set(true);
            }
        });
        return is.get();
    }
    @SuppressWarnings(value = "all")
    public static boolean isModded(ItemStack is){
        if (is.getTagCompound() != null && !is.getTagCompound().hasKey("dpeModded")){
            return false;
        }
        return is.getTagCompound().getBoolean("dpeModded");
    }
    @SuppressWarnings(value = "all")
    public static boolean isTempModded(ItemStack is){
        if (is.getTagCompound() != null && !is.getTagCompound().hasKey("dpeTempModded")){
            return false;
        } else return is.getTagCompound().getLong("dpeTempModded") + 3 > Instant.now().getEpochSecond();
    }
    public static void setTempModded(ItemStack is){
        if(is.getTagCompound() != null) {
            is.getTagCompound().setLong("dpeTempModded", Instant.now().getEpochSecond());
        }
    }
    public static String[] getLore(ItemStack is){
        List<String> lore = new ArrayList<>();
        NBTTagCompound displayNBT = is.getSubCompound("display");
        NBTTagCompound NBT1 = is.getTagCompound();
        NBTTagCompound NBT2 = new NBTTagCompound();
        NBTTagList tag = new NBTTagList();
        if(displayNBT != null && displayNBT.hasKey("Lore")) {
            for (NBTBase a : displayNBT.getTagList("Lore", 8)) {
                lore.add(a.toString().substring(1, a.toString().length() - 1));
            }
        }else{
            lore.add("");
            return lore.toArray(new String[0]);
        }
        lore.forEach(s -> tag.appendTag(new NBTTagString(s)));
        if(NBT1 != null) {
            NBT2.setTag("lore", tag);
            NBT1.setTag("dpeExtraAttributes", NBT2);
            setTempModded(is);
            is.setTagCompound(NBT1);
        }
        return lore.toArray(new String[0]);
    }
    public static Integer getItemLevel(ItemStack is){
        String str = "0";
        for (String s : getLore(is)) {
            if (clearColor(s).contains("❃ 強化レベル:")){
                str = clearColor(s).split(" ")[3];
            }
        }
        return Integer.parseInt(str);
    }
    public static Integer getItemLevelMax(ItemStack is){
        String str = "0";
        for (String s : getLore(is)) {
            if (clearColor(s).contains("❃ 強化レベル:")){
                str = clearColor(s).split(" ")[5];
            }
        }
        return Integer.parseInt(str);
    }
}
