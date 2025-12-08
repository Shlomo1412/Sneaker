package net.shlomo1412.sneaker.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

public class SneakerClient implements ClientModInitializer {

    private static boolean autoSneakEnabled = false;
    private static KeyBinding toggleKeyBinding;

    @Override
    public void onInitializeClient() {
        // Register the keybind for toggling auto-sneak
        toggleKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.sneaker.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                KeyBinding.Category.MISC
        ));

        // Register client tick event to handle keybind press
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null && toggleKeyBinding.wasPressed()) {
                autoSneakEnabled = !autoSneakEnabled;
                
                // Display formatted action bar message
                Text message = Text.empty()
                        .append(Text.literal("✦ ").formatted(Formatting.GOLD))
                        .append(Text.literal("Auto-Sneak").formatted(Formatting.YELLOW, Formatting.BOLD))
                        .append(Text.literal(": ").formatted(Formatting.GRAY))
                        .append(autoSneakEnabled 
                                ? Text.literal("ON").formatted(Formatting.GREEN, Formatting.BOLD)
                                : Text.literal("OFF").formatted(Formatting.RED, Formatting.BOLD))
                        .append(Text.literal(" ✦").formatted(Formatting.GOLD));
                
                client.player.sendMessage(message, true);
            }
        });
    }

    public static boolean isAutoSneakEnabled() {
        return autoSneakEnabled;
    }
}
