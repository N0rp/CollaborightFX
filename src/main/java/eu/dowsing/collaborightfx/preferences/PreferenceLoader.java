package eu.dowsing.collaborightfx.preferences;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Contains all known preferences. Retrieve instance by calling {@link PreferenceLoader#getInstance()}.
 * 
 * @author richardg
 * 
 */
public class PreferenceLoader {

    private static final String PREF_EXT = ".pref";

    private static final String prefFolder = "res/pref/";

    private ObservableList<String> preferenceFiles = FXCollections.observableArrayList();

    /** Map of a preference name to the one in memory. **/
    private Map<String, Preferences> name2LoadedPreferences = new HashMap<>();

    /** The current preferences, can be null. **/
    private Preferences currentPreferences;

    /** Name of the current preferences. **/
    private String currentPreferencesName;

    /**
     * Get the instance of the loader.
     */
    private static PreferenceLoader instance = new PreferenceLoader();

    private PreferenceLoader() {
        refreshPreferenceNames(prefFolder);
    }

    public static PreferenceLoader getInstance() {
        return instance;
    }

    /**
     * Get the preference file names on the permanent storage.
     * 
     * @return a list of preference file names.
     */
    public ObservableList<String> propertyPreferenceFileList() {
        return preferenceFiles;
    }

    /**
     * Return the current preferences that were set with {@link PreferenceLoader#setCurrentPreferences(String)} or
     * {@link PreferenceLoader#setCurrentPreferences(Preferences)}.
     * 
     * @return the current preferences or <code>null</code> if there are none set
     */
    public Preferences getCurrentPreferences() {
        return this.currentPreferences;
    }

    /**
     * Set the current preferences that can be retrieved by calling {@link PreferenceLoader#getCurrenPreferences()}.
     * 
     * @param preferenceName
     *            the name of the preferences that can be used for storing
     * @param pref
     *            preferences that might not be stored on the file system and thus temporary.
     */
    public void setCurrentPreferences(String preferencesName, Preferences pref) {
        this.currentPreferencesName = preferencesName;
        this.currentPreferences = pref;
    }

    /**
     * Set the current preferences that can be retrieved by calling {@link PreferenceLoader#getCurrenPreferences()}.
     * 
     * @param fileName
     *            name of one of the preferences on the file system.
     * @return <code>true</code> if setting was successful, else <code>false</code>. If <code>false</code> the
     *         preferences do not exist on the file system or could not be loaded into memory.
     */
    public boolean setCurrentPreferences(String fileName) {
        if (preferenceFiles.contains(fileName)) {
            if (!name2LoadedPreferences.containsKey(fileName)) {
                try {
                    name2LoadedPreferences.put(fileName, PreferenceWrapper.load(prefFolder, fileName, true));
                } catch (FileNotFoundException e) {
                    System.err.println("Could not find preferences: " + prefFolder + fileName);
                    e.printStackTrace();
                    return false;
                } catch (IOException e) {
                    System.err.println("Could not read preferences: " + prefFolder + fileName);
                    e.printStackTrace();
                    return false;
                } catch (InvalidPreferencesFormatException e) {
                    System.err.println("Wrong preferences format at: " + prefFolder + fileName);
                    e.printStackTrace();
                    return false;
                }
            }
            this.currentPreferencesName = fileName;
            this.currentPreferences = name2LoadedPreferences.get(fileName);
            return true;
        } else {
            return false;
        }
    }

    public void saveCurrentPreferences() throws FileNotFoundException, IOException, BackingStoreException {
        PreferenceWrapper.save(prefFolder, currentPreferencesName, currentPreferences);
    }

    /**
     * Refresh sketch names and remember the supplied name.
     * 
     * @param rootPath
     *            the root path were sketches fill be looked for.
     */
    private void refreshPreferenceNames(String prefFolder) {
        System.out.println("Refreshing Preference names");
        this.preferenceFiles.setAll(getPreferenceNames(prefFolder));
    }

    /**
     * Get all preference filesfiles names.
     * 
     * @param prefPath
     *            the folder where to look for preferences
     * @return a list of names that match the preference files available.
     */
    private List<String> getPreferenceNames(String prefPath) {
        List<String> prefNames = new LinkedList<String>();
        File root = new File(prefPath);
        if (!root.exists()) {
            throw new RuntimeException("Root preference folder does not exist at " + prefPath);
        } else if (root.isDirectory()) {
            for (String p : root.list()) {
                File f = new File(p);
                if (!f.isDirectory()) {
                    String name = f.getName();
                    int length = name.length();
                    if (length > PREF_EXT.length()
                            && name.substring(length - PREF_EXT.length(), length).equals(PREF_EXT)) {
                        prefNames.add(p);
                    }
                }
            }
        } else {
            throw new RuntimeException("Root sketch folder is not a directory at " + prefPath);
        }
        System.out.println("Found " + prefNames.size() + " Preference names with values " + prefNames);
        return prefNames;
    }

}
