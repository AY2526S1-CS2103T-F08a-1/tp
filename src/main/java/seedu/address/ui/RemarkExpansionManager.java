package seedu.address.ui;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

/**
 * Manages the expansion and collapse functionality for remark text in UI components.
 * Handles overflow detection and toggle behavior for long text content.
 */
public class RemarkExpansionManager {

    private final Label remarkLabel;
    private final Button toggleButton;
    private final HBox parentContainer;
    private final String fullText;

    private boolean isExpanded = false;

    /**
     * Creates a RemarkExpansionManager for the given UI components.
     *
     * @param remarkLabel the label displaying the remark text
     * @param toggleButton the button to toggle expansion
     * @param parentContainer the parent container for width calculations
     * @param fullText the complete remark text
     */
    public RemarkExpansionManager(Label remarkLabel, Button toggleButton,
                                HBox parentContainer, String fullText) {
        this.remarkLabel = remarkLabel;
        this.toggleButton = toggleButton;
        this.parentContainer = parentContainer;
        this.fullText = fullText;

        initialize();
    }

    /**
     * Initializes the expansion manager with default settings and event handlers.
     */
    private void initialize() {
        if (fullText == null || fullText.trim().isEmpty()) {
            hideToggleButton();
            return;
        }

        // Initialize in collapsed state
        setCollapsedState();

        // Set up button click handler
        toggleButton.setOnAction(event -> toggleExpansion());

        // Check overflow after UI fully rendered
        Platform.runLater(this::checkOverflow);

        // Add listener for width changes (window resizing)
        parentContainer.widthProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(this::checkOverflow);
        });
    }

    /**
     * Toggles between expanded and collapsed states.
     */
    public void toggleExpansion() {
        isExpanded = !isExpanded;

        if (isExpanded) {
            setExpandedState();
        } else {
            setCollapsedState();
        }
    }

    /**
     * Sets the remark to expanded state showing full text with wrapping.
     */
    private void setExpandedState() {
        remarkLabel.setWrapText(true);
        remarkLabel.setText(fullText);
        toggleButton.setText("see less");
    }

    /**
     * Sets the remark to collapsed state showing single line with ellipsis.
     */
    private void setCollapsedState() {
        remarkLabel.setWrapText(false);
        remarkLabel.setTextOverrun(javafx.scene.control.OverrunStyle.ELLIPSIS);
        remarkLabel.setText(fullText);
        toggleButton.setText("see more");
    }

    /**
     * Checks if the remark text overflows and shows/hides toggle button accordingly.
     */
    private void checkOverflow() {
        if (fullText == null || fullText.trim().isEmpty()) {
            hideToggleButton();
            return;
        }

        // Get available width, fallback to card width if maxWidth not set yet
        double availableWidth = parentContainer.widthProperty().get();

        // Don't proceed if width is still not available
        if (availableWidth <= 0) {
            return;
        }

        // Get text width
        // Create a Text node to measure actual text width
        Text textNode = new Text(fullText);
        textNode.setFont(remarkLabel.getFont());

        double textWidth = textNode.getBoundsInLocal().getWidth();

        if (textWidth > availableWidth) {
            // Only show button if text exceeds single line width
            showToggleButton();
        } else if (isExpanded) {
            // Expanded but text fits — hide button
            hideToggleButton();
        } else {
            // Collapsed and text fits — hide button
            hideToggleButton();
        }
    }

    /**
     * Shows the toggle button.
     */
    private void showToggleButton() {
        toggleButton.setVisible(true);
        toggleButton.setManaged(true);
    }

    /**
     * Hides the toggle button.
     */
    private void hideToggleButton() {
        toggleButton.setVisible(false);
        toggleButton.setManaged(false);
    }
}
