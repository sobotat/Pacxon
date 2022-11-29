package pacxon.listeners;

public interface HUDListener {

    void mapFillPercentageChanged(int percents);
    void levelChanged(int levelNumber);
    void gameWon();
    void gameOver();
    void livesChanged(int lives);
}
