/**
 * The NumberleController class serves as the controller component in the MVC (Model-View-Controller) design pattern
 * for the Numberle game. It handles user input, interacts with the model, and updates the view accordingly.
 * This class facilitates communication between the model (INumberleModel) and the view (NumberleView).
 * 'view.setRestartEnable' is mainly use for check whether player type one valid equation. So that player can restart game.
 */
public class NumberleController {
    private INumberleModel model;
    private NumberleView view;

    public NumberleController(INumberleModel model) {
        this.model = model;
    }

    public void setView(NumberleView view) {
        this.view = view;
        view.setRestartEnable(false);
    }

    public void processInput(String input) {
        if(model.processInput(input)==0) {
        	view.setRestartEnable(true);
        }
    }

    public void startNewGame() {
        model.startNewGame();
        if(view!=null) {
            view.setRestartEnable(false);
        }
    }

    public void setRandom(boolean random) {
        model.setRandom(random);
    }

    public String getTargetWord() {
        return model.getTargetNumber();
    }

    public StringBuilder getCurrentGuess() {
        return model.getCurrentGuess();
    }

    public int getRemainingAttempts() {
        return model.getRemainingAttempts();
    }

    public boolean isGameOver() {
        return model.isGameOver();
    }

    public boolean isGameWon() {
        return model.isGameWon();
    }

}