package net.sapphic.dalekapi;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wcore.sapphic.api.registry.TypesRegistries;
import wcore.sapphic.commands.ReloadPacksCommand;
import wcore.sapphic.datapack.SonicManager;
import wcore.sapphic.init.ModEntities;
import wcore.sapphic.init.ModItems;
import wcore.sapphic.items.PackagedSonicItem;
import wcore.sapphic.packs.PackLoader;

@Mod(DalekapiMod.MODID)
public class DalekapiMod {
    public static final String MODID = "dalekapi";
    private static final Logger LOGGER = LogManager.getLogger(MODID);

    public DalekapiMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Load packs in the constructor to discover sonics before registration
        LOGGER.info("Initializing PackLoader for DalekAPI...");
        PackLoader.initialize();

        // Register our deferred registries for other things
        ModItems.register(modEventBus);
        ModEntities.register(modEventBus);
        TypesRegistries.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Common setup tasks can go here. Pack loading is now done earlier.
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEventBusEvents {
        /**
         * This is the modern Forge event for dynamic registration.
         * It fires after static registration, allowing us to register items
         * based on the packs we loaded in the constructor.
         */
        @SubscribeEvent
        public static void onRegisterItems(RegisterEvent event) {
            // We only want to run this for the Item registry
            if (event.getRegistryKey().equals(ForgeRegistries.Keys.ITEMS)) {
                LOGGER.info("Dynamically registering sonic screwdrivers...");
                SonicManager.INSTANCE.getDefinitions().forEach((id, definition) -> {
                    // For each sonic definition, create and register a new item
                    Item.Properties properties = new Item.Properties().stacksTo(1).rarity(Rarity.RARE);
                    PackagedSonicItem sonicItem = new PackagedSonicItem(properties, id);
                    event.register(ForgeRegistries.Keys.ITEMS, id, () -> sonicItem);
                    LOGGER.debug("Registered sonic item: {}", id);
                });
                LOGGER.info("Finished registering {} sonic screwdrivers.", SonicManager.INSTANCE.getDefinitions().size());
            }
        }
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onRegisterCommands(RegisterCommandsEvent event) {
            ReloadPacksCommand.register(event.getDispatcher());
        }

        @SubscribeEvent
        public static void onServerStarting(ServerStartingEvent event) {
            LOGGER.info("DalekAPI server is starting: {}", event.getServer().getServerModName());
        }
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientForgeEvents {
        // Client-only Forge events can go here
    }
}
