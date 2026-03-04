/**
 * Demonstrates THI00-J: Do not invoke Thread.run().
 *
 * @author Nathan Hilbert
 */
public class ThreadTask implements Runnable {

	/** Prints the name of the thead that is executing the task */
	@Override
	public void run() {
		System.out.println("I am a thread running this task. My name is: " + Thread.currentThread().getName());
	}

	public static void main(String[] args) {
		ThreadTask task = new ThreadTask();

		/**
		 * Do not call run() directly on the thread. This will run the action on the
		 * current thread instead of creating a new one. Use start() to create a new
		 * thread and execute the task.
		 */
		Thread thread = new Thread(task);
		thread.start();
	}
}