package com.github.soramame0256.dungeonpvextension.commands;

import com.github.soramame0256.dungeonpvextension.listener.EventListener;
import com.github.soramame0256.dungeonpvextension.utils.DataUtils;
import com.google.gson.JsonArray;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.IOException;

import static com.github.soramame0256.dungeonpvextension.DungeonPvExtension.getDataUtil;

public class QuickChatChangeMsgCmd extends DpeCmdBase{

    @Override
    public String getName() {
        return "quickchatmessage";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/quickchatmessage <slot> <メッセージ>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        DataUtils dataUtil = getDataUtil();
        JsonArray ja = dataUtil.getJsonArrayData("QuickChat");
        if (args.length == 0){
            ITextComponent textComponent = new TextComponentString(getUsage(sender));
            textComponent.appendText("\n設定中のメッセージ");
            for (int i = 1; i< 6; i++){
                textComponent.appendText("\n" + i + ": " +(ja.get(0).getAsJsonObject().has(String.valueOf(i)) ? ja.get(0).getAsJsonObject().get(String.valueOf(i)).getAsString() : "設定されていません"));
            }
            Minecraft.getMinecraft().player.sendMessage(textComponent);
            return;
        }
        int slot = 0;
        try {
            slot = Integer.parseInt(args[0]);
        }catch(NumberFormatException e){
            Minecraft.getMinecraft().player.sendMessage(new TextComponentString("§cスロットは整数である必要があります。"));
        }
        if(args.length >=2) {
            String message = args[1];
            if (args.length >= 3) {
                for (int i = 2; i < args.length; i++) {
                    message = message.concat(" " + args[i]);
                }
            }
            ja.get(0).getAsJsonObject().addProperty(String.valueOf(slot), message);
            dataUtil.saveJsonData("QuickChat", ja);
            Minecraft.getMinecraft().player.sendMessage(new TextComponentString("§aスロット §6" + slot + " §aのメッセージを " + message + "に設定しました。"));
        }else{
            if(ja.get(0).getAsJsonObject().has(String.valueOf(slot))) {
                ja.get(0).getAsJsonObject().remove(String.valueOf(slot));
            }
            dataUtil.saveJsonData("QuickChat", ja);
            Minecraft.getMinecraft().player.sendMessage(new TextComponentString("§aスロット §6" + slot + " §aに設定されたメッセージを削除しました。"));
        }
    }
}

