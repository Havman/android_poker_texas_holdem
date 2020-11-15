package com.example.poker;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ThreadHandler extends Thread {

    private ArrayList<ServerThread> serverThreads = new ArrayList<>();

    public void run(ServerSocket serverSocket) throws IOException {
        while (!isInterrupted()) { //You should handle interrupts in some way, so that the thread won't keep on forever if you exit the app.
//            (Code...)
            ServerThread thread = new ServerThread(serverSocket.accept());
            serverThreads.add(thread);
            thread.start();
        }
    }
    public void doSomethingOnAllThreads() {
        for (ServerThread serverThread : serverThreads) {
            serverThread.otherMethod();
        }
    }

    public class ServerThread extends Thread {
        private Socket socket;
        public ServerThread(Socket socket) {
            this.socket = socket;
        }
        public void run() {
//            (...other code here.)
        }
        public void otherMethod() {
            //Signal to the thread that it needs to do something (which should then be handled in the run method)
        }
    }
}