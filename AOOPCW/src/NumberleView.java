import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.Observer;


/**
 * The NumberleView class represents the graphical user interface (GUI) of the Numberle game.
 * It implements the Observer interface to receive updates from the NumberleModel and interacts
 * with the NumberleController to handle user input and game logic.
 */
public class NumberleView implements Observer { //Implements Observe
    private final INumberleModel model;
    private final NumberleController controller;
    private final JFrame frame = new JFrame("Numberle");
    private final JTextField inputTextField = new JTextField(3);;
    private final StringBuilder input;
    private RoundedTextField[][] fields = new RoundedTextField[INumberleModel.MAX_ATTEMPTS][7];
    private final Map<String, RoundedButton> buttonMap = new HashMap<>();
    private JButton restartGameButton;
    private boolean showErrorMessage=true;
    private int remainingAttempts;
    private int currentPosition = 0;

    /**
     * Constructs a NumberleView object with the given model and controller.
     * This method initializes the view by starting a new game, adding itself as an observer to the model,
     * initializing the frame, setting the view for the controller, and updating the view with the initial model state.
     * It also retrieves the current guess from the controller's input.
     *
     * @param model      The INumberleModel implementation that provides the game logic and state.
     * @param controller The NumberleController responsible for handling user input and game flow.
     */
    public NumberleView(INumberleModel model, NumberleController controller) {
        // Assign the controller and model
        this.controller = controller;
        this.model = model;
        // Start a new game
        this.controller.startNewGame();
        // Add this view as an observer to the model
        ((NumberleModel)this.model).addObserver(this); // Calls addObserver
        // Initialize the graphical user interface (GUI) frame
        initializeFrame();
        // Set this view for the controller
        this.controller.setView(this);
        // Update the view with the initial model state
        update((NumberleModel)this.model, null);
        // Retrieve the current guess from the controller's input
        input = controller.getCurrentGuess();
    }

    /**
     * This method initializes the main frame of the game application. It sets up the user interface components
     * such as buttons, panels, menus, and event listeners for the game.
     */
    public void initializeFrame() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(720, 650);
        frame.setLayout(new BorderLayout(0,10));
        frame.setResizable(false);
        frame.setBackground(Color.WHITE);

        JPanel northPanel = new JPanel(new BorderLayout());
        ImageIcon icon = new ImageIcon("icon.png");
        Image scaledImage = icon.getImage().getScaledInstance(200, 50, Image.SCALE_SMOOTH);
        icon = new ImageIcon(scaledImage);
        JLabel iconLabel = new JLabel(icon);
        northPanel.add(iconLabel, BorderLayout.WEST);
        frame.add(northPanel, BorderLayout.NORTH);

        // Create a menu bar for the frame. Mainly use for testing FR3.
        JMenuBar menuBar = new JMenuBar();
        Color buttonBackgroundColor = Color.decode("#DCE1ED");
        Color buttonTextColor = Color.decode("#5A6376");
                
        restartGameButton = new JButton("Restart Game");
        restartGameButton.addActionListener(e -> restartGame());
        restartGameButton.setForeground(buttonTextColor);
        restartGameButton.setBackground(buttonBackgroundColor);
        menuBar.add(restartGameButton);
        menuBar.add(Box.createHorizontalGlue());

        JToggleButton showErrorButton = new JToggleButton("Show Error");
        showErrorButton.setSelected(true);
        showErrorButton.addActionListener(e -> showError(showErrorButton.isSelected()));
        showErrorButton.setForeground(buttonTextColor);
        showErrorButton.setBackground(buttonBackgroundColor);
        menuBar.add(showErrorButton);

        JButton showAnswerButton = new JButton("Show Answer");
        showAnswerButton.addActionListener(e -> showAnswer());
        showAnswerButton.setForeground(buttonTextColor);
        showAnswerButton.setBackground(buttonBackgroundColor);
        menuBar.add(showAnswerButton);

        JToggleButton randomEquationButton = new JToggleButton("Random Equation");
        randomEquationButton.setSelected(true);
        randomEquationButton.addActionListener(e -> randomEquation(randomEquationButton.isSelected()));
        randomEquationButton.setForeground(buttonTextColor);
        randomEquationButton.setBackground(buttonBackgroundColor);
        menuBar.add(randomEquationButton);
        frame.setJMenuBar(menuBar);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.X_AXIS));
        center.add(new JPanel());
        center.setBackground(Color.WHITE);
        JPanel displayPanel = new JPanel();
        displayPanel.setLayout(new GridLayout(6, 7, 5, 5));
        displayPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        Font font = new Font("Verdana", Font.PLAIN, 25);
        Border roundedBorder = new RoundedBorder(10);
        Dimension squareDimension = new Dimension(45, 56);
        Color borderColor = Color.decode("#DCE1ED");
        Color highlightBorderColor = Color.GRAY;
        Color textColor = Color.decode("#5A6376");
        for (int i = 0; i < INumberleModel.MAX_ATTEMPTS; i++) {
            for (int j = 0; j < 7; j++) {
                fields[i][j] = new RoundedTextField(10,borderColor, highlightBorderColor);
                fields[i][j].setEditable(false);
                fields[i][j].setHorizontalAlignment(JTextField.CENTER);
                fields[i][j].setFont(font);
                fields[i][j].setForeground(textColor);  // Applying the color to the text
                fields[i][j].setBorder(roundedBorder);
                fields[i][j].setPreferredSize(squareDimension);
                fields[i][j].setBackground(Color.decode("#e6e9ed"));
                displayPanel.add(fields[i][j]);
            }
        }
        center.add(displayPanel);
        center.add(new JPanel());
        frame.add(center, BorderLayout.CENTER);

        JPanel keyboardPanel = new JPanel();
        keyboardPanel.setLayout(new GridLayout(2, 1, 5, 5));
        keyboardPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonBackgroundColor = Color.decode("#DCE1ED");
        buttonTextColor = Color.decode("#5A6376");
        JPanel numberPanel = new JPanel(new GridLayout(1, 10, 5, 5));
        String[] numberKeys = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};
        for (String key : numberKeys) {
            RoundedButton button = new RoundedButton(key, buttonBackgroundColor, buttonTextColor);
            button.setFont(new Font("Verdana", Font.PLAIN, 20));
            button.setPreferredSize(new Dimension(30,55));
            button.setBorder(new RoundedBorder(10));
            button.setBorderPainted(false);
            button.addActionListener(e -> {
                if (currentPosition < 7) {
                    fields[remainingAttempts][currentPosition].setText(key);
                    currentPosition++;
                }
            });
            buttonMap.put(key, button);
            numberPanel.add(button);
        }
        JPanel operationPanel = new JPanel(new GridLayout(1, 14, 5, 5));
        String[] operationKeys = {"Back", "+", "-", "*", "/", "=", "Enter"};
        operationPanel.setLayout(null);
        int x = 1;
        int y = 0;
        for (String key : operationKeys) {
            RoundedButton button = new RoundedButton(key, buttonBackgroundColor, buttonTextColor);
            button.setFont(new Font("Verdana", Font.PLAIN, 25));
            int width = key.equals("Back") || key.equals("Enter") ? 158 : 70;
            button.setBounds(x,y,width,55);
            x += width * 1.03;
            button.setBorder(new RoundedBorder(10));
            button.setBorderPainted(false);
            button.addActionListener(e -> {
                if (currentPosition <= 7) {
                    switch (key) {
                        case "Back":
                            if (currentPosition > 0) {
                                fields[remainingAttempts][currentPosition - 1].setText("");
                                currentPosition--;
                            }
                            break;
                        case "Enter":
                            for (int i = 0; i < currentPosition; i++) {
                                input.append(fields[remainingAttempts][i].getText());
                            }
                            controller.processInput(input.toString());
                            break;

                        case "+":
                        case "-":
                        case "*":
                        case "/":
                        case "=":
                            if (currentPosition < 6) {
                                fields[remainingAttempts][currentPosition].setText(key);
                                currentPosition++;
                            }
                            break;
                    }
                }
            });
            buttonMap.put(key, button);
            operationPanel.add(button);
        }
        keyboardPanel.add(numberPanel, BorderLayout.NORTH);
        keyboardPanel.add(operationPanel, BorderLayout.SOUTH);
        frame.add(keyboardPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    /**
     * This method is an override of the update() method from the Observable class.
     * It is used to update the view based on changes in the model, specifically for the Numberle game.
     *
     * @param o   The Observable object.
     * @param arg The argument passed when the Observable object is updated.
     */
    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof String) {
            String message = (String) arg;
            switch (message) {
                case "Invalid Input":
                	if(showErrorMessage) {
                		new TimedRoundedDialog(frame, message, "").setVisible(true);
                	}
                    currentPosition = input.length();
                    remainingAttempts = INumberleModel.MAX_ATTEMPTS - controller.getRemainingAttempts();
                    input.setLength(0);
                    break;
                case "Game Won":
                    showColor();
                    new TimedRoundedDialog(frame, "Congratulations! You won the game!", "").setVisible(true);
                    controller.startNewGame();
                    clearAllContent();
                    resetButtonColors();
                    currentPosition = 0;
                    remainingAttempts = INumberleModel.MAX_ATTEMPTS - controller.getRemainingAttempts();
                    input.setLength(0);
                    break;
                case "Game Over":
                    showColor();
                    new TimedRoundedDialog(frame, message + "! No Attempts! The correct equation was: " + controller.getTargetWord(), "",600,100).setVisible(true);
                    controller.startNewGame();
                    clearAllContent();
                    resetButtonColors();
                    currentPosition = 0;
                    remainingAttempts = INumberleModel.MAX_ATTEMPTS - controller.getRemainingAttempts();
                    input.setLength(0);
                    break;
                case "Try Again":
                    showColor();
                    setButtonColors();
                    for (int j = 0; j < 7; j++) {
                        fields[remainingAttempts][j].resetBorderColor();
                        fields[remainingAttempts][j].setForeground(Color.WHITE); // Change text color to white in the guess fields

                    }
                    new TimedRoundedDialog(frame, message + "! Attempts remaining: " + controller.getRemainingAttempts(), "Try Again").setVisible(true);
                    currentPosition = 0;
                    remainingAttempts = INumberleModel.MAX_ATTEMPTS - controller.getRemainingAttempts();
                    input.setLength(0);
                    break;
                case "No Equal":
                	if(showErrorMessage) {
                		new TimedRoundedDialog(frame, "No equal '=' sign.", message).setVisible(true);
                	}
                    currentPosition = input.length();
                    remainingAttempts = INumberleModel.MAX_ATTEMPTS - controller.getRemainingAttempts();
                    input.setLength(0);
                    break;
                case "Missing Symbols":
                	if(showErrorMessage) {
                		new TimedRoundedDialog(frame, "There must be at least one '+-×÷'.", message).setVisible(true);
                	}
                    currentPosition = input.length();
                    remainingAttempts = INumberleModel.MAX_ATTEMPTS - controller.getRemainingAttempts();
                    input.setLength(0);
                    break;
                case "Not Equal":
                	if(showErrorMessage) {
                		new TimedRoundedDialog(frame, "The left side is not equal to the right.", message).setVisible(true);
                	}
                    currentPosition = input.length();
                    remainingAttempts = INumberleModel.MAX_ATTEMPTS - controller.getRemainingAttempts();
                    input.setLength(0);
                    break;
            }
        }
    }

    /**
     * Restarts the game by starting a new game, clearing all content on the interface,
     * resetting button colors, resetting the current position, and updating the remaining attempts.
     * This method is called when the user wants to restart the game.
     */
    private void restartGame() {
        controller.startNewGame();
        clearAllContent();
        resetButtonColors();
        currentPosition = 0;
        remainingAttempts = INumberleModel.MAX_ATTEMPTS - controller.getRemainingAttempts();
        input.setLength(0);
    }

    /**
     * This method is used to clear all content in a GUI.
     * It iterates through a 2D array of text fields and resets their text, background color,
     * and border. It also resets the background color and appearance of a collection of buttons.
     */
    private void clearAllContent() {
        for (int i = 0; i < INumberleModel.MAX_ATTEMPTS; i++) {
            for (int j = 0; j < 7; j++) {
                fields[i][j].setText("");
                fields[i][j].setBackground(Color.decode("#e6e9ed")); // 设置默认的背景色
                fields[i][j].setBorder(new RoundedBorder(10));
            }
        }
        Color defaultButtonBackgroundColor = Color.decode("#DCE1ED");
        for (RoundedButton button : buttonMap.values()) {
            button.setBackground(defaultButtonBackgroundColor);
            button.setOpaque(false);
            button.setContentAreaFilled(false);
            button.setBorderPainted(false);
            button.repaint();
        }
    }

    /**
     * Displays colors based on the color codes retrieved from the model.
     */
    private void showColor() {
        // Loop through the color list in the model
        for (int i = 0; i < model.getColors().size(); i++) {
            // Switch statement to set background color based on color code. This is mainly use method in model.
            switch (model.getColors().get(i)) {
                case "0":
                    fields[remainingAttempts][i].setBackground(Color.decode("#2FCEA5"));
                    break;
                case "1":
                    fields[remainingAttempts][i].setBackground(Color.decode("#F79A6F"));
                    break;
                case "2":
                    fields[remainingAttempts][i].setBackground(Color.decode("#A4AEC4"));
                    break;
            }
        }
    }

    /**
     * Sets the colors of buttons based on a predefined mapping of color names to Color objects.
     * This method iterates through the characters mapped to each color in the provided model
     * and sets the background color of corresponding buttons to the specified colors.
     */
    public void setButtonColors() {
        Map<String, Color> colorDefinitions = new HashMap<>();
        colorDefinitions.put("Gray", Color.decode("#A4AEC4"));
        colorDefinitions.put("Green", Color.decode("#2FCEA5"));
        colorDefinitions.put("Orange", Color.decode("#F79A6F"));

        for (Map.Entry<String, Set<Character>> entry : model.getMap().entrySet()) {
            String colorName = entry.getKey();
            Set<Character> characters = entry.getValue();

            Color color = colorDefinitions.get(colorName);
            if (color == null) continue;

            for (Character character : characters) {
                RoundedButton button = buttonMap.get(character.toString());
                if (button != null) {
                    button.setBackground(color);
                    button.setForeground(Color.WHITE);
                }
            }
        }
    }

    /**
     * Custom JTextField with rounded corners and customizable border colors.
     * This class provides a text field component with rounded corners and the ability to set different border colors.
     */
    class RoundedTextField extends JTextField {
        private int radius;
        private Color borderColor;
        private Color highlightBorderColor;

        /**
         * Constructs a new RoundedTextField.
         *
         * @param radius              The radius of the rounded corners.
         * @param borderColor         The default border color.
         * @param highlightBorderColor The border color when text is inserted.
         */
        public RoundedTextField(int radius, Color borderColor, Color highlightBorderColor) {
            super();
            this.radius = radius;
            this.borderColor = borderColor;
            this.highlightBorderColor = highlightBorderColor;
            setOpaque(false);
            getDocument().addDocumentListener(new DocumentListener() {
                public void insertUpdate(DocumentEvent e) {
                    setBorderColor(highlightBorderColor);
                }
                public void removeUpdate(DocumentEvent e) {
                    if (getText().length() == 0) {
                        setBorderColor(borderColor);
                    }
                }
                public void changedUpdate(DocumentEvent e) { }
            });
        }
        /**
         * Sets the border color of the text field.
         *
         * @ param newColor The new border color to set.
         */
        public void setBorderColor(Color newColor) {
            this.borderColor = newColor;
            repaint();
        }
        /**
         * Sets the border color of the text field.
         *
         * @ param newColor The new border color to set.
         */
        public void resetBorderColor() {
            setBorderColor(Color.decode("#DCE1ED"));
        }
        /**
         * Customizes the painting of the text field component.
         *
         * @param g The Graphics context to paint on.
         */
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, radius, radius);
            g2.dispose();

            super.paintComponent(g);
        }
        /**
         * Customizes the painting of the text field border.
         *
         * @param g The Graphics context to paint on.
         */
        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(this.borderColor);
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, radius, radius);
            g2.dispose();
        }
    }

    /**
     * This class extends JButton to create rounded buttons with customizable properties such as corner radius,
     * button size, background color, text color, and optional icon.
     */
    class RoundedButton extends JButton {
        private int cornerRadius = 10;
        private Dimension buttonsize = new Dimension();
        private ImageIcon icon;
        /**
         * Constructor to create a rounded button with specified label, background color, and text color.
         * @param label The text label of the button.
         * @param backgroundColor The background color of the button.
         * @param textColor The text color of the button.
         */
        public RoundedButton(String label, Color backgroundColor, Color textColor) {
            super(label);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setPreferredSize(buttonsize);
            setForeground(textColor);
            setBackground(backgroundColor);
            if ("Back".equals(label)) {
                ImageIcon originalIcon = new ImageIcon("deleteIcon.png");
                Image image = originalIcon.getImage();
                Image newimg = image.getScaledInstance(40, 30,  java.awt.Image.SCALE_SMOOTH);
                icon = new ImageIcon(newimg);
            }
        }
        /**
         * Method to set the size of the button.
         * @param width The width of the button.
         * @param height The height of the button.
         */
        public void setButtonsize(int width, int height){
            buttonsize = new Dimension(width, height);
            setPreferredSize(buttonsize);
        }
        /**
         * Override method to customize the painting of the button component.
         * @param g The Graphics object used for painting.
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, cornerRadius, cornerRadius);
            g2.setColor(getBackground());
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, cornerRadius, cornerRadius);

            if (icon != null) {
                int x = (getWidth() - icon.getIconWidth()) / 2;
                int y = (getHeight() - icon.getIconHeight()) / 2;
                icon.paintIcon(this, g2, x, y);
            } else {
                FontMetrics fm = g2.getFontMetrics();
                int stringWidth = fm.stringWidth(getText());
                int stringAscent = fm.getAscent();
                int x = (getWidth() - stringWidth) / 2;
                int y = (getHeight() + stringAscent) / 2 - fm.getLeading() - fm.getDescent() / 2;
                g2.setColor(getForeground());
                g2.drawString(getText(), x, y);
            }
            g2.dispose();
        }
    }

    /**
     * This class represents a custom rounded border for Swing components.
     * It extends AbstractBorder to provide a rounded border with a specified radius.
     */
    class RoundedBorder extends AbstractBorder {
        private int radius;
        /**
         * Constructs a RoundedBorder with the given radius.
         * @param radius The radius of the rounded corners.
         */
        RoundedBorder(int radius) {
            this.radius = radius;
        }
        /**
         * Paints the rounded border on the specified component.
         * @param c The component on which the border is painted.
         * @param g The Graphics object used for painting.
         * @param x The x-coordinate of the top-left corner of the border.
         * @param y The y-coordinate of the top-left corner of the border.
         * @param width The width of the border.
         * @param height The height of the border.
         */
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.setColor(Color.decode("#b7b7b7"));
            g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
    }

    /**
     * This class represents a timed dialog with rounded corners.
     * It extends JDialog and provides methods to display a message in a dialog window with a specified time duration.
     */
    class TimedRoundedDialog extends JDialog {
        /**
         * Constructs a new TimedRoundedDialog with the specified owner frame, message, and title.
         *
         * @param owner   the owner frame for the dialog
         * @param message the message to be displayed in the dialog
         * @param title   the title of the dialog window
         */
        public TimedRoundedDialog(Frame owner, String message, String title ) {
            super(owner, title, true);
            setUndecorated(true);
            setSize(300, 100);
            setLocationRelativeTo(owner);
            setLayout(new BorderLayout());
            display(message);
        }
        /**
         * Constructs a new TimedRoundedDialog with the specified owner frame, message, title, width, and height.
         *
         * @param owner   the owner frame for the dialog
         * @param message the message to be displayed in the dialog
         * @param title   the title of the dialog window
         * @param width   the width of the dialog window
         * @param height  the height of the dialog window
         */
        public TimedRoundedDialog(Frame owner, String message, String title ,int width,int height) {
            super(owner, title, true);
            setUndecorated(true);
            setSize(width,height);
            setLocationRelativeTo(owner);
            setLayout(new BorderLayout());
            display(message);
        }
        /**
         * Displays the specified message in the dialog window with rounded corners.
         *
         * @param message the message to be displayed in the dialog
         */
        public void display(String message) {
            JPanel panel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.setColor(getBackground());
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                }
            };
            panel.setBackground(Color.WHITE);
            panel.setLayout(new GridBagLayout());
            add(panel);

            JLabel label = new JLabel(message, SwingConstants.CENTER);
            label.setFont(new Font("Verdana", Font.BOLD, 14));
            panel.add(label);

            Timer timer = new Timer(2000, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });
            timer.setRepeats(false);
            timer.start();
        }
    }

    /**
     * This class represents a custom toggle button with rounded edges, extending JToggleButton.
     * It is designed to provide a visual representation of a toggle button with rounded corners.
     */
    public class RoundedToggleButton extends JToggleButton {
        private RoundedButton roundedButton;
        /**
         * Constructs a new RoundedToggleButton with the specified label, background color, and text color.
         * @param label The text label displayed on the button.
         * @param backgroundColor The background color of the button.
         * @param textColor The color of the text displayed on the button.
         */
        public RoundedToggleButton(String label, Color backgroundColor, Color textColor) {
            super();
            roundedButton = new RoundedButton(label,backgroundColor,textColor);

        }
        /**
         * Sets whether this toggle button is selected or not.
         * Overrides the setSelected method in JToggleButton to update the selected state of the associated RoundedButton.
         * @param selected true if the button should be selected, false otherwise.
         */
        @Override
        public void setSelected(boolean selected) {
            super.setSelected(selected);
            roundedButton.setSelected(selected);
        }
    }

    /**
     *Only after the player has entered the first legal equation will it run to use the restart game button.
     */
    public void setRestartEnable(boolean enable) {
        restartGameButton.setEnabled(enable);
    }

    private void showAnswer() {
        new TimedRoundedDialog(frame,  "The correct equation was: " + controller.getTargetWord(), "Current Answer").setVisible(true);
    }

    private void showError(boolean on) {
        showErrorMessage=on;
    }

    public void randomEquation(boolean on) {
        controller.setRandom(on);
        restartGame();
    }

    private void resetButtonColors() {
        Color buttonTextColor = Color.decode("#5A6376"); // 原始的字体颜色
        for (RoundedButton button : buttonMap.values()) {
            button.setForeground(buttonTextColor);
        }

        for (int i = 0; i < INumberleModel.MAX_ATTEMPTS; i++) {
            for (int j = 0; j < 7; j++) {
                fields[i][j].setForeground(buttonTextColor);
            }
        }
    }

}