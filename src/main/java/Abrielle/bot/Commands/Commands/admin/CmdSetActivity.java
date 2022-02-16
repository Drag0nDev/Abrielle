package Abrielle.bot.Commands.Commands.admin;

import Abrielle.bot.Abrielle;
import Abrielle.bot.Commands.Command;
import Abrielle.constants.Colors;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.time.ZonedDateTime;
import java.util.ArrayList;

import static Abrielle.bot.Abrielle.getBot;

@CommandDescription(
        name = "setactivity",
        description = "Set a new status for Abrielle",
        triggers = {"setactivity", "activity", "sa"},
        attributes = {
                @CommandAttribute(key = "category", value = "admin"),
                @CommandAttribute(key = "usage", value = "[command | alias]"),
                @CommandAttribute(key = "examples", value = "a!sa new activity"),
        }
)

public record CmdSetActivity(Abrielle bot) implements Command {

    @Override
    public void runSlash(Guild guild, TextChannel tc, Member member, SlashCommandInteractionEvent event, InteractionHook hook) throws Exception {
        String activity = event.getOption("activity").getAsString();
        String message = event.getOption("message").getAsString();
        String url = event.getOption("url").getAsString();

        hook.sendMessageEmbeds(setActivity(activity, message, url)).queue();
    }

    @Override
    public void runCommand(Message msg, Guild guild, TextChannel tc, Member member) throws Exception {
        String[] args = bot.getArguments(msg);

        if (args.length < 2) {
            tc.sendMessageEmbeds(
                    new EmbedBuilder()
                            .setTitle("Set activity")
                            .setColor(Colors.ERROR.getCode())
                            .setDescription("Please provide the required arguments")
                            .setTimestamp(ZonedDateTime.now())
                            .build()
            ).queue();
            return;
        }

        String activity = args[0];
        String url = "";
        String message;
        String[] msgArgs;
        if (activity.equalsIgnoreCase("streaming")) {
            msgArgs = new String[args.length - 2];
            url = args[args.length - 1];

            System.arraycopy(args, 1, msgArgs, 0, args.length - 2);

        } else {
            msgArgs = new String[args.length - 1];

            System.arraycopy(args, 1, msgArgs, 0, args.length - 1);

        }
        message = String.join(" ", msgArgs);

        tc.sendMessageEmbeds(setActivity(activity, message, url)).queue();
    }

    private MessageEmbed setActivity(String activity, String message, String url) {
        Activity activityObj;
        switch (activity) {
            case "streaming":
                activityObj = Activity.streaming(message, url);
                getBot().getPresence().setActivity(activityObj);
                return new EmbedBuilder()
                        .setTitle("Set activity")
                        .setColor(Colors.NORMAL.getCode())
                        .setTimestamp(ZonedDateTime.now())
                        .setDescription("New activity set")
                        .addField("Type", activity, true)
                        .addField("Message", message, true)
                        .addField("URL", url, true)
                        .build();
            case "playing":
                activityObj = Activity.playing(message);
                getBot().getPresence().setActivity(activityObj);
                break;
            case "listening":
                activityObj = Activity.listening(message);
                getBot().getPresence().setActivity(activityObj);
                break;
            case "watching":
                activityObj = Activity.watching(message);
                getBot().getPresence().setActivity(activityObj);
                break;
            default:
                return new EmbedBuilder()
                        .setTitle("Set activity")
                        .setColor(Colors.ERROR.getCode())
                        .setTimestamp(ZonedDateTime.now())
                        .setDescription("The activity " + activity + " is not possible!")
                        .build();
        }
        return new EmbedBuilder()
                .setTitle("Set activity")
                .setColor(Colors.NORMAL.getCode())
                .setTimestamp(ZonedDateTime.now())
                .setDescription("New activity set")
                .addField("Type", activity, true)
                .addField("Message", message, true)
                .build();
    }

    @Override
    public ArrayList<Permission> getNeededPermissions() {
        ArrayList<Permission> permissions = new ArrayList<>();
        permissions.add(Permission.ADMINISTRATOR);
        return permissions;
    }
}