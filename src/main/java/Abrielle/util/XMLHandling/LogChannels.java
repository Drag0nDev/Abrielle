package Abrielle.util.XMLHandling;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

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

    @Override
    public String toString() {
        return "LogChannels{" +
                "joinLeaveHook='" + joinLeaveHook + '\'' +
                ", memberHook='" + memberHook + '\'' +
                ", messageHook='" + messageHook + '\'' +
                ", serverHook='" + serverHook + '\'' +
                ", voiceHook='" + voiceHook + '\'' +
                '}';
    }
}
