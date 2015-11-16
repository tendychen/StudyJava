package com.study.util.concurrency;

//: concurrency/waxomatic2/WaxOMatic2.java
//Using Lock and Condition objects.

import java.util.concurrent.*;
import java.util.concurrent.locks.*;

class CarMatic2 {
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();
    private boolean waxOn = false;

    public void waxed() {
	lock.lock();
	try {
	    waxOn = true; // Ready to buff
	    condition.signalAll();
	} finally {
	    lock.unlock();
	}
    }

    public void buffed() {
	lock.lock();
	try {
	    waxOn = false; // Ready for another coat of wax
	    condition.signalAll();
	} finally {
	    lock.unlock();
	}
    }

    public void waitForWaxing() throws InterruptedException {
	lock.lock();
	try {
	    while (waxOn == false)
		condition.await();
	} finally {
	    lock.unlock();
	}
    }

    public void waitForBuffing() throws InterruptedException {
	lock.lock();
	try {
	    while (waxOn == true)
		condition.await();
	} finally {
	    lock.unlock();
	}
    }
}

class WaxOnMatic2 implements Runnable {
    private CarMatic2 car;

    public WaxOnMatic2(CarMatic2 c) {
	car = c;
    }

    public void run() {
	try {
	    while (!Thread.interrupted()) {
		System.out.println("Wax On! ");
		TimeUnit.MILLISECONDS.sleep(200);
		car.waxed();
		car.waitForBuffing();
	    }
	} catch (InterruptedException e) {
	    System.out.println("Exiting via interrupt");
	}
	System.out.println("Ending Wax On task");
    }
}

class WaxOffMatic2 implements Runnable {
    private CarMatic2 car;

    public WaxOffMatic2(CarMatic2 c) {
	car = c;
    }

    public void run() {
	try {
	    while (!Thread.interrupted()) {
		car.waitForWaxing();
		System.out.println("Wax Off! ");
		TimeUnit.MILLISECONDS.sleep(200);
		car.buffed();
	    }
	} catch (InterruptedException e) {
	    System.out.println("Exiting via interrupt");
	}
	System.out.println("Ending Wax Off task");
    }
}

public class WaxOMatic2 {
    public static void main(String[] args) throws Exception {
	CarMatic2 car = new CarMatic2();
	ExecutorService exec = Executors.newCachedThreadPool();
	exec.execute(new WaxOffMatic2(car));
	exec.execute(new WaxOnMatic2(car));
	TimeUnit.SECONDS.sleep(5);
	exec.shutdownNow();
    }
}
