package seedu.address.ui;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * The UI component that is responsible for receiving user command inputs.
 */
public class CommandBox extends UiPart<Region> {

    public static final String ERROR_STYLE_CLASS = "error";
    private static final String FXML = "CommandBox.fxml";
    private static final int MAX_HISTORY_SIZE = 50;

    private final CommandExecutor commandExecutor;
    private final List<String> commandHistory;
    private int historyPointer;
    private String currentInput;

    @FXML
    private TextField commandTextField;

    /**
     * Creates a {@code CommandBox} with the given {@code CommandExecutor}.
     */
    public CommandBox(CommandExecutor commandExecutor) {
        super(FXML);
        this.commandExecutor = commandExecutor;
        this.commandHistory = new ArrayList<>();
        this.historyPointer = -1;
        this.currentInput = "";

        // calls #setStyleToDefault() whenever there is a change to the text of the command box.
        commandTextField.textProperty().addListener((unused1, unused2, unused3) -> setStyleToDefault());

        // Add key event handler for arrow key navigation
        commandTextField.setOnKeyPressed(this::handleKeyPress);
    }

    /**
     * Handles the Enter button pressed event.
     */
    @FXML
    private void handleCommandEntered() {
        String commandText = commandTextField.getText();
        if (commandText.equals("")) {
            return;
        }

        try {
            commandExecutor.execute(commandText);
            addToHistory(commandText);
            commandTextField.setText("");
            historyPointer = -1;
            currentInput = "";
        } catch (CommandException | ParseException e) {
            setStyleToIndicateCommandFailure();
        }
    }

    /**
     * Sets the command box style to use the default style.
     */
    private void setStyleToDefault() {
        commandTextField.getStyleClass().remove(ERROR_STYLE_CLASS);
    }

    /**
     * Sets the command box style to indicate a failed command.
     */
    private void setStyleToIndicateCommandFailure() {
        ObservableList<String> styleClass = commandTextField.getStyleClass();

        if (styleClass.contains(ERROR_STYLE_CLASS)) {
            return;
        }

        styleClass.add(ERROR_STYLE_CLASS);
    }

    /**
     * Handles key press events for arrow key navigation through command history.
     */
    private void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.UP) {
            navigateToPreviousCommand();
            event.consume();
        } else if (event.getCode() == KeyCode.DOWN) {
            navigateToNextCommand();
            event.consume();
        }
    }

    /**
     * Navigates to the previous command in history.
     * If currently at the user's current input, saves it before navigating.
     */
    private void navigateToPreviousCommand() {
        if (commandHistory.isEmpty()) {
            return;
        }

        // Save current input if we're starting to navigate
        if (historyPointer == -1) {
            currentInput = commandTextField.getText();
            historyPointer = commandHistory.size();
        }

        // Move to previous command if possible
        if (historyPointer > 0) {
            historyPointer--;
            commandTextField.setText(commandHistory.get(historyPointer));
            commandTextField.positionCaret(commandTextField.getText().length());
        }
    }

    /**
     * Navigates to the next command in history, or returns to the current input.
     */
    private void navigateToNextCommand() {
        if (historyPointer == -1) {
            return; // Not navigating, nothing to do
        }

        historyPointer++;

        if (historyPointer >= commandHistory.size()) {
            // Restore the current input and exit navigation mode
            commandTextField.setText(currentInput);
            commandTextField.positionCaret(commandTextField.getText().length());
            historyPointer = -1;
            currentInput = "";
        } else {
            // Show next command from history
            commandTextField.setText(commandHistory.get(historyPointer));
            commandTextField.positionCaret(commandTextField.getText().length());
        }
    }

    /**
     * Adds a command to the command history.
     * Maintains a maximum history size by removing oldest commands if necessary.
     *
     * @param command The command to add to history.
     */
    private void addToHistory(String command) {
        commandHistory.add(command);

        // Remove oldest command if history exceeds maximum size
        if (commandHistory.size() > MAX_HISTORY_SIZE) {
            commandHistory.remove(0);
        }
    }

    /**
     * Represents a function that can execute commands.
     */
    @FunctionalInterface
    public interface CommandExecutor {
        /**
         * Executes the command and returns the result.
         *
         * @see seedu.address.logic.Logic#execute(String)
         */
        CommandResult execute(String commandText) throws CommandException, ParseException;
    }

}
