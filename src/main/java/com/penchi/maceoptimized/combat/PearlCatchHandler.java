package com.penchi.maceoptimized.combat;

import com.penchi.maceoptimized.config.ModConfig;
import com.penchi.maceoptimized.util.ChatUtils;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

public class PearlCatchHandler {

    private static long lastPearlTime = 0;
    private static boolean swapped = false;
    private static boolean autoUsed = false;
    private static int autoUseTimer = -1; // counts down ticks before auto use

    public static void register() {

        // Detect pearl throw
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (world.isClient() && player.getStackInHand(hand).isOf(Items.ENDER_PEARL)) {
                lastPearlTime = System.currentTimeMillis();
                swapped = false;
                autoUsed = false;
                autoUseTimer = -1;
            }
            return ActionResult.PASS;
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!ModConfig.masterToggle) return;
            if (!ModConfig.pearlCatchEnabled) return;
            if (client.player == null) return;

            long timeSincePearl = System.currentTimeMillis() - lastPearlTime;
            if (timeSincePearl > 5000) return; // 5 sec window

            // Find wind charge slot
            int windSlot = -1;
            for (int i = 0; i < 9; i++) {
                if (client.player.getInventory().getStack(i).isOf(Items.WIND_CHARGE)) {
                    windSlot = i;
                    break;
                }
            }
            if (windSlot == -1) return;

            // Step 1: Always swap to wind charge when looking up (both modes)
            ChatUtils ChatUtils = null;
            if (!swapped && client.player.getPitch() < -70) {
                if (client.player.getInventory().getSelectedSlot() != windSlot) {
                    client.player.getInventory().setSelectedSlot(windSlot);
                    if (client.getNetworkHandler() != null) {
                        client.getNetworkHandler().sendPacket(
                                new UpdateSelectedSlotC2SPacket(windSlot));
                    }
                    swapped = true;

                    // AUTO MODE: start countdown timer
                    if (ModConfig.autoPearlCatch) {
                        autoUseTimer = ModConfig.autoPearlCatchDelay;
                        ChatUtils.sendPearlCatch();
                    } else {
                        ChatUtils.sendPearlCatch();
                    }
                }
            }

            // Step 2: AUTO MODE only — countdown then auto use wind charge
            if (ModConfig.autoPearlCatch && swapped && !autoUsed) {
                if (autoUseTimer > 0) {
                    autoUseTimer--;
                } else if (autoUseTimer == 0) {
                    if (client.interactionManager != null
                            && client.player.getInventory().getSelectedSlot() == windSlot) {
                        client.interactionManager.interactItem(client.player, Hand.MAIN_HAND);
                        client.player.swingHand(Hand.MAIN_HAND);
                        autoUsed = true;
                        autoUseTimer = -1;
                        ChatUtils.sendPearlCatch();
                    }
                }
            }
        });
    }
}
