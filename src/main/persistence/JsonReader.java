package persistence;

import model.MatchList;
import model.MatchLog;
import model.exception.IllegalValueException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

// HEAVILY INSPIRED BY THE JSON SERIALIZATION DEMO!!!
// A reader that reads matches from jSON data stored in a file
public class JsonReader {
    private String source;

    //EFFECTS: constructs reader to read the file
    public JsonReader(String source) {
        this.source = source;
    }

    // EFFECTS: reads the list of matches from file and returns it;
    // throws IOException if an error occurs reading data from file
    public MatchList read() throws IOException, IllegalValueException  {
        String jsonData = readFile(source);
        JSONObject jsonObject = new JSONObject(jsonData);
        return parseMatches(jsonObject);
    }

    // EFFECTS: reads source file as string and returns it
    private String readFile(String source) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(source), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s));
        }

        return contentBuilder.toString();
    }

    // EFFECTS: parses MatchList from JSON object and returns it
    private MatchList parseMatches(JSONObject jsonObject) throws IllegalValueException {
        String name = jsonObject.getString("user");
        MatchList ml = new MatchList(name);
        addMatchList(ml, jsonObject);
        return ml;
    }

    // MODIFIES: ml
    // EFFECTS: parses MatchLogs from JSON object and adds them to MatchList
    private void addMatchList(MatchList ml, JSONObject jsonObject) throws IllegalValueException {
        JSONArray jsonArray = jsonObject.getJSONArray("stats");
        for (Object json : jsonArray) {
            JSONObject nextThingy = (JSONObject) json;
            addMatchLogs(ml, nextThingy);
        }
    }

    // MODIFIES: ml
    // EFFECTS: parses MatchLog from JSON object and adds it to MatchList
    private void addMatchLogs(MatchList ml, JSONObject jsonObject) throws IllegalValueException {
        String characterName = jsonObject.getString("name");
        int kills = jsonObject.getInt("kills");
        int deaths = jsonObject.getInt("deaths");
        int damage = jsonObject.getInt("damage");
        boolean isMvp = jsonObject.getBoolean("isMvp");
        int deltaTrophy = jsonObject.getInt("deltaTrophy");

        MatchLog log = new MatchLog(characterName, kills, deaths, damage, isMvp, deltaTrophy);
        ml.addLog(log);
    }
}
