package wcore.sapphic.init;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sapphic.dalekapi.DalekapiMod;
import wcore.sapphic.entities.DataCybermanEntity;
import wcore.sapphic.entities.DataDalekEntity;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, DalekapiMod.MODID);

    // Register the data-driven Dalek template entity
    public static final RegistryObject<EntityType<DataDalekEntity>> DATA_DALEK =
            ENTITY_TYPES.register("data_dalek",
                    () -> EntityType.Builder.of(DataDalekEntity::new, MobCategory.MONSTER)
                            .sized(0.9f, 1.9f).build("data_dalek"));

    // Register the data-driven Cyberman template entity
    public static final RegistryObject<EntityType<DataCybermanEntity>> DATA_CYBERMAN =
            ENTITY_TYPES.register("data_cyberman",
                    () -> EntityType.Builder.of(DataCybermanEntity::new, MobCategory.MONSTER)
                            .sized(0.6f, 1.95f).build("data_cyberman"));


    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }

    // This method is called from the main mod class to register the entity attributes
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(DATA_DALEK.get(), DataDalekEntity.createAttributes().build());
        event.put(DATA_CYBERMAN.get(), DataCybermanEntity.createAttributes().build());
    }
}