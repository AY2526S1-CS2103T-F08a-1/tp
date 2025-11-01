package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
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

    @Test
    public void calculateMetrics_nullAddressBook_returnsEmptyMetrics() {
        MetricsCalculator.MetricsData result = metricsCalculator.calculateMetrics(null);

        assertEquals(0, result.getTotalCompanies());
        assertTrue(result.getStatusCounts().isEmpty());
        assertFalse(result.hasData());
    }

    @Test
    public void calculateMetrics_emptyAddressBook_returnsEmptyMetrics() {
        MetricsCalculator.MetricsData result = metricsCalculator.calculateMetrics(addressBook);

        assertEquals(0, result.getTotalCompanies());
        assertTrue(result.getStatusCounts().isEmpty());
        assertFalse(result.hasData());
        assertEquals(0.0, result.getStatusPercentage("TO-APPLY"));
    }

    @Test
    public void calculateMetrics_singleCompany_returnsCorrectMetrics() throws Exception {
        Company company = createCompany("Google", "applied");
        addressBook.addCompany(company);

        MetricsCalculator.MetricsData result = metricsCalculator.calculateMetrics(addressBook);

        assertEquals(1, result.getTotalCompanies());
        assertTrue(result.hasData());
        assertEquals(1L, result.getStatusCount("APPLIED"));
        assertEquals(0L, result.getStatusCount("TO-APPLY"));
        assertEquals(100.0, result.getStatusPercentage("APPLIED"));
        assertEquals(0.0, result.getStatusPercentage("TO-APPLY"));
    }

    @Test
    public void calculateMetrics_multipleCompaniesVariousStatuses_returnsCorrectCounts() throws Exception {
        // Add companies with different statuses
        addressBook.addCompany(createCompany("Google", "applied"));
        addressBook.addCompany(createCompany("Meta", "applied"));
        addressBook.addCompany(createCompany("Microsoft", "to-apply"));
        addressBook.addCompany(createCompany("Apple", "offered"));
        addressBook.addCompany(createCompany("Netflix", "rejected"));

        MetricsCalculator.MetricsData result = metricsCalculator.calculateMetrics(addressBook);

        assertEquals(5, result.getTotalCompanies());
        assertTrue(result.hasData());

        // Verify counts
        assertEquals(2L, result.getStatusCount("APPLIED"));
        assertEquals(1L, result.getStatusCount("TO-APPLY"));
        assertEquals(1L, result.getStatusCount("OFFERED"));
        assertEquals(1L, result.getStatusCount("REJECTED"));
        assertEquals(0L, result.getStatusCount("OA"));

        // Verify percentages
        assertEquals(40.0, result.getStatusPercentage("APPLIED"), 0.01);
        assertEquals(20.0, result.getStatusPercentage("TO-APPLY"), 0.01);
        assertEquals(20.0, result.getStatusPercentage("OFFERED"), 0.01);
        assertEquals(20.0, result.getStatusPercentage("REJECTED"), 0.01);
        assertEquals(0.0, result.getStatusPercentage("OA"), 0.01);
    }

    @Test
    public void calculateMetrics_allStatusTypes_returnsCorrectMetrics() throws Exception {
        // Test all possible status types - dynamically get from Status enum
        String[] allStatuses = Arrays.stream(Status.Stage.values())
                .map(Status::toUserInputString)
                .toArray(String[]::new);

        for (int i = 0; i < allStatuses.length; i++) {
            addressBook.addCompany(createCompany("Company" + i, allStatuses[i]));
        }

        MetricsCalculator.MetricsData result = metricsCalculator.calculateMetrics(addressBook);

        int totalStatusTypes = Status.Stage.values().length;
        assertEquals(totalStatusTypes, result.getTotalCompanies());
        assertTrue(result.hasData());

        // Each status should have exactly 1 company
        List<String> allStatusesUppercase = Arrays.stream(Status.Stage.values())
                .map(Status::toUserInputString)
                .map(String::toUpperCase)
                .collect(java.util.stream.Collectors.toList());

        for (String statusUpper : allStatusesUppercase) {
            assertEquals(1L, result.getStatusCount(statusUpper),
                "Status " + statusUpper + " should have exactly 1 company");
        }

        // Each should have equal percentage
        double expectedPercentage = 100.0 / totalStatusTypes;
        for (String statusUpper : allStatusesUppercase) {
            assertEquals(expectedPercentage, result.getStatusPercentage(statusUpper), 0.01,
                "Status " + statusUpper + " should have " + expectedPercentage + "% of companies");
        }
    }

    @Test
    public void calculateMetrics_duplicateStatuses_aggregatesCorrectly() throws Exception {
        // Add multiple companies with same status
        for (int i = 0; i < 3; i++) {
            addressBook.addCompany(createCompany("Applied" + i, "applied"));
        }
        for (int i = 0; i < 2; i++) {
            addressBook.addCompany(createCompany("Rejected" + i, "rejected"));
        }

        MetricsCalculator.MetricsData result = metricsCalculator.calculateMetrics(addressBook);

        assertEquals(5, result.getTotalCompanies());
        assertEquals(3L, result.getStatusCount("APPLIED"));
        assertEquals(2L, result.getStatusCount("REJECTED"));
        assertEquals(0L, result.getStatusCount("TO-APPLY"));

        assertEquals(60.0, result.getStatusPercentage("APPLIED"));
        assertEquals(40.0, result.getStatusPercentage("REJECTED"));
        assertEquals(0.0, result.getStatusPercentage("TO-APPLY"));
    }

    @Test
    public void metricsData_getStatusCounts_returnsCorrectMap() throws Exception {
        addressBook.addCompany(createCompany("Company1", "applied"));
        addressBook.addCompany(createCompany("Company2", "rejected"));

        MetricsCalculator.MetricsData result = metricsCalculator.calculateMetrics(addressBook);
        Map<String, Long> statusCounts = result.getStatusCounts();

        assertTrue(statusCounts.containsKey("APPLIED"));
        assertTrue(statusCounts.containsKey("REJECTED"));
        assertEquals(1L, statusCounts.get("APPLIED"));
        assertEquals(1L, statusCounts.get("REJECTED"));
    }

    @Test
    public void metricsData_edgeCasePercentages_handlesCorrectly() throws Exception {
        // Test with single company to verify 100% calculation
        addressBook.addCompany(createCompany("OnlyCompany", "applied"));

        MetricsCalculator.MetricsData result = metricsCalculator.calculateMetrics(addressBook);

        assertEquals(100.0, result.getStatusPercentage("APPLIED"));
        assertEquals(0.0, result.getStatusPercentage("REJECTED"));
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

