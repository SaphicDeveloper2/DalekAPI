// Placed in your original package: wcore.sapphic.entities
package wcore.sapphic.entities;

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

import static net.sapphic.dalekapi.DalekapiMod.MODID;

// Correctly configured with your mod ID
@Mod.EventBusSubscriber(modid = "dalekapi", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class DalekRegistryFactory {

    private static DeferredRegister<EntityType<?>> entityTypeRegister = null;
    private static String modId = "dalekapi";
    private static final Map<Supplier<EntityType<? extends Mob>>, ResourceLocation> RENDERER_REGISTRY_MAP = new HashMap<>();


    public DalekRegistryFactory(String modId) {
        DalekRegistryFactory.modId = modId;
        entityTypeRegister = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, modId);
    }

    public static <T extends Mob> RegistryObject<EntityType<T>> registerDalek(String name, EntityType.EntityFactory<T> entitySupplier, String textureName) {
        RegistryObject<EntityType<T>> entityType = entityTypeRegister.register(name,
                () -> EntityType.Builder.of(entitySupplier, MobCategory.MONSTER)
                        .sized(0.9F, 1.8F)
                        .build(new ResourceLocation(modId, name).toString())
        );

        ResourceLocation texture = new ResourceLocation(modId, "textures/entity/" + textureName);
        RENDERER_REGISTRY_MAP.put(entityType::get, texture);

        return entityType;
    }

    public void register(IEventBus modEventBus) {
        entityTypeRegister.register(modEventBus);
    }

    @SubscribeEvent
    public static void onRegisterRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        RENDERER_REGISTRY_MAP.forEach((entityTypeSupplier, texture) -> {
            event.registerEntityRenderer(entityTypeSupplier.get(),
                    (context) -> new BaseDalekRenderer<>(context, texture)
            );
        });
        RENDERER_REGISTRY_MAP.clear();
    }
}
