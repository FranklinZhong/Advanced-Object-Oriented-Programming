
/**
 * The GUIApp class represents a Graphical User Interface (GUI) application for playing the Numberle game.
 * It initializes the GUI components using Swing utilities and follows the MVC (Model-View-Controller) design pattern.
 * This class sets up the model, controller, and view components required for the game.
 */
public class GUIApp {
    public static void main(String[] args) {

        javax.swing.SwingUtilities.invokeLater(
                new Runnable() {
                    public void run() {
                        createAndShowGUI();
                    }
                }
        );
    }

    public static void createAndShowGUI() {
        INumberleModel model = new NumberleModel();
        NumberleController controller = new NumberleController(model);
        NumberleView view = new NumberleView(model, controller);
    }
}