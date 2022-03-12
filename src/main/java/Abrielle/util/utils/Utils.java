package Abrielle.util.utils;

import Abrielle.util.Exceptions.AbrielleException;
import com.google.gson.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.EnumSet;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static @NotNull InputStream getFile(@NotNull String fileName) {
        ClassLoader classLoader = Utils.class.getClassLoader();
        InputStream resource = classLoader.getResourceAsStream(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        }

        return resource;
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

        raw = raw.replaceFirst(Pattern.quote(usedPrefix), "");
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

    public static @NotNull BaseGuildMessageChannel getChannel(Message msg, Guild guild, String[] args) throws AbrielleException {
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

    public static @NotNull MessageEmbed jsonToEmbed(String str) {
        JsonObject json = stringToJsonEmbed(str);

        return jsonToEmbed(json);
    }

    private static JsonObject stringToJsonEmbed(String str) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonElement jsonElement = gson.fromJson(str, JsonElement.class);
        return jsonElement.getAsJsonObject();
    }

    public static @NotNull MessageEmbed jsonToEmbed(@NotNull JsonObject json) {
        EmbedBuilder embedBuilder = new EmbedBuilder();

        JsonPrimitive titleObj = json.getAsJsonPrimitive("title");
        if (titleObj != null) // Make sure the object is not null before adding it onto the embed.
            embedBuilder.setTitle(titleObj.getAsString());


        JsonObject authorObj = json.getAsJsonObject("author");
        if (authorObj != null) {
            String authorName = authorObj.get("name").getAsString();
            String authorIconUrl = authorObj.get("icon_url").getAsString();
            if (authorIconUrl != null) // Make sure the icon_url is not null before adding it onto the embed. If its null then add just the author's name.
                embedBuilder.setAuthor(authorName, authorIconUrl);
            else
                embedBuilder.setAuthor(authorName);
        }

        JsonPrimitive descObj = json.getAsJsonPrimitive("description");
        if (descObj != null)
            embedBuilder.setDescription(descObj.getAsString());


        JsonPrimitive colorObj = json.getAsJsonPrimitive("color");
        if (colorObj != null) {
            String colorStr = colorObj.getAsString().replace("#", "0x");
            Color color = new Color(Integer.decode(colorStr));
            embedBuilder.setColor(color);
        }

        JsonArray fieldsArray = json.getAsJsonArray("fields");
        if (fieldsArray != null) {
            // Loop over the fields array and add each one by order to the embed.
            fieldsArray.forEach(ele -> {
                boolean inline;
                String name = ele.getAsJsonObject().get("name").getAsString();
                String content = ele.getAsJsonObject().get("value").getAsString();
                if (ele.getAsJsonObject().get("inline") != null)
                    inline = ele.getAsJsonObject().get("inline").getAsBoolean();
                else
                    inline = false;
                embedBuilder.addField(name, content, inline);
            });
        }

        JsonPrimitive thumbnailObj = json.getAsJsonPrimitive("thumbnail");
        if (thumbnailObj != null) {
            embedBuilder.setThumbnail(thumbnailObj.getAsString());
        }

        JsonObject footerObj = json.getAsJsonObject("footer");
        if (footerObj != null) {
            String content = footerObj.get("text").getAsString();
            String footerIconUrl = footerObj.get("icon_url").getAsString();

            if (footerIconUrl != null)
                embedBuilder.setFooter(content, footerIconUrl);
            else
                embedBuilder.setFooter(content);
        }

        return embedBuilder.build();
    }

    public static String capitalize(String str) {
        if (str.length() == 0) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    public static String getPerms(EnumSet<Permission> allowed, EnumSet<Permission> denied) {
        if (allowed.isEmpty() && denied.isEmpty())
            return "";

        StringJoiner perms = new StringJoiner("\n");

        for (Permission perm : allowed) {
            perms.add(perm.getName() + " ✅");
        }

        for (Permission perm : denied) {
            perms.add(perm.getName() + " ❌");
        }

        return perms.toString();
    }
}
