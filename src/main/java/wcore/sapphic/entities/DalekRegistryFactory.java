package wcore.sapphic.entities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import wcore.sapphic.ai.AbstractDalekEntity;

import java.util.function.Supplier;

public class DalekRegistryFactory {

    private final DeferredRegister<EntityType<?>> entityTypeRegister;
    private final DeferredRegister<Item> itemRegister;
    private final String modId;

    public DalekRegistryFactory(String modId) {
        this.modId = modId;
        this.entityTypeRegister = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, modId);
        this.itemRegister = DeferredRegister.create(ForgeRegistries.ITEMS, modId);
    }

    /**
     * Registers a new Dalek entity type, its attributes, and a spawn egg.
     * Renderer registration must now be done separately on the client side.
     *
     * @param name           The registry name for the dalek (e.g., "special_dalek").
     * @param entitySupplier A supplier for the new entity class (e.g., SpecialDalekEntity::new).
     * @param attributeSupplier A supplier for the entity's attributes.
     * @return A RegistryObject for the created EntityType.
     */
    public <T extends AbstractDalekEntity> RegistryObject<EntityType<T>> registerDalek(
            String name,
            EntityType.EntityFactory<T> entitySupplier,
            Supplier<AttributeSupplier.Builder> attributeSupplier) {

        RegistryObject<EntityType<T>> entityType = entityTypeRegister.register(name,
                () -> EntityType.Builder.of(entitySupplier, MobCategory.MONSTER)
                        .sized(0.9f, 1.9f)
                        .build(modId + ":" + name));

        // Register the spawn egg for this Dalek
        itemRegister.register(name + "_spawn_egg",
                () -> new ForgeSpawnEggItem(entityType, 0xC0C0C0, 0x808080, new Item.Properties()));

        // We can't register attributes directly here, so we need a separate event listener.
        // It's best to handle this in your main mod class alongside other attribute registrations.
        // FMLJavaModLoadingContext.get().getModEventBus().addListener((EntityAttributeCreationEvent event) -> {
        //    event.put(entityType.get(), attributeSupplier.get().build());
        // });

        return entityType;
    }

    public void register(IEventBus modEventBus) {
        entityTypeRegister.register(modEventBus);
        itemRegister.register(modEventBus);
    }
}