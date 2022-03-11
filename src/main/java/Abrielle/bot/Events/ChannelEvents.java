package Abrielle.bot.Events;

import Abrielle.bot.Abrielle;
import Abrielle.constants.Colors;
import Abrielle.util.XMLHandling.LogChannels;
import Abrielle.util.utils.Config;
import club.minnced.discord.webhook.send.WebhookEmbed.*;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import jakarta.xml.bind.JAXBException;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.channel.update.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.*;

import static Abrielle.util.XMLHandling.XMLHandler.getLogChannels;
import static Abrielle.util.utils.Utils.capitalize;
import static net.dv8tion.jda.api.entities.ChannelType.*;

public class ChannelEvents extends ListenerAdapter {


    private static final Logger LOGGER = LoggerFactory.getLogger(Listener.class);
    private final Config config = new Config();
    private final Abrielle bot;

    public ChannelEvents(Abrielle bot) throws IOException, ParseException, URISyntaxException {
        this.bot = bot;
    }

    @Override
    public void onChannelCreate(@NotNull ChannelCreateEvent event) {
        Channel channel = event.getChannel();
        Guild guild = event.getGuild();
        ChannelType type = channel.getType();
        List<PermissionOverride> perms = new ArrayList<>();

        try {
            LogChannels logs = getLogChannels(guild);

            WebhookEmbedBuilder embed = new WebhookEmbedBuilder();

            EmbedTitle title = new EmbedTitle(capitalize(type.name()) + " Channel created", null);
            EmbedField nameField = new EmbedField(true, "Name", channel.getName());
            EmbedField catField = new EmbedField(true, " Category", getCategory(channel, guild));
            EmbedFooter footer = new EmbedFooter("Channel ID: " + channel.getId(), null);

            embed.setTitle(title)
                    .setFooter(footer)
                    .setColor(Colors.LOGADD.getCode())
                    .addField(nameField)
                    .addField(catField)
                    .setTimestamp(ZonedDateTime.now());

            switch (channel.getType()) {
                case TEXT, NEWS -> {
                    BaseGuildMessageChannel gChannel = guild.getTextChannelById(channel.getId());
                    if (gChannel == null)
                        break;
                    perms = gChannel.getMemberPermissionOverrides();
                }
                case VOICE -> {
                    VoiceChannel vc = guild.getVoiceChannelById(channel.getId());
                    if (vc == null)
                        break;
                    perms = vc.getMemberPermissionOverrides();
                }
                case STAGE -> {
                    StageChannel sc = guild.getStageChannelById(channel.getId());
                    if (sc == null)
                        break;
                    perms = sc.getMemberPermissionOverrides();
                }
            }

            if (!perms.isEmpty()) {
                for (PermissionOverride perm : perms) {
                    Member member = perm.getMember();

                    if (member != null) {
                        String permTitle = "Role overrides for " + member.getUser().getAsTag();
                        String perms1 = getPerms(perm.getAllowed(), perm.getDenied());

                        if (!perms1.equals("")) {
                            embed.addField(new EmbedField(false, permTitle, perms1));
                        }
                    } else {
                        Role role = perm.getRole();
                        if (role != null) {
                            String permTitle = "Role overrides for " + role.getName();
                            String perms1 = getPerms(perm.getAllowed(), perm.getDenied());

                            if (!perms1.equals("")) {
                                embed.addField(new EmbedField(false, permTitle, perms1));
                            }
                        }
                    }
                }
            }

            logs.sendServerLog(embed.build());
        } catch (JAXBException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public void onChannelDelete(@NotNull ChannelDeleteEvent event) {
        super.onChannelDelete(event);
    }

    @Override
    public void onChannelUpdateName(@NotNull ChannelUpdateNameEvent event) {
        super.onChannelUpdateName(event);
    }

    @Override
    public void onChannelUpdateNSFW(@NotNull ChannelUpdateNSFWEvent event) {
        super.onChannelUpdateNSFW(event);
    }

    @Override
    public void onChannelUpdateParent(@NotNull ChannelUpdateParentEvent event) {
        super.onChannelUpdateParent(event);
    }

    @Override
    public void onChannelUpdateRegion(@NotNull ChannelUpdateRegionEvent event) {
        super.onChannelUpdateRegion(event);
    }

    @Override
    public void onChannelUpdateSlowmode(@NotNull ChannelUpdateSlowmodeEvent event) {
        super.onChannelUpdateSlowmode(event);
    }

    @Override
    public void onChannelUpdateTopic(@NotNull ChannelUpdateTopicEvent event) {
        super.onChannelUpdateTopic(event);
    }

    @Override
    public void onChannelUpdateType(@NotNull ChannelUpdateTypeEvent event) {
        super.onChannelUpdateType(event);
    }

    @Override
    public void onChannelUpdateUserLimit(@NotNull ChannelUpdateUserLimitEvent event) {
        super.onChannelUpdateUserLimit(event);
    }

    private String getCategory(Channel channel, Guild guild) {
        Category cat;
        switch (channel.getType()) {
            case TEXT:
            case NEWS:
                BaseGuildMessageChannel gChannel = guild.getTextChannelById(channel.getId());
                if (gChannel == null)
                    return "Did not find parent category!";
                cat = gChannel.getParentCategory();
                if (cat == null)
                    return "No category";
                return cat.getName();
            case VOICE:
                VoiceChannel vc = guild.getVoiceChannelById(channel.getId());
                if (vc == null)
                    return "Did not find a parent category!";
                cat = vc.getParentCategory();
                if (cat == null)
                    return "No category";
                return cat.getName();
            case STAGE:
                StageChannel sc = guild.getStageChannelById(channel.getId());
                if (sc == null)
                    return "Did not find a parent category!";
                cat = sc.getParentCategory();
                if (cat == null)
                    return "No category";
                return cat.getName();
            case CATEGORY:
            default:
                return "No category";
        }
    }

    private String getPerms(EnumSet<Permission> allowed, EnumSet<Permission> denied) {
        if (allowed.isEmpty() && denied.isEmpty())
            return "";

        StringJoiner perms = new StringJoiner("\n");

        for (Permission perm : allowed) {
            perms.add(perm.getName() + " ✅");
        }

        for (Permission perm : denied) {
            perms.add(perm.getName() + " ❌");
        }

        return perms.toString();
    }
}