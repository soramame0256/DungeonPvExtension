package com.github.soramame0256.dungeonpvextension.commands;

import com.github.soramame0256.dungeonpvextension.utils.DataUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class WeaponLockCmd extends DpeCmdBase{

    @Override
    public String getName() {
        return "weaponlock";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/weaponlock";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            ItemStack item = Minecraft.getMinecraft().player.getHeldItemMainhand();
            DataUtils dataUtils = new DataUtils("itemlock.json");
            if (dataUtils.getBooleanData(String.format("%040x", new BigInteger(1, sha256.digest(item.getTagCompound().toString().getBytes()))))){
                dataUtils.saveBooleanData(String.format("%040x", new BigInteger(1, sha256.digest(item.getTagCompound().toString().getBytes()))),false);
                sender.sendMessage(new TextComponentString("§cアイテムロックを解除しました。"));
            }else{
                dataUtils.saveBooleanData(String.format("%040x", new BigInteger(1, sha256.digest(item.getTagCompound().toString().getBytes()))),true);
                sender.sendMessage(new TextComponentString("§cアイテムロックをしました。"));

            }
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }
}
