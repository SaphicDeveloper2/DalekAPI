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
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import wcore.sapphic.tardis.dimension.TardisExteriorManager;

import java.util.UUID;

public class TardisDoorBlock extends BaseEntityBlock {

    public TardisDoorBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TardisDoorBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!world.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            if (world.getBlockEntity(pos) instanceof TardisDoorBlockEntity door) {
                UUID tardisId = door.getTardisId();
                if (tardisId == null) {
                    player.displayClientMessage(Component.literal("This TARDIS door is not linked!"), true);
                    return InteractionResult.FAIL;
                }

                // Get the manager and ask for the location
                TardisExteriorManager manager = TardisExteriorManager.get((ServerLevel) world);
                TardisExteriorManager.TARDISLocation location = manager.getExteriorLocation(tardisId);

                if (location != null) {
                    ServerLevel exteriorLevel = serverPlayer.getServer().getLevel(location.dimension);
                    if (exteriorLevel != null) {
                        serverPlayer.teleportTo(exteriorLevel, location.pos.getX() + 0.5, location.pos.getY() + 1, location.pos.getZ() + 0.5, player.getYRot(), player.getXRot());
                        serverPlayer.displayClientMessage(Component.literal("You've left the TARDIS."), true);
                        return InteractionResult.SUCCESS;
                    }
                }

                player.displayClientMessage(Component.literal("Could not find the TARDIS exterior!"), true);
            }
        }
        return InteractionResult.SUCCESS;
    }
}