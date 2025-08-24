package wcore.sapphic.items;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class ExampleSonicItem extends BaseSonicItem {

    public ExampleSonicItem() {
        super();
    }

    @Override
    protected void onSonicUse(Level world, BlockPos pos, Player player) {
        // Check if the block clicked is diamond ore.
        if (world.getBlockState(pos).is(Blocks.DIAMOND_ORE)) {
            if (!world.isClientSide()) {
                // Set the block to a diamond block.
                world.setBlock(pos, Blocks.DIAMOND_BLOCK.defaultBlockState(), 3);

                // Play a rewarding sound and spawn particles.
                world.playSound(null, pos, SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.7F, 1.0F);
                if (world instanceof ServerLevel) {
                    ((ServerLevel) world).sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 20, 0.5, 0.5, 0.5, 0.1);
                }
            }
        } else {
            // If it's not diamond ore, just perform the default action.
            super.onSonicUse(world, pos, player);
        }
    }
}
