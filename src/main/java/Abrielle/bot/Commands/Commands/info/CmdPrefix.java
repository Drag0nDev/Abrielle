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

import java.time.ZonedDateTime;
import java.util.List;

import static net.dv8tion.jda.api.interactions.commands.OptionType.USER;

@CommandDescription(
        name = "prefix",
        description = "Show the prefixes of the bot",
        triggers = {"prefix"},
        attributes = {
                @CommandAttribute(key = "category", value = "info"),
                @CommandAttribute(key = "usage", value = "[command | alias]"),
                @CommandAttribute(key = "examples", value = "`a!prefix`\n"),
        }
)

public record CmdPrefix(Abrielle bot) implements Command {

    @Override
    public void runSlash(Guild guild, TextChannel tc, Member member, SlashCommandInteractionEvent event, InteractionHook hook) throws Exception {
        hook.sendMessageEmbeds(prefix()).queue();
    }

    @Override
    public void runCommand(Message msg, Guild guild, TextChannel tc, Member member) throws Exception {
        tc.sendMessageEmbeds(prefix()).queue();
    }

    @Override
    public String[] getOptionNames() {
        return Command.super.getOptionNames();
    }

    private MessageEmbed prefix() {
        EmbedBuilder embed = new EmbedBuilder().setColor(Colors.NORMAL.getCode())
                .setTitle(this.getDescription().name())
                .setTimestamp(ZonedDateTime.now());

        StringBuilder strbldr = new StringBuilder();

        List<String> prefix = bot.getConfig().getPrefix();

        for (int i = 0; i < prefix.size(); i++) {
            strbldr.append(prefix.get(i)).append("\n");
        }

        embed.setDescription(strbldr.toString());

        return embed.build();
    }
}
