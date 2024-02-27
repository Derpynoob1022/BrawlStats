package model;

import model.exception.IllegalValueException;
import model.exception.NoMatchingFields;
import org.json.JSONObject;
import persistence.Writable;

// represents the log for a single match of the game having KDR, damage, and the type of character
public class MatchLog implements Writable {
    private int damage;               // damage dealt in the match
    private int kills;                // number of kills
    private int deaths;               // number of deaths
    private float kdr;                // kill death ratio (kills/deaths)
    private String characterName;     // the name of the character
    private boolean isMvp;            // played the best in the match or no
    private int deltaTrophy;          // how many trophies gained or lost


    //REQUIRES: kills && deaths && damage >= 0
    //EFFECTS: creates log object with the given parameters
    public MatchLog(String characterName, int kills, int deaths, int damage, boolean isMvp, int deltaTrophy)
            throws IllegalValueException {

        if (damage < 0) {
            throw new IllegalValueException();
        }
        this.damage = damage;

        if (kills < 0) {
            throw new IllegalValueException();
        }
        this.kills = kills;

        if (deaths < 0) {
            throw new IllegalValueException();
        }
        this.deaths = deaths;
        this.kdr = (float) kills / deaths;
        this.characterName = characterName;
        this.isMvp = isMvp;
        this.deltaTrophy = deltaTrophy;
    }

    public int getDamage() {
        return this.damage;
    }

    public int getKills() {
        return this.kills;
    }

    public int getDeaths() {
        return this.deaths;
    }

    public String getCharacterName() {
        return this.characterName;
    }

    public boolean getIsMvp() {
        return this.isMvp;
    }

    public int getDeltaTrophy() {
        return this.deltaTrophy;
    }


    //MODIFIES: this
    //EFFECTS: edit the log by indicating which field the user wants to replace
    public void editLog(String field, String replacement) throws NoMatchingFields {
        switch (field) {
            case "damage":
                this.damage = Integer.parseInt(replacement);
                break;
            case "kills":
                this.kills = Integer.parseInt(replacement);
                this.kdr = (float) kills / deaths;
                break;
            case "deaths":
                this.deaths = Integer.parseInt(replacement);
                this.kdr = (float) kills / deaths;
                break;
            case "name":
                this.characterName = replacement;
                break;
            case "mvp":
                this.isMvp = Boolean.parseBoolean(replacement);
                break;
            case "trophy":
                this.deltaTrophy = Integer.parseInt(replacement);
                break;
            default: throw new NoMatchingFields();
        }
    }

    //MODIFIES: this
    //EFFECTS: produce a log entry text with all the information
    public String logToString() {
        String separator = " | ";

        return "[Character: " + characterName + separator + "Kills: " + kills + separator
                + "Deaths: " + deaths + separator + "K/D ratio: " + String.format("%.2f", kdr)
                + separator + "Damage: " + damage + separator + "Star player: " + isMvp + separator
                + "Trophy gain: " + String.format("%+d", deltaTrophy) + "]";
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("name", characterName);
        json.put("kills", kills);
        json.put("deaths", deaths);
        json.put("damage", damage);
        json.put("isMvp", isMvp);
        json.put("deltaTrophy", deltaTrophy);
        return json;
    }

}
