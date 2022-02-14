package Abrielle.bot.Commands.Commands.Reactions;

import Abrielle.bot.Abrielle;
import Abrielle.bot.Commands.Command;
import Abrielle.constants.ApiCalls;
import Abrielle.constants.Colors;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.StringJoiner;

@CommandDescription(
        name = "hug",
        description = "Hug someone",
        triggers = {"hug"},
        attributes = {
                @CommandAttribute(key = "category", value = "reactions"),
                @CommandAttribute(key = "usage", value = "[command | alias] <mention/id>"),
                @CommandAttribute(key = "examples", value = "`a!hug 418037700751261708`\n" +
                        "`a!hug @Drag0n#6666`\n"),
        }
)

public record CmdHug(Abrielle bot) implements Command {

    @Override
    public void runSlash(Guild guild, TextChannel tc, Member member, SlashCommandInteractionEvent event, InteractionHook hook) throws Exception {
        ArrayList<Member> hugged = new ArrayList<>();

        if (event.getOption("user") == null)
            hugged.add(member);
        else
            hugged.add(Objects.requireNonNull(event.getOption("user")).getAsMember());

        hook.sendMessage(hug(hugged, member)).queue();
    }

    @Override
    public void runCommand(Message msg, Guild guild, TextChannel tc, Member member) throws Exception {
        String[] args = bot.getArguments(msg);
        ArrayList<Member> hugged = new ArrayList<>();

        if (args.length == 0)
            hugged.add(member);
        else {
            if (!msg.getMentionedMembers().isEmpty())
                hugged.addAll(msg.getMentionedMembers());
            else
                for (String arg : args) hugged.add(guild.retrieveMemberById(arg).complete());
        }

        tc.sendMessage(hug(hugged, member)).queue();
    }

    private Message hug(ArrayList<Member> hugged, Member executor) {
        MessageBuilder message = new MessageBuilder();
        String content;

        if (hugged.get(0) == executor)
            content = "*Hugs* " + hugged.get(0).getAsMention();
        else {
            String exec = executor.getNickname() != null ?
                    executor.getNickname() :
                    executor.getUser().getName();
            StringJoiner mentions = new StringJoiner(" ");

            for (Member member : hugged)
                mentions.add(member.getAsMention());

            content = mentions + " you have been hugged by **" + exec + "**!";
        }

        return message
                .setContent(content)
                .setEmbeds(new EmbedBuilder()
                        .setTimestamp(LocalDateTime.now())
                        .setColor(Colors.NORMAL.getCode())
                        .setImage(ApiCalls.HUG.get())
                        .setFooter("Powered by nekos.life")
                        .build())
                .build();
    }
}