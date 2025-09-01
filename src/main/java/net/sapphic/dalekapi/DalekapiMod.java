package net.sapphic.dalekapi;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wcore.sapphic.datapack.CybermanManager;
import wcore.sapphic.datapack.DalekManager;
import wcore.sapphic.datapack.SonicManager;
import wcore.sapphic.init.ModEntities;
import wcore.sapphic.init.ModItems;
import wcore.sapphic.items.ExampleSonicItem;
import wcore.sapphic.renderers.BaseCybermanRenderer;
import wcore.sapphic.renderers.BaseDalekRenderer;
import wcore.sapphic.sonic.SonicItemRegistryFactory;

@Mod("dalekapi")
public class DalekapiMod {
    public static final Logger LOGGER = LogManager.getLogger(DalekapiMod.class);
    public static final String MODID = "dalekapi";

    private static RegistryObject<Item> exampleSonicItem;

    public DalekapiMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(bus);
        ModEntities.register(bus);
        bus.addListener(ModEntities::registerAttributes);

        registerSonics(bus);

        bus.addListener(this::addCreative);
        MinecraftForge.EVENT_BUS.register(this);

        // Run client-side setup for renderers
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            bus.addListener(this::registerRenderers);
        });
    }

    private void registerSonics(IEventBus bus) {
        SonicItemRegistryFactory sonicFactory = new SonicItemRegistryFactory(MODID);
        exampleSonicItem = SonicItemRegistryFactory.registerSonic("example_sonic_item", ExampleSonicItem::new);
        sonicFactory.register(bus);
    }

    // This method will now work without errors.
    private void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.DATA_DALEK.get(), BaseDalekRenderer::new);
        event.registerEntityRenderer(ModEntities.DATA_CYBERMAN.get(), BaseCybermanRenderer::new);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            if (exampleSonicItem != null) {
                event.accept(exampleSonicItem.get());
            }

            SonicManager.INSTANCE.getSonics().forEach((location, definition) -> {
                ItemStack stack = new ItemStack(ModItems.DATA_SONIC_ITEM.get());
                CompoundTag tag = new CompoundTag();
                tag.putString("sonic_id", location.toString());
                stack.setTag(tag);
                event.accept(stack);
            });
        }

        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            DalekManager.INSTANCE.getDaleks().forEach((location, definition) -> {
                ItemStack stack = new ItemStack(ModItems.DALEK_SPAWN_EGG.get());
                CompoundTag entityTag = new CompoundTag();
                entityTag.putString("definition_id", location.toString());
                stack.getOrCreateTag().put("EntityTag", entityTag);
                event.accept(stack);
            });

            CybermanManager.INSTANCE.getCybermen().forEach((location, definition) -> {
                ItemStack stack = new ItemStack(ModItems.CYBERMAN_SPAWN_EGG.get());
                CompoundTag entityTag = new CompoundTag();
                entityTag.putString("definition_id", location.toString());
                stack.getOrCreateTag().put("EntityTag", entityTag);
                event.accept(stack);
            });
        }
    }

    @SubscribeEvent
    public void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(SonicManager.INSTANCE);
        event.addListener(DalekManager.INSTANCE);
        event.addListener(CybermanManager.INSTANCE);
    }
}