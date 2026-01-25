package com.saloeater.armageddon_tooltip_tier;

import com.saloeater.armageddon_tooltip_tier.config.ArmageddonConfig;
import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

@Mod(ArmageddonTooltipTier.MODID)
public class ArmageddonTooltipTier
{
    public static final String MODID = "armageddon_tooltip_tier";
    private static final Logger LOGGER = LogUtils.getLogger();

    public ArmageddonTooltipTier()
    {
        var forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.register(this);

        // Register config
        ArmageddonConfig.register();

        // Register client-side tooltip handler
        if (FMLEnvironment.dist == Dist.CLIENT) {
            forgeBus.register(new TooltipHandler());
            LOGGER.info("Armageddon Tooltip Handler registered");
        }
    }
}
