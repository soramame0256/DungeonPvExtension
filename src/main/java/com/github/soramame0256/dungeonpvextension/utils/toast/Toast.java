package com.github.soramame0256.dungeonpvextension.utils.toast;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import static net.minecraft.client.gui.Gui.drawRect;

/**
 * @author soramame_256
 * Toastを利用するためのクラスです。
 */
public class Toast {
    private static List<Toast> toasts = new ArrayList<>();
    private int x, y, width, height;
    private long timeout = 10000L;
    private final List<ToastQueue> toastQueues = new ArrayList<>();
    private final List<IToastMessage> toastMessages = new ArrayList<>();
    private final List<IToastMessage> garbage = new ArrayList<>();
    private boolean isRenderBackground = true;
    private boolean isInstantDestroy = true;
    private boolean shouldTBWhenReachBound = false;
    private int messagesAllocated;
    private final int maxMessagesAllocated;
    public Toast(int x, int y, int width, int height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.maxMessagesAllocated = this.height / Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;
        messagesAllocated = maxMessagesAllocated;
        toasts.add(this);
    }
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
    public void setRenderBackground(boolean render){
        this.isRenderBackground = render;
    }
    public void makeNoTimeout(){
        this.timeout = Long.MAX_VALUE;
    }
    public boolean isRenderBackground(){
        return this.isRenderBackground;
    }
    public void setMessagesAllocated(int alpha){
        this.messagesAllocated = alpha <= maxMessagesAllocated ? alpha : messagesAllocated;
    }
    public void addQueue(ToastQueue tq){
        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        String str;
        int leng = tq.message.length();
        if(fr.getStringWidth(tq.message) > this.width && shouldTBWhenReachBound){
            while (fr.getStringWidth(tq.message.substring(0,leng )) > this.width){
                leng --;
            }
            str = tq.message;
            while (str.length()>0){
                String st = str.substring(0, Math.min(leng , str.length()));
                str = str.substring(Math.min(str.length(),leng +1));
                toastQueues.add(new ToastQueue(st, tq.renderType, tq.toastMsg));
            }
        }else{
            toastQueues.add(tq);
        }
    }
    public void update(){
        List<ToastQueue> toRemove = new ArrayList<>();
        if(toastQueues.size() > 0){
            for(ToastQueue tq : toastQueues) {
                if (toastMessages.size() == messagesAllocated && isInstantDestroy) {
                    for(int i=0; i<toastMessages.size(); i++) {
                        if(i>=toastQueues.size()) break;
                        if(toastMessages.get(i).isKilledSoon()) continue;
                        toastMessages.get(i).kill();
                        break;
                    }
                }
                if(toastMessages.size() < messagesAllocated) {
                    IToastMessage newMsg = null;
                    try {
                        newMsg = tq.toastMsg.getDeclaredConstructor(ToastQueue.class, Long.class).newInstance(tq, this.timeout);
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                    if (newMsg != null) {
                        newMsg.setCurrentY(height+this.y-Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT);
                        toastMessages.add(newMsg);
                        toRemove.add(tq);
                    }
                }
            }
        }
        toastQueues.removeAll(toRemove);
        for(int i=0; i<toastMessages.size(); i++){
            IToastMessage tm = toastMessages.get(i);
            tm.setTargetY(Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT*i+this.y);
        }
    }
    private void draw(){
        if (isRenderBackground){
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            drawRect(this.x-2,this.y-2,this.width+this.x,this.y+this.height,0x50000000);
            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
        }
        for (IToastMessage tm : toastMessages) {
            if (tm.checkExpire()) {
                garbage.add(tm);
                continue;
            }
            tm.calcNextY();
            if (Math.max(tm.getCurrentY(), tm.getTargetY())==tm.getTargetY()) tm.setCurrentY(tm.getTargetY());
            tm.draw(this.x);
        }
        for(IToastMessage tm : garbage){
            toastMessages.remove(tm);
        }
        garbage.clear();
    }
    @Override
    protected void finalize() throws Throwable {
        toasts.remove(this);
        super.finalize();
    }
    public void destroy(){
        toasts.remove(this);
    }
    public void setInstantDestroy(boolean instantDestroy) {
        isInstantDestroy = instantDestroy;
    }
    public void setToBeTurnBack(boolean toBeTurnBack){
        this.shouldTBWhenReachBound=toBeTurnBack;
    }
    public static class Renderer{
        public Renderer(){
            MinecraftForge.EVENT_BUS.register(this);
        }
        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public void onUpdate(RenderGameOverlayEvent e){
            if(e.getType() == RenderGameOverlayEvent.ElementType.ALL){
                for(Toast ts : toasts){
                    ts.draw();
                    ts.update();
                }
            }
        }
    }
}