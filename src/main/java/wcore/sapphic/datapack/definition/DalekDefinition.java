package wcore.sapphic.datapack.definition;

import com.google.gson.annotations.SerializedName;
import net.minecraft.resources.ResourceLocation;

public class DalekDefinition {
    private transient ResourceLocation id;

    @SerializedName("texture_location")
    private String textureLocation;
    @SerializedName("model_location")
    private String modelLocation;

    public ResourceLocation getId() { return id; }
    public void setId(ResourceLocation id) { this.id = id; }
    public String getTextureLocation() { return textureLocation; }
    public String getModelLocation() { return modelLocation; }
}