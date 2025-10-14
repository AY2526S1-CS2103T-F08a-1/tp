package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import seedu.address.model.company.Company;
import seedu.address.testutil.CompanyBuilder;

/**
 * Minimal test to execute CompanyCard constructor for coverage.
 */
public class CompanyCardTest {

    @Test
    public void constructor_executesWithoutError_usesGivenCompany() throws Exception {
        // Initialize JavaFX toolkit if not already initialized
        try {
            Platform.startup(() -> { /* no-op */ });
        } catch (IllegalStateException ignore) {
            // Toolkit already initialized
        }

        // Build a company with placeholder values to hit placeholder branches
        Company company = new CompanyBuilder()
                .withName("Acme Corp")
                .withPhone("000")
                .withEmail("noemailprovided@placeholder.com")
                .withAddress("No address provided")
                .withRemark("No remark provided")
                .withTags()
                .build();

        AtomicReference<CompanyCard> ref = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            ref.set(new CompanyCard(company, 1));
            latch.countDown();
        });
        latch.await(5, TimeUnit.SECONDS);

        CompanyCard card = ref.get();

        // Sanity check: the card holds the same company
        assertEquals(company, card.company);
    }
}
