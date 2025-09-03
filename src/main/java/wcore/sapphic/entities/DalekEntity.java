package wcore.sapphic.entities;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import wcore.sapphic.ai.AbstractDalekEntity;
import wcore.sapphic.datapack.DalekManager;
import wcore.sapphic.datapack.definition.DalekDefinition;
import wcore.sapphic.packs.PackAssetManager;

public class DalekEntity extends AbstractDalekEntity {

    public DalekEntity(EntityType<? extends AbstractDalekEntity> type, Level world) {
        super(type, world);
    }

    public ResourceLocation getDefinitionId() {
        return ForgeRegistries.ENTITY_TYPES.getKey(this.getType());
    }

    public DalekDefinition getDefinition() {
        return DalekManager.INSTANCE.getDefinitions().get(getDefinitionId());
    }

    @Override
    public ResourceLocation getTexture() {
        DalekDefinition def = getDefinition();
        if (def != null && def.getTexture() != null) {
            return PackAssetManager.INSTANCE.getTexture(def.getTexture());
        }
        // Fallback texture
        return new ResourceLocation("dalekapi", "textures/entity/dalek/default.png");
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbstractDalekEntity.createAttributes();
    }
}
