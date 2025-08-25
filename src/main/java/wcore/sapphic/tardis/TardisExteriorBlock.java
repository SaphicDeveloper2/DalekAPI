package wcore.sapphic.tardis;

import net.minecraft.core.BlockPos;
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

    /**
     * This method is called when a player places the block.
     * We'll add our dimension check here.
     */
    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        // We only want to run this logic on the server side.
        if (!level.isClientSide()) {
            // Check if the current dimension is the TARDIS dimension
            if (level.dimension().equals(TardisDimension.TARDIS_DIM_KEY)) {
                // If it is, we remove the block
                level.removeBlock(pos, false);

                // And if the placer is a player, we drop the item back and send them a message.
                if (placer instanceof ServerPlayer) {
                    ServerPlayer player = (ServerPlayer) placer;
                    // We check if the player is in creative mode before giving the item back.
                    if (!player.getAbilities().instabuild) {
                        player.getInventory().add(stack); // Give the item back
                    }
                    player.sendSystemMessage(Component.literal("You cannot place a TARDIS inside another TARDIS."), false);
                }
            }
        }
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, world, pos, oldState, isMoving);
        // This logic runs after setPlacedBy, so if the block was removed, this won't execute for it.
        if (!world.isClientSide() && world.getBlockEntity(pos) instanceof TardisExteriorBlockEntity tardis) {
            TardisExteriorManager.get((ServerLevel) world)
                    .setExteriorLocation(tardis.getTardisId(), world.dimension(), pos);
        }
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        // This check prevents an error if the block is removed by our placement check before the BlockEntity is created.
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

                // Check if the interior needs to be built for the first time
                if (tardisDim.getBlockState(interiorDoorPos).isAir()) {
                    player.displayClientMessage(Component.literal("Generating TARDIS interior..."), true);

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

    private void buildInterior(ServerLevel tardisDim, BlockPos doorPos) {
        int radius = 8;
        int height = 5;

        BlockState floorState = Blocks.IRON_BLOCK.defaultBlockState();
        BlockState wallState = Blocks.IRON_BLOCK.defaultBlockState();

        BlockPos floorCenter = doorPos.below();

        for (int x = -radius; x <= radius; x++) {
            for (int y = 0; y <= height; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos currentPos = floorCenter.offset(x, y, z);

                    if (y == 0) {
                        tardisDim.setBlock(currentPos, floorState, 3);
                    }
                    else if (y == height) {
                        tardisDim.setBlock(currentPos, wallState, 3);
                    }
                    else if (Math.abs(x) == radius || Math.abs(z) == radius) {
                        tardisDim.setBlock(currentPos, wallState, 3);
                    }
                }
            }
        }
    }
}
