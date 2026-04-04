package com.penchi.maceoptimized.combat;

import com.penchi.maceoptimized.config.ModConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.registry.RegistryKeys;

public class GlideWeaponSwap {

    private static boolean wasGliding = false;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!ModConfig.masterToggle || !ModConfig.glideWeaponSwapEnabled) return;
            if (client.player == null) return;

            ClientPlayerEntity player = client.player;
            boolean isGliding = player.isGliding();

            // Detect: was gliding, now NOT gliding + chestplate equipped
            if (wasGliding && !isGliding) {
                ItemStack chest = player.getEquippedStack(EquipmentSlot.CHEST);
                boolean hasChestplate = chest.isOf(Items.NETHERITE_CHESTPLATE)
                        || chest.isOf(Items.DIAMOND_CHESTPLATE)
                        || chest.isOf(Items.IRON_CHESTPLATE)
                        || chest.isOf(Items.GOLDEN_CHESTPLATE)
                        || chest.isOf(Items.LEATHER_CHESTPLATE)
                        || chest.isOf(Items.CHAINMAIL_CHESTPLATE);

                if (hasChestplate) {
                    if (ModConfig.glideWeaponChoice == 0) {
                        // Mace
                        int slot = findMace(player);
                        if (slot != -1) swapTo(slot, client);
                    } else {
                        // Sword
                        int slot = findSword(player);
                        if (slot != -1) swapTo(slot, client);
                    }
                }
            }

            wasGliding = isGliding;
        });
    }

    private static int findSword(ClientPlayerEntity player) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isOf(Items.NETHERITE_SWORD)
                    || stack.isOf(Items.DIAMOND_SWORD)
                    || stack.isOf(Items.IRON_SWORD)
                    || stack.isOf(Items.GOLDEN_SWORD)
                    || stack.isOf(Items.STONE_SWORD)
                    || stack.isOf(Items.WOODEN_SWORD)) return i;
        }
        return -1;
    }

    private static int findMace(ClientPlayerEntity player) {
        int bestMaceSlot = -1;
        int currentPriority = -1; // 0: Normal, 1: Breach, 2: Density

        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getStack(i);

            if (stack.isOf(Items.MACE)) {
                int priority = 0; // Default normal mace

                // 1.21.x Enchantment Check Logic
                if (EnchantmentHelper.getLevel(player.getEntityWorld().getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(Enchantments.DENSITY), stack) > 0) {
                    priority = 2; // Sabse zyada priority
                } else if (EnchantmentHelper.getLevel(player.getEntityWorld().getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(Enchantments.BREACH), stack) > 0) {
                    priority = 1; // Middle priority
                }

                // Agar ye mace pehle wali se behtar hai, toh iska slot save karlo
                if (priority > currentPriority) {
                    currentPriority = priority;
                    bestMaceSlot = i;
                }

                // Agar Density (Max Priority) mil gayi hai, toh aage dhoondne ki zaroorat nahi
                if (currentPriority == 2) break;
            }
        }
        return bestMaceSlot;
    }

    private static void swapTo(int slot, MinecraftClient client) {
        if (client.player != null && client.getNetworkHandler() != null) {
            client.player.getInventory().setSelectedSlot(slot);
            client.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(slot));
        }
    }
}

