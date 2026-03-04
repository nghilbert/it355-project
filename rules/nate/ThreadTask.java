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

	/**
	 * Use this function to create a new thread so someone doesn't accidentally use
	 * run(). Calling run() directly on the thread will run the task on the
	 * current thread instead of creating a new one. Use start() to create a new
	 * thread and execute the task.
	 */
	public static void startNewThread(ThreadTask task) {
		Thread thread = new Thread(task);
		thread.start();
	}

	/** Run a test case */
	public static void main(String[] args) {
		startNewThread(new ThreadTask());
	}
}