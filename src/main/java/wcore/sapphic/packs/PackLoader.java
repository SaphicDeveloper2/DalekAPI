package wcore.sapphic.packs;

import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wcore.sapphic.datapack.CybermanManager;
import wcore.sapphic.datapack.DalekManager;
import wcore.sapphic.datapack.SonicManager;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public final class PackLoader {
    public static final Logger LOGGER = LogManager.getLogger("DalekAPI PackLoader");
    public static final Path PACKS_DIRECTORY = FMLPaths.GAMEDIR.get().resolve("Packs");

    public static void initialize() {
        LOGGER.info("Initializing DalekAPI Pack Loader...");
        try {
            if (Files.notExists(PACKS_DIRECTORY)) {
                Files.createDirectories(PACKS_DIRECTORY);
                LOGGER.info("Created Packs directory at: {}", PACKS_DIRECTORY.toAbsolutePath());
            }
            // We only need to load the JSON definitions here. Assets are handled by the resource pack system.
            loadAllPackDefinitions();
        } catch (IOException e) {
            LOGGER.error("Failed to create or access Packs directory!", e);
        }
    }

    private static void loadAllPackDefinitions() {
        LOGGER.info("Loading all pack definitions from: {}", PACKS_DIRECTORY.toAbsolutePath());

        // Clear previous definitions on reload
        DalekManager.INSTANCE.clear();
        CybermanManager.INSTANCE.clear();
        SonicManager.INSTANCE.clear();

        try (Stream<Path> stream = Files.list(PACKS_DIRECTORY)) {
            stream.forEach(path -> {
                String fileName = path.getFileName().toString();
                if (Files.isDirectory(path)) {
                    LOGGER.info("Loading definitions from directory: {}", fileName);
                    loadPackContents(path, getPackId(path));
                } else if (fileName.toLowerCase().endsWith(".zip")) {
                    LOGGER.info("Loading definitions from zip file: {}", fileName);
                    try (FileSystem zipFs = FileSystems.newFileSystem(path, (ClassLoader) null)) {
                        loadPackContents(zipFs.getPath("/"), getPackId(path));
                    } catch (IOException e) {
                        LOGGER.error("Could not read zip file: {}", fileName, e);
                    }
                }
            });
        } catch (IOException e) {
            LOGGER.error("An error occurred while scanning for packs.", e);
        }

        LOGGER.info("Finished loading definitions. Found {} Daleks, {} Cybermen, {} Sonics.",
                DalekManager.INSTANCE.getDefinitions().size(),
                CybermanManager.INSTANCE.getDefinitions().size(),
                SonicManager.INSTANCE.getDefinitions().size()
        );
    }

    private static void loadPackContents(Path root, String packId) {
        LOGGER.debug("Loading definitions for pack ID '{}' from root: {}", packId, root);
        DalekManager.INSTANCE.loadFromPack(root, packId);
        CybermanManager.INSTANCE.loadFromPack(root, packId);
        SonicManager.INSTANCE.loadFromPack(root, packId);
    }

    public static String getPackId(Path path) {
        String fileName = path.getFileName().toString();
        if (fileName.toLowerCase().endsWith(".zip")) {
            return fileName.substring(0, fileName.length() - 4).toLowerCase().replaceAll("[^a-z0-9_.-]", "");
        }
        return fileName.toLowerCase().replaceAll("[^a-z0-9_.-]", "");
    }
}
