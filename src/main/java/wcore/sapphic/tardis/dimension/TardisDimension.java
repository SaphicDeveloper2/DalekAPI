package wcore.sapphic.tardis.dimension;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.sapphic.dalekapi.DalekapiMod;

public class TardisDimension {
    public static final ResourceKey<Level> TARDIS_DIM_KEY = ResourceKey.create(Registries.DIMENSION,
            new ResourceLocation(DalekapiMod.MODID, "tardis_dim"));

    public static final ResourceKey<DimensionType> TARDIS_DIM_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE,
            new ResourceLocation(DalekapiMod.MODID, "tardis_dim_type"));

    public static void register() {
        // This method just needs to exist to ensure the class is loaded by Java.
        // The simple act of calling this (even if it's empty) forces the static
        // ResourceKey fields above to be initialized.
    }
}
