import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NumberleModelTest {
    private NumberleModel model;

    @BeforeEach
    public void setUp() {
        model = new NumberleModel();
        model.setRandom(false);
        model.initialize();
    }

    /**
     * Tests the initialization of the NumberleModel. And the random function work.
     * The testInitialize method is a unit test designed to verify the initialization behavior of a NumberleModel object.
     * It is divided into two main parts:
     * one for testing the initialization without random generation of the target equation,
     * and the other for testing initialization with random generation.
     * -----------------------------------------------------------------------------------------------------------------
     * @ requires model != null;
     * @ requires model.getTargetNumber().equals("1+2+3=6");
     * @ ensures model.getRemainingAttempts() == NumberleModel.MAX_ATTEMPTS;
     * @ ensures !model.isGameOver();
     * @ ensures !model.isGameWon();
     * @ ensures model.getUnusedCharacters().size() == 14;
     *
     * @pre The NumberleModel instance is created. The target equation is "1+2+3=6".
     * @post The remaining attempts are set to the maximum attempts defined in the model.
     *       The game over flag is set to false.
     *       The game won flag is set to false.
     *       The target number is set to a predefined equation ("1+2+3=6").
     *       The list of unused characters contains all possible characters for equations.
     *       If random generation is enabled, the target number is not equal to the predefined equation.
     */

    @Test
    public void testInitialize() {
        //In the first part of the test, it checks various properties of the model when the target equation is not randomly generated.
        assertEquals(NumberleModel.MAX_ATTEMPTS, model.getRemainingAttempts()); //Verifies that the remaining attempts in the model are equal to the maximum attempts allowed.
        assertFalse(model.isGameOver()); //Ensures that the game over status is false.
        assertFalse(model.isGameWon()); //Ensures that the game won status is false.
        assertEquals(model.getTargetNumber(),"1+2+3=6"); //Checks that the target equation in the model is "1+2+3=6".
        assertEquals(model.getUnusedCharacters().size(),14); //Checks that the number of unused characters in the model is 14.

        // In the second part, it tests the initialization behavior when the target equation is randomly generated.
        // Similar to the first part, it checks the same properties of the model after initialization to ensure they meet the expected conditions.
        model.setRandom(true); //Sets the model to generate a random target equation.
        model.startNewGame(); //Initializes the model for a new game.
        assertEquals(NumberleModel.MAX_ATTEMPTS, model.getRemainingAttempts()); 
        assertFalse(model.isGameOver()); 
        assertFalse(model.isGameWon()); 
        assertNotEquals(model.getTargetNumber(),"1+2+3=6");
        assertEquals(model.getUnusedCharacters().size(),14); //Checks that the number of unused characters in the model is 14, as expected after initialization.
    }

    /**
     * Tests the scenario where the user inputs invalid equations.
     * This test case is designed to validate the behavior of the processInput method in the NumberleModel class when invalid inputs are provided.
     * The test checks whether the remaining attempts remain unchanged after processing various invalid input equations.
     *
     *------------------------------------------------------------------------------------------------------------------
     * @ ensures model.getRemainingAttempts() == \old(model.getRemainingAttempts());
     * @ ensures \result == true;
     *
     * @post The game continues.
     *       The remaining attempts do not decrease.
     */

    @Test
    public void testInvalidInput() {
        model.processInput("abcd=fg"); //No number type in.
        assertEquals(NumberleModel.MAX_ATTEMPTS, model.getRemainingAttempts()); 
        model.processInput("+-*/+-*"); //No number and "=" type in.
        assertEquals(NumberleModel.MAX_ATTEMPTS, model.getRemainingAttempts()); 
        model.processInput("*123+5="); //Invalid equation.
        assertEquals(NumberleModel.MAX_ATTEMPTS, model.getRemainingAttempts()); 
        model.processInput("12=12=1"); // No "+-*/" type in.
        assertEquals(NumberleModel.MAX_ATTEMPTS, model.getRemainingAttempts()); 
        model.processInput("1+2+3=7"); //Not equal condition.
        assertEquals(NumberleModel.MAX_ATTEMPTS, model.getRemainingAttempts()); 
    }
    
    /**
     * Tests the scenario where the game is over due to running out of attempts.
     * This test focuses on testing the player's ability to enter valid equation at every opportunity.
     * But eventually all the chances are used up without guessing the correct equation.
     * This results in the game being lost.
     *
     * ----------------------------------------------------------------------------------------------------------------
     * //@ requires model != null && model.getRemainingAttempts() == NumberleModel.MAX_ATTEMPTS;
     * //@ ensures model.isGameOver() && !model.isGameWon();
     *
     * @pre The model should be initialized and a new game should be started.
     * @post No attempts left.
     *       The game is over.
     *       The game is not won.
     */
    @Test
    public void testGameOverNoAttempts() {
        //Assume user type wrong equation in every attempts. And lost the game.
        model.processInput("3+2+2=7"); // Player type in wrong but valid equation
        assertEquals(NumberleModel.MAX_ATTEMPTS-1, model.getRemainingAttempts()); //The remaining attempts should decrease by 1.
        model.processInput("+9+1=10"); 
        assertEquals(NumberleModel.MAX_ATTEMPTS-2, model.getRemainingAttempts()); 
        model.processInput("2*8-9=7"); 
        assertEquals(NumberleModel.MAX_ATTEMPTS-3, model.getRemainingAttempts()); 
        model.processInput("-8+5=-3"); 
        assertEquals(NumberleModel.MAX_ATTEMPTS-4, model.getRemainingAttempts()); 
        model.processInput("1+4*2=9"); 
        assertEquals(NumberleModel.MAX_ATTEMPTS-5, model.getRemainingAttempts()); 
        model.processInput("+3+2=+5");
        assertEquals(NumberleModel.MAX_ATTEMPTS-6, model.getRemainingAttempts()); //Player try 6 times. So no attempt left.
        assertTrue(model.isGameOver()); // Player run out of chance. So lost the game.
        assertFalse(model.isGameWon());

    }
    
}
