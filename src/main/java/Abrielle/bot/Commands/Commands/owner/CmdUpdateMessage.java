package Abrielle.bot.Commands.Commands.owner;

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

@CommandDescription(
        name = "updatemessage",
        description = "Send the latest update",
        triggers = {"updatemessage", "um"},
        attributes = {
                @CommandAttribute(key = "category", value = "admin"),
                @CommandAttribute(key = "usage", value = "[command | alias]"),
                @CommandAttribute(key = "examples", value = "`a!updatemessage this is a new update`"),
        }
)

public record CmdUpdateMessage(Abrielle bot) implements Command {

    @Override
    public void runSlash(Guild guild, TextChannel tc, Member member, SlashCommandInteractionEvent event, InteractionHook hook) throws Exception {

    }

    @Override
    public void runCommand(Message msg, Guild guild, TextChannel tc, Member member) throws Exception {
        String[] args = bot.getArguments(msg);

    }
}