package wcore.sapphic.api.registry.types;

import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;
import wcore.sapphic.ai.AbstractDalekEntity;
import wcore.sapphic.api.behavior.DalekBehavior;

import java.util.Objects;
import java.util.Optional;

/**
 * An immutable data object that defines the properties of a custom Dalek type.
 * <p>
 * This class is designed to be used with Forge's modern Deferred Register system.
 * Instead of extending {@code IForgeRegistryEntry}, you create instances of this class
 * and register them in a {@code DeferredRegister<DalekType>}.
 * This provides a clean, type-safe way to manage your custom Dalek definitions.
 */
public final class DalekType {

    /**
     * A functional interface responsible for registering the client-side renderer
     * for a Dalek entity type during the appropriate Forge event.
     */
    @FunctionalInterface
    public interface RendererRegistrar {
        /**
         * Registers the entity renderer.
         *
         * @param event The registration event provided by Forge.
         * @param type  The entity type to register a renderer for.
         */
        void register(
                net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers event,
                EntityType<? extends AbstractDalekEntity> type
        );
    }

    private final RegistryObject<EntityType<? extends AbstractDalekEntity>> entityType;
    @Nullable
    private final DalekBehavior defaultBehavior;
    @Nullable
    private final RendererRegistrar rendererRegistrar;

    /**
     * Private constructor to be used exclusively by the inner Builder class.
     */
    private DalekType(final Builder builder) {
        this.entityType = builder.entityType;
        this.defaultBehavior = builder.defaultBehavior;
        this.rendererRegistrar = builder.rendererRegistrar;
    }

    /**
     * @return The {@link RegistryObject} holding the associated Dalek entity type.
     */
    public RegistryObject<EntityType<? extends AbstractDalekEntity>> getEntityType() {
        return entityType;
    }

    /**
     * @return An {@link Optional} containing the default behavior, or empty if none was defined.
     */
    public Optional<DalekBehavior> getDefaultBehavior() {
        return Optional.ofNullable(defaultBehavior);
    }

    /**
     * @return An {@link Optional} containing the renderer registrar, or empty if none was defined.
     */
    public Optional<RendererRegistrar> getRendererRegistrar() {
        return Optional.ofNullable(rendererRegistrar);
    }

    /**
     * Creates a new {@link Builder} to construct a {@link DalekType}.
     *
     * @param entityType The {@link RegistryObject} for the Dalek's entity type. This is required.
     * @return A new Builder instance.
     */
    public static Builder builder(final RegistryObject<EntityType<? extends AbstractDalekEntity>> entityType) {
        return new Builder(entityType);
    }

    /**
     * A standard builder for creating immutable {@link DalekType} instances.
     */
    public static final class Builder {
        private final RegistryObject<EntityType<? extends AbstractDalekEntity>> entityType;
        @Nullable
        private DalekBehavior defaultBehavior;
        @Nullable
        private RendererRegistrar rendererRegistrar;

        private Builder(final RegistryObject<EntityType<? extends AbstractDalekEntity>> entityType) {
            this.entityType = Objects.requireNonNull(entityType, "EntityType RegistryObject cannot be null");
        }

        /**
         * Sets the default behavior for this Dalek type.
         *
         * @param behavior The default {@link DalekBehavior}.
         * @return This builder for chaining.
         */
        public Builder behavior(final DalekBehavior behavior) {
            this.defaultBehavior = behavior;
            return this;
        }

        /**
         * Sets the client-side renderer registration logic for this Dalek type.
         *
         * @param registrar The {@link RendererRegistrar} functional interface.
         * @return This builder for chaining.
         */
        public Builder renderer(final RendererRegistrar registrar) {
            this.rendererRegistrar = registrar;
            return this;
        }

        /**
         * Constructs the final, immutable {@link DalekType} instance.
         *
         * @return The newly created DalekType.
         */
        public DalekType build() {
            return new DalekType(this);
        }
    }
}
