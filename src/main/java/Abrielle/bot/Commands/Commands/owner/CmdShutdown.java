package Abrielle.bot.Commands.Commands.owner;

import Abrielle.bot.Abrielle;
import Abrielle.bot.Commands.Command;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import static Abrielle.util.utils.Utils.jsonToEmbed;

@CommandDescription(
        name = "shutdown",
        description = "Stop the bot.",
        triggers = {"shutdown"},
        attributes = {
                @CommandAttribute(key = "category", value = "owner"),
                @CommandAttribute(key = "usage", value = "[command | alias]"),
                @CommandAttribute(key = "examples", value = "a!shutdown")
        }
)

public record CmdShutdown(Abrielle bot) implements Command {

    @Override
    public void runCommand(Message msg, Guild guild, TextChannel tc, Member member) {
        String test = "{" +
                "\"color\": \"#ff0000\"," +
                "\"title\": \"Shutting down\"," +
                "\"description\": \"I am shutting down myself\"" +
                "}";

        tc.sendMessageEmbeds(jsonToEmbed(test)).complete();
        Abrielle.getBot().shutdownNow();
        System.exit(0);
    }
}
