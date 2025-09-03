package wcore.sapphic.api.easy;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import wcore.sapphic.api.easy.client.EasyEntityClient;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * A simple API to register entities with minimal boilerplate for Forge 1.20.1.
 * <p>
 * This utility handles the creation of {@link EntityType}s, registration of their attributes,
 * and wiring for client-side renderers.
 * <p>
 * - {@link #registerSimpleMob}: Uses {@link GenericMobEntity} and accepts lambdas for AI/tick logic and attributes.
 * - {@link #registerAdvancedMob}: Accepts a custom entity class/factory while still handling attribute registration.
 * <p>
 * Client-side renderer and model setup is delegated to {@link EasyEntityClient}.
 * <p>
 * Usage: Call {@link #init(String, IEventBus)} in your main mod class constructor.
 */
public final class EasyEntityRegistry {

    private EasyEntityRegistry() {}

    private static String MODID;
    private static DeferredRegister<EntityType<?>> ENTITIES;

    // Map to hold attribute suppliers before the registration event.
    // Keyed by EntityType instance; this relies on object identity during setup, which is reliable in this context.
    private static final Map<EntityType<? extends Mob>, AttributeSupplier> ATTRIBUTES = new ConcurrentHashMap<>();

    // Maps to hold hooks for GenericMobEntity instances.
    private static final Map<EntityType<GenericMobEntity>, GenericMobEntity.GoalsConfigurer<GenericMobEntity>> GOALS = new ConcurrentHashMap<>();
    private static final Map<EntityType<GenericMobEntity>, GenericMobEntity.TickHandler<GenericMobEntity>> TICKS = new ConcurrentHashMap<>();

    /**
     * Initializes the entity registry system. This must be called from your mod's constructor
     * before any entities are registered.
     *
     * @param modid  Your mod's ID.
     * @param modBus The mod event bus from your main mod class.
     */
    public static void init(final String modid, final IEventBus modBus) {
        if (ENTITIES != null) {
            if (!Objects.equals(MODID, modid)) {
                throw new IllegalStateException("EasyEntityRegistry already initialized for modid=" + MODID + " but received new modid=" + modid);
            }
            return; // Already initialized for the same modid
        }
        MODID = Objects.requireNonNull(modid, "modid cannot be null");
        ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, modid);
        ENTITIES.register(modBus);

        // Register the listener for the common-side attribute event
        modBus.addListener(EasyEntityRegistry::onRegisterAttributes);
        // Register client-side setup for renderers and layers
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> () -> EasyEntityClient.register(modBus));
    }

    /**
     * Registers a simple mob that uses the {@link GenericMobEntity} class.
     * This is the easiest way to create a new entity with custom AI and logic without a dedicated entity class.
     *
     * @param name           The registry name for the entity (e.g., "my_mob").
     * @param category       The {@link MobCategory} (e.g., MONSTER, CREATURE).
     * @param width          The width of the entity's bounding box.
     * @param height         The height of the entity's bounding box.
     * @param attributes     A builder for the entity's attributes (e.g., health, speed).
     * @param aiConfigurer   A consumer that registers AI goals and targets. Can be null for no AI.
     * @param tickHandler    A consumer that runs custom logic every tick. Can be null.
     * @return A {@link RegistryObject} for the new entity type.
     */
    public static RegistryObject<EntityType<GenericMobEntity>> registerSimpleMob(
            final String name,
            final MobCategory category,
            final float width,
            final float height,
            final AttributeSupplier.Builder attributes,
            final Consumer<SimpleAI<GenericMobEntity>> aiConfigurer,
            final Consumer<GenericMobEntity> tickHandler
    ) {
        ensureInit();
        validateMobParameters(name, category, width, height, attributes);

        // Using the GenericMobEntity::new method reference is cleaner and equivalent to the lambda factory.
        final EntityType<GenericMobEntity> built = EntityType.Builder
                .of(GenericMobEntity::new, category)
                .sized(width, height)
                .build(new ResourceLocation(MODID, name).toString());

        // Wire attributes, AI, and tick handlers for this specific entity type.
        ATTRIBUTES.put(built, attributes.build());
        if (aiConfigurer != null) {
            GOALS.put(built, mob -> aiConfigurer.accept(new SimpleAI<>(mob)));
        }
        if (tickHandler != null) {
            TICKS.put(built, tickHandler::accept);
        }

        return ENTITIES.register(name, () -> built);
    }

    /**
     * Registers a more advanced mob that uses a custom entity class.
     * Use this when you need custom data, methods, or more complex behavior than {@link GenericMobEntity} provides.
     *
     * @param name       The registry name for the entity (e.g., "my_advanced_mob").
     * @param category   The {@link MobCategory} (e.g., MONSTER, CREATURE).
     * @param width      The width of the entity's bounding box.
     * @param height     The height of the entity's bounding box.
     * @param factory    The factory method for creating an instance of your entity (e.g., {@code MyEntity::new}).
     * @param attributes A builder for the entity's attributes (e.g., health, speed).
     * @param <T>        The type of your custom Mob class.
     * @return A {@link RegistryObject} for the new entity type.
     */
    public static <T extends Mob> RegistryObject<EntityType<T>> registerAdvancedMob(
            final String name,
            final MobCategory category,
            final float width,
            final float height,
            final EntityType.EntityFactory<T> factory,
            final AttributeSupplier.Builder attributes
    ) {
        ensureInit();
        validateMobParameters(name, category, width, height, attributes);
        Objects.requireNonNull(factory, "factory cannot be null");

        final EntityType<T> built = EntityType.Builder
                .of(factory, category)
                .sized(width, height)
                .build(new ResourceLocation(MODID, name).toString());

        // Only wire attributes, as AI and other logic should be handled within the custom entity class.
        ATTRIBUTES.put(built, attributes.build());

        return ENTITIES.register(name, () -> built);
    }

    // ---- Internal methods accessed by GenericMobEntity at runtime ----

    public static Optional<GenericMobEntity.GoalsConfigurer<GenericMobEntity>> getGoalsConfigurer(final EntityType<?> type) {
        // This cast is safe because the GOALS map is only populated with EntityType<GenericMobEntity> keys.
        return Optional.ofNullable(GOALS.get(type));
    }

    public static Optional<GenericMobEntity.TickHandler<GenericMobEntity>> getTickHandler(final EntityType<?> type) {
        // This cast is safe for the same reason as above.
        return Optional.ofNullable(TICKS.get(type));
    }

    // ---- Forge Event Handlers ----

    /**
     * Event listener that registers the attributes for all created entities.
     */
    private static void onRegisterAttributes(final EntityAttributeCreationEvent event) {
        ATTRIBUTES.forEach(event::put);
    }

    // ---- Validation and Helper Methods ----

    /**
     * Ensures that the init method has been called before registration is attempted.
     */
    private static void ensureInit() {
        if (ENTITIES == null) {
            throw new IllegalStateException("EasyEntityRegistry.init(modid, modBus) must be called from your mod constructor before registering entities.");
        }
    }

    /**
     * Centralized validation for common mob registration parameters.
     */
    private static void validateMobParameters(final String name, final MobCategory category, final float width, final float height, final Object attributes) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Entity name must be a non-empty string");
        }
        Objects.requireNonNull(category, "MobCategory cannot be null");
        Objects.requireNonNull(attributes, "AttributeSupplier.Builder cannot be null");
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Entity width and height must be positive");
        }
    }
}
