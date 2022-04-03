package com.github.soramame0256.dungeonpvextension.listener;

import java.time.Instant;

public class DpeTimer {
    private final Instant dpeTimerStarts;
    private final Long dpeTimer;
    private final String icon;
    public DpeTimer(Instant starts, Long timer, String icon){
        this.dpeTimerStarts = starts;
        this.dpeTimer = timer;
        this.icon = icon;
    }
    public boolean update(){
        if(dpeTimerStarts.toEpochMilli() + dpeTimer < Instant.now().toEpochMilli()){
            return true;
        }
        return false;
    }
    public void delete(){
        EventListener.dpeTimers.remove(this);
    }
    public String getSecond(){
        return "§7 | " + icon + " " + (dpeTimer / 1000 - (Instant.now().getEpochSecond() - dpeTimerStarts.getEpochSecond()));
    }
    public String getConcatSecond(String s){
        return s.concat(getSecond());
    }
    public String getIcon(){
        return this.icon;
    }

}
