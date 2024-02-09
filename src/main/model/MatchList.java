package model;

import exception.DivideByZeroException;
import exception.IndexOutOfBound;
import exception.NoMatchingFields;

import java.util.ArrayList;

// represents the list of MatchLogs
public class MatchList {
    private final ArrayList<MatchLog> list;

    //EFFECTS: creates new match list with an empty arraylist
    public MatchList() {
        list = new ArrayList<>();
        //list.add(new MatchLog("jerry", 233,23,1,true,8)); for testing purposes
    }

    public ArrayList<MatchLog> getList() {
        return list;
    }

    public MatchLog getLog(int i) throws IndexOutOfBound {
        if (0 < i || i < (list.size() - 1)) {
            throw new IndexOutOfBound();
        } else {
            return list.get(i);
        }
    }

    public int getSize() {
        return list.size();
    }

    // MODIFIES: this
    // EFFECTS: adds a single log entry to the list
    public void addLog(MatchLog m) {
        this.list.add(m);
    }

    // MODIFIES: this
    // EFFECTS: removes the last log entry
    public void editList(int index, String field, String rep) throws IndexOutOfBound, NoMatchingFields {
        MatchLog editedLog = list.get(index);
        editedLog.editLog(field, rep);
    }

    // EFFECTS: produces the total amount of trophies gained or lost
    public String totalTrophyGain() {
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
    public String characterStat(String name) throws DivideByZeroException {

        return "Statistics for " + name + " "
                + "[Kill Death Ratio: " + String.format("%.2f", killDeathRatioCalculator(name)) + " | "
                + "Win rate: " + String.format("%.2f", winRateCalculator(name)) + "% | "
                + "Average damage: " + averageDamageCalculator(name) + "]";
    }

    // EFFECTS: calculates the win rate for a certain character name
    public float winRateCalculator(String name) throws DivideByZeroException {
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

        if (totalGames == 0) {
            throw new DivideByZeroException();
        } else {
            return 100 * numberWins / totalGames;
        }
    }

    // EFFECTS: calculates the total kill death ratio of a certain character
    public float killDeathRatioCalculator(String name) throws DivideByZeroException {
        int totalKills = 0;
        int totalDeaths = 0;

        for (MatchLog logs : list) {
            if (logs.getCharacterName().equals(name)) {
                totalKills = totalKills + logs.getKills();
                totalDeaths = totalDeaths + logs.getDeaths();
            }
        }

        if (totalDeaths == 0) {
            throw new DivideByZeroException();
        } else {
            return (float) totalKills / totalDeaths;
        }
    }

    // EFFECTS: calculates the average damage for a certain character name
    public int averageDamageCalculator(String name) throws DivideByZeroException {
        int numMatches = 0;
        int totalDamage = 0;

        for (MatchLog logs : list) {
            if (logs.getCharacterName().equals(name)) {
                totalDamage = totalDamage + logs.getDamage();
            }
            numMatches++;
        }

        if (numMatches == 0) {
            throw new DivideByZeroException();
        } else {
            return totalDamage / numMatches;
        }
    }
}
