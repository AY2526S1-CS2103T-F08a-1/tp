package seedu.address.ui;

import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.Logic;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * The Main Window. Provides the basic application layout containing
 * a menu bar and space where other JavaFX elements can be placed.
 */
public class MainWindow extends ClosableWindow {

    private static final String FXML = "MainWindow.fxml";

    private final Logger logger = LogsCenter.getLogger(getClass());

    private Stage primaryStage;
    private Logic logic;

    // Independent Ui parts residing in this Ui container
    private CompanyListPanel companyListPanel;
    private HelpWindow helpWindow;
    private MetricsWindow metricsWindow;

    @FXML
    private StackPane commandBoxPlaceholder;

    @FXML
    private MenuItem helpMenuItem;

    @FXML
    private MenuItem metricsMenuItem;

    @FXML
    private StackPane companyListPanelPlaceholder;

    @FXML
    private StackPane resultDisplayPlaceholder;

    @FXML
    private StackPane statusbarPlaceholder;

    /**
     * Creates a {@code MainWindow} with the given {@code Stage} and {@code Logic}.
     */
    public MainWindow(Stage primaryStage, Logic logic) {
        super(FXML, primaryStage);

        // Set dependencies
        this.primaryStage = primaryStage;
        this.logic = logic;

        // Configure the UI
        setWindowDefaultSize(logic.getGuiSettings());

        setAccelerators();

        helpWindow = new HelpWindow();
        metricsWindow = new MetricsWindow();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    private void setAccelerators() {
        setAccelerator(helpMenuItem, KeyCombination.valueOf("F1"));
    }

    /**
     * Sets the accelerator of a MenuItem.
     * @param keyCombination the KeyCombination value of the accelerator
     */
    private void setAccelerator(MenuItem menuItem, KeyCombination keyCombination) {
        menuItem.setAccelerator(keyCombination);

        /*
         * TODO: the code below can be removed once the bug reported here
         * https://bugs.openjdk.java.net/browse/JDK-8131666
         * is fixed in later version of SDK.
         *
         * According to the bug report, TextInputControl (TextField, TextArea) will
         * consume function-key events. Because CommandBox contains a TextField, and
         * ResultDisplay contains a TextArea, thus some accelerators (e.g F1) will
         * not work when the focus is in them because the key event is consumed by
         * the TextInputControl(s).
         *
         * For now, we add following event filter to capture such key events and open
         * help window purposely so to support accelerators even when focus is
         * in CommandBox or ResultDisplay.
         */
        getRoot().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getTarget() instanceof TextInputControl && keyCombination.match(event)) {
                menuItem.getOnAction().handle(new ActionEvent());
                event.consume();
            }
        });
    }

    /**
     * Fills up all the placeholders of this window.
     */
    void fillInnerParts() {
        companyListPanel = new CompanyListPanel(logic.getFilteredCompanyList());
        companyListPanelPlaceholder.getChildren().add(companyListPanel.getRoot());

        StatusBarFooter statusBarFooter = new StatusBarFooter(logic.getAddressBookFilePath());
        statusbarPlaceholder.getChildren().add(statusBarFooter.getRoot());

        CommandBox commandBox = new CommandBox(this::executeCommand);
        commandBoxPlaceholder.getChildren().add(commandBox.getRoot());
    }

    /**
     * Sets the default size based on {@code guiSettings}.
     * If the settings are invalid (off-screen or too large), corrected settings
     * are applied and immediately saved to preferences.
     */
    private void setWindowDefaultSize(GuiSettings guiSettings) {
        GuiSettings appliedSettings = WindowPositionManager.applyGuiSettings(primaryStage, guiSettings);

        // If settings were corrected, save them immediately to preferences
        if (!appliedSettings.equals(guiSettings)) {
            logic.setGuiSettings(appliedSettings);
            logger.info("Window position/size was corrected and saved to preferences.");
        }
    }

    /**
     * Opens the help window or focuses on it if it's already opened.
     * If the help window is minimized, it will be restored and brought to front.
     */
    @FXML
    public void handleHelp() {
        if (!helpWindow.isShowing()) {
            helpWindow.show();
        } else {
            // Check if the window is minimized (iconified)
            if (helpWindow.getRoot().isIconified()) {
                helpWindow.getRoot().setIconified(false);
            }
        }
        helpWindow.focus();
    }

    /**
     * Opens the metrics window or focuses on it if it's already opened.
     * If the metrics window is minimized, it will be restored and brought to front.
     */
    @FXML
    public void handleMetrics() {
        if (!metricsWindow.isShowing()) {
            metricsWindow.setData(logic.getAddressBook());
            metricsWindow.show();
        } else {
            // Check if the window is minimized (iconified)
            if (metricsWindow.getRoot().isIconified()) {
                metricsWindow.getRoot().setIconified(false);
            }
            // Update data before focusing
            metricsWindow.setData(logic.getAddressBook());
        }
        metricsWindow.focus();
    }

    void show() {
        primaryStage.show();
    }

    /**
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        GuiSettings guiSettings = WindowPositionManager.captureGuiSettings(primaryStage);
        logic.setGuiSettings(guiSettings);
        helpWindow.hide();
        metricsWindow.hide();
        primaryStage.hide();
    }

    public CompanyListPanel getcompanyListPanel() {
        return companyListPanel;
    }

    /**
     * Executes the command and returns the result.
     *
     * @see seedu.address.logic.Logic#execute(String)
     */
    private CommandResult executeCommand(String commandText) throws CommandException, ParseException {
        try {
            CommandResult commandResult = logic.execute(commandText);
            logger.info("Result: " + commandResult.getFeedbackToUser());
            companyListPanel.setFeedbackToUser(commandResult.getFeedbackToUser());

            // Auto-update metrics window if it's showing as split screen
            if (metricsWindow.isShowing()) {
                metricsWindow.setData(logic.getAddressBook());
            }

            if (commandResult.isShowHelp()) {
                handleHelp();
            }

            if (commandResult.isShowMetrics()) {
                handleMetrics();
            }

            if (commandResult.isExit()) {
                handleExit();
            }

            return commandResult;
        } catch (CommandException | ParseException e) {
            logger.info("An error occurred while executing command: " + commandText);
            companyListPanel.setFeedbackToUser(e.getMessage());
            throw e;
        }
    }

    @Override
    protected boolean enableEscClose() {
        // Disable ESC close for the primary window.
        return false;
    }

    @Override
    protected void onCloseShortcut() {
        // Behave like clicking the window close button:
        getRoot().fireEvent(
                new javafx.stage.WindowEvent(getRoot(), javafx.stage.WindowEvent.WINDOW_CLOSE_REQUEST)
        );
    }
}
