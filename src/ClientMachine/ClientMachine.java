package ClientMachine;

import java.io.*;
import java.net.*;
import java.util.*;

public class ClientMachine {

    private static InetAddress host;
    private static final int PORT = 1234;

    public static void main(String[] args) {
        String str;
        try {
            host = InetAddress.getLocalHost();
        } catch (UnknownHostException uhEx) {
            System.out.println("\nHost ID not found!\n");
            System.exit(1);
        }
        str = addMachine();
        sendMessages(str);
    }

    private static void sendMessages(String fullText) {
        Socket socket = null;

        try {
            socket = new Socket(host, PORT);
            Scanner networkInput =
                    new Scanner(socket.getInputStream());
            PrintWriter networkOutput =
                    new PrintWriter(
                            socket.getOutputStream(), true);
            //Set up stream for keyboard entry…
            Scanner input = new Scanner(System.in);
            String message, response;

            do {
                //Send message to server on the
                //socket's output stream…
                //Accept response from server on the
                //socket's input stream…
                message = fullText;
                networkOutput.println(message);
                response = networkInput.nextLine();
                //Display server's response to user…
                System.out.println(
                        "\nSERVER> " + response);
                System.out.print(
                        "Enter message ('QUIT' to exit): ");
                message = input.nextLine();
            } while (!message.equals("QUIT"));
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        } finally {
            try {
                System.out.println(
                        "\nClosing connection…");
                socket.close();
            } catch (IOException ioEx) {
                System.out.println(
                        "Unable to disconnect!");
                System.exit(1);
            }
        }
    }

    public static String addMachine() {
        Scanner input = new Scanner(System.in);
        MachineInformations newMachine = new MachineInformations();

        String machineInformations = "M_INFO##";
        String value = "";
        System.out.print("Enter a machine name: ");
        value = input.nextLine();
        machineInformations += value + "##";
        System.out.print("Enter a machine ID: ");
        value = input.nextLine();
        machineInformations += value + "##";
        System.out.print("Enter machine type: ");
        value = input.nextLine();
        machineInformations += value + "##";
        System.out.print("Enter machine speed: ");
        value = input.nextLine();
        machineInformations += value + "##EMPTY";
        return machineInformations;
    }
}

