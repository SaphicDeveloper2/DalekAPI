package net.sapphic.dalekapi;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import org.slf4j.Logger;
import wcore.sapphic.api.easy.client.EasyEntityClient;
import wcore.sapphic.commands.ReloadPacksCommand;
import wcore.sapphic.datapack.CybermanManager;
import wcore.sapphic.datapack.DalekManager;
import wcore.sapphic.datapack.SonicManager;
import wcore.sapphic.entities.CybermanEntity;
import wcore.sapphic.entities.DalekEntity;
import wcore.sapphic.init.ModEntities;
import wcore.sapphic.init.ModItems;
import wcore.sapphic.items.PackagedSonicItem;
import wcore.sapphic.packs.PackAssetManager;
import wcore.sapphic.packs.PackLoader;
import wcore.sapphic.renderers.BaseCybermanRenderer;
import wcore.sapphic.renderers.BaseDalekRenderer;
import wcore.sapphic.api.easy.EasyEntityRegistry;


@Mod(DalekapiMod.MODID)
public class DalekapiMod {
    public static final String MODID = "dalekapi";
    private static final Logger LOGGER = LogUtils.getLogger();

    public DalekapiMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        LOGGER.info("Initializing PackLoader for DalekAPI...");
        PackLoader.initialize();

        EasyEntityRegistry.init(MODID, modEventBus);
        ModItems.register(modEventBus);
        ModEntities.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreative);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // This is where we can register our virtual datapack
        });
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        // Add items to creative tabs here
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEventBusEvents {
        @SubscribeEvent
        public static void onRegister(RegisterEvent event) {
            if (event.getRegistryKey().equals(ForgeRegistries.Keys.ITEMS)) {
                LOGGER.info("Dynamically registering items from packs...");
                // Register Sonics
                SonicManager.INSTANCE.getDefinitions().forEach((id, definition) -> {
                    event.register(ForgeRegistries.Keys.ITEMS, id, () -> new PackagedSonicItem(new Item.Properties().stacksTo(1).rarity(Rarity.RARE), id));
                });
                // Register Spawn Eggs
                DalekManager.INSTANCE.getDefinitions().forEach((id, definition) -> {
                    int primaryColor = definition.getSpawnEgg().getPrimaryColor();
                    int secondaryColor = definition.getSpawnEgg().getSecondaryColor();
                    event.register(ForgeRegistries.Keys.ITEMS, id.withSuffix("_spawn_egg"), () -> new SpawnEggItem(ModEntities.DALEK_ENTITIES.get(id).get(), primaryColor, secondaryColor, new Item.Properties()));
                });
                CybermanManager.INSTANCE.getDefinitions().forEach((id, definition) -> {
                    int primaryColor = definition.getSpawnEgg().getPrimaryColor();
                    int secondaryColor = definition.getSpawnEgg().getSecondaryColor();
                    event.register(ForgeRegistries.Keys.ITEMS, id.withSuffix("_spawn_egg"), () -> new SpawnEggItem(ModEntities.CYBERMAN_ENTITIES.get(id).get(), primaryColor, secondaryColor, new Item.Properties()));
                });
            }

            if (event.getRegistryKey().equals(ForgeRegistries.Keys.ENTITY_TYPES)) {
                LOGGER.info("Dynamically registering entities from packs...");
                DalekManager.INSTANCE.getDefinitions().forEach((id, definition) ->
                        ModEntities.DALEK_ENTITIES.put(id, EasyEntityRegistry.registerAdvancedMob(
                                id.getPath(),
                                definition.getMobCategory(),
                                0.9f, 1.9f,
                                DalekEntity::new,
                                DalekEntity.createAttributes()
                        ))
                );
                CybermanManager.INSTANCE.getDefinitions().forEach((id, definition) ->
                        ModEntities.CYBERMAN_ENTITIES.put(id, EasyEntityRegistry.registerAdvancedMob(
                                id.getPath(),
                                definition.getMobCategory(),
                                0.6f, 1.95f,
                                CybermanEntity::new,
                                CybermanEntity.createAttributes()
                        ))
                );
            }
        }
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                ModEntities.DALEK_ENTITIES.values().forEach(ro -> EasyEntityClient.registerRenderer(ro, BaseDalekRenderer::new));
                ModEntities.CYBERMAN_ENTITIES.values().forEach(ro -> EasyEntityClient.registerRenderer(ro, BaseCybermanRenderer::new));
            });
        }

        @SubscribeEvent
        public static void onRegisterModels(ModelEvent.RegisterAdditional event) {
            // For every sonic we registered, tell the game to use our generic model file.
            // This signals that a custom BEWLR will handle the rendering.
            SonicManager.INSTANCE.getDefinitions().keySet().forEach(id -> {
                event.register(new ResourceLocation(id.getNamespace(), "item/" + id.getPath()));
            });
        }

        @SubscribeEvent
        public static void onModelBake(ModelEvent.BakingCompleted event) {
            // This is the crucial step that was missing.
            // It tells our PackAssetManager to "bake" all the loaded models
            // so they can be rendered in-game.
            PackAssetManager.INSTANCE.onBake(event.getModelBakery(), event.getModelManager());
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
}

