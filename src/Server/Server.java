package Server;

import java.io.*;
import java.net.*;

public class Server {
    private static ServerSocket serverSocket;
    private static final int PORT = 1234;

    public static void main(String[] args)
            throws IOException {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server is running");

        } catch (IOException ioEx) {
            System.out.println("\nUnable to set up port!");
            System.exit(1);
        }
        do {
            //Wait for client…
            Socket client = serverSocket.accept();
            // System.out.println("\nNew client accepted.\n");
            //Create a thread to handle communication with
            //this client and pass the constructor for this
            //thread a reference to the relevant socket…
            ClientHandler handler = new ClientHandler(client);
            handler.start();//As usual, method calls run .
        } while (true);
    }
}

