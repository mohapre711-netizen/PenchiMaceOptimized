package com.penchi.maceoptimized.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.literal("§6§lPenchi MaceOptimized §r§7Settings"));

            ConfigEntryBuilder entryBuilder = builder.entryBuilder();
            ConfigCategory all = builder.getOrCreateCategory(Text.literal("Settings"));

            // --- GENERAL ---
            all.addEntry(entryBuilder.startTextDescription(Text.literal("§e§l— General —")).build());

            all.addEntry(entryBuilder.startBooleanToggle(Text.literal("Master Toggle"), ModConfig.masterToggle)
                    .setDefaultValue(true)
                    .setTooltip(Text.literal("Enable/Disable entire mod"))
                    .setSaveConsumer(v -> ModConfig.masterToggle = v)
                    .build());

            // Mace Swap Delay
            // --- MACE SWAP ---

            all.addEntry(entryBuilder.startTextDescription(Text.literal("§b§l— Mace Swap Toggle —")).build());

            all.addEntry(entryBuilder.startBooleanToggle(Text.literal("Density Swap"), ModConfig.densityEnabled)
                    .setDefaultValue(true)
                    .setTooltip(Text.literal("Auto swap to Density Mace when falling"))
                    .setSaveConsumer(v -> ModConfig.densityEnabled = v)
                    .build());

            all.addEntry(entryBuilder.startBooleanToggle(Text.literal("Breach Swap"), ModConfig.breachEnabled)
                    .setDefaultValue(true)
                    .setTooltip(Text.literal("Auto swap to Breach Mace when target has armor"))
                    .setSaveConsumer(v -> ModConfig.breachEnabled = v)
                    .build());




//            all.addEntry(entryBuilder.startTextDescription(Text.literal("§b§l— Mace Swap Delay Settings —")).build());
//
///// --- UNIVERSAL REVERT ---
            all.addEntry(entryBuilder.startIntSlider(Text.literal("Universal Revert Delay"), ModConfig.revertDelayTicks, 1, 10)
                    .setDefaultValue(2)
                    .setTooltip(Text.literal("Ticks to wait AFTER hit before going back to original item"))
                    .setSaveConsumer(v -> ModConfig.revertDelayTicks = v)
                    .build());
//
//// --- DENSITY SWAP (PRE-HIT) ---
//            all.addEntry(entryBuilder.startIntSlider(Text.literal("Density Swap Delay"), ModConfig.densitySwapDelay, 0, 10)
//                    .setDefaultValue(1)
//                    .setTooltip(Text.literal("Delay between falling and swapping to Density Mace"))
//                    .setSaveConsumer(v -> ModConfig.densitySwapDelay = v)
//                    .build());
//
//// --- BREACH SWAP (PRE-HIT) ---
//            all.addEntry(entryBuilder.startIntSlider(Text.literal("Breach Swap Delay"), ModConfig.breachSwapDelay, 0, 10)
//                    .setDefaultValue(1)
//                    .setTooltip(Text.literal("Delay between aiming at armor and swapping to Breach Mace"))
//                    .setSaveConsumer(v -> ModConfig.breachSwapDelay = v)
//                    .build());

            // --- ELYTRA & UTILITY (NEW) ---
            all.addEntry(entryBuilder.startTextDescription(Text.literal("§a§l— Elytra & Utility —")).build());

            all.addEntry(entryBuilder.startBooleanToggle(Text.literal("Enable Elytra/Chest Swap"), ModConfig.elytraChestSwapEnabled)
                    .setDefaultValue(true)
                    .setTooltip(Text.literal("Automatically swaps Elytra for Chestplate when taking damage on ground"))
                    .setSaveConsumer(v -> ModConfig.elytraChestSwapEnabled = v)
                    .build());

            all.addEntry(entryBuilder.startBooleanToggle(Text.literal("Auto Rocket Select"), ModConfig.elytraLaunchEnabled)
                    .setDefaultValue(false)
                    .setTooltip(Text.literal("Automatically selects Rocket/WindCharge when equipping Elytra"))
                    .setSaveConsumer(v -> ModConfig.elytraLaunchEnabled = v)
                    .build());

            // --- GLIDE WEAPON SWAP ---
            all.addEntry(entryBuilder.startTextDescription(Text.literal("§6§l— Glide Weapon Swap —")).build());

            all.addEntry(entryBuilder.startBooleanToggle(Text.literal("Glide Weapon Swap"), ModConfig.glideWeaponSwapEnabled)
                    .setDefaultValue(false)
                    .setTooltip(Text.literal("When elytra stops gliding + chestplate equipped\nauto switch to selected weapon"))
                    .setSaveConsumer(v -> ModConfig.glideWeaponSwapEnabled = v)
                    .build());

            all.addEntry(entryBuilder.startSelector(
                            Text.literal("Weapon Choice"),
                            new Object[]{"§aMace", "§bSword"},
                            ModConfig.glideWeaponChoice == 0 ? "§aMace" : "§bSword")
                    .setDefaultValue("§aMace")
                    .setTooltip(Text.literal("Click to cycle: Mace / Sword"))
                    .setSaveConsumer(v -> ModConfig.glideWeaponChoice = v.equals("§aMace") ? 0 : 1)
                    .build());

            // --- PEARL CATCH ---
            all.addEntry(entryBuilder.startTextDescription(Text.literal("§d§l— Pearl Catch —")).build());

            all.addEntry(entryBuilder.startBooleanToggle(Text.literal("Pearl Catch Enabled"), ModConfig.pearlCatchEnabled)
                    .setDefaultValue(true)
                    .setTooltip(Text.literal("Auto swap to Wind Charge after throwing Ender Pearl"))
                    .setSaveConsumer(v -> ModConfig.pearlCatchEnabled = v)
                    .build());

            all.addEntry(entryBuilder.startBooleanToggle(Text.literal("Auto Pearl Catch"), ModConfig.autoPearlCatch)
                    .setDefaultValue(false)
                    .setTooltip(Text.literal("OFF = swap only, you click manually | ON = auto use Wind Charge after delay"))
                    .setSaveConsumer(v -> ModConfig.autoPearlCatch = v)
                    .build());

            all.addEntry(entryBuilder.startIntSlider(Text.literal("Auto Catch Delay (Ticks)"), ModConfig.autoPearlCatchDelay, 1, 10)
                    .setDefaultValue(2)
                    .setTooltip(Text.literal("Ticks after swap before auto using Wind Charge\nRange: 1-10 ticks"))
                    .setSaveConsumer(v -> ModConfig.autoPearlCatchDelay = v)
                    .build());

// --- APPEARANCE ---
            all.addEntry(entryBuilder.startTextDescription(Text.literal("§d§l— Appearance —")).build());

            all.addEntry(entryBuilder.startBooleanToggle(Text.literal("Use Custom Mace Textures"), ModConfig.useCustomTextures)
                    .setDefaultValue(true)
                    .setSaveConsumer(v -> {
                        ModConfig.useCustomTextures = v;
                        MinecraftClient client = MinecraftClient.getInstance();

                        if (client != null && client.getResourcePackManager() != null) {
                            // EXACT ID jo aapne options.txt mein dekhi
                            String packId = "maceoptimized:custom_textures";

                            // 1. Current profiles ki fresh list lo
                            java.util.List<String> enabledPacks = new java.util.ArrayList<>(client.options.resourcePacks);

                            // 2. Toggle ke hisab se ID add ya remove karo
                            if (v) {
                                if (!enabledPacks.contains(packId)) {
                                    enabledPacks.add(packId);
                                }
                            } else {
                                enabledPacks.remove(packId);
                            }

                            // 3. Sabse Important: Manager ko order do ki sirf yehi packs load karein
                            client.getResourcePackManager().setEnabledProfiles(enabledPacks);

                            // 4. Options update karo aur file mein save karo
                            client.options.resourcePacks = enabledPacks;
                            client.options.write();

                            // 5. Force Reload trigger karo (Red screen aayegi)
                            client.reloadResources();
                        }
                    })
                    .build());

            builder.setSavingRunnable(() -> {
                // Yahan aap config save karne ka logic likh sakte hain agar file mein save karna ho
            });

            return builder.build();
        };
    }
}