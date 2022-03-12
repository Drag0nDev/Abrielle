package Abrielle.bot;

import Abrielle.bot.Commands.CommandListener;
import Abrielle.bot.Commands.CommandLoader;
import Abrielle.bot.Events.ChannelEvents;
import Abrielle.bot.Events.JoinLeaveEvents;
import Abrielle.bot.Events.Listener;
import Abrielle.util.utils.Config;
import com.github.rainestormee.jdacommand.CommandHandler;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.menu.EmbedPaginator;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static Abrielle.util.utils.Utils.getArgs;

public class Abrielle {
    private final Config config = new Config();
    private static final Logger logger = LoggerFactory.getLogger(Listener.class);

    private static JDA bot = null;
    private static final String version = "2.0.0";

    private final CommandLoader commandLoader = new CommandLoader(this);
    private final CommandHandler<Message> CMD_HANDLER = new CommandHandler<>();
    private final EventWaiter waiter = new EventWaiter();

    public Abrielle() throws IOException, ParseException, URISyntaxException {
    }

    public static void main(String[] arguments) {
        try {
            new Abrielle().setup();
        } catch (Exception ex) {
            logger.error("Couldn't login to Discord!", ex);
        }
    }

    private void setup() throws LoginException {
        CMD_HANDLER.registerCommands(new HashSet<>(commandLoader.getCommands()));

        bot = JDABuilder
                .create(config.getToken(), GatewayIntent.GUILD_MEMBERS)
                .enableIntents(
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_BANS,
                        GatewayIntent.GUILD_EMOJIS,
                        GatewayIntent.GUILD_VOICE_STATES,
                        GatewayIntent.GUILD_INVITES,
                        GatewayIntent.GUILD_WEBHOOKS,
                        GatewayIntent.GUILD_MESSAGE_REACTIONS
                )
                .disableCache(
                        CacheFlag.EMOTE,
                        CacheFlag.VOICE_STATE,
                        CacheFlag.CLIENT_STATUS,
                        CacheFlag.ACTIVITY,
                        CacheFlag.ONLINE_STATUS,
                        CacheFlag.MEMBER_OVERRIDES,
                        CacheFlag.ROLE_TAGS
                )
                .addEventListeners(
                        new CommandListener(this, CMD_HANDLER),
                        new JoinLeaveEvents(),
                        new ChannelEvents(),
                        new Listener(),
                        waiter
                )
                .setActivity(Activity.watching("over all senpai's"))
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .build();

        //commandLoader.loadSlashCommands();
    }

    public static JDA getBot() {
        return bot;
    }

    public Config getConfig() {
        return config;
    }

    public static Logger getLogger() {
        return logger;
    }

    public CommandHandler<Message> getCmdHandler() {
        return CMD_HANDLER;
    }

    public String[] getArguments(@NotNull Message msg) {
        //find the used prefix
        String raw = msg.getContentRaw();
        List<String> prefix = this.getConfig().getPrefix();
        raw = getArgs(raw, prefix);
        String[] split = raw.split("\\s+");

        return Arrays.copyOfRange(split, 1, split.length);
    }

    public Abrielle getAbrielle() {
        return this;
    }

    public String getVersion() {
        return version;
    }

    public EmbedPaginator.Builder getPaginator(Member member, TextChannel tc) {
        EmbedPaginator.Builder builder = new EmbedPaginator.Builder()
                .setTimeout(1, TimeUnit.MINUTES)
                .setEventWaiter(waiter)
                .waitOnSinglePage(true)
                .setText(EmbedBuilder.ZERO_WIDTH_SPACE)
                .wrapPageEnds(true)
                .addUsers(member.getUser())
                .setFinalAction(message -> {
                    if(tc.getGuild().getSelfMember().hasPermission(tc, Permission.MESSAGE_MANAGE))
                        message.clearReactions().queue(
                                null,
                                e -> logger.warn("Couldn't clear reactions from message.")
                        );
                });

        if(tc.getGuild().getOwner() != null)
            builder.addUsers(tc.getGuild().getOwner().getUser());

        return builder;
    }
}
