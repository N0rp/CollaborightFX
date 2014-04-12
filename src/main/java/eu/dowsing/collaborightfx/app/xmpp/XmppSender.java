package eu.dowsing.collaborightfx.app.xmpp;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.StringUtils;

import eu.dowsing.collaborightfx.sketch.structure.Shape;
import eu.dowsing.collaborightfx.sketch.transaction.StructureUpdate;

public class XmppSender {

    private XmppReceiver receiver;
    private XmppHistory history;

    public XmppSender(XmppReceiver receiver, XmppHistory history) {
        this.receiver = receiver;
        this.history = history;
    }

    public void sendMessage(String message, String jid) throws XMPPException {

        System.out.println(String.format("Sending mesage '%1$s' to user %2$s", message, jid));
        jid = getJID(jid);

        Chat chat = receiver.getChat(jid);
        Message msg = new Message(jid, Message.Type.chat);
        msg.setBody(message);
        chat.sendMessage(msg);

        history.addMessage2ContactHistory(jid, msg);
    }

    private static long testStructureId = 0;

    public void sendSketchUpdate(String jid, Shape shape) throws XMPPException {
        Chat chat = receiver.getChat(jid);
        Message msg = new Message(jid, Message.Type.chat);
        StructureUpdate transaction = new StructureUpdate(testStructureId++, shape);
        msg.addExtension(transaction);
        chat.sendMessage(msg);
        // conn.sendPacket(msg);
    }

    private String getJID(String url) {
        return StringUtils.parseBareAddress(url);
    }

}
