package com.github.soramame0256.dungeonpvextension.listener;

import com.github.soramame0256.dungeonpvextension.DungeonPvExtension;
import com.github.soramame0256.dungeonpvextension.api.Character;
import com.github.soramame0256.dungeonpvextension.api.Option;
import com.github.soramame0256.dungeonpvextension.utils.ArrayUtilities;
import com.github.soramame0256.dungeonpvextension.utils.DataUtils;
import com.github.soramame0256.dungeonpvextension.utils.HudUtilities;
import com.github.soramame0256.dungeonpvextension.utils.ItemUtilities;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
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

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.soramame0256.dungeonpvextension.DungeonPvExtension.CONFIG_TYPES.disableIds;
import static com.github.soramame0256.dungeonpvextension.DungeonPvExtension.*;
import static com.github.soramame0256.dungeonpvextension.utils.ItemUtilities.*;
import static com.github.soramame0256.dungeonpvextension.utils.NumberUtilities.commaSeparate;
import static com.github.soramame0256.dungeonpvextension.utils.NumberUtilities.toTime;
import static com.github.soramame0256.dungeonpvextension.utils.StringUtilities.clearColor;
import static java.lang.Math.round;

public class EventListener {
    public static Instant potCooldownStarts;
    public static final long POT_COOLDOWN = 3000;
    public static boolean isPotCooldown = false;
    public static boolean isAutoDieEnabled = false;
    public static List<DpeTimer> dpeTimers = new ArrayList<>();
    private static final double WEAPON_UPGRADE_CONSTANT = 0.15d;
    private static final double ARMOR_UPGRADE_CONSTANT = 1 / 3d;
    private static final int WEAPON_SUB_STATUS_FACTOR = 3;
    private static Instant healthChatCooldown = null;
    private static Instant dungeonClearTime = null;
    public static boolean isHealthShowFeatureEnabled = false;
    private static JsonArray storageName;
    private static Instant quickChatCooldown = null;
    private static final Pattern CLEAR_TIME_DISPLAY = Pattern.compile(". 最速クリアタイム: (?<sec>.*)秒");
    private static final Pattern CLEAR_TIME_DISPLAY_ON_CLEAR = Pattern.compile("クリアタイム: (?<sec>.*)秒.*");
    private static final ResourceLocation RESOURCE_LOCATION_CIRCLE = new ResourceLocation(MOD_ID, "textures/circle.png");
    private static final Map<String, List<String>> memo = new HashMap<>();
    private static final double WEAPON_SUB_STATUS_MAXIMUM = 0.18d;
    private static final double WEAPON_SUB_STATUS_MINIMUM = 0.12d;

    //private static final Toast toast = new Toast();
    public EventListener() {
        MinecraftForge.EVENT_BUS.register(this);
        try {
            reload();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void reload() throws IOException {
        DataUtils dataUtils = getDataUtil();
        storageName = dataUtils.getJsonArrayData("StorageNames");
        memo.clear();
        List<String> memos;
        if(dataUtils.getRootJson().has("ItemMemo")) {
            for (Map.Entry<String, JsonElement> itemMemo : dataUtils.getRootJson().get("ItemMemo").getAsJsonObject().entrySet()) {
                memos = new ArrayList<>();
                for (JsonElement jsonElement : itemMemo.getValue().getAsJsonArray()) {
                    memos.add(jsonElement.getAsString());
                }
                memo.put(itemMemo.getKey(), memos);
            }
        }

    }

    @SubscribeEvent
    public void onUpdate(TickEvent.ClientTickEvent e) {
        if (e.phase == TickEvent.Phase.END && Minecraft.getMinecraft().player != null) {
            ScoreObjective so = Minecraft.getMinecraft().player.getWorldScoreboard().getObjectiveInDisplaySlot(1);
            if (isEnable && inDP && so != null && so.getDisplayName().contains("Dungeon PvE") && !ArrayUtilities.isContain(disableIds, Minecraft.getMinecraft().player.getDisplayName().getUnformattedText())) {
                if (isPotCooldown && potCooldownStarts.toEpochMilli() + POT_COOLDOWN < Instant.now().toEpochMilli()) {
                    isPotCooldown = false;
                    System.out.println(String.valueOf(potCooldownStarts.toEpochMilli()) + false);
                }
                if (dpeTimers.size() != 0) {
                    AtomicReference<Integer> amount = new AtomicReference<>(0);
                    dpeTimers.forEach(dpeTimer -> {
                        if (dpeTimer.update()) {
                            amount.set(amount.get() + 1);
                        }
                    });
                    if (amount.get() > 0) {
                        dpeTimers.subList(0, amount.get()).clear();
                    }
                }
            } else if (so != null && isEnable && !ArrayUtilities.isContain(disableIds, Minecraft.getMinecraft().player.getDisplayName().getUnformattedText())) {
                inDP = so.getDisplayName().contains("Dungeon PvE");
            } else if (!isEnable || ArrayUtilities.isContain(disableIds, Minecraft.getMinecraft().player.getDisplayName().getUnformattedText())) {
                inDP = false;
            }
        }
    }

    public void dpeTimerDelete(String icon) {
        DpeTimer dpeTimer1 = null;
        for (DpeTimer dpeTimer : dpeTimers) {
            if (dpeTimer.getIcon().equals(icon)) {
                dpeTimer1 = dpeTimer;
                break;
            }
        }
        if (dpeTimer1 != null) {
            dpeTimer1.delete();
        }
    }

    public static void initializeDungeonItemViewer() {
        DataUtils dataUtils = getDataUtil();
        JsonArray screenRendering;
        JsonObject dungeonItemViewer;
        JsonObject rootJson = dataUtils.getRootJson();
        if (!dataUtils.getRootJson().has("ScreenRendering")) {
            screenRendering = new JsonArray();
            dungeonItemViewer = new JsonObject();
            JsonObject jsonObjectTemp = new JsonObject();
            dungeonItemViewer.addProperty("height", 0d);
            dungeonItemViewer.addProperty("width", 0d);
            dungeonItemViewer.addProperty("active", true);
            jsonObjectTemp.add("DungeonItemViewer", dungeonItemViewer);
            screenRendering.add(jsonObjectTemp);
            rootJson.add("ScreenRendering", screenRendering);
            try {
                dataUtils.flush();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @SubscribeEvent
    public void onScreenRender(RenderGameOverlayEvent e) {
        if (e.getType() == RenderGameOverlayEvent.ElementType.ALL  && inDP) {
            DataUtils dataUtils = getDataUtil();
            int height = e.getResolution().getScaledHeight();
            int width = e.getResolution().getScaledWidth();
            double heightPercent, widthPercent;
            initializeDungeonItemViewer();
            heightPercent = dataUtils.getJsonArrayData("ScreenRendering").get(0).getAsJsonObject().get("DungeonItemViewer").getAsJsonObject().get("height").getAsDouble();
            widthPercent = dataUtils.getJsonArrayData("ScreenRendering").get(0).getAsJsonObject().get("DungeonItemViewer").getAsJsonObject().get("width").getAsDouble();
            int screenRenderingHeight = (int) (height * heightPercent);
            int screenRenderingWidth = (int) (width * widthPercent);
            if (!dataUtils.getJsonArrayData("ScreenRendering").get(0).getAsJsonObject().get("DungeonItemViewer").getAsJsonObject().get("active").getAsBoolean()) {
                return;
            }
            ScoreObjective so = Minecraft.getMinecraft().world.getScoreboard().getObjectiveInDisplaySlot(1);
            if (so != null) {
                AtomicReference<Float> charaChargePercent1 = new AtomicReference<>(-1f);
                AtomicReference<Float> charaChargePercent2 = new AtomicReference<>(-1f);
                AtomicReference<Float> charaCharge1 = new AtomicReference<>(-1f);
                AtomicReference<Float> charaCharge2 = new AtomicReference<>(-1f);
                if (inDP) so.getScoreboard().getTeams().forEach(a -> {
                    String str;
                    if (a.getPrefix().contains("✴")) {
                        str = clearColor(a.getSuffix().replaceAll("\\|", "")).trim();
                        if (str.matches("[0-9]+(\\.[0-9])?")) {
                            if (a.getName().equals("line13")) {
                                charaCharge1.set(Float.parseFloat(str));
                            } else {
                                charaCharge2.set(Float.parseFloat(str));
                            }
                        }
                    }
                });
                if (inDP) so.getScoreboard().getTeams().forEach(a ->{
                    String str;
                    str = clearColor(a.getPrefix() + a.getSuffix()).trim();
                    if (str.matches("\\[Lv[0-9]?[0-9]] .*")) {
                        for (Character character : Character.values()) {
                            if (str.split(" ")[1].equals(character.name) || (character == Character.TRAVELER && str.split(" ")[1].equals(Minecraft.getMinecraft().player.getName()))) {
                                if (a.getName().equals("line14")) {
                                    charaChargePercent1.set(charaCharge1.get() / character.chargeMax * 100);
                                } else {
                                    charaChargePercent2.set(charaCharge2.get() / character.chargeMax * 100);
                                }
                                break;
                            }
                        }
                    }
                }
                );
                if (inDP) so.getScoreboard().getTeams().forEach(a -> {
                    if (a.getPrefix().contains("✴") && !clearColor(a.getSuffix()).contains("%")) {
                        if (a.getName().equals("line13")) {
                            a.setSuffix(a.getSuffix().replace(charaCharge1.get().toString().replace(".0", ""), charaChargePercent1.get().intValue() + "%"));
                        }else if (a.getName().equals("line11")) {
                            a.setSuffix(a.getSuffix().replace(charaCharge2.get().toString().replace(".0",""),charaChargePercent2.get().intValue() + "%"));
                        }
                    }
                });
            }
            Map<String, Integer> pots = new HashMap<>();
            for (ItemStack is : Minecraft.getMinecraft().player.inventory.mainInventory) {
                if (!isDungeonItem(Arrays.asList(getLore(is)))) {
                    if(isPotItem(Arrays.asList(getLore(is)))){
                        pots.put(is.getDisplayName(), pots.getOrDefault(is.getDisplayName(),0)+1);
                        continue;
                    }else{
                        continue;
                    }
                }
                Minecraft.getMinecraft().fontRenderer.drawString(is.getDisplayName() + "×" + is.getCount(), screenRenderingWidth, screenRenderingHeight, 0);
                screenRenderingHeight += 10;
            }
            if(!pots.isEmpty()){
                for(Map.Entry<String, Integer> ent : pots.entrySet()){
                    Minecraft.getMinecraft().fontRenderer.drawString(ent.getKey() + "×" + ent.getValue(), screenRenderingWidth, screenRenderingHeight, 0);
                    screenRenderingHeight += 10;
                }
            }
            if(isHealthShowFeatureEnabled) {
                int x = 1;
                int y = 100;
                int cnt = 0;
                if (Minecraft.getMinecraft().getConnection() != null) {
                    for (EntityPlayer p : Minecraft.getMinecraft().world.playerEntities) {
                        if (!p.getDisplayNameString().matches("([a-zA-Z_0-9]{3,16})")) continue;
                        float healthPercent = p.getHealth() / p.getMaxHealth();
                        int healthP = (int) (healthPercent * 100);
                        //Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation(MOD_ID,"textures/gauge.png"));
                        //GlStateManager.tex
                        GlStateManager.popMatrix();
                        GlStateManager.enableAlpha();
                        GlStateManager.enableBlend();
                        Minecraft.getMinecraft().fontRenderer.drawString(p.getDisplayNameString(), x, y, 0xffffff);
                        y += Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;
                        Gui.drawRect(x, y, x + 100, y + Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT, 0xffff0000);
                        Gui.drawRect(x + 100 - healthP, y, x + 100, y + Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT, 0xff00ff00);
                        Minecraft.getMinecraft().fontRenderer.drawString(((float) round(healthPercent * 1000)) / 10 + "%", x + 10, y, 0xffffff);
                        y += Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;
                        GlStateManager.disableAlpha();
                        GlStateManager.disableBlend();
                        GlStateManager.pushMatrix();
                        cnt++;
                        if (cnt == 5) break;
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onChatReceived(ClientChatReceivedEvent e) {
        String chatMsg = clearColor(e.getMessage().getUnformattedText());
        if (!isUpToDate && (chatMsg.equals("サーバーに関する重要なお知らせなどはDiscordで行っているため、Discordに参加することを推奨しています。") || chatMsg.equals("We encourage you to join Discord because we have important server-related announcements on Discord."))) {
            Minecraft.getMinecraft().player.sendMessage(new TextComponentString("DungeonPvExtensionの最新バージョンが存在します! /dpeupdateで更新できます。"));
        }
        if (inDP) {
            Matcher mat;
            //&1[♥ +114&1]
            if (chatMsg.startsWith("[❤ +") && chatMsg.endsWith("]")) {
                potCooldownStarts = Instant.now();
                isPotCooldown = true;
                System.out.println(String.valueOf(potCooldownStarts.toEpochMilli()) + true);
            } else if (chatMsg.equals("60秒以内に爆弾を解除(討伐)せよ！")) {
                dpeTimers.add(new DpeTimer(Instant.now(), 60000L, "§c☢"));
            } else if (chatMsg.equals("爆弾の解除に成功した！")) {
                dpeTimerDelete("§c☢");
            } else if (chatMsg.equals("ロボットのHPを0にしてバクハツを止めろ！")) {
                dpeTimers.add(new DpeTimer(Instant.now(), 8000L, "§c☼"));
            } else if (chatMsg.equals("[天国の番人] いでよ幻影！")) {
                dpeTimers.add(new DpeTimer(Instant.now(), 60000L, "§c❂"));
            } else if (chatMsg.equals("[煉獄の支配者] 個々の力を見せてみろ！") || chatMsg.equals("[❁ 煉獄の支配者] 個々の力を見せてみろ！")) {
                dpeTimers.add(new DpeTimer(Instant.now(), 30000L, "§c۞"));
            } else if (chatMsg.equals("機械の討伐に成功した！")) {
                dpeTimerDelete("§c۞");
            } else if (chatMsg.equals("誰かが世界樹の心臓部にテレポートされた。")) {
                dpeTimers.add(new DpeTimer(Instant.now(), 30000L, "§c✿"));
            } else if (chatMsg.equals("心臓部からの脱出に成功した！")) {
                dpeTimerDelete("§c✿");
            } else if (chatMsg.equals("\u22D9 ダンジョンに転移します.. ")) {
                dungeonClearTime = Instant.now();
            } else if (chatMsg.equals("8秒後にテレポートされます")) {
                ITextComponent textComponent = new TextComponentString("クリアタイム: " + toTime(Instant.now().getEpochSecond() - dungeonClearTime.getEpochSecond()));
                Minecraft.getMinecraft().player.sendMessage(textComponent);
            } else if (chatMsg.endsWith("秒後にダンジョン前に戻ります")) {
                int timer = 0;
                try {
                    timer = Integer.parseInt(chatMsg.replace("秒後にダンジョン前に戻ります", ""));
                } catch (NumberFormatException exception) {
                    //Minecraft.getMinecraft().player.sendMessage(new TextComponentString("[DungeonPvExtension] §cメッセージの分析中にエラーが発生しました。"));
                }
                dpeTimers.add(new DpeTimer(Instant.now(), timer * 1000L, "§d۩"));
            } else if (chatMsg.equals("初期地点に戻ります")) {
                dpeTimerDelete("§d۩");
            } else if ((mat = CLEAR_TIME_DISPLAY.matcher(chatMsg)).matches()){
                String strSec = mat.group("sec");
                long sec = (long)Double.parseDouble(strSec);
                double doubleSec = Double.parseDouble(strSec);
                long ms = Math.round((doubleSec-sec)*100);
                System.out.println(doubleSec);
                System.out.println(sec);
                System.out.println(doubleSec-sec);
                System.out.println(ms);
                System.out.println(toTime(sec));
                String time = toTime(sec) + (ms==0 ? "" : ms);
                time = time.
                        replaceAll("h","時間").
                        replaceAll("m","分").
                        replaceAll("s","秒");
                e.setMessage(new TextComponentString(e.getMessage().getFormattedText().replaceAll(strSec+"秒",time)));
            } else if((mat = CLEAR_TIME_DISPLAY_ON_CLEAR.matcher(chatMsg)).matches()){
                String strSec = mat.group("sec");
                long sec = (long)Double.parseDouble(strSec);
                double doubleSec = Double.parseDouble(strSec);
                long ms = Math.round((doubleSec-sec)*100);
                String time = toTime(sec) + (ms==0 ? "" : ms);
                time = time.
                        replaceAll("h","時間").
                        replaceAll("m","分").
                        replaceAll("s","秒");
                e.setMessage(new TextComponentString(e.getMessage().getFormattedText().replaceAll(strSec+"秒",time)));
            } else if((chatMsg.equals("[黒ニ染マリシ者] 黒は万物の色...消えるはずがないのに...") && isAutoDieEnabled)){
                new Thread(()->{
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                    Minecraft.getMinecraft().player.sendChatMessage("/die");
                }).start();
            }

        }
    }

    @SubscribeEvent
    public void onMobDead(RenderLivingEvent.Pre<EntityLiving> e) {
        if (e.getEntity().getHealth() == 0) {
            if (clearColor(e.getEntity().getDisplayName().getUnformattedText()).equals("幻影")) {
                dpeTimerDelete("§c❂");
            } else if (clearColor(e.getEntity().getDisplayName().getUnformattedText()).equals("壊れた雪原の戦闘練習用ロボット")) {
                dpeTimerDelete("§c☼");
            }
        }
    }
    @SubscribeEvent
    public void onInventoryClick(GuiScreenEvent.MouseInputEvent e) {
//        Mouse.isButtonDown(2); //Middle Click
        if (e.getGui() instanceof GuiChest) {
            boolean toCancel = hasToCancelOnClick(e.getGui(), ((GuiChest) e.getGui()).getSlotUnderMouse());
            e.setCanceled(toCancel);
        }
    }
    @SubscribeEvent
    public void onInventoryKeyInput(GuiScreenEvent.KeyboardInputEvent e){
        if (e.getGui() instanceof GuiChest){
            boolean toCancel = hasToCancelOnClick(e.getGui(),((GuiChest) e.getGui()).getSlotUnderMouse());
            e.setCanceled(toCancel);
        }
    }
    private boolean hasToCancelOnClick(GuiScreen gui,Slot slotIn){
        if (gui instanceof GuiChest) {
            // This method is called every time you click in a GuiContainer but slotIn will be null if you click outside of any slots
            if (slotIn != null && slotIn.getStack() != null && HudUtilities.getCurrentGuiTitle().equals("解体したい防具・武器を入れてください")) {
                try {
                    MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
                    ItemStack clickedItem = slotIn.getStack();
                    DataUtils dataUtils = new DataUtils("itemlock.json");
                    if (clickedItem.getTagCompound() != null) {
                        if (dataUtils.getBooleanData(String.format("%040x", new BigInteger(1, sha256.digest(clickedItem.getTagCompound().toString().getBytes()))))) {
                            return true;
                        }
                    }
                    if (clearColor(slotIn.getStack().getDisplayName()).contains("解体を実行")){
                        if(Minecraft.getMinecraft().player.openContainer instanceof ContainerChest) {
                            for (int i=0;i<=7;i++){
                                ItemStack is = ((ContainerChest) Minecraft.getMinecraft().player.openContainer).getLowerChestInventory().getStackInSlot(i);
                                if (is.getTagCompound() != null) {
                                    if (dataUtils.getBooleanData(String.format("%040x", new BigInteger(1, sha256.digest(is.getTagCompound().toString().getBytes()))))) {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                    ;
                    // Do stuff here
                } catch (NoSuchAlgorithmException | IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return false;
    }
    @SubscribeEvent
    public void onToolTipRender(ItemTooltipEvent e) {
        if (inDP && ItemUtilities.isWeapon(e.getToolTip()) && e.getItemStack().getTagCompound() != null && e.getItemStack().getTagCompound().hasKey("display")) {
            List<String> newLore = new ArrayList<>();
            boolean nextSub = false;
            boolean customLore = false; //+--------------------+から+--------------------+の間=true
            boolean printId = false;
            double subStat = 0;
            double subStatGrowth;
            double subStatGrowthPercent;
            List<String> oldLore;
            oldLore = e.getToolTip();
            oldLore.set(0, e.getToolTip().get(0) + (isLocked(e.getItemStack()) ? " §a[Locked]" : ""));
            for (String s : oldLore) {
                if (nextSub & e.getItemStack().getTagCompound().hasKey("baseSubStat")) {
                    newLore.add(s + " §7(" + getBaseSubStatus(e.getItemStack()) + ")");
                    for (Option o : Option.values()) {
                        if (clearColor(s).contains(o.icon)) {
                            if (clearColor(s).contains("%") && o.name.contains("%")) {
                                subStat = Double.parseDouble(clearColor(s).split(" ")[3].replace("+", "").replace("%", ""));
                            } else if (!clearColor(s).contains("%") && !o.name.contains("%")) {
                                subStat = Double.parseDouble(clearColor(s).split(" ")[3].replace("+", ""));
                            }
                        }
                    }
                    subStatGrowth = subStat-getBaseSubStatus(e.getItemStack());
                    subStatGrowthPercent = (double)(round(subStatGrowth * 100 / getBaseSubStatus(e.getItemStack()))) /100d;
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
                        printId = true;
                    } else {
                        customLore = true;
                    }
                    newLore.add(s);
                    if(printId && e.getFlags().isAdvanced()) newLore.add("§8dungeonpve:" + getId(e.getItemStack()));
                    printId = false;
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
                newLore.add("§7サブステータス振れ幅");
                int maxGrowthCount = maxLevel / WEAPON_SUB_STATUS_FACTOR;
                int currentGrowthCount = level / WEAPON_SUB_STATUS_FACTOR;
                double min=subStat,max=subStat;
                for (int i=Math.min(currentGrowthCount+1,maxGrowthCount); i<=maxGrowthCount;i++){
                    newLore.add("§7 " + i*WEAPON_SUB_STATUS_FACTOR + ": " + round(10*(min=(min+getBaseSubStatus(e.getItemStack())*WEAPON_SUB_STATUS_MINIMUM)))/10d + "~" + round(10*(max=(max+getBaseSubStatus(e.getItemStack())*WEAPON_SUB_STATUS_MAXIMUM)))/10d);
                }
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
                if(s.matches("§8minecraft:.*")) newLore.add("§8dungeonpve:" + getId(e.getItemStack()));
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
        String hash = itemToHash(e.getItemStack());
        if(inDP && memo.containsKey(hash) && !memo.get(hash).isEmpty()){
            e.getToolTip().add("-----memo-----");
            for (int i = 0; i < memo.get(hash).size(); i++) {
                e.getToolTip().add(i+1 + ": " + memo.get(hash).get(i));
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
            for (int i = 3; i< 8; i++){
                if (keyBindings[i].isPressed()){
                    if (quickChatCooldown != null){
                        if (!(quickChatCooldown.getEpochSecond() < Instant.now().getEpochSecond() - 3)){
                            return;
                        }
                    }
                    quickChatCooldown = Instant.now();
                    DataUtils dataUtils = getDataUtil();
                    if(!dataUtils.getRootJson().has("QuickChat") || !dataUtils.getJsonArrayData("QuickChat").get(0).getAsJsonObject().has(String.valueOf(i-2))){
                        Minecraft.getMinecraft().player.sendMessage(new TextComponentString("§cQuickChatの使用には事前に /quickchatmessage で設定する必要があります。"));
                        return;
                    }
                    String message = dataUtils.getJsonArrayData("QuickChat").get(0).getAsJsonObject().get(String.valueOf(i-2)).getAsString();
                    Minecraft.getMinecraft().player.sendChatMessage(message);
                    return;
                }
            }
        }
    }
}
