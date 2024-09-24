package com.github.soramame0256.dungeonpvextension.utils;

public enum EnumTextRenderType {
    FULL_SHADOW {
        @Override
        public void draw(String text, float x, float y, int color) {
            RenderUtils.drawStringWithFullShadow(text, x, y, color & 0xffffff, (color & 0xff000000)>>24);
        }
    },
    SHADOW {
        @Override
        public void draw(String text, float x, float y, int color) {
            RenderUtils.drawStringWithShadow(text, x, y, color);
        }
    },
    NORMAL {
        @Override
        public void draw(String text, float x, float y, int color){
            RenderUtils.drawString(text, x, y, color);
        }
    };

    public abstract void draw(String text, float x, float y, int color);
}