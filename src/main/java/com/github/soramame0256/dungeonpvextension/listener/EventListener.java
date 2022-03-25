package com.github.soramame0256.dungeonpvextension.listener;

import com.github.soramame0256.dungeonpvextension.DungeonPvExtension;
import com.github.soramame0256.dungeonpvextension.utils.ArrayUtilities;
import com.github.soramame0256.dungeonpvextension.utils.HudUtilities;
import com.github.soramame0256.dungeonpvextension.utils.ItemUtilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
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
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

import static com.github.soramame0256.dungeonpvextension.DungeonPvExtension.CONFIG_TYPES.disableIds;
import static com.github.soramame0256.dungeonpvextension.DungeonPvExtension.inDP;
import static com.github.soramame0256.dungeonpvextension.DungeonPvExtension.isEnable;
import static com.github.soramame0256.dungeonpvextension.utils.NumberUtilities.commaSeparate;
import static com.github.soramame0256.dungeonpvextension.utils.StringUtilities.clearColor;
import static java.lang.Math.round;

public class EventListener {
    public static Instant potCooldownStarts;
    public static final long POT_COOLDOWN = 3000;
    public static Boolean isPotCooldown = false;
    public static List<BombTimer> bombTimers = new ArrayList<>();
    private static ResourceLocation BAR = new ResourceLocation("minecraft", "textures/gui/bars.png");
    private static final double WEAPON_UPGRADE_CONSTANT = 0.15d;
    private static final double ARMOR_UPGRADE_CONSTANT = 1/3d;
    public EventListener() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onUpdate(TickEvent.ClientTickEvent e){
        if(e.phase == TickEvent.Phase.END && Minecraft.getMinecraft().player != null ){
            ScoreObjective so = Minecraft.getMinecraft().player.getWorldScoreboard().getObjectiveInDisplaySlot(1);
            if(isEnable && inDP && so != null && so.getDisplayName().contains("Dungeon PvE") && !ArrayUtilities.isContain(disableIds, Minecraft.getMinecraft().player.getDisplayName().getUnformattedText())){
                if(isPotCooldown && potCooldownStarts.toEpochMilli() + POT_COOLDOWN < Instant.now().toEpochMilli()){
                    isPotCooldown = false;
                    System.out.println(String.valueOf(potCooldownStarts.toEpochMilli()) + false);
                }
                if(bombTimers.size() != 0){
                    AtomicReference<Integer> amount = new AtomicReference<>(0);
                    bombTimers.forEach(bombTimer -> {
                        if (bombTimer.update()){
                            amount.set(amount.get()+1);
                        }
                    });
                    if (amount.get() > 0) {
                        bombTimers.subList(0, amount.get()).clear();
                    }
                }
            }else if(so != null && isEnable && !ArrayUtilities.isContain(disableIds, Minecraft.getMinecraft().player.getDisplayName().getUnformattedText())){inDP = so.getDisplayName().contains("Dungeon PvE");
            }else if(!isEnable || ArrayUtilities.isContain(disableIds, Minecraft.getMinecraft().player.getDisplayName().getUnformattedText())){inDP = false;
            }
        }
    }
    public void bombTimerDelete(String icon){
        BombTimer bombTimer1 = null;
        for (BombTimer bombTimer : bombTimers) {
            if (bombTimer.getIcon().equals(icon)) {
                bombTimer1 = bombTimer;
                break;
            }
        }
        if (bombTimer1 != null){
            bombTimer1.delete();
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
            } else if (chatMsg.equals("60秒以内に爆弾を解除(討伐)せよ！")){
                bombTimers.add(new BombTimer(Instant.now(), 60000L, "☢"));
            } else if (chatMsg.equals("爆弾の解除に成功した！")){
                bombTimerDelete("☢");
            } else if (chatMsg.equals("ロボットのHPを0にしてバクハツを止めろ！")){
                bombTimers.add(new BombTimer(Instant.now(), 8000L, "☼"));
            } else if (chatMsg.equals("[天国の番人] いでよ幻影！")){
                bombTimers.add(new BombTimer(Instant.now(), 60000L, "❂"));
            } else if (chatMsg.equals("[煉獄の支配者] 個々の力を見せてみろ！")){
                bombTimers.add(new BombTimer(Instant.now(), 30000L, "۞"));
            } else if (chatMsg.equals("機械の討伐に成功した！")){
                bombTimerDelete("۞");
            }

        }
    }

    @SubscribeEvent
    public void onMobDead(RenderLivingEvent.Pre<EntityLiving> e){
        if (e.getEntity().getHealth() == 0){
            if (clearColor(e.getEntity().getDisplayName().getUnformattedText()).equals("幻影")){
                bombTimerDelete("❂");
            }else if(clearColor(e.getEntity().getDisplayName().getUnformattedText()).equals("壊れた雪原の戦闘練習用ロボット")){
                bombTimerDelete("☼");
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
                        newLore.add(" §f解体入手ゴールド: §a+" + commaSeparate(round(e.getItemStack().getTagCompound().getInteger("internalExp")*0.6)));
                        customLore = false;
                    } else {
                        customLore = true;
                    }
                    newLore.add(s);
                } else if (!s.equals(e.getItemStack().getDisplayName())) {
                    newLore.add(s);
                }
            }
            if(ArrayUtilities.isStringContainsInList(e.getToolTip(), "強化費係数:")) {
                Integer level = ItemUtilities.getWeaponLevel(e.getItemStack());
                int maxLevel = ItemUtilities.getWeaponLevelMax(e.getItemStack());
                newLore.add("§7 必要コスト/メイン成長値リスト");
                long totalCost = 0L;
                double baseAtk = e.getItemStack().getTagCompound().getInteger("baseAtk");
                double baseSubStat = e.getItemStack().getTagCompound().getInteger("baseSubStat");
                for(int i = level; i < maxLevel; i++){
                    long cost = round((1+0.2*(Math.pow(i, 1.5)))*e.getItemStack().getTagCompound().getInteger("amp")*100);
                    int nextLevel = i+1;
                    newLore.add("§7 " + nextLevel + ": " + commaSeparate(cost) + " | " + round(baseAtk*(1+WEAPON_UPGRADE_CONSTANT*nextLevel)));
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
                        newLore.add(" §f解体入手ゴールド: §a+" + commaSeparate(round(e.getItemStack().getTagCompound().getInteger("internalExp")*0.6)));
                        customLore = false;
                    }else{
                        customLore = true;
                    }
                }
                newLore.add(s);
            }
            if(HudUtilities.getCurrentGuiTitle().equals("防具強化")){
                Integer level = ItemUtilities.getArmorLevel(e.getItemStack());
                int maxLevel = ItemUtilities.getArmorLevelMax(e.getItemStack());
                long totalCost = 0L;
                double baseStat = e.getItemStack().getTagCompound().getInteger("mainStat");
                newLore.add("§7必要コスト/メイン成長値リスト");
                for(int i = level; i < maxLevel; i++){
                    long cost = round(100*(1+0.2*(Math.pow(i, 2)))*maxLevel*0.25);
                    int nextLevel = i+1;
                    newLore.add("§7" + nextLevel + ": " + commaSeparate(cost) + " | " + round(baseStat*(1+ARMOR_UPGRADE_CONSTANT*nextLevel)));
                    totalCost += cost;
                }
                newLore.add("§7合計: " + commaSeparate(totalCost));
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
    public void onActionBarUpdate(RenderGameOverlayEvent e) {
        if (!HudUtilities.getActionbar().contains("§d§3§e")) {
            String second;
            AtomicReference<String> actionBarAddText = new AtomicReference<>("");
            if (inDP && isPotCooldown) {
                second = String.valueOf(POT_COOLDOWN / 1000 - (Instant.now().getEpochSecond() - potCooldownStarts.getEpochSecond()));
                if (second.length() == 1) {
                    second = "0" + second;
                    actionBarAddText.set(actionBarAddText.get().concat("§7 |§6 ⌛ " + second));
                }
            }
            if (inDP && bombTimers.size() != 0) {
                bombTimers.forEach(bombTimer -> actionBarAddText.set(bombTimer.getConcatSecond(actionBarAddText.get())));
            }
            Minecraft.getMinecraft().ingameGUI.setOverlayMessage(HudUtilities.getActionbar() + actionBarAddText.get() + "§d§3§e", false);
        }
    }
    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.NORMAL,receiveCanceled = true)
    public void onKeyBindingPressed(InputEvent.KeyInputEvent e) {
        if (inDP) {
            KeyBinding[] keyBindings = DungeonPvExtension.keyBindings;
            if (keyBindings[0].isPressed()) {
                Minecraft.getMinecraft().player.sendChatMessage("/die");
            }
            if (keyBindings[1].isPressed()) {
                Minecraft.getMinecraft().player.sendChatMessage("/item");
            }
            if (keyBindings[2].isPressed()) {
                Minecraft.getMinecraft().player.sendChatMessage("❤ " + HudUtilities.getHealth() + "/" + HudUtilities.getMaxHealth());
            }
        }
    }
}
