package Abrielle.bot.Commands;

import Abrielle.bot.Abrielle;
import Abrielle.bot.Commands.Commands.admin.*;
import Abrielle.bot.Commands.Commands.fun.*;
import Abrielle.bot.Commands.Commands.info.*;
import Abrielle.bot.Commands.Commands.logging.CmdCreateLogs;
import Abrielle.bot.Commands.Commands.owner.*;
import Abrielle.bot.Commands.Commands.reactions.*;
import Abrielle.bot.Events.Listener;
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
                //admin commands
                new CmdSay(bot),

                //fun commands
                new CmdCuterate(bot),
                new CmdFruityrate(bot),
                new CmdGayrate(bot),
                new CmdHornyrate(bot),
                new CmdShip(bot),

                //info commands
                new CmdAvatar(bot),
                new CmdBanner(bot),
                new CmdHelp(bot),
                new CmdPrefix(bot),
                new CmdPing(bot),
                new CmdServerAvatar(bot),
                new CmdServerInfo(bot),

                //logging
                new CmdCreateLogs(bot),

                //owner commands
                new CmdShutdown(bot),
                new CmdSetActivity(bot),

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
