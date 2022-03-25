package com.github.soramame0256.dungeonpvextension.listener;

import java.time.Instant;

public class BombTimer {
    private final Instant bombTimerStarts;
    private final Long bombTimer;
    private final String icon;
    public BombTimer(Instant starts, Long timer, String icon){
        this.bombTimerStarts = starts;
        this.bombTimer = timer;
        this.icon = icon;
    }
    public boolean update(){
        if(bombTimerStarts.toEpochMilli() + bombTimer < Instant.now().toEpochMilli()){
            return true;
        }
        return false;
    }
    public void delete(){
        EventListener.bombTimers.remove(this);
    }
    public String getSecond(){
        return "§7 |§c " + icon + " " + (bombTimer / 1000 - (Instant.now().getEpochSecond() - bombTimerStarts.getEpochSecond()));
    }
    public String getConcatSecond(String s){
        return s.concat(getSecond());
    }
    public String getIcon(){
        return this.icon;
    }

}
