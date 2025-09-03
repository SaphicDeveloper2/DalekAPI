package wcore.sapphic.api.easy.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.entity.EntityType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Client-only registration for renderers and layer definitions.
 * Call EasyEntityRegistry.init(...) on common; this class is wired automatically on client.
 */
public final class EasyEntityClient {

    private EasyEntityClient() {}

    private static final List<Runnable> RENDER_REGISTRATIONS = new ArrayList<>();
    private static final List<java.util.function.Consumer<EntityRenderersEvent.RegisterLayerDefinitions>> LAYER_REGISTRATIONS = new ArrayList<>();

    public static void register(IEventBus modBus) {
        modBus.addListener(EasyEntityClient::onRegisterRenderers);
        modBus.addListener(EasyEntityClient::onRegisterLayerDefinitions);
    }

    public static <T extends net.minecraft.world.entity.Mob> void registerRenderer(
            RegistryObject<EntityType<T>> type,
            EntityRendererProvider<T> provider
    ) {
        RENDER_REGISTRATIONS.add(() -> {
            net.minecraft.world.entity.EntityType<T> resolved = type.get();
            PROVIDERS.add(new ProviderEntry<>(resolved, provider));
        });
    }

    public static void registerLayerDefinition(
            net.minecraft.client.model.geom.ModelLayerLocation layer,
            Supplier<net.minecraft.client.model.geom.builders.LayerDefinition> supplier
    ) {
        LAYER_REGISTRATIONS.add(event -> event.registerLayerDefinition(layer, supplier));
    }

    private static final List<ProviderEntry<?>> PROVIDERS = new ArrayList<>();

    private static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // Execute deferred population first
        RENDER_REGISTRATIONS.forEach(Runnable::run);
        for (ProviderEntry<?> entry : PROVIDERS) {
            registerProvider(event, entry);
        }
    }

    private static void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        for (var consumer : LAYER_REGISTRATIONS) {
            consumer.accept(event);
        }
    }

    private static <T extends net.minecraft.world.entity.Entity> void registerProvider(
            EntityRenderersEvent.RegisterRenderers event,
            ProviderEntry<T> entry
    ) {
        event.registerEntityRenderer((EntityType<? extends T>) entry.type, (EntityRendererProvider<? super T>) entry.provider);
    }

    private static final class ProviderEntry<T extends net.minecraft.world.entity.Entity> {
        final EntityType<T> type;
        final EntityRendererProvider<T> provider;

        private ProviderEntry(EntityType<T> type, EntityRendererProvider<T> provider) {
            this.type = type;
            this.provider = provider;
        }
    }
}
