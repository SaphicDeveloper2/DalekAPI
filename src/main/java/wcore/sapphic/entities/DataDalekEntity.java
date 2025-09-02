package wcore.sapphic.entities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import wcore.sapphic.ai.AbstractDalekEntity;
import wcore.sapphic.api.events.DalekTickEvent;
import wcore.sapphic.datapack.DalekManager;
import wcore.sapphic.datapack.definition.DalekDefinition;

import javax.annotation.Nullable;

public class DataDalekEntity extends AbstractDalekEntity {
    private static final EntityDataAccessor<String> DEFINITION_ID = SynchedEntityData.defineId(DataDalekEntity.class, EntityDataSerializers.STRING);
    private static final String DEFINITION_NBT_KEY = "DalekDefinition";
    private DalekDefinition cachedDefinition;

    public DataDalekEntity(EntityType<? extends AbstractDalekEntity> type, Level world) {
        super(type, world);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DEFINITION_ID, "dalekapi:example_dalek");
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
    public DalekDefinition getDefinition() {
        if (this.cachedDefinition == null) {
            this.cachedDefinition = DalekManager.DALEKS.get(getDefinitionId());
        }
        return this.cachedDefinition;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            DalekDefinition def = getDefinition();
            if (def != null) {
                MinecraftForge.EVENT_BUS.post(new DalekTickEvent(this, def));
            }
        }
    }

    @Override
    public ResourceLocation getTexture() {
        return null;
    }
}