package Abrielle.bot.Commands.Commands.info;

import Abrielle.bot.Abrielle;
import Abrielle.bot.Commands.Command;
import Abrielle.constants.Colors;
import Abrielle.util.Exceptions.AbrielleException;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.time.ZonedDateTime;
import java.util.Locale;

import static Abrielle.util.utils.Utils.*;

@CommandDescription(
        name = "serverinfo",
        description = "See the info about the server.",
        triggers = {"serverinfo", "sinfo"},
        attributes = {
                @CommandAttribute(key = "category", value = "info"),
                @CommandAttribute(key = "usage", value = "[command | alias]"),
                @CommandAttribute(key = "examples", value = "`a!sinfo`\n"),
        }
)

public record CmdServerInfo(Abrielle bot) implements Command {

    @Override
    public void runSlash(Guild guild, TextChannel tc, Member member, SlashCommandInteractionEvent event, InteractionHook hook) throws Exception {
        hook.sendMessageEmbeds(serverInfo(guild)).queue();
    }

    @Override
    public void runCommand(Message msg, Guild guild, TextChannel tc, Member member) throws Exception {
        tc.sendMessageEmbeds(serverInfo(guild)).queue();
    }

    private MessageEmbed serverInfo(Guild guild) throws AbrielleException {
        EmbedBuilder embed = new EmbedBuilder().setTitle(guild.getName())
                .setThumbnail(guild.getIconUrl())
                .setColor(Colors.NORMAL.getCode())
                .setTimestamp(ZonedDateTime.now());

        Member owner = guild.retrieveOwner().complete();

        //owner field
        embed
                .addField("Id", guild.getId(), true)
                .addField("Owner", owner.getUser().getAsTag(), true)
                //users field
                .addField("Total user", String.valueOf(guild.getMemberCount()), true)
                //find bot count
                .addField(
                        "User division",
                        "**Members:** " + (guild.getMemberCount() - getBots(guild)) + "\n" +
                                "**Bots:** " + getBots(guild),
                        true)
                //get the creation date
                .addField("Creation date", guild.getTimeCreated().format(format()), true)
                //get amount of each channel sort
                .addField(
                        "Channels",
                        "**Category:** " + getChannelAmount(guild.getChannels(), ChannelType.CATEGORY) + "\n" +
                                "**Text channels:** " + getChannelAmount(guild.getChannels(), ChannelType.TEXT) + "\n" +
                                "**Voice channels:** " + getChannelAmount(guild.getChannels(), ChannelType.VOICE),
                        true
                )
                //look for the system channel
                .addField(
                        "System channel",
                        getSystemChannel(guild),
                        true
                )
                //get afk channel
                .addField(
                        "AFK channel",
                        getAfkChannel(guild),
                        true
                )
                //other minor fields
                .addField("Region", getRegion(guild), true)
                .addField("Verification level", capitalize(guild.getVerificationLevel().name()), true)
                .addField("Boost tier", capitalize(guild.getBoostTier().name().replace("_", " ")), true)
                .addField("Boosts", String.valueOf(guild.getBoostCount()), true);

        if (guild.getBannerUrl() != null)
            embed.setImage(guild.getBannerUrl());

        return embed.build();
    }
}