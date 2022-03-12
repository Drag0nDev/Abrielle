package Abrielle.bot.Events;

import Abrielle.bot.Abrielle;
import Abrielle.constants.Colors;
import Abrielle.util.XMLHandling.LogChannels;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbed.*;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import jakarta.xml.bind.JAXBException;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.Region;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.channel.update.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.*;

import static Abrielle.util.XMLHandling.XMLHandler.getLogChannels;
import static Abrielle.util.utils.Utils.capitalize;

public class ChannelEvents extends ListenerAdapter {


    private static final Logger LOGGER = LoggerFactory.getLogger(Listener.class);
    private final Abrielle bot;

    public ChannelEvents(Abrielle bot) {
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

            WebhookEmbedBuilder embed = baseEmbedCreateDelete(channel, guild, type, Colors.LOGADD.getCode(), "created");

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
        Channel channel = event.getChannel();
        Guild guild = event.getGuild();
        ChannelType type = channel.getType();

        try {
            LogChannels logs = getLogChannels(guild);
            WebhookEmbedBuilder embed = baseEmbedCreateDelete(channel, guild, type, Colors.LOGREMOVE.getCode(), "deleted");
            logs.sendServerLog(embed.build());
        } catch (JAXBException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public void onChannelUpdateName(@NotNull ChannelUpdateNameEvent event) {
        Channel channel = event.getChannel();
        String oldName = event.getOldValue();
        String newName = event.getNewValue();
        Guild guild = event.getGuild();
        ChannelType type = channel.getType();

        if (oldName == null || newName == null)
            return;

        try {
            getLogChannels(guild).sendServerLog(baseEmbed(channel, type, oldName, newName, null));
        } catch (JAXBException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public void onChannelUpdateNSFW(@NotNull ChannelUpdateNSFWEvent event) {
        Channel channel = event.getChannel();
        Boolean oldState = event.getOldValue();
        Boolean newState = event.getNewValue();
        Guild guild = event.getGuild();
        ChannelType type = channel.getType();

        if (oldState == null || newState == null)
            return;

        try {
            LogChannels logs = getLogChannels(guild);
            WebhookEmbedBuilder embed = new WebhookEmbedBuilder();

            EmbedTitle title = new EmbedTitle(capitalize(type.name()) + " channel updated NSFW state", null);
            EmbedField nameField = new EmbedField(true, "Before", oldState ? "NSFW" : "Not NSFW");
            EmbedField catField = new EmbedField(true, "After", newState ? "NSFW" : "Not NSFW");
            EmbedFooter footer = new EmbedFooter("Channel ID: " + channel.getId(), null);

            embed.setTitle(title)
                    .setFooter(footer)
                    .setColor(Colors.LOGCHANGE.getCode())
                    .addField(nameField)
                    .addField(catField)
                    .setTimestamp(ZonedDateTime.now());

            logs.sendServerLog(embed.build());
        } catch (JAXBException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public void onChannelUpdateParent(@NotNull ChannelUpdateParentEvent event) {
        Channel channel = event.getChannel();
        Category olcCat = event.getOldValue();
        Category newCat = event.getNewValue();
        Guild guild = event.getGuild();
        ChannelType type = channel.getType();

        if (olcCat == null || newCat == null)
            return;

        try {
            getLogChannels(guild).sendServerLog(baseEmbed(channel, type, olcCat.getName(), newCat.getName(), channel.getAsMention() + " **changed category"));
        } catch (JAXBException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public void onChannelUpdateRegion(@NotNull ChannelUpdateRegionEvent event) {
        Channel channel = event.getChannel();
        Region oldName = event.getOldValue();
        Region newName = event.getNewValue();
        Guild guild = event.getGuild();
        ChannelType type = channel.getType();

        if (oldName == null || newName == null)
            return;

        try {
            getLogChannels(guild).sendServerLog(baseEmbed(channel, type, oldName.getName(), newName.name(), channel.getAsMention() + " **changed region**"));
        } catch (JAXBException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public void onChannelUpdateSlowmode(@NotNull ChannelUpdateSlowmodeEvent event) {
        Channel channel = event.getChannel();
        Integer oldLength = event.getOldValue();
        Integer newLength = event.getNewValue();
        Guild guild = event.getGuild();
        ChannelType type = channel.getType();

        if (oldLength == null || newLength == null)
            return;

        try {
            getLogChannels(guild).sendServerLog(baseEmbed(channel, type, oldLength + "s", newLength + "s", channel.getAsMention() + " **changed slow mode time**"));
        } catch (JAXBException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public void onChannelUpdateTopic(@NotNull ChannelUpdateTopicEvent event) {
        Channel channel = event.getChannel();
        String oldTopic = event.getOldValue();
        String newTopic = event.getNewValue();
        Guild guild = event.getGuild();
        ChannelType type = channel.getType();

        if (oldTopic == null || newTopic == null)
            return;

        try {
            getLogChannels(guild).sendServerLog(baseEmbed(channel, type, oldTopic, newTopic, channel.getAsMention() + " **changed topic**"));
        } catch (JAXBException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public void onChannelUpdateType(@NotNull ChannelUpdateTypeEvent event) {
        Channel channel = event.getChannel();
        ChannelType oldType = event.getOldValue();
        ChannelType newType = event.getNewValue();
        Guild guild = event.getGuild();
        ChannelType type = channel.getType();

        if (oldType == null || newType == null)
            return;

        try {
            getLogChannels(guild).sendServerLog(baseEmbed(channel, type, oldType.name(), newType.name(), channel.getAsMention() + " **changed type**"));
        } catch (JAXBException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    public void onChannelUpdateUserLimit(@NotNull ChannelUpdateUserLimitEvent event) {
        super.onChannelUpdateUserLimit(event);
    }

    private WebhookEmbedBuilder baseEmbedCreateDelete(Channel channel, Guild guild, ChannelType type, int color, String action) {
        WebhookEmbedBuilder embed = new WebhookEmbedBuilder();

        EmbedTitle title = new EmbedTitle(capitalize(type.name()) + " channel " + action, null);
        EmbedField nameField = new EmbedField(true, "Name", channel.getName());
        EmbedField catField = new EmbedField(true, " Category", getCategory(channel, guild));
        EmbedFooter footer = new EmbedFooter("Channel ID: " + channel.getId(), null);

        embed.setTitle(title)
                .setFooter(footer)
                .setColor(color)
                .addField(nameField)
                .addField(catField)
                .setTimestamp(ZonedDateTime.now());

        return embed;
    }

    private WebhookEmbed baseEmbed(Channel channel, ChannelType type, String old, String now, String description) {
        WebhookEmbedBuilder embed = new WebhookEmbedBuilder();

        EmbedTitle title = new EmbedTitle(capitalize(type.name()) + " channel updated", null);
        EmbedField nameField = new EmbedField(true, "Before", old);
        EmbedField catField = new EmbedField(true, "After", now);
        EmbedFooter footer = new EmbedFooter("Channel ID: " + channel.getId(), null);

        embed.setTitle(title)
                .setFooter(footer)
                .setColor(Colors.LOGCHANGE.getCode())
                .addField(nameField)
                .addField(catField)
                .setTimestamp(ZonedDateTime.now());

        if (description != null)
            embed.setDescription(description);

        return embed.build();
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
