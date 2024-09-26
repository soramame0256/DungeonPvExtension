package com.github.soramame0256.dungeonpvextension.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static com.github.soramame0256.dungeonpvextension.utils.ItemUtilities.HashAlgorithm.SHA512;

public class ItemUtilities {
    private static Map<ItemStack, String> hashes = new HashMap<>();
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
            if (StringUtilities.clearColor(s).contains("❃ 強化レベル:")){
                str = StringUtilities.clearColor(s).split(" ")[3];
            }
        }
        return Integer.parseInt(str);
    }
    public static Integer getWeaponLevelMax(ItemStack is){
        String str = "0";
        for (String s : getLore(is)) {
            if (StringUtilities.clearColor(s).contains("❃ 強化レベル:")){
                str = StringUtilities.clearColor(s).split(" ")[5];
            }
        }
        return Integer.parseInt(str);
    }
    public static double getBaseSubStatus(ItemStack is){
        if (!is.hasTagCompound()) return 0;
        return is.getTagCompound().hasKey("baseSubStat") ? is.getTagCompound().getDouble("baseSubStat") : 0;
    }
    public static Integer getArmorLevel(ItemStack is){
        String str = "0";
        for (String s : getLore(is)) {
            if (StringUtilities.clearColor(s).contains("❃ 強化値:")){
                str = StringUtilities.clearColor(s).split(" ")[3].replace("+","");
            }
        }
        return Integer.parseInt(str);
    }
    public static boolean isDungeonItem(List<String> lore){
        AtomicReference<Boolean> is = new AtomicReference<>();
        is.set(false);
        lore.forEach(str ->{
            if(str.contains("ダンジョンアイテム")){
                is.set(true);
            }
        });
        return is.get();
    }
    public static boolean isPotItem(List<String> lore){
        AtomicReference<Boolean> is = new AtomicReference<>();
        is.set(false);
        lore.forEach(str ->{
            if(str.contains("回復アイテム")){
                is.set(true);
            }
        });
        return is.get();
    }
    public static Integer getDamageMin(Float damBase){
        return Math.round(damBase*0.7f);
    }
    public static Integer getDamageMax(Float damBase){
        return Math.round(damBase*1.1f);
    }
    public static Integer getArmorLevelMax(ItemStack is){
        switch (getRarity(is)){
            case 3:
                return 12;
            case 4:
                return 16;
            case 5:
                return 20;

            default:
                return 0;
        }
    }

    private static String toHash(String text, HashAlgorithm algorithm) {
        String hashAlgorithm = "";
        if (algorithm==HashAlgorithm.MD5) {
            hashAlgorithm = "MD5";
        } else if (algorithm==HashAlgorithm.SHA1) {
            hashAlgorithm = "SHA-1";
        } else if (algorithm==HashAlgorithm.SHA256) {
            hashAlgorithm = "SHA-256";
        } else {
            hashAlgorithm = "SHA-512";
        }

        StringBuffer sb = new StringBuffer();
        try {
            MessageDigest md = MessageDigest.getInstance(hashAlgorithm);
            byte[] cipherBytes = md.digest(text.getBytes());

            for (int i=0; i<cipherBytes.length; i++) {
                sb.append(String.format("%02x", cipherBytes[i]&0xff));
            }
        } catch (NoSuchAlgorithmException ex1) {
            System.out.println("ハッシュアルゴリズム名が不正です。");
        } catch (NullPointerException ex2) {
            System.out.println("ハッシュアルゴリズム名が指定されていません。");
        }

        return sb.toString();
    }
    public static String itemToHash(ItemStack is){
        if(hashes.containsKey(is)){
            return hashes.get(is);
        }
        String hash;
        if(is.hasTagCompound())
            hash = toHash(is.getTagCompound().toString(), SHA512);
        else
            hash = toHash(is.getTranslationKey(), SHA512);
        hashes.put(is, hash);
        return hash;
    }
    public static String getArtifactGroupId(ItemStack is){
        if(is.getTagCompound() != null && is.getTagCompound().hasKey("artiSet")){
            return is.getTagCompound().getString("artiSet");
        }
        return "";
    }
    public static String getId(ItemStack is){
        if(isArmor(Arrays.asList(getLore(is)))) return getArtifactGroupId(is);
        if(isWeapon(Arrays.asList(getLore(is)))) return (is.getTagCompound() != null && is.getTagCompound().hasKey("id")) ? (is.getTagCompound().getString("id")) : "";
        return "";
    }
    public static Integer getRarity(ItemStack is){
        String type = "";
        if (isArmor(Arrays.asList(getLore(is)))){
            type = "防具アイテム";
        }else if(isWeapon(Arrays.asList(getLore(is)))){
            type = "武器アイテム";
        }
        if(type.equals("")){
            return 0;
        }
        for (String s : getLore(is)) {
            if (StringUtilities.clearColor(s).contains(type)){
                String r = s.split(" ")[1];
                if(r.contains("§b")){
                    return 3;
                }else if(r.contains("§d")){
                    return 4;
                }else if(r.contains("§6")){
                    return 5;
                }
            }
        }
        return 0;
    }
    enum HashAlgorithm {
        MD5,
        SHA1,
        SHA256,
        SHA512
    }
}
