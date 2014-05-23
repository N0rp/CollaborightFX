package eu.dowsing.collaborightfx.persistence;

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

    /**
     * Possible keys in the preference. Use toString() to get their value.
     * 
     * @author richardg
     * 
     */
    public enum Keys {
        JABBER_HOST, JABBER_PORT, JABBER_USER, JABBER_PASSWORD, JABBER_AUTO_CONNECT, SKETCH_OPEN
    }

    /**
     * All the known preference keys and their default value.
     */
    private static final Object[][] keysAndDefaults = new Object[][] { new Object[] { Keys.JABBER_HOST, "jabber.org" },
            new Object[] { Keys.JABBER_PORT, "5522" },
            new Object[] { Keys.JABBER_USER, "foo (do not put @jabber.org)" },
            new Object[] { Keys.JABBER_PASSWORD, "thepassword" }, new Object[] { Keys.JABBER_AUTO_CONNECT, "true" },
            new Object[] { Keys.SKETCH_OPEN, "current.skml" } };

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
            boolean xAutoConnect, String sketchOpen) throws BackingStoreException {
        Preferences prefs = Preferences.userRoot().node(prefName);
        // set default values
        setTemplateValues(prefs, true);

        // set custom values
        prefs.put(Keys.JABBER_HOST.toString(), xHost);
        prefs.putInt(Keys.JABBER_PORT.toString(), xPort);
        prefs.put(Keys.JABBER_USER.toString(), xUser);
        prefs.put(Keys.JABBER_PASSWORD.toString(), xPw);
        prefs.putBoolean(Keys.JABBER_AUTO_CONNECT.toString(), xAutoConnect);
        prefs.put(Keys.SKETCH_OPEN.toString(), sketchOpen);

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
        for (Object[] keyAndDefault : keysAndDefaults) {
            Keys key = (Keys) keyAndDefault[0];
            Object def = keyAndDefault[1];

            if (overwrite || prefs.get(key.toString(), e).equals(e)) {
                prefs.put(key.toString(), def.toString());
            }
        }
    }
}
