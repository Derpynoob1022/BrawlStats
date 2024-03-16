package model;

import model.exception.IllegalValueException;
import model.exception.IndexOutOfBound;
import model.exception.NoMatchingFields;
import model.exception.CharacterDoesNotExistException;
import org.json.JSONArray;
import org.json.JSONObject;
import persistence.Writable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// represents the list of MatchLogs
public class MatchList implements Writable {
    private ArrayList<MatchLog> list;          // new list of logs
    private String userName;                   // name of the user

    //EFFECTS: creates new match list with an empty arraylist
    public MatchList(String name) {
        list = new ArrayList<>();
        userName = name;
    }

    public ArrayList<MatchLog> getList() {
        return list;
    }

    public MatchLog getLog(int i) throws IndexOutOfBound {
        if (i < 0 || i > (list.size() - 1)) {
            throw new IndexOutOfBound();
        } else {
            return list.get(i);
        }
    }

    public String getName() {
        return userName;
    }

    public int getSize() {
        return list.size();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    // MODIFIES: this
    // EFFECTS: deletes a log from the list of matches
    public void deleteLog(int i) throws IndexOutOfBound {
        if (i < 0 || i > (list.size() - 1)) {
            throw new IndexOutOfBound();
        } else {
            list.remove(i);
        }
    }

    // MODIFIES: this
    // EFFECTS: adds a single log entry to the list
    public void addLog(MatchLog m) throws IllegalValueException {
        this.list.add(m);
    }

    // MODIFIES: this
    // EFFECTS: edits one of prior logs
    public void editList(int index, String field, String rep) throws IndexOutOfBound, NoMatchingFields,
            NumberFormatException {
        MatchLog editedLog = list.get(index);
        editedLog.editLog(field, rep);
    }

    // EFFECTS: produces the total amount of trophies gained or lost
    public String totalTrophyGain(ArrayList<MatchLog> list) {
        int total = 0;
        for (MatchLog logs : list) {
            total = total + logs.getDeltaTrophy();
        }
        return String.format("%+d", total);
    }

    // EFFECTS: produces the % of star player games
    public String starPlayerPercentage() {
        float totalGames = 0;
        float starPlayerGames = 0;

        for (MatchLog logs : list) {
            if (logs.getIsMvp()) {
                starPlayerGames++;
            }
            totalGames++;
        }

        return String.format("%.2f", 100 * starPlayerGames / totalGames) + "%";
    }

    // EFFECTS: produces statistics for a certain character
    public String characterStat(String name) throws CharacterDoesNotExistException {
        int numberEntries = 0;

        for (MatchLog logs : list) {
            if (logs.getCharacterName().equals(name)) {
                numberEntries++;
            }
        }
        if (numberEntries == 0) {
            throw new CharacterDoesNotExistException();
        }

        return "Statistics for " + name + " "
                + "[Kill Death Ratio: " + String.format("%.2f", killDeathRatioCalculator(name, list)) + " | "
                + "Win rate: " + String.format("%.2f", winRateCalculator(name, list)) + "% | "
                + "Average damage: " + averageDamageCalculator(name, list) + " | "
                + "Number of matches played: " + numberEntries + "]";
    }

    // EFFECTS: produces statistics for the n few matches
    public ArrayList<String> characterStatLastFew(int numOfPastGames) throws IndexOutOfBound {
        ArrayList<String> result = new ArrayList<>();

        int startNum = list.size() - numOfPastGames;

        if (startNum < 0) {
            throw new IndexOutOfBound();
        }

        Map<String, ArrayList<MatchLog>> matchMap = new HashMap<>();

        for (int i = startNum; i < list.size(); i++) {
            MatchLog currMatch = list.get(i);
            if (!matchMap.containsKey(list.get(i).getCharacterName())) {
                ArrayList<MatchLog> matches = new ArrayList<>();
                matches.add(currMatch);
                matchMap.put(currMatch.getCharacterName(), matches);
            } else {
                ArrayList<MatchLog> matches = matchMap.get(currMatch.getCharacterName());
                matches.add(currMatch);
            }
        }

        for (String s : matchMap.keySet()) {
            ArrayList<MatchLog> currMatchList = matchMap.get(s);

            result.add(toString(s, currMatchList));
        }
        return result;
    }

    // EFFECTS: convert the character stats to a string
    public String toString(String key, ArrayList<MatchLog> currMatchList) {
        return "Statistics for " + key + " "
                + "[Kill Death Ratio: " + String.format("%.2f", killDeathRatioCalculator(key, currMatchList)) + " | "
                + "Win rate: " + String.format("%.2f", winRateCalculator(key, currMatchList)) + "% | "
                + "Average damage: " + averageDamageCalculator(key, currMatchList) + " | "
                + "Number of matches played: " + currMatchList.size() + " | " + "Delta trophy: "
                + totalTrophyGain(currMatchList) + "]";
    }

    // EFFECTS: calculates the win rate for a certain character name
    public float winRateCalculator(String name, ArrayList<MatchLog> list) {
        float numberWins = 0;
        float totalGames = 0;

        for (MatchLog logs : list) {
            if (logs.getCharacterName().equals(name)) {
                if (logs.getDeltaTrophy() > 0) {
                    numberWins++;
                }
                totalGames++;
            }
        }
        return 100 * numberWins / totalGames;
    }

    // EFFECTS: calculates the total kill death ratio of a certain character
    public float killDeathRatioCalculator(String name, ArrayList<MatchLog> list) {
        int totalKills = 0;
        int totalDeaths = 0;

        for (MatchLog logs : list) {
            if (logs.getCharacterName().equals(name)) {
                totalKills = totalKills + logs.getKills();
                totalDeaths = totalDeaths + logs.getDeaths();
            }
        }

        if (totalDeaths == 0) {
            return (float) totalKills;
        } else {
            return (float) totalKills / totalDeaths;
        }
    }

    // EFFECTS: calculates the average damage for a certain character name
    public int averageDamageCalculator(String name, ArrayList<MatchLog> list) {
        int numMatches = 0;
        int totalDamage = 0;

        for (MatchLog logs : list) {
            if (logs.getCharacterName().equals(name)) {
                totalDamage = totalDamage + logs.getDamage();
                numMatches++;
            }
        }
        return totalDamage / numMatches;
    }


    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("user", userName);
        json.put("stats", logsToJson());
        return json;
    }

    // EFFECTS: returns matches in this log as a JSON array
    private JSONArray logsToJson() {
        JSONArray jsonArray = new JSONArray();
        for (MatchLog l : list) {
            jsonArray.put(l.toJson());
        }
        return jsonArray;
    }
}
