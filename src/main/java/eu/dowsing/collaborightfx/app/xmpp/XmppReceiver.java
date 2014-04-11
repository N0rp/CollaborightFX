package eu.dowsing.collaborightfx.app.xmpp;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.DefaultPacketExtension;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Message.Type;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.util.StringUtils;
import org.xmlpull.v1.XmlPullParser;

import eu.dowsing.collaborightfx.sketch.structure.Shape;
import eu.dowsing.collaborightfx.sketch.transaction.StructureUpdate;

public class XmppReceiver implements ChatManagerListener, MessageListener, PacketExtensionProvider {

    /** List of chat for each user. **/
    private Map<String, Chat> user2Chat = new HashMap<>();

    private XmppHistory history;
    private ChatManager chatManager;

    private List<OnStructureUpdateListener> constructListener = new LinkedList<>();

    public void addOnConstructUpdateListener(OnStructureUpdateListener listener) {
        this.constructListener.add(listener);
    }

    private void notifyOnConstructUpdateListener(Shape shape) {
        for (OnStructureUpdateListener listener : this.constructListener) {
            listener.onConstructUpdate(shape);
        }
    }

    public void setData(XmppHistory history, ChatManager manager) {
        this.history = history;

        chatManager.addChatListener(this);
        ProviderManager.getInstance().addExtensionProvider(StructureUpdate.NAME, StructureUpdate.NS, this);
    }

    public Chat getChat(String jid) {
        if (!user2Chat.containsKey(jid)) {
            user2Chat.put(jid, chatManager.createChat(jid, this));
        }
        return user2Chat.get(jid);
    }

    @Override
    public void chatCreated(Chat chat, boolean createdLocally) {

        String jid = getJID(chat.getParticipant());
        System.out.println("ChatListener: Chat was created with: " + jid);

        if (!createdLocally) {
            // add message listener
            chat.addMessageListener(this);
        }
    }

    private String getJID(String url) {
        return StringUtils.parseBareAddress(url);
    }

    @Override
    public void processMessage(Chat chat, Message message) {
        String from = message.getFrom();
        String body = message.getBody();
        int bodyCount = message.getBodies().size();
        // body can very well be empty, f.ex. the 'user-is-typing' message is null
        Type type = message.getType();

        if (message != null) {

            PacketExtension ext = message.getExtension("structureUpdate", "http://www.dowsing.eu/collaboright");

            if (ext != null) {
                System.out.println("Xmpp: Received a structure update extension of class " + ext.getClass());
                if (ext instanceof DefaultPacketExtension) {
                    DefaultPacketExtension def = (DefaultPacketExtension) ext;
                    System.out.println("Xmpp: Default extension names are " + def.getNames());
                    System.out.println("Xmpp: Default extension content is " + def.toXML());
                    System.out.println("Xmpp: Default extension value is " + def.getValue("transaction"));
                    System.out.println("Xmpp: Default extension test value is " + def.getValue("test"));
                    try {
                        StructureUpdate update = StructureUpdate.fromXml(def.getValue("transaction"));
                        notifyOnConstructUpdateListener(update.getConstruct());
                        System.out.println("Struture update for id " + update.getStructureId());
                    } catch (Exception e) {
                        System.err.println("Could not transform extension into structure update");
                        e.printStackTrace();
                    }
                } else if (ext instanceof StructureUpdate) {
                    StructureUpdate def = (StructureUpdate) ext;
                    System.out.println("Xmpp: Structure update content is " + def.toXML());
                    try {
                        StructureUpdate update = StructureUpdate.fromExtension(ext);
                        notifyOnConstructUpdateListener(update.getConstruct());
                        System.out.println("Struture update for id " + update.getStructureId());
                    } catch (Exception e) {
                        System.err.println("Could not transform extension into structure update");
                        e.printStackTrace();
                    }
                }
                // StructureUpdate update = (StructureUpdate) ext;
                // System.out.println("MyMessageListener: 1/2 Received a structure update for structure id: "
                // + update.getStructureId());
                // try {
                // update.updateFromXml();
                // } catch (Exception e) {
                // System.err.println("MyMessageListener: Could not update from xml because");
                // e.printStackTrace();
                // }
                // System.out.println("MyMessageListener: 2/2 Received a structure update for structure id: "
                // + update.getStructureId());
            }

            if (type == Message.Type.chat && bodyCount >= 1) {
                String fromJid = getJID(from);

                System.out
                        .println(String
                                .format("MyMessageListener: Received message '%1$s' from %2$s and JID %3$s with type %4$s and bodyCount %5$s",
                                        body, from, fromJid, type, bodyCount));
                history.addMessage2ContactHistory(fromJid, message);
            } else {
                System.out.println("MyMessageListener: Received extensioncount " + message.getExtensions().size());
                for (PacketExtension extension : message.getExtensions()) {
                    System.out.println("  Extension element " + extension.getElementName() + " and namespace "
                            + extension.getNamespace());
                }

                Object tmp = message.getExtension(MyExtension.NAME, MyExtension.NS);
                if (tmp != null) {
                    MyExtension transaction = (MyExtension) tmp;
                    System.out.println("Extension received with value " + transaction.getValue("foo"));
                }
            }
        }
    }

    private class MyExtension extends DefaultPacketExtension {

        public static final String NAME = "Sketch";
        public static final String NS = "eu.dowsing.collaboright";

        public MyExtension() {
            super(NAME, NS);
        }

    }

    @Override
    public PacketExtension parseExtension(XmlPullParser parser) throws Exception {
        System.out.println("Parsing exception, text is: " + parser.getText());
        parser.getText();
        DefaultPacketExtension tmp = new DefaultPacketExtension(StructureUpdate.NAME, StructureUpdate.NS);
        tmp.setValue("works?", "works!");
        return tmp;
    }
}
