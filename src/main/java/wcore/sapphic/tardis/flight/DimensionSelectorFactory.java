package wcore.sapphic.tardis.flight;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.ArrayList;

public class DimensionSelectorFactory {

    // A data class to hold the dimension key and its corresponding texture
    public static class DimensionEntry {
        public final ResourceKey<Level> dimensionKey;
        public final ResourceLocation textureLocation;

        public DimensionEntry(ResourceKey<Level> dimensionKey, ResourceLocation textureLocation) {
            this.dimensionKey = dimensionKey;
            this.textureLocation = textureLocation;
        }
    }

    // A public list to hold all registered dimension selectors
    // This allows other mods to add their dimensions to the cycling list.
    public static final List<DimensionEntry> ALL_DIMENSIONS = new ArrayList<>();

    // Public static method for other mods to add their dimension and texture
    public static void addDimension(ResourceKey<Level> dimension, ResourceLocation texture) {
        // Prevent duplicate entries
        if (ALL_DIMENSIONS.stream().noneMatch(entry -> entry.dimensionKey.equals(dimension))) {
            ALL_DIMENSIONS.add(new DimensionEntry(dimension, texture));
        }
    }
}