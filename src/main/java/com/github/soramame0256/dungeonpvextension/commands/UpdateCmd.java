package com.github.soramame0256.dungeonpvextension.commands;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;

import static com.github.soramame0256.dungeonpvextension.DungeonPvExtension.*;

public class UpdateCmd extends DpeCmdBase {

    @Override
    public String getName() {
        return "dpeupdate";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/dpeupdate";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(isUpToDate){
            try{
                URL url = new URL("https://raw.githubusercontent.com/soramame0256/DungeonPvExtension-update/main/latestversion.json");
                InputStream strm = url.openStream();
                InputStreamReader in = new InputStreamReader(strm);
                BufferedReader inb = new BufferedReader(in);
                String line;
                StringBuilder sb = new StringBuilder();
                while((line=inb.readLine()) != null){
                    sb.append(line);
                }
                inb.close();
                in.close();
                strm.close();
                JsonElement je = new JsonParser().parse(sb.toString());
                JsonObject jo = je.getAsJsonObject();
                String version = jo.get("version").getAsString();
                String fileName = jo.get("filename").getAsString();
                if (version.equals(VERSION)){
                    System.out.println("VersionChecker: up to date!");
                    sender.sendMessage(new TextComponentString("最新バージョンです!"));
                    return;
                }else{
                    System.out.println("VersionChecker: version " + version + " is live.");
                    isUpToDate = false;
                    latestVersionFileName = fileName;
                    latestVersion = version;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(isUpToDate) {
                try {
                    INSTANCE.updateModFile();
                } catch (IOException | URISyntaxException e) {
                    e.printStackTrace();
                    sender.sendMessage(new TextComponentString("実行中にエラーが発生しました"));
                    return;
                }
                sender.sendMessage(new TextComponentString("更新しました。 再起動してください。"));
            }
        }else{
            try {
                INSTANCE.updateModFile();
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
                sender.sendMessage(new TextComponentString("実行中にエラーが発生しました"));
                return;
            }
            sender.sendMessage(new TextComponentString("更新しました。 再起動してください。"));
        }
    }
}
