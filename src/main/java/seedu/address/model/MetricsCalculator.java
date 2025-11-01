package seedu.address.model;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import seedu.address.model.company.Status;

/**
 * Calculates metrics and statistics for company data.
 */
public class MetricsCalculator {

    private static final List<String> DEFAULT_STATUS_ORDER = Arrays.stream(Status.Stage.values())
            .map(Status::toUserInputString)
            .map(String::toUpperCase)
            .collect(Collectors.toList());

    private final List<String> statusOrder;

    /**
     * Creates a MetricsCalculator with default status ordering.
     */
    public MetricsCalculator() {
        this.statusOrder = DEFAULT_STATUS_ORDER;
    }

    /**
     * Calculates metrics for the given address book data.
     *
     * @param addressBook The address book containing company data
     * @return MetricsData object containing calculated statistics
     */
    public MetricsData calculateMetrics(ReadOnlyAddressBook addressBook) {
        if (addressBook == null) {
            return new MetricsData(0, Map.of(), statusOrder);
        }

        long totalCompanies = addressBook.getCompanyList().size();

        if (totalCompanies == 0) {
            return new MetricsData(0, Map.of(), statusOrder);
        }

        Map<String, Long> statusCounts = addressBook.getCompanyList().stream()
                .collect(Collectors.groupingBy(
                        company -> company.getStatus().toUserInputString().toUpperCase(),
                        Collectors.counting()
                ));

        return new MetricsData(totalCompanies, statusCounts, statusOrder);
    }

    /**
     * Renders the metrics data into the provided VBox container.
     * Clears existing content and populates with formatted metrics display.
     *
     * @param container The VBox container to render metrics into
     * @param metricsData The calculated metrics data to display
     */
    public void renderMetrics(VBox container, MetricsData metricsData) {
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
    private void renderStatusMetrics(VBox container, MetricsData metricsData) {
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
     * Data class containing calculated metrics.
     */
    public static class MetricsData {
        private final long totalCompanies;
        private final Map<String, Long> statusCounts;
        private final List<String> statusOrder;

        /**
         * Creates a MetricsData object with the given metrics.
         *
         * @param totalCompanies The total number of companies
         * @param statusCounts Map of status to count
         * @param statusOrder List of statuses in display order
         */
        public MetricsData(long totalCompanies, Map<String, Long> statusCounts, List<String> statusOrder) {
            this.totalCompanies = totalCompanies;
            this.statusCounts = statusCounts;
            this.statusOrder = statusOrder;
        }

        public long getTotalCompanies() {
            return totalCompanies;
        }

        public Map<String, Long> getStatusCounts() {
            return statusCounts;
        }

        public List<String> getStatusOrder() {
            return statusOrder;
        }

        public long getStatusCount(String status) {
            return statusCounts.getOrDefault(status, 0L);
        }

        public double getStatusPercentage(String status) {
            if (totalCompanies == 0) {
                return 0.0;
            }
            return (getStatusCount(status) * 100.0) / totalCompanies;
        }

        public boolean hasData() {
            return totalCompanies > 0;
        }
    }
}

