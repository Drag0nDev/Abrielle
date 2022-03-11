package Abrielle.bot.Commands.Commands.admin;

import Abrielle.bot.Abrielle;
import Abrielle.bot.Commands.Command;
import Abrielle.constants.Colors;
import Abrielle.util.Exceptions.AbrielleException;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Objects;

import static Abrielle.util.utils.Utils.getChannel;

@CommandDescription(
        name = "say",
        description = "Sends a message in the specified channel.",
        triggers = {"say"},
        attributes = {
                @CommandAttribute(key = "category", value = "admin"),
                @CommandAttribute(key = "usage", value = "[command | alias] [channel] [message]"),
                @CommandAttribute(key = "examples", value = "'a!say #general hi mom!'\n" +
                        "'a!say 418037700751261708 hi mom!'"),
        }
)

public record CmdSay(Abrielle bot) implements Command {

    @Override
    public void runSlash(Guild guild, TextChannel tc, Member member, SlashCommandInteractionEvent event, InteractionHook hook) throws Exception {
        BaseGuildMessageChannel channel;
        OptionMapping option1 = event.getOption("channel");
        OptionMapping option2 = event.getOption("message");

        if (option1 == null || option2 == null)
            throw new AbrielleException("Please provide a channel and a message");

        channel = option1.getAsTextChannel();

        if (channel == null)
            channel = option1.getAsNewsChannel();

        String msg = option2.getAsString();

        if (channel == null)
            throw new AbrielleException("Channel does not exist!");

        channel.sendMessage(msg).queue();
        hook.sendMessageEmbeds(
                        new EmbedBuilder()
                                .setColor(Colors.NORMAL.getCode())
                                .setTimestamp(ZonedDateTime.now())
                                .setDescription("Message sent succesefully!")
                                .addField("Channel", channel.getAsMention(), false)
                                .addField("Message", msg, false)
                                .build())
                .queue();
    }

    @Override
    public void runCommand(Message msg, Guild guild, TextChannel tc, Member member) throws Exception {
        String[] args = bot.getArguments(msg);

        if (args.length - 1 <= 0)
            throw new AbrielleException("Please provide a channel and then the message to say!");

        String[] msgToSend = new String[args.length - 1];

        BaseGuildMessageChannel channel = getChannel(msg, guild, args);

        System.arraycopy(args, 1, msgToSend, 0, args.length - 1);
        String msgComplete = String.join(" ", msgToSend);

        channel.sendMessage(msgComplete).queue();
        tc.sendMessageEmbeds(
                        new EmbedBuilder()
                                .setColor(Colors.NORMAL.getCode())
                                .setTimestamp(ZonedDateTime.now())
                                .setDescription("Message sent succesefully!")
                                .addField("Channel", channel.getAsMention(), false)
                                .addField("Message", msgComplete, false)
                                .build())
                .queue();
    }

    @Override
    public ArrayList<Permission> getNeededPermissions() {
        ArrayList<Permission> permissions = new ArrayList<>();
        permissions.add(Permission.ADMINISTRATOR);
        return permissions;
    }
}