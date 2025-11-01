package seedu.address.ui;

import static seedu.address.model.company.Status.VALID_STATUSES;

import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Stage;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.FilterCommand;
import seedu.address.logic.commands.FindCommand;

/**
 * Controller for a help page
 */
public class HelpWindow extends ClosableWindow {

    public static final String USERGUIDE_URL = "https://ay2526s1-cs2103t-f08a-1.github.io/tp/UserGuide.html";
    public static final String HELP_MESSAGE = "Refer to the full user guide at: " + USERGUIDE_URL;
    public static final String COMMAND_OVERVIEW = String.format("""
                                Commands Overview:

                                Status values: %s

                                * list
                                ---------
                                Display all companies in Cerebro.

                                * %s

                                * %s

                                * %s

                                * %s

                                * %s

                                * clear
                                ---------
                                Clear all companies from Cerebro (irreversible!).

                                * help
                                ---------
                                Display this help message.

                                * metrics
                                ---------
                                View application status metrics.

                                * exit
                                ---------
                                Exit Cerebro.
                                """, VALID_STATUSES, FilterCommand.MESSAGE_USAGE, FindCommand.MESSAGE_USAGE,
                                AddCommand.MESSAGE_USAGE, EditCommand.MESSAGE_USAGE, DeleteCommand.MESSAGE_USAGE);


    private static final Logger logger = LogsCenter.getLogger(HelpWindow.class);
    private static final String FXML = "HelpWindow.fxml";

    @FXML
    private Button copyButton;

    @FXML
    private Label helpMessage;

    @FXML
    private TextArea commandOverview;

    /**
     * Creates a new HelpWindow.
     *
     * @param root Stage to use as the root of the HelpWindow.
     */
    public HelpWindow(Stage root) {
        super(FXML, root);
        helpMessage.setText(HELP_MESSAGE);
        commandOverview.setText(COMMAND_OVERVIEW);
    }

    /**
     * Creates a new HelpWindow.
     */
    public HelpWindow() {
        this(new Stage());
    }

    /**
     * Shows the help window.
     * @throws IllegalStateException
     *     <ul>
     *         <li>
     *             if this method is called on a thread other than the JavaFX Application Thread.
     *         </li>
     *         <li>
     *             if this method is called during animation or layout processing.
     *         </li>
     *         <li>
     *             if this method is called on the primary stage.
     *         </li>
     *         <li>
     *             if {@code dialogStage} is already showing.
     *         </li>
     *     </ul>
     */
    public void show() {
        logger.fine("Showing help page about the application.");
        getRoot().show();
        getRoot().centerOnScreen();

        Platform.runLater(() -> {
            if (getRoot().getScene() != null && getRoot().getScene().getRoot() != null) {
                getRoot().getScene().getRoot().requestFocus();
            }
        });
    }

    /**
     * Returns true if the help window is currently being shown.
     */
    public boolean isShowing() {
        return getRoot().isShowing();
    }

    /**
     * Hides the help window.
     */
    public void hide() {
        getRoot().hide();
    }

    /**
     * Focuses on the help window.
     */
    public void focus() {
        getRoot().requestFocus();
    }

    /**
     * Copies the URL to the user guide to the clipboard.
     */
    @FXML
    private void copyUrl() {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent url = new ClipboardContent();
        url.putString(USERGUIDE_URL);
        clipboard.setContent(url);
    }
}
