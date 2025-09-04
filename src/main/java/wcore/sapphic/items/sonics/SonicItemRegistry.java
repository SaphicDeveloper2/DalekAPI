package wcore.sapphic.items.sonics;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;
import wcore.sapphic.items.PackagedSonicItem;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class SonicItemRegistry {
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * A factory for creating instances of sonic items.
     * Coders can implement this to provide their own subclasses of PackagedSonicItem.
     */
    @FunctionalInterface
    public interface SonicItemFactory {
        PackagedSonicItem create(Item.Properties properties, ResourceLocation definitionId);
    }

    private static final Map<ResourceLocation, SonicItemFactory> FACTORIES = new ConcurrentHashMap<>();
    /** The default factory used for all sonics that do not specify a custom java_type. */
    public static final SonicItemFactory DEFAULT_FACTORY = PackagedSonicItem::new;

    /**
     * Registers a new factory for a custom sonic item type.
     * This allows a custom class to be used when a sonic.json specifies the corresponding java_type.
     *
     * @param javaType The ID to associate with this factory.
     * @param factory  The factory instance, typically a constructor reference like MySonicItem::new.
     */
    public static void register(ResourceLocation javaType, SonicItemFactory factory) {
        if (FACTORIES.containsKey(javaType)) {
            LOGGER.warn("SonicItemFactory for ID '{}' is being overwritten. This may be unintentional.", javaType);
        }
        FACTORIES.put(javaType, factory);
    }

    /**
     * Gets the factory for a given type.
     * @return The registered factory, or the default factory if the type is null or not found.
     */
    public static SonicItemFactory get(@Nullable ResourceLocation javaType) {
        if (javaType == null) {
            return DEFAULT_FACTORY;
        }
        return FACTORIES.getOrDefault(javaType, DEFAULT_FACTORY);
    }
}