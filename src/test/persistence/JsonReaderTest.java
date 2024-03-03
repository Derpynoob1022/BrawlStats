package persistence;

import model.MatchList;
import model.exception.IllegalValueException;
import model.exception.IndexOutOfBound;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

// INSPIRED BY THE JSON SERIALIZATION DEMO
public class JsonReaderTest extends JsonTest {
    private JsonReader testReader;

    @Test
    void normalLogTest() {
        testReader = new JsonReader("./data/testFile.json");
        try {
            MatchList readList = testReader.read();

            assertEquals("Neo's matches", readList.getName());

            assertEquals(2, readList.getSize());

            checkLog(readList.getLog(0), "Jerry", 2, 10, 100, false, -8);

            checkLog(readList.getLog(1), "Marcus", 9, 9, 0, true, 100);

        } catch (IllegalValueException e) {
            fail("Should not have thrown exception");
        } catch (IOException e) {
            fail("Should not have thrown exception");
        } catch (IndexOutOfBound e) {
            fail("Should not have thrown exception");
        }
    }

    @Test
    void noFileTest() {
        try {
            testReader = new JsonReader("./data/testFileMisingFields.json");
            testReader.read();
            fail("Should have thrown exception");
        } catch (IllegalValueException e) {
            fail("Should not have thrown exception");
        } catch (IOException e) {
        }
    }

    @Test
    void noMatchesTest() {
        testReader = new JsonReader("./data/testFileNoMatches.json");
        try {
            assertEquals(0, testReader.read().getSize());
        } catch (IllegalValueException | IOException e) {
            fail("Should not have thrown exception");
        }
    }

    @Test
    void IllegalFieldsTest() {
        testReader = new JsonReader("./data/testFileIllegalFields.json");
        try {
            testReader.read();
            fail("Should have thrown exception");
        } catch (IllegalValueException e) {
        } catch (IOException e) {
            fail("Should not have thrown exception");
        }
    }
}
