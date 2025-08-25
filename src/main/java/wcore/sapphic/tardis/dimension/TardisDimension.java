package wcore.sapphic.tardis.dimension;

import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sapphic.dalekapi.DalekapiMod;

@Mod.EventBusSubscriber
public class TardisDimension {
    public static final ResourceKey<Level> TARDIS_DIM_KEY = ResourceKey.create(Registries.DIMENSION,
            new ResourceLocation(DalekapiMod.MODID, "tardis_dim"));

    public static final ResourceKey<DimensionType> TARDIS_DIM_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE,
            new ResourceLocation(DalekapiMod.MODID, "tardis_dim_type"));

    public static void register() {
        // This method still exists to ensure the static keys are initialized.
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        // Check if the player is entering YOUR TARDIS dimension
        if (event.getTo() == TARDIS_DIM_KEY) {
            //
            // You can add any logic you want to happen when a player enters the TARDIS here.
            // For example, you could play a sound, grant an advancement, etc.
            //
            // Example:
            // event.getEntity().sendSystemMessage(Component.literal("Welcome to the TARDIS!"));
        }
    }

    @Mod.EventBusSubscriber(modid = DalekapiMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class TardisDimensionSpecialEffectsHandler {
        @SubscribeEvent
        public static void registerDimensionSpecialEffects(RegisterDimensionSpecialEffectsEvent event) {
            event.register(TARDIS_DIM_KEY.location(), new DimensionSpecialEffects(Float.NaN, true, DimensionSpecialEffects.SkyType.NONE, false, false) {
                @Override
                public Vec3 getBrightnessDependentFogColor(Vec3 color, float sunHeight) {
                    // You can return a specific color for the void fog here if you want one
                    return color;
                }

                @Override
                public boolean isFoggyAt(int x, int y) {
                    // Set to true if you want fog, false if you don't
                    return false;
                }
            });
        }
    }
}
