package com.github.soramame0256.dungeonpvextension.commands;

import com.github.soramame0256.dungeonpvextension.listener.EventListener;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class HealthShownToggleCmd extends DpeCmdBase{
    @Override
    public String getName() {
        return "healthshowtoggle";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/healthshowtoggle";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        sender.sendMessage(new TextComponentString("体力表示機能: " + (EventListener.isHealthShowFeatureEnabled=!EventListener.isHealthShowFeatureEnabled)));
    }
}
