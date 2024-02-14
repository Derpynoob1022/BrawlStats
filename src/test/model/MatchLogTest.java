package model;

import model.exception.IllegalValueException;
import model.exception.NoMatchingFields;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MatchLogTest {
    private MatchLog testLog;
    private MatchLog exceptionTestLog;

    @BeforeEach
    public void setup() {
        try {
            testLog = new MatchLog("Piper", 5, 4, 50000, true, 8);
        } catch (IllegalValueException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testConstructorNegKills() {
        try {
            testLog = new MatchLog("Piper", -5, 4, 50000, true, 8);
            fail("Constructor did not throw exception");
        } catch (IllegalValueException e) {
        }
    }

    @Test
    void testConstructorNegDamage() {
        try {
            testLog = new MatchLog("Piper", 5, 4, -50000, true, 8);
            fail("Constructor did not throw exception");
        } catch (IllegalValueException e) {
        }
    }

    @Test
    void testConstructorNegDeaths() {
        try {
            testLog = new MatchLog("Piper", 5, -4, 50000, true, 8);
            fail("Constructor did not throw exception");
        } catch (IllegalValueException e) {
        }
    }

    @Test
    public void editLogTest() {
        try {
            testLog.editLog("damage", "3500");
            assertEquals(3500, testLog.getDamage());
            testLog.editLog("kills", "135");
            assertEquals(135, testLog.getKills());
            testLog.editLog("deaths", "19");
            assertEquals(19, testLog.getDeaths());
            testLog.editLog("name", "Daren");
            assertEquals("Daren", testLog.getCharacterName());
            testLog.editLog("mvp", "false");
            assertFalse(testLog.getIsMvp());
            testLog.editLog("trophy", "-10");
            assertEquals(-10, testLog.getDeltaTrophy());
        } catch (NoMatchingFields e) {
            fail("editLogTest threw a NotExpectedException");
        }
    }

    @Test
    public void editLogExceptionFieldTest() {
        try {
            testLog.editLog("fefsfa", "Daren");
            fail("editLogTest threw a NotExpectedException");
        } catch (NoMatchingFields e) {
            assertEquals(5, testLog.getKills());
        } catch (NumberFormatException e) {
            assertEquals(5, testLog.getKills());
        }
    }

    @Test
    public void editLogExceptionTest() {
        try {
            testLog.editLog("damage", "fueufe");
            fail("editLogExceptionTest did not throw an exception");
        } catch (Exception e) {
        }

        try {
            testLog.editLog("kills", "fesffs");
            fail("editLogExceptionTest did not throw an exception");
        } catch (Exception e) {
        }

        try {
            testLog.editLog("deaths", "fesff2");
            fail("editLogExceptionTest did not throw an exception");
        } catch (Exception e) {
        }

        try {
            testLog.editLog("mvp", "fesffsfef");
            assertFalse(testLog.getIsMvp());
        } catch (Exception e) {
            fail("editLogExceptionTest threw a NotExpectedException");
        }
        try {
            testLog.editLog("trophy", "-fesfsfa");
            fail("editLogExceptionTest did not throw an exception");
        } catch (Exception e) {
        }
    }


    @Test
    public void logToStringTest() {
        assertEquals("[Character: Piper | Kills: 5 | Deaths: 4 " +
                "| K/D ratio: 1.25 | Damage: 50000 | Star player: true | Trophy gain: +8]", testLog.logToString());
    }
}