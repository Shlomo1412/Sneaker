package net.shlomo1412.sneaker.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.shlomo1412.sneaker.client.SneakerClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class ClientPlayerEntityMixin {

    @Unique
    private boolean sneaker$wasAutoSneaking = false;

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        LocalPlayer player = (LocalPlayer) (Object) this;
        Minecraft client = Minecraft.getInstance();
        
        if (client.options == null) {
            return;
        }

        // If auto-sneak is disabled, reset state and return
        if (!SneakerClient.isAutoSneakEnabled()) {
            if (sneaker$wasAutoSneaking) {
                client.options.keyShift.setDown(false);
                sneaker$wasAutoSneaking = false;
            }
            return;
        }

        // Check if player is on ground
        if (!player.onGround()) {
            if (sneaker$wasAutoSneaking) {
                client.options.keyShift.setDown(false);
                sneaker$wasAutoSneaking = false;
            }
            return;
        }

        // Get player center position
        double playerX = player.getX();
        double playerZ = player.getZ();
        
        // The Y level of the block the player is standing on
        int groundY = (int) Math.floor(player.getY()) - 1;
        
        // Player hitbox is 0.6 wide, so 0.3 from center to edge
        // Use 0.0001 for pixel-perfect timing at the very edge
        double halfWidth = 0.0001;
        
        // Calculate the 4 corners of the player's hitbox
        double[][] cornerPoints = {
            {playerX - halfWidth, playerZ - halfWidth},  // Corner 1
            {playerX + halfWidth, playerZ - halfWidth},  // Corner 2
            {playerX - halfWidth, playerZ + halfWidth},  // Corner 3
            {playerX + halfWidth, playerZ + halfWidth}   // Corner 4
        };
        
        boolean shouldSneak = false;
        ClientLevel world = client.level;
        
        if (world == null) {
            return;
        }
        
        for (double[] corner : cornerPoints) {
            BlockPos cornerBlockPos = new BlockPos(
                (int) Math.floor(corner[0]), 
                groundY, 
                (int) Math.floor(corner[1])
            );
            
            // Check if this corner is over air at the SAME level as the block we're standing on
            if (!world.getBlockState(cornerBlockPos).isSolidRender()) {
                shouldSneak = true;
                break;
            }
        }
        
        // Apply or release auto-sneak
        if (shouldSneak) {
            client.options.keyShift.setDown(true);
            sneaker$wasAutoSneaking = true;
        } else {
            // Safe to release - no edges detected
            if (sneaker$wasAutoSneaking) {
                client.options.keyShift.setDown(false);
                sneaker$wasAutoSneaking = false;
            }
        }
    }
}
