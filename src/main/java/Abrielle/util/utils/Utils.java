package Abrielle.util.utils;

import Abrielle.util.Exceptions.AbrielleException;
import com.fasterxml.jackson.databind.ser.Serializers;
import net.dv8tion.jda.api.entities.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static @NotNull File getFile(@NotNull String fileName) throws URISyntaxException {
        ClassLoader classLoader = Utils.class.getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        }

        return new File(resource.toURI());
    }

    public static @NotNull String getArgs(String raw, List<String> prefix) {
        String usedPrefix = "";
        int i = 0;

        while (usedPrefix.equals("")) {
            String toFind = "^" + prefix.get(i);
            Pattern pattern = Pattern.compile(toFind, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(raw);
            boolean matchFound = matcher.find();
            if (matchFound) usedPrefix = prefix.get(i);
            i++;
        }

        raw = raw.toLowerCase().replaceFirst(Pattern.quote(usedPrefix), "");
        return raw;
    }

    public static @NotNull DateTimeFormatter format() {
        return new DateTimeFormatterBuilder().appendPattern("dd-M-yyyy hh:mm:ss a").toFormatter();
    }

    public static int getBots(@NotNull Guild guild) {
        AtomicInteger amount = new AtomicInteger();
        List<Member> members = guild.loadMembers().get();

        members.forEach(member -> {
            if (member.getUser().isBot())
                amount.getAndIncrement();
        });

        return amount.get();
    }

    public static int getChannelAmount(@NotNull List<GuildChannel> channels, ChannelType sort) {
        AtomicInteger amount = new AtomicInteger();

        channels.forEach(guildChannel -> {
            guildChannel.getType();
            if (guildChannel.getType() == sort)
                amount.getAndIncrement();
        });

        return amount.get();
    }

    public static @NotNull String getSystemChannel(@NotNull Guild guild) {
        if (guild.getSystemChannel() == null)
            return "-";

        return guild.getSystemChannel().getName();
    }

    public static @NotNull String getAfkChannel(@NotNull Guild guild) {
        if (guild.getAfkChannel() == null)
            return "-";

        return guild.getAfkChannel().getName();
    }

    public static @NotNull String getRegion(@NotNull Guild guild) {
        if (guild.getVoiceChannels().stream().findFirst().isEmpty())
            return "Could not get the region";

        return guild.getVoiceChannels().stream().findFirst().get().getRegion().getName();
    }

    public static @NotNull BaseGuildMessageChannel getChannel(Message msg, Guild guild, String[] args, int pos) throws AbrielleException {
        BaseGuildMessageChannel channel;

        if (msg.getMentionedChannels().isEmpty())
            channel = guild.getTextChannelById(args[0].replaceAll("[^\\d]", ""));
        else
            channel = msg.getMentionedChannels().get(0);

        if (channel == null)
            channel = guild.getNewsChannelById(args[0].replaceAll("[^\\d]", ""));

        if (channel == null)
            throw new AbrielleException("Text channel " + args[0] + " does not exist.");

        return channel;
    }
}
