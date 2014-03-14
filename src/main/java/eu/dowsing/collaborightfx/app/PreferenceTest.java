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

    public static final String ID1 = "Test1";
    public static final String ID2 = "Test2";
    public static final String ID3 = "Test3";

    private static final String PREF_NAME = "Main.pref";

    private Preferences prefs;

    public void load() throws FileNotFoundException, IOException, InvalidPreferencesFormatException {
        File pref = new File("res/" + PREF_NAME);
        if (pref.exists()) {
            Preferences.importPreferences(new FileInputStream(pref));
        }
        prefs = Preferences.userRoot().node(PREF_NAME);
    }

    public void save() throws FileNotFoundException, IOException, BackingStoreException {
        File pref = new File("res/" + PREF_NAME);
        if (!pref.exists()) {
            pref.createNewFile();
        }
        prefs.exportNode(new FileOutputStream(pref));
    }

    public void printPreference() {
        // This will define a node in which the preferences can be stored

        // First we will get the values
        // Define a boolean value
        System.out.println(prefs.getBoolean(ID1, true));
        // Define a string with default "Hello World
        System.out.println(prefs.get(ID2, "Hello World"));
        // Define a integer with default 50
        System.out.println(prefs.getInt(ID3, 50));
    }

    public void setTemplateValues() {
        // now set the values
        prefs.putBoolean(ID1, false);
        prefs.put(ID2, "Hello Europa");
        prefs.putInt(ID3, 45);
        // Delete the preference settings for the first value
        prefs.remove(ID1);
    }
}
