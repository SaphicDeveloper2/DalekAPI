package wcore.sapphic.tardis;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import wcore.sapphic.tardis.dimension.TardisDimension;
import wcore.sapphic.tardis.dimension.TardisDimensionManager;
import wcore.sapphic.tardis.dimension.TardisExteriorManager;

import java.util.Optional;
import java.util.UUID;

public class TardisDoorBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public TardisDoorBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        // This restores the original 1-pixel deep hitbox.
        return switch (state.getValue(FACING)) {
            case SOUTH -> Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 3.0);
            case WEST -> Block.box(13.0, 0.0, 0.0, 16.0, 16.0, 16.0);
            case EAST -> Block.box(0.0, 0.0, 0.0, 3.0, 16.0, 16.0);
            default -> Block.box(0.0, 0.0, 13.0, 16.0, 16.0, 16.0); // North
        };
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TardisDoorBlockEntity(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (player instanceof ServerPlayer serverPlayer) {
            if (world.getBlockEntity(pos) instanceof TardisDoorBlockEntity doorEntity) {
                UUID tardisId = doorEntity.getTardisId();
                if (tardisId == null) {
                    player.displayClientMessage(Component.literal("ERROR: This TARDIS door is not linked!"), true);
                    return InteractionResult.FAIL;
                }

                TardisExteriorManager manager = TardisExteriorManager.get(world.getServer().overworld());
                TardisExteriorManager.TARDISLocation exterior = manager.getExteriorLocation(tardisId);

                if (exterior != null) {
                    ServerLevel exteriorWorld = world.getServer().getLevel(exterior.dimension);

                    if (exteriorWorld != null) {
                        serverPlayer.teleportTo(exteriorWorld, exterior.pos.getX() + 0.5, exterior.pos.getY(), exterior.pos.getZ() + 0.5, player.getYRot(), player.getXRot());
                        player.displayClientMessage(Component.literal("You've left the TARDIS."), true);
                        return InteractionResult.CONSUME;
                    }
                }
                player.displayClientMessage(Component.literal("ERROR: TARDIS exterior not found!"), true);
                return InteractionResult.FAIL;
            }
        }
        return InteractionResult.FAIL;
    }

    // --- NEW CODE INTEGRATED HERE ---

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        super.onPlace(pState, pLevel, pPos, pOldState, pIsMoving);
        if (pOldState.is(pState.getBlock())) {
            return; // Don't run logic on a block property update
        }

        // Auto-linking logic runs on the server and only in the TARDIS dimension
        if (!pLevel.isClientSide() && pLevel.dimension().equals(TardisDimension.TARDIS_DIM_KEY)) {
            ServerLevel serverLevel = (ServerLevel) pLevel;
            TardisDimensionManager manager = TardisDimensionManager.get(serverLevel);

            // Find which TARDIS interior this block was placed in
            Optional<UUID> tardisIdOpt = manager.findTardisForPos(pPos);

            if (tardisIdOpt.isPresent()) {
                UUID tardisId = tardisIdOpt.get();
                // Check if this specific TARDIS is waiting for a door to be replaced
                if (manager.isAwaitingDoor(tardisId)) {
                    BlockEntity be = pLevel.getBlockEntity(pPos);
                    if (be instanceof TardisDoorBlockEntity doorEntity) {
                        // Link the new door and tell the manager it has been replaced
                        doorEntity.setTardisId(tardisId);
                        manager.markDoorAsReplaced(tardisId);
                    }
                }
            }
        }
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.hasBlockEntity() && !pState.is(pNewState.getBlock())) {
            // This logic runs when the block is broken
            if (!pLevel.isClientSide()) {
                BlockEntity be = pLevel.getBlockEntity(pPos);
                // Check if the door was linked to a TARDIS before it was broken
                if (be instanceof TardisDoorBlockEntity doorEntity && doorEntity.getTardisId() != null) {
                    // Tell the manager that this specific TARDIS is now missing its door
                    TardisDimensionManager manager = TardisDimensionManager.get((ServerLevel) pLevel);
                    manager.markDoorAsBroken(doorEntity.getTardisId());
                }
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    // --- REST OF THE ORIGINAL CODE ---

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}