package ui;

import model.exception.CharacterDoesNotExistException;
import model.exception.IllegalValueException;
import model.exception.IndexOutOfBound;
import model.exception.NoMatchingFields;
import model.MatchList;
import model.MatchLog;
import persistence.JsonReader;
import persistence.JsonWriter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

// the user interface for the application
public class MatchStatsApp {
    private static final String JSON_DESTINATION = "./data/saved.json";
    private Scanner input;           // scanner input
    private MatchList log;           // the list of matches
    private JsonWriter jsonWriter;   // writing the json file
    private JsonReader jsonReader;   // reading the json file

    public MatchStatsApp() {
        runMatchStatsApp();
    }

    // MODIFIES: this
    // EFFECTS: processes user input
    // INSPIRED BY THE TELLER APP EXAMPLE!!!
    private void runMatchStatsApp() {
        boolean running = true;
        String command;

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
        System.out.println("Application closed");
    }

    // MODIFIES: this
    // EFFECTS: sets up the application
    private void setup() {
        log = new MatchList("Neo's matches");
        input = new Scanner(System.in);
        input.useDelimiter("\n");
        jsonWriter = new JsonWriter(JSON_DESTINATION);
        jsonReader = new JsonReader(JSON_DESTINATION);
    }

    // EFFECTS: processes user inputs
    private void processCommand(String s) {
        if ("view logs".equals(s)) {
            displayHistory();
        } else if ("stats".equals(s)) {
            viewStats();
        } else if ("add".equals(s)) {
            addLog();
        } else if ("edit".equals(s)) {
            editLog();
        } else if ("del".equals(s)) {
            deleteLog();
        } else if ("save".equals(s)) {
            saveMatches();
        } else if ("load".equals(s)) {
            loadMatches();
        } else {
            System.out.println("Command not found");
        }
    }


    // EFFECTS: saves the match list to file
    private void saveMatches() {
        try {
            jsonWriter.open();
            jsonWriter.write(log);
            jsonWriter.close();
            System.out.println("Saved " + log.getName() + " to " + JSON_DESTINATION);
        } catch (FileNotFoundException e) {
            System.out.println("Unable to write to file: " + JSON_DESTINATION);
        }
    }

    // MODIFIES: this
    // EFFECTS: loads the saved match list from file
    private void loadMatches() {
        try {
            log = jsonReader.read();
            System.out.println("Loaded " + log.getName() + " from " + JSON_DESTINATION);
        } catch (IllegalValueException | IOException e) {
            System.out.println("Unable to read from file: " + JSON_DESTINATION);
        }
    }

    // MODIFIES: log
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

    // MODIFIES: log
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
            System.out.println("Failed to add match, can not accept a negative value");
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
            if (statInput.equals("previous")) {
                System.out.println("select: 1 - " + log.getSize());
                log.characterStatLastFew(input.nextInt()).forEach(System.out::println);
            } else if (statInput.equals("character")) {
                System.out.println("Select character");
                System.out.println(log.characterStat(input.next()));
            } else if (statInput.equals("star")) {
                System.out.println(log.starPlayerPercentage());
            } else {
                System.out.println("Command not found");
            }
        } catch (CharacterDoesNotExistException e) {
            System.out.println("Character does not exist");
        } catch (IndexOutOfBound e) {
            System.out.println("Not enough matches to view that many previous matches");
        } catch (Exception e) {
            System.out.println("Invalid input");
            input.nextLine();
        }
    }

    // EFFECTS: deletes a log
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

    // EFFECTS: prints out the list of commands for stats
    private void printCommandList() {
        System.out.println("=============================================");
        System.out.println("What stat do you want to see?");
        System.out.println("Past x matches? -> enter \"previous\"");
        System.out.println("Total games with a character? -> enter \"character\"");
        System.out.println("Star player percentage? -> enter \"star\"");
        System.out.println("=============================================");
    }

    // EFFECTS: prints out the past matches
    private void displayHistory() {
        int startNum = Integer.max(0,log.getSize() - 25);

        try {
            for (int i = startNum; i < log.getSize(); i++) {
                System.out.println(log.getLog(i).logToString());
            }
        } catch (IndexOutOfBound e) {
            System.out.println("An error has occurred");
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
        System.out.println("Save current files -> \"save\"");
        System.out.println("Load saved files -> \"load\"");
        System.out.println("Quit application -> \"exit\"");
        System.out.println("=============================================");
    }
}
