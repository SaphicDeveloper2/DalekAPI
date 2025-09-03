package wcore.sapphic.entities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import wcore.sapphic.ai.AbstractCybermanEntity;
import wcore.sapphic.datapack.CybermanManager;
import wcore.sapphic.datapack.definition.CybermanDefinition;
import wcore.sapphic.packs.PackAssetManager;

public class CybermanEntity extends AbstractCybermanEntity {

    public CybermanEntity(EntityType<? extends AbstractCybermanEntity> type, Level world) {
        super(type, world);
    }

    public ResourceLocation getDefinitionId() {
        return ForgeRegistries.ENTITY_TYPES.getKey(this.getType());
    }

    public CybermanDefinition getDefinition() {
        return CybermanManager.INSTANCE.getDefinitions().get(getDefinitionId());
    }

    @Override
    public ResourceLocation getTexture() {
        CybermanDefinition def = getDefinition();
        if (def != null && def.getTexture() != null) {
            return PackAssetManager.INSTANCE.getTexture(def.getTexture());
        }
        // Fallback texture
        return new ResourceLocation("dalekapi", "textures/entity/cyberman/default.png");
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbstractCybermanEntity.createAttributes();
    }
}
