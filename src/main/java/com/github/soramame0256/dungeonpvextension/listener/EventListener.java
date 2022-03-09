package com.github.soramame0256.dungeonpvextension.listener;

import com.github.soramame0256.dungeonpvextension.DungeonPvExtension;
import com.github.soramame0256.dungeonpvextension.utils.ArrayUtilities;
import com.github.soramame0256.dungeonpvextension.utils.CurrentSelection;
import com.github.soramame0256.dungeonpvextension.utils.HudUtilities;
import com.github.soramame0256.dungeonpvextension.utils.ItemUtilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.github.soramame0256.dungeonpvextension.DungeonPvExtension.CONFIG_TYPES.disableIds;
import static com.github.soramame0256.dungeonpvextension.DungeonPvExtension.inDP;
import static com.github.soramame0256.dungeonpvextension.DungeonPvExtension.isEnable;
import static com.github.soramame0256.dungeonpvextension.utils.NumberUtilities.commaSeparate;
import static com.github.soramame0256.dungeonpvextension.utils.StringUtilities.clearColor;

public class EventListener {
    public static Instant potCooldownStarts;
    public static final long POT_COOLDOWN = 3000;
    public static Boolean isPotCooldown = false;
    private static ResourceLocation BAR = new ResourceLocation("minecraft", "textures/gui/bars.png");
    public EventListener() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    @SubscribeEvent
    public void onUpdate(TickEvent.ClientTickEvent e){
        if(e.phase == TickEvent.Phase.END && Minecraft.getMinecraft().player != null ){
            ScoreObjective so = Minecraft.getMinecraft().player.getWorldScoreboard().getObjectiveInDisplaySlot(1);
            if(inDP && so != null && so.getDisplayName().contains("Dungeon PvE") && !ArrayUtilities.isContain(disableIds, Minecraft.getMinecraft().player.getDisplayName().getUnformattedText())){
                if(isPotCooldown && potCooldownStarts.toEpochMilli() + POT_COOLDOWN < Instant.now().toEpochMilli()){
                    isPotCooldown = false;
                    System.out.println(String.valueOf(potCooldownStarts.toEpochMilli()) + false);
                }
            }else if(so != null && isEnable && !ArrayUtilities.isContain(disableIds, Minecraft.getMinecraft().player.getDisplayName().getUnformattedText())){inDP = so.getDisplayName().contains("Dungeon PvE");
            }else if(!isEnable || ArrayUtilities.isContain(disableIds, Minecraft.getMinecraft().player.getDisplayName().getUnformattedText())){inDP = false;
            }
        }
    }
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChatReceived(ClientChatReceivedEvent e) {
        if (inDP) {
            String chatMsg = clearColor(e.getMessage().getUnformattedText());
            //&1[♥ +114&1]
            if (chatMsg.startsWith("[❤ +") && chatMsg.endsWith("]")) {
                potCooldownStarts = Instant.now();
                isPotCooldown = true;
                System.out.println(String.valueOf(potCooldownStarts.toEpochMilli()) + true);
            }
        }
    }

    @SubscribeEvent
    public void onToolTipRender(ItemTooltipEvent e){
        if (inDP && ItemUtilities.isWeapon(e.getToolTip()) && !ItemUtilities.isTempModded(e.getItemStack()) && e.getItemStack().getTagCompound() != null && e.getItemStack().getTagCompound().hasKey("display")){
            List<String> newLore = new ArrayList<>();
            boolean nextSub = false;
            boolean customLore = false; //+--------------------+から+--------------------+の間=true
            List<String> oldLore;
            oldLore = Arrays.asList(ItemUtilities.getNonModdedLore(e.getItemStack()));
            for (String s : oldLore) {
                if (nextSub) {
                    newLore.add(s + " §7(" + e.getItemStack().getTagCompound().getInteger("baseSubStat") + ")");
                    nextSub = false;
                } else if (s.contains("基礎攻撃力") && e.getItemStack().getTagCompound() != null && e.getItemStack().getTagCompound().hasKey("baseAtk")) {
                    newLore.add(s + " §7(" + e.getItemStack().getTagCompound().getInteger("baseAtk") + ")");
                    nextSub = true;
                } else if (clearColor(s).equals("+--------------------+")) {
                    if (customLore && e.getItemStack().getTagCompound() != null && e.getItemStack().getTagCompound().hasKey("internalExp")) {
                        newLore.add("§0");
                        newLore.add(" §f解体入手エッセンス: §a+" + commaSeparate(e.getItemStack().getTagCompound().getInteger("internalExp")));
                        customLore = false;
                    } else {
                        customLore = true;
                    }
                    newLore.add(s);
                } else if (!s.equals(e.getItemStack().getDisplayName())) {
                    newLore.add(s);
                }
            }
            if(ArrayUtilities.isStringContainsInList(ItemUtilities.getNonModdedLore(e.getItemStack()), "強化費係数:")) {
                Integer level = ItemUtilities.getItemLevel(e.getItemStack());
                int maxLevel = ItemUtilities.getItemLevelMax(e.getItemStack());
                newLore.add("§7 必要コストリスト");
                Long totalCost = 0L;
                for(int i = level; i < maxLevel; i++){
                    Long cost = Math.round((1+0.2*(Math.pow(i, 1.5)))*e.getItemStack().getTagCompound().getInteger("amp")*100);
                    newLore.add("§7 " + i + ": " + commaSeparate(cost));
                    totalCost += cost;
                }
                newLore.add("§7 合計: " + commaSeparate(totalCost));
            }
            ItemUtilities.changeLore(e.getItemStack(), newLore);
        }else if (inDP && ItemUtilities.isArmor(e.getToolTip()) && !ItemUtilities.isTempModded(e.getItemStack()) && e.getItemStack().getTagCompound() != null && e.getItemStack().getTagCompound().hasKey("display")){
            List<String> newLore = new ArrayList<>();
            boolean customLore = false; //+--------------------+から+--------------------+の間=true
            List<String> oldLore;
            oldLore = Arrays.asList(ItemUtilities.getNonModdedLore(e.getItemStack()));
            for (String s : oldLore) {
                if(clearColor(s).equals("+--------------------+")){
                    if (customLore && e.getItemStack().getTagCompound() != null && e.getItemStack().getTagCompound().hasKey("internalExp")){
                        newLore.add("§0");
                        newLore.add(" §f解体入手エッセンス: §a+" + commaSeparate(e.getItemStack().getTagCompound().getInteger("internalExp")));
                        customLore = false;
                    }else{
                        customLore = true;
                    }
                    newLore.add(s);
                }else{
                    newLore.add(s);
                }
            }
            ItemUtilities.changeLore(e.getItemStack(), newLore);
        }else if (inDP && ItemUtilities.isScrap(e.getToolTip()) && !ItemUtilities.isTempModded(e.getItemStack()) && e.getItemStack().getTagCompound() != null && e.getItemStack().getTagCompound().hasKey("display")){
            List<String> newLore = new ArrayList<>();
            List<String> oldLore;
            oldLore = Arrays.asList(ItemUtilities.getNonModdedLore(e.getItemStack()));
            for (String s : oldLore) {
                if(clearColor(s).contains("解体アイテム")){
                    newLore.add(s);
                    if(e.getItemStack().getTagCompound().hasKey("sellEssence")) {
                        newLore.add("§f解体時入手エッセンス: §a+" + commaSeparate(e.getItemStack().getTagCompound().getInteger("sellEssence") * e.getItemStack().getCount()) + " §7(@" + commaSeparate(e.getItemStack().getTagCompound().getInteger("sellEssence")) + ")");
                    }
                    if(e.getItemStack().getTagCompound().hasKey("sellGold")){
                        newLore.add("§f解体時入手ゴールド: §e+" + commaSeparate(e.getItemStack().getTagCompound().getInteger("sellGold")*e.getItemStack().getCount()) + " §7(@" + commaSeparate(e.getItemStack().getTagCompound().getInteger("sellGold")) + ")");
                    }
                }else{
                    newLore.add(s);
                }
            }
            ItemUtilities.changeLore(e.getItemStack(), newLore);
        }//else if (inDP && ArrayUtilities.isStringContainsInList(e.getToolTip(),">> 右クリックでキャラ変更 <<") && e.getItemStack().getDisplayName().startsWith("キャラクター [") && e.getItemStack().getDisplayName().endsWith("]") && !ItemUtilities.isTempModded(e.getItemStack()) && e.getItemStack().getTagCompound() != null && e.getItemStack().getTagCompound().hasKey("display")){
            //CurrentSelection.setAll(e.getItemStack());
//        }
    }
    @SubscribeEvent
    public void onActionBarUpdate(RenderGameOverlayEvent e){
        String second;
        if(inDP && isPotCooldown && !HudUtilities.getActionbar().contains("⌛")) {
            second = String.valueOf(POT_COOLDOWN/1000 - (Instant.now().getEpochSecond() - potCooldownStarts.getEpochSecond()));
            if (second.length() == 1) {
                second = "0" + second;
            }
            Minecraft.getMinecraft().ingameGUI.setOverlayMessage(HudUtilities.getActionbar() + "§7 |§6 ⌛ " + second,false);
        }
    }
    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.NORMAL,receiveCanceled = true)
    public void onKeyBindingPressed(InputEvent.KeyInputEvent e){
        KeyBinding[] keyBindings = DungeonPvExtension.keyBindings;
        if (keyBindings[0].isPressed()){
            Minecraft.getMinecraft().player.sendChatMessage("/die");
        }
        if (keyBindings[1].isPressed()){
            Minecraft.getMinecraft().player.sendChatMessage("/item");
        }
        if (keyBindings[2].isPressed()){
            Minecraft.getMinecraft().player.sendChatMessage("♥ " + HudUtilities.getHealth() + "/" + HudUtilities.getMaxHealth());
        }
    }
}
