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
import wcore.sapphic.ai.AbstractDalekEntity;
import wcore.sapphic.datapack.DalekManager;
import wcore.sapphic.datapack.definition.DalekDefinition;

import javax.annotation.Nullable;

public class DataDalekEntity extends AbstractDalekEntity {

    private static final EntityDataAccessor<String> DEFINITION_ID = SynchedEntityData.defineId(DataDalekEntity.class, EntityDataSerializers.STRING);
    private static final ResourceLocation DEFAULT_TEXTURE = new ResourceLocation("dalekapi", "textures/entity/dalek_default.png"); // A fallback texture

    public DataDalekEntity(EntityType<? extends AbstractDalekEntity> entityType, Level level) {
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
    public DalekDefinition getDefinition() {
        return DalekManager.INSTANCE.getDalek(getDefinitionId());
    }

    private void updateAttributes() {
        DalekDefinition def = getDefinition();
        if (def != null) {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(def.health);
            this.setHealth(def.health);
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(def.damage);
        }
    }

    @Override
    public ResourceLocation getTexture() {
        DalekDefinition def = getDefinition();
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
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }
}