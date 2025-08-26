//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package wcore.sapphic.tardis.flight.DimensionSelector.scripts;

import com.google.common.collect.UnmodifiableIterator;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.teabs.teabsdoctorwhomod.init.TeabsDoctorWhoModModBlocks;
import net.teabs.teabsdoctorwhomod.network.TeabsDoctorWhoModModVariables.MapVariables;

public class OnRightClick {
    public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
        if (entity != null) {
            if (!MapVariables.get(world).Dematerialzed) {
                if (entity instanceof Player) {
                    Player _player = (Player)entity;
                    if (!_player.level().isClientSide()) {
                        _player.displayClientMessage(Component.literal("Dimension Selector can only be used while Dematerialized."), true);
                    }
                }
            } else {
                BlockPos _bp = BlockPos.containing(x, y, z);
                BlockState _bs = ((Block)TeabsDoctorWhoModModBlocks.DIMENSION_SELECTOR_SKARO.get()).defaultBlockState();
                BlockState _bso = world.getBlockState(_bp);
                UnmodifiableIterator var11 = _bso.getValues().entrySet().iterator();

                while(var11.hasNext()) {
                    Map.Entry<Property<?>, Comparable<?>> entry = (Map.Entry)var11.next();
                    Property _property = _bs.getBlock().getStateDefinition().getProperty(((Property)entry.getKey()).getName());
                    if (_property != null && _bs.getValue(_property) != null) {
                        try {
                            _bs = (BlockState)_bs.setValue(_property, (Comparable)entry.getValue());
                        } catch (Exception var15) {
                        }
                    }
                }

                world.setBlock(_bp, _bs, 3);
                if (entity instanceof Player) {
                    Player _player = (Player)entity;
                    if (!_player.level().isClientSide()) {
                        _player.displayClientMessage(Component.literal("TARDIS Dimension set to: Skaro."), true);
                    }
                }

                MapVariables.get(world).TargetDimension = "Skaro";
                MapVariables.get(world).syncData(world);
            }

        }
    }
}
