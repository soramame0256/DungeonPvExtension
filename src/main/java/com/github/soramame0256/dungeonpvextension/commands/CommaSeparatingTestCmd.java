package com.github.soramame0256.dungeonpvextension.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import static com.github.soramame0256.dungeonpvextension.utils.NumberUtilities.commaSeparate;

public class CommaSeparatingTestCmd extends DpeCmdBase{
    @Override
    public String getName() {
        return "cst";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/cst <double>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 1){
            if (!Double.valueOf(args[0]).isNaN()){
                Minecraft.getMinecraft().player.sendMessage(new TextComponentString(commaSeparate(Double.valueOf(args[0]))));
            }
        }
    }
}
