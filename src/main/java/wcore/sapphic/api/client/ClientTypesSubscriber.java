package wcore.sapphic.api.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegistryObject;
import net.sapphic.dalekapi.DalekapiMod;
import wcore.sapphic.api.registry.TypesRegistries;
import wcore.sapphic.api.registry.types.CybermanType;
import wcore.sapphic.api.registry.types.DalekType;
import wcore.sapphic.api.registry.types.SonicType;

/**
 * Client-side event subscriber responsible for bootstrapping renderers and other
 * client-specific features for custom types. It correctly hooks into the
 * appropriate mod bus events for safe and reliable registration.
 */
@Mod.EventBusSubscriber(modid = DalekapiMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientTypesSubscriber {

    private ClientTypesSubscriber() {
    }

    /**
     * Registers entity renderers for custom Dalek and Cyberman types.
     * This method is subscribed to the {@link EntityRenderersEvent.RegisterRenderers} event,
     * which is the correct and safe place to register any entity renderer.
     *
     * @param event The event provided by the Forge mod bus.
     */
    @SubscribeEvent
    public static void onRegisterRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        // Register Dalek renderers
        TypesRegistries.DALEK_TYPES.getEntries().stream()
                .map(RegistryObject::get)
                .forEach(dalekType -> dalekType.getRendererRegistrar().ifPresent(
                        registrar -> registrar.register(event, dalekType.getEntityType().get())
                ));

        // Register Cyberman renderers
        TypesRegistries.CYBERMAN_TYPES.getEntries().stream()
                .map(RegistryObject::get)
                .forEach(cybermanType -> cybermanType.getRendererRegistrar().ifPresent(
                        registrar -> registrar.register(event, cybermanType.getEntityType().get())
                ));
    }

    /**
     * Executes general client-side setup logic, such as registering item properties for Sonics.
     * This method is subscribed to the {@link FMLClientSetupEvent}, which runs after registries
     * are in place but before the game has fully loaded. Work is enqueued to ensure thread safety.
     *
     * @param event The event provided by the Forge mod bus.
     */
    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {
        // Run client-side hooks for Sonics (e.g., item properties, color handlers)
        event.enqueueWork(() ->
                TypesRegistries.SONIC_TYPES.getEntries().stream()
                        .map(RegistryObject::get)
                        .forEach(sonicType -> sonicType.getClientRegistrar()
                                .ifPresent(SonicType.ClientRegistrar::registerClient)
                        )
        );
    }
}
