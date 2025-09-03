package wcore.sapphic.datapack.definition;

import com.google.gson.annotations.SerializedName;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the data structure for a Dalek defined in a JSON file.
 * This is an immutable data holder class parsed by GSON.
 */
public final class DalekDefinition {

    @SerializedName("java_type")
    private final String javaType;

    @SerializedName("display_name")
    private final String displayName;

    @SerializedName("texture")
    private final String texture;

    // GSON constructor
    private DalekDefinition(String javaType, String displayName, String texture) {
        this.javaType = javaType;
        this.displayName = displayName;
        this.texture = texture;
    }

    /**
     * @return The optional ResourceLocation for a code-defined Java type to link this definition to.
     */
    @Nullable
    public ResourceLocation getJavaType() {
        return javaType != null ? new ResourceLocation(javaType) : null;
    }

    /**
     * @return The display name for this Dalek variant.
     */
    public String getDisplayName() {
        return displayName != null ? displayName : "Dalek";
    }

    /**
     * @return The ResourceLocation of the texture for this Dalek, as defined in a pack.
     */
    @Nullable
    public ResourceLocation getTexture() {
        return texture != null ? new ResourceLocation(texture) : null;
    }
}
