package wcore.sapphic.tardis;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import wcore.sapphic.tardis.dimension.TardisDimension;
import wcore.sapphic.tardis.dimension.TardisDimensionManager;
import wcore.sapphic.tardis.dimension.TardisExteriorManager;

import java.util.Optional;

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
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        if (!pLevel.isClientSide()) {
            if (pLevel.getBlockEntity(pPos) instanceof TardisExteriorBlockEntity tardis) {
                // Corrected method call
                TardisExteriorManager.get((ServerLevel) pLevel).setExteriorLocation(tardis.getTardisId(), pLevel.dimension(), pPos);
            }
        }
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pLevel.isClientSide() && pState.getBlock() != pNewState.getBlock()) {
            if (pLevel.getBlockEntity(pPos) instanceof TardisExteriorBlockEntity tardis) {
                TardisExteriorManager.get((ServerLevel) pLevel).removeExteriorLocation(tardis.getTardisId());
            }
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
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
                TardisDimensionManager.TARDISInteriorData data = manager.getOrCreateInteriorData(tardis.getTardisId());

                if (!data.isInitialized) {
                    player.displayClientMessage(Component.literal("Generating TARDIS interior..."), true);
                    TardisInteriorBuilder.build(tardisDim, data.placementCenter, tardis.getTardisId());
                }

                if (data.spawnPoint != null) {
                    BlockPos spawnPos = data.spawnPoint;

                    // The player's yaw should be based on the door's facing direction
                    float playerYaw = Direction.NORTH.toYRot(); // Default
                    BlockState doorState = tardisDim.getBlockState(spawnPos.below()); // Assuming the spawn is one block above the door
                    if (doorState.hasProperty(TardisDoorBlock.FACING)) {
                        playerYaw = doorState.getValue(TardisDoorBlock.FACING).getOpposite().toYRot();
                    }

                    serverPlayer.teleportTo(tardisDim, spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, playerYaw, 0f);
                    serverPlayer.displayClientMessage(Component.literal("You've entered the TARDIS."), true);
                } else {
                    player.displayClientMessage(Component.literal("ERROR: TARDIS interior spawn point not found! Retrying..."), true);
                    TardisInteriorBuilder.build(tardisDim, data.placementCenter, tardis.getTardisId());
                    return InteractionResult.FAIL;
                }
            }
        }
        return InteractionResult.SUCCESS;
    }
}