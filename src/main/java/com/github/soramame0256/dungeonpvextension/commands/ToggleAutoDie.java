package com.github.soramame0256.dungeonpvextension.commands;

import com.github.soramame0256.dungeonpvextension.listener.EventListener;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class ToggleAutoDie extends DpeCmdBase{
    @Override
    public String getName() {
        return "toggleautodie";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/toggleautodie";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        sender.sendMessage(new TextComponentString("自動die実行: " + (EventListener.isAutoDieEnabled=!EventListener.isAutoDieEnabled)));
    }
}
