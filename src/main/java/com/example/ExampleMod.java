package net.fabricmc.example;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.entity.player.PlayerEntity;
import org.lwjgl.glfw.GLFW;

public class ExampleMod implements ClientModInitializer {
    public static boolean espActive = false;
    public static boolean triggerActive = false;
    private boolean isAltZeroDown = false;
    private boolean isBDown = false;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.getWindow() == null) return;
            long win = client.getWindow().getHandle();

            // Alt + 0: ESP
            boolean alt = InputUtil.isKeyPressed(win, GLFW.GLFW_KEY_LEFT_ALT) || InputUtil.isKeyPressed(win, GLFW.GLFW_KEY_RIGHT_ALT);
            boolean zero = InputUtil.isKeyPressed(win, GLFW.GLFW_KEY_0);
            if (alt && zero && !isAltZeroDown) {
                espActive = !espActive;
                isAltZeroDown = true;
                client.player.sendMessage(Text.literal("§7[Sodium] §fVisuals: " + (espActive ? "§aHigh" : "§cOff")), true);
            } else if (!zero) isAltZeroDown = false;

            // B: Trigger (Криты в прыжке)
            boolean b = InputUtil.isKeyPressed(win, GLFW.GLFW_KEY_B);
            if (b && !isBDown) {
                triggerActive = !triggerActive;
                isBDown = true;
                client.player.sendMessage(Text.literal("§7[Sodium] §fTrigger: " + (triggerActive ? "§aOn" : "§cOff")), true);
            } else if (!b) isBDown = false;

            if (triggerActive && client.crosshairTarget instanceof EntityHitResult hit) {
                if (hit.getEntity() instanceof PlayerEntity target && !target.isInvisible()) {
                    boolean isFalling = client.player.fallDistance > 0.0f && !client.player.isOnGround();
                    if (client.player.getAttackCooldownProgress(0) >= 1.0f && isFalling) {
                        client.interactionManager.attackEntity(client.player, target);
                        client.player.swingHand(Hand.MAIN_HAND);
                    }
                }
            }
        });
    }
}
