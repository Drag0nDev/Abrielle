package Abrielle.bot.Commands.Commands.owner;

import Abrielle.bot.Abrielle;
import Abrielle.bot.Commands.Command;
import Abrielle.constants.Colors;
import Abrielle.util.utils.Restart;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import static Abrielle.util.utils.Utils.jsonToEmbed;

@CommandDescription(
        name = "restart",
        description = "Restart the bot with an update",
        triggers = {"restart", "update"},
        attributes = {
                @CommandAttribute(key = "category", value = "owner"),
                @CommandAttribute(key = "usage", value = "[command | alias]"),
                @CommandAttribute(key = "examples", value = "a!restart"),
        }
)

public record CmdRestart(Abrielle bot) implements Command {

    @Override
    public void runCommand(Message msg, Guild guild, TextChannel tc, Member member) throws Exception {

        Restart thread = new Restart();

        tc.sendMessageEmbeds(new EmbedBuilder()
                        .setColor(Colors.NORMAL.getCode())
                        .setDescription("I am restarting now!\n" +
                                "I will be back shortly.")
                        .setTimestamp(ZonedDateTime.now())
                        .setTitle("Restart")
                        .build())
                .complete();
        Abrielle.getBot().shutdownNow();
        thread.start();
        System.exit(0);
    }
}