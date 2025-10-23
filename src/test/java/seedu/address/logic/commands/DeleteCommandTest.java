package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showCompanyAtIndex;
import static seedu.address.testutil.TypicalCompanies.getTypicalAddressBook;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_COMPANY;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_COMPANY;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.company.Company;

/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code DeleteCommand}.
 */
public class DeleteCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    /** Tests that a valid index in an unfiltered list deletes the correct company successfully. */
    @Test
    public void execute_validIndexUnfilteredList_success() {
        Company companyToDelete = model.getFilteredCompanyList().get(INDEX_FIRST_COMPANY.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(List.of(INDEX_FIRST_COMPANY));

        String expectedMessage = String.format(
                DeleteCommand.MESSAGE_DELETE_COMPANY_SUCCESS, Messages.format(companyToDelete));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deleteCompany(companyToDelete);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    /** Tests that multiple valid indices in an unfiltered list delete all specified companies successfully. */
    @Test
    public void execute_multipleIndicesUnfilteredList_success() {
        Company c1 = model.getFilteredCompanyList().get(INDEX_FIRST_COMPANY.getZeroBased());
        Company c2 = model.getFilteredCompanyList().get(INDEX_SECOND_COMPANY.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(List.of(INDEX_FIRST_COMPANY, INDEX_SECOND_COMPANY));

        String joined = Stream.of(c1, c2).map(Messages::format).collect(Collectors.joining(", "));
        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_COMPANY_SUCCESS, joined);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deleteCompany(c2);
        expectedModel.deleteCompany(c1);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    /** Tests that an invalid index in an unfiltered list throws a {@code CommandException}. */
    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredCompanyList().size() + 1);
        DeleteCommand deleteCommand = new DeleteCommand(List.of(outOfBoundIndex));

        assertCommandFailure(deleteCommand, model, Messages.MESSAGE_INVALID_COMPANY_DISPLAYED_INDEX);
    }

    /** Tests that a valid index in a filtered list deletes the correct company and clears the filtered view. */
    @Test
    public void execute_validIndexFilteredList_success() {
        showCompanyAtIndex(model, INDEX_FIRST_COMPANY);

        Company companyToDelete = model.getFilteredCompanyList().get(INDEX_FIRST_COMPANY.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(List.of(INDEX_FIRST_COMPANY));

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_COMPANY_SUCCESS,
                Messages.format(companyToDelete));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deleteCompany(companyToDelete);
        showNoCompany(expectedModel);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    /** Tests that an invalid index in a filtered list throws a {@code CommandException}. */
    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showCompanyAtIndex(model, INDEX_FIRST_COMPANY);

        Index outOfBoundIndex = INDEX_SECOND_COMPANY;
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getCompanyList().size());

        DeleteCommand deleteCommand = new DeleteCommand(List.of(outOfBoundIndex));

        assertCommandFailure(deleteCommand, model, Messages.MESSAGE_INVALID_COMPANY_DISPLAYED_INDEX);
    }

    /** Tests that two {@code DeleteCommand} instances with the same target indices are considered equal. */
    @Test
    public void equals_sameValues_returnsTrue() {
        DeleteCommand deleteFirst = new DeleteCommand(List.of(INDEX_FIRST_COMPANY));
        assertEquals(deleteFirst, new DeleteCommand(List.of(INDEX_FIRST_COMPANY)));
    }

    /** Tests that a {@code DeleteCommand} is not equal to an object of a different type. */
    @Test
    public void equals_differentTypes_returnsFalse() {
        DeleteCommand deleteFirst = new DeleteCommand(List.of(INDEX_FIRST_COMPANY));
        assertNotEquals(new Object(), deleteFirst);
    }

    /** Tests that a {@code DeleteCommand} is not equal to {@code null}. */
    @Test
    public void equals_null_returnsFalse() {
        DeleteCommand deleteFirst = new DeleteCommand(List.of(INDEX_FIRST_COMPANY));
        assertNotEquals(null, deleteFirst);
    }

    /** Tests that two {@code DeleteCommand} instances with different single indices are not equal. */
    @Test
    public void equals_differentSingleIndex_returnsFalse() {
        DeleteCommand deleteFirst = new DeleteCommand(List.of(INDEX_FIRST_COMPANY));
        DeleteCommand deleteSecond = new DeleteCommand(List.of(INDEX_SECOND_COMPANY));
        assertNotEquals(deleteFirst, deleteSecond);
    }

    /** Tests that two {@code DeleteCommand} instances with different sets of indices are not equal. */
    @Test
    public void equals_differentMultipleIndices_returnsFalse() {
        DeleteCommand deleteFirst = new DeleteCommand(List.of(INDEX_FIRST_COMPANY));
        DeleteCommand deleteBoth = new DeleteCommand(List.of(INDEX_FIRST_COMPANY, INDEX_SECOND_COMPANY));
        assertNotEquals(deleteFirst, deleteBoth);
    }

    /** Tests that the {@code toString()} method returns the correct string representation of the command. */
    @Test
    public void toStringMethod() {
        DeleteCommand deleteCommand = new DeleteCommand(List.of(Index.fromOneBased(1), Index.fromOneBased(3)));
        String expected = DeleteCommand.class.getCanonicalName()
                + "{targetIndices=" + List.of(Index.fromOneBased(1), Index.fromOneBased(3)) + "}";
        assertEquals(expected, deleteCommand.toString());
    }

    /** Updates {@code model}'s filtered list to show no companies. */
    private void showNoCompany(Model model) {
        model.updateFilteredCompanyList(p -> false);
        assertTrue(model.getFilteredCompanyList().isEmpty());
    }
}
