package Abrielle.bot.Events;

import Abrielle.bot.Abrielle;
import Abrielle.constants.Colors;
import Abrielle.util.XMLHandling.LogChannels;
import Abrielle.util.utils.Config;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbed.*;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import jakarta.xml.bind.JAXBException;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.StringJoiner;

import static Abrielle.util.XMLHandling.XMLHandler.getLogChannels;

public class JoinLeaveEvents extends ListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(Listener.class);
    private final Config config = new Config();
    private final Abrielle bot;

    public JoinLeaveEvents(Abrielle bot) throws IOException, ParseException, URISyntaxException {
        this.bot = bot;
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        Member member = event.getMember();
        Guild guild = event.getGuild();

        try {
            LogChannels logs = getLogChannels(guild);

            WebhookEmbedBuilder embed = new WebhookEmbedBuilder();

            //embed variables
            EmbedTitle title = new EmbedTitle("Member joined", null);
            EmbedAuthor author = new EmbedAuthor(member.getUser().getAsTag(), member.getEffectiveAvatarUrl(), null);
            EmbedField membercount = new EmbedField(true, "Membercount", String.valueOf(guild.getMemberCount()));
            EmbedField accountAge = new EmbedField(true, "Account age", calculateTime(member.getTimeCreated()));
            EmbedFooter footer = new EmbedFooter("ID: " + member.getId(), null);

            embed.setTitle(title)
                    .setColor(Colors.JOIN.getCode())
                    .setAuthor(author)
                    .setDescription(member.getAsMention() + " joined the server.")
                    .addField(membercount)
                    .addField(accountAge)
                    .setFooter(footer)
                    .setTimestamp(ZonedDateTime.now());

            logs.sendJoinLeaveLog(embed.build(), guild);
        } catch (JAXBException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        Member member = event.getMember();
        Guild guild = event.getGuild();

        try {
            LogChannels logs = getLogChannels(guild);

            WebhookEmbedBuilder embed = new WebhookEmbedBuilder();

            StringJoiner roleJoiner = new StringJoiner("\n");

            if (member == null) {
                LOGGER.error("Member leave event triggered but member value was 'null'");
                return;
            }

            for (Role role : member.getRoles()) {
                roleJoiner.add(role.getAsMention());
            }

            String roleString = roleJoiner.toString().equals("") ? "No roles" : roleJoiner.toString();

            //embed variables
            EmbedTitle title = new EmbedTitle("Member left", null);
            EmbedAuthor author = new EmbedAuthor(member.getUser().getAsTag(), member.getEffectiveAvatarUrl(), null);
            EmbedField membercount = new EmbedField(true, "Membercount", String.valueOf(guild.getMemberCount()));
            EmbedField accountAge = new EmbedField(true, "Time in server", calculateTime(member.getTimeJoined()));
            EmbedField roles = new EmbedField(true, "Roles", roleString);
            EmbedFooter footer = new EmbedFooter("ID: " + member.getId(), null);

            embed.setTitle(title)
                    .setColor(Colors.LEAVE.getCode())
                    .setAuthor(author)
                    .addField(membercount)
                    .addField(accountAge)
                    .addField(roles)
                    .setFooter(footer)
                    .setTimestamp(ZonedDateTime.now());


            logs.sendJoinLeaveLog(embed.build(), guild);
        } catch (JAXBException e) {
            LOGGER.error(e.getMessage());
        }
    }

    private @NotNull String calculateTime(@NotNull OffsetDateTime date) {
        ZonedDateTime now = ZonedDateTime.now();

        long diff = Math.abs(date.toEpochSecond() - now.toEpochSecond());

        long second = diff % 60;
        long minute = (diff / 60) % 60;
        long hour = (diff / (60 * 60)) % 24;
        long day = (diff / (60 * 60 * 24)) % 365;
        long year = diff / (60 * 60 * 24 * 30 * 12);

        StringJoiner joiner = new StringJoiner(", ");

        if (year != 0)
            joiner.add(year + " years");
        if (day != 0)
            joiner.add(day + " days");
        if (hour != 0)
            joiner.add(hour + " hours");
        if (minute != 0)
            joiner.add(minute + " minutes");
        if (second != 0)
            joiner.add(second + " seconds");

        return joiner.toString();
    }
}
