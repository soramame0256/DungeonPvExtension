package com.github.soramame0256.dungeonpvextension.utils;

import net.minecraft.client.Minecraft;

public class ConnectionUtils {
    public static Long getPing() {
        if (Minecraft.getMinecraft().getCurrentServerData() == null){
            return 0L;
        }
        return Minecraft.getMinecraft().getCurrentServerData().pingToServer;
    }
}
