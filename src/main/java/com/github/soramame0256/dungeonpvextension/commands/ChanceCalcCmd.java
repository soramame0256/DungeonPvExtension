package com.github.soramame0256.dungeonpvextension.commands;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class ChanceCalcCmd extends DpeCmdBase {

    @Override
    public String getName() {
        return "dpechancecalc";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "dpechancecalc <確率> <抽選回数> <分母(百分率->100)>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length == 3){
            Integer[] argsInt = {Integer.parseInt(args[0]),Integer.parseInt(args[1]),Integer.parseInt(args[2])};
            BigDecimal d1 = BigDecimal.valueOf(argsInt[0]);
            BigDecimal d2 = BigDecimal.valueOf(argsInt[2]);
            d1 = d1.add(d2.negate()).abs().pow(argsInt[1]);
            d2 = d2.pow(argsInt[1]);
            BigDecimal d3 = d1.divide(d2, 9, RoundingMode.HALF_EVEN);
            d3 = d3.add(BigDecimal.ONE.negate()).abs();
            d3 = d3.multiply(BigDecimal.TEN.multiply(BigDecimal.TEN));
            sender.sendMessage(new TextComponentString(d3.toString()).appendText("%"));
        }else{
            sender.sendMessage(new TextComponentString(getUsage(sender)));
        }
    }
}
