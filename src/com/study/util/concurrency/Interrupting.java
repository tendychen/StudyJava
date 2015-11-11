package com.study.util.concurrency;

//: concurrency/Interrupting.java
//Interrupting a blocked thread.
import java.util.concurrent.*;
import java.io.*;

class SleepBlocked implements Runnable {
    public void run() {
	try {
	    TimeUnit.SECONDS.sleep(100);
	} catch (InterruptedException e) {
	    System.out.println("SleepBlocked Thread: catch a InterruptedException");
	}
	System.out.println("SleepBlocked Thread: Exiting SleepBlocked.run()");
    }
}

class IOBlocked implements Runnable {
    private InputStream in;

    public IOBlocked(InputStream is) {
	in = is;
    }

    public void run() {
	try {
	    System.out.println("IOBlocked Thread: Waiting for read():");
	    in.read();
	} catch (IOException e) {
	    if (Thread.currentThread().isInterrupted()) {
		System.out.println("IOBlocked Thread: catch IoException and Interrupted from blocked I/O");
	    } else {
		System.out.println("IOBlocked Thread: catch IoException and not Interrupted");
	    }
	}
	System.out.println("IOBlocked Thread: Exiting IOBlocked.run()");
    }
}

class SynchronizedBlocked implements Runnable {
    public synchronized void f() {
	while (true) { // Never releases lock
	    Thread.yield();
	}
    }

    public SynchronizedBlocked() {
	new Thread() {
	    public void run() {
		f(); // Lock acquired by this thread
	    }
	}.start();
    }

    public void run() {
	System.out.println("SynchronizedBlocked Thread: Trying to call f()");
	f();
	System.out.println("SynchronizedBlocked Thread: Exiting SynchronizedBlocked.run()");
    }
}

public class Interrupting {
    private static ExecutorService exec = Executors.newCachedThreadPool();

    static void test(Runnable r) throws InterruptedException {
	Future<?> f = exec.submit(r);
	TimeUnit.MILLISECONDS.sleep(100);
	System.out.println("Interrupting " + r.getClass().getName());
	f.cancel(true); // Interrupts if running
	System.out.println("Interrupt sent to " + r.getClass().getName());
    }

    public static void main(String[] args) throws Exception {
	test(new SleepBlocked());
	test(new IOBlocked(System.in));
	test(new SynchronizedBlocked());
	TimeUnit.SECONDS.sleep(10);
	System.out.println("main thread Aborting with System.exit(0)");
	System.exit(0); // ... since last 2 interrupts failed
    }
}
