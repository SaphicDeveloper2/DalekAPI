package net.sapphic.dalekapi;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import wcore.sapphic.datapack.CybermanManager;
import wcore.sapphic.datapack.DalekManager;
import wcore.sapphic.datapack.SonicManager;
import wcore.sapphic.init.ModEntities;
import wcore.sapphic.init.ModItems;

@Mod(DalekapiMod.MODID)
public class DalekapiMod {
    public static final String MODID = "dalekapi";
    private static final Logger LOGGER = LogUtils.getLogger();

    public DalekapiMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register our items and entities.
        ModItems.register(modEventBus);
        ModEntities.register(modEventBus);

        // Register ourselves to the main event bus to listen for events like server starting.
        MinecraftForge.EVENT_BUS.register(this);
    }

    // This event is crucial. It adds our data managers to the game's reload sequence.
    @SubscribeEvent
    public void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(new SonicManager());
        event.addListener(new DalekManager());
        event.addListener(new CybermanManager());
        LOGGER.info("DalekAPI data managers registered.");
    }
}