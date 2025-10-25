package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_COMPANIES_LISTED_OVERVIEW;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalCompanies.ALPHA;
import static seedu.address.testutil.TypicalCompanies.CONS;
import static seedu.address.testutil.TypicalCompanies.HOLLY;
import static seedu.address.testutil.TypicalCompanies.getTypicalAddressBook;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.company.Status;

/**
 * Contains integration tests (interaction with the Model) for {@code FilterCommand}.
 */
public class FilterCommandTest {
    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private final Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    // Verifies equals method works correctly for FilterCommand objects
    @Test
    public void equals() {
        FilterCommand byApplied = new FilterCommand(new Status("applied"));
        FilterCommand byOa = new FilterCommand(new Status("oa"));

        // same object -> returns true
        assertTrue(byApplied.equals(byApplied));

        // same values -> returns true
        FilterCommand byAppliedCopy = new FilterCommand(new Status("applied"));
        assertTrue(byApplied.equals(byAppliedCopy));

        // different types -> returns false
        assertFalse(byApplied.equals(1));
        assertFalse(byApplied.equals("applied"));

        // null -> returns false
        assertFalse(byApplied.equals(null));

        // different status -> returns false
        assertFalse(byApplied.equals(byOa));

        // case insensitive -> returns true
        FilterCommand byAppliedUpperCase = new FilterCommand(new Status("APPLIED"));
        assertTrue(byApplied.equals(byAppliedUpperCase));
    }

    // Ensures filtering finds the correct company with given status (1 company found)
    @Test
    public void execute_matchingStatus_singleCompanyFound() {
        String expectedMessage = String.format(MESSAGE_COMPANIES_LISTED_OVERVIEW, 1);
        FilterCommand command = new FilterCommand(new Status("applied"));
        expectedModel.updateFilteredCompanyList(c -> c.getStatus().equals(new Status("applied")));
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(CONS), model.getFilteredCompanyList());
    }

    // Ensures filtering finds the correct company with given status (multiple companies found)
    @Test
    public void execute_matchingStatus_multipleCompaniesFound() {
        String expectedMessage = String.format(MESSAGE_COMPANIES_LISTED_OVERVIEW, 2);
        FilterCommand command = new FilterCommand(new Status("to-apply"));
        expectedModel.updateFilteredCompanyList(c -> c.getStatus().equals(new Status("to-apply")));
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Arrays.asList(ALPHA, HOLLY), model.getFilteredCompanyList());
    }

    // Ensures filtering correctly yields empty list when no matches.
    @Test
    public void execute_noCompanyWithStatus_zeroFound() {
        String expectedMessage = String.format(MESSAGE_COMPANIES_LISTED_OVERVIEW, 0);
        FilterCommand command = new FilterCommand(new Status("accepted"));
        expectedModel.updateFilteredCompanyList(c -> c.getStatus().equals(new Status("accepted")));
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(Collections.emptyList(), model.getFilteredCompanyList());
    }

    // Verifies filtering doesn't mutate the model's address book contents.
    @Test
    public void execute_filterDoesNotModifyOriginalList() {
        AddressBook original = new AddressBook(model.getAddressBook());
        FilterCommand command = new FilterCommand(new Status("applied"));
        command.execute(model);
        assertEquals(original, model.getAddressBook());
    }

    // Checks toString() returns the correct string.
    @Test
    public void toStringMethod() {
        Status status = new Status("oa");
        FilterCommand command = new FilterCommand(status);
        String expected = FilterCommand.class.getCanonicalName() + "{status=" + status + "}";
        assertEquals(expected, command.toString());
    }

    // Ensures constructor rejects null status parameter
    @Test
    public void constructor_nullStatus_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new FilterCommand(null));
    }

    // Ensures execute method rejects null model parameter
    @Test
    public void execute_nullModel_throwsNullPointerException() {
        FilterCommand command = new FilterCommand(new Status("applied"));
        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    // Tests filtering on empty address book returns no companies
    @Test
    public void execute_emptyAddressBook_zeroFound() {
        Model emptyModel = new ModelManager(new AddressBook(), new UserPrefs());
        Model expectedEmptyModel = new ModelManager(new AddressBook(), new UserPrefs());

        String expectedMessage = String.format(MESSAGE_COMPANIES_LISTED_OVERVIEW, 0);
        FilterCommand command = new FilterCommand(new Status("applied"));
        expectedEmptyModel.updateFilteredCompanyList(c -> c.getStatus().equals(new Status("applied")));
        assertCommandSuccess(command, emptyModel, expectedMessage, expectedEmptyModel);
        assertEquals(Collections.emptyList(), emptyModel.getFilteredCompanyList());
    }

    // Verifies that filtering is case-insensitive.
    @Test
    public void execute_caseInsensitiveStatusMatch_success() {
        String expectedMessage = String.format(MESSAGE_COMPANIES_LISTED_OVERVIEW, 1);
        FilterCommand command = new FilterCommand(new Status("APPLIED")); // uppercase
        expectedModel.updateFilteredCompanyList(c -> c.getStatus().equals(new Status("applied")));
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }
}
