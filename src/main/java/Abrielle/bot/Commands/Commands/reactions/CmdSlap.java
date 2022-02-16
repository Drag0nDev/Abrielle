package Abrielle.bot.Commands.Commands.reactions;

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

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.StringJoiner;

@CommandDescription(
        name = "slap",
        description = "Slap someone",
        triggers = {"slap"},
        attributes = {
                @CommandAttribute(key = "category", value = "reactions"),
                @CommandAttribute(key = "usage", value = "[command | alias] <mention/id>"),
                @CommandAttribute(key = "examples", value = "`a!slap 418037700751261708`\n" +
                        "`a!slap @Drag0n#6666`\n"),
        }
)

public record CmdSlap(Abrielle bot) implements Command {

    @Override
    public void runSlash(Guild guild, TextChannel tc, Member member, SlashCommandInteractionEvent event, InteractionHook hook) throws Exception {
        ArrayList<Member> members = new ArrayList<>();
        ArrayList<Role> roles = new ArrayList<>();

        if (event.getOption("user") == null)
            members.add(member);
        if (event.getOption("role") != null)
            roles.add(Objects.requireNonNull(event.getOption("role")).getAsRole());
        else
            members.add(Objects.requireNonNull(event.getOption("user")).getAsMember());

        hook.sendMessage(slap(members, roles, member)).queue();
    }

    @Override
    public void runCommand(Message msg, Guild guild, TextChannel tc, Member member) throws Exception {
        String[] args = bot.getArguments(msg);
        ArrayList<Member> members = new ArrayList<>();
        ArrayList<Role> roles = new ArrayList<>();

        if (args.length == 0)
            members.add(member);
        else {
            if (!msg.getMentionedRoles().isEmpty())
                roles.addAll(msg.getMentionedRoles());
            if (!msg.getMentionedMembers().isEmpty())
                members.addAll(msg.getMentionedMembers());
            else
                for (String arg : args)
                    if (arg.matches("\\d*"))
                        members.add(guild.retrieveMemberById(arg).complete());
        }

        tc.sendMessage(slap(members, roles, member)).queue();
    }

    private Message slap(ArrayList<Member> members, ArrayList<Role> roles, Member executor) {
        MessageBuilder message = new MessageBuilder();
        String content;

        if (roles.isEmpty() && members.isEmpty())
            content = "*Slaps* " + executor.getAsMention();
        else {
            String exec = executor.getNickname() != null ?
                    executor.getNickname() :
                    executor.getUser().getName();
            StringJoiner mentions = new StringJoiner(" ");

            for (Member member : members)
                mentions.add(member.getAsMention());

            for (Role role : roles)
                mentions.add(role.getAsMention());

            content = mentions + " you have been slapped by **" + exec + "**!";
        }

        return message
                .setContent(content)
                .setEmbeds(new EmbedBuilder()
                        .setTimestamp(ZonedDateTime.now())
                        .setColor(Colors.NORMAL.getCode())
                        .setImage(ApiCalls.SLAP.get())
                        .setFooter("Powered by nekos.life")
                        .build())
                .denyMentions(Message.MentionType.ROLE)
                .build();
    }
}