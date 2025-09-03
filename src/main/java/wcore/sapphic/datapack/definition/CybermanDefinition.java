package wcore.sapphic.datapack.definition;

import com.google.gson.annotations.SerializedName;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public final class CybermanDefinition {

    @SerializedName("display_name")
    private final String displayName;

    @SerializedName("texture")
    private final String texture;

    @SerializedName("spawn_egg")
    private final SpawnEggDefinition spawnEgg;

    private CybermanDefinition(String displayName, String texture, SpawnEggDefinition spawnEgg) {
        this.displayName = displayName;
        this.texture = texture;
        this.spawnEgg = spawnEgg;
    }

    public String getDisplayName() {
        return displayName != null ? displayName : "Cyberman";
    }

    @Nullable
    public ResourceLocation getTexture() {
        return texture != null ? new ResourceLocation(texture) : null;
    }

    @Nullable
    public SpawnEggDefinition getSpawnEgg() {
        return spawnEgg;
    }
}

