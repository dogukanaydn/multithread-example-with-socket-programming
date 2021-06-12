package Server;

import ClientMachine.MachineInformations;
import ClientPlanner.PlannerInformations;

import java.io.*;
import java.net.*;
import java.sql.SQLOutput;
import java.util.*;

class ClientHandler extends Thread {
    private Socket client;
    private Scanner input;
    private PrintWriter output;

    public ClientHandler(Socket socket) {
        //Set up reference to associated socket…
        client = socket;
        try {
            input = new Scanner(client.getInputStream());
            output = new PrintWriter(
                    client.getOutputStream(), true);
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        }
    }

    public void run() {
        String received;
        do {
            //Accept message from client on
            //the socket's input stream…
            received = input.nextLine();
            //Echo message back to client on
            //the socket's output stream…
            setOperation(received);
            output.println("ECHO: " + received);
            //Repeat above until 'QUIT' sent by client…
        } while (!received.equals("QUIT"));
        try {
            if (client != null) {
                System.out.println(
                        "Closing down connection…");
                client.close();
            }
        } catch (IOException ioEx) {
            System.out.println("Unable to disconnect!");
        }
    }

    public void setOperation(String fullText) {
        String[] pieceOfText;
        String protocolType;

        pieceOfText = fullText.split("##");
        protocolType = pieceOfText[0];
        switch (protocolType) {
            case "P_AUTH" -> login(fullText);
            case "M_INFO" -> addMachine(fullText);
            case "P_DISPLAYMACHINEINFOBYTYPE" -> showMachineList(fullText);
            case "P_DISPLAYMACHINESTATUSANDWORKS" -> displayMachineStatusAndWork(fullText);
            case "P_ADDWORKORDER" -> addWorkOrdersToServer(fullText);
            case "P_LISTWORKORDERS" -> listWorkOrders(fullText);
            default -> System.out.println("default çalıştı");
        }
    }

    // case: P_LISTWORKORDERS
    // format: P_LISTWORKORDERS
    private void listWorkOrders(String fullText) {
        StringBuilder sb = new StringBuilder();
        if (MachineInformations.allJobs.size() != 0) {
            output.println(MachineInformations.allJobs);
        } else {
            output.println("There is no work order waiting server");
        }
    }


    // case: P_AUTH
    // format: P_AUTH##username##password
    // format example: P_AUTH##dogukan##dogukanpass
    private void login(String fullText) {
        String[] pieceOfString = fullText.split("##");
        String name, password;
        name = pieceOfString[1];
        password = pieceOfString[2];
        boolean isLoggedIn = false;
        Map<String, String> users = PlannerInformations.allUserInformations;

        for (Map.Entry<String, String> entry : users.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (key.equals(name) && value.equals(password)) {
                isLoggedIn = true;
            }
        }

        if (isLoggedIn) {
            System.out.println("New user logged in to server");
            output.println("Login is successfull");
        } else {
            System.out.println("New user rejected");
            output.println("You entered wrong username or password");
        }

    }


    // case: M_INFO
    // format: M_INFO##machine-name##id##type##speed##status
    // format example: M_INFO##makine-1##1##CNC##1##EMPTY
    private void addMachine(String fullText) {
        String[] pieceOfString = fullText.split("##");

        for (int i = 1; i <= 5; i++) {
            MachineInformations.allMachineInformations.add(pieceOfString[i]);
        }
        output.println("Machine added successfuly");
        System.out.println("Added New Machine");
    }

    // case: P_DISPLAYMACHINEINFOBYTYPE
    // format: P_DISPLAYMACHINEINFOBYTYPE##type
    // format example: P_DISPLAYMACHINEINFOBYTYPE##CNC
    private void showMachineList(String fullText) {
        StringBuilder sb = new StringBuilder();
        String[] pieceOfString = fullText.split("##");
        String machineType = pieceOfString[1];
        for (int i = 0; i < MachineInformations.allMachineInformations.size(); i++) {
            if (machineType.equals(MachineInformations.allMachineInformations.get(i))) {
                sb.append("machine name : ").append(MachineInformations.allMachineInformations.get(i - 2)).append(" ");
                sb.append("machine id : ").append(MachineInformations.allMachineInformations.get(i - 1)).append(" ");
                sb.append("machine type : ").append(MachineInformations.allMachineInformations.get(i)).append(" ");
                sb.append("machine speed : ").append(MachineInformations.allMachineInformations.get(i + 1)).append(" ");
                sb.append("machine status : ").append(MachineInformations.allMachineInformations.get(i + 2)).append(" || ");
            }
        }
        output.println(sb);

    }

    // case: P_DISPLAYMACHINESTATUSANDWORKS
    // format: P_DISPLAYMACHINESTATUSANDWORKS##id
    private void displayMachineStatusAndWork(String fullText) {
        StringBuilder sb = new StringBuilder();

        String[] pieceOfString = fullText.split("##");
        String machineID = pieceOfString[1];

        if (MachineInformations.doneJobsByMachines.size() != 0) {
            for (int i = 0; i < MachineInformations.doneJobsByMachines.size(); i++) {
                if (MachineInformations.doneJobsByMachines.get(i).equals(machineID)) {
                    sb.append("machine id : ").append(MachineInformations.doneJobsByMachines.get(i)).append(" ");
                    sb.append("machine status : ").append(MachineInformations.doneJobsByMachines.get(i + 1)).append(" ");
                    sb.append("work id : ").append(MachineInformations.doneJobsByMachines.get(i + 2)).append(" ");
                }
            }
            output.println(sb);
        } else {
            output.println("There is no done jobs");
        }
    }


    // case: P_ADDWORKORDER
    // format: P_ADDWORKORDER##id##type##length
    // format example: P_ADDWORKORDER##1##CNC##234
    private void addWorkOrdersToServer(String fullText) {
        String[] pieceOfString = fullText.split("##");
        for (int i = 1; i <= 3; i++) {
            MachineInformations.allJobs.add(pieceOfString[i]);
        }
        System.out.println("Added new work order succesfully");
        output.println("You added new work order succesfully");

        assignJobToMachine();
    }

    private void assignJobToMachine() {
        for (int i = 0; i < MachineInformations.allMachineInformations.size(); i++) {
            if (MachineInformations.allJobs.size() != 0) {
                if (MachineInformations.allJobs.get(1).equals(MachineInformations.allMachineInformations.get(i))
                        && MachineInformations.allMachineInformations.get(i + 2).equals("EMPTY")) {

                    // Set machine 'BUSY'
                    MachineInformations.allMachineInformations.set(i + 2, "BUSY");

                    // add works by machine
                    MachineInformations.doneJobsByMachines.add(MachineInformations.allMachineInformations.get(i - 1));
                    MachineInformations.doneJobsByMachines.add(MachineInformations.allMachineInformations.get(i + 2));
                    MachineInformations.doneJobsByMachines.add(MachineInformations.allJobs.get(0));
                    MachineInformations.doneJobsByMachines.add(MachineInformations.allJobs.get(1));
                    MachineInformations.doneJobsByMachines.add(MachineInformations.allJobs.get(2));

                    // Calculate Time
                    int machineSpeed = Integer.parseInt(MachineInformations.allMachineInformations.get(i + 1));
                    int jobsSpeed = Integer.parseInt(MachineInformations.allJobs.get(2));
                    int calculateTime = (jobsSpeed / machineSpeed) * 1000;

                    // remove work order from all jobs
                    MachineInformations.allJobs.remove(0);
                    MachineInformations.allJobs.remove(0);
                    MachineInformations.allJobs.remove(0);


                    try {
                        System.out.println("uyuyor: " + MachineInformations.allMachineInformations.get(i - 2));
                        Thread.sleep(calculateTime);
                        // Set machine 'EMPTY'
                        MachineInformations.allMachineInformations.set(i + 2, "EMPTY");
                        MachineInformations.doneJobsByMachines.set(i - 1, "EMPTY");
                        System.out.println("uyandı: " + MachineInformations.allMachineInformations.get(i - 2));

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

}