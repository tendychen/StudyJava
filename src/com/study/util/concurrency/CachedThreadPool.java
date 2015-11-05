package com.study.util.concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CachedThreadPool {

    public static void main(String[] args) {
	ExecutorService services = Executors.newCachedThreadPool();
	for (int i=0; i<5; ++i) {
	    services.execute(new LiftOff(10));
	}
	services.shutdown();
    }

}
