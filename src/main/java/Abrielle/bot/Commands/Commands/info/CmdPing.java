package Abrielle.bot.Commands.Commands.info;

import Abrielle.bot.Abrielle;
import Abrielle.bot.Commands.Command;
import Abrielle.constants.Colors;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.time.LocalDateTime;
import java.util.Date;

@CommandDescription(
        name = "ping",
        description = "Response time of the bot",
        triggers = {"ping"},
        attributes = {
                @CommandAttribute(key = "category", value = "info"),
                @CommandAttribute(key = "usage", value = "[command | alias]"),
                @CommandAttribute(key = "examples", value = "'a!ping'\n"),
        }
)

public record CmdPing(Abrielle bot) implements Command {

    @Override
    public void runSlash(Guild guild, TextChannel tc, Member member, SlashCommandInteractionEvent event, InteractionHook hook) throws Exception {
        hook.sendMessageEmbeds(ping(new Date(event.getTimeCreated().toInstant().toEpochMilli()))).queue();
    }

    @Override
    public void runCommand(Message msg, Guild guild, TextChannel tc, Member member) throws Exception {
        tc.sendMessageEmbeds(ping(new Date(msg.getTimeCreated().toInstant().toEpochMilli()))).queue();
    }

    private MessageEmbed ping(Date sent) {
        EmbedBuilder embed = new EmbedBuilder().setColor(Colors.NORMAL.getCode())
                .setTitle(this.getDescription().name())
                .setTimestamp(LocalDateTime.now());

        Date now = new Date();
        long ping = now.getTime() - sent.getTime();

        embed.setDescription(ping + "ms");

        return embed.build();
    }
}