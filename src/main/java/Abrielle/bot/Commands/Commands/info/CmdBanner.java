package Abrielle.bot.Commands.Commands.info;

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

@CommandDescription(
        name = "banner",
        description = "Show someones global banner",
        triggers = {"banner"},
        attributes = {
                @CommandAttribute(key = "category", value = "info"),
                @CommandAttribute(key = "usage", value = "[command | alias] <mention/id>"),
                @CommandAttribute(key = "examples", value = "'a!banner'\n" +
                        "'a!banner 418037700751261708'\n" +
                        "'a!banner @Drag0n#6666'"
                ),
        }
)

public record CmdBanner(Abrielle bot) implements Command {

    @Override
    public void runSlash(Guild guild, TextChannel tc, Member member, SlashCommandInteractionEvent event, InteractionHook hook) throws Exception {
        Member target;

        if (event.getOption("user") == null)
            target = member;
        else
            target = Objects.requireNonNull(event.getOption("user")).getAsMember();

        if (target == null)
            return;

        hook.sendMessageEmbeds(banner(target)).queue();
    }

    @Override
    public void runCommand(Message msg, Guild guild, TextChannel tc, Member member) throws Exception {
        String[] args = bot.getArguments(msg);
        Member target;

        if (args.length == 0)
            target = member;
        else {
            if (!msg.getMentionedMembers().isEmpty())
                target = msg.getMentionedMembers().get(0);
            else
                target = guild.retrieveMemberById(args[0]).complete();
        }

        tc.sendMessageEmbeds(banner(target)).queue();
    }

    private MessageEmbed banner(Member target) {
        String bannerUrl = target.getUser().retrieveProfile().complete().getBannerUrl();

        if (bannerUrl == null)
            return new EmbedBuilder()
                    .setColor(Colors.NORMAL.getCode())
                    .setTitle("Banner of: " + target.getUser().getAsTag())
                    .setDescription("**" + target.getUser().getAsTag() + "** doesn't have a banner!")
                    .setTimestamp(ZonedDateTime.now())
                    .build();
        
        return new EmbedBuilder()
                .setColor(Colors.NORMAL.getCode())
                .setTitle("Banner of: " + target.getUser().getAsTag())
                .setImage(target.getUser().retrieveProfile().complete().getBannerUrl() + "?size=4096")
                .setTimestamp(ZonedDateTime.now())
                .build();
    }
}