package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.company.Address;
import seedu.address.model.company.Company;
import seedu.address.model.company.Email;
import seedu.address.model.company.Name;
import seedu.address.model.company.Phone;
import seedu.address.model.company.Remark;
import seedu.address.model.company.Status;
import seedu.address.model.tag.Tag;

/**
 * Unit tests for MetricsCalculator to verify metrics calculation logic
 * without GUI dependencies.
 */
public class MetricsCalculatorTest {

    private MetricsCalculator metricsCalculator;
    private AddressBook addressBook;

    @BeforeEach
    public void setUp() {
        metricsCalculator = new MetricsCalculator();
        addressBook = new AddressBook();
    }

    /**
     * Helper method to add multiple companies with specified statuses to the address book.
     */
    private void addCompaniesToAddressBook(String... statuses) throws Exception {
        for (int i = 0; i < statuses.length; i++) {
            addressBook.addCompany(createCompany("Company" + i, statuses[i]));
        }
    }

    /**
     * Helper method to assert basic metrics properties.
     */
    private void assertBasicMetrics(MetricsCalculator.MetricsData result, int expectedTotal, boolean expectedHasData) {
        assertEquals(expectedTotal, result.getTotalCompanies());
        assertEquals(expectedHasData, result.hasData());
    }

    /**
     * Helper method to assert status counts match expected values.
     */
    private void assertStatusCounts(MetricsCalculator.MetricsData result, Map<String, Long> expectedCounts) {
        for (Map.Entry<String, Long> entry : expectedCounts.entrySet()) {
            assertEquals(entry.getValue(), result.getStatusCount(entry.getKey()),
                "Status " + entry.getKey() + " should have " + entry.getValue() + " companies");
        }
    }

    /**
     * Helper method to assert status percentages match expected values.
     */
    private void assertStatusPercentages(MetricsCalculator.MetricsData result,
                                        Map<String, Double> expectedPercentages) {
        for (Map.Entry<String, Double> entry : expectedPercentages.entrySet()) {
            assertEquals(entry.getValue(), result.getStatusPercentage(entry.getKey()), 0.01,
                "Status " + entry.getKey() + " should have " + entry.getValue() + "% of companies");
        }
    }

    @Test
    public void calculateMetrics_nullOrEmptyAddressBook_returnsEmptyMetrics() {
        // Test null address book
        MetricsCalculator.MetricsData nullResult = metricsCalculator.calculateMetrics(null);
        assertBasicMetrics(nullResult, 0, false);
        assertTrue(nullResult.getStatusCounts().isEmpty());

        // Test empty address book
        MetricsCalculator.MetricsData emptyResult = metricsCalculator.calculateMetrics(addressBook);
        assertBasicMetrics(emptyResult, 0, false);
        assertTrue(emptyResult.getStatusCounts().isEmpty());
        assertEquals(0.0, emptyResult.getStatusPercentage("TO-APPLY"));
    }

    @Test
    public void calculateMetrics_singleCompany_returnsCorrectMetrics() throws Exception {
        addCompaniesToAddressBook("applied");

        MetricsCalculator.MetricsData result = metricsCalculator.calculateMetrics(addressBook);

        assertBasicMetrics(result, 1, true);
        assertStatusCounts(result, Map.of("APPLIED", 1L, "TO-APPLY", 0L));
        assertStatusPercentages(result, Map.of("APPLIED", 100.0, "TO-APPLY", 0.0));
    }

    @Test
    public void calculateMetrics_multipleCompaniesVariousStatuses_returnsCorrectCounts() throws Exception {
        addCompaniesToAddressBook("applied", "applied", "to-apply", "offered", "rejected");

        MetricsCalculator.MetricsData result = metricsCalculator.calculateMetrics(addressBook);

        assertBasicMetrics(result, 5, true);
        assertStatusCounts(result, Map.of(
            "APPLIED", 2L,
            "TO-APPLY", 1L,
            "OFFERED", 1L,
            "REJECTED", 1L,
            "OA", 0L
        ));
        assertStatusPercentages(result, Map.of(
            "APPLIED", 40.0,
            "TO-APPLY", 20.0,
            "OFFERED", 20.0,
            "REJECTED", 20.0,
            "OA", 0.0
        ));
    }

    @Test
    public void calculateMetrics_allStatusTypes_returnsCorrectMetrics() throws Exception {
        // Test all possible status types - dynamically get from Status enum
        String[] allStatuses = Arrays.stream(Status.Stage.values())
                .map(Status::toUserInputString)
                .toArray(String[]::new);

        addCompaniesToAddressBook(allStatuses);

        MetricsCalculator.MetricsData result = metricsCalculator.calculateMetrics(addressBook);

        int totalStatusTypes = Status.Stage.values().length;
        assertBasicMetrics(result, totalStatusTypes, true);

        // Build expected counts and percentages maps
        Map<String, Long> expectedCounts = new HashMap<>();
        Map<String, Double> expectedPercentages = new HashMap<>();
        double expectedPercentage = 100.0 / totalStatusTypes;

        Arrays.stream(Status.Stage.values())
                .map(Status::toUserInputString)
                .map(String::toUpperCase)
                .forEach(status -> {
                    expectedCounts.put(status, 1L);
                    expectedPercentages.put(status, expectedPercentage);
                });

        assertStatusCounts(result, expectedCounts);
        assertStatusPercentages(result, expectedPercentages);
    }

    @Test
    public void calculateMetrics_duplicateStatuses_aggregatesCorrectly() throws Exception {
        addCompaniesToAddressBook("applied", "applied", "applied", "rejected", "rejected");

        MetricsCalculator.MetricsData result = metricsCalculator.calculateMetrics(addressBook);

        assertBasicMetrics(result, 5, true);
        assertStatusCounts(result, Map.of(
            "APPLIED", 3L,
            "REJECTED", 2L,
            "TO-APPLY", 0L
        ));
        assertStatusPercentages(result, Map.of(
            "APPLIED", 60.0,
            "REJECTED", 40.0,
            "TO-APPLY", 0.0
        ));
    }


    /**
     * Helper method to create a Company with minimal required fields.
     */
    private Company createCompany(String name, String status) throws Exception {
        Name companyName = new Name(name);
        Phone phone = new Phone("12345678");
        Email email = new Email("test@example.com");
        Address address = new Address("123 Test Street");
        Remark remark = new Remark("");
        Status companyStatus = new Status(status);

        return new Company(companyName, phone, email, address,
                          java.util.Collections.singleton(new Tag("test")), remark, companyStatus);
    }
}

