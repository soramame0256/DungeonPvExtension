package com.github.soramame0256.dungeonpvextension;

import com.github.soramame0256.dungeonpvextension.commands.*;
import com.github.soramame0256.dungeonpvextension.listener.EventListener;
import com.github.soramame0256.dungeonpvextension.utils.DataUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.commons.io.FileUtils;
import org.lwjgl.input.Keyboard;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.security.ProtectionDomain;

@Mod(
        modid = DungeonPvExtension.MOD_ID,
        name = DungeonPvExtension.MOD_NAME,
        version = DungeonPvExtension.VERSION
)
public class DungeonPvExtension {

    public static final String MOD_ID = "dungeonpvextension";
    public static final String MOD_NAME = "DungeonPvExtension";
    public static final String VERSION = "1.0.16c";
    private static DataUtils dataUtil;
    public static KeyBinding[] keyBindings = new KeyBinding[8];
    public static boolean inDP = false;
    public static boolean isEnable = true;
    public static Minecraft mc;
    public static Boolean isUpToDate = true;
    public static String latestVersionFileName = "";
    public static String latestVersion = VERSION;
    /**
     * This is the instance of your mod as created by Forge. It will never be null.
     */
    @Mod.Instance(MOD_ID)
    public static DungeonPvExtension INSTANCE;

    public static DataUtils getDataUtil() {
        return dataUtil;
    }

    @Config(modid = MOD_ID, type = Config.Type.INSTANCE)
    public static class CONFIG_TYPES{
        public static String[] disableIds = {"Steve", "Alex"};
    }
    /**
     * This is the first initialization event. Register tile entities here.
     * The registry events below will have fired prior to entry to this method.
     */
    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        //https://seiai.ed.jp/sys/text/java/jv08a02.html
        INSTANCE = this;
        try {
            URL url = new URL("https://raw.githubusercontent.com/soramame0256/DungeonPvExtension-update/main/latestversion.json");
            InputStream strm = url.openStream();
            InputStreamReader in = new InputStreamReader(strm);
            BufferedReader inb = new BufferedReader(in);
            String line;
            StringBuilder sb = new StringBuilder();
            while((line=inb.readLine()) != null){
                sb.append(line);
            }
            inb.close();
            in.close();
            strm.close();
            JsonElement je = new JsonParser().parse(sb.toString());
            JsonObject jo = je.getAsJsonObject();
            String version = jo.get("version").getAsString();
            String fileName = jo.get("filename").getAsString();
            if (version.equals(VERSION)){
                System.out.println("VersionChecker: up to date!");
            }else{
                System.out.println("VersionChecker: version " + version + " is live.");
                isUpToDate = false;
                latestVersionFileName = fileName;
                latestVersion = version;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


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
        for (int i = 3; i< 8; i++){
            keyBindings[i] = new KeyBinding("QuickChat Slot" + (i-2), Keyboard.KEY_NONE, "DungeonPvExtension");
        }
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
        System.out.println("Minecraft Version: " + Loader.instance().getMCVersionString());
        try {
            dataUtil = new DataUtils("saves.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
        new EventListener();
        System.out.println("Generated EventListener Instance.");
        ClientCommandHandler.instance.registerCommand(new ToggleCmd());
        System.out.println("Registered ToggleCmd.");
        ClientCommandHandler.instance.registerCommand(new CommaSeparatingTestCmd());
        System.out.println("Registered CommaSeparatingTestCmd.");
        ClientCommandHandler.instance.registerCommand(new ChanceCalcCmd());
        ClientCommandHandler.instance.registerCommand(new TestMsgSendCmd());
        ClientCommandHandler.instance.registerCommand(new UpdateCmd());
        ClientCommandHandler.instance.registerCommand(new ChangeStorageNameCmd());
        ClientCommandHandler.instance.registerCommand(new QuickChatChangeMsgCmd());
        ClientCommandHandler.instance.registerCommand(new ScreenRenderingBasicCmd());
        ClientCommandHandler.instance.registerCommand(new WeaponLockCmd());
    }
    public void updateModFile() throws IOException, URISyntaxException {
        //https://blogs.osdn.jp/2017/09/24/runnable-jar.html
        URL url = new URL("https://github.com/soramame0256/DungeonPvExtension/releases/download/v" + latestVersion + "/" + latestVersionFileName);
        File file = new File(latestVersionFileName);
        ProtectionDomain pd = this.getClass().getProtectionDomain();
        CodeSource cs = pd.getCodeSource();
        URL location = cs.getLocation();
        JarURLConnection conn = (JarURLConnection)location.openConnection();
        Path path = Paths.get(conn.getJarFileURL().toURI());
        FileUtils.copyURLToFile(url,path.toFile());
    }
}
