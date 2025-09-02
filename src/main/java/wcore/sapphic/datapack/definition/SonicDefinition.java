package wcore.sapphic.datapack.definition;

import com.google.gson.annotations.SerializedName;
import net.minecraft.resources.ResourceLocation;
import javax.annotation.Nullable;

public class SonicDefinition {
    private transient ResourceLocation id;

    @SerializedName("model_location")
    private String modelLocation;
    @SerializedName("texture_location")
    private String textureLocation;
    @SerializedName("use_sound")
    @Nullable
    private String useSound;
    @SerializedName("repair_material")
    @Nullable
    private String repairMaterial;

    public ResourceLocation getId() { return id; }
    public void setId(ResourceLocation id) { this.id = id; }
    public String getModelLocation() { return modelLocation; }
    public String getTextureLocation() { return textureLocation; }
    @Nullable public String getUseSound() { return useSound; }
    @Nullable public ResourceLocation getRepairMaterial() { return repairMaterial != null ? new ResourceLocation(repairMaterial) : null; }
}