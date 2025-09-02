package wcore.sapphic.entities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import wcore.sapphic.ai.AbstractCybermanEntity;
import wcore.sapphic.api.events.CybermanTickEvent;
import wcore.sapphic.datapack.CybermanManager;
import wcore.sapphic.datapack.definition.CybermanDefinition;

import javax.annotation.Nullable;

public class DataCybermanEntity extends AbstractCybermanEntity {
    private static final EntityDataAccessor<String> DEFINITION_ID = SynchedEntityData.defineId(DataCybermanEntity.class, EntityDataSerializers.STRING);
    private static final String DEFINITION_NBT_KEY = "CybermanDefinition";
    private CybermanDefinition cachedDefinition;

    public DataCybermanEntity(EntityType<? extends AbstractCybermanEntity> type, Level world) {
        super(type, world);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DEFINITION_ID, "dalekapi:example_cyberman");
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains(DEFINITION_NBT_KEY)) {
            this.setDefinitionId(new ResourceLocation(pCompound.getString(DEFINITION_NBT_KEY)));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putString(DEFINITION_NBT_KEY, this.getDefinitionId().toString());
    }

    public void setDefinitionId(ResourceLocation id) {
        this.entityData.set(DEFINITION_ID, id.toString());
        this.cachedDefinition = null;
    }

    public ResourceLocation getDefinitionId() {
        return new ResourceLocation(this.entityData.get(DEFINITION_ID));
    }

    @Nullable
    public CybermanDefinition getDefinition() {
        if (this.cachedDefinition == null) {
            this.cachedDefinition = CybermanManager.CYBERMEN.get(getDefinitionId());
        }
        return this.cachedDefinition;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            CybermanDefinition def = getDefinition();
            if (def != null) {
                MinecraftForge.EVENT_BUS.post(new CybermanTickEvent(this, def));
            }
        }
    }

    @Override
    public ResourceLocation getTexture() {
        return null;
    }
}