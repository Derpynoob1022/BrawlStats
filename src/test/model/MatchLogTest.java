package model;

import org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MatchLogTest {
    private MatchLog testLog;

    @BeforeEach
    public void setup() {
        testLog = new MatchLog("Piper", 5, 4, 50000, true, 8);
    }

    @Test
    public void editLogTest() {
        //stub
    }
}