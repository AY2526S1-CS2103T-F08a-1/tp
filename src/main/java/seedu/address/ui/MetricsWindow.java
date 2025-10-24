package seedu.address.ui;

import java.util.Map;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.ReadOnlyAddressBook;

/**
 * Controller for the metrics window that displays application status statistics.
 */
public class MetricsWindow extends UiPart<Stage> {

    private static final Logger logger = LogsCenter.getLogger(MetricsWindow.class);
    private static final String FXML = "MetricsWindow.fxml";

    @FXML
    private VBox statusMetrics;

    private ReadOnlyAddressBook addressBook;

    /**
     * Creates a new MetricsWindow.
     *
     * @param root Stage to use as the root of the MetricsWindow.
     */
    public MetricsWindow(Stage root) {
        super(FXML, root);
        configureWindow();
    }

    /**
     * Creates a new MetricsWindow.
     */
    public MetricsWindow() {
        this(new Stage());
    }

    /**
     * Configures the metrics window size and behavior.
     */
    private void configureWindow() {
        Stage stage = getRoot();
        stage.setMinWidth(400);
        stage.setMinHeight(300);
        stage.setMaxWidth(600);
        stage.setMaxHeight(500);
        stage.setWidth(500);
        stage.setHeight(400);
    }

    /**
     * Sets the address book data for metrics calculation.
     */
    public void setData(ReadOnlyAddressBook addressBook) {
        if (addressBook == null) {
            logger.warning("Attempted to set null address book data for metrics");
            return;
        }
        this.addressBook = addressBook;
        updateMetrics();
    }

    /**
     * Updates the metrics display with current status statistics.
     */
    private void updateMetrics() {
        if (addressBook == null) {
            return;
        }

        statusMetrics.getChildren().clear();

        // Count companies by status
        Map<String, Long> statusCounts = addressBook.getCompanyList().stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        company -> company.getStatus().toUserInputString().toUpperCase(),
                        java.util.stream.Collectors.counting()
                ));

        long totalCompanies = addressBook.getCompanyList().size();

        if (totalCompanies == 0) {
            Label noDataLabel = new Label("No companies found");
            noDataLabel.getStyleClass().add("metrics-no-data");
            statusMetrics.getChildren().add(noDataLabel);
            return;
        }

        // Add total count
        Label totalLabel = new Label(String.format("Total Companies: %d", totalCompanies));
        totalLabel.getStyleClass().add("metrics-total");
        statusMetrics.getChildren().add(totalLabel);

        // Create a separator
        Separator separator = new Separator();
        separator.getStyleClass().add("metrics-separator");
        statusMetrics.getChildren().add(separator);

        // Define the order of statuses to display
        String[] statusOrder = {
            "TO-APPLY", "APPLIED", "OA", "TECH-INTERVIEW",
            "HR-INTERVIEW", "IN-PROCESS", "REJECTED", "OFFERED", "ACCEPTED"
        };

        // Display metrics for each status in order
        for (String status : statusOrder) {
            long count = statusCounts.getOrDefault(status, 0L);
            double percentage = (count * 100.0) / totalCompanies;

            String displayText = String.format("%s:  %d (%.1f%%)",
                    status, count, percentage);

            Label statusLabel = new Label(displayText);
            statusLabel.getStyleClass().addAll("metrics-status", "status-" + status.toLowerCase());
            statusMetrics.getChildren().add(statusLabel);
        }

    }


    /**
     * Shows the metrics window.
     */
    public void show() {
        logger.fine("Showing metrics window.");
        getRoot().show();
        getRoot().centerOnScreen();
    }

    /**
     * Returns true if the metrics window is currently being shown.
     */
    public boolean isShowing() {
        return getRoot().isShowing();
    }

    /**
     * Hides the metrics window.
     */
    public void hide() {
        getRoot().hide();
    }

    /**
     * Focuses on the metrics window.
     */
    public void focus() {
        getRoot().requestFocus();
    }
}
