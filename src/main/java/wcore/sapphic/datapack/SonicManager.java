package wcore.sapphic.datapack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wcore.sapphic.datapack.definition.SonicDefinition;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public final class SonicManager {
    public static final SonicManager INSTANCE = new SonicManager();
    private static final Gson GSON = new GsonBuilder().create();
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String DIRECTORY_NAME = "sonics";

    private final Map<ResourceLocation, SonicDefinition> definitions = new ConcurrentHashMap<>();

    private SonicManager() {}

    public Map<ResourceLocation, SonicDefinition> getDefinitions() {
        return Collections.unmodifiableMap(this.definitions);
    }

    public void clear() {
        this.definitions.clear();
    }

    /**
     * Loads all Sonic definitions from a specific pack.
     *
     * @param packRoot The root directory of the pack (or the root of the zip filesystem).
     * @param packId   The sanitized, lowercase ID of the pack, used as the namespace.
     */
    public void loadFromPack(Path packRoot, String packId) {
        final Path sonicsDir = packRoot.resolve(DIRECTORY_NAME);
        if (Files.notExists(sonicsDir)) {
            return;
        }

        try (Stream<Path> stream = Files.walk(sonicsDir)) {
            stream.filter(path -> path.toString().endsWith(".json")).forEach(path -> {
                String fileName = path.getFileName().toString();
                String id = fileName.substring(0, fileName.lastIndexOf('.'));
                ResourceLocation loc = new ResourceLocation(packId, id);

                try (InputStreamReader reader = new InputStreamReader(Files.newInputStream(path))) {
                    SonicDefinition def = GSON.fromJson(reader, SonicDefinition.class);
                    if (def != null) {
                        this.definitions.put(loc, def);
                        LOGGER.debug("Loaded Sonic definition: {}", loc);
                    }
                } catch (Exception e) {
                    LOGGER.error("Failed to load or parse Sonic definition: {}", path, e);
                }
            });
        } catch (IOException e) {
            LOGGER.error("Error walking Sonics directory for pack: {}", packId, e);
        }
    }
}

