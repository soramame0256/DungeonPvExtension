package com.github.soramame0256.dungeonpvextension.api;

public enum Element {
    WIND("風"),
    WATER("水"),
    FIRE("火"),
    ICE("氷"),
    ROCK("岩"),
    THUNDER("雷");
    public String name;
    Element(String name){
        this.name = name;
    }

}
