package tk.chroma.antiplots.client;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

public class AntiplotsClient implements ClientModInitializer {
    public static final String CONFIG_FILE_NAME = "antiplots_enabled";
    private static final KeyMapping signToggle = new KeyMapping("key.antiplots.toggle", InputConstants.Type.KEYSYM, 92, "category.antiplots");
    public static boolean hideSigns = true;
    public static boolean onPlots = false;
    public static int ticks = 0;

    @Override
    public void onInitializeClient() {
        KeyBindingHelper.registerKeyBinding(signToggle);
        ClientTickEvents.END_CLIENT_TICK.register(et -> {
            if (signToggle.consumeClick() && onPlots) {
                signToggle.setDown(false);
                hideSigns = !hideSigns;
            }
            ticks++;
            if (ticks%100 == 0) {
                try {
                    save();
                } catch (Exception ignored) {
                    System.out.println("failed to save");
                }
            }
        });
        ClientChunkEvents.CHUNK_LOAD.register((level, chunk) -> {
            onPlots = level.dimension().location().getPath().equals("plots");
        });
        String config = FabricLoader.getInstance().getConfigDir() + File.separator + CONFIG_FILE_NAME;
        try {
            hideSigns = Files.readString(Path.of(config)).startsWith("1");
        } catch (Exception ignored) {
            // dont really care
        }
        ClientLifecycleEvents.CLIENT_STOPPING.register((a) -> {

        });
    }

    public static boolean hidingSigns() {
        return hideSigns && onPlots;
    }

    public static void save() throws IOException {
        String config = FabricLoader.getInstance().getConfigDir() + File.separator + CONFIG_FILE_NAME;
        Path file = Path.of(config);
        try {
            Files.createFile(file);
        } catch (IOException ignored) {

        }
        Files.writeString(file, hideSigns ? "1" : "0");
    }
}
