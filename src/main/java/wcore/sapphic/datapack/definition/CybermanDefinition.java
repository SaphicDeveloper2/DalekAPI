package wcore.sapphic.datapack.definition;

import com.google.gson.annotations.SerializedName;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the data structure for a Cyberman defined in a JSON file.
 * This is an immutable data holder class parsed by GSON.
 */
public final class CybermanDefinition {

    @SerializedName("java_type")
    private final String javaType;

    @SerializedName("display_name")
    private final String displayName;

    @SerializedName("texture")
    private final String texture;

    // GSON constructor
    private CybermanDefinition(String javaType, String displayName, String texture) {
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
     * @return The display name for this Cyberman variant.
     */
    public String getDisplayName() {
        return displayName != null ? displayName : "Cyberman";
    }

    /**
     * @return The ResourceLocation of the texture for this Cyberman, as defined in a pack.
     */
    @Nullable
    public ResourceLocation getTexture() {
        return texture != null ? new ResourceLocation(texture) : null;
    }
}
