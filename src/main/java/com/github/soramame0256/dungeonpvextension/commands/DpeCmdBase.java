package com.github.soramame0256.dungeonpvextension.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public abstract class DpeCmdBase extends CommandBase {

    @Override
    public abstract String getName();

    @Override
    public abstract String getUsage(ICommandSender sender);

    @Override
    public abstract void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException;

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
