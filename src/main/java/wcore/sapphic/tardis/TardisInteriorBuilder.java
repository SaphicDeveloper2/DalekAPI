package wcore.sapphic.tardis;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.sapphic.dalekapi.DalekapiMod;
import wcore.sapphic.tardis.dimension.TardisDimensionManager;

import java.util.Optional;
import java.util.UUID;

/**
 * Handles the loading and placement of TARDIS interior structures from NBT files.
 */
public class TardisInteriorBuilder {

    /**
     * The ResourceLocation for the default TARDIS interior structure.
     * This points to 'src/main/resources/data/dalekapi/structures/14th_interior_updated.nbt'
     */
    private static final ResourceLocation DEFAULT_INTERIOR_STRUCTURE = new ResourceLocation(DalekapiMod.MODID, "14th_interior_updated");

    /**
     * Builds the TARDIS interior from the NBT file, links the pre-placed door,
     * and marks the interior as initialized.
     *
     * @param tardisDim The dimension to build in (the TARDIS dimension).
     * @param doorPos   The position where the door block of the interior should be. The structure is placed relative to this.
     * @param tardisId  The unique ID of the TARDIS to link the door to.
     */
    public static void build(ServerLevel tardisDim, BlockPos doorPos, UUID tardisId) {
        // Get the structure template manager from the server level
        var structureManager = tardisDim.getStructureManager();

        // Try to load the structure template from the ResourceLocation
        Optional<StructureTemplate> templateOptional = structureManager.get(DEFAULT_INTERIOR_STRUCTURE);

        if (templateOptional.isPresent()) {
            StructureTemplate template = templateOptional.get();

            // Calculate the position to place the structure.
            // This offset ensures that the door inside your NBT file ends up at the 'doorPos'.
            // You may need to adjust these offsets (-8, -1, -15) if your NBT structure's origin is different.
            BlockPos placementPos = doorPos.offset(-8, -1, -15);
            StructurePlaceSettings placeSettings = new StructurePlaceSettings();

            // Place the structure into the world
            if (template.placeInWorld(tardisDim, placementPos, placementPos, placeSettings, tardisDim.getRandom(), 3)) {

                // After successfully placing the structure, mark this TARDIS as initialized in the manager.
                // This prevents the interior from being generated again.
                TardisDimensionManager.get(tardisDim).markInteriorAsInitialized(tardisId);

                // Now, search the newly placed area to find the door block that was part of the NBT file.
                Vec3i size = template.getSize();
                for (int x = 0; x < size.getX(); x++) {
                    for (int y = 0; y < size.getY(); y++) {
                        for (int z = 0; z < size.getZ(); z++) {
                            BlockPos currentPos = placementPos.offset(x, y, z);
                            if (tardisDim.getBlockState(currentPos).is(TardisRegistry.TARDIS_DOOR_BLOCK.get())) {
                                // We found the door. Now get its BlockEntity to link it.
                                BlockEntity be = tardisDim.getBlockEntity(currentPos);
                                if (be instanceof TardisDoorBlockEntity doorEntity) {
                                    // Link the door by setting its TARDIS ID.
                                    doorEntity.setTardisId(tardisId);
                                    DalekapiMod.LOGGER.info("Successfully linked TARDIS interior door for ID: {}", tardisId);
                                    return; // Stop searching once the door is found and linked.
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Log an error if the structure file couldn't be found.
            DalekapiMod.LOGGER.error("Could not find TARDIS interior structure: {}", DEFAULT_INTERIOR_STRUCTURE);
        }
    }
}