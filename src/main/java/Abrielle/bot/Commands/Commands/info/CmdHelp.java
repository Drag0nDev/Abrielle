package Abrielle.bot.Commands.Commands.info;

import Abrielle.bot.Abrielle;
import Abrielle.bot.Commands.Command;
import Abrielle.constants.Colors;
import Abrielle.util.utils.AsciiTable;
import com.github.rainestormee.jdacommand.AbstractCommand;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;

@CommandDescription(
        name = "help",
        description = "A command to show all commands or help for a single command/category!",
        triggers = {"help", "h"},
        attributes = {
                @CommandAttribute(key = "category", value = "info"),
                @CommandAttribute(key = "usage", value = "[command | alias] <categoryname/commandname>"),
                @CommandAttribute(key = "examples", value = "`h!help`\n" +
                        "`h!help info`\n" +
                        "`h!help slum`"),
        }
)

public class CmdHelp implements Command {
    private final Abrielle bot;
    private final HashSet<String> categories = new LinkedHashSet<>();

    public CmdHelp(Abrielle bot) {
        this.bot = bot;

        categories.add("info");
        categories.add("reactions");
    }

    @Override
    public void runCommand(Message msg, Guild guild, TextChannel tc, Member member) {
        String[] arguments = bot.getArguments(msg);
        Map<String, ArrayList<String>> cmdMap = new HashMap<>();

        //map the categories and their commands
        for (String cat : categories) {
            cmdMap.put(cat, new ArrayList<>());
            for (AbstractCommand<Message> cmd : bot.getCmdHandler().getCommands()) {
                if (cmd.getAttribute("category").contains(cat)) {
                    cmdMap.get(cat).add(cmd.getDescription().name());
                }
            }
        }

        assert member != null;

        if (arguments.length > 0) {
            Command cmd = (Command) bot.getCmdHandler().findCommand(arguments[0]);

            //check if command exists
            if (cmd == null || isCommand(cmd)) {
                tc.sendMessageEmbeds(showHelpMenu(cmdMap, arguments[0], member.getUser())).queue();
                return;
            }

            //check if it is an owner only command
            if (cmd.getAttribute("category").contains("owner") && member.getUser().getId().equals(this.bot.getConfig().getOwner())) {
                EmbedBuilder embed = new EmbedBuilder().setColor(Colors.ERROR.getCode())
                        .setTitle("Bot owner only command")
                        .setDescription("This command is not available for your use.\n" +
                                "This command can only be used by the bot owner.")
                        .setTimestamp(ZonedDateTime.now());

                tc.sendMessageEmbeds(embed.build()).queue();
                return;
            }

            tc.sendMessageEmbeds(commandHelp(member, guild, cmd)).queue();
        } else {
            tc.sendMessageEmbeds(showHelpMenu(cmdMap, null, member.getUser())).queue();
        }
    }

    @Override
    public void runSlash(Guild guild, TextChannel tc, Member member, SlashCommandInteractionEvent event, InteractionHook hook) {

        Map<String, ArrayList<String>> cmdMap = new HashMap<>();
        String input = event.getOption("command") == null ? "" : Objects.requireNonNull(event.getOption("command")).getAsString();

        //map the categories and their commands
        for (String cat : categories) {
            cmdMap.put(cat, new ArrayList<>());
            for (AbstractCommand<Message> cmd : bot.getCmdHandler().getCommands()) {
                if (cmd.getAttribute("category").contains(cat)) {
                    cmdMap.get(cat).add(cmd.getDescription().name());
                }
            }
        }

        if (!input.equals("")) {
            Command cmd = (Command) bot.getCmdHandler().findCommand(input);

            //check if command exists
            if (cmd == null || isCommand(cmd)) {
                hook.sendMessageEmbeds(showHelpMenu(cmdMap, input, event.getUser())).queue();
                return;
            }

            //check if it is an owner only command
            if (cmd.getAttribute("category").contains("owner")) {
                EmbedBuilder embed = new EmbedBuilder().setColor(Colors.ERROR.getCode())
                        .setTitle("Bot owner only command")
                        .setDescription("This command is not available for your use.\n" +
                                "This command can only be used by the bot owner.")
                        .setTimestamp(ZonedDateTime.now());

                hook.sendMessageEmbeds(embed.build()).queue();
                return;
            }

            hook.sendMessageEmbeds(commandHelp(member, guild, cmd)).queue();
        } else
            hook.sendMessageEmbeds(showHelpMenu(cmdMap, null, event.getUser())).queue();
    }

    private MessageEmbed commandHelp(Member member, Guild guild, Command cmd) {
        EmbedBuilder embed = new EmbedBuilder();
        StringBuilder triggers = new StringBuilder();
        Member self = guild.getSelfMember();

        for (int i = 0; i < cmd.getDescription().triggers().length; i++) {
            triggers.append("`").append(cmd.getDescription().triggers()[i]).append("`").append("\n");
        }

        embed.setTitle(cmd.getDescription().name())
                .setColor(Colors.NORMAL.getCode())
                .addField("Command name:", cmd.getDescription().name(), false)
                .addField("Category", cmd.getAttribute("category"), true)
                .addField("Triggers", triggers.toString(), true)
                .addField("Description", cmd.getDescription().description(), false)
                .addField("Usage", cmd.getAttribute("usage"), true)
                .addField("Examples", cmd.getAttribute("examples"), true)
                .setFooter("Syntax: [] = required, <> = optional")
                .setTimestamp(ZonedDateTime.now());

        if (cmd.getNeededPermissions() != null)
            embed.addField("Permissions:", checkPermissions(member, self, cmd), false);

        return embed.build();
    }

    private MessageEmbed showHelpMenu(Map<String, ArrayList<String>> cmdMap, String category, User user) {
        if (category == null) {
            //show all commands
            EmbedBuilder embed = new EmbedBuilder().setTitle("help")
                    .setColor(Colors.NORMAL.getCode())
                    .setTimestamp(ZonedDateTime.now());

            if (!user.getId().equals(this.bot.getConfig().getOwner()))
                cmdMap.remove("owner");

            //sort the categories that act as keys
            ArrayList<String> sortedKeys = new ArrayList<>(cmdMap.keySet());
            Collections.sort(sortedKeys);

            for (String cat : sortedKeys) {
                StringBuilder cmdStr = new StringBuilder();
                ArrayList<String> cmdArray = cmdMap.get(cat);

                for (String cmd : cmdArray) {
                    cmdStr.append(cmd).append("\n");
                }

                embed.addField(cat, cmdStr.toString(), true);

                if (embed.getFields().size() >= 10) {
                    break;
                }

            }

            return embed.build();
        } else {
            if (cmdMap.containsKey(category)) {
                if (!category.equals("owner") && !user.getId().equals(this.bot.getConfig().getOwner())) {
                    ArrayList<String> cmds = cmdMap.get(category);

                    EmbedBuilder embed = new EmbedBuilder().setColor(Colors.NORMAL.getCode())
                            .setTitle("Category: " + category)
                            .setTimestamp(ZonedDateTime.now());

                    for (String cmdStr : cmds) {
                        Command cmd = (Command) bot.getCmdHandler().findCommand(cmdStr);

                        embed.addField(cmd.getDescription().name(), cmd.getDescription().description(), false);
                    }

                    return embed.build();
                } else {
                    return new EmbedBuilder().setColor(Colors.ERROR.getCode())
                            .setTitle("Bot owner only command")
                            .setDescription("This command is not available for your use.\n" +
                                    "This command can only be used by the bot owner.")
                            .setTimestamp(ZonedDateTime.now())
                            .build();
                }
            } else {
                return new EmbedBuilder().setColor(Colors.ERROR.getCode())
                        .setTitle("No command found")
                        .setDescription("No information is found for command/category **" + category.toLowerCase() + "**")
                        .setTimestamp(ZonedDateTime.now())
                        .setFooter("Maybe you typed it wrong?").build();
            }
        }
    }

    private boolean isCommand(Command cmd) {
        return cmd.getDescription() == null && !cmd.hasAttribute("description");
    }

    private String checkPermissions(Member member, Member self, Command cmd) {
        StringBuilder permissionsCheck = new StringBuilder();
        AsciiTable table = new AsciiTable();
        table.setMaxColumnWidth(50);
        table.getColumns().add(new AsciiTable.Column("Permission"));
        table.getColumns().add(new AsciiTable.Column("Bot"));
        table.getColumns().add(new AsciiTable.Column("User"));

        for (Permission permission : cmd.getNeededPermissions()) {
            String botCheck;
            String memberCheck;

            //check bot
            if (self.hasPermission(permission)) {
                botCheck = "✅";
            } else {
                botCheck = "❌";
            }

            //check member
            if (member.hasPermission(permission)) {
                memberCheck = "✅";
            } else {
                memberCheck = "❌";
            }

            AsciiTable.Row row = new AsciiTable.Row();
            table.getData().add(row);
            row.getValues().add(permission.getName());
            row.getValues().add(botCheck);
            row.getValues().add(memberCheck);
        }

        table.calculateColumnWidth();
        System.out.println(table.render());

        return permissionsCheck.append("`").append(table.render()).append("`").toString();
    }
}