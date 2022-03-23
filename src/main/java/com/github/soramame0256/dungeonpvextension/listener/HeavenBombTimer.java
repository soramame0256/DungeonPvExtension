package com.github.soramame0256.dungeonpvextension.listener;

import java.time.Instant;

public class HeavenBombTimer {
    private final Instant bombTimerStarts;
    private static final Long TIMER_MIN_SEC = 60000L;
    public HeavenBombTimer(Instant starts){
        this.bombTimerStarts = starts;
    }
    public void update(){
        if(bombTimerStarts.toEpochMilli() + TIMER_MIN_SEC < Instant.now().toEpochMilli()){
            EventListener.bombTimers.remove(this);
        }
    }
    public void delete(){
        EventListener.bombTimers.remove(this);
    }
    public String getSecond(){
        return "§7 |§6 ☢ " + (TIMER_MIN_SEC / 1000 - (Instant.now().getEpochSecond() - bombTimerStarts.getEpochSecond()));
    }
    public String getConcatSecond(String s){
        return s.concat(getSecond());
    }

}
