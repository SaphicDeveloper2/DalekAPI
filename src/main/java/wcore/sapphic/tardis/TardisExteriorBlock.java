package wcore.sapphic.tardis;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import wcore.sapphic.tardis.dimension.TardisDimension;
import wcore.sapphic.tardis.dimension.TardisDimensionManager;
import wcore.sapphic.tardis.dimension.TardisExteriorManager;

public class TardisExteriorBlock extends BaseEntityBlock {

    public TardisExteriorBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TardisExteriorBlockEntity(pos, state);
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, world, pos, oldState, isMoving);
        if (!world.isClientSide() && world.getBlockEntity(pos) instanceof TardisExteriorBlockEntity tardis) {
            TardisExteriorManager.get((ServerLevel) world)
                    .setExteriorLocation(tardis.getTardisId(), world.dimension(), pos);
        }
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!world.isClientSide() && world.getBlockEntity(pos) instanceof TardisExteriorBlockEntity tardis) {
            TardisExteriorManager.get((ServerLevel) world)
                    .removeExteriorLocation(tardis.getTardisId());
        }
        super.onRemove(state, world, pos, newState, isMoving);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!world.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            if (world.getBlockEntity(pos) instanceof TardisExteriorBlockEntity tardis) {

                if (tardis.isLocked()) {
                    player.displayClientMessage(Component.literal("The TARDIS is locked."), true);
                    return InteractionResult.FAIL;
                }

                ServerLevel tardisDim = serverPlayer.getServer().getLevel(TardisDimension.TARDIS_DIM_KEY);
                if (tardisDim == null) {
                    player.displayClientMessage(Component.literal("ERROR: TARDIS dimension not found!"), true);
                    return InteractionResult.FAIL;
                }

                TardisDimensionManager manager = TardisDimensionManager.get(tardisDim);
                BlockPos interiorDoorPos = manager.getOrCreateInteriorPos(tardis.getTardisId());

                // Check if the interior needs to be built for the first time
                if (tardisDim.getBlockState(interiorDoorPos).isAir()) {
                    player.displayClientMessage(Component.literal("Generating TARDIS interior..."), true);

                    // --- NEW --- Build the room structure
                    buildInterior(tardisDim, interiorDoorPos);

                    tardisDim.setBlock(interiorDoorPos, TardisRegistry.TARDIS_DOOR_BLOCK.get().defaultBlockState(), 3);
                    if (tardisDim.getBlockEntity(interiorDoorPos) instanceof TardisDoorBlockEntity door) {
                        door.setTardisId(tardis.getTardisId());
                    }
                }

                // Teleport player into the TARDIS dimension
                serverPlayer.teleportTo(tardisDim, interiorDoorPos.getX() + 0.5, interiorDoorPos.getY(), interiorDoorPos.getZ() + 0.5, player.getYRot(), player.getXRot());
                serverPlayer.displayClientMessage(Component.literal("You've entered the TARDIS."), true);
            }
        }
        return InteractionResult.SUCCESS;
    }

    /**
     * Generates a simple room in the TARDIS dimension.
     * @param tardisDim The TARDIS dimension level.
     * @param doorPos The position of the interior door, which acts as the room's center.
     */
    private void buildInterior(ServerLevel tardisDim, BlockPos doorPos) {
        // Define the size of the room
        int radius = 8;
        int height = 5;

        // Get the block states for floor and walls (using indestructible iron blocks for now)
        BlockState floorState = Blocks.IRON_BLOCK.defaultBlockState();
        BlockState wallState = Blocks.IRON_BLOCK.defaultBlockState();

        BlockPos floorCenter = doorPos.below();

        // Use nested loops to place blocks in a cube
        for (int x = -radius; x <= radius; x++) {
            for (int y = 0; y <= height; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos currentPos = floorCenter.offset(x, y, z);

                    // Place the floor
                    if (y == 0) {
                        tardisDim.setBlock(currentPos, floorState, 3);
                    }
                    // Place the ceiling
                    else if (y == height) {
                        tardisDim.setBlock(currentPos, wallState, 3);
                    }
                    // Place the walls
                    else if (Math.abs(x) == radius || Math.abs(z) == radius) {
                        tardisDim.setBlock(currentPos, wallState, 3);
                    }
                }
            }
        }
    }
}