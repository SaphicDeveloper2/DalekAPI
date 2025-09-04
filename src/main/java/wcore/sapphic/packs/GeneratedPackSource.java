package wcore.sapphic.packs;

import com.mojang.logging.LogUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.world.flag.FeatureFlags;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * A custom repository source that finds user-created packs in the /Packs directory
 * and wraps them as valid resource packs for Minecraft using VirtualPackResources.
 * (User-corrected version)
 */
public class GeneratedPackSource implements RepositorySource {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Path packsDirectory;

    public GeneratedPackSource(Path folder) {
        this.packsDirectory = folder;
    }

    @Override
    public void loadPacks(Consumer<Pack> consumer) {
        if (Files.notExists(packsDirectory)) {
            LOGGER.warn("DalekAPI 'Packs' directory not found at: {}", packsDirectory);
            return;
        }

        try (Stream<Path> stream = Files.list(packsDirectory)) {
            stream.filter(path -> Files.isDirectory(path) || path.getFileName().toString().toLowerCase().endsWith(".zip"))
                    .forEach(path -> {
                        String packName = path.getFileName().toString().replace(".zip", "");
                        String packId = "dalekapi_pack_" + packName.toLowerCase().replaceAll("[^a-z0-9_.-]", "");

                        LOGGER.info("Found DalekAPI pack '{}', registering as '{}'", packName, packId);

                        // Create a supplier for VirtualPackResources
                        Pack.ResourcesSupplier supplier = (id) -> new VirtualPackResources(id, path);

                        // Create the pack
                        Pack pack = Pack.create(
                                packId,
                                Component.literal("DalekAPI Pack: " + packName),
                                true, // Required pack
                                supplier,
                                new Pack.Info(
                                        Component.literal("Assets for DalekAPI"),
                                        15, // Pack format for 1.20.1
                                        FeatureFlags.DEFAULT_FLAGS
                                ),
                                PackType.CLIENT_RESOURCES,
                                Pack.Position.TOP,
                                false,
                                PackSource.BUILT_IN
                        );

                        // Provide the pack to Minecraft
                        consumer.accept(pack);
                    });
        } catch (IOException e) {
            LOGGER.error("Failed to scan for DalekAPI packs in directory: {}", packsDirectory, e);
        }
    }
}

