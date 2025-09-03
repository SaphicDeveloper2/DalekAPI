package wcore.sapphic.datapack.definition;

import com.google.gson.annotations.SerializedName;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobCategory;
import org.jetbrains.annotations.Nullable;

public final class DalekDefinition {

    @SerializedName("display_name")
    private final String displayName;

    @SerializedName("texture")
    private final String texture;

    @SerializedName("spawn_egg")
    private final SpawnEggDefinition spawnEgg;

    @SerializedName("mob_category")
    private final String mobCategory;

    private DalekDefinition(String displayName, String texture, SpawnEggDefinition spawnEgg, String mobCategory) {
        this.displayName = displayName;
        this.texture = texture;
        this.spawnEgg = spawnEgg;
        this.mobCategory = mobCategory;
    }

    public String getDisplayName() {
        return displayName != null ? displayName : "Dalek";
    }

    @Nullable
    public ResourceLocation getTexture() {
        return texture != null ? new ResourceLocation(texture) : null;
    }

    public SpawnEggDefinition getSpawnEgg() {
        // Return a default if null to prevent crashes
        return spawnEgg != null ? spawnEgg : new SpawnEggDefinition();
    }

    public MobCategory getMobCategory() {
        if (mobCategory == null || mobCategory.isBlank()) {
            return MobCategory.MONSTER; // Default value
        }
        try {
            // Converts the string from JSON (e.g., "monster") to the correct enum
            return MobCategory.valueOf(mobCategory.toUpperCase());
        } catch (IllegalArgumentException e) {
            // If the user enters an invalid string, fall back to the default
            return MobCategory.MONSTER;
        }
    }
}

