package com.study.util.concurrency;

public class MainThread {

    public static void main(String[] args) {
	LiftOff obj = new LiftOff(10);
	obj.run();
    }

}
