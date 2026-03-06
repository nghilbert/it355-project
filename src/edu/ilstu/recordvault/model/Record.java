package edu.ilstu.recordvault.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

// SER05-J (Nate)
public class Record implements Serializable {

    /**
     * SER00-J: increment this value manually when incompatible changes are made to
     * this class. Tracks compatibility with previously saved files.
     */
    private static final long serialVersionUID = 1L;

    /** RULE: VNA00-J (Caleb) */
    private static final AtomicInteger ID_COUNTER = new AtomicInteger(1);

    private final int id;
    private String name;
    private String value;
    private List<String> tags; 

    // TSM03-J (Clayton): all fields are set before the object is returned to callers.
    Record(int id, String name, String value, List<String> tags) throws InvalidObjectException {
        validate(id, name, value); // per SER04-J
        this.id = id;
        this.name = name;
        this.value = value;
        this.tags  = (tags != null) ? new ArrayList<>(tags) : new ArrayList<>(); // OBJ13-J 
    }

    // OBJ11-J: factory method is the only way external code creates a Record.
    // TSM03-J:the object is comlpete before it is returned
    public static Record of(String name, String value) throws InvalidObjectException {
        int newId = ID_COUNTER.getAndIncrement();
        return new Record(newId, name, value, new ArrayList<>());
    }

    // OBJ11-J: reconstruction from saved data also uses the factory
    public static Record ofExisting(int id, String name, String value, List<String> tags) throws InvalidObjectException {
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

    /**
     * Validates the fields of a Record. Validation must be done in the constructor
     * and after reading a serialized object. (per SER04-J)
     *
     * @throws InvalidObjectException if any of the object's fields are invalid.
     */
    private void validate(int id, String name, String value) throws InvalidObjectException {
        if (name == null || name.isEmpty()) {
            throw new InvalidObjectException("Record name is missing or empty");
        }
        if (value == null) {
            throw new InvalidObjectException("Record value is null");
        }
        if (id <= 0) {
            throw new InvalidObjectException("Record has invalid id: " + this.id);
        }
    }

    /**
     * Must have a private and non-static signature. Deviating from this rule could
     * cause unexpected serialization behavior (per SER01-J).
     *
     * @param outStream the output stream.
     * @throws IOException if an I/O error occurs.
     */
    private void writeObject(ObjectOutputStream outStream) throws IOException {
        outStream.defaultWriteObject();
    }

    /**
     * Must have a private and non-static signature. Deviating from this rule could
     * cause unexpected serialization behavior (per SER01-J).
     * <p>
     * Deserialization bypasses the constructor. Must call validate (per SER04-J).
     *
     * @param inStream the input stream.
     * @throws IOException            if an I/O error occurs.
     * @throws ClassNotFoundException if the class of a serialized object could not
     *                                be found.
     */
    private void readObject(ObjectInputStream inStream) throws IOException, ClassNotFoundException {
        inStream.defaultReadObject();
        validate(this.id, this.name, this.value);
        this.tags = (this.tags != null) ? new ArrayList<>(this.tags) : new ArrayList<>(); // SER06-J
    }

    @Override
    public String toString() {
        return "{ id=" + id + ", name=" + name + ", value=" + value + " }";
    }
}