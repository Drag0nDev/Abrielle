package Abrielle.util.XMLHandling;

import Abrielle.bot.Abrielle;
import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.exception.HttpException;
import club.minnced.discord.webhook.send.WebhookEmbed;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.slf4j.Logger;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class LogChannels {
    @XmlElement(name = "join-leave")
    String joinLeaveHook;

    @XmlElement(name = "member")
    String memberHook;

    @XmlElement(name = "message")
    String messageHook;

    @XmlElement(name = "server")
    String serverHook;

    @XmlElement(name = "voice")
    String voiceHook;

    public String getJoinLeaveHook() {
        return joinLeaveHook;
    }

    public void setJoinLeaveHook(String joinLeaveHook) {
        this.joinLeaveHook = joinLeaveHook;
    }

    public String getMemberHook() {
        return memberHook;
    }

    public void setMemberHook(String memberHook) {
        this.memberHook = memberHook;
    }

    public String getMessageHook() {
        return messageHook;
    }

    public void setMessageHook(String messageHook) {
        this.messageHook = messageHook;
    }

    public String getServerHook() {
        return serverHook;
    }

    public void setServerHook(String serverHook) {
        this.serverHook = serverHook;
    }

    public String getVoiceHook() {
        return voiceHook;
    }

    public void setVoiceHook(String voiceHook) {
        this.voiceHook = voiceHook;
    }

    public void sendJoinLeaveLog(WebhookEmbed embed) throws JAXBException {
        Logger LOGGER = Abrielle.getLogger();
        WebhookClient client = createClient(this.joinLeaveHook);
        sendEmbed(embed, LOGGER, client);
    }

    public void sendServerLog(WebhookEmbed embed) throws JAXBException {
        Logger LOGGER = Abrielle.getLogger();
        WebhookClient client = createClient(this.serverHook);
        sendEmbed(embed, LOGGER, client);
    }

    private void sendEmbed(WebhookEmbed embed, Logger LOGGER, WebhookClient client) {
        WebhookClient.setDefaultErrorHandler((wClient, msg, throwable) -> {
            if (throwable != null)
                LOGGER.error(throwable.getMessage());

            if (throwable instanceof HttpException ex && ex.getCode() == 404)
                client.close();
        });
        client.send(embed);
        client.close();
    }

    private WebhookClient createClient(String url) {
        WebhookClientBuilder builder = new WebhookClientBuilder(url);
        builder.setThreadFactory((job) -> {
            Thread thread = new Thread(job);
            thread.setName("log");
            thread.setDaemon(true);
            return thread;
        });

        return builder.build();
    }
}
