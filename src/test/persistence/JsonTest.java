package persistence;

import model.MatchLog;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonTest {
    protected void checkLog(MatchLog log, String characterName, int kills, int deaths, int damage, boolean isMvp, int deltaTrophy) {
        assertEquals(damage, log.getDamage());
        assertEquals(kills, log.getKills());
        assertEquals(deaths, log.getDeaths());
        assertEquals(characterName, log.getCharacterName());
        assertEquals(isMvp, log.getIsMvp());
        assertEquals(deltaTrophy, log.getDeltaTrophy());
    }
}
