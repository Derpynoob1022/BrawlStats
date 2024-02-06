package model;

import java.util.ArrayList;

// represents the list of MatchLogs
public class MatchList {
    private final ArrayList<MatchLog> list;

    //EFFECTS: creates new match list with an empty arraylist
    public MatchList() {
        list = new ArrayList<>();
    }

    public ArrayList<MatchLog> getList() {
        return list;
    }

    public MatchLog getLog(int i) {
        return list.get(i);
    }

    public int logSize() {
        return list.size();
    }

    //MODIFIES: this
    //EFFECTS: adds a single log entry to the list
    public void addLog(MatchLog m) {
        this.list.add(m);
    }

    //MODIFIES: this
    //EFFECTS: removes the last log entry
    public void removeLastLog() {
        if (logSize() != 0) {
            this.list.remove(logSize() - 1);
        }
    }
}
