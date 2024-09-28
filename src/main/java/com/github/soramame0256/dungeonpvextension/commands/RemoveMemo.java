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

public class RemoveMemo extends DpeCmdBase{
    @Override
    public String getName() {
        return "removememo";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/removememo <index>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length==1 && sender instanceof EntityPlayer && ((EntityPlayer) sender).getHeldItemMainhand()!= ItemStack.EMPTY){
            DataUtils du = getDataUtil();
            JsonObject root = du.getRootJson();
            if(!root.has("ItemMemo")) return;
            JsonArray arr;
            JsonObject itemMemos = root.get("ItemMemo").getAsJsonObject();
            if(!itemMemos.has(itemToHash(((EntityPlayer) sender).getHeldItemMainhand()))) return;
            arr = itemMemos.get(itemToHash(((EntityPlayer) sender).getHeldItemMainhand())).getAsJsonArray();
            arr.remove(Integer.parseInt(args[0])-1);
            try {
                du.flush();
                EventListener.reload();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
