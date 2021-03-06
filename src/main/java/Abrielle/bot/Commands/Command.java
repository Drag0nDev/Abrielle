package Abrielle.bot.Commands;

import com.github.rainestormee.jdacommand.AbstractCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.ArrayList;

public interface Command extends AbstractCommand<Message> {

    default void runSlash(Guild guild, TextChannel tc, Member member, SlashCommandInteractionEvent event, InteractionHook hook) throws Exception {};

    @Override
    default void execute(Message object, Object... args){} //This code is useless for Custom error messages

    default String[] getOptionNames() {
        return null;
    }

    default ArrayList<Permission> getNeededPermissions() {
        return null;
    }

    default int getCooldown() {
        return 0;
    }

    void runCommand(Message msg, Guild guild, TextChannel tc, Member member) throws Exception;
}