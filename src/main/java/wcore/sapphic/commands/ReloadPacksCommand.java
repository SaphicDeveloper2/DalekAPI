package wcore.sapphic.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import wcore.sapphic.packs.PackLoader;

public final class ReloadPacksCommand {

    private ReloadPacksCommand() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> command = Commands.literal("packreload")
                .requires(source -> source.hasPermission(2)) // Requires permission level 2 (op)
                .executes(context -> {
                    context.getSource().sendSystemMessage(Component.literal("Reloading DalekAPI packs..."));
                    PackLoader.LOGGER.info("Reloading packs via command.");

                    // This reloads assets and definitions from the /Packs directory
                    PackLoader.initialize();

                    context.getSource().sendSystemMessage(Component.literal("Pack assets and definitions reloaded successfully!"));
                    context.getSource().sendSystemMessage(
                            Component.literal("Note: Adding or removing Sonics, Daleks, or Cybermen requires a full game restart.")
                                    .withStyle(ChatFormatting.DARK_PURPLE)
                    );
                    return 1; // Success
                });

        dispatcher.register(command);
    }
}
