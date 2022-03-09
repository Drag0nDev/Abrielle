package Abrielle.bot.Commands.Commands.owner;

import Abrielle.bot.Abrielle;
import Abrielle.bot.Commands.Command;
import Abrielle.constants.Colors;
import Abrielle.util.XMLHandling.LogChannels;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.managers.WebhookManager;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static Abrielle.util.XMLHandling.XMLHandler.*;

@CommandDescription(
        name = "test",
        description = "test",
        triggers = {"test"},
        attributes = {
                @CommandAttribute(key = "category", value = "owner"),
                @CommandAttribute(key = "usage", value = "[command | alias]"),
                @CommandAttribute(key = "examples", value = "test"),
        }
)

public record CmdTest(Abrielle bot) implements Command {
    @Override
    public void runCommand(Message msg, Guild guild, TextChannel tc, Member member) throws Exception {

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