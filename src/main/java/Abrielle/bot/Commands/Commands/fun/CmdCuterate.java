package Abrielle.bot.Commands.Commands.fun;

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
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@CommandDescription(
        name = "cuterate",
        description = "Get the cuteness of a person.",
        triggers = {"cuterate", "howcute"},
        attributes = {
                @CommandAttribute(key = "category", value = "fun"),
                @CommandAttribute(key = "usage", value = "[command | alias] <user mention>"),
                @CommandAttribute(key = "examples", value = "`a!cuterate`\n" +
                        "`a!cuterate 418037700751261708`\n" +
                        "`a!cuterate @Drag0n#6666`")
        }
)

public record CmdCuterate(Abrielle bot) implements Command {

    @Override
    public void runSlash(Guild guild, TextChannel tc, Member member, SlashCommandInteractionEvent event, InteractionHook hook) {
        Member target;

        if (event.getOption("user") == null)
            target = member;
        else
            target = Objects.requireNonNull(event.getOption("user")).getAsMember();

        if (target == null)
            return;

        hook.sendMessageEmbeds(first()).queue(message -> {
            sendEmbed(message, target);
        });
    }

    @Override
    public void runCommand(Message msg, Guild guild, TextChannel tc, Member member) {
        String[] arguments = bot.getArguments(msg);
        Member target;

        if (arguments.length > 0) {
            if (!msg.getMentionedMembers().isEmpty()) {
                target = msg.getMentionedMembers(guild).get(0);
            } else {
                target = guild.getMemberById(arguments[0]);
            }
        } else {
            target = msg.getMember();
        }

        if (target == null) {
            try {
                throw new Exception("member value is null");
            } catch (Exception e) {
                Abrielle.getLogger().error(String.valueOf(e));
            }
            return;
        }

        tc.sendMessageEmbeds(first()).queue(message -> {
            sendEmbed(message, target);
        });
    }

    private void sendEmbed(Message msg, Member target) {
        MessageEmbed embed = msg.getEmbeds().get(0);
        EmbedBuilder last = new EmbedBuilder()
                .setTitle(embed.getTitle())
                .setColor(embed.getColor())
                .setTimestamp(ZonedDateTime.now());
        StringBuilder desc = new StringBuilder();
        int cuterate = getCuteRate();

        desc.append("**").append(target.getUser().getAsTag()).append("** cuteness is ").append("**").append(cuterate).append("%**!");
        last.setDescription(desc);

        if (cuterate > 75)
            last.setImage("https://i.gifer.com/XfFd.gif");

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            Abrielle.getLogger().error(String.valueOf(e));
        }

        msg.editMessageEmbeds(last.build()).queue();
    }

    private int getCuteRate() {
        return new Random().nextInt(101);
    }

    private MessageEmbed first() {
        return new EmbedBuilder().setTitle("cuterate")
                .setColor(Colors.NORMAL.getCode())
                .setDescription("Calculating!")
                .setTimestamp(ZonedDateTime.now())
                .build();
    }
}