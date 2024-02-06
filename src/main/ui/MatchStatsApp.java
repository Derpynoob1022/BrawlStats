package ui;

import model.MatchList;
import model.MatchLog;

import java.util.Scanner;

// the user interface for the application
public class MatchStatsApp {
    private Scanner input;
    private MatchList log;

    public MatchStatsApp() {
        runMatchStatsApp();
    }

    // MODIFIES: this
    // EFFECTS: processes user input
    // INSPIRED BY THE TELLER APP EXAMPLE!!!
    private void runMatchStatsApp() {
        boolean running = true;
        String command = null;

        setup();

        while (running) {
            displayOptions();
            command = input.next();
            command = command.toLowerCase();

            if (command.equals("exit")) {
                running = false;
            } else {
                processCommand(command);
            }
        }
    }

    private void processCommand(String s) {
        switch (s) {
            case "help":
                displayOptions();
                break;
            case "view logs":
                displayLogs();
                break;
            case "stats":
                viewStats();
                break;
            case "add":
                addLog();
                break;
        }
    }

    private void addLog() {
    }

    private void viewStats() {

    }

    private void displayLogs() {

    }

    private void setup() {
        log = new MatchList();
        input = new Scanner(System.in);
        input.useDelimiter("\n");
        System.out.println("type \"help\" for more instructions ");
    }

    private void displayOptions() {

    }


}
