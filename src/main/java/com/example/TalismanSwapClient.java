package com.example;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import org.lwjgl.glfw.GLFW;

public class TalismanSwapClient implements ClientModInitializer {
    private static KeyBinding swapKeyBinding;

    @Override
    public void onInitializeClient() {
        // Исправленный метод регистрации кнопки для версии 1.21.1
        swapKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.talismanswap.swap",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_X, 
                KeyBinding.GAMEPLAY_CATEGORY // Используем стандартную категорию игры во избежание ошибок
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.interactionManager == null) return;

            while (swapKeyBinding.wasPressed()) {
                PlayerInventory inventory = client.player.getInventory();
                ItemStack offhandStack = inventory.getStack(PlayerInventory.OFF_HAND_SLOT);
                String currentName = offhandStack.getName().getString().toLowerCase();
                String targetName = "";

                if (currentName.contains("крушител")) {
                    targetName = "карател";
                } else if (currentName.contains("карател") || offhandStack.isEmpty()) {
                    targetName = "крушител"; 
                }

                if (!targetName.isEmpty()) {
                    int slot = findTalismanSlot(inventory, targetName);
                    if (slot != -1) {
                        client.interactionManager.clickSlot(
                            client.player.currentScreenHandler.syncId,
                            slot,
                            40, 
                            SlotActionType.SWAP,
                            client.player
                        );
                    }
                }
            }
        });
    }

    private int findTalismanSlot(PlayerInventory inventory, String targetName) {
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty() && stack.getName().getString().toLowerCase().contains(targetName)) {
                if (i < 9) return i + 36; 
                return i;
            }
        }
        return -1;
    }
}
