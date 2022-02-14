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
        name = "pat",
        description = "Pat someone",
        triggers = {"pat"},
        attributes = {
                @CommandAttribute(key = "category", value = "reactions"),
                @CommandAttribute(key = "usage", value = "[command | alias] <mention/id>"),
                @CommandAttribute(key = "examples", value = "`a!hug 418037700751261708`\n" +
                        "`a!pat @Drag0n#6666`\n"),
        }
)

public record CmdPat(Abrielle bot) implements Command {

    @Override
    public void runSlash(Guild guild, TextChannel tc, Member member, SlashCommandInteractionEvent event, InteractionHook hook) throws Exception {
        ArrayList<Member> targets = new ArrayList<>();

        if (event.getOption("user") == null)
            targets.add(member);
        else
            targets.add(Objects.requireNonNull(event.getOption("user")).getAsMember());

        hook.sendMessage(pat(targets, member)).queue();
    }

    @Override
    public void runCommand(Message msg, Guild guild, TextChannel tc, Member member) throws Exception {
        String[] args = bot.getArguments(msg);
        ArrayList<Member> targets = new ArrayList<>();

        if (args.length == 0)
            targets.add(member);
        else {
            if (!msg.getMentionedMembers().isEmpty())
                targets.addAll(msg.getMentionedMembers());
            else
                for (String arg : args) targets.add(guild.retrieveMemberById(arg).complete());
        }

        tc.sendMessage(pat(targets, member)).queue();
    }

    private Message pat(ArrayList<Member> targets, Member executor) {
        MessageBuilder message = new MessageBuilder();
        String content;

        if (targets.get(0) == executor)
            content = "*Pats* " + targets.get(0).getAsMention();
        else {
            String exec = executor.getNickname() != null ?
                    executor.getNickname() :
                    executor.getUser().getName();
            StringJoiner mentions = new StringJoiner(" ");

            for (Member member : targets)
                mentions.add(member.getAsMention());

            content = mentions + " you have been patted by **" + exec + "**!";
        }

        return message
                .setContent(content)
                .setEmbeds(new EmbedBuilder()
                        .setTimestamp(LocalDateTime.now())
                        .setColor(Colors.NORMAL.getCode())
                        .setImage(ApiCalls.PAT.get())
                        .setFooter("Powered by nekos.life")
                        .build())
                .build();
    }
}