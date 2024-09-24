package com.github.soramame0256.dungeonpvextension.commands;

import com.github.soramame0256.dungeonpvextension.listener.EventListener;
import com.github.soramame0256.dungeonpvextension.utils.DataUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;

import java.io.IOException;

import static com.github.soramame0256.dungeonpvextension.DungeonPvExtension.getDataUtil;
import static com.github.soramame0256.dungeonpvextension.utils.ItemUtilities.itemToHash;

public class AddMemo extends DpeCmdBase{
    @Override
    public String getName() {
        return "addmemo";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/addmemo <text>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length>=1 && sender instanceof EntityPlayer && ((EntityPlayer) sender).getHeldItemMainhand()!= ItemStack.EMPTY){
            StringBuilder sb = new StringBuilder();
            for (String arg : args) {
                sb.append(arg).append(" ");
            }
            String mem = sb.toString().trim();
            DataUtils du = getDataUtil();
            JsonObject root = du.getRootJson();
            if(!root.has("ItemMemo")){
                root.add("ItemMemo",new JsonObject());
            }
            JsonArray arr = new JsonArray();
            JsonObject itemMemos = root.get("ItemMemo").getAsJsonObject();
            String hash = itemToHash(((EntityPlayer) sender).getHeldItemMainhand());
            if(itemMemos.has(hash))
                arr = root.get(hash).getAsJsonArray();
            arr.add(mem);
            itemMemos.add(hash,arr);

            try {
                du.flush();
                EventListener.reload();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
