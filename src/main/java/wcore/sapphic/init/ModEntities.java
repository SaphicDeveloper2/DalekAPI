package wcore.sapphic.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sapphic.dalekapi.DalekapiMod;
import wcore.sapphic.entities.CybermanEntity;
import wcore.sapphic.entities.DalekEntity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, DalekapiMod.MODID);

    // Maps to hold the dynamically registered entity types
    public static final Map<ResourceLocation, RegistryObject<EntityType<DalekEntity>>> DALEK_ENTITIES = new ConcurrentHashMap<>();
    public static final Map<ResourceLocation, RegistryObject<EntityType<CybermanEntity>>> CYBERMAN_ENTITIES = new ConcurrentHashMap<>();


    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}

