package com.github.soramame0256.dungeonpvextension.utils.toast;

import com.github.soramame0256.dungeonpvextension.utils.EnumTextRenderType;

import java.time.Instant;


public class ConstantMessage implements IToastMessage{
    private int y;
    private int limitY;
    private String message;
    private EnumTextRenderType renderType;
    private long dieAfter;
    private long dieAt;
    private final long bornAt;
    int alpha = 3;
    boolean dieInstantly = false;
    ConstantMessage(ToastQueue tq, Long dieAfter){
        this.message = tq.message;
        this.renderType = tq.renderType;
        this.bornAt = Instant.now().toEpochMilli();
        this.dieAfter = dieAfter;
        this.dieAt = this.bornAt+this.dieAfter;
    }
    @Override
    public void calcAlpha(){
        this.alpha = 255;
    }
    @Override
    public void calcNextY(){
        this.y = limitY;
    }
    @Override
    public void draw(int x){
        renderType.draw(this.message, x, this.y, 0xffffffff);
    }
    @Override
    public void kill(){
        dieInstantly = true;
        this.dieAfter = Instant.now().toEpochMilli()-this.bornAt;
        this.dieAt = Instant.now().toEpochMilli();
    }
    @Override
    public boolean checkExpire(){
        return Instant.now().toEpochMilli() > dieAt;
    }
    @Override
    public boolean isKilledSoon(){
        return dieInstantly;
    }
    @Override
    public int getCurrentY(){
        return this.y;
    }
    @Override
    public void setCurrentY(int y){
        this.y = y;
    }
    @Override
    public int getTargetY(){
        return this.limitY;
    }
    @Override
    public void setTargetY(int limitY){
        this.limitY = limitY;
    }
}