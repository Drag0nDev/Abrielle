package Abrielle.bot.Commands.Commands.logging;

import Abrielle.bot.Abrielle;
import Abrielle.bot.Commands.Command;
import Abrielle.constants.Colors;
import Abrielle.util.XMLHandling.LogChannels;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.managers.WebhookManager;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;

import static Abrielle.util.XMLHandling.XMLHandler.writeToXml;

@CommandDescription(
        name = "createlogs",
        description = "Creates all log channels",
        triggers = {"createlogs", "cl"},
        attributes = {
                @CommandAttribute(key = "category", value = "logging"),
                @CommandAttribute(key = "usage", value = "[command | alias]"),
                @CommandAttribute(key = "examples", value = "a!cl"),
        }
)

public record CmdCreateLogs(Abrielle bot) implements Command {

    @Override
    public void runSlash(Guild guild, TextChannel tc, Member member, SlashCommandInteractionEvent event, InteractionHook hook) throws Exception {

    }

    @Override
    public void runCommand(Message msg, Guild guild, TextChannel tc, Member member) throws Exception {
        Collection<Permission> allow = new ArrayList<>();
        allow.add(Permission.VIEW_CHANNEL);
        allow.add(Permission.MANAGE_CHANNEL);

        Collection<Permission> deny = new ArrayList<>();

        Category category = guild.createCategory("Logs")
                .addMemberPermissionOverride(member.getIdLong(), allow, deny)
                .addMemberPermissionOverride(Abrielle.getBot().getSelfUser().getIdLong(), allow, deny)
                .addPermissionOverride(guild.getPublicRole(), deny, allow)
                .complete();

        TextChannel jlLog = category.createTextChannel("join-leave-log").complete();
        TextChannel mLog = category.createTextChannel("member-log").complete();
        TextChannel sLog = category.createTextChannel("server-log").complete();
        TextChannel msglLog = category.createTextChannel("message-log").complete();
        TextChannel vLog = category.createTextChannel("voice-log").complete();

        Webhook jlHook = jlLog.createWebhook(Abrielle.getBot().getSelfUser().getName()).complete();
        Webhook mHook = mLog.createWebhook(Abrielle.getBot().getSelfUser().getName()).complete();
        Webhook sHook = sLog.createWebhook(Abrielle.getBot().getSelfUser().getName()).complete();
        Webhook msgHook = msglLog.createWebhook(Abrielle.getBot().getSelfUser().getName()).complete();
        Webhook vHook = vLog.createWebhook(Abrielle.getBot().getSelfUser().getName()).complete();

        setAvatar(jlHook);
        setAvatar(mHook);
        setAvatar(sHook);
        setAvatar(msgHook);
        setAvatar(vHook);


        LogChannels logs = new LogChannels();
        logs.setJoinLeaveHook(jlHook.getUrl());
        logs.setMemberHook(mHook.getUrl());
        logs.setServerHook(sHook.getUrl());
        logs.setMessageHook(msgHook.getUrl());
        logs.setVoiceHook(vHook.getUrl());

        writeToXml(logs, guild);

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Colors.NORMAL.getCode())
                .setTitle("Create logs")
                .setTimestamp(ZonedDateTime.now());

        embed.addField("Join leave log", jlLog.getAsMention(), true)
            .addField("Member log", mLog.getAsMention(), true)
            .addField("Server log", sLog.getAsMention(), true)
            .addField("Message log", msglLog.getAsMention(), true)
            .addField("Voice log", vLog.getAsMention(), true);

        tc.sendMessageEmbeds(embed.build()).queue();
    }

    private void setAvatar(Webhook hook) throws IOException {
        WebhookManager manager = hook.getManager();
        SelfUser bot = Abrielle.getBot().getSelfUser();

        Icon av = Icon.from(new URL(bot.getEffectiveAvatarUrl()).openStream());

        manager.setAvatar(av).queue();
    }

    @Override
    public ArrayList<Permission> getNeededPermissions() {
        ArrayList<Permission> permissions = new ArrayList<>();
        permissions.add(Permission.MANAGE_CHANNEL);
        permissions.add(Permission.MANAGE_PERMISSIONS);
        permissions.add(Permission.MANAGE_WEBHOOKS);
        return permissions;
    }
}