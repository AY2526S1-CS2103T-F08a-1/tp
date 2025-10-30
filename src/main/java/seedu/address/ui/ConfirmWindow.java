package seedu.address.ui;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Small helper to synchronously show a Yes/No confirmation dialog from any thread.
 * Captures Y/y and N/n as shortcuts. Returns true for Yes, false for No/close.
 */
public final class ConfirmWindow {
    private static final String THEME_CSS = "/view/DarkTheme.css";
    private static final AtomicBoolean fxInitTried = new AtomicBoolean(false);
    private static final String SKIP_PROMPTS_PROP = "seedu.skipPrompts";

    private ConfirmWindow() {}

    /**
     * Shows a themed, modal confirmation dialog with actions
     * “Continue (Y/y)” and “Cancel (N/n)”.
     */
    public static boolean confirm(String title, String header, String content) {
        // Hard, zero-FX bypasses first: CI flag or headless AWT
        if (Boolean.getBoolean("seedu.skipPrompts") || java.awt.GraphicsEnvironment.isHeadless()) {
            // pretend user clicked “Continue”
            return true;
        }

        if (Platform.isFxApplicationThread()) {
            return showNow(title, header, content);
        }

        AtomicBoolean answer = new AtomicBoolean(false);
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                answer.set(showNow(title, header, content));
            } finally {
                latch.countDown();
            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return answer.get();
    }

    /** Internal helper that constructs and shows the confirmation on the JavaFX Application Thread. **/
    private static boolean showNow(String title, String header, String content) {
        ButtonType yes = new ButtonType("Continue (Y/y)", ButtonBar.ButtonData.OK_DONE);
        ButtonType no = new ButtonType("Cancel (N/n)", ButtonBar.ButtonData.CANCEL_CLOSE);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, content, yes, no);
        alert.setTitle(title);
        alert.setHeaderText(header);

        // --- NEW: attach your app stylesheet + tag this dialog for scoped rules
        DialogPane pane = alert.getDialogPane();
        // or MainWindow.class / any class in same classloader
        String css = seedu.address.MainApp.class
                // adjust if your css file name differs
                .getResource(THEME_CSS)
                .toExternalForm();
        if (!pane.getStylesheets().contains(css)) {
            pane.getStylesheets().add(css);
        }
        pane.getStyleClass().add("confirm-dialog");

        // Style the real buttons
        Button yesBtn = (Button) pane.lookupButton(yes);
        Button noBtn = (Button) pane.lookupButton(no);
        yesBtn.getStyleClass().addAll("pill-button", "pill-button-primary");
        noBtn.getStyleClass().addAll("pill-button", "pill-button-secondary");
        ButtonBar.setButtonUniformSize(yesBtn, false);
        ButtonBar.setButtonUniformSize(noBtn, false);
        yesBtn.setDefaultButton(true);
        noBtn.setCancelButton(true);

        // Y/N shortcuts (kept)
        pane.getScene().addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.Y) {
                alert.setResult(yes);
                alert.hide();
                e.consume();
            } else if (e.getCode() == KeyCode.N) {
                alert.setResult(no);
                alert.hide();
                e.consume();
            }
        });

        Optional<ButtonType> result = alert.showAndWait();
        return result.orElse(no) == yes;
    }
}
