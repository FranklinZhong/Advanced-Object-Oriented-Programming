import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
/**
 * The INumberleModel interface defines the contract for a Numberle game model.
 * It provides methods to initialize the game, process user input, check game status,
 * access game data, and manage game state.
 * Implementations of this interface handle the game logic and state management for playing Numberle.
 */
public interface INumberleModel {
    int MAX_ATTEMPTS = 6;
    void initialize();
    int processInput(String input);
    boolean isGameOver();
    boolean isGameWon();
    String getTargetNumber();
    StringBuilder getCurrentGuess();
    int getRemainingAttempts();
    void startNewGame();
    int evaluateExpression(String expression);
    void setRandom(boolean random);
    List<Character> getUnusedCharacters();
    ArrayList<String> getColors();
    Map<String, Set<Character>> getMap();
    String getColorCharacters();
}
