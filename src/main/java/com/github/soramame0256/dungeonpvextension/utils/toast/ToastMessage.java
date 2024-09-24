package com.github.soramame0256.dungeonpvextension.utils.toast;


import com.github.soramame0256.dungeonpvextension.utils.EnumTextRenderType;

import java.time.Instant;


public class ToastMessage implements IToastMessage{
    private int y;
    private int limitY;
    private String message;
    private EnumTextRenderType renderType;
    private double yCalcCache;
    private long timesYCalc;
    private long dieAfter;
    private long dieAt;
    private final long bornAt;
    private int initY;
    int alpha = 3;
    boolean dieInstantly = false;
    ToastMessage(ToastQueue tq, Long dieAfter){
        this.message = tq.message;
        this.renderType = tq.renderType;
        this.bornAt = Instant.now().toEpochMilli();
        this.dieAfter = dieAfter;
        this.dieAt = this.bornAt+this.dieAfter;
    }
    @Override
    public void calcAlpha(){
        if (dieInstantly) alpha = (int) ((double)(dieAt-Instant.now().toEpochMilli())/200*255);
        else alpha = (double)(Instant.now().toEpochMilli()-this.bornAt)/dieAfter > 0.9d ? Math.max((int)((1-((double)(Instant.now().toEpochMilli()-this.bornAt)/dieAfter))*2550),1) : (double)(Instant.now().toEpochMilli()-this.bornAt)/dieAfter <= 0.01d ? Math.max((int)((((double)(Instant.now().toEpochMilli()-this.bornAt)/dieAfter))*25500),1) : 255;
    }
    @Override
    public void calcNextY(){
        if(yCalcCache <= limitY){
            timesYCalc = 0;
        }else {
            yCalcCache = (double)initY/((timesYCalc+1)/20d);
            timesYCalc++;
            this.y = this.yCalcCache <= y ? (int) this.yCalcCache : y;
        }

    }
    @Override
    public void draw(int x){
        if(!(bornAt+30 < Instant.now().toEpochMilli())) return;
        if((dieAt-10 < Instant.now().toEpochMilli())) return;
        calcAlpha();
        renderType.draw(this.message, x, this.y, 0xffffff | alpha<<24);
    }
    @Override
    public void kill(){
        dieInstantly = true;
        this.dieAfter = Instant.now().toEpochMilli()-this.bornAt+200;
        this.dieAt = Instant.now().toEpochMilli()+200;
    }
    @Override
    public boolean checkExpire(){
        return Instant.now().toEpochMilli() > dieAt;
    }

    @Override
    public boolean isKilledSoon() {
        return dieInstantly;
    }

    @Override
    public int getCurrentY() {
        return this.y;
    }

    @Override
    public void setCurrentY(int y) {
        this.y = y;
        this.yCalcCache = y;
        this.initY = Math.max(initY, y);
    }

    @Override
    public int getTargetY() {
        return this.limitY;
    }

    @Override
    public void setTargetY(int limitY) {
        this.limitY = limitY;
        initY = limitY;
    }
}