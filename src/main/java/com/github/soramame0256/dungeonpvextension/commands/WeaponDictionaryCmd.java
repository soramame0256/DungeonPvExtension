package com.github.soramame0256.dungeonpvextension.commands;

import com.github.soramame0256.dungeonpvextension.api.Option;
import com.github.soramame0256.dungeonpvextension.utils.DataUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import java.io.IOException;
import java.util.Map;

public class WeaponDictionaryCmd extends DpeCmdBase{

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return null;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        DataUtils dictionary = null;
        try {
            dictionary = new DataUtils("dictionary.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(dictionary == null){
            throw new CommandException("dungeonpvextension.datautils.fail", dictionary);
        }
        JsonObject dicJson = dictionary.getJsonArrayData("Weapon").get(0).getAsJsonObject();
        if (args.length == 0){
            Integer damage, essence, gold, rarity;
            Double subStats;
            String skill,skillInfo;
            Option subOption;
            for (Map.Entry<String, JsonElement> alpha : dicJson.entrySet()){
                damage = alpha.getValue().getAsJsonObject().get("damage").getAsInt();
                essence = alpha.getValue().getAsJsonObject().get("essence").getAsInt();
                gold = alpha.getValue().getAsJsonObject().get("gold").getAsInt();
                rarity = alpha.getValue().getAsJsonObject().get("rarity").getAsInt();
                subStats = alpha.getValue().getAsJsonObject().get("subStats").getAsDouble();
                skill = alpha.getValue().getAsJsonObject().get("skillName").getAsString();
                skillInfo = alpha.getValue().getAsJsonObject().get("skillInfo").getAsString();
                subOption = Option.valueOf(alpha.getValue().getAsJsonObject().get("subOption").getAsString());
            }
        }
    }
}
