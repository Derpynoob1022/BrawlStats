package model;

import org.junit.jupiter.api.Assertions.*;
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
        testLog1 = new MatchLog("Piper", 5, 4, 50000, true, 82);
        testLog2 = new MatchLog("Piper", 129, 7, 123415, false, 43);
        testLog3 = new MatchLog("Jerry", 322, 2, 67457, false, 27);
        testLog4 = new MatchLog("Jerry", 50, 0, 0, true, 102);
        testLog5 = new MatchLog("Will", 50, 20, 2345878, true, 14);
    }

    @Test
    public void addLogTest() {
        //stub
    }

    @Test
    public void addLogMultiTest() {
        //stub
    }

    @Test
    public void editListTest() {
        //stub
    }

    @Test
    public void totalTrophyGainTest() {

    }

    @Test
    public void totalTrophyGainMultiTest() {

    }

    @Test
    public void starPlayerPercentageTest() {

    }

    @Test
    public void winRateCalculatorTest() {

    }

    @Test
    public void killDeathRatioCalculatorTest() {

    }

    @Test
    public void averageDamageCalculatorTest() {

    }


}
