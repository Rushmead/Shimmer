package com.lowdragmc.shimmer.neoforge;

import com.lowdragmc.shimmer.ShimmerConstants;
import com.lowdragmc.shimmer.neoforge.client.ClientProxy;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.IExtensionPoint;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforgespi.Environment;

@Mod(ShimmerConstants.MOD_ID)
public class ShimmerMod {

    public ShimmerMod(IEventBus modBus, ModContainer modContainer) {
        NeoForgeShimmerConfig.registerConfig(modContainer);

        if (Environment.get().getDist().isClient()) {
            new ClientProxy(modBus);
        } else {
            new CommonProxy(modBus);
        }
    }

    public static boolean isRubidiumLoaded() {
        return ModList.get().isLoaded("rubidium");
    }
}
