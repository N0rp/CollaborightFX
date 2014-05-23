package eu.dowsing.collaborightfx.com.contacts;

import org.jivesoftware.smack.RosterEntry;

/**
 * Wraps around xmpp roster entries and other to bring you people.
 * 
 * @author richardg
 * 
 */
public class Person {

    private RosterEntry entry;

    public Person(RosterEntry entry) {
        this.entry = entry;
    }

    public String getUser() {
        return entry.getUser();
    }

}
