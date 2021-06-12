package ClientPlanner;

import ClientMachine.MachineInformations;

import java.io.*;
import java.net.*;
import java.util.*;

public class ClientPlanner {
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
        str = isLoggedIn();
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
                menu();

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

    public static void menu() {
        int choice;
        Scanner input = new Scanner(System.in);
        System.out.println("---------------------------------------------------- Menu ----------------------------------------------------");
        System.out.println("1 ------- Display all machines informations by type: ");
        System.out.println("2 ------- Select the machine current status and work it has done so far");
        System.out.println("3 ------- Listing work orders");
        System.out.println("4 ------- Enter a new work order");
        System.out.println("--------------------------------------------------------------------------------------------------------------");
        System.out.print(
                "Select the action you want to do or if you want to exit press 0: ");
        choice = input.nextInt();
        System.out.println("--------------------------------------------------------------------------------------------------------------");
        switch (choice) {
            case 1 -> sendMessages(getMachineByType());
            case 2 -> sendMessages(getMachineStatus());
            case 3 -> sendMessages(listWorkOrders());
            case 4 -> sendMessages(addWorkOrder());
            default -> System.out.println("planner default çalıştı");
        }
    }

    // Case 1
    // format: P_DISPLAYMACHINEINFOBYTYPE##type
    public static String getMachineByType() {
        Scanner input = new Scanner(System.in);
        String displayMachineStatusByType = "P_DISPLAYMACHINEINFOBYTYPE##";
        String value = "";
        System.out.print("Enter a machine type: ");
        value = input.nextLine();
        displayMachineStatusByType += value;
        System.out.println("displayMachineClient: " + displayMachineStatusByType);
        return displayMachineStatusByType;
    }

    // Case 2
    // format: P_DISPLAYMACHINESTATUSANDWORKS##id
    public static String getMachineStatus() {
        Scanner input = new Scanner(System.in);
        String displayMachineStatusAndWorks = "P_DISPLAYMACHINESTATUSANDWORKS##";
        String value = "";
        System.out.print("Enter machine id: ");
        value = input.nextLine();
        displayMachineStatusAndWorks += value;
        return displayMachineStatusAndWorks;
    }

    // case 3
    // format: P_LISTWORKORDERS##
    public static String listWorkOrders() {
        return "P_LISTWORKORDERS";
    }


    // case 4
    // format: P_ADDWORKORDER##id##type##length
    public static String addWorkOrder() {
        Scanner input = new Scanner(System.in);
        String workOrderInformations = "P_ADDWORKORDER##";
        String value = "";
        System.out.print("Enter a work id: ");
        value = input.nextLine();
        workOrderInformations += value + "##";
        System.out.print("Enter a work type: ");
        value = input.nextLine();
        workOrderInformations += value + "##";
        System.out.print("Enter a work length: ");
        value = input.nextLine();
        workOrderInformations += value;
        return workOrderInformations;
    }

    // format: P_AUTH##username##password
    public static String isLoggedIn() {
        Scanner input = new Scanner(System.in);
        String userInformations = "P_AUTH##";
        String value = "";
        System.out.print("Enter a name: ");
        value = input.nextLine();
        userInformations += value + "##";
        System.out.print("Enter a password: ");
        value = input.nextLine();
        userInformations += value;
        return userInformations;
    }
}



