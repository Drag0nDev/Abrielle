package Abrielle.bot.Commands;

import Abrielle.bot.Abrielle;
import Abrielle.bot.Events.Listener;
import Abrielle.constants.Colors;
import Abrielle.util.Exceptions.AbrielleException;
import com.github.rainestormee.jdacommand.CommandHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

import static Abrielle.util.utils.Utils.getArgs;

public class CommandListener extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(Listener.class);

    private final ThreadGroup CMD_THREAD = new ThreadGroup("CommandThread");
    private final Executor CMD_EXECUTOR = Executors.newCachedThreadPool(
            r -> new Thread(CMD_THREAD, r, "CommandPool")
    );

    private final Abrielle bot;
    private final CommandHandler<Message> HANDLER;

    public CommandListener(Abrielle bot, CommandHandler<Message> HANDLER) {
        this.bot = bot;
        this.HANDLER = HANDLER;
    }

    /*@Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        if (event.getGuild() == null)
            return;

        bot.getCmdHandler().findCommand("baka");

        CMD_EXECUTOR.execute(() -> {
            Guild guild = event.getGuild();
            Member member = guild.retrieveMemberById(event.getUser().getId()).complete();
            MessageChannel tc = event.getChannel();
            Member self = guild.getSelfMember();

            Command command = (Command) HANDLER.findCommand(event.getName());

            if (command == null)
                return;

            if (!self.hasPermission((GuildChannel) tc, Permission.MESSAGE_SEND))
                return;

            StringBuilder args = new StringBuilder();

            if (command.getOptionNames() == null) {
                args = new StringBuilder();
            } else {
                for (String option : command.getOptionNames())
                    args.append(event.getOption(option) == null ? "" : Objects.requireNonNull(event.getOption(option)).getAsString());
            }

            try {
                sendLogger(
                        event.getName(),
                        args.toString(),
                        member.getUser().getAsTag(),
                        member.getId(),
                        guild.getName(),
                        guild.getId(),
                        tc.getName()
                );

                if (command.getAttribute("category").equals("owner") && !member.getId().equals(bot.getConfig().getOwner())) {
                    sendOwnerOnly(event.getHook(), command);
                    return;
                }

                if (command.getNeededPermissions() != null) {
                    for (Permission permission : command.getNeededPermissions()) {
                        //check member permissions
                        if (!member.hasPermission(permission)) {
                            sendNoPermissionError(event.getHook(), member, self, permission);
                            return;
                        }

                        //check bot permissions
                        if (!self.hasPermission(permission)) {
                            sendNoPermissionError(event.getHook(), self, self, permission);
                            return;
                        }
                    }
                }

                command.runSlash(event.getGuild(), event.getTextChannel(), event.getMember(), event, event.getHook());
            } catch (AbrielleException message) {
                sendCustomError(event.getHook(), message);
            } catch (Exception e) {
                LOGGER.error("Couldn't preform command {}!", event.getName(), e);
            }
        });
    }*/

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!event.isFromGuild())
            return;

        Message msg = event.getMessage();
        Guild guild = event.getGuild();
        Member member = event.getMember();

        if (member == null || event.isWebhookMessage())
            return;

        if (!event.getChannel().getType().equals(ChannelType.TEXT))
            return;

        if (member.getUser().isBot())
            return;

        CMD_EXECUTOR.execute(() -> {
            if (!checkPrefix(msg.getContentRaw()))
                return;

            String raw = msg.getContentRaw();

            //find the used prefix
            List<String> prefix = bot.getConfig().getPrefix();
            raw = getArgs(raw, prefix);
            int i;
            String[] args = Arrays.copyOfRange(raw.trim().split("\\s+"), 0, 10);

            TextChannel tc = event.getTextChannel();
            Member self = guild.getSelfMember();

            if (args[0] == null)
                return;

            Command command = (Command) HANDLER.findCommand(args[0].toLowerCase());

            if (command == null)
                return;

            if (!self.hasPermission(tc, Permission.MESSAGE_SEND))
                return;

            try {
                for (i = 1; i < 10; i++) {
                    if (args[i] == null)
                        args[i] = "";
                }
                String[] arguments = Arrays.copyOfRange(args, 1, 10);
                StringBuilder argumentsLog = new StringBuilder();
                for (String arg : arguments) {
                    if (!arg.equals(""))
                        argumentsLog.append(arg).append(" ");
                }
                sendLogger(
                        command.getDescription().name(),
                        argumentsLog.toString(),
                        member.getUser().getAsTag(),
                        member.getId(),
                        guild.getName(),
                        guild.getId(),
                        tc.getName()
                );

                if (command.getAttribute("category").equals("owner") && !member.getId().equals(bot.getConfig().getOwner())) {
                    sendOwnerOnly(tc, command);
                    return;
                }

                if (command.getNeededPermissions() != null) {
                    for (Permission permission : command.getNeededPermissions()) {
                        //check member permissions
                        if (!member.hasPermission(permission)) {
                            sendNoPermissionError(tc, member, self, permission);
                            return;
                        }

                        //check bot permissions
                        if (!self.hasPermission(permission)) {
                            sendNoPermissionError(tc, self, self, permission);
                            return;
                        }
                    }
                }

                command.runCommand(msg, msg.getGuild(), msg.getTextChannel(), msg.getMember());
            } catch (AbrielleException message) {
                sendCustomError(tc, message);
            } catch (Exception e) {
                LOGGER.info("Couldn't preform command {}!", command.getDescription().name(), e);
            }
        });
    }

    private void sendLogger(String cmdName, String args, String executorTag, String executorId, String guildName, String guildId, String tcName) {
        LOGGER.info("------------------------------\n" +
                        "Command: '{}'\n" +
                        "Arguments: '{}'\n" +
                        "User: '{}'\n" +
                        "User ID: '{}'\n" +
                        "Server: '{}'\n" +
                        "Server ID: '{}'\n" +
                        "Channel: '{}'",
                cmdName,
                args,
                executorTag,
                executorId,
                guildName,
                guildId,
                tcName
        );
    }

    private boolean checkPrefix(String message) {
        List<String> prefix = bot.getConfig().getPrefix();

        for (String newPrefix : prefix) {
            String toFind = "^" + newPrefix;
            Pattern pattern = Pattern.compile(toFind, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(message);
            boolean matchFound = matcher.find();
            if (matchFound) return true;
        }

        return false;
    }

    private void sendCustomError(@NotNull TextChannel tc, @NotNull AbrielleException e) {
        EmbedBuilder embed = new EmbedBuilder().setColor(Colors.ERROR.getCode())
                .setDescription(e.getMessage())
                .setTimestamp(ZonedDateTime.now());

        tc.sendMessageEmbeds(embed.build()).queue();
    }

    private void sendCustomError(@NotNull InteractionHook hook, @NotNull AbrielleException e) {
        EmbedBuilder embed = new EmbedBuilder().setColor(Colors.ERROR.getCode())
                .setDescription(e.getMessage())
                .setTimestamp(ZonedDateTime.now());

        hook.sendMessageEmbeds(embed.build()).queue();
    }

    private void sendNoPermissionError(@NotNull InteractionHook hook, Member member, Member self, Permission permission) {
        hook.sendMessageEmbeds(sendNoPermissionEmbed(member, self, permission)).queue();
    }

    private void sendNoPermissionError(@NotNull TextChannel tc, Member member, Member self, Permission permission) {
        tc.sendMessageEmbeds(sendNoPermissionEmbed(member, self, permission)).queue();
    }

    private void sendOwnerOnly(@NotNull TextChannel tc, @NotNull Command cmd) {
        tc.sendMessageEmbeds(
                        new EmbedBuilder().setColor(Colors.ERROR.getCode())
                                .setTitle("Owner only")
                                .setDescription("This command (**" + cmd.getDescription().name() + "**) is only for the owner of the bot")
                                .setTimestamp(ZonedDateTime.now())
                                .build())
                .queue();
    }

    private void sendOwnerOnly(@NotNull InteractionHook tc, @NotNull Command cmd) {
        tc.sendMessageEmbeds(
                        new EmbedBuilder().setColor(Colors.ERROR.getCode())
                                .setTitle("Owner only")
                                .setDescription("This command (**" + cmd.getDescription().name() + "**) is only for the owner of the bot")
                                .setTimestamp(ZonedDateTime.now())
                                .build())
                .queue();
    }

    private @NotNull MessageEmbed sendNoPermissionEmbed(Member member, Member self, Permission permission) {
        EmbedBuilder embed = new EmbedBuilder().setColor(Colors.ERROR.getCode())
                .setTitle("No Permission")
                .setTimestamp(ZonedDateTime.now());

        if (member == self) {
            embed.setDescription(
                    "I don't have the required permission to run this command\n" +
                            "**Missing permission:** " + permission.getName()
            );
        } else {
            embed.setDescription(
                    "You don't have the required permission to run this command\n" +
                            "**Missing permission:** " + permission.getName()
            );
        }

        return embed.build();
    }
}
