package wcore.sapphic.entities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import wcore.sapphic.ai.AbstractCybermanEntity;
import wcore.sapphic.datapack.CybermanManager;
import wcore.sapphic.datapack.definition.CybermanDefinition;

import javax.annotation.Nullable;

public class DataCybermanEntity extends AbstractCybermanEntity {

    private static final EntityDataAccessor<String> DEFINITION_ID = SynchedEntityData.defineId(DataCybermanEntity.class, EntityDataSerializers.STRING);
    private static final ResourceLocation DEFAULT_TEXTURE = new ResourceLocation("dalekapi", "textures/entity/cyberman_default.png"); // A fallback texture

    public DataCybermanEntity(EntityType<? extends AbstractCybermanEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DEFINITION_ID, "dalekapi:empty");
    }

    public void setDefinitionId(ResourceLocation id) {
        this.entityData.set(DEFINITION_ID, id.toString());
        this.updateAttributes();
    }

    public ResourceLocation getDefinitionId() {
        return new ResourceLocation(this.entityData.get(DEFINITION_ID));
    }

    @Nullable
    public CybermanDefinition getDefinition() {
        return CybermanManager.INSTANCE.getCyberman(getDefinitionId());
    }

    private void updateAttributes() {
        CybermanDefinition def = getDefinition();
        if (def != null) {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(def.health);
            this.setHealth(def.health);
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(def.damage);
        }
    }

    @Override
    public ResourceLocation getTexture() {
        CybermanDefinition def = getDefinition();
        if (def != null && def.texture != null) {
            return def.texture;
        }
        return DEFAULT_TEXTURE;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("definition_id")) {
            this.setDefinitionId(new ResourceLocation(pCompound.getString("definition_id")));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putString("definition_id", this.getDefinitionId().toString());
    }

    @Override
    public Component getName() {
        ResourceLocation id = getDefinitionId();
        return Component.translatable("entity." + id.getNamespace() + "." + id.getPath());
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 25.0D) // Default values
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 7.0D)
                .add(Attributes.FOLLOW_RANGE, 40.0D);
    }
}
