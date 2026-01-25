package com.saloeater.armageddon_tooltip_tier;

import com.saloeater.armageddon_tooltip_tier.config.ArmageddonConfig;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class Events {

    @SubscribeEvent
    public static void onModConfigLoading(ModConfigEvent.Loading event) {
        var a = event.getConfig().getConfigData().get("version.config_version");
        if (a != ArmageddonConfig.VERSION) {
            event.getConfig().getConfigData().set("version.config_version", ArmageddonConfig.VERSION);
            event.getConfig().getConfigData().set("general.tags", ArmageddonConfig.CLIENT.tags.getDefault());
            event.getConfig().save();
        }
    }
}
