package nachos.threads;

import nachos.machine.*;

/**
 * A <i>communicator</i> allows threads to synchronously exchange 32-bit
 * messages. Multiple threads can be waiting to <i>speak</i>, and multiple
 * threads can be waiting to <i>listen</i>. But there should never be a time
 * when both a speaker and a listener are waiting, because the two threads can
 * be paired off at this point.
 */
public class Communicator {
	/**
	 * Allocate a new communicator.
	 */
	public Communicator() {
	}

	/**
	 * Wait for a thread to listen through this communicator, and then transfer
	 * <i>word</i> to the listener.
	 *
	 * <p>
	 * Does not return until this thread is paired up with a listening thread.
	 * Exactly one listener should receive <i>word</i>.
	 *
	 * @param word the integer to transfer.
	 */
	public void speak(int word) {
		// Ensure atomicity
		lock.acquire();

		// Prevent this speaker from overwriting a previous speaker's word
		while (words) {
			canSpeak.sleep();
			
			canListen.wake(); // adding the listener wake in here.
		}

		// Once the buffer is empty, add our word to it, then flag it as full
		this.word = word;
		words = true;

		// Wake any waiting listeners
		//canListen.wake(); //adding the listen wake in here.
		// Go to sleep to ensure we are partnered before we return
		canSpeak.sleep();

		// Wake the next waiting speaker
		canSpeak.wake();

		lock.release();
	}

	/**
	 * Wait for a thread to speak through this communicator, and then return the
	 * <i>word</i> that thread passed to <tt>speak()</tt>.
	 *
	 * @return the integer transferred.
	 */
	public int listen() {
		// Ensure atomicity
		 lock.acquire(); 

		// Prevent the listener from accessing an empty buffer
		while (!words) {
			canListen.sleep();

			canSpeak.wake(); // adding the speaker wake in here.
		}

		// Once the buffer is full, take the word and flag it as empty
		int message = word;
		words = false;

		// Notify the speaker it is partnered
		// This message may be intercepted by another waiting speaker
		// That's okay -- the speaker will pass it along until it reaches the right one
		
		//canSpeak.wake(); // get rid of one extra speaker wake

		// We don't have to sleep here -- if we have a word we have a partner

		lock.release();

		return message;
	}

	// Add communicator testing code to the Communicator class

	/**
	 * Test with 1 listener then 1 speaker.
	 */
	public static void selfTest1() {

		KThread listener1 = new KThread(listenRun);
		listener1.setName("listener1");
		listener1.fork();

		KThread speaker1 = new KThread(speakerRun);
		speaker1.setName("speaker1");
		speaker1.fork();

	} // selfTest1()

	/**
	 * Test with 1 speaker then 1 listener.
	 */
	public static void selfTest2() {

		KThread speaker1 = new KThread(speakerRun);
		speaker1.setName("speaker1");
		speaker1.fork();

		KThread listener1 = new KThread(listenRun);
		listener1.setName("listener1");
		listener1.fork();

	} // selfTest2()

	/**
	 * Test with 2 speakers and 2 listeners intermixed.
	 */
	public static void selfTest3() {

		KThread speaker1 = new KThread(speakerRun);
		speaker1.setName("speaker1");
		speaker1.fork();

		KThread listener1 = new KThread(listenRun);
		listener1.setName("listener1");
		listener1.fork();

		KThread listener2 = new KThread(listenRun);
		listener2.setName("listener2");
		listener2.fork();

		KThread speaker2 = new KThread(speakerRun);
		speaker2.setName("speaker2");
		speaker2.fork();

	} // selfTest3()
    //Test with 3 speakers and 1 listener
	public static void selftest4() {

		KThread speaker1 = new KThread(speakerRun);
		speaker1.setName("speaker1");
		speaker1.fork();

		KThread speaker2 = new KThread(speakerRun);
		speaker2.setName("speaker2");
		speaker2.fork();

		KThread speaker3 = new KThread(speakerRun);
		speaker3.setName("speaker3");
		speaker3.fork();

		KThread listener1 = new KThread(listenRun);
		listener1.setName("listener1");
		listener1.fork();

	} 
    //Test with 1 speaker and 3 listeners
	public static void selftest5() {

		KThread speaker1 = new KThread(speakerRun);
		speaker1.setName("speaker1");
		speaker1.fork();

		KThread listener1 = new KThread(listenRun);
		listener1.setName("listener1");
		listener1.fork();

		KThread listener2 = new KThread(listenRun);
		listener2.setName("listener2");
		listener2.fork();

		KThread listener3 = new KThread(listenRun);
		listener3.setName("listener3");
		listener3.fork();

	}
	//Test with 5 speakers and 5 listeners
	public static void selftest6() {

		KThread speaker1 = new KThread(speakerRun);
		speaker1.setName("speaker1");
		speaker1.fork();

		KThread speaker2 = new KThread(speakerRun);
		speaker2.setName("speaker2");
		speaker2.fork();

		KThread speaker3 = new KThread(speakerRun);
		speaker3.setName("speaker3");
		speaker3.fork();

		KThread speaker4 = new KThread(speakerRun);
		speaker4.setName("speaker4");
		speaker4.fork();

		KThread speaker5 = new KThread(speakerRun);
		speaker5.setName("speaker5");
		speaker5.fork();

		KThread listener1 = new KThread(listenRun);
		listener1.setName("listener1");
		listener1.fork();

		KThread listener2 = new KThread(listenRun);
		listener2.setName("listener2");
		listener2.fork();

		KThread listener3 = new KThread(listenRun);
		listener3.setName("listener3");
		listener3.fork();
        
		KThread listener4 = new KThread(listenRun);
		listener4.setName("listener4");
		listener4.fork();

		KThread listener5 = new KThread(listenRun);
		listener5.setName("listener5");
		listener5.fork();
	} 
	/**
	 * Function to run inside Runnable object listenRun. Uses the function listen on
	 * static object myComm inside this class, allowing the threads inside the
	 * respective selfTests above to call the runnable variables below and test
	 * functionality for listen. Needs to run with debug flags enabled. See NACHOS
	 * README for info on how to run in debug mode.
	 */
	static void listenFunction() {
		Lib.debug(dbgThread, "Thread " + KThread.currentThread().getName() + " is about to listen");

		Lib.debug(dbgThread, "Thread " + KThread.currentThread().getName() + " got value " + myComm.listen());

	} // listenFunction()

	/**
	 * Function to run inside Runnable object speakerRun. Uses the function listen
	 * on static object myComm inside this class, allowing the threads inside the
	 * respective selfTests above to call the runnable variables below and test
	 * functionality for speak. Needs to run with debug flags enabled. See NACHOS
	 * README for info on how to run in debug mode.
	 */
	static void speakFunction() {
		Lib.debug(dbgThread, "Thread " + KThread.currentThread().getName() + " is about to speak");

		myComm.speak(myWordCount++);

		Lib.debug(dbgThread, "Thread " + KThread.currentThread().getName() + " has spoken");
	} // speakFunction()

	/**
	 * Wraps listenFunction inside a Runnable object so threads can be generated for
	 * testing.
	 */
	private static Runnable listenRun = new Runnable() {
		public void run() {
			listenFunction();
		}
	}; // runnable listenRun

	/**
	 * Wraps speakFunction inside a Runnable object so threads can be generated for
	 * testing.
	 */
	private static Runnable speakerRun = new Runnable() {
		public void run() {
			speakFunction();
		}
	}; // Runnable speakerRun

	// Implement more test methods here ...

	// Invoke Communicator.selfTest() from ThreadedKernel.selfTest()

	public static void selfTest() {
		selfTest1();
		selfTest2();
		selfTest3();

		selftest4();
        selftest5();
		selftest6();
		// Invoke your other test methods here ...

	}

	// dbgThread = 't' variable needed for debug output
	private static final char dbgThread = 't';
	// myComm is a shared object that tests Communicator functionality
	private static Communicator myComm = new Communicator();
	// myWordCount is used for selfTest5 when spawning listening/speaking threads
	private static int myWordCount = 0;

	private Lock lock = new Lock();

	private Condition canListen = new Condition(lock);
	private Condition canSpeak = new Condition(lock);

	// Flag to indicate if the buffer is full
	private boolean words = false;

	// Buffer with which to pass messages between speak() and listen()
	private int word;
}
