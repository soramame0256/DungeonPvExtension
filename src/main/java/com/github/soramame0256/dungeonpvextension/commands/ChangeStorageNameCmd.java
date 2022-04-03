package com.github.soramame0256.dungeonpvextension.commands;

import com.github.soramame0256.dungeonpvextension.listener.EventListener;
import com.github.soramame0256.dungeonpvextension.utils.DataUtils;
import com.google.gson.JsonArray;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import scala.collection.parallel.ParIterableLike;

import java.io.IOException;

public class ChangeStorageNameCmd extends DpeCmdBase{

    @Override
    public String getName() {
        return "changestoragename";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/changestoragename <倉庫番号> <名前>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0){
            Minecraft.getMinecraft().player.sendMessage(new TextComponentString(getUsage(sender)));
            return;
        }
        int slot = 0;
        try {
             slot = Integer.parseInt(args[0]);
        }catch(NumberFormatException e){
            Minecraft.getMinecraft().player.sendMessage(new TextComponentString("§cスロットは整数である必要があります。"));
        }
        DataUtils dataUtils = DataUtils.getInstance();
        JsonArray ja = dataUtils.getJsonArrayData("StorageNames");
        if(args.length >=2) {
            String name = args[1];
            if (args.length >= 3) {
                for (int i = 2; i < args.length; i++) {
                    name = name.concat(" " + args[i]);
                }
            }
            ja.get(0).getAsJsonObject().addProperty(String.valueOf(slot), name.replaceAll("&", "§"));
            Minecraft.getMinecraft().player.sendMessage(new TextComponentString("§a倉庫スロット §6" + slot + " §aを " + name.replaceAll("&", "§") + " §aと命名しました"));
        }else {
            if(ja.get(0).getAsJsonObject().has(String.valueOf(slot))){
                ja.get(0).getAsJsonObject().remove(String.valueOf(slot));
            }
            Minecraft.getMinecraft().player.sendMessage(new TextComponentString("§a倉庫スロット §6" + slot + " §aの命名を削除しました"));
        }
        try {
            dataUtils.saveJsonData("StorageNames", ja);
        } catch (IOException e) {
            e.printStackTrace();
        }
        EventListener.reload();
    }
}
