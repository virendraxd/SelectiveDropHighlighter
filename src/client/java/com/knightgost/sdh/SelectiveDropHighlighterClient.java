package com.knightgost.sdh;

import com.knightgost.sdh.mixin.client.HandledScreenAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.InputUtil;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class SelectiveDropHighlighterClient implements ClientModInitializer {

    public static KeyBinding toggleKey;
    public static KeyBinding menuKey;
    public static boolean isEnabled = true;

    // This variable prevents the key from firing 20 times a second while held
    private static boolean wasHeld = false;

    // Settings>Controls>Key_Binds here
    //? if >=1.21.9 {
    public static final KeyBinding.Category SDH_CATEGORY = KeyBinding.Category.create(
            net.minecraft.util.Identifier.of("selective-drop-highlighter", "general")
    );
    //? } else {
    /*public static final String SDH_CATEGORY = "category.selective-drop-highlighter.general";
    *///? }

    private static void playPling(net.minecraft.client.MinecraftClient client, float pitch) {
        //? if >=1.21.11 {
        client.getSoundManager().play(PositionedSoundInstance.ui(
                SoundEvents.BLOCK_NOTE_BLOCK_PLING, pitch));
        //?} else {
        /*client.getSoundManager().play(PositionedSoundInstance.master(
            SoundEvents.BLOCK_NOTE_BLOCK_PLING, pitch));
        *///?}
    }

    @Override
    public void onInitializeClient() {
        SDHConfig.load();

        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.sdh.toggle",
                GLFW.GLFW_KEY_H,
                SDH_CATEGORY
        ));

        menuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.sdh.menu",
                GLFW.GLFW_KEY_O,
                SDH_CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            InputUtil.Key boundKey = KeyBindingHelper.getBoundKeyOf(toggleKey);
            // Now check if THAT specific key is being held
            //? if >=1.21.9 {
            boolean isHeld = InputUtil.isKeyPressed(client.getWindow(), boundKey.getCode());
            //? } else {
            /*boolean isHeld = InputUtil.isKeyPressed(client.getWindow().getHandle(), boundKey.getCode());
            *///? }

            // Only trigger once per physical press
            if (isHeld && !wasHeld) {

                // CASE 1: In Inventory/Screen
                if (client.currentScreen instanceof HandledScreen<?> screen) {
                    var slot = ((HandledScreenAccessor) screen).getFocusedSlot();

                    if (slot != null && slot.hasStack()) {
                        var item = slot.getStack().getItem();
                        String itemName = item.getName().getString();

                        if (SDHConfig.HIGHLIGHTED_ITEMS.contains(item)) {
                            // REMOVE logic
                            SDHConfig.HIGHLIGHTED_ITEMS.remove(item);
                            client.player.sendMessage(Text.literal("§eRemoved §6" + itemName), true);

                            // Low pitch pling for removal
                            playPling(client, 0.8f);
                        } else {
                            // ADD logic
                            SDHConfig.HIGHLIGHTED_ITEMS.add(item);
                            client.player.sendMessage(Text.literal("§aAdded §6" + itemName), true);

                            // High pitch pling for success
                            playPling(client, 1.2f);
                        }
                        SDHConfig.save();
                    }
                }
                // CASE 2: Walking around the world
                else if (client.currentScreen == null) {
                    isEnabled = !isEnabled;

                    // Simple pling for toggle
                    playPling(client, 1.0f);

                    client.player.sendMessage(Text.literal(
                            "§6[SDH] §fHighlighter: " + (isEnabled ? "§aON" : "§cOFF")
                    ), true);
                }
            }
            while (menuKey.wasPressed()) {
                // Open new custom screen
                client.setScreen(SDHMenu.createScreen(client.currentScreen));
            }

            // Sync the held state
            wasHeld = isHeld;
        });
    }
}