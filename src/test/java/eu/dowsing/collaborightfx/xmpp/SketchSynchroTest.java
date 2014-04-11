package eu.dowsing.collaborightfx.xmpp;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javafx.application.Application;

import org.jivesoftware.smack.XMPPException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import eu.dowsing.collaborightfx.app.TestGrid;
import eu.dowsing.collaborightfx.app.xmpp.XmppConnector;
import eu.dowsing.collaborightfx.app.xmpp.XmppConnector.ConnectStatus;

/**
 * Tests that the sketch synchronizes over the network.
 * 
 * @author richardg
 * 
 */
public class SketchSynchroTest {

    private static final String SERVER_PROP = "src/test/resources/prop/server.properties";
    private static final String CLIENT_PROP = "src/test/resources/prop/client.properties";

    private static final String HOST = "host";
    private static final String PORT = "port";
    private static final String USER = "user";
    private static final String PW = "pw";

    private static XmppConnector server;
    private static XmppConnector client;

    private static String serverJid;
    private static String clientJid;

    /**
     * This method is executed once, before the start of all tests.
     * 
     * @throws IOException
     */
    @BeforeClass
    public static void testSetupAndLogin() throws IOException {
        // load properties
        System.out.println("-----------BeforeClass---------------");
        Properties serverProps = new Properties();
        {
            FileInputStream in = new FileInputStream(SERVER_PROP);
            serverProps.load(in);
            in.close();
        }

        Properties clientProps = new Properties();
        {
            FileInputStream in = new FileInputStream(CLIENT_PROP);
            clientProps.load(in);
            in.close();
        }

        System.out.println("Host Server is " + serverProps.getProperty(HOST));

        server = new XmppConnector();
        client = new XmppConnector();

        server.setConnectionData(serverProps.getProperty(HOST), Integer.parseInt(serverProps.getProperty(PORT)),
                serverProps.getProperty(USER), serverProps.getProperty(PW));
        client.setConnectionData(clientProps.getProperty(HOST), Integer.parseInt(clientProps.getProperty(PORT)),
                clientProps.getProperty(USER), clientProps.getProperty(PW));

        serverJid = serverProps.getProperty(USER) + "@" + serverProps.getProperty(HOST);
        clientJid = clientProps.getProperty(USER) + "@" + clientProps.getProperty(HOST);

        // Assert.assertEquals(ConnectStatus.LOGGED_IN, server.connectAndLoginSync());
        Assert.assertEquals(ConnectStatus.LOGGED_IN, client.connectAndLoginSync());

        // server.setSelectedContact(clientJid);
        client.getHistory().setSelectedContact(serverJid);

        Application.launch(TestGrid.class);
    }

    @AfterClass
    public static void testCleanupAndDisconnect() {
        System.out.println("-----------AfterClass---------------");
        // Teardown for data used by the unit tests
        // server.disconnect();
        client.disconnect();
    }

    @Test(groups = { "send" })
    public void testSimpleMessage() throws XMPPException {
        System.out.println("Server user is: " + server.getXmppUser());
        System.out.println("Client user is: " + client.propertyConnectedHost());

        client.getSender().sendMessage("Test", clientJid);
        // Shape shape1 = new Shape(5, 5, 12, null);
        // shape1.addPoint(10, 20, true, null);
        // Shape shape2 = new Shape(1, 1, 1, null);
        // shape2.addPoint(4, 4, false, null);
        // shape2.addPoint(4, 5, true, null);
        //
        // List<Shape> shapes = new LinkedList<>();
        // shapes.add(shape1);
        // shapes.add(shape2);
        // Sketch sketch = new Sketch(shapes);
        // server.se
        // server.connectAndLoginSync();
    }

    @Test(timeOut = 10000, dependsOnGroups = { "send" })
    public void receiveSimpleMessage() throws XMPPException {
        // client.getXmppSelectedContactChat().addListener(new ListChangeListener<Message>() {
        //
        // @Override
        // public void onChanged(ListChangeListener.Change<? extends Message> message) {
        // System.out.println("Received message from: " + message.getFrom());
        // }
        // });
    }

}
