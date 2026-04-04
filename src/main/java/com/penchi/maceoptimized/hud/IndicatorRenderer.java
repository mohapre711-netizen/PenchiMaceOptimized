//package com.yashh.maceoptimized.hud;
//
//import com.yashh.maceoptimized.config.ModConfig;
//import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
//
//public class IndicatorRenderer {
//    private static int color = 0xFF00FF00;
//    private static int ticksLeft = 0;
//
//    public static void register() {
//        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
//            // Check if indicators and master toggle are both ON
//            if (ModConfig.masterToggle && ModConfig.indicatorsEnabled && ticksLeft > 0) {
//                int x = drawContext.getScaledWindowWidth() / 2 - 2;
//                int y = drawContext.getScaledWindowHeight() / 2 + 40;
//                drawContext.fill(x, y, x + 1, y + 1, color);
//                ticksLeft--;
//            }
//        });
//    }
//
//    public static void show(int newColor) {
//        color = newColor;
//        ticksLeft = 20; // 1 second display
//    }
//}