// Ladle Code Mat Dekh, Licenced hai CC BY-NC-ND 4.0

package com.penchi.maceoptimized;

import com.penchi.maceoptimized.combat.GlideWeaponSwap;
import com.penchi.maceoptimized.combat.MaceSwapHandler;
import com.penchi.maceoptimized.combat.PearlCatchHandler;
import com.penchi.maceoptimized.keybind.KeybindManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.fabricmc.loader.api.FabricLoader;

public class MaceOptimizedClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        KeybindManager.register();
        MaceSwapHandler.register();
        PearlCatchHandler.register();
        GlideWeaponSwap.register();

        // ✅ Saare ticks ek hi jagah
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                KeybindManager.handleInputs(); // Sirf ek baar
//                ElytraHandler.onTick();
            }
        });

        // OnInitializeClient ke andar:
        ResourceManagerHelper.registerBuiltinResourcePack(
                Identifier.of("maceoptimized", "custom_textures"),
                FabricLoader.getInstance().getModContainer("maceoptimized").get(),
                Text.literal("MaceOptimized Textures"),
                ResourcePackActivationType.NORMAL // ✅ Sirf NORMAL rakho
        );
    }
}