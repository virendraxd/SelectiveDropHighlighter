package com.knightgost.sdh;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.YetAnotherConfigLib;

public class SDHMenu {
    public static Screen createScreen(Screen parent) {
        YetAnotherConfigLib.Builder builder = YetAnotherConfigLib.createBuilder()
                .title(Text.literal("SDH Settings"));

        // CATEGORY 1: Settings Tab (Global Toggles)
        ConfigCategory settingsCategory = ConfigCategory.createBuilder()
                .name(Text.literal("Settings"))
                .option(Option.<Boolean>createBuilder()
                        .name(Text.literal("Enable Highlighter"))
                        .binding(true, () -> SelectiveDropHighlighterClient.isEnabled,
                                val -> SelectiveDropHighlighterClient.isEnabled = val)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                .build();

        // CATEGORY 2: Highlighted Items Tab (Target for Search Bar)
        ConfigCategory.Builder listCategoryBuilder = ConfigCategory.createBuilder()
                .name(Text.literal("Highlighted Items"));

        if (!SDHConfig.HIGHLIGHTED_ITEMS.isEmpty()) {
            // Sort items alphabetically by their display name
            SDHConfig.HIGHLIGHTED_ITEMS.stream()
                    .sorted((a, b) -> a.getName().getString().compareToIgnoreCase(b.getName().getString()))
                    .forEach(item -> {
                        listCategoryBuilder.option(Option.<Boolean>createBuilder()
                                .name(item.getName())
                                .description(OptionDescription.of(Text.literal("Uncheck to remove this item.")))
                                .binding(
                                        true,
                                        () -> {
                                            //? if <1.21.11 {
                                            /*return SDHConfig.HIGHLIGHTED_ITEMS.stream().anyMatch(i ->
                                                net.minecraft.registry.Registries.ITEM.getId(i).equals(net.minecraft.registry.Registries.ITEM.getId(item)));
                                            *///? } else {
                                            return SDHConfig.HIGHLIGHTED_ITEMS.contains(item);
                                            //? }
                                        },
                                        val -> {
                                            if (!val) {
                                                //? if <1.21.11 {
                                                /*SDHConfig.HIGHLIGHTED_ITEMS.removeIf(i ->
                                                    net.minecraft.registry.Registries.ITEM.getId(i)
                                                    .equals(net.minecraft.registry.Registries.ITEM.getId(item))
                                                );
                                                *///? } else {
                                                SDHConfig.HIGHLIGHTED_ITEMS.remove(item);
                                                //? }
                                                SDHConfig.save();
                                            }
                                        }
                                )
                                .controller(TickBoxControllerBuilder::create)
                                .build());
                    });
        } else {
            // Fallback for empty list
            listCategoryBuilder.option(Option.<String>createBuilder()
                    .name(Text.literal("No Items Selected"))
                    .description(OptionDescription.of(Text.literal("Hover over an item and press H!")))
                    .binding("None", () -> "None", v -> {
                    })
                    .controller(dev.isxander.yacl3.api.controller.StringControllerBuilder::create)
                    .build());
        }

        return builder
                .category(settingsCategory)
                .category(listCategoryBuilder.build())
                .build()
                .generateScreen(parent);
    }
}