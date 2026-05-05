package com.knightgost.sdh;

import net.fabricmc.api.ModInitializer;

public class SelectiveDropHighlighter implements ModInitializer {

    @Override
    public void onInitialize() {
        // Load the config first so the metrics have data to read
        SDHConfig.load();
    }
}