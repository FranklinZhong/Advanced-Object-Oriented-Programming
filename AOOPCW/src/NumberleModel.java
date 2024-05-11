import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * The NumberleModel class implements the INumberleModel interface to provide the logic and state management for playing the Numberle game.
 * It generates target equations, processes user input, evaluates expressions, and determines game status.
 * This class maintains the target equation, current guess, remaining attempts, and game outcome.
 * It also handles the colors associated with the current guess and character mapping for feedback.
 */
public class NumberleModel extends Observable implements INumberleModel {
    private String targetNumber;
    private StringBuilder currentGuess;
    private int remainingAttempts;
    private boolean gameWon;
    private boolean isRandom=true; // Flag3
    private final ArrayList<String> colors = new ArrayList<>();
    private final Map<String, Set<Character>> map = new HashMap<>();
    private final HashMap<String, HashSet<Character>> colorCharacters = new HashMap<>();

    /**
     * Generates a target equation by reading from a file containing equations.
     * If the file is unavailable or empty, a default equation "1+2+3=6" is returned.
     * If random selection is enabled and the list is not empty, a random equation from the list is returned.
     * Also follow the requirement of flag3
     * @return The generated target equation as a String.
     */
    private String generateTargetEquation() {
        // Initialize a list to store equations
        List<String> equations = new ArrayList<>();
        // Specify the file name containing equations
        String fileName = "equations.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            // Read equations line by line from the file and add them to the list
            while ((line = reader.readLine()) != null) {
                equations.add(line);
            }
        } catch (IOException e) {
            // Handle file reading exceptions
            e.printStackTrace();
        }
        // Check if the list of equations is not empty and random selection is enabled. Flag3
        if (!equations.isEmpty()&&isRandom) {
            // Generate a random index within the range of the list
            Random rand = new Random();
            // Return the equation at the randomly selected index
            return equations.get(rand.nextInt(equations.size()));
        } else {
            // Return a default equation if either the list is empty or random selection is disabled
            return "1+2+3=6";
        }
    }

    /**
     * Initializes the game by setting up necessary parameters and generating the target number.
     * This method ensures that the maximum number of attempts is greater than 0 before proceeding.
     * It generates a random target number, initializes the current guess, sets the remaining attempts,
     * sets the game state to not won, notifies observers of the game state change, generates a target equation,
     * and initializes color sets for characters.
     *
     * @ Invariants MAX_ATTEMPTS;
     * @ assignable targetNumber, currentGuess, remainingAttempts, gameWon;
     * @ ensures targetNumber != null;
     * @ ensures remainingAttempts = MAX_ATTEMPTS;
     * @ ensures !gameWon;
     * @ ensures currentGuess.length() == 0
     * @ ensures for all String color; color.equals ( " Green ") || color.equals("Orange") || color.equals("Gray");
     * @ ensures  colorCharacters.get(color) != null && colorCharacters.get(color).isEmpty())
     */
    @Override
    public void initialize() {
        assert MAX_ATTEMPTS > 0; // Precondition: Ensure maximum attempts are valid
        Random rand = new Random();
        targetNumber = Integer.toString(rand.nextInt(10000000)); // Generate random target number
        currentGuess = new StringBuilder(""); // Initialize current guess
        remainingAttempts = MAX_ATTEMPTS;  // Set remaining attempts
        gameWon = false; // Set game state to not won
        setChanged(); // Notify observers of game state change
        notifyObservers();
        targetNumber = generateTargetEquation(); // Generate target equation
        System.out.println(targetNumber); // Print target number (for debugging purposes(flag2))
        assert targetNumber != null; // Postcondition: Ensure target number is generated
        // Initialize color sets for characters
        colorCharacters.put("Green",new HashSet<Character>());
        colorCharacters.put("Orange",new HashSet<Character>());
        colorCharacters.put("Gray",new HashSet<Character>());
    }


    /**
     * This method processes the user input for a game. It evaluates the input against a target number,
     * provides feedback on the correctness of the input, updates game state, and notifies observers.
     * @param input The user input to be processed.
     * @return An integer indicating the result of processing:
     *         - 0: Successful processing.
     *         - 1: Invalid input length.
     *         - 5: Game over.
     * ----------------------------------------------------------------------------------------------------------------
     * @ requires input != null && input.length() == 7;
     * @ assignable colors, remainingAttempts, gameWon, map;
     * @ ensures result == 0 || result == 1 || result == 5;
     * @ ensures \result == 0 ==> \notifiedObservers("Try Again");
     * @ ensures \result == 1 ==> (\old(input) == null || \old(input.length()) != 7)
     * @ ensures \result == 5 ==> \isGameOver()
     * @ ensures (input.equals(targetNumber)) ==> gameWon
     * @ ensures gameWon ==> \notifiedObservers("Game Won");
     * @ ensures isGameOver() ==> \notifiedObservers("Game Over");
     * @ ensures !gameWon ==> (\forall int i; i >= 0 && i < input.length();
     *          (input.charAt(i) == targetNumber.charAt(i) ==> colors.get(i).equals("0")) &&
     *          (targetNumber.contains(String.valueOf(input.charAt(i))) && input.charAt(i) != targetNumber.charAt(i) ==> colors.get(i).equals("1")) &&
     *          (!targetNumber.contains(String.valueOf(input.charAt(i))) ==> colors.get(i).equals("2")))
    @*/
    @Override
    public int processInput(String input) {
    	int result;// Variable to store the result of evaluating the input.
        assert input != null && input.length() == 7 : "Invalid input length"; // Assert input validity.
        colors.clear(); // Clear the list of colors.
        // Check for invalid input length and notify observers if found.
        if (input == null || input.length() != 7) {
            setChanged();
            notifyObservers("Invalid Input");
            return 1;
        }
        // Evaluate the input expression and handle any errors.
        if ((result=evaluateExpression(input))!=0) {
            return result;
        }
        remainingAttempts--; // Decrement the remaining attempts.
        // Check if the input matches the target number.
        if (input.equals(targetNumber)) {
            gameWon = true; // Set gameWon flag to true.
            // Populate colors list with "0" indicating correct positions.
            for(int i = 0; i < input.length(); i++) {
                colors.add("0");
            }
            System.out.println();
        } else {
            // Process input characters and provide feedback based on correctness.
            for (int i = 0; i < input.length(); i++) {
                char c = input.charAt(i);
                if (i < targetNumber.length() && c == targetNumber.charAt(i)) {
                    colors.add("0"); // Add "0" to colors list for correct position.
                    map.computeIfAbsent("Green", k -> new HashSet<>()).add(c);  // Update map with correct position.
                    colorCharacters.get("Green").add(c);// Add character to Green set.
                    colorCharacters.get("Orange").remove(c);// Remove character from Orange set.
                    colorCharacters.get("Gray").remove(c);// Remove character from Gray set.
                    System.out.println("Green: "+ c + " is in right position, ");
                } else if (targetNumber.contains(String.valueOf(c))) {
                    colors.add("1");// Add "1" to colors list for wrong position.
                    map.computeIfAbsent("Orange", k -> new HashSet<>()).add(c); // Update map with wrong position.
                    if(!colorCharacters.get("Green").contains(c)) {
	                    colorCharacters.get("Orange").add(c);
	                    colorCharacters.get("Gray").remove(c);
                    }
                    System.out.println("Orange: " + c + " is in wrong position, ");

                } else {
                    colors.add("2");// Add "2" to colors list for not in the equation.
                    map.computeIfAbsent("Gray", k -> new HashSet<>()).add(c); // Update map with not in equation.
                    if(!colorCharacters.get("Green").contains(c)&&!colorCharacters.get("Orange").contains(c)) {
                    	colorCharacters.get("Gray").add(c);
                    }
                    System.out.println("Gray: " + c + " is not in the equation, ");
                }
            }
        }
        System.out.println();
        // Check if the game is over and notify observers accordingly.
        if (isGameOver()) {
            setChanged();
            notifyObservers(gameWon ? "Game Won" : "Game Over");
            map.clear();// Clear the map
            return 5;// Return 5 for game over.
        } else {
            setChanged();
            notifyObservers("Try Again");
        }
        return 0;// Return 0 for successful processing.
    }


    /**
     * This method evaluates an expression in the form of "leftSide = rightSide" and checks if the two sides are equal.
     * If the expression is valid and the two sides are equal within a small tolerance, it returns 0.
     * If the expression is invalid (e.g., missing symbols), it returns 3.
     * If the two sides are not equal, it returns 4.
     * If the expression does not contain an equal sign, it returns 2.
     *
     * @param expression The expression to be evaluated, in the format "leftSide = rightSide".
     * @return 0 if the sides are equal,
     *         3 if the expression is invalid,
     *         4 if the sides are not equal,
     *         2 if no equal sign is present.
     *
     * -----------------------------------------------------------------------------------------------------------------
     * @ ensures \result == 0 || \result == 2 || \result == 3 || \result == 4;
     * @ ensures \result == 2 ==> \notifiedObservers("No Equal");
     * @ ensures \result == 3 ==> \notifiedObservers("Missing Symbols");
     * @ ensures \result == 4 ==> \notifiedObservers("Not Equal");
     *
     */
    @Override
    public int evaluateExpression(String expression) {
        assert expression != null : "Expression cannot be null";
        // Split the expression into two parts based on the equal sign
        String[] parts = expression.split("=");
        // Check if the expression contains an equal sign
        if (parts.length != 2) {
            // Notify observers and return 2 if no equal sign is found
            setChanged();
            notifyObservers("No Equal");
            return 2;
        }
        // Check if the expression is valid (e.g., contains required symbols)
        if (!isExpressionValid(expression)) {
            // Notify observers and return 3 if the expression is invalid
            setChanged();
            notifyObservers("Missing Symbols");
            return 3;
        }
        // Evaluate the numerical value of each side of the expression
        if (Math.abs( evaluateSide(parts[0]) - evaluateSide(parts[1]) ) < 0.0001) {
            // Return 0 if the sides are equal within a tolerance
            return 0;
        } else {
            // Notify observers and return 4 if the sides are not equal
            setChanged();
            notifyObservers("Not Equal");
            return 4;
        }
    }


    /**
     * Checks if a mathematical expression is valid.
     *
     * @param expression The mathematical expression to be checked.
     * @return true if the expression is valid, false otherwise.
     * @throws AssertionError If the expression is null.
     */
    private static boolean isExpressionValid(String expression) {
        // Ensure the expression is not null
        assert expression != null : "Expression cannot be null";
        // Regular expression pattern to match a valid mathematical expression
        String regex = "^[+-]?\\d+([+\\-*/]\\d+)*=[+-]?\\d+([+\\-*/]\\d+)*$";
        // Check if the expression matches the regex pattern
        return Pattern.matches(regex, expression);
    }

    /**
     * This method evaluates a mathematical expression represented as a string,
     * containing numbers, addition (+), subtraction (-), multiplication (*),
     * and division (/) operators. It parses the string, calculates the result,
     * and returns the evaluated value.
     *
     * @param side The mathematical expression to evaluate.
     * @return The result of the evaluation as a double value.
     * @throws IllegalArgumentException If the input expression contains unexpected operators.
     */
    private static double evaluateSide(String side) {
        assert side != null : "Side cannot be null";
        // Initialize lists to store numbers and operators
        List<Double> numbers = new ArrayList<>();
        List<Character> operators = new ArrayList<>();
        // Temporary string to store a number during parsing
        String tempNum = "";
        // Flag to track if parsing starts with an operator
        boolean isStart = true;
        // Parse the input string
        for (char ch : side.toCharArray()) {
            if (ch == '+' || ch == '-' || ch == '*' || ch == '/') {
                // Handle negative numbers at the beginning of the expression
                if (isStart && (ch == '+' || ch == '-')) {
                    tempNum += ch;
                    isStart = false;
                    continue;
                }
                // Add parsed number to the list and reset temporary number string
                if (!tempNum.isEmpty()) {
                    numbers.add(Double.parseDouble(tempNum));
                    tempNum = "";
                }
                // Add operator to the list
                operators.add(ch);
            } else {
                // Append digits to temporary number string
                tempNum += ch;
            }
            isStart = false;
        }
        // Add the last parsed number to the list
        if (!tempNum.isEmpty()) {
            numbers.add(Double.parseDouble(tempNum));
        }
        // Evaluate multiplication and division operations first
        for (int i = 0; i < operators.size(); i++) {
            char operator = operators.get(i);
            if (operator == '*' || operator == '/') {
                double result = operator == '*' ? numbers.get(i) * numbers.get(i + 1) : numbers.get(i) / numbers.get(i + 1);
                numbers.set(i, result);
                numbers.remove(i + 1);
                operators.remove(i);
                i--;
            }
        }
        // Evaluate addition and subtraction operations
        double result = numbers.get(0);
        for (int i = 0; i < operators.size(); i++) {
            double number = numbers.get(i + 1);
            switch (operators.get(i)) {
                case '+':
                    result += number;
                    break;
                case '-':
                    result -= number;
                    break;
                default:
                    throw new IllegalArgumentException("Unexpected operator: " + operators.get(i));
            }
        }
        return result;
    }

    /**
     * Retrieves a list of unused characters from the character map.
     *
     * @return List of unused characters
     */
    @Override
    public List<Character> getUnusedCharacters() {
        // Initialize a list of characters including digits and arithmetic operators
        List<Character> unusedCharacters = new ArrayList<>(Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '-', '*', '/'));
        // Iterate through each set of used characters in the map
        for (Set<Character> usedChars : map.values()) {
            // Remove used characters from the list of unused characters
            unusedCharacters.removeAll(usedChars);
        }
        return unusedCharacters;
    }

    /**
     * Retrieves a formatted string containing color names and associated characters.
     *
     * @return Formatted string with color names and characters
     */
    @Override
    public String getColorCharacters() {
        // Initialize an empty string to store color-character pairs
        String colors="";
        // Iterate through each entry in the colorCharacters map
        for (Map.Entry<String, HashSet<Character>> entry : colorCharacters.entrySet()) {
            // Get the color name and associated character set
            String color = entry.getKey();
            HashSet<Character> characters = entry.getValue();
            // Append color name and characters to the colors string
            colors += color+": " + characters+"\n";
        }
    	return colors;
    }

    @Override
    public ArrayList<String> getColors(){
        return colors;
    }

    @Override
    public Map<String, Set<Character>> getMap() {
        return map;
    }

    @Override
    public void setRandom(boolean random) {
    	this.isRandom=random;
    }

    @Override
    public boolean isGameOver() {
        return remainingAttempts <= 0 || gameWon;
    }

    @Override
    public boolean isGameWon() {
        return gameWon;
    }

    @Override
    public String getTargetNumber() {
        return targetNumber;
    }

    @Override
    public StringBuilder getCurrentGuess() {
        return currentGuess;
    }

    @Override
    public int getRemainingAttempts() {
        return remainingAttempts;
    }

    @Override
    public void startNewGame() {
        initialize();
    }
}

