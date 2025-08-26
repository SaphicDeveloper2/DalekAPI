package wcore.sapphic.tardis.flight.DimensionSelector;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import wcore.sapphic.tardis.TardisExteriorBlockEntity;
import wcore.sapphic.tardis.dimension.TardisDimension;
import wcore.sapphic.tardis.dimension.TardisDimensionManager;
import wcore.sapphic.tardis.dimension.TardisExteriorManager;
import wcore.sapphic.tardis.flight.DimensionSelectorFactory;
import wcore.sapphic.tardis.flight.TardisFlightState;

import java.util.Optional;
import java.util.UUID;

public class DimensionSelectorBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final IntegerProperty DIMENSION_INDEX = IntegerProperty.create("dimension_index", 0, Math.max(0, DimensionSelectorFactory.ALL_DIMENSIONS.size() - 1));

    // --- YOUR IDEA IMPLEMENTED HERE ---
    public static final BooleanProperty UP = BooleanProperty.create("up");

    public DimensionSelectorBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(DIMENSION_INDEX, 0)
                .setValue(UP, false) // Default to "upside down"
        );
    }

    @Override
    public RenderShape getRenderShape(BlockState p_49232_) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DimensionSelectorBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        // Add the new UP property to the block's state
        builder.add(FACING, DIMENSION_INDEX, UP);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        // When placed, force it to be facing the player and in the "upside down" (UP: false) state.
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(UP, false);
    }

    // --- NO CHANGES NEEDED TO THE 'use' METHOD ---
    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (world.isClientSide() || world.dimension() != TardisDimension.TARDIS_DIM_KEY) {
            return InteractionResult.SUCCESS;
        }

        if (DimensionSelectorFactory.ALL_DIMENSIONS.isEmpty()) {
            player.sendSystemMessage(Component.literal("Error: No dimensions have been registered for the selector."));
            return InteractionResult.FAIL;
        }

        ServerLevel tardisDim = (ServerLevel) world;
        TardisDimensionManager dimensionManager = TardisDimensionManager.get(tardisDim);
        Optional<UUID> tardisIdOpt = dimensionManager.findTardisForPos(pos);

        if (tardisIdOpt.isEmpty()) {
            player.sendSystemMessage(Component.literal("Error: Could not find parent TARDIS for this selector."));
            return InteractionResult.FAIL;
        }

        UUID tardisId = tardisIdOpt.get();
        TardisExteriorManager exteriorManager = TardisExteriorManager.get(tardisDim.getServer().overworld());
        TardisExteriorManager.TARDISLocation location = exteriorManager.getExteriorLocation(tardisId);

        if (location == null) {
            player.sendSystemMessage(Component.literal("Error: Could not find TARDIS exterior."));
            return InteractionResult.FAIL;
        }

        ServerLevel exteriorWorld = tardisDim.getServer().getLevel(location.dimension);
        if (exteriorWorld != null && exteriorWorld.getBlockEntity(location.pos) instanceof TardisExteriorBlockEntity exterior) {
            if (exterior.getFlightState() != TardisFlightState.LANDED) {
                player.sendSystemMessage(Component.literal("Cannot change destination while in flight."));
                return InteractionResult.FAIL;
            }

            int currentIndex = state.getValue(DIMENSION_INDEX);
            int nextIndex = (currentIndex + 1) % DimensionSelectorFactory.ALL_DIMENSIONS.size();

            world.setBlockAndUpdate(pos, state.setValue(DIMENSION_INDEX, nextIndex));

            DimensionSelectorFactory.DimensionEntry nextDimensionEntry = DimensionSelectorFactory.ALL_DIMENSIONS.get(nextIndex);
            ResourceKey<Level> nextDimension = nextDimensionEntry.dimensionKey;

            exterior.setTargetDimension(nextDimension);
            player.sendSystemMessage(Component.literal("TARDIS destination set to: " + nextDimension.location().getPath()));
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }
}