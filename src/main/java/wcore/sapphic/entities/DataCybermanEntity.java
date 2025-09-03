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
import wcore.sapphic.packs.PackAssetManager;

import javax.annotation.Nullable;

/**
 * An implementation of a Cyberman whose properties are defined by a JSON file
 * from the new global "Packs" system.
 */
public class DataCybermanEntity extends AbstractCybermanEntity {
    private static final EntityDataAccessor<String> DEFINITION_ID = SynchedEntityData.defineId(DataCybermanEntity.class, EntityDataSerializers.STRING);
    private static final String DEFINITION_NBT_KEY = "CybermanDefinition";

    // A server-side cache to avoid repeated lookups in the manager.
    private CybermanDefinition cachedDefinition;

    public DataCybermanEntity(EntityType<? extends AbstractCybermanEntity> type, Level world) {
        super(type, world);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        // Provides a default fallback definition.
        this.entityData.define(DEFINITION_ID, "dalekapi:example_cyberman");
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains(DEFINITION_NBT_KEY)) {
            this.setDefinitionId(new ResourceLocation(compound.getString(DEFINITION_NBT_KEY)));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString(DEFINITION_NBT_KEY, this.getDefinitionId().toString());
    }

    /**
     * Sets the unique identifier for this entity's definition.
     * This will cause the entity to reload its properties from the corresponding JSON file.
     *
     * @param id The ResourceLocation of the definition.
     */
    public void setDefinitionId(ResourceLocation id) {
        this.entityData.set(DEFINITION_ID, id.toString());
        this.cachedDefinition = null; // Invalidate the cache
    }

    /**
     * @return The ResourceLocation of the definition this entity is based on.
     */
    public ResourceLocation getDefinitionId() {
        return new ResourceLocation(this.entityData.get(DEFINITION_ID));
    }

    /**
     * Retrieves the CybermanDefinition for this entity from the central manager.
     * Caches the result to improve performance.
     *
     * @return The definition, or null if it's not found.
     */
    @Nullable
    public CybermanDefinition getDefinition() {
        if (this.level().isClientSide()) {
            // On the client, we can't guarantee the cache is valid, so we always look it up.
            return CybermanManager.INSTANCE.getDefinitions().get(getDefinitionId());
        }
        if (this.cachedDefinition == null) {
            this.cachedDefinition = CybermanManager.INSTANCE.getDefinitions().get(getDefinitionId());
        }
        return this.cachedDefinition;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            CybermanDefinition def = getDefinition();
            if (def != null) {
                // Fire events and behaviors based on the definition
                MinecraftForge.EVENT_BUS.post(new CybermanTickEvent(this, def));
                wcore.sapphic.api.registry.Factory.getCybermanBehavior(getDefinitionId()).onTick(this, def);
            }
        }
    }

    /**
     * Gets the dynamically registered texture for this entity from the PackAssetManager.
     *
     * @return The ResourceLocation for the texture, or null if not defined.
     */
    @Override
    public ResourceLocation getTexture() {
        CybermanDefinition def = getDefinition();
        if (def != null && def.getTexture() != null) {
            // The PackAssetManager handles converting the definition's location
            // into a dynamically registered texture that Minecraft can render.
            return PackAssetManager.INSTANCE.getTexture(def.getTexture());
        }
        return null;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        CybermanDefinition def = getDefinition();
        if (def != null) {
            wcore.sapphic.api.registry.Factory.getCybermanBehavior(getDefinitionId()).configureGoals(this, def);
        }
    }
}
