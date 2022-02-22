package com.github.soramame0256.dungeonpvextension.api;

public enum Character {
    TRAVELER("主人公", Element.WIND),
    EMBER("エンバー", Element.FIRE),
    PARPARA("パーパラ", Element.WATER),
    RAZER("レーザー", Element.THUNDER),
    RAIKA("ライカ", Element.THUNDER),
    GRAY("グレー", Element.FIRE);
    public String name;
    public Element element;
    Character(String name, Element element){
        this.name = name;
        this.element = element;
    }
}
