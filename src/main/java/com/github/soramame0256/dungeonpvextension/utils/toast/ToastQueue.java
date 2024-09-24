package com.github.soramame0256.dungeonpvextension.utils.toast;


import com.github.soramame0256.dungeonpvextension.utils.EnumTextRenderType;

import javax.annotation.Nullable;


public class ToastQueue {
    String message;
    EnumTextRenderType renderType;
    Class<? extends IToastMessage> toastMsg;
    public ToastQueue(String message){
        this.message = message;
        this.renderType = EnumTextRenderType.FULL_SHADOW;
        this.toastMsg = ToastMessage.class;
    }
    public ToastQueue(String message, EnumTextRenderType renderType){
        this.message = message;
        this.renderType = renderType;
        this.toastMsg = ToastMessage.class;
    }
    public ToastQueue(String message, @Nullable EnumTextRenderType renderType, Class<? extends IToastMessage> toastMessage){
        this.message = message;
        this.renderType = renderType == null ? EnumTextRenderType.FULL_SHADOW : renderType;
        this.toastMsg = toastMessage;

    }
}