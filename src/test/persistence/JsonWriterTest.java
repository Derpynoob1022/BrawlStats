package persistence;

import model.MatchList;
import model.MatchLog;
import model.exception.IllegalValueException;
import model.exception.IndexOutOfBound;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

// INSPIRED BY THE JSON SERIALIZATION DEMO
public class JsonWriterTest extends JsonTest {

    @Test
    void writerInvalidFileTest() {
        try {
            JsonWriter writer = new JsonWriter("./data/my\0illegal:fileName.json");
            writer.open();
            fail("IOException was expected");
        } catch (IOException e) {
        }
    }

    @Test
    void writerNoMatchesTest() {
        try {
            MatchList testList = new MatchList("test file");
            JsonWriter writer = new JsonWriter("./data/testFileWriterEmpty.json");
            writer.open();
            writer.write(testList);
            writer.close();

            JsonReader reader = new JsonReader("./data/testFileWriterEmpty.json");

            testList = reader.read();

            assertEquals("test file", testList.getName());
            assertEquals(0, testList.getSize());
        } catch (IOException | IllegalValueException e) {
            fail("Shouldn't have thrown an exception");
        }
    }

    @Test
    void normalWriterTest() {
        try {
            MatchList testList = new MatchList("test file");

            testList.addLog(new MatchLog("Piper", 5, 4, 50000, true, 82));
            testList.addLog(new MatchLog("Piper", 129, 7, 123415, false, 0));
            JsonWriter writer = new JsonWriter("./data/testFileWriterMatches.json");
            writer.open();
            writer.write(testList);
            writer.close();

            JsonReader reader = new JsonReader("./data/testFileWriterMatches.json");
            testList = reader.read();
            assertEquals("test file", testList.getName());
            List<MatchLog> matches = testList.getList();
            assertEquals(2, matches.size());
            checkLog(testList.getLog(0), "Piper", 5, 4, 50000, true, 82 );
            checkLog(testList.getLog(1), "Piper", 129, 7, 123415, false, 0);
        } catch (IndexOutOfBound | IOException | IllegalValueException e) {
            fail("Shouldn't have thrown an exception");
        }
    }
}