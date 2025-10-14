package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import seedu.address.model.company.Company;
import seedu.address.testutil.CompanyBuilder;

/**
 * Minimal test to execute CompanyCard constructor for coverage.
 */
public class CompanyCardTest {

    @Test
    public void constructor_executesWithoutError_usesGivenCompany() {
        // Build a company with placeholder values to hit placeholder branches
        Company company = new CompanyBuilder()
                .withName("Acme Corp")
                .withPhone("000")
                .withEmail("noemailprovided@placeholder.com")
                .withAddress("No address provided")
                .withRemark("No remark provided")
                .withTags()
                .build();

        CompanyCard card = new CompanyCard(company, 1);

        // Sanity check: the card holds the same company
        assertEquals(company, card.company);
    }
}

