package wcore.sapphic.datapack.definition;

import com.google.gson.annotations.SerializedName;

public class SpawnEggDefinition {

    @SerializedName("primary")
    private String primary;

    @SerializedName("secondary")
    private String secondary;

    public int getPrimaryColor() {
        try {
            // Decodes hex string like "#AAAAAA" into an integer
            return Integer.decode(primary);
        } catch (NumberFormatException e) {
            return 0xFFFFFF; // Default to white on error
        }
    }

    public int getSecondaryColor() {
        try {
            // Decodes hex string like "#CCCCCC" into an integer
            return Integer.decode(secondary);
        } catch (NumberFormatException e) {
            return 0x000000; // Default to black on error
        }
    }

    public boolean isValid() {
        return primary != null && secondary != null;
    }
}
