package com.study.util.concurrency;

//: concurrency/SleepingTask.java
//Calling sleep() to pause for a while.
import java.util.concurrent.*;

public class SleepingTask extends LiftOff {
    
    public SleepingTask(int countDown) {
	super(countDown);
    }

    public void run() {
	try {
	    while (countDown-- > 0) {
		System.out.print(status());
		// Old-style:
		// Thread.sleep(100);
		// Java SE5/6-style:
		TimeUnit.MILLISECONDS.sleep(100);
	    }
	} catch (InterruptedException e) {
	    System.err.println("Interrupted");
	}
    }

    public static void main(String[] args) {
	ExecutorService exec = Executors.newCachedThreadPool();
	for (int i = 0; i < 5; i++) {
	    exec.execute(new SleepingTask(10));
	}
	exec.shutdown();
    }
}
