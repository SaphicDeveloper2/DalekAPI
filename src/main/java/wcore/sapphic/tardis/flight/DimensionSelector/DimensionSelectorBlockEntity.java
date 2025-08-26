package wcore.sapphic.tardis.flight.DimensionSelector;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import wcore.sapphic.tardis.TardisRegistry;

public class DimensionSelectorBlockEntity extends BlockEntity {
    public DimensionSelectorBlockEntity(BlockPos pos, BlockState state) {
        super(TardisRegistry.DIMENSION_SELECTOR_BLOCK_ENTITY.get(), pos, state);
    }
}