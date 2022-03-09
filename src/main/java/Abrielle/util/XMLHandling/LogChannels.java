package Abrielle.util.XMLHandling;

import Abrielle.bot.Abrielle;
import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

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

    public void sendJoinLeaveLog(WebhookEmbed embed, Guild guild) throws JAXBException {
        WebhookClientBuilder builder = new WebhookClientBuilder(this.joinLeaveHook);
        builder.setThreadFactory((job) -> {
            Thread thread = new Thread(job);
            thread.setName("Join leave log");
            thread.setDaemon(true);
            return thread;
        });
        WebhookClient client = builder.build();

        client.send(embed);

        client.close();
    }
}
