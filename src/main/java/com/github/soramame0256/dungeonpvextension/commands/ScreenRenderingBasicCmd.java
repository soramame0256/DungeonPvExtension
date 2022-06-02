package com.github.soramame0256.dungeonpvextension.commands;

import com.github.soramame0256.dungeonpvextension.utils.ArrayUtilities;
import com.github.soramame0256.dungeonpvextension.utils.DataUtils;
import com.github.soramame0256.dungeonpvextension.utils.NumberUtilities;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import java.io.IOException;

import static com.github.soramame0256.dungeonpvextension.DungeonPvExtension.getDataUtil;
import static com.github.soramame0256.dungeonpvextension.listener.EventListener.initializeDungeonItemViewer;

public class ScreenRenderingBasicCmd extends DpeCmdBase{

    @Override
    public String getName() {
        return "screenconfig";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/screenconfig [dungeonItem] [<on/off/x/y>] [<Number>]";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        DataUtils dataUtils = getDataUtil();
        initializeDungeonItemViewer();
        JsonObject dungeonItemViewerConfig = dataUtils.getJsonArrayData("ScreenRendering").get(0).getAsJsonObject().get("DungeonItemViewer").getAsJsonObject();
        if(args.length == 0){
            Minecraft.getMinecraft().player.sendMessage(new TextComponentString(getUsage(sender)));
        }else if (args.length == 1 && args[0].equals("dungeonItem")){
            ITextComponent textComponent = new TextComponentString("機能: " + (dungeonItemViewerConfig.get("active").getAsBoolean() ? "有効" : "無効"));
            textComponent.appendText("x座標(割合) : " + dungeonItemViewerConfig.get("width"));
            textComponent.appendText("y座標(割合) : " + dungeonItemViewerConfig.get("height"));
            Minecraft.getMinecraft().player.sendMessage(textComponent);
        }else if (args.length == 2 && args[0].equals("dungeonItem")){
            if(ArrayUtilities.isContain(new String[]{"on", "true", "false", "off"}, args[1])){
                dungeonItemViewerConfig.addProperty("active", ArrayUtilities.isContain(new String[]{"on", "true"}, args[1]));
                Minecraft.getMinecraft().player.sendMessage(new TextComponentString(ArrayUtilities.isContain(new String[]{"on", "true"}, args[1]) ? "§a機能を有効化しました" : "§c機能を無効化しました"));
            }else{
                Minecraft.getMinecraft().player.sendMessage(new TextComponentString(getUsage(sender)));
            }
        }else if (args.length == 3 && args[0].equals("dungeonItem")){
            if (args[1].equals("x")){
                if(NumberUtilities.canParse(args[2])){
                    dungeonItemViewerConfig.addProperty("width", Double.parseDouble(args[2]));
                    Minecraft.getMinecraft().player.sendMessage(new TextComponentString("§ax座標を更新しました。"));
                }else{
                    Minecraft.getMinecraft().player.sendMessage(new TextComponentString("§c入力された文字列は数値に変換することができない文字が含まれています。"));
                }
            }else if (args[1].equals("y")){
                if(NumberUtilities.canParse(args[2])){
                    dungeonItemViewerConfig.addProperty("height", Double.parseDouble(args[2]));
                    Minecraft.getMinecraft().player.sendMessage(new TextComponentString("§ay座標を更新しました。"));
                }else{
                    Minecraft.getMinecraft().player.sendMessage(new TextComponentString("§c入力された文字列は数値に変換することができない文字が含まれています。"));
                }
            }else{
                Minecraft.getMinecraft().player.sendMessage(new TextComponentString(getUsage(sender)));
            }
        }
        try {
            dataUtils.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
