package Abrielle.bot.Commands.Commands.fun;

import Abrielle.bot.Abrielle;
import Abrielle.bot.Commands.Command;
import Abrielle.constants.Colors;
import Abrielle.util.Exceptions.AbrielleException;
import com.github.rainestormee.jdacommand.CommandAttribute;
import com.github.rainestormee.jdacommand.CommandDescription;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.w3c.dom.Text;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.Buffer;
import java.time.LocalDateTime;
import java.util.Random;

import static Abrielle.util.utils.Utils.getFile;

@CommandDescription(
        name = "ship",
        description = "Ship 2 people",
        triggers = {"ship"},
        attributes = {
                @CommandAttribute(key = "category", value = "fun"),
                @CommandAttribute(key = "usage", value = "[command | alias] [mention/id] [mention/id]"),
                @CommandAttribute(key = "examples", value = "a!ship @yana @seem"),
        }
)

public record CmdShip(Abrielle bot) implements Command {
    @Override
    public void runCommand(Message msg, Guild guild, TextChannel tc, Member member) throws Exception {
        String[] arguments = bot.getArguments(msg);
        Member member1 = null;
        Member member2 = null;

        try {
            if (arguments.length == 1) {
                member1 = member;
                if (msg.getMentionedMembers().isEmpty())
                    member2 = guild.retrieveMemberById(arguments[0]).complete();
                else if (msg.getMentionedMembers().size() == 1)
                    member2 = msg.getMentionedMembers().get(0);
                else
                    throw new AbrielleException("Please provide a valid id/mention.\n Roles are not allowed as mention!");
            } else if (arguments.length == 2) {
                member1 = guild.retrieveMemberById(arguments[0].replaceAll("[^[0-9]]", "")).complete();
                member2 = guild.retrieveMemberById(arguments[1].replaceAll("[^[0-9]]", "")).complete();
            } else {
                throw new AbrielleException("Please mention user(s) to ship with!");
            }
        } catch (NumberFormatException | ErrorResponseException e) {
            throw new AbrielleException("Please use id or mentions on this command!\n**Role mentions are not allowed!**");
        }


        ship(member1, member2, tc);
    }

    private void ship(Member member1, Member member2, TextChannel tc) throws IOException, URISyntaxException {
        File file = new File("card.png");
        Random rand = new Random();
        int percent = rand.nextInt(100);
        String outsight;

        if (percent < 25)
            outsight = "Bad!";
        else if (percent < 50)
            outsight = "Poor!";
        else if (percent < 75)
            outsight = "Questionable!";
        else
            outsight = "Set for life!";

        makeCard(member1, member2, file, percent);
        Message msg = new MessageBuilder().setEmbeds(
                new EmbedBuilder()
                        .setTitle(member1.getUser().getAsTag() + " x " + member2.getUser().getAsTag())
                        .setDescription("This ship is " + percent + "% compatible!")
                        .addField("The ship is:", outsight, false)
                        .setImage("attachment://card.png")
                        .setColor(Colors.NORMAL.getCode())
                        .build()
        ).build();

        tc.sendMessage(msg).addFile(file, "card.png").queue();

        if (!file.delete())
            Abrielle.getLogger().error("File " + file.getName() + " failed to delete.\n" +
                    "Path: " + file.getAbsolutePath());
    }

    private void makeCard(Member member1, Member member2, File file, Integer percent) throws IOException, URISyntaxException {
        BufferedImage out = new BufferedImage(260, 130, BufferedImage.TYPE_INT_ARGB);
        URL link1 = new URL(member1.getUser().getEffectiveAvatarUrl());
        URL link2 = new URL(member2.getUser().getEffectiveAvatarUrl());
        BufferedImage hrt;

        if (percent > 50)
            hrt = ImageIO.read(getFile("Emojis/heart.png"));
        else
            hrt = ImageIO.read(getFile("Emojis/broken.png"));

        BufferedImage av1 = ImageIO.read(link1);
        BufferedImage av2 = ImageIO.read(link2);

        // get the types
        int av1Type = av1.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : av1.getType();
        int av2Type = av2.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : av2.getType();

        // resizing the images
        BufferedImage avatar1 = resizeImage(av1, av1Type, 130, 130);
        BufferedImage avatar2 = resizeImage(av2, av2Type, 130, 130);
        BufferedImage heart = resizeImage(hrt, 2, 50, 50);
        BufferedImage circleAvatar1 = cropCicle(avatar1);
        BufferedImage circleAvatar2 = cropCicle(avatar2);

        // starting the drawing
        Graphics g = out.createGraphics();

        // input background and profile picture
        g.drawImage(circleAvatar1, 0, 0, null);
        g.drawImage(circleAvatar2, 130, 0, null);
        g.drawImage(heart, 105, 40, null);

        ImageIO.write(out, "png", file);
        g.dispose();
    }

    private static BufferedImage resizeImage(BufferedImage originalImage, int type, Integer img_width, Integer img_height) {
        BufferedImage resizedImage = new BufferedImage(img_width, img_height, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, img_width, img_height, null);
        g.dispose();

        return resizedImage;
    }

    private static BufferedImage cropCicle(BufferedImage avatar) {
        int width = avatar.getWidth();
        BufferedImage circleBuffer = new BufferedImage(width, width, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = circleBuffer.createGraphics();
        g2.setClip(new Ellipse2D.Float(0, 0, width, width));
        g2.drawImage(avatar, 0, 0, width, width, null);

        return circleBuffer;
    }
}
