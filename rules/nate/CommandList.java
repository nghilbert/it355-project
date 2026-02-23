import java.io.*;

/**
 * <ul>
 * <li>Demonstrates SER01-J: Do not deviate from the proper signatures of
 * serialization methods.</li>
 * <li>Demonstrates SER04-J: Do not allow serialization and deserialization to
 * bypass SecurityManager. Note: SecurityManager has been deprecated
 * (<a href="https://openjdk.org/jeps/486">JEP 486</a>). Instead, I am using a
 * validation method to demonstrate this rule.</li>
 * <li>Demonstrates SER05-J: Do not serialize instances of inner classes.</li>
 * </ul>
 *
 * @author Nathan Hilbert
 */
public class CommandList implements Serializable {
	private Command[] commands;

	/**
	 * Validates then initializes a CommandList.
	 *
	 * @param commands a list of Command objects.
	 * 
	 * @throws InvalidObjectException if the any of the commands are invalid.
	 */
	public CommandList(Command[] commands) throws InvalidObjectException {
		validate(commands);
		this.commands = commands;
	}

	/**
	 * The inner Command class signature must be static to avoid serializing the
	 * list (per SER05-J).
	 */
	static class Command implements Serializable {
		private final String action;
		private final String parameter;

		Command(String action, String parameter) {
			this.action = action;
			this.parameter = parameter;
		}

		@Override
		public String toString() {
			return "{action: " + action + ", parameter: " + parameter + "}";
		}
	}

	/**
	 * Validates the fields of a command.
	 *
	 * @param commands  each command's action and parameter must not be null/blank.
	 * @param parameter must not be null or blank.
	 * @throws InvalidObjectException if the object is invalid.
	 */
	private void validate(Command[] commands) throws InvalidObjectException {
		for (Command command : commands) {
			if (command.action == null || command.action.isBlank()) {
				throw new InvalidObjectException("Each command must have an action.");
			}
			if (command.parameter == null || command.parameter.isBlank()) {
				throw new InvalidObjectException("Each command must have a parameter.");
			}
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
		validate(this.commands);
	}

	private static byte[] serialize(CommandList commandList) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		ObjectOutputStream outStream = new ObjectOutputStream(buffer);
		outStream.writeObject(commandList);

		byte[] serialized = buffer.toByteArray();
		outStream.close();
		return serialized;
	}

	private static CommandList deserialize(byte[] data) throws IOException, ClassNotFoundException {
		ByteArrayInputStream buffer = new ByteArrayInputStream(data);
		ObjectInputStream inStream = new ObjectInputStream(buffer);

		CommandList deserialized = (CommandList) inStream.readObject();
		inStream.close();
		return deserialized;
	}

	@Override
	public String toString() {
		String result = "";
		for (Command command : this.commands) {
			result += "	" + command + ",\n";
		}
		return "[\n" + result + "]";
	}

	/** Creates list, serializes, then deserializes it. Printing at each step. */
	public static void main(String[] args) {
		CommandList commandList;
		try {
			commandList = new CommandList(new Command[] {
					new Command("POWER", "ON"),
					new Command("POWER", "OFF")
			});
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return;
		}

		System.out.println("Original CommandList: " + commandList);
		try {
			byte[] serializedList = serialize(commandList);
			System.out.println("\nCommandList after serialization (memory address): " + serializedList);
			CommandList restoredList = deserialize(serializedList);
			System.out.println("\nCommandList after deserialization: " + restoredList);
		} catch (Exception e) {
			System.err.println("Serialization error: " + e.getMessage());
		}
	}
}