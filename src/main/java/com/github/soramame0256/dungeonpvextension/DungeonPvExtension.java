package com.github.soramame0256.dungeonpvextension;

import com.github.soramame0256.dungeonpvextension.commands.ChanceCalcCmd;
import com.github.soramame0256.dungeonpvextension.commands.CommaSeparatingTestCmd;
import com.github.soramame0256.dungeonpvextension.commands.ToggleCmd;
import com.github.soramame0256.dungeonpvextension.listener.EventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

@Mod(
        modid = DungeonPvExtension.MOD_ID,
        name = DungeonPvExtension.MOD_NAME,
        version = DungeonPvExtension.VERSION
)
public class DungeonPvExtension {

    public static final String MOD_ID = "dungeonpvextension";
    public static final String MOD_NAME = "DungeonPvExtension";
    public static final String VERSION = "1.0.5";
    public static KeyBinding[] keyBindings = new KeyBinding[3];
    public static boolean inDP = false;
    public static boolean isEnable = true;
    public static Minecraft mc;
    /**
     * This is the instance of your mod as created by Forge. It will never be null.
     */
    @Mod.Instance(MOD_ID)
    public static DungeonPvExtension INSTANCE;
    @Config(modid = MOD_ID, type = Config.Type.INSTANCE)
    public static class CONFIG_TYPES{
        public static String[] disableIds = {"Steve", "Alex"};
    }
    /**
     * This is the first initialization event. Register tile entities here.
     * The registry events below will have fired prior to entry to this method.
     */
    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent event){
    }

    /**
     * This is the second initialization event. Register custom recipes
     */
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        mc = Minecraft.getMinecraft();
        keyBindings[0] = new KeyBinding("/die command", Keyboard.KEY_NONE, "DungeonPvExtension");
        keyBindings[1] = new KeyBinding("/item command", Keyboard.KEY_NONE, "DungeonPvExtension");
        keyBindings[2] = new KeyBinding("Msg current HP", Keyboard.KEY_NONE, "DungeonPvExtension");
        for (KeyBinding binding : keyBindings) {
            ClientRegistry.registerKeyBinding(binding);
        }
    }

    /**
     * This is the final initialization event. Register actions from other mods here
     */
    @Mod.EventHandler
    public void postinit(FMLPostInitializationEvent event) {
        System.out.println("Initializing...");
        new EventListener();
        System.out.println("Generated EventListener Instance.");
        ClientCommandHandler.instance.registerCommand(new ToggleCmd());
        System.out.println("Registered ToggleCmd.");
        ClientCommandHandler.instance.registerCommand(new CommaSeparatingTestCmd());
        System.out.println("Registered CommaSeparatingTestCmd.");
        ClientCommandHandler.instance.registerCommand(new ChanceCalcCmd());
    }
}
