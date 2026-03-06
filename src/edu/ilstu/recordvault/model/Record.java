package edu.ilstu.recordvault.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

// SER05-J (Nate)
public class Record implements Serializable {

    // RULE: SER01-J (Nate)
    private static final long serialVersionUID = 1L;
    private static final AtomicInteger ID_COUNTER = new AtomicInteger(1);

    private final int id;
    private String name;
    private String value;
    private List<String> tags; 

    // TSM03-J (Clayton): all fields are set before the object is returned to callers.
    Record(int id, String name, String value, List<String> tags) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.tags  = (tags != null) ? new ArrayList<>(tags) : new ArrayList<>(); // OBJ13-J 
    }

    // OBJ11-J: factory method is the only way external code creates a Record.
    // TSM03-J:the object is comlpete before it is returned
    public static Record of(String name, String value) {
        int newId = ID_COUNTER.getAndIncrement();
        return new Record(newId, name, value, new ArrayList<>());
    }

    // OBJ11-J: reconstruction from saved data also uses the factory
    public static Record ofExisting(int id, String name, String value, List<String> tags) {
        ID_COUNTER.updateAndGet(current -> Math.max(current, id + 1));
        return new Record(id, name, value, tags);
    }

    public int getId() { 
        return id;
    }

    public String getName() { 
        return name; 
    }

    public String getValue() { 
        return value; 
    }

    // OBJ13-J (Clayton) returns a copy so callers cannot modify the internal list.
    public List<String> getTags() {
        return new ArrayList<>(this.tags);
    }

    public void setValue(String value) { this.value = value; }

    // OBJ13-J: stores a copy of the list
    public void setTags(List<String> newTags) {
        this.tags = (newTags != null) ? new ArrayList<>(newTags) : new ArrayList<>();
    }

    public void addTag(String tag) {
        if (tag != null && !tag.isEmpty()) {
            this.tags.add(tag);
        }
    }
