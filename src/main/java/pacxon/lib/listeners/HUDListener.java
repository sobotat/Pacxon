package pacxon.lib.listeners;

public interface HUDListener {

    void mapFillPercentageChanged(int percents);
    void levelChanged(int levelNumber);
    void levelWon();
    void gameWon();
    void gameOver();
    void livesChanged(int lives);
}
