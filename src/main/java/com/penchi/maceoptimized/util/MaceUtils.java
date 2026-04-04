// Ladle Code Mat Dekh, Licenced hai CC BY-NC-ND 4.0

package com.penchi.maceoptimized.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;

public class MaceUtils {
    public static int findMaceInHotbar(PlayerEntity player) {
        for (int i = 0; i < 9; i++) {
            if (player.getInventory().getStack(i).isOf(Items.MACE)) {
                return i;
            }
        }
        return -1;
    }
}