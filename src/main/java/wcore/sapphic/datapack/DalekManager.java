package wcore.sapphic.datapack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wcore.sapphic.datapack.definition.DalekDefinition;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public final class DalekManager {
    public static final DalekManager INSTANCE = new DalekManager();
    private static final Gson GSON = new GsonBuilder().create();
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String DIRECTORY_NAME = "daleks";

    private final Map<ResourceLocation, DalekDefinition> definitions = new ConcurrentHashMap<>();

    private DalekManager() {}

    public Map<ResourceLocation, DalekDefinition> getDefinitions() {
        return Collections.unmodifiableMap(this.definitions);
    }

    public void clear() {
        this.definitions.clear();
    }

    /**
     * Loads all Dalek definitions from a specific pack.
     *
     * @param packRoot The root directory of the pack (or the root of the zip filesystem).
     * @param packId   The sanitized, lowercase ID of the pack, used as the namespace.
     */
    public void loadFromPack(Path packRoot, String packId) {
        final Path dalekDir = packRoot.resolve(DIRECTORY_NAME);
        if (Files.notExists(dalekDir)) {
            return;
        }

        try (Stream<Path> stream = Files.walk(dalekDir)) {
            stream.filter(path -> path.toString().endsWith(".json")).forEach(path -> {
                String fileName = path.getFileName().toString();
                String id = fileName.substring(0, fileName.lastIndexOf('.'));
                ResourceLocation loc = new ResourceLocation(packId, id);

                try (InputStreamReader reader = new InputStreamReader(Files.newInputStream(path))) {
                    DalekDefinition def = GSON.fromJson(reader, DalekDefinition.class);
                    if (def != null) {
                        this.definitions.put(loc, def);
                        LOGGER.debug("Loaded Dalek definition: {}", loc);
                    }
                } catch (Exception e) {
                    LOGGER.error("Failed to load or parse Dalek definition: {}", path, e);
                }
            });
        } catch (IOException e) {
            LOGGER.error("Error walking Dalek directory for pack: {}", packId, e);
        }
    }
}

