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
    /**
 * Creates a new Record with a unique ID.
 *
 * Uses an AtomicInteger to safely generate IDs across threads
 * (per VNA00-J). The object is fully initialized before being
 * returned to callers (per TSM03-J).
 *
 * @param name the record name
 * @param value the record value
 * @return the created Record
 * @throws InvalidObjectException if the record fields are invalid
 */
    public static Record of(String name, String value) throws InvalidObjectException {
        int newId = ID_COUNTER.getAndIncrement();
        return new Record(newId, name, value, new ArrayList<>());
    }

    // OBJ11-J: reconstruction from saved data also uses the factory
    /**
 * Recreates a Record from previously saved data.
 *
 * Updates the ID counter to prevent duplicate IDs when loading
 * stored records (per VNA00-J).
 *
 * @param id the existing record ID
 * @param name the record name
 * @param value the record value
 * @param tags the associated tags
 * @return the reconstructed Record
 * @throws InvalidObjectException if the record fields are invalid
 */
    public static Record ofExisting(int id, String name, String value, List<String> tags) throws InvalidObjectException {
        ID_COUNTER.updateAndGet(current -> Math.max(current, id + 1));
        return new Record(id, name, value, tags);
    }
    /**
 * Returns the unique ID of the record.
 *
 * @return the record ID
 */
    public int getId() { 
        return id;
    }
    /**
 * Returns the name of the record.
 *
 * @return the record name
 */
    public String getName() { 
        return name; 
    }
    /**
 * Returns the stored value of the record.
 *
 * @return the record value
 */
    public String getValue() { 
        return value; 
    }

    // OBJ13-J (Clayton) returns a copy so callers cannot modify the internal list.
    /**
 * Returns a copy of the tag list.
 *
 * A defensive copy is returned so callers cannot modify
 * the internal list directly (per OBJ13-J).
 *
 * @return a copy of the tag list
 */
    public List<String> getTags() {
        return new ArrayList<>(this.tags);
    }
    /**
 * Updates the value stored in the record.
 *
 * @param value the new value
 */
    public void setValue(String value) { this.value = value; }

    // OBJ13-J: stores a copy of the list
    /**
 * Replaces the record's tags.
 *
 * A copy of the provided list is stored to protect the
 * internal state of the object (per OBJ13-J).
 *
 * @param newTags the new tag list
 */
    public void setTags(List<String> newTags) {
        this.tags = (newTags != null) ? new ArrayList<>(newTags) : new ArrayList<>();
    }
    /**
 * Adds a tag to the record if it is valid.
 *
 * @param tag the tag to add
 */
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
    /**
 * Returns a string representation of the record.
 *
 * @return a formatted string containing the record fields
 */
    @Override
    public String toString() {
        return "{ id=" + id + ", name=" + name + ", value=" + value + " }";
    }
}
