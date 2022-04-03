package com.github.soramame0256.dungeonpvextension.listener;

import com.github.soramame0256.dungeonpvextension.DungeonPvExtension;
import com.github.soramame0256.dungeonpvextension.api.Option;
import com.github.soramame0256.dungeonpvextension.utils.ArrayUtilities;
import com.github.soramame0256.dungeonpvextension.utils.DataUtils;
import com.github.soramame0256.dungeonpvextension.utils.HudUtilities;
import com.github.soramame0256.dungeonpvextension.utils.ItemUtilities;
import com.google.gson.JsonArray;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLiving;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
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

import javax.xml.crypto.Data;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static com.github.soramame0256.dungeonpvextension.DungeonPvExtension.CONFIG_TYPES.disableIds;
import static com.github.soramame0256.dungeonpvextension.DungeonPvExtension.*;
import static com.github.soramame0256.dungeonpvextension.utils.ItemUtilities.isLocked;
import static com.github.soramame0256.dungeonpvextension.utils.NumberUtilities.commaSeparate;
import static com.github.soramame0256.dungeonpvextension.utils.NumberUtilities.toTime;
import static com.github.soramame0256.dungeonpvextension.utils.StringUtilities.clearColor;
import static java.lang.Math.round;

public class EventListener {
    public static Instant potCooldownStarts;
    public static final long POT_COOLDOWN = 3000;
    public static Boolean isPotCooldown = false;
    public static List<DpeTimer> dpeTimers = new ArrayList<>();
    private static final double WEAPON_UPGRADE_CONSTANT = 0.15d;
    private static final double ARMOR_UPGRADE_CONSTANT = 1/3d;
    private static Instant healthChatCooldown = null;
    private static Instant dungeonClearTime = null;
    private static JsonArray storageName = DataUtils.getInstance().getJsonArrayData("StorageNames");
    public EventListener() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static void reload(){
        storageName = DataUtils.getInstance().getJsonArrayData("StorageNames");
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
                if(dpeTimers.size() != 0){
                    AtomicReference<Integer> amount = new AtomicReference<>(0);
                    dpeTimers.forEach(dpeTimer -> {
                        if (dpeTimer.update()){
                            amount.set(amount.get()+1);
                        }
                    });
                    if (amount.get() > 0) {
                        dpeTimers.subList(0, amount.get()).clear();
                    }
                }
            }else if(so != null && isEnable && !ArrayUtilities.isContain(disableIds, Minecraft.getMinecraft().player.getDisplayName().getUnformattedText())){inDP = so.getDisplayName().contains("Dungeon PvE");
            }else if(!isEnable || ArrayUtilities.isContain(disableIds, Minecraft.getMinecraft().player.getDisplayName().getUnformattedText())){inDP = false;
            }
        }
    }
    public void dpeTimerDelete(String icon){
        DpeTimer dpeTimer1 = null;
        for (DpeTimer dpeTimer : dpeTimers) {
            if (dpeTimer.getIcon().equals(icon)) {
                dpeTimer1 = dpeTimer;
                break;
            }
        }
        if (dpeTimer1 != null){
            dpeTimer1.delete();
        }
    }
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChatReceived(ClientChatReceivedEvent e){
        String chatMsg = clearColor(e.getMessage().getUnformattedText());
        if (!isUpToDate && (chatMsg.equals("サーバーに関する重要なお知らせなどはDiscordで行っているため、Discordに参加することを推奨しています。") || chatMsg.equals("We encourage you to join Discord because we have important server-related announcements on Discord."))){
            Minecraft.getMinecraft().player.sendMessage(new TextComponentString("DungeonPvExtensionの最新バージョンが存在します! /dpeupdateで更新できます。"));
        }
        if (inDP) {
            //&1[♥ +114&1]
            if (chatMsg.startsWith("[❤ +") && chatMsg.endsWith("]")) {
                potCooldownStarts = Instant.now();
                isPotCooldown = true;
                System.out.println(String.valueOf(potCooldownStarts.toEpochMilli()) + true);
            } else if (chatMsg.equals("60秒以内に爆弾を解除(討伐)せよ！")){
                dpeTimers.add(new DpeTimer(Instant.now(), 60000L, "§c☢"));
            } else if (chatMsg.equals("爆弾の解除に成功した！")){
                dpeTimerDelete("§c☢");
            } else if (chatMsg.equals("ロボットのHPを0にしてバクハツを止めろ！")){
                dpeTimers.add(new DpeTimer(Instant.now(), 8000L, "§c☼"));
            } else if (chatMsg.equals("[天国の番人] いでよ幻影！")){
                dpeTimers.add(new DpeTimer(Instant.now(), 60000L, "§c❂"));
            } else if (chatMsg.equals("[煉獄の支配者] 個々の力を見せてみろ！") || chatMsg.equals("[❁ 煉獄の支配者] 個々の力を見せてみろ！")){
                dpeTimers.add(new DpeTimer(Instant.now(), 30000L, "§c۞"));
            } else if (chatMsg.equals("機械の討伐に成功した！")){
                dpeTimerDelete("§c۞");
            } else if (chatMsg.equals("誰かが世界樹の心臓部にテレポートされた。")){
                dpeTimers.add(new DpeTimer(Instant.now(), 30000L, "§c✿"));
            } else if (chatMsg.equals("心臓部からの脱出に成功した！")){
                dpeTimerDelete("§c✿");
            } else if (chatMsg.equals("\u22D9 ダンジョンに転移します.. ")){
                dungeonClearTime = Instant.now();
            } else if (chatMsg.equals("8秒後にテレポートされます")){
                ITextComponent textComponent = new TextComponentString("クリアタイム: " + toTime(Instant.now().getEpochSecond() - dungeonClearTime.getEpochSecond()));
                Minecraft.getMinecraft().player.sendMessage(textComponent);
            } else if (chatMsg.endsWith("秒後にダンジョン前に戻ります")){
                int timer = 0;
                try{
                    timer = Integer.parseInt(chatMsg.replace("秒後にダンジョン前に戻ります", ""));
                }catch (NumberFormatException exception){
                    //Minecraft.getMinecraft().player.sendMessage(new TextComponentString("[DungeonPvExtension] §cメッセージの分析中にエラーが発生しました。"));
                }
                dpeTimers.add(new DpeTimer(Instant.now(), timer* 1000L, "§d۩"));
            } else if (chatMsg.equals("初期地点に戻ります")){
                dpeTimerDelete("§d۩");
            }

        }
    }
    @SubscribeEvent
    public void onMobDead(RenderLivingEvent.Pre<EntityLiving> e){
        if (e.getEntity().getHealth() == 0){
            if (clearColor(e.getEntity().getDisplayName().getUnformattedText()).equals("幻影")){
                dpeTimerDelete("§c❂");
            }else if(clearColor(e.getEntity().getDisplayName().getUnformattedText()).equals("壊れた雪原の戦闘練習用ロボット")){
                dpeTimerDelete("§c☼");
            }
        }
    }
    @SubscribeEvent
    public void onToolTipRender(ItemTooltipEvent e) {
        if (inDP && ItemUtilities.isWeapon(e.getToolTip()) && e.getItemStack().getTagCompound() != null && e.getItemStack().getTagCompound().hasKey("display")) {
            List<String> newLore = new ArrayList<>();
            boolean nextSub = false;
            boolean customLore = false; //+--------------------+から+--------------------+の間=true
            List<String> oldLore;
            oldLore = e.getToolTip();
            oldLore.set(0, e.getToolTip().get(0) + (isLocked(e.getItemStack()) ? " §a[Locked]" : ""));
            for (String s : oldLore) {
                if (nextSub) {
                    newLore.add(s + " §7(" + e.getItemStack().getTagCompound().getDouble("baseSubStat") + ")");
                    nextSub = false;
                } else if (s.contains("基礎攻撃力") && e.getItemStack().getTagCompound() != null && e.getItemStack().getTagCompound().hasKey("baseAtk")) {
                    newLore.add(s + " §7(" + e.getItemStack().getTagCompound().getInteger("baseAtk") + ")");
                    nextSub = true;
                } else if (clearColor(s).equals("+--------------------+")) {
                    if (customLore && e.getItemStack().getTagCompound() != null && e.getItemStack().getTagCompound().hasKey("internalExp")) {
                        newLore.add("§0");
                        newLore.add(" §f解体入手エッセンス: §a+" + commaSeparate(e.getItemStack().getTagCompound().getInteger("internalExp")));
                        newLore.add(" §f解体入手ゴールド: §a+" + commaSeparate(round(e.getItemStack().getTagCompound().getInteger("internalExp") * 0.6)));
                        customLore = false;
                    } else {
                        customLore = true;
                    }
                    newLore.add(s);
                } else if (!s.equals(e.getItemStack().getDisplayName())) {
                    newLore.add(s);
                }
            }
            if (ArrayUtilities.isStringContainsInList(e.getToolTip(), "強化費係数:")) {
                Integer level = ItemUtilities.getWeaponLevel(e.getItemStack());
                int maxLevel = ItemUtilities.getWeaponLevelMax(e.getItemStack());
                newLore.add("§7 必要コスト/メイン成長値リスト");
                long totalCost = 0L;
                double baseAtk = e.getItemStack().getTagCompound().getInteger("baseAtk");
                for (int i = level; i < maxLevel; i++) {
                    long cost = round((1 + 0.2 * (Math.pow(i, 1.5))) * e.getItemStack().getTagCompound().getInteger("amp") * 100);
                    int nextLevel = i + 1;
                    newLore.add("§7 " + nextLevel + ": " + commaSeparate(cost) + " | " + round(baseAtk * (1 + WEAPON_UPGRADE_CONSTANT * nextLevel)));
                    totalCost += cost;
                }
                newLore.add("§7 合計: " + commaSeparate(totalCost));
            }
            e.getToolTip().clear();
            e.getToolTip().addAll(newLore);

        } else if (inDP && ItemUtilities.isArmor(e.getToolTip()) && e.getItemStack().getTagCompound() != null && e.getItemStack().getTagCompound().hasKey("display")) {
            List<String> newLore = new ArrayList<>();
            boolean customLore = false; //+--------------------+から+--------------------+の間=true
            List<String> oldLore;
            Map<Option, Double> options = new HashMap<>();
            int ignore = 3;
            oldLore = e.getToolTip();
            for (String s : oldLore) {
                if (clearColor(s).equals("+--------------------+")) {
                    if (customLore && e.getItemStack().getTagCompound() != null && e.getItemStack().getTagCompound().hasKey("internalExp")) {
                        newLore.add("§0");
                        newLore.add(" §f解体入手エッセンス: §a+" + commaSeparate(e.getItemStack().getTagCompound().getInteger("internalExp")));
                        newLore.add(" §f解体入手ゴールド: §a+" + commaSeparate(round(e.getItemStack().getTagCompound().getInteger("internalExp") * 0.6)));
                        customLore = false;
                        AtomicReference<Double> normalScore = new AtomicReference<>(0d);
                        AtomicReference<Double> hpScore = new AtomicReference<>(0d);
                        options.forEach((o, d) -> {
                            if (o.equals(Option.ATK_PERCENTAGE)) {
                                normalScore.updateAndGet(v -> v + d);
                                hpScore.updateAndGet(v -> v + d);
                            } else if (o.equals(Option.HP_PERCENTAGE)) {
                                hpScore.updateAndGet(v -> v + d);
                            } else if (o.equals(Option.CRITICAL_CHANCE)) {
                                normalScore.updateAndGet(v -> v + d * 2d);
                                hpScore.updateAndGet(v -> v + d * 2d);
                            } else if (o.equals(Option.CRITICAL_DAMAGE)) {
                                normalScore.updateAndGet(v -> v + d);
                                hpScore.updateAndGet(v -> v + d);
                            }
                        });
                        newLore.add(" §f汎用スコア: §a" + ((double) Math.round(normalScore.get() * 10)) / 10);
                        newLore.add(" §fHPスコア: §a" + ((double) Math.round(hpScore.get() * 10)) / 10);
                    } else {
                        customLore = true;
                    }
                } else if (customLore) {
                    if (ignore > 0) {
                        ignore--;
                    } else {
                        for (Option o : Option.values()) {
                            if (clearColor(s).contains(o.icon)) {
                                if (clearColor(s).contains("%") && o.name.contains("%")) {
                                    if (options.containsKey(o)) {
                                        options.replace(o, options.get(o) + Double.parseDouble(clearColor(s).split(" ")[3].replace("+", "").replace("%", "")));
                                    } else {
                                        options.put(o, Double.parseDouble(clearColor(s).split(" ")[3].replace("+", "").replace("%", "")));
                                    }
                                } else if (!clearColor(s).contains("%") && !o.name.contains("%")) {
                                    if (options.containsKey(o)) {
                                        options.replace(o, options.get(o) + Double.parseDouble(clearColor(s).split(" ")[3].replace("+", "")));
                                    } else {
                                        options.put(o, Double.parseDouble(clearColor(s).split(" ")[3].replace("+", "")));
                                    }
                                }
                            }
                        }
                    }
                }
                newLore.add(s);
            }

            if (HudUtilities.getCurrentGuiTitle().equals("防具強化")) {
                Integer level = ItemUtilities.getArmorLevel(e.getItemStack());
                int maxLevel = ItemUtilities.getArmorLevelMax(e.getItemStack());
                long totalCost = 0L;
                double baseStat = e.getItemStack().getTagCompound().getInteger("mainStat");
                newLore.add("§7必要コスト/メイン成長値リスト");
                for (int i = level; i < maxLevel; i++) {
                    long cost = round(100 * (1 + 0.2 * (Math.pow(i, 2))) * maxLevel * 0.25);
                    int nextLevel = i + 1;
                    newLore.add("§7" + nextLevel + ": " + commaSeparate(cost) + " | " + round(baseStat * (1 + ARMOR_UPGRADE_CONSTANT * nextLevel)));
                    totalCost += cost;
                }
                newLore.add("§7合計: " + commaSeparate(totalCost));
            }
            e.getToolTip().clear();
            e.getToolTip().addAll(newLore);
        } else if (inDP && ItemUtilities.isScrap(e.getToolTip()) && e.getItemStack().getTagCompound() != null && e.getItemStack().getTagCompound().hasKey("display")) {
            List<String> newLore = new ArrayList<>();
            List<String> oldLore;
            oldLore = e.getToolTip();
            for (String s : oldLore) {
                if (clearColor(s).contains("解体アイテム")) {
                    newLore.add(s);
                    if (e.getItemStack().getTagCompound().hasKey("sellEssence")) {
                        newLore.add("§f解体時入手エッセンス: §a+" + commaSeparate(e.getItemStack().getTagCompound().getInteger("sellEssence") * e.getItemStack().getCount()) + " §7(@" + commaSeparate(e.getItemStack().getTagCompound().getInteger("sellEssence")) + ")");
                    }
                    if (e.getItemStack().getTagCompound().hasKey("sellGold")) {
                        newLore.add("§f解体時入手ゴールド: §e+" + commaSeparate(e.getItemStack().getTagCompound().getInteger("sellGold") * e.getItemStack().getCount()) + " §7(@" + commaSeparate(e.getItemStack().getTagCompound().getInteger("sellGold")) + ")");
                    }
                } else {
                    newLore.add(s);
                }
            }
            e.getToolTip().clear();
            e.getToolTip().addAll(newLore);
        } else if (inDP && HudUtilities.getCurrentGuiTitle().equals("アイテム倉庫") && e.getToolTip().get(0).contains("§fアイテム倉庫 §a")){
            String slot = clearColor(e.getItemStack().getDisplayName()).replace("アイテム倉庫 ", "");
            if (storageName.get(0).getAsJsonObject().has(slot)){
                e.getToolTip().set(0, e.getToolTip().get(0).replace("§fアイテム倉庫 §a" + slot, storageName.get(0).getAsJsonObject().get(slot).getAsString()));
            }
            if (e.getFlags().isAdvanced()){
                String buffer = null;
                List<String> newLore = new ArrayList<>();
                for(String s : e.getToolTip()){
                    newLore.add(s);
                    if(s.equals(e.getToolTip().get(0))){
                        newLore.add("§7StorageSlot " + slot);
                    }
                }
                e.getToolTip().clear();
                e.getToolTip().addAll(newLore);
            }
        }
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
            if (inDP && dpeTimers.size() != 0) {
                dpeTimers.forEach(dpeTimer -> actionBarAddText.set(dpeTimer.getConcatSecond(actionBarAddText.get())));
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
                if (healthChatCooldown != null){
                    if (!(healthChatCooldown.getEpochSecond() < Instant.now().getEpochSecond())){
                        return;
                    }
                }
                healthChatCooldown = Instant.now();
                Minecraft.getMinecraft().player.sendChatMessage("❤ " + HudUtilities.getHealth() + "/" + HudUtilities.getMaxHealth());
            }
        }
    }
}
