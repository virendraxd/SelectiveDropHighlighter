package com.knightgost.sdh.mixin.client;

import com.knightgost.sdh.SDHConfig;
import com.knightgost.sdh.SelectiveDropHighlighterClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class ItemGlowMixin {

    // This handles WHETHER the item glows
    @Inject(method = "isGlowing", at = @At("HEAD"), cancellable = true)
    private void sdh$makeSelectedItemsGlow(CallbackInfoReturnable<Boolean> cir) {
        if (!SelectiveDropHighlighterClient.isEnabled) return;

        if ((Object) this instanceof ItemEntity itemEntity) {
            var item = itemEntity.getStack().getItem();
            if (SDHConfig.HIGHLIGHTED_ITEMS.contains(item)) {
                cir.setReturnValue(true);
            }
        }
    }

    // This handles WHAT COLOR the glow is
    @Inject(method = "getTeamColorValue", at = @At("HEAD"), cancellable = true)
    private void sdh$applyRarityColor(CallbackInfoReturnable<Integer> cir) {
        if (!SelectiveDropHighlighterClient.isEnabled) return;

        if ((Object) this instanceof ItemEntity itemEntity) {
            var stack = itemEntity.getStack();

            // Only override the color if it's one of our highlighted items
            if (SDHConfig.HIGHLIGHTED_ITEMS.contains(stack.getItem())) {
                // --- CUSTOM OVERRIDES START HERE ---
                if (stack.getItem() == net.minecraft.item.Items.DIAMOND) {
                    cir.setReturnValue(0x33EBFF); // Diamond Blue
                    return; // Stop here so it doesn't run the rarity check below
                }

                if (stack.getItem() == net.minecraft.item.Items.NETHERITE_INGOT) {
                    cir.setReturnValue(0x733E39); // Netherite Brown/Red
                    return;
                }
                // --- CUSTOM OVERRIDES END HERE ---

                // 1. Get the rarity (Common, Uncommon, Rare, Epic)
                // 2. Get the Formatting associated with it (White, Yellow, Aqua, Light Purple)
                Formatting rarityFormatting = stack.getRarity().getFormatting();

                // 3. Convert the formatting to its Integer Color Value (Hex)
                Integer color = rarityFormatting.getColorValue();

                if (color != null) {
                    cir.setReturnValue(color);
                }
            }
        }
    }
}