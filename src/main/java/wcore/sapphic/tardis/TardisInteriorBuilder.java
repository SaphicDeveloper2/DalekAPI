package wcore.sapphic.tardis;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.sapphic.dalekapi.DalekapiMod;
import wcore.sapphic.tardis.dimension.TardisDimensionManager;

import java.util.Optional;
import java.util.UUID;

public class TardisInteriorBuilder {

    private static final ResourceLocation DEFAULT_INTERIOR_STRUCTURE = new ResourceLocation(DalekapiMod.MODID, "14th_interior_updated");

    public static void build(ServerLevel tardisDim, BlockPos placementAreaCenter, UUID tardisId) {
        var structureManager = tardisDim.getStructureManager();
        Optional<StructureTemplate> templateOptional = structureManager.get(DEFAULT_INTERIOR_STRUCTURE);

        if (templateOptional.isPresent()) {
            StructureTemplate template = templateOptional.get();
            BlockPos placementPos = placementAreaCenter.offset(-8, -1, -15);

            if (template.placeInWorld(tardisDim, placementPos, placementPos, new StructurePlaceSettings(), tardisDim.getRandom(), 3)) {
                TardisDimensionManager manager = TardisDimensionManager.get(tardisDim);
                TardisDimensionManager.TARDISInteriorData data = manager.getOrCreateInteriorData(tardisId);

                data.isInitialized = true;

                // Search the structure for the single door block
                for (BlockPos pos : BlockPos.betweenClosed(placementPos, placementPos.offset(template.getSize()))) {
                    BlockState foundState = tardisDim.getBlockState(pos);
                    // This check is correct for your single-block door
                    if (foundState.is(TardisRegistry.TARDIS_DOOR_BLOCK.get())) {
                        if (tardisDim.getBlockEntity(pos) instanceof TardisDoorBlockEntity doorEntity) {
                            // Link the door's BlockEntity
                            doorEntity.setTardisId(tardisId);

                            // --- THIS IS THE REQUESTED CHANGE ---
                            Direction doorFacing = foundState.getOptionalValue(TardisDoorBlock.FACING).orElse(Direction.NORTH);

                            // For testing, we now move in the SAME direction as the door is facing.
                            BlockPos spawnPos = pos.relative(doorFacing, 2);

                            data.spawnPoint = spawnPos;

                            manager.setDirty();
                            DalekapiMod.LOGGER.info("Linked TARDIS door and saved spawn point at {} for ID: {}", spawnPos, tardisId);
                            return;
                        }
                    }
                }
                DalekapiMod.LOGGER.error("Failed to find a TardisDoorBlock in the structure for TARDIS ID: {}", tardisId);
            }
        } else {
            DalekapiMod.LOGGER.error("Could not find TARDIS interior structure: {}", DEFAULT_INTERIOR_STRUCTURE);
        }
    }
}