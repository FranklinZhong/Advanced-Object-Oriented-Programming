import java.util.Scanner;

/**
 * The CLIApp class represents a Command-Line Interface (CLI) application for playing the Numberle game.
 * Users can interact with the game through the command line by entering their guesses for the equation.
 * It communicates with the NumberleModel to manage the game state and logic.
 * This class handles user input and displays game status and messages.
 */

public class CLIApp {
    public static void main(String[] args) {
        INumberleModel model = new NumberleModel();

        try (Scanner scanner = new Scanner(System.in)) {
            model.startNewGame();
            System.out.println("\nWelcome to Numberle - CLI Version");
            System.out.println("You have " + model.getRemainingAttempts() + " attempts to guess. The equation only have 7 characters.");

            while (!model.isGameOver()) {
                System.out.println("Enter your guess: ");
                String input = scanner.nextLine();
                int result = model.processInput(input);

                if (model.isGameOver()) {
                    if (model.isGameWon()) {
                        System.out.println("You won!!");
                    } else {
                        System.out.println("You Lost! The correct equation is: " + model.getTargetNumber());
                    }
                } else {
                    switch (result) {
                        case 1:
                            System.out.println("Invalid Input");
                            break;
                        case 2:
                            System.out.println("No equal '=' sign.");
                            break;
                        case 3:
                            System.out.println("There must be at least one '+-*/'.");
                            break;
                        case 4:
                            System.out.println("The left side is not equal to the right.");
                            break;
                    }
                    System.out.println("Unused characters: " + model.getUnusedCharacters());

                    System.out.println(model.getColorCharacters());

                    System.out.println("\nTry again. You have " + model.getRemainingAttempts() + " attempts left.");
                }
            }
        }
    }
}

