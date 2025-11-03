package seedu.address.ui;

import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.MetricsCalculator;
import seedu.address.model.ReadOnlyAddressBook;

/**
 * Controller for the metrics window that displays application status statistics.
 */
public class MetricsWindow extends ClosableWindow {

    private static final Logger logger = LogsCenter.getLogger(MetricsWindow.class);
    private static final String FXML = "MetricsWindow.fxml";

    @FXML
    private VBox statusMetrics;

    private final MetricsCalculator metricsCalculator;
    private ReadOnlyAddressBook currentAddressBook;

    /**
     * Creates a new MetricsWindow.
     *
     * @param root Stage to use as the root of the MetricsWindow.
     */
    public MetricsWindow(Stage root) {
        super(FXML, root);
        this.metricsCalculator = new MetricsCalculator();
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

        // Add event handlers to refresh data when window is restored or focused
        stage.iconifiedProperty().addListener((observable, wasIconified, isIconified) -> {
            if (!isIconified && currentAddressBook != null) {
                // Window was restored from minimized state, refresh data
                logger.fine("Metrics window restored from minimized state, refreshing data");
                refreshMetrics();
            }
        });

        stage.focusedProperty().addListener((observable, wasFocused, isFocused) -> {
            if (isFocused && currentAddressBook != null) {
                // Window gained focus, refresh data to ensure it's current
                logger.fine("Metrics window gained focus, refreshing data");
                refreshMetrics();
            }
        });
    }

    /**
     * Sets the address book data for metrics calculation and updates the display.
     *
     * @param addressBook The address book containing company data
     */
    public void setData(ReadOnlyAddressBook addressBook) {
        if (addressBook == null) {
            logger.warning("Attempted to set null address book data for metrics");
            return;
        }

        this.currentAddressBook = addressBook;
        refreshMetrics();
    }

    /**
     * Refreshes the metrics display with the current address book data.
     */
    private void refreshMetrics() {
        if (currentAddressBook == null) {
            return;
        }

        MetricsCalculator.MetricsData metricsData = metricsCalculator.calculateMetrics(currentAddressBook);
        renderMetrics(statusMetrics, metricsData);
    }

    /**
     * Renders the metrics data into the provided VBox container.
     * Clears existing content and populates with formatted metrics display.
     *
     * @param container The VBox container to render metrics into
     * @param metricsData The calculated metrics data to display
     */
    private void renderMetrics(VBox container, MetricsCalculator.MetricsData metricsData) {
        container.getChildren().clear();

        if (!metricsData.hasData()) {
            renderNoDataMessage(container);
            return;
        }

        renderTotalCount(container, metricsData.getTotalCompanies());
        renderSeparator(container);
        renderStatusMetrics(container, metricsData);
    }

    /**
     * Renders a "no data" message when no companies are found.
     */
    private void renderNoDataMessage(VBox container) {
        Label noDataLabel = new Label("No companies found");
        noDataLabel.getStyleClass().add("metrics-no-data");
        container.getChildren().add(noDataLabel);
    }

    /**
     * Renders the total company count.
     */
    private void renderTotalCount(VBox container, long totalCompanies) {
        Label totalLabel = new Label(String.format("Total Companies: %d", totalCompanies));
        totalLabel.getStyleClass().add("metrics-total");
        container.getChildren().add(totalLabel);
    }

    /**
     * Renders a visual separator.
     */
    private void renderSeparator(VBox container) {
        Separator separator = new Separator();
        separator.getStyleClass().add("metrics-separator");
        container.getChildren().add(separator);
    }

    /**
     * Renders individual status metrics in the defined order.
     */
    private void renderStatusMetrics(VBox container, MetricsCalculator.MetricsData metricsData) {
        for (String status : metricsData.getStatusOrder()) {
            long count = metricsData.getStatusCount(status);
            double percentage = metricsData.getStatusPercentage(status);

            String displayText = String.format("%s:  %d (%.1f%%)", status, count, percentage);

            Label statusLabel = new Label(displayText);
            String statusUpperCase = status.toUpperCase().replace('_', '-');
            statusLabel.getStyleClass().addAll("metrics-status",
                    "status-" + statusUpperCase, "status-color-" + statusUpperCase);
            container.getChildren().add(statusLabel);
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
