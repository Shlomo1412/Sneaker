package net.shlomo1412.sneaker.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class SneakerClient implements ClientModInitializer {

    private static boolean autoSneakEnabled = false;
    private static KeyMapping toggleKeyMapping;

    @Override
    public void onInitializeClient() {
        // Register the keybind for toggling auto-sneak
        toggleKeyMapping = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "Toggle Sneaker",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                KeyMapping.Category.MISC
        ));

        // Register client tick event to handle keybind press
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null && toggleKeyMapping.consumeClick()) {
                autoSneakEnabled = !autoSneakEnabled;

                // Display formatted action bar message
                Component message = Component.empty()
                        .append(Component.literal("✦ ").withStyle(ChatFormatting.GOLD))
                        .append(Component.literal("Auto-Sneak").withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD))
                        .append(Component.literal(": ").withStyle(ChatFormatting.GRAY))
                        .append(autoSneakEnabled
                                ? Component.literal("ON").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD)
                                : Component.literal("OFF").withStyle(ChatFormatting.RED, ChatFormatting.BOLD))
                        .append(Component.literal(" ✦").withStyle(ChatFormatting.GOLD));

                client.gui.setOverlayMessage(message, true);
            }
        });
    }

    public static boolean isAutoSneakEnabled() {
        return autoSneakEnabled;
    }
}
