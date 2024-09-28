package com.github.soramame0256.dungeonpvextension.api;

public enum Character {
    TRAVELER("主人公", Element.WIND,60f),
    EMBER("エンバー", Element.FIRE, 40f),
    PARPARA("パーパラ", Element.WATER, 80f),
    RAZER("レーザー", Element.THUNDER, 60f),
    RAIKA("ライカ", Element.THUNDER,60f),
    GRAY("グレー", Element.FIRE, 80f),
    ARCTICA("アークティカ", Element.ICE, 70f),
    TOPARA("トパーラ", Element.ROCK, 60f),
    SAFARA("サファーラ",Element.WATER, 50f),
    TURQUOIRE("ターコイラ",Element.WIND, 90f),
    NONE("",Element.WIND, 0f);

    public String name;
    public Element element;
    public Float chargeMax;
    Character(String name, Element element, Float chargeMax){
        this.name = name;
        this.element = element;
        this.chargeMax = chargeMax;
    }
}
