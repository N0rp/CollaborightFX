package eu.dowsing.collaborightfx.app;

import java.io.IOException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javafx.application.Application;
import eu.dowsing.collaborightfx.preferences.PreferenceLoader;
import eu.dowsing.collaborightfx.preferences.PreferenceWrapper;

/**
 * Hello world!
 * 
 */
public class App {

    private static String array2String(Object[] array) {
        String str = "";
        for (Object o : array) {
            if (!str.isEmpty()) {
                str += ", ";
            }
            str += o;
        }

        return "[" + str + "]";
    }

    public static void main(String[] args) throws BackingStoreException {
        // TODO disconnect from xmpp??? do we need to, how would we do that?

        final int MAX_ARGS = 5;

        if (!(args.length == 0 || args.length == MAX_ARGS)) {
            System.err.println("Argument length is " + args.length);
            System.err.println("Arguments must be exactly 0 or 5 and contain: Xmpp host, port, user, password "
                    + "and a boolean flag if auto-connect to xmpp");
            System.err.println("Arguments are instead: " + array2String(args));

            System.exit(-1);
        }

        PreferenceLoader loader = PreferenceLoader.getInstance();
        if (args.length == MAX_ARGS) {
            Preferences temp = getArgsPreferences(args);
            loader.setCurrentPreferences("temp", temp);
            System.out.println("Using temporary Preferences");
        } else {
            System.out.println("Loading Preferences");
            String mainPrefName = "Main.pref";
            if (!loader.setCurrentPreferences(mainPrefName)) {
                System.err.println("Could not load " + mainPrefName + " ; Known preferences are "
                        + loader.propertyPreferenceFileList());
            }
        }

        Preferences p = loader.getCurrentPreferences();
        if (p == null) {
            System.err.println("Could not find any preferences");
            System.exit(-1);
        }

        // PreferenceWrapper.printPreference(p);

        try {
            if (args.length == 0) {
                // save changes to the loaded preferences in case new default values were added, so they show up on the
                // file system
                loader.saveCurrentPreferences();
            }
        } catch (IOException | BackingStoreException e) {
            System.err.println("Could not save preferences");
            e.printStackTrace();
        }

        System.out.println("Launching App");
        Application.launch(TestGrid.class);
    }

    private static Preferences getArgsPreferences(String[] args) throws BackingStoreException {
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String user = args[2];
        String pw = args[3];
        boolean autoConnect = Boolean.parseBoolean(args[4]);
        return PreferenceWrapper.create("temp", host, port, user, pw, autoConnect, "default.skml");
    }
}
