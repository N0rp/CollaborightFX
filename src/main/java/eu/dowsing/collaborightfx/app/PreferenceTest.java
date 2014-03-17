package eu.dowsing.collaborightfx.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

public class PreferenceTest {

    public static final String JABBER_HOST_DEFAULT = "jabber.org";
    public static final String JABBER_USER_DEFAULT = "foo";
    public static final String JABBER_PASSWORD_DEFAULT = "bar$";

    public static final String JABBER_HOST = "JabberHost";
    public static final String JABBER_USER = "JabberUser";
    public static final String JABBER_PASSWORD = "JabberPw";

    private static final String PREF_NAME = "Main.pref";

    private static Preferences prefs;

    private PreferenceTest() {
    }

    public static Preferences getPreferences() {
        return prefs;
    }

    /**
     * Load the preferences from file
     * 
     * @param createTemplate
     *            if <code>true</code> missing preference (attributes) will be created from template
     * @throws FileNotFoundException
     * @throws IOException
     * @throws InvalidPreferencesFormatException
     */
    public static void load(boolean createTemplate) throws FileNotFoundException, IOException,
            InvalidPreferencesFormatException {
        File pref = new File("res/" + PREF_NAME);
        if (pref.exists()) {
            Preferences.importPreferences(new FileInputStream(pref));
        }
        prefs = Preferences.userRoot().node(PREF_NAME);
        if (createTemplate) {
            try {
                setTemplateValuesForNotExisting();
            } catch (BackingStoreException e) {
                System.err.println("Could not set template values");
                e.printStackTrace();
            }
        }
    }

    /**
     * Save preferences to file
     * 
     * @throws FileNotFoundException
     * @throws IOException
     * @throws BackingStoreException
     */
    public static void save() throws FileNotFoundException, IOException, BackingStoreException {
        File pref = new File("res/" + PREF_NAME);
        if (!pref.exists()) {
            pref.createNewFile();
        }
        prefs.exportNode(new FileOutputStream(pref));
    }

    /**
     * Print the preference values
     */
    public static void printPreference() {
        // This will define a node in which the preferences can be stored

        // First we will get the values
        // Define a boolean value
        System.out.println(prefs.get(JABBER_HOST, JABBER_HOST_DEFAULT));
        // Define a string with default "Hello World
        System.out.println(prefs.get(JABBER_USER, JABBER_USER_DEFAULT));
        // Define a integer with default 50
        System.out.println(prefs.get(JABBER_PASSWORD, JABBER_PASSWORD_DEFAULT));
    }

    /**
     * Set the template values for preference attributes that do not exist
     * 
     * @throws BackingStoreException
     */
    public static void setTemplateValuesForNotExisting() throws BackingStoreException {
        // now set the values

        // we create an empty string
        String e = "";

        if (prefs.get(JABBER_HOST, e).equals(e)) {
            prefs.put(JABBER_HOST, JABBER_HOST_DEFAULT);
        }

        if (prefs.get(JABBER_USER, e).equals(e)) {
            prefs.put(JABBER_USER, JABBER_USER_DEFAULT);
        }

        if (prefs.get(JABBER_PASSWORD, e).equals(e)) {
            prefs.put(JABBER_PASSWORD, JABBER_PASSWORD_DEFAULT);
        }
    }
}
