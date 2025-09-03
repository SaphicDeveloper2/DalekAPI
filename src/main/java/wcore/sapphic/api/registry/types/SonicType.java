package wcore.sapphic.api.registry.types;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;
import wcore.sapphic.api.behavior.SonicBehavior;

import java.util.Objects;
import java.util.Optional;

/**
 * An immutable data object that defines the properties of a custom Sonic Device type.
 * <p>
 * This class is designed to be used with Forge's modern Deferred Register system.
 * Instead of extending {@code IForgeRegistryEntry}, you create instances of this class
 * and register them in a {@code DeferredRegister<SonicType>}.
 * This provides a clean, type-safe way to manage your custom sonic definitions.
 */
public final class SonicType {

    /**
     * A functional interface responsible for running client-side setup for a sonic item,
     * such as registering item properties or special renderers.
     */
    @FunctionalInterface
    public interface ClientRegistrar {
        /**
         * Runs client-side registration logic. This is typically called during the
         * {@code FMLClientSetupEvent}.
         */
        void registerClient();
    }

    private final RegistryObject<Item> item;
    @Nullable
    private final SonicBehavior defaultBehavior;
    @Nullable
    private final ClientRegistrar clientRegistrar;

    /**
     * Private constructor to be used exclusively by the inner Builder class.
     */
    private SonicType(final Builder builder) {
        this.item = builder.item;
        this.defaultBehavior = builder.defaultBehavior;
        this.clientRegistrar = builder.clientRegistrar;
    }

    /**
     * @return The {@link RegistryObject} holding the associated sonic item.
     */
    public RegistryObject<Item> getItem() {
        return item;
    }

    /**
     * @return An {@link Optional} containing the default behavior, or empty if none was defined.
     */
    public Optional<SonicBehavior> getDefaultBehavior() {
        return Optional.ofNullable(defaultBehavior);
    }

    /**
     * @return An {@link Optional} containing the client registrar, or empty if none was defined.
     */
    public Optional<ClientRegistrar> getClientRegistrar() {
        return Optional.ofNullable(clientRegistrar);
    }

    /**
     * Creates a new {@link Builder} to construct a {@link SonicType}.
     *
     * @param item The {@link RegistryObject} for the sonic's item. This is required.
     * @return A new Builder instance.
     */
    public static Builder builder(final RegistryObject<Item> item) {
        return new Builder(item);
    }

    /**
     * A standard builder for creating immutable {@link SonicType} instances.
     */
    public static final class Builder {
        private final RegistryObject<Item> item;
        @Nullable
        private SonicBehavior defaultBehavior;
        @Nullable
        private ClientRegistrar clientRegistrar;

        private Builder(final RegistryObject<Item> item) {
            this.item = Objects.requireNonNull(item, "Item RegistryObject cannot be null");
        }

        /**
         * Sets the default behavior for this sonic type.
         *
         * @param behavior The default {@link SonicBehavior}.
         * @return This builder for chaining.
         */
        public Builder behavior(final SonicBehavior behavior) {
            this.defaultBehavior = behavior;
            return this;
        }

        /**
         * Sets the client-side registration logic for this sonic type.
         *
         * @param registrar The {@link ClientRegistrar} functional interface.
         * @return This builder for chaining.
         */
        public Builder client(final ClientRegistrar registrar) {
            this.clientRegistrar = registrar;
            return this;
        }

        /**
         * Constructs the final, immutable {@link SonicType} instance.
         *
         * @return The newly created SonicType.
         */
        public SonicType build() {
            return new SonicType(this);
        }
    }
}
