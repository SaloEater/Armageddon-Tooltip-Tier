package com.saloeater.armageddon_tooltip_tier;

import com.saloeater.armageddon_tooltip_tier.config.ArmageddonConfig;
import com.saloeater.armageddon_tooltip_tier.mixin.ClientAdvancementsAccessor;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class TooltipHandler {

    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        ItemStack itemStack = event.getItemStack();
        List<ArmageddonConfig.TagEntry> tagEntries = ArmageddonConfig.CLIENT.getTagEntries();

        for (ArmageddonConfig.TagEntry entry : tagEntries) {
            // Check if the item has any of the tags
            boolean hasAnyTag = false;
            for (var tagKey : entry.getTagKeys()) {
                if (itemStack.is(tagKey)) {
                    hasAnyTag = true;
                    break;
                }
            }

            if (hasAnyTag) {
                // Check if player has the advancement
                MutableComponent locked = getLockedComponent(player, entry.getAdvancement());

                // Item is locked
                event.getToolTip().add(Component.literal(""));

                event.getToolTip().add(locked
                        .append(Component.literal(" "))
                        .append(Component.translatable(entry.getLabel())));

                // Only show tooltip for the first matching tag
                break;
            }
        }
    }

    private MutableComponent getLockedComponent(LocalPlayer player, ResourceLocation advancement) {
        boolean hasAdvancement = playerHasAdvancement(player, advancement);
        String unlocked = hasAdvancement ? "armageddon_tooltip_tier.unlocked_by" : "armageddon_tooltip_tier.locked_by";
        ChatFormatting color = hasAdvancement ? ChatFormatting.DARK_GREEN : ChatFormatting.RED;

        return Component.translatable(unlocked).withStyle(color);
    }

    private boolean playerHasAdvancement(LocalPlayer player, ResourceLocation advancementId) {
        var advancementManager = player.connection.getAdvancements();
        var advancementAccessor = (ClientAdvancementsAccessor) advancementManager;
        for (var advancement : advancementAccessor.getProgress().keySet()) {
            if (advancement.getId().equals(advancementId)) {
                var progress = advancementAccessor.getProgress().get(advancement);
                return progress != null && progress.isDone();
            }
        }

        return false;
    }
}
