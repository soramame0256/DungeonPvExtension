package com.github.soramame0256.dungeonpvextension.listener;

import com.github.soramame0256.dungeonpvextension.DungeonPvExtension;
import com.github.soramame0256.dungeonpvextension.utils.HudUtilities;
import com.github.soramame0256.dungeonpvextension.utils.ItemUtilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.nbt.NBTBase;
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
import java.util.List;

import static com.github.soramame0256.dungeonpvextension.DungeonPvExtension.inDP;
import static com.github.soramame0256.dungeonpvextension.utils.StringUtilities.clearColor;

public class EventListener {
    public static Instant potCooldownStarts;
    public static final long POT_COOLDOWN = 3000;
    public static Boolean isPotCooldown = false;
    public EventListener() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    @SubscribeEvent
    public void onUpdate(TickEvent.ClientTickEvent e){
        if(e.phase == TickEvent.Phase.END){
            if(isPotCooldown && inDP){
                if(potCooldownStarts.toEpochMilli() + POT_COOLDOWN < Instant.now().toEpochMilli()){
                    isPotCooldown = false;
                    System.out.println(String.valueOf(potCooldownStarts.toEpochMilli()) + false);
                }

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
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onToolTipRender(ItemTooltipEvent e){
        if (inDP && ItemUtilities.isWeapon(e.getToolTip()) && !ItemUtilities.isModded(e.getItemStack()) && e.getItemStack().getTagCompound() != null && e.getItemStack().getTagCompound().hasKey("display")){
            List<String> newLore = new ArrayList<>();
            Boolean nextSub = false;
            Boolean customLore = false; //+--------------------+から+--------------------+の間=true
            List<String> oldLore = new ArrayList<>();
            for (NBTBase a : e.getItemStack().getSubCompound("display").getTagList("Lore",8)){
                oldLore.add(a.toString().substring(1,a.toString().length()-1));
            }
            for (String s : oldLore) {
                if(nextSub){
                    newLore.add(s + " §7("  + e.getItemStack().getTagCompound().getInteger("baseSubStat") + ")");
                    nextSub = false;
                } else if(s.contains("基礎攻撃力") && e.getItemStack().getTagCompound() != null && e.getItemStack().getTagCompound().hasKey("baseAtk")){
                    newLore.add(s + " §7(" + e.getItemStack().getTagCompound().getInteger("baseAtk") + ")");
                    nextSub = true;
                } else if (clearColor(s).equals("+--------------------+")){
                    if (customLore && e.getItemStack().getTagCompound() != null && e.getItemStack().getTagCompound().hasKey("internalExp")){
                        newLore.add("§0");
                        newLore.add(" §f解体入手エッセンス: §a+" + e.getItemStack().getTagCompound().getInteger("internalExp"));
                        customLore = false;
                    }else{
                        customLore = true;
                    }
                    newLore.add(s);
                } else if (!s.equals(e.getItemStack().getDisplayName())) {
                    newLore.add(s);
                }
            }
            ItemUtilities.changeLore(e.getItemStack(), newLore);
        }else if (inDP && ItemUtilities.isArmor(e.getToolTip()) && !ItemUtilities.isModded(e.getItemStack()) && e.getItemStack().getTagCompound() != null && e.getItemStack().getTagCompound().hasKey("display")){
            List<String> newLore = new ArrayList<>();
            Boolean customLore = false; //+--------------------+から+--------------------+の間=true
            List<String> oldLore = new ArrayList<>();
            for (NBTBase a : e.getItemStack().getSubCompound("display").getTagList("Lore",8)){
                oldLore.add(a.toString().substring(1,a.toString().length()-1));
            }
            for (String s : oldLore) {
                if(clearColor(s).equals("+--------------------+")){
                    if (customLore && e.getItemStack().getTagCompound() != null && e.getItemStack().getTagCompound().hasKey("internalExp")){
                        newLore.add("§0");
                        newLore.add(" §f解体入手エッセンス: §a+" + e.getItemStack().getTagCompound().getInteger("internalExp"));
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
        }
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
    }
}
