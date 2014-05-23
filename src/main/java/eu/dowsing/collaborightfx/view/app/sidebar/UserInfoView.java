package eu.dowsing.collaborightfx.view.app.sidebar;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import org.controlsfx.control.PopOver;

import eu.dowsing.collaborightfx.xmpp.app.ChangeUpdateTextHandler;
import eu.dowsing.collaborightfx.xmpp.app.XmppConnector;

public class UserInfoView {
    private XmppConnector xmpp;

    /* ***
     * Xmpp User Details
     */
    private Text lXConnected = new Text("...");
    private Text lXUser = new Text("User");
    private Text lXHost = new Text("Host");
    private Text lXPort = new Text("Port");
    private Text lXContacts = new Text("Contacts");

    private Button bXUser = new Button("User");

    public UserInfoView(XmppConnector xmpp) {
        this.xmpp = xmpp;

        init();
    }

    private void init() {
        xmpp.getXmppConnectStatus().addListener(new ChangeUpdateTextHandler<>("Connected:", "").setLables(lXConnected));
        xmpp.propertyConnectedHost().addListener(new ChangeUpdateTextHandler<>("Host:", "").setLables(lXHost));
        xmpp.getXmppPort().addListener(new ChangeUpdateTextHandler<>("Port:", "").setLables(lXPort));
        xmpp.getXmppUser().addListener(new ChangeUpdateTextHandler<>("User:", "").setButtons(bXUser).setLables(lXUser));

        Pane userDetails = new VBox();
        userDetails.setStyle("-fx-padding: 10 10 10 10;");

        final PopOver userPop = new PopOver(userDetails);
        userPop.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
        userPop.setDetachable(false);

        bXUser.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                if (userPop.isShowing()) {
                    userPop.hide();
                } else {
                    Point2D l = bXUser.localToScreen(0, 0);
                    userPop.show(bXUser, l.getX() + bXUser.getWidth() / 2, l.getY() + bXUser.getHeight() * 2);
                }
            }
        });
    }

    /**
     * Return the button that opens the User PopOver.
     * 
     * @return
     */
    public Button getPopOverButton() {
        return bXUser;
    }
}
