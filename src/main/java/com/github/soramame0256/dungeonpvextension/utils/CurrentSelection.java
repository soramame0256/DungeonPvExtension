package com.github.soramame0256.dungeonpvextension.utils;

import com.github.soramame0256.dungeonpvextension.api.Character;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

public class CurrentSelection {
    private static Character character = Character.NONE;
    private static Integer maxHealth = 0;
    private static Integer defence = 0;
    private static Integer attack = 0;
    private static Float critChance = 0f;
    private static Double critDamage = 0d;
    private static Double chargeEff = 0d;
    public static void setAll(ItemStack i){
        int line = 0;
        for(String s : ItemUtilities.getNonModdedLore(i)) {
            line++;
            if (line == 1) {
                try {
                    character = Character.valueOf(StringUtilities.clearColor(s).replace(Minecraft.getMinecraft().player.getDisplayNameString(), "主人公"));
                }catch(IllegalArgumentException e){
                    character = Character.NONE;
                }
            }else if (s.contains("最大体力:")){
                maxHealth = Integer.parseInt(s.split(" ")[3]);
            }else if (s.contains("防御力:")){
                defence = Integer.parseInt(s.split(" ")[3]);
            }else if(s.contains("攻撃力:")){
                attack = Integer.parseInt(s.split(" ")[3]);
            }else if(s.contains("会心率:")){
                critChance = Float.parseFloat(s.split(" ")[3].replace("%", ""));
            }else if(s.contains("会心ダメージ:")){
                critDamage = Double.parseDouble(s.split(" ")[3].replace("%", ""));
            }else if(s.contains("チャージ効率:")){
                chargeEff = Double.parseDouble(s.split(" ")[3].replace("%", ""));
            }
        }
        ItemUtilities.setTempModded(i);
    }
    public static Double getChargeEff() {
        return chargeEff;
    }
    public static Double getCritDamage() {
        return critDamage;
    }
    public static Integer getAttack() {
        return attack;
    }
    public static Float getCritChance() {
        return critChance;
    }
    public static Integer getDefence() {
        return defence;
    }
    public static Integer getMaxHealth() {
        return maxHealth;
    }
    public static Character getCharacter() {
        return character;
    }
}
