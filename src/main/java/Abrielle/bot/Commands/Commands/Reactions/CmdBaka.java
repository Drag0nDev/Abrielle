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
        name = "baka",
        description = "Call someone a baka (idiot)",
        triggers = {"baka", "idiot"},
        attributes = {
                @CommandAttribute(key = "category", value = "reactions"),
                @CommandAttribute(key = "usage", value = "[command | alias] <mention/id>"),
                @CommandAttribute(key = "examples", value = "`a!baka 418037700751261708`\n" +
                        "`a!baka @Drag0n#6666`\n"),
        }
)

public record CmdBaka(Abrielle bot) implements Command {

    @Override
    public void runSlash(Guild guild, TextChannel tc, Member member, SlashCommandInteractionEvent event, InteractionHook hook) throws Exception {
        ArrayList<Member> bakas = new ArrayList<>();

        if (event.getOption("user") == null)
            bakas.add(member);
        else
            bakas.add(Objects.requireNonNull(event.getOption("user")).getAsMember());

        hook.sendMessage(baka(bakas, member)).queue();
    }

    @Override
    public void runCommand(Message msg, Guild guild, TextChannel tc, Member member) throws Exception {
        String[] args = bot.getArguments(msg);
        ArrayList<Member> bakas = new ArrayList<>();

        if (args.length == 0)
            bakas.add(member);
        else {
            if (!msg.getMentionedMembers().isEmpty())
                bakas.addAll(msg.getMentionedMembers());
            else
                for (String arg : args) bakas.add(guild.retrieveMemberById(arg).complete());
        }

        tc.sendMessage(baka(bakas, member)).queue();
    }

    private Message baka(ArrayList<Member> bakas, Member executor) {
        MessageBuilder message = new MessageBuilder();
        String content;

        if (bakas.get(0) == executor)
            content = bakas.get(0).getAsMention() + " you are an idiot!";
        else {
            String exec = executor.getNickname() != null ?
                    executor.getNickname() :
                    executor.getUser().getName();
            StringJoiner mentions = new StringJoiner(" ");

            for (Member member : bakas)
                mentions.add(member.getAsMention());

            content = mentions + " you are an idiot, with love, **" + exec + "**!";
        }

        return message
                .setContent(content)
                .setEmbeds(new EmbedBuilder()
                        .setTimestamp(LocalDateTime.now())
                        .setColor(Colors.NORMAL.getCode())
                        .setImage(ApiCalls.BAKA.get())
                        .setFooter("Powered by nekos.life")
                        .build())
                .build();
    }
}