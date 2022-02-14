package Abrielle.bot.Commands;

import Abrielle.bot.Abrielle;
import Abrielle.bot.Commands.Commands.Reactions.*;
import Abrielle.bot.Commands.Commands.fun.CmdCuterate;
import Abrielle.bot.Commands.Commands.info.*;
import Abrielle.bot.Events.Listener;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CommandLoader {
    private final Set<Command> COMMANDS = new HashSet<>();
    private final Set<Command> SLASH = new HashSet<>();
    private final Logger LOGGER = LoggerFactory.getLogger(Listener.class);

    public CommandLoader(Abrielle bot) {
        loadCommands(
                //fun commands
                new CmdCuterate(bot),

                //info commands
                new CmdAvatar(bot),
                new CmdHelp(bot),
                new CmdPrefix(bot),
                new CmdPing(bot),
                new CmdServerAvatar(bot),

                //reaction commands
                new CmdBaka(bot),
                new CmdCuddle(bot),
                new CmdHug(bot),
                new CmdKiss(bot),
                new CmdPat(bot),
                new CmdPoke(bot),
                new CmdSlap(bot)
        );

        LOGGER.info("Loaded {} commands!", COMMANDS.size());
    }

    public Set<Command> getCommands() {
        return COMMANDS;
    }

    private void loadCommands(Command... commands) {
        COMMANDS.addAll(Arrays.asList(commands));
    }

    /*public void loadSlashCommands() {
        CommandListUpdateAction cmds = Abrielle.getBot().updateCommands();

        cmds.addCommands(
                Commands.slash("info", "info commands")
                        .addSubcommands(
                                new SubcommandData("prefix", "Show the prefixes of the bot"),
                                new SubcommandData("ping", "Response time of the bot")
                        ),
                Commands.slash("reaction", "reaction commands")
                        .addSubcommands(
                                new SubcommandData("baka", "Call someone a baka (idiot)")
                                        .addOption(OptionType.USER, "user", "the baka user", false)
                        )
        ).queue();

        LOGGER.info("Loaded {} slash commands!", cmds.complete().size());
    }*/
}
