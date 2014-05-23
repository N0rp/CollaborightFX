package eu.dowsing.collaborightfx.com.contacts;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.jivesoftware.smack.RosterEntry;

public class People {

    private ObservableList<Person> persons = FXCollections.observableArrayList();

    public void addPerson(RosterEntry entry) {
        this.persons.add(new Person(entry));
    }

    public ObservableList<Person> getPeople() {
        return persons;
    }

}
