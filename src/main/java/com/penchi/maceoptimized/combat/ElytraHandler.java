package com.penchi.maceoptimized.combat;

import com.penchi.maceoptimized.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

public class ElytraHandler {

    private static int tickDelay = 0;
    private static boolean shouldSelectRocket = false;
    private static long lastSwapTime = 0;

    // ✅ NEW: track Elytra state (important fix)
    private static boolean wasWearingElytra = false;

    // 🔁 Tick handler
    public static void onTick() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        ClientPlayerEntity player = client.player;

        // ❌ GUI open → skip
        if (player.currentScreenHandler != player.playerScreenHandler) return;

        // ❌ Air/falling → skip
        if (!player.isOnGround()) return;

        ItemStack chest = player.getEquippedStack(EquipmentSlot.CHEST);

        boolean isWearingElytra = chest.isOf(Items.ELYTRA);

        if (!ModConfig.elytraLaunchEnabled) return; // Toggle check

        // ✅ Only trigger ONCE when Elytra is newly equipped
        if (isWearingElytra && !wasWearingElytra) {
            shouldSelectRocket = true;
            tickDelay = 3;
        }

        // update state
        wasWearingElytra = isWearingElytra;

        // ⏱️ Delay system
        if (shouldSelectRocket) {
            tickDelay--;
            if (tickDelay <= 0) {
                selectRocket(player);
                shouldSelectRocket = false;
            }
        }
    }

    // 🗡️ Damage event (unchanged)
    public static void onDamage() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        ClientPlayerEntity player = client.player;

        // ✅ FIX 1: Toggle Check (Mod Menu se band hai toh kuch mat karo)
        if (!ModConfig.elytraChestSwapEnabled) return;

        // ✅ FIX 2: Escape Logic (Hawa mein ho ya ud rahe ho toh swap MAT karo)
        if (!player.isOnGround() || player.checkGliding()) {
            return;
        }

        // ❌ GUI open → skip
        if (player.currentScreenHandler != player.playerScreenHandler) return;

        ItemStack chest = player.getEquippedStack(EquipmentSlot.CHEST);
        if (!chest.isOf(Items.ELYTRA)) return;

        // Anti-spam logic (paisa-wasool fix)
        long now = System.currentTimeMillis();
        if (now - lastSwapTime < 500) return;

        int slot = findHotbarChestplate(player);
        if (slot == -1) return;

        swapWithChestSlot(client, player, slot);
        lastSwapTime = now;

//        player.sendMessage(Text.literal("§cChestplate equipped for safety!"), true);
    }

    // 🚀 Rocket / WindCharge select
    private static void selectRocket(ClientPlayerEntity player) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.getNetworkHandler() == null) return;

        int rocket = findHotbarItem(player, Items.FIREWORK_ROCKET);
        int wind = findHotbarItem(player, Items.WIND_CHARGE);

        int slot = rocket != -1 ? rocket : wind;
        if (slot == -1) return;

        // ✅ Prevent unnecessary re-select
        if (player.getInventory().getSelectedSlot() == slot) return;

        player.getInventory().setSelectedSlot(slot);
        client.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(slot));

        // 💬 Actionbar message
//        player.sendMessage(Text.literal("§aRocket/WindCharge selected!"), true);
    }

    // 🔍 Hotbar item finder
    private static int findHotbarItem(ClientPlayerEntity player, net.minecraft.item.Item item) {
        for (int i = 0; i < 9; i++) {
            if (player.getInventory().getStack(i).isOf(item)) return i;
        }
        return -1;
    }

    // 🔍 Hotbar chestplate finder
    private static int findHotbarChestplate(ClientPlayerEntity player) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (isChestplate(stack)) return i;
        }
        return -1;
    }

    // 🔄 Safe swap (hotbar only)
    private static void swapWithChestSlot(MinecraftClient client, ClientPlayerEntity player, int hotbarSlot) {
        if (client.interactionManager == null) return;

        int chestSlot = 6;
        int invSlot = 36 + hotbarSlot;

        client.interactionManager.clickSlot(
                player.playerScreenHandler.syncId,
                invSlot, 0, SlotActionType.PICKUP, player);

        client.interactionManager.clickSlot(
                player.playerScreenHandler.syncId,
                chestSlot, 0, SlotActionType.PICKUP, player);

        client.interactionManager.clickSlot(
                player.playerScreenHandler.syncId,
                invSlot, 0, SlotActionType.PICKUP, player);
    }

    // 🛡️ Chestplate check
    private static boolean isChestplate(ItemStack stack) {
        if (stack.isEmpty()) return false;

        return stack.isOf(Items.NETHERITE_CHESTPLATE)
                || stack.isOf(Items.DIAMOND_CHESTPLATE)
                || stack.isOf(Items.IRON_CHESTPLATE)
                || stack.isOf(Items.GOLDEN_CHESTPLATE)
                || stack.isOf(Items.LEATHER_CHESTPLATE)
                || stack.isOf(Items.CHAINMAIL_CHESTPLATE);
    }
}