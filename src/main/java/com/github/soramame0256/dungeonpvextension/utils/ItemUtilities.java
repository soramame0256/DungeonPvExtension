package com.github.soramame0256.dungeonpvextension.utils;

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
            i.setTagCompound(NBT1);
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
    public static boolean isLocked(ItemStack i){
        if(i.getTagCompound() != null && i.getTagCompound().hasKey("itemlock")){
            return i.getTagCompound().getInteger("itemlock") == 1;
        } else{
            return false;
        }
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
    public static boolean isPickaxe(List<String> lore){
        AtomicReference<Boolean> is = new AtomicReference<>();
        is.set(false);
        lore.forEach(str ->{
            if(str.contains("採掘アイテム")){
                is.set(true);
            }
        });
        return is.get();
    }
    public static boolean isOre(List<String> lore){
        AtomicReference<Boolean> is = new AtomicReference<>();
        is.set(false);
        lore.forEach(str ->{
            if(str.contains("未精錬アイテム")){
                is.set(true);
            }
        });
        return is.get();
    }
    public static boolean isGem(List<String> lore){
        AtomicReference<Boolean> is = new AtomicReference<>();
        is.set(false);
        lore.forEach(str ->{
            if(str.contains("精錬アイテム")){
                is.set(true);
            }
        });
        return is.get();
    }
    public static String[] getLore(ItemStack is){
        List<String> lore = new ArrayList<>();
        NBTTagCompound displayNBT = is.getSubCompound("display");
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
        return lore.toArray(new String[0]);
    }
    public static Integer getWeaponLevel(ItemStack is){
        String str = "0";
        for (String s : getLore(is)) {
            if (clearColor(s).contains("❃ 強化レベル:")){
                str = clearColor(s).split(" ")[3];
            }
        }
        return Integer.parseInt(str);
    }
    public static Integer getWeaponLevelMax(ItemStack is){
        String str = "0";
        for (String s : getLore(is)) {
            if (clearColor(s).contains("❃ 強化レベル:")){
                str = clearColor(s).split(" ")[5];
            }
        }
        return Integer.parseInt(str);
    }
    public static Integer getArmorLevel(ItemStack is){
        String str = "0";
        for (String s : getLore(is)) {
            if (clearColor(s).contains("❃ 強化値:")){
                str = clearColor(s).split(" ")[3].replace("+","");
            }
        }
        return Integer.parseInt(str);
    }
    public static Integer getArmorLevelMax(ItemStack is){
        for (String s : getLore(is)) {
            if (clearColor(s).contains("防具アイテム")){
                String r = s.split(" ")[1];
                if(r.contains("§b")){
                    return 12;
                }else if(r.contains("§d")){
                    return 16;
                }else if(r.contains("§6")){
                    return 20;
                }
            }
        }
        return 0;
    }
}
