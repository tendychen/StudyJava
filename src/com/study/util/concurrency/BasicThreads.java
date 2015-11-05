package com.study.util.concurrency;

public class BasicThreads {

    public static void main(String[] args) {
	for (int i=0; i<5; ++i) {
	    new Thread(new LiftOff(100)).start();
	}
	System.out.println("waiting for liftoff ");
    }
}
