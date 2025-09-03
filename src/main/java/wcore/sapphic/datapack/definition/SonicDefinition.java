package wcore.sapphic.datapack.definition;

import com.google.gson.annotations.SerializedName;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the data structure for a Sonic Screwdriver defined in a JSON file.
 * This is an immutable data holder class parsed by GSON.
 */
public final class SonicDefinition {

    @SerializedName("java_type")
    private final String javaType;

    @SerializedName("display_name")
    private final String displayName;

    @SerializedName("texture")
    private final String texture;

    @SerializedName("model")
    private final String model;


    // GSON constructor
    private SonicDefinition(String javaType, String displayName, String texture, String model) {
        this.javaType = javaType;
        this.displayName = displayName;
        this.texture = texture;
        this.model = model;
    }

    /**
     * @return The optional ResourceLocation for a code-defined Java type to link this definition to.
     */
    @Nullable
    public ResourceLocation getJavaType() {
        return javaType != null ? new ResourceLocation(javaType) : null;
    }

    /**
     * @return The display name key for this sonic item.
     */
    public String getDisplayName() {
        return displayName != null ? displayName : "item.dalekapi.data_sonic";
    }

    /**
     * @return The ResourceLocation of the item texture for this sonic, as defined in a pack.
     */
    @Nullable
    public ResourceLocation getTexture() {
        return texture != null ? new ResourceLocation(texture) : null;
    }

    /**
     * @return The ResourceLocation of the 3D model for this sonic, as defined in a pack.
     */
    @Nullable
    public ResourceLocation getModel() {
        return model != null ? new ResourceLocation(model) : null;
    }
}
