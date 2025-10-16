package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalCompanies.getTypicalAddressBook;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_COMPANY;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_COMPANY;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.company.Company;
import seedu.address.model.company.Status;
import seedu.address.testutil.CompanyBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for StatusCommand.
 */
public class StatusCommandTest {

    private static final String STATUS_STUB = "hr_interview";

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_statusAcceptedByModel_updateSuccessful() throws Exception {
        Company firstCompany = model.getFilteredCompanyList().get(INDEX_FIRST_COMPANY.getZeroBased());
        Company editedCompany = new CompanyBuilder(firstCompany).withStatus(STATUS_STUB).build();

        StatusCommand statusCommand = new StatusCommand(INDEX_FIRST_COMPANY, new Status(STATUS_STUB));

        String expectedMessage = String.format(StatusCommand.MESSAGE_UPDATE_STATUS_SUCCESS,
                Messages.format(editedCompany));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setCompany(firstCompany, editedCompany);

        assertCommandSuccess(statusCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidCompanyIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredCompanyList().size() + 1);
        StatusCommand statusCommand = new StatusCommand(outOfBoundIndex, new Status(STATUS_STUB));

        assertCommandFailure(statusCommand, model, Messages.MESSAGE_INVALID_COMPANY_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        final StatusCommand standardCommand = new StatusCommand(INDEX_FIRST_COMPANY,
                new Status("to-apply"));

        // same values -> returns true
        StatusCommand commandWithSameValues = new StatusCommand(INDEX_FIRST_COMPANY,
                new Status("to-apply"));
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different index -> returns false
        assertFalse(standardCommand.equals(new StatusCommand(INDEX_SECOND_COMPANY,
                new Status("to-apply"))));

        // different status -> returns false
        assertFalse(standardCommand.equals(new StatusCommand(INDEX_FIRST_COMPANY,
                new Status("in-process"))));
    }

    @Test
    public void toStringMethod() {
        Index index = Index.fromOneBased(1);
        Status status = new Status("to-apply");
        StatusCommand statusCommand = new StatusCommand(index, status);
        String expected = StatusCommand.class.getCanonicalName()
                + "{index=" + index + ", status=" + status + "}";
        assertEquals(expected, statusCommand.toString());
    }
}
