package seedu.address.model;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

