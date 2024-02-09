package ui;

import exception.DivideByZeroException;
import exception.IndexOutOfBound;
import exception.NoMatchingFields;
import model.MatchList;
import model.MatchLog;

import java.util.InputMismatchException;
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

    // EFFECTS: processes user inputs
    private void processCommand(String s) {
        switch (s) {
            case "help":
                displayOptions();
                break;
            case "view logs":
                displayHistory();
                break;
            case "stats":
                viewStats();
                break;
            case "add":
                addLog();
                break;
            case "edit":
                editLog();
                break;
            default:
                System.out.println("Command not found");
        }
    }

    // MODIFIES: this
    // EFFECTS: able to make edits on a log
    @SuppressWarnings("methodlength")
    private void editLog() {
        int indexNumber;
        String field;
        String replacement;

        if (log.getSize() == 0) {
            System.out.println("There is no log yet");
            return;
        } else {
            System.out.println("Which log do you want to update?");
            System.out.println("0 - " + (log.getSize() - 1));
        }

        indexNumber = Integer.parseInt(input.next());
        try {
            System.out.println(log.getLog(indexNumber).logToString() + " selected!");
        } catch (IndexOutOfBound e) {
            System.out.println("Index not found");
        }

        System.out.println("Which field do you want to edit?");
        field = input.next();
        System.out.println(field + " selected!");

        System.out.println("What do you want to replace it with?");
        replacement = input.next();
        System.out.println(replacement + " confirmed!");

        try {
            log.editList(indexNumber, field, replacement);
            System.out.println("Log changed to: " + log.getLog(indexNumber).logToString());
        } catch (IndexOutOfBound e) {
            System.out.println("Index not found");
        } catch (NoMatchingFields e) {
            System.out.println("Field not accepted");
        }
    }

    // MODIFIES: this
    // EFFECTS: adds a log to the MatchList
    @SuppressWarnings("methodlength")
    private void addLog() {
        String name;
        int damage;
        int kills;
        int deaths;
        boolean isMvp;
        int deltaTrophy;

        try {
            System.out.println("Character name?");
            name = input.next();

            System.out.println("Damage?");
            damage = input.nextInt();

            System.out.println("Kills?");
            kills = input.nextInt();

            System.out.println("Deaths?");
            deaths = input.nextInt();

            System.out.println("Star player? true or false");
            isMvp = input.nextBoolean();

            System.out.println("Trophy gain or loss?");
            deltaTrophy = input.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Invalid input");
            input.nextLine();
            return;
        }

        try {
            log.addLog(new MatchLog(name, kills, deaths, damage, isMvp, deltaTrophy));
            System.out.println("Match added");
        } catch (Exception e) {
            System.out.println("Cannot add match");
        }
    }

    // EFFECTS: prints out some statistics
    private void viewStats() {
        printCommandList();
        String statInput = input.next();

        switch (statInput) {
            case "trophy":
                System.out.println(log.totalTrophyGain());
                break;
            case "character":
                System.out.println("Select character");
                String name = input.next();
                try {
                    System.out.println(log.characterStat(name));
                } catch (DivideByZeroException e) {
                    System.out.println("Character does not exist");
                }
                break;
            case "star":
                System.out.println(log.starPlayerPercentage());
                break;
            default: System.out.println("Command not found");
        }
    }

    private void printCommandList() {
        System.out.println("=============================================");
        System.out.println("What stat do you want to see?");
        System.out.println("Total trophy gain? -> enter \"trophy\"");
        System.out.println("Total games with a character? -> enter \"character\"");
        System.out.println("Star player percentage? -> enter \"star\"");
        System.out.println("=============================================");
    }

    // EFFECTS: prints out the past 15 matches
    private void displayHistory() {
        for (MatchLog logs : log.getList()) {
            System.out.println(logs.logToString());
            System.out.println();
        }
    }

    // MODIFIES: this
    // EFFECTS: sets up the application
    private void setup() {
        log = new MatchList();
        input = new Scanner(System.in);
        input.useDelimiter("\n");
        System.out.println("type \"help\" for more instructions ");
    }

    //EFFECTS: prints out the list of commands
    private void displayOptions() {
        System.out.println("=============================================");
        System.out.println("To view the past games -> \"view logs\"");
        System.out.println("To show statistics -> \"stats\"");
        System.out.println("To add a game -> \"add\"");
        System.out.println("To change an entry -> \"edit\"");
        System.out.println("quit application -> \"exit\"");
        System.out.println("=============================================");
    }
}
