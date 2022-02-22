package com.github.soramame0256.dungeonpvextension.api;

public enum Option {
    ATK("攻撃力"),
    ATK_PERCENTAGE("攻撃力%"),
    HP("体力"),
    HP_PERCENTAGE("体力%"),
    DEF("防御力"),
    DEF_PERCENTAGE("防御力%"),
    CRITICAL_DAMAGE("会心ダメージ"),
    CRITICAL_CHANCE("会心率"),
    CHARGE_EFFICIENCY("チャージ効率");
    public String name;
    Option(String name) {
        this.name = name;
    }
}
