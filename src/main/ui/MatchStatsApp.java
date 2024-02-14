package ui;

import model.exception.CharacterDoesNotExistException;
import model.exception.IllegalValueException;
import model.exception.IndexOutOfBound;
import model.exception.NoMatchingFields;
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

    // MODIFIES: this
    // EFFECTS: sets up the application
    private void setup() {
        log = new MatchList();
        input = new Scanner(System.in);
        input.useDelimiter("\n");
        System.out.println("type \"help\" for more instructions ");
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
            case "del":
                deleteLog();
                break;
            default:
                System.out.println("Command not found");
        }
    }

    // MODIFIES: this
    // EFFECTS: able to make edits on a log
    private void editLog() {

        if (log.getSize() == 0) {
            System.out.println("There is no log yet");
        } else {
            try {
                System.out.println("Which log do you want to update?");
                System.out.println("0 - " + (log.getSize() - 1));

                int indexNumber = Integer.parseInt(input.next());

                System.out.println(log.getLog(indexNumber).logToString() + " selected!");

                System.out.println("Which field do you want to edit?");
                System.out.println("name/kills/deaths/damage/mvp/trophy");
                String field = input.next();

                System.out.println("What do you want to replace it with?");
                String replacement = input.next();

                log.editList(indexNumber, field, replacement);
                System.out.println("Log changed to: " + log.getLog(indexNumber).logToString());

            } catch (IndexOutOfBound e) {
                System.out.println("Index not found");
            } catch (NoMatchingFields e) {
                System.out.println("Field not accepted");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input");
            }
        }
    }

    // MODIFIES: this
    // EFFECTS: adds a log to the MatchList
    private void addLog() {
        try {
            System.out.println("Character name?");
            String name = input.next();

            System.out.println("Damage?");
            int damage = input.nextInt();

            System.out.println("Kills?");
            int kills = input.nextInt();

            System.out.println("Deaths?");
            int deaths = input.nextInt();

            System.out.println("Star player? true or false");
            boolean isMvp = input.nextBoolean();

            System.out.println("Trophy gain?");
            int deltaTrophy = input.nextInt();

            log.addLog(new MatchLog(name, kills, deaths, damage, isMvp, deltaTrophy));
            System.out.println("Match added");
        } catch (IllegalValueException e) {
            System.out.println("Cannot accept a negative value");
            input.nextLine();
        } catch (Exception e) {
            System.out.println("Invalid input");
            input.nextLine();
        }
    }

    // EFFECTS: prints out some statistics
    private void viewStats() {
        printCommandList();
        String statInput = input.next();

        try {
            switch (statInput) {
                case "trophy":
                    System.out.println(log.totalTrophyGain());
                    break;
                case "character":
                    System.out.println("Select character");
                    String name = input.next();
                    System.out.println(log.characterStat(name));
                    break;
                case "star":
                    System.out.println(log.starPlayerPercentage());
                    break;
                default:
                    System.out.println("Command not found");
            }
        } catch (CharacterDoesNotExistException e) {
            System.out.println("Character does not exist");
        }
    }

    public void deleteLog() {
        int indexNumber;

        if (log.getSize() == 0) {
            System.out.println("There is no log yet");
        } else {
            try {
                System.out.println("Which log do you want to delete?");
                System.out.println("0 - " + (log.getSize() - 1));

                indexNumber = Integer.parseInt(input.next());

                System.out.println(log.getLog(indexNumber).logToString() + " selected!");

                System.out.println("Type \"confirm\" to delete entry");
                if (input.next().equals("confirm")) {
                    log.deleteLog(indexNumber);
                    System.out.println("Entry deleted!");
                }
            } catch (IndexOutOfBound e) {
                System.out.println("Index not found");
            }
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

    //EFFECTS: prints out the list of commands
    private void displayOptions() {
        System.out.println("=============================================");
        System.out.println("To view the past games -> \"view logs\"");
        System.out.println("To show statistics -> \"stats\"");
        System.out.println("To add a game -> \"add\"");
        System.out.println("To change an entry -> \"edit\"");
        System.out.println("Delete an entry -> \"del\"");
        System.out.println("quit application -> \"exit\"");
        System.out.println("=============================================");
    }
}
