package Abrielle.bot.Commands.Commands.fun;

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
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@CommandDescription(
        name = "fruityrate",
        description = "Get the fruitiness of a person.",
        triggers = {"fruityrate", "howfruity"},
        attributes = {
                @CommandAttribute(key = "category", value = "fun"),
                @CommandAttribute(key = "usage", value = "[command | alias]"),
                @CommandAttribute(key = "examples", value = "a!fruityrate @Drag0n#6669"),
        }
)

public record CmdFruityrate(Abrielle bot) implements Command {

    @Override
    public void runSlash(Guild guild, TextChannel tc, Member member, SlashCommandInteractionEvent event, InteractionHook hook) throws Exception {
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
    public void runCommand(Message msg, Guild guild, TextChannel tc, Member member) throws Exception {
        String[] arguments = bot.getArguments(msg);
        Member target;

        try {
            if (arguments.length > 0) {
                if (!msg.getMentionedMembers().isEmpty()) {
                    target = msg.getMentionedMembers().get(0);
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
        } catch (NumberFormatException e) {
            throw new AbrielleException("Please use id or mentions on this command!\n**Role mentions are not allowed!**");
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
        int fruityrate = getFruityRate();

        desc.append("**").append(target.getUser().getAsTag()).append("** is ").append("**").append(fruityrate).append("%** gay!");
        last.setDescription(desc);

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            Abrielle.getLogger().error(String.valueOf(e));
        }

        msg.editMessageEmbeds(last.build()).queue();
    }

    private int getFruityRate() {
        return new Random().nextInt(101);
    }

    private MessageEmbed first() {
        return new EmbedBuilder().setTitle(this.getDescription().name())
                .setColor(Colors.NORMAL.getCode())
                .setDescription("Calculating!")
                .setTimestamp(ZonedDateTime.now())
                .build();
    }
}