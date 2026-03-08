package com.knightgost.sdh;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class SDHConfig {
    // List that stores highlighted items
    public static final Set<Item> HIGHLIGHTED_ITEMS = new HashSet<>();

    // File save path (.minecraft/config/sdh_config.json)
    private static final Path CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("sdh_config.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // File SAVE method
    public static void save() {
        try (Writer writer = new FileWriter(CONFIG_FILE.toFile())) {
            // Save IDs (like "minecraft:diamond") instead of Item object itself
            List<String> ids = HIGHLIGHTED_ITEMS.stream()
                    .map(item -> Registries.ITEM.getId(item).toString())
                    .toList();
            GSON.toJson(ids, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // File LOAD method
    public static void load() {
        if (!CONFIG_FILE.toFile().exists()) return;

        try (Reader reader = new FileReader(CONFIG_FILE.toFile())) {
            List<String> ids = GSON.fromJson(reader, new TypeToken<List<String>>(){}.getType());
            HIGHLIGHTED_ITEMS.clear();
            if (ids != null) {
                for (String id : ids) {
                    HIGHLIGHTED_ITEMS.add(Registries.ITEM.get(Identifier.of(id)));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean shouldHighlight(Item item) {
        return HIGHLIGHTED_ITEMS.contains(item);
    }
}