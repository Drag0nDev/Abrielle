package Abrielle.util.XMLHandling;

import Abrielle.bot.Abrielle;
import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

import java.io.File;

public class XMLHandler {
    public static void writeToXml(LogChannels logs, Guild guild) throws JAXBException {
        JAXBContext jaxbContext = null;
        
        jaxbContext = org.eclipse.persistence.jaxb.JAXBContextFactory.createContext(new Class[]{LogChannels.class}, null);

        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        jaxbMarshaller.marshal(logs, new File("logFiles/logs_" + guild.getId() + ".xml"));
    }

    public static LogChannels getLogChannels(Guild guild) throws JAXBException {
        JAXBContext jaxbContext = null;

        jaxbContext = org.eclipse.persistence.jaxb.JAXBContextFactory
                .createContext(new Class[]{LogChannels.class}, null);

        File file = new File("logFiles/logs_" + guild.getId() + ".xml");

        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        return (LogChannels) jaxbUnmarshaller.unmarshal(file);
    }
}
