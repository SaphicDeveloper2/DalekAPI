package net.sapphic.dalekapi;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import wcore.sapphic.items.ExampleSonicItem;
import wcore.sapphic.sonic.SonicItemRegistryFactory;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Mod("dalekapi")
public class DalekapiMod {
    public static final Logger LOGGER = LogManager.getLogger(DalekapiMod.class);
    public static final String MODID = "dalekapi";

    public DalekapiMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register Sonic Items
        registerSonics(bus);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void registerSonics(IEventBus bus) {
        SonicItemRegistryFactory sonicFactory = new SonicItemRegistryFactory(MODID);
        SonicItemRegistryFactory.registerSonic("example_sonic_item", ExampleSonicItem::new, getExampleSonicModelJson());
        sonicFactory.register(bus);
    }

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel PACKET_HANDLER = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID, MODID), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
    private static int messageID = 0;

    public static <T> void addNetworkMessage(Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, Supplier<NetworkEvent.Context>> messageConsumer) {
        PACKET_HANDLER.registerMessage(messageID++, messageType, encoder, decoder, messageConsumer);
    }

    private static final Collection<AbstractMap.SimpleEntry<Runnable, Integer>> workQueue = new ConcurrentLinkedQueue<>();

    public static void queueServerWork(int tick, Runnable action) {
        if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER)
            workQueue.add(new AbstractMap.SimpleEntry<>(action, tick));
    }

    // The ServerTickEvent is no longer needed here as TARDIS ticking is handled by DTLib.

    private static String getExampleSonicModelJson() {
        return """
        {
            "credit": "Made with Blockbench",
                "textures": {
            "0": "dalekapi:block/sonics/example_sonic_item",
                    "particle": "dalekapi:block/sonics/example_sonic_item"
        },
            "elements": [
            {
                "name": "handle",
                    "from": [7, 0, 7],
                "to": [9, 10, 9],
                "faces": {
                "north": {"uv": [4, 0, 6, 10], "texture": "#0"},
                "east": {"uv": [2, 0, 4, 10], "texture": "#0"},
                "south": {"uv": [8, 0, 10, 10], "texture": "#0"},
                "west": {"uv": [6, 0, 8, 10], "texture": "#0"},
                "up": {"uv": [2, 10, 4, 12], "texture": "#0"},
                "down": {"uv": [4, 10, 6, 12], "texture": "#0"}
            }
            },
            {
                "name": "emitter",
                    "from": [7.5, 10, 7.5],
                "to": [8.5, 12, 8.5],
                "faces": {
                "north": {"uv": [0, 0, 1, 2], "texture": "#0"},
                "east": {"uv": [0, 2, 1, 4], "texture": "#0"},
                "south": {"uv": [1, 0, 2, 2], "texture": "#0"},
                "west": {"uv": [1, 2, 2, 4], "texture": "#0"},
                "up": {"uv": [0, 4, 1, 5], "texture": "#0"}
            }
            }
           ],
            "display": {
            "thirdperson_righthand": {"rotation": [75, 45, 0], "translation": [0, 2.5, 0], "scale": [0.375, 0.375, 0.375]},
            "thirdperson_lefthand": {"rotation": [75, 45, 0], "translation": [0, 2.5, 0], "scale": [0.375, 0.375, 0.375]},
            "firstperson_righthand": {"rotation": [0, 45, 0], "scale": [0.40, 0.40, 0.40]},
            "firstperson_lefthand": {"rotation": [0, 225, 0], "scale": [0.40, 0.40, 0.40]},
            "ground": {"translation": [0, 3, 0], "scale": [0.25, 0.25, 0.25]},
            "gui": {"rotation": [30, 225, 0], "translation": [0, 2, 0], "scale": [0.8, 0.8, 0.8]},
            "fixed": {"scale": [0.5, 0.5, 0.5]}
        }
        }
        """;
    }
}