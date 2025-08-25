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
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide()) {
            if (level.dimension().equals(TardisDimension.TARDIS_DIM_KEY)) {
                level.removeBlock(pos, false);
                if (placer instanceof ServerPlayer player) {
                    if (!player.getAbilities().instabuild) {
                        player.getInventory().add(stack);
                    }
                    player.sendSystemMessage(Component.literal("You cannot place a TARDIS inside another TARDIS."), false);
                }
            }
        }
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
        if (state.hasBlockEntity() && !state.is(newState.getBlock())) {
            if (!world.isClientSide() && world.getBlockEntity(pos) instanceof TardisExteriorBlockEntity tardis) {
                TardisExteriorManager.get((ServerLevel) world)
                        .removeExteriorLocation(tardis.getTardisId());
            }
            super.onRemove(state, world, pos, newState, isMoving);
        }
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

                if (!manager.isInteriorInitialized(tardis.getTardisId())) {
                    player.displayClientMessage(Component.literal("Generating TARDIS interior..."), true);
                    TardisInteriorBuilder.build(tardisDim, interiorDoorPos, tardis.getTardisId());
                }

                // --- CORRECTED TELEPORT LOGIC ---

                BlockState doorState = tardisDim.getBlockState(interiorDoorPos);
                Direction doorFacing = Direction.NORTH;
                if (doorState.hasProperty(TardisDoorBlock.FACING)) {
                    doorFacing = doorState.getValue(TardisDoorBlock.FACING);
                }

                // THE FIX: Move the player in the OPPOSITE direction the door is facing.
                // This places them on the "inside" of the door block.
                BlockPos spawnPos = interiorDoorPos.relative(doorFacing.getOpposite());

                // The rotation is correct - we want the player to face the door when they enter.
                float playerYaw = doorFacing.toYRot();

                serverPlayer.teleportTo(tardisDim, spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, playerYaw, 0f);
                serverPlayer.displayClientMessage(Component.literal("You've entered the TARDIS."), true);
            }
        }
        return InteractionResult.SUCCESS;
    }
}