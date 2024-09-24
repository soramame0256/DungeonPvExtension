package com.github.soramame0256.dungeonpvextension.utils.toast;

public interface IToastMessage {
    void calcAlpha();
    void calcNextY();
    void draw(int x);
    void kill();
    boolean checkExpire();
    boolean isKilledSoon();
    int getCurrentY();
    void setCurrentY(int y);
    int getTargetY();
    void setTargetY(int limitY);
}