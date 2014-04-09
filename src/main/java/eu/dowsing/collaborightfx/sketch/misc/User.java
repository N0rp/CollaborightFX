package eu.dowsing.collaborightfx.sketch.misc;

import org.simpleframework.xml.Attribute;

/**
 * A user.
 * 
 * @author richardg
 * 
 */
public class User {

    public final static User DEFAULT = new User("Default");
    public final static User TEMP = new User("Temporary");

    @Attribute(name = "name")
    private String name;

    public User(@Attribute(name = "name") String name) {
        this.name = name;
    }
}
