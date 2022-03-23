package com.github.soramame0256.dungeonpvextension.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class TestMsgSendCmd extends DpeCmdBase {

    @Override
    public String getName() {
        return "testmsgsend";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/testmsgsend";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        Minecraft.getMinecraft().player.sendMessage(new TextComponentString(args[0]));
    }
}
