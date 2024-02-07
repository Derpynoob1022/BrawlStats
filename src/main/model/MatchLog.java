package model;

// represents the log for a single match of the game having KDR, damage, and the type of character
public class MatchLog {
    private int damage;               // damage dealt in the match
    private int kills;                // number of kills
    private int deaths;               // number of deaths
    private float kdr;                // kill death ratio (kills/deaths)
    private String characterName;     // the name of the character
    private boolean isMvp;            // played the best in the match or no
    private int deltaTrophy;          // how many trophies gained or lost



    //REQUIRES: kills && deaths && damage >= 0
    //EFFECTS: creates log object with the given parameters
    public MatchLog(String characterName, int kills, int deaths, int damage, boolean isMvp, int deltaTrophy) {
        this.damage = damage;
        this.kills = kills;
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

    public float getKdr() {
        return this.kdr;
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


    //throw exception?
    //REQUIRES: string has to match be one of the fields
    //MODIFIES: this
    //EFFECTS: edit the log by indicating which field the user wants to replace
    public void editLog(String field, String replacement) {
        switch (field) {
            case "damage":
                this.damage = Integer.valueOf(replacement);
                break;
            case "kills":
                this.kills = Integer.valueOf(replacement);
                this.kdr = (float) kills / deaths;
                break;
            case "deaths":
                this.deaths = Integer.valueOf(replacement);
                this.kdr = (float) kills / deaths;
                break;
            case "name":
                this.characterName = replacement;
                break;
            case "mvp":
                this.isMvp = Boolean.valueOf(replacement);
                break;
            case "trophy":
                this.deltaTrophy = Integer.valueOf(replacement);
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + field);
        }
    }

    //MODIFIES: this
    //EFFECTS: produce a log entry text with all the information
    public String logToString() {
        String mvpToString;
        String separator = " | ";

        if (isMvp) {
            mvpToString = "Yes";
        } else {
            mvpToString = "No";
        }

        return "[Character: " + characterName + separator + "Kills: " + kills + separator
                + "Deaths: " + deaths + separator + "K/D ratio: " + String.format("%.2f", kdr)
                + separator + "Damage: " + damage + separator + "Star player: " + mvpToString + separator
                + "Trophy gain or loss: " + String.format("%+d%n", deltaTrophy) + "]";
    }
}
