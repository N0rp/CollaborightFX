package eu.dowsing.collaborightfx.preferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

/**
 * Wraps around the basic Java Preferences to provide some convienve methods.
 * 
 * @author richardg
 * 
 */
public class PreferenceWrapper {

    public static final String JABBER_HOST = "JabberHost";
    public static final String JABBER_PORT = "JabberPort";
    public static final String JABBER_USER = "JabberUser";
    public static final String JABBER_PASSWORD = "JabberPw";
    public static final String JABBER_AUTO_CONNECT = "JabberAutoConnect";

    /**
     * All the known preference keys and their default value.
     */
    private static final String[][] keysAndDefaults = new String[][] { new String[] { JABBER_HOST, "jabber.org" },
            new String[] { JABBER_PORT, "5522" }, new String[] { JABBER_USER, "foo (do not put @jabber.org)" },
            new String[] { JABBER_PASSWORD, "thepassword" }, new String[] { JABBER_AUTO_CONNECT, true + "" } };

    // private static final String PREF_NAME = "Main.pref";

    // private static Preferences prefs;

    private PreferenceWrapper() {
    }

    /**
     * Create new preferences with default values for every know value that is not specified
     * 
     * @return
     * @throws BackingStoreException
     */
    public static Preferences create(String prefName, String xHost, int xPort, String xUser, String xPw,
            boolean xAutoConnect) throws BackingStoreException {
        Preferences prefs = Preferences.userRoot().node(prefName);
        // set default values
        setTemplateValues(prefs, true);

        // set custom values
        prefs.put(JABBER_HOST, xHost);
        prefs.putInt(JABBER_PORT, xPort);
        prefs.put(JABBER_USER, xUser);
        prefs.put(JABBER_PASSWORD, xPw);
        prefs.putBoolean(JABBER_AUTO_CONNECT, xAutoConnect);

        return prefs;
    }

    // public String get(String key, String def) {
    // return prefs.get(key, def);
    // }

    /**
     * Load the preferences from file
     * 
     * @param createTemplate
     *            if <code>true</code> missing preference (attributes) will be created from template
     * @throws FileNotFoundException
     * @throws IOException
     * @throws InvalidPreferencesFormatException
     */
    public static Preferences load(String path, String prefName, boolean createTemplate) throws FileNotFoundException,
            IOException, InvalidPreferencesFormatException {
        File pref = new File(path + prefName);
        if (pref.exists()) {
            Preferences.importPreferences(new FileInputStream(pref));
        }
        Preferences prefs = Preferences.userRoot().node(prefName);
        if (createTemplate) {
            try {
                setTemplateValues(prefs, false);
            } catch (BackingStoreException e) {
                System.err.println("Could not set template values");
                e.printStackTrace();
            }
        }

        return prefs;
    }

    /**
     * Save preferences to file
     * 
     * @throws FileNotFoundException
     * @throws IOException
     * @throws BackingStoreException
     */
    public static void save(String prefPath, String prefName, Preferences prefs) throws FileNotFoundException,
            IOException, BackingStoreException {
        File pref = new File(prefPath, prefName);
        if (!pref.exists()) {
            pref.createNewFile();
        }
        prefs.exportNode(new FileOutputStream(pref));
    }

    /**
     * Print the preference values
     * 
     * @throws BackingStoreException
     */
    public static void printPreference(Preferences prefs) throws BackingStoreException {
        String e = "";

        String result = "";
        // now set the values
        String[] keys = prefs.keys();
        for (String key : keys) {

            if (!result.isEmpty()) {
                result += "\n";
            }

            result += key + ": " + prefs.get(key, e);
        }

        System.out.println(result);
    }

    /**
     * Set the template values for preference attributes that do not exist.
     * 
     * @param overwrite
     *            if <code>true</code> we will overwrite the existing value
     * @throws BackingStoreException
     * 
     *             <hr/>
     *             The preference API requires us to specify a default value for every get. So why would we use this
     *             method. Because then the stored preference files will have all the possible keys and an example
     *             present in case the user views it in a text editor.
     */
    public static void setTemplateValues(Preferences prefs, boolean overwrite) throws BackingStoreException {
        // we create an empty string
        String e = "";

        // now set the values
        for (String[] keyAndDefault : keysAndDefaults) {
            String key = keyAndDefault[0];
            String def = keyAndDefault[1];

            if (overwrite || prefs.get(key, e).equals(e)) {
                prefs.put(key, def);
            }
        }
    }
}
