package wcore.sapphic.packs;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.math.Transformation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.client.model.SimpleModelState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Stream;

public final class PackAssetManager {
    public static final PackAssetManager INSTANCE = new PackAssetManager();
    private static final Logger LOGGER = LogManager.getLogger("DalekAPI PackAssetManager");

    private final Map<ResourceLocation, Path> texturePaths = new ConcurrentHashMap<>();
    private final Map<ResourceLocation, BlockModel> unbakedModels = new ConcurrentHashMap<>();
    private final Map<ResourceLocation, BakedModel> bakedModels = new ConcurrentHashMap<>();
    private final Map<ResourceLocation, ResourceLocation> registeredTextures = new ConcurrentHashMap<>();

    private PackAssetManager() {}

    // ------------------------------------------------------------------------
    // Asset loading
    // ------------------------------------------------------------------------
    public void loadAssetsFromPack(Path packDir, String packId) {
        Path assetsDir = packDir.resolve("assets");
        if (Files.notExists(assetsDir)) return;

        try (Stream<Path> namespaces = Files.list(assetsDir)) {
            namespaces.forEach(namespaceDir -> {
                if (Files.isDirectory(namespaceDir)) {
                    String namespace = namespaceDir.getFileName().toString();
                    // --- Load Textures ---
                    Path texturesDir = namespaceDir.resolve("textures");
                    if (Files.exists(texturesDir)) {
                        try (Stream<Path> walk = Files.walk(texturesDir)) {
                            walk.filter(p -> !Files.isDirectory(p) && p.toString().endsWith(".png")).forEach(path -> {
                                String relative = texturesDir.relativize(path).toString().replace("\\", "/");
                                ResourceLocation id = new ResourceLocation(namespace, relative.replace(".png", ""));
                                texturePaths.put(id, path);
                            });
                        } catch (IOException e) {
                            LOGGER.error("Failed to scan textures for pack: {}", packId, e);
                        }
                    }

                    // --- Load Models ---
                    Path modelsDir = namespaceDir.resolve("models");
                    if (Files.exists(modelsDir)) {
                        try (Stream<Path> walk = Files.walk(modelsDir)) {
                            walk.filter(p -> !Files.isDirectory(p) && p.toString().endsWith(".json")).forEach(path -> {
                                String relative = modelsDir.relativize(path).toString().replace("\\", "/");
                                ResourceLocation id = new ResourceLocation(namespace, relative.replace(".json", ""));
                                try (InputStreamReader reader = new InputStreamReader(Files.newInputStream(path))) {
                                    BlockModel model = BlockModel.fromStream(reader);
                                    unbakedModels.put(id, model);
                                } catch (Exception e) {
                                    LOGGER.error("Failed to load or parse model JSON: {}", id, e);
                                }
                            });
                        } catch (IOException e) {
                            LOGGER.error("Failed to scan models for pack: {}", packId, e);
                        }
                    }
                }
            });
        } catch (IOException e) {
            LOGGER.error("Failed to list namespaces for pack: " + packId, e);
        }
    }


    // ------------------------------------------------------------------------
    // Baking
    // ------------------------------------------------------------------------
    public void onBake(ModelBakery bakery, ModelManager modelManager) {
        LOGGER.info("Baking {} custom pack models...", unbakedModels.size());
        bakedModels.clear();

        // ✅ Correct atlas constant for 1.20.1
        TextureAtlas atlas = modelManager.getAtlas(InventoryMenu.BLOCK_ATLAS);
        final Function<Material, TextureAtlasSprite> spriteGetter = mat -> atlas.getSprite(mat.texture());

        unbakedModels.forEach((id, model) -> {
            try {
                // Resolve parents using our in-memory models first, then vanilla
                model.resolveParents(loc -> unbakedModels.getOrDefault((Object) loc, (BlockModel) bakery.getModel(loc)));

                // Bake using the correct signature
                bakedModels.put(id, model.bake((ModelBaker) bakery, spriteGetter,
                        new SimpleModelState(Transformation.identity()), id));
            } catch (Exception e) {
                LOGGER.error("Failed to bake custom model: {}", id, e);
            }
        });

        LOGGER.info("Finished baking custom models. {} successfully baked.", bakedModels.size());
    }

    @Nullable
    public BakedModel getBakedModel(ResourceLocation modelId) {
        return bakedModels.get(modelId);
    }

    // ------------------------------------------------------------------------
    // Textures
    // ------------------------------------------------------------------------
    public ResourceLocation getTexture(ResourceLocation originalLocation) {
        return registeredTextures.computeIfAbsent(originalLocation, loc -> {
            Path texturePath = texturePaths.get(loc);
            if (texturePath == null) {
                LOGGER.warn("Could not find pack texture path for: {}", loc);
                return TextureManager.INTENTIONAL_MISSING_TEXTURE;
            }
            try (InputStream stream = Files.newInputStream(texturePath)) {
                NativeImage image = NativeImage.read(stream);
                DynamicTexture texture = new DynamicTexture(image);
                ResourceLocation texLoc = new ResourceLocation(loc.getNamespace(), "generated/" + loc.getPath());
                Minecraft.getInstance().getTextureManager().register(texLoc, texture);
                return texLoc;
            } catch (IOException e) {
                LOGGER.error("Failed to load dynamic texture: {}", loc, e);
                return TextureManager.INTENTIONAL_MISSING_TEXTURE;
            }
        });
    }

    // ------------------------------------------------------------------------
    // Utils
    // ------------------------------------------------------------------------
    public void clear() {
        texturePaths.clear();
        unbakedModels.clear();
        bakedModels.clear();
        registeredTextures.clear();
    }

    public void logCounts() {
        LOGGER.info("... Asset Manager found {} texture paths and {} model definitions.", texturePaths.size(), unbakedModels.size());
    }
}