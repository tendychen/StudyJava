package com.study.util.concurrency;

//: concurrency/NIOInterruption.java
//Interrupting a blocked NIO channel.
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.concurrent.*;
import java.io.*;

class NIOBlocked implements Runnable {
    private final SocketChannel sc;

    public NIOBlocked(SocketChannel sc) {
	this.sc = sc;
    }

    public void run() {
	try {
	    System.out.println("Waiting for read() in " + this);
	    sc.read(ByteBuffer.allocate(1));
	} catch (ClosedByInterruptException e) {
	    System.out.println("ClosedByInterruptException");
	} catch (AsynchronousCloseException e) {
	    System.out.println("AsynchronousCloseException");
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}
	System.out.println("Exiting NIOBlocked.run() " + this);
    }
}

public class NIOInterruption {
    public static void main(String[] args) throws Exception {
	ExecutorService exec = Executors.newCachedThreadPool();
	ServerSocket server = new ServerSocket(8080);
	InetSocketAddress isa = new InetSocketAddress("localhost", 8080);
	SocketChannel sc1 = SocketChannel.open(isa);
	SocketChannel sc2 = SocketChannel.open(isa);
	Future<?> f = exec.submit(new NIOBlocked(sc1));
	exec.execute(new NIOBlocked(sc2));
	exec.shutdown();
	System.out.println("before sleep(1)");
	TimeUnit.SECONDS.sleep(1);
	// Produce an interrupt via cancel:
	System.out.println("before cancel");
	f.cancel(true);
	TimeUnit.SECONDS.sleep(1);
	// Release the block by closing the channel:
	System.out.println("before close");
	sc2.close();
    }
}