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
import wcore.sapphic.ai.AbstractCybermanEntity;

import java.util.function.Supplier;

public class CybermanRegistryFactory {

    private final DeferredRegister<EntityType<?>> entityTypeRegister;
    private final DeferredRegister<Item> itemRegister;
    private final String modId;

    public CybermanRegistryFactory(String modId) {
        this.modId = modId;
        this.entityTypeRegister = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, modId);
        this.itemRegister = DeferredRegister.create(ForgeRegistries.ITEMS, modId);
    }

    /**
     * Registers a new Cyberman entity type, its attributes, and a spawn egg.
     * Renderer registration must now be done separately on the client side.
     *
     * @param name           The registry name for the cyberman (e.g., "mondasian_cyberman").
     * @param entitySupplier A supplier for the new entity class (e.g., MondasianCybermanEntity::new).
     * @param attributeSupplier A supplier for the entity's attributes.
     * @return A RegistryObject for the created EntityType.
     */
    public <T extends AbstractCybermanEntity> RegistryObject<EntityType<T>> registerCyberman(
            String name,
            EntityType.EntityFactory<T> entitySupplier,
            Supplier<AttributeSupplier.Builder> attributeSupplier) {

        RegistryObject<EntityType<T>> entityType = entityTypeRegister.register(name,
                () -> EntityType.Builder.of(entitySupplier, MobCategory.MONSTER)
                        .sized(0.6f, 1.95f)
                        .build(modId + ":" + name));

        // Register the spawn egg for this Cyberman
        itemRegister.register(name + "_spawn_egg",
                () -> new ForgeSpawnEggItem(entityType, 0xA9A9A9, 0x000000, new Item.Properties()));

        return entityType;
    }

    public void register(IEventBus modEventBus) {
        entityTypeRegister.register(modEventBus);
        itemRegister.register(modEventBus);
    }
}