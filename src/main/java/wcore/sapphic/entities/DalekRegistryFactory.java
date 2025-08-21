// Placed in your original package: wcore.sapphic.entities
package wcore.sapphic.entities;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import wcore.sapphic.renderers.BaseDalekRenderer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

// Correctly configured with your mod ID
@Mod.EventBusSubscriber(modid = "dalekapi", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class DalekRegistryFactory {

    private static DeferredRegister<EntityType<?>> entityTypeRegister = null;
    private static String modId = "dalekapi";

    // This map now stores the provider for the renderer, not just the texture.
    // This gives us the flexibility to store any kind of renderer.
    private static final Map<Supplier<EntityType<? extends Mob>>, EntityRendererProvider<?>> RENDERER_PROVIDER_MAP = new HashMap<>();

    public DalekRegistryFactory(String modId) {
        DalekRegistryFactory.modId = modId;
        entityTypeRegister = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, modId);
    }

    /**
     * The simple way to register a Dalek.
     * This automatically uses the BaseDalekRenderer with your specified texture.
     * Use this for Daleks that only need a different texture.
     *
     * @param name           The registry name for the Dalek (e.g., "paladin_dalek").
     * @param entitySupplier The constructor for your Dalek entity class (e.g., DalekEntityPaladin::new).
     * @param textureName    The name of the texture file in 'assets/your_mod_id/textures/entity/'.
     * @return A RegistryObject for the created EntityType.
     */
    public static <T extends Mob> RegistryObject<EntityType<T>> registerDalek(String name, EntityType.EntityFactory<T> entitySupplier, String textureName) {
        // Create the texture resource location.
        ResourceLocation texture = new ResourceLocation(modId, "textures/entity/" + textureName);
        // This creates a default renderer provider that uses BaseDalekRenderer.
        EntityRendererProvider<T> rendererProvider = (context) -> new BaseDalekRenderer<>(context, texture);

        // Call the advanced registration method with our default provider.
        return registerDalek(name, entitySupplier, rendererProvider);
    }

    /**
     * The advanced way to register a Dalek with a completely custom renderer.
     * Use this when you need more than just a texture swap (e.g., custom animations, layers, etc.).
     *
     * @param name             The registry name for the Dalek.
     * @param entitySupplier   The constructor for your Dalek entity class.
     * @param rendererProvider A provider for your custom renderer class (e.g., MyCustomDalekRenderer::new).
     * @return A RegistryObject for the created EntityType.
     */
    public static <T extends Mob> RegistryObject<EntityType<T>> registerDalek(String name, EntityType.EntityFactory<T> entitySupplier, EntityRendererProvider<T> rendererProvider) {
        RegistryObject<EntityType<T>> entityType = entityTypeRegister.register(name,
                () -> EntityType.Builder.of(entitySupplier, MobCategory.MONSTER)
                        .sized(0.9F, 1.8F)
                        .build(new ResourceLocation(modId, name).toString())
        );

        // Store the renderer provider (either the default or the custom one) in the map.
        RENDERER_PROVIDER_MAP.put(entityType::get, rendererProvider);

        return entityType;
    }


    public void register(IEventBus modEventBus) {
        entityTypeRegister.register(modEventBus);
    }

    @SubscribeEvent
    @SuppressWarnings({"unchecked", "rawtypes"}) // Suppress warnings for the necessary cast
    public static void onRegisterRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        // This now registers any type of renderer provider from our map.
        RENDERER_PROVIDER_MAP.forEach((entityTypeSupplier, provider) -> {
            event.registerEntityRenderer(entityTypeSupplier.get(), (EntityRendererProvider) provider);
        });
        RENDERER_PROVIDER_MAP.clear(); // Clear the map after use
    }
}