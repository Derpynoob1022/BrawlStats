package model;

import static org.junit.jupiter.api.Assertions.*;

import model.exception.CharacterDoesNotExistException;
import model.exception.IllegalValueException;
import model.exception.IndexOutOfBound;
import model.exception.NoMatchingFields;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class MatchListTest {
    private MatchList testList;
    private MatchLog testLog1;
    private MatchLog testLog2;
    private MatchLog testLog3;
    private MatchLog testLog4;
    private MatchLog testLog5;

    @BeforeEach
    public void setup() {
        testList = new MatchList();
        try {
            testLog1 = new MatchLog("Piper", 5, 4, 50000, true, 82);
            testLog2 = new MatchLog("Piper", 129, 7, 123415, false, 0);
            testLog3 = new MatchLog("Jerry", 3, 2, 67457, false, -27);
            testLog4 = new MatchLog("Jerry", 20, 0, 0, true, 102);
            testLog5 = new MatchLog("Will", 50, 20, 2345878, true, -32);
        } catch (IllegalValueException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void addLogTest() {
        assertTrue(testList.isEmpty());
        try {
            testList.addLog(testLog1);
        } catch (IllegalValueException e) {
            throw new RuntimeException(e);
        }
        assertEquals(1, testList.getSize());
        try {
            assertEquals(testLog1, testList.getLog(0));
        } catch (IndexOutOfBound e) {
            fail("addLogTest threw a NotExpectedException");
        }
    }

    @Test
    public void addLogExceptionOverTest() {
        assertTrue(testList.isEmpty());
        try {
            testList.addLog(testLog1);
        } catch (IllegalValueException e) {
            throw new RuntimeException(e);
        }
        assertEquals(1, testList.getSize());
        try {
            assertEquals(testLog1, testList.getLog(2));
            fail("addLogTest threw a NotExpectedException");
        } catch (IndexOutOfBound e) {
        }
    }

    @Test
    public void addLogExceptionUnderTest() {
        assertTrue(testList.isEmpty());
        try {
            testList.addLog(testLog1);
        } catch (IllegalValueException e) {
            throw new RuntimeException(e);
        }
        assertEquals(1, testList.getSize());
        try {
            assertEquals(testLog1, testList.getLog(-1));
            fail("addLogTest threw a NotExpectedException");
        } catch (IndexOutOfBound e) {
        }
    }

    @Test
    public void addLogMultiTest() {
        assertTrue(testList.isEmpty());
        try {
            testList.addLog(testLog1);
            testList.addLog(testLog2);
            testList.addLog(testLog3);
        } catch (IllegalValueException e) {
            throw new RuntimeException(e);
        }
        assertEquals(3, testList.getSize());
        try {
            assertEquals(testLog1, testList.getLog(0));
            assertEquals(testLog2, testList.getLog(1));
            assertEquals(testLog3, testList.getLog(2));
        } catch (IndexOutOfBound e) {
            fail("addLogMultiTest threw a NotExpectedException");
        }
    }

    @Test
    public void deleteLogTest() {
        assertTrue(testList.isEmpty());
        try {
            testList.addLog(testLog1);
        } catch (IllegalValueException e) {
            throw new RuntimeException(e);
        }
        assertEquals(1, testList.getSize());
        try {
            testList.deleteLog(0);
        } catch (IndexOutOfBound e) {
            fail("deleteLogTest threw a NotExpectedException");
        }
        assertTrue(testList.getList().isEmpty());
    }

    @Test
    public void deleteLogZeroTest() {
        assertTrue(testList.isEmpty());
        try {
            testList.deleteLog(-1);
            fail("deleteLogTest didn't throw an exception");
        } catch (IndexOutOfBound e) {
            assertTrue(testList.isEmpty());
        }
    }

    @Test
    public void deleteLogZeroOverTest() {
        assertTrue(testList.isEmpty());
        try {
            testList.deleteLog(123);
            fail("deleteLogTest didn't throw an exception");
        } catch (IndexOutOfBound e) {
            assertTrue(testList.isEmpty());
        }
    }

    @Test
    public void deleteLogMultiTest() {
        assertTrue(testList.isEmpty());
        try {
            testList.addLog(testLog1);
        } catch (IllegalValueException e) {
            throw new RuntimeException(e);
        }
        try {
            testList.addLog(testLog2);
        } catch (IllegalValueException e) {
            throw new RuntimeException(e);
        }
        try {
            testList.deleteLog(0);
            assertEquals(testLog2, testList.getLog(0));
        } catch (IndexOutOfBound e) {
            fail("deleteLogMultiTest threw a NotExpectedException");
        }
        assertEquals(1, testList.getSize());
    }

    @Test
    public void editListTest() {
        assertTrue(testList.isEmpty());
        try {
            testList.addLog(testLog1);
        } catch (IllegalValueException e) {
            throw new RuntimeException(e);
        }
        try {
            testList.editList(0, "kills", "10");
            assertEquals(10, testList.getLog(0).getKills());
        } catch (IndexOutOfBound e) {
            fail("editListTest threw a NotExpectedException");
        } catch (NoMatchingFields e) {
            fail("editListTest threw a NotExpectedException");
        }
    }

    @Test
    public void editListExceptionTest() {
        assertTrue(testList.isEmpty());
        try {
            testList.addLog(testLog1);
        } catch (IllegalValueException e) {
            throw new RuntimeException(e);
        }
        try {
            testList.editList(0, "fjeisofji", "10fj");
            assertEquals(10, testList.getLog(0).getKills());
            fail("editListTest didn't throw an exception");
        } catch (IndexOutOfBound e) {
            fail("editListTest threw a NotExpectedException");
        } catch (NoMatchingFields e) {
        }
    }

    @Test
    public void editListMultiTest() {
        assertTrue(testList.isEmpty());
        try {
            testList.addLog(testLog1);
            testList.addLog(testLog2);
            testList.addLog(testLog3);
        } catch (IllegalValueException e) {
            throw new RuntimeException(e);
        }
        try {
            testList.editList(1, "damage", "3678");
            testList.editList(2, "name", "Test");
            assertEquals(testLog1, testList.getLog(0));
            assertEquals(3678, testList.getLog(1).getDamage());
            assertEquals("Test", testList.getLog(2).getCharacterName());
        } catch (IndexOutOfBound e) {
            fail("editListTest threw a NotExpectedException");
        } catch (NoMatchingFields e) {
            fail("editListTest threw a NotExpectedException");
        }
    }

    @Test
    public void totalTrophyGainTest() {
        assertTrue(testList.isEmpty());
        try {
            testList.addLog(testLog3);
        } catch (IllegalValueException e) {
            throw new RuntimeException(e);
        }

        assertEquals("-27", testList.totalTrophyGain());
    }

    @Test
    public void totalTrophyGainMultiTest() {
        assertTrue(testList.isEmpty());
        try {
            testList.addLog(testLog1);
            testList.addLog(testLog2);
            testList.addLog(testLog3);
            testList.addLog(testLog4);
            testList.addLog(testLog5);
        } catch (IllegalValueException e) {
            throw new RuntimeException(e);
        }

        assertEquals("+125", testList.totalTrophyGain());
    }

    @Test
    public void starPlayerPercentageTest() {
        assertTrue(testList.isEmpty());
        try {
            testList.addLog(testLog1);
            testList.addLog(testLog2);
        } catch (IllegalValueException e) {
            throw new RuntimeException(e);
        }
        assertEquals("50.00%", testList.starPlayerPercentage());
    }

    @Test
    public void starPlayerPercentageZeroTest() {
        assertTrue(testList.isEmpty());
        try {
            testList.addLog(testLog3);
            testList.addLog(testLog2);
        } catch (IllegalValueException e) {
            throw new RuntimeException(e);
        }
        assertEquals("0.00%", testList.starPlayerPercentage());
    }

    @Test
    public void starPlayerPercentageEmptyTest() {
        assertTrue(testList.isEmpty());
        assertEquals("NaN%", testList.starPlayerPercentage());
    }

    @Test
    public void winRateCalculatorTest() {
        assertTrue(testList.isEmpty());
        try {
            testList.addLog(testLog1);
            testList.addLog(testLog2);
            testList.addLog(testLog4);
            testList.addLog(testLog5);
        } catch (IllegalValueException e) {
            throw new RuntimeException(e);
        }

        assertEquals(50.0, testList.winRateCalculator("Piper"));
        assertEquals(100.0, testList.winRateCalculator("Jerry"));
        assertEquals(0.0, testList.winRateCalculator("Will"));
    }

    @Test
    public void killDeathRatioCalculatorTest() {
        assertTrue(testList.isEmpty());
        try {
            testList.addLog(testLog1);
            testList.addLog(testLog2);
            testList.addLog(testLog3);
            testList.addLog(testLog4);
            testList.addLog(testLog5);
        } catch (IllegalValueException e) {
            throw new RuntimeException(e);
        }

        assertEquals((float) 134 / 11, testList.killDeathRatioCalculator("Piper"));
        assertEquals((float) 23 / 2, testList.killDeathRatioCalculator("Jerry"));
    }

    @Test
    public void killDeathRatioCalculatorZeroTest() {
        assertTrue(testList.isEmpty());
        try {
            testList.addLog(testLog4);
        } catch (IllegalValueException e) {
            throw new RuntimeException(e);
        }
        assertEquals(20, testList.killDeathRatioCalculator("Jerry"));
    }

    @Test
    public void averageDamageCalculatorTest() {
        assertTrue(testList.isEmpty());
        try {
            testList.addLog(testLog1);
            testList.addLog(testLog2);
            testList.addLog(testLog3);
            testList.addLog(testLog4);
            testList.addLog(testLog5);
        } catch (IllegalValueException e) {
            throw new RuntimeException(e);
        }

        assertEquals( 67457/2, testList.averageDamageCalculator("Jerry"));
        assertEquals( (50000 + 123415) / 2, testList.averageDamageCalculator("Piper"));
        assertEquals( 2345878, testList.averageDamageCalculator("Will"));
    }

    @Test
    public void characterStatTest() {
        assertTrue(testList.isEmpty());
        try {
            testList.addLog(testLog1);
            testList.addLog(testLog2);
            testList.addLog(testLog3);
            testList.addLog(testLog4);
            testList.addLog(testLog5);
        } catch (IllegalValueException e) {
            throw new RuntimeException(e);
        }

        try {
            assertEquals("Statistics for Piper [Kill Death Ratio: 12.18 " +
                    "| Win rate: 50.00% | Average damage: 86707 | Number of matches played: 2]"
                    , testList.characterStat("Piper"));
        } catch (CharacterDoesNotExistException e) {
            fail("characterStatTest threw a NotExpectedException");
        }
    }

    @Test
    public void characterStatEmptyTest() {
        assertTrue(testList.isEmpty());
        try {
            testList.addLog(testLog1);
            testList.addLog(testLog2);
            testList.addLog(testLog3);
            testList.addLog(testLog4);
            testList.addLog(testLog5);
        } catch (IllegalValueException e) {
            throw new RuntimeException(e);
        }

        try {
            testList.characterStat("efefsef");
            fail("characterStatTest didn't throw an exception");
        } catch (CharacterDoesNotExistException e) {
        }
    }
}
