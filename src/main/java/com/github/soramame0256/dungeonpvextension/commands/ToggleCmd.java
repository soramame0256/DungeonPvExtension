package com.github.soramame0256.dungeonpvextension.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import static com.github.soramame0256.dungeonpvextension.DungeonPvExtension.inDP;

public class ToggleCmd extends DpeCmdBase {

    @Override
    public String getName() {
        return "dpetoggle";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/dpetoggle";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        inDP = !inDP;
        Minecraft.getMinecraft().player.sendMessage(new TextComponentString("切り替えました: " + inDP));
    }
}
