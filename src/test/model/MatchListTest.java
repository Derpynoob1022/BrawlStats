package model;

import static org.junit.jupiter.api.Assertions.*;

import model.exception.CharacterDoesNotExistException;
import model.exception.IllegalValueException;
import model.exception.IndexOutOfBound;
import model.exception.NoMatchingFields;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;


public class MatchListTest {
    private MatchList testList;
    private MatchLog testLog1;
    private MatchLog testLog2;
    private MatchLog testLog3;
    private MatchLog testLog4;
    private MatchLog testLog5;

    @BeforeEach
    public void setup() {
        testList = new MatchList("test");
        try {
            testLog1 = new MatchLog("Piper", 5, 4, 50000, true, 82);
            testLog2 = new MatchLog("Piper", 129, 7, 123415, false, 0);
            testLog3 = new MatchLog("Jerry", 3, 2, 67457, false, -27);
            testLog4 = new MatchLog("Jerry", 20, 0, 0, true, 102);
            testLog5 = new MatchLog("Will", 50, 20, 2345878, true, -32);
        } catch (IllegalValueException e) {
            fail("testLog(s) is initiated wrong");
        }

        assertEquals("test", testList.getName());
    }

    @Test
    public void addLogTest() {
        assertTrue(testList.isEmpty());
        try {
            testList.addLog(testLog1);
        } catch (IllegalValueException e) {
            fail("testLog(s) is invalid");
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
            fail("testLog(s) is invalid");
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
            fail("testLog(s) is invalid");
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
            fail("testLog(s) is invalid");
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
            fail("testLog(s) is invalid");
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
            testList.addLog(testLog2);
        } catch (IllegalValueException e) {
            fail("testLog(s) is invalid");
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
            fail("testLog(s) is invalid");
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
            fail("testLog(s) is invalid");
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
            fail("testLog(s) is invalid");
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
            fail("testLog(s) is invalid");
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
            fail("testLog(s) is invalid");
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
            fail("testLog(s) is invalid");
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
            fail("testLog(s) is invalid");
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
            fail("testLog(s) is invalid");
        }

        assertEquals(50.0, testList.winRateCalculator("Piper", testList.getList()));
        assertEquals(100.0, testList.winRateCalculator("Jerry", testList.getList()));
        assertEquals(0.0, testList.winRateCalculator("Will", testList.getList()));
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
            fail("testLog(s) is invalid");
        }

        assertEquals((float) 134 / 11, testList.killDeathRatioCalculator("Piper", testList.getList()));
        assertEquals((float) 23 / 2, testList.killDeathRatioCalculator("Jerry", testList.getList()));
    }

    @Test
    public void killDeathRatioCalculatorZeroTest() {
        assertTrue(testList.isEmpty());
        try {
            testList.addLog(testLog4);
        } catch (IllegalValueException e) {
            fail("testLog(s) is invalid");
        }
        assertEquals(20, testList.killDeathRatioCalculator("Jerry", testList.getList()));
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
            fail("testLog(s) is invalid");
        }

        assertEquals( 67457/2, testList.averageDamageCalculator("Jerry", testList.getList()));
        assertEquals( (50000 + 123415) / 2, testList.averageDamageCalculator("Piper", testList.getList()));
        assertEquals( 2345878, testList.averageDamageCalculator("Will", testList.getList()));
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
            fail("testLog(s) is invalid");
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
            fail("testLog(s) is invalid");
        }

        try {
            testList.characterStat("efefsef");
            fail("characterStatTest didn't throw an exception");
        } catch (CharacterDoesNotExistException e) {
        }
    }

    @Test
    public void characterStatLastFewTest() {
        assertTrue(testList.isEmpty());
        try {
            testList.addLog(testLog1);
            testList.addLog(testLog2);
            testList.addLog(testLog3);
            testList.addLog(testLog4);
            testList.addLog(testLog5);
        } catch (IllegalValueException e) {
            fail("testLog(s) is invalid");
        }

        try {
            assertEquals("Statistics for Will [Kill Death Ratio: 2.50 |" +
                    " Win rate: 0.00% | Average damage: 2345878 |" +
                    " Number of matches played: 1]",testList.characterStatLastFew(3).get(0));
            assertEquals("Statistics for Jerry [Kill Death Ratio: 11.50 |" +
                    " Win rate: 50.00% | Average damage: 33728 |" +
                    " Number of matches played: 2]",testList.characterStatLastFew(3).get(1));
        } catch (IndexOutOfBound e) {
            fail("Shouldn't have thrown an exception");
        }
    }

    @Test
    public void characterStatLastFewOverTest() {
        assertTrue(testList.isEmpty());
        try {
            testList.addLog(testLog1);
            testList.addLog(testLog2);
            testList.addLog(testLog3);
            testList.addLog(testLog4);
            testList.addLog(testLog5);
        } catch (IllegalValueException e) {
            fail("testLog(s) is invalid");
        }

        try {
            testList.characterStatLastFew(6);
            fail("Should have thrown an exception");
        } catch (IndexOutOfBound e) {
        }
    }

    @Test
    public void characterStatLastFewZeroTest() {
        assertTrue(testList.isEmpty());
        try {
            testList.addLog(testLog1);
            testList.addLog(testLog2);
            testList.addLog(testLog3);
            testList.addLog(testLog4);
            testList.addLog(testLog5);
        } catch (IllegalValueException e) {
            fail("testLog(s) is invalid");
        }

        try {
            assertEquals(new ArrayList<>(), testList.characterStatLastFew(0));
        } catch (IndexOutOfBound e) {
            fail("Shouldn't have thrown an exception");
        }
    }
}
