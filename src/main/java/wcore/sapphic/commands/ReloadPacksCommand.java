package wcore.sapphic.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import wcore.sapphic.packs.PackLoader;

/**
 * Adds the "/dalekapireload" command to the game.
 * When executed, this command triggers the PackLoader to clear all existing pack data
 * and reload everything from the "/Packs" directory, allowing for runtime updates.
 */
public final class ReloadPacksCommand {

    private ReloadPacksCommand() {}

    /**
     * Registers the command with the server's command dispatcher.
     *
     * @param dispatcher The command dispatcher.
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> command = Commands.literal("packreload")
                .requires(source -> source.hasPermission(2)) // Requires permission level 2 (op)
                .executes(context -> {
                    context.getSource().sendSystemMessage(Component.literal("Reloading DalekAPI packs..."));
                    PackLoader.LOGGER.info("Reloading packs via command.");

                    // The initialize method handles clearing old data and loading new data.
                    PackLoader.initialize();

                    context.getSource().sendSystemMessage(Component.literal("DalekAPI packs reloaded successfully!"));
                    return 1; // Success
                });

        dispatcher.register(command);
    }
}
