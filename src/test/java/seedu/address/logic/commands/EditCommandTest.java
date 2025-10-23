package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.DESC_AIRBUS;
import static seedu.address.logic.commands.CommandTestUtil.DESC_BOEING;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOEING;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOEING;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOEING;
import static seedu.address.logic.commands.CommandTestUtil.VALID_REMARK_BOEING;
import static seedu.address.logic.commands.CommandTestUtil.VALID_STATUS_AIRBUS;
import static seedu.address.logic.commands.CommandTestUtil.VALID_STATUS_BOEING;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_GOOD_PAY;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showCompanyAtIndex;
import static seedu.address.testutil.TypicalCompanies.getTypicalAddressBook;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_COMPANY;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_COMPANY;
import static seedu.address.testutil.TypicalIndexes.INDEX_THIRD_COMPANY;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditCommand.EditCompanyDescriptor;
import seedu.address.logic.parser.IndexParser;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.company.Company;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.CompanyBuilder;
import seedu.address.testutil.EditCompanyDescriptorBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for EditCommand.
 */
public class EditCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    /**
     * Tests editing a company with all fields specified in an unfiltered list.
     * Ensures that all company fields (name, phone, email, address, tags, remark, status) can be updated
     * simultaneously in a single edit operation.
     * Expected: Command succeeds and the company is updated with all new values.
     */
    @Test
    public void execute_allFieldsSpecifiedUnfilteredList_success() {
        Company editedCompany = new CompanyBuilder().build();
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder(editedCompany).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_COMPANY, descriptor);

        String expectedMessage =
                String.format(EditCommand.MESSAGE_EDIT_COMPANY_SUCCESS, Messages.format(editedCompany));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setCompany(model.getFilteredCompanyList().get(0), editedCompany);

        // Command: edit 1 with every field from descriptor (full company replacement).
        // Expectation: success message and model updated to the new company details.
        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    /**
     * Tests editing a company with only some fields specified in an unfiltered list.
     * Verifies that partial edits work correctly and unspecified fields remain unchanged.
     * Expected: Command succeeds with only specified fields updated.
     */
    @Test
    public void execute_someFieldsSpecifiedUnfilteredList_success() {
        Index indexLastCompany = Index.fromOneBased(model.getFilteredCompanyList().size());
        Company lastCompany = model.getFilteredCompanyList().get(indexLastCompany.getZeroBased());

        CompanyBuilder companyInList = new CompanyBuilder(lastCompany);
        Company editedCompany = companyInList.withName(VALID_NAME_BOEING).withPhone(VALID_PHONE_BOEING)
                .withTags(VALID_TAG_GOOD_PAY).withRemark(VALID_REMARK_BOEING).build();

        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder().withName(VALID_NAME_BOEING)
                .withPhone(VALID_PHONE_BOEING).withTags(VALID_TAG_GOOD_PAY).withRemark(VALID_REMARK_BOEING)
                .build();
        EditCommand editCommand = new EditCommand(indexLastCompany, descriptor);

        String expectedMessage =
                String.format(EditCommand.MESSAGE_EDIT_COMPANY_SUCCESS, Messages.format(editedCompany));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setCompany(lastCompany, editedCompany);

        // Command: edit <last index> with name/phone/tags/remark only.
        // Expectation: success and only those fields change for the last company.
        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    /**
     * Tests editing a company with no fields specified (empty descriptor) in an unfiltered list.
     * Validates that editing with an empty descriptor is allowed and results in no actual changes.
     * Expected: Command succeeds but company remains unchanged.
     */
    @Test
    public void execute_noFieldSpecifiedUnfilteredList_success() {
        EditCommand editCommand = new EditCommand(INDEX_FIRST_COMPANY, new EditCompanyDescriptor());
        Company editedCompany = model.getFilteredCompanyList().get(INDEX_FIRST_COMPANY.getZeroBased());

        String expectedMessage =
                String.format(EditCommand.MESSAGE_EDIT_COMPANY_SUCCESS, Messages.format(editedCompany));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    /**
     * Tests editing a company in a filtered list.
     * Ensures that editing works correctly when the company list is filtered to show only specific companies.
     * Expected: Command succeeds and the company in the filtered view is updated.
     */
    @Test
    public void execute_filteredList_success() {
        showCompanyAtIndex(model, INDEX_FIRST_COMPANY);

        Company companyInFilteredList = model.getFilteredCompanyList().get(INDEX_FIRST_COMPANY.getZeroBased());
        Company editedCompany = new CompanyBuilder(companyInFilteredList).withName(VALID_NAME_BOEING).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_COMPANY,
                new EditCompanyDescriptorBuilder().withName(VALID_NAME_BOEING).build());

        String expectedMessage =
                String.format(EditCommand.MESSAGE_EDIT_COMPANY_SUCCESS, Messages.format(editedCompany));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setCompany(model.getFilteredCompanyList().get(0), editedCompany);

        // Command: edit 1 in filtered view to rename company to Boeing.
        // Expectation: success and filtered company now shows renamed entry.
        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    /**
     * Tests that editing a company to duplicate another company's name in an unfiltered list fails.
     * Since companies are identified by name (case-insensitive), this operation should be rejected.
     * Expected: Command fails with duplicate company error message.
     */
    @Test
    public void execute_duplicateCompanyUnfilteredList_failure() {
        Company firstCompany = model.getFilteredCompanyList().get(INDEX_FIRST_COMPANY.getZeroBased());
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder(firstCompany).build();
        EditCommand editCommand = new EditCommand(INDEX_SECOND_COMPANY, descriptor);

        // Command: edit 2 to clone company 1's details.
        // Expectation: failure with duplicate company message.
        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_DUPLICATE_COMPANY);
    }

    /**
     * Tests that editing a company in a filtered list to duplicate another company fails.
     * Validates that duplicate detection works even when the list is filtered.
     * Expected: Command fails with duplicate company error message.
     */
    @Test
    public void execute_duplicateCompanyFilteredList_failure() {
        showCompanyAtIndex(model, INDEX_FIRST_COMPANY);

        // edit company in filtered list into a duplicate in address book
        Company companyInList = model.getAddressBook().getCompanyList().get(INDEX_SECOND_COMPANY.getZeroBased());
        EditCommand editCommand = new EditCommand(INDEX_FIRST_COMPANY,
                new EditCompanyDescriptorBuilder(companyInList).build());

        // Command: edit visible company 1 to match hidden company 2.
        // Expectation: failure due to duplicate company detection.
        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_DUPLICATE_COMPANY);
    }

    /**
     * Tests editing with an invalid company index in an unfiltered list.
     * The index is larger than the total number of companies in the address book.
     * Expected: Command fails with index out of range error message.
     */
    @Test
    public void execute_invalidCompanyIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredCompanyList().size() + 1);
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder().withName(VALID_NAME_BOEING).build();
        EditCommand editCommand = new EditCommand(outOfBoundIndex, descriptor);

        String expectedMessage = String.format(IndexParser.MESSAGE_INDEX_OUT_OF_RANGE,
                outOfBoundIndex.getOneBased(), model.getFilteredCompanyList().size());
        // Command: edit <size+1> ... (index outside unfiltered list).
        // Expectation: failure with index out-of-range message.
        assertCommandFailure(editCommand, model, expectedMessage);
    }

    /**
     * Tests editing with an invalid index in a filtered list.
     * The index is larger than the filtered list size but within the full address book size.
     * Expected: Command fails with index out of range error for the filtered list.
     */
    @Test
    public void execute_invalidCompanyIndexFilteredList_failure() {
        showCompanyAtIndex(model, INDEX_FIRST_COMPANY);
        Index outOfBoundIndex = INDEX_SECOND_COMPANY;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getCompanyList().size());

        EditCommand editCommand = new EditCommand(outOfBoundIndex,
                new EditCompanyDescriptorBuilder().withName(VALID_NAME_BOEING).build());

        String expectedMessage = String.format(IndexParser.MESSAGE_INDEX_OUT_OF_RANGE,
                outOfBoundIndex.getOneBased(), model.getFilteredCompanyList().size());
        // Command: edit 2 while filtered list only shows one entry.
        // Expectation: failure indicating index exceeds filtered list.
        assertCommandFailure(editCommand, model, expectedMessage);
    }

    // ================ Single Edit Edge Case Tests ================

    /**
     * Tests editing only the status field of a company in an unfiltered list.
     * Validates that individual field edits work correctly without affecting other fields.
     * Expected: Command succeeds with only the status field updated.
     */
    @Test
    public void execute_editStatusOnlyUnfilteredList_success() {
        Index indexLastCompany = Index.fromOneBased(model.getFilteredCompanyList().size());
        Company lastCompany = model.getFilteredCompanyList().get(indexLastCompany.getZeroBased());

        Company editedCompany = new CompanyBuilder(lastCompany).withStatus(VALID_STATUS_BOEING).build();
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder().withStatus(VALID_STATUS_BOEING).build();
        EditCommand editCommand = new EditCommand(indexLastCompany, descriptor);

        String expectedMessage =
                String.format(EditCommand.MESSAGE_EDIT_COMPANY_SUCCESS, Messages.format(editedCompany));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setCompany(lastCompany, editedCompany);

        // Command: edit <last index> status/to-apply -> status/to-apply (Boeing).
        // Expectation: success updating only the status field.
        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    /**
     * Tests editing only the remark field of a company in an unfiltered list.
     * Ensures that remarks can be added or updated independently of other fields.
     * Expected: Command succeeds with only the remark field updated.
     */
    @Test
    public void execute_editRemarkOnlyUnfilteredList_success() {
        Company firstCompany = model.getFilteredCompanyList().get(INDEX_FIRST_COMPANY.getZeroBased());

        Company editedCompany = new CompanyBuilder(firstCompany).withRemark(VALID_REMARK_BOEING).build();
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder().withRemark(VALID_REMARK_BOEING).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_COMPANY, descriptor);

        String expectedMessage =
                String.format(EditCommand.MESSAGE_EDIT_COMPANY_SUCCESS, Messages.format(editedCompany));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setCompany(firstCompany, editedCompany);

        // Command: edit 1 r/"Lacking pay but good experience".
        // Expectation: success updating only the remark field.
        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    /**
     * Tests editing only the tags of a company by replacing them with a single tag.
     * Validates that tag editing replaces all existing tags with the new set.
     * Expected: Command succeeds with tags replaced by the new single tag.
     */
    @Test
    public void execute_editSingleTagUnfilteredList_success() {
        Company secondCompany = model.getFilteredCompanyList().get(INDEX_SECOND_COMPANY.getZeroBased());

        Company editedCompany = new CompanyBuilder(secondCompany).withTags(VALID_TAG_GOOD_PAY).build();
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder().withTags(VALID_TAG_GOOD_PAY).build();
        EditCommand editCommand = new EditCommand(INDEX_SECOND_COMPANY, descriptor);

        String expectedMessage =
                String.format(EditCommand.MESSAGE_EDIT_COMPANY_SUCCESS, Messages.format(editedCompany));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setCompany(secondCompany, editedCompany);

        // Command: edit 2 t/good-pay (replace existing tags).
        // Expectation: success with tags replaced by single value.
        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    /**
     * Tests editing a company name to a different case version of the same name.
     * Since company identity is case-insensitive, editing to uppercase should be treated as same company.
     * Expected: Command succeeds without duplicate company error.
     */
    @Test
    public void execute_editNameCaseDifference_success() {
        Company firstCompany = model.getFilteredCompanyList().get(INDEX_FIRST_COMPANY.getZeroBased());
        String originalName = firstCompany.getName().fullName;
        String upperCaseName = originalName.toUpperCase();

        // Edit to uppercase version of the same name (should be treated as same company)
        Company editedCompany = new CompanyBuilder(firstCompany).withName(upperCaseName).build();
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder().withName(upperCaseName).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_COMPANY, descriptor);

        String expectedMessage =
                String.format(EditCommand.MESSAGE_EDIT_COMPANY_SUCCESS, Messages.format(editedCompany));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setCompany(firstCompany, editedCompany);

        // Command: edit 1 n/<NAME IN UPPERCASE>.
        // Expectation: success, demonstrating case-insensitive identity check.
        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    /**
     * Tests clearing the remark field by setting it to an empty string.
     * Validates that remarks can be removed from a company.
     * Expected: Command succeeds with the remark field cleared.
     */
    @Test
    public void execute_clearRemarkUnfilteredList_success() {
        Company firstCompany = model.getFilteredCompanyList().get(INDEX_FIRST_COMPANY.getZeroBased());

        Company editedCompany = new CompanyBuilder(firstCompany).withRemark("").build();
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder().withRemark("").build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_COMPANY, descriptor);

        String expectedMessage =
                String.format(EditCommand.MESSAGE_EDIT_COMPANY_SUCCESS, Messages.format(editedCompany));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setCompany(firstCompany, editedCompany);

        // Command: edit 1 r/"" (clear remark).
        // Expectation: success with remark emptied.
        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    /**
     * Tests clearing all tags from a company by specifying no tags in the edit.
     * Ensures that companies can have all their tags removed.
     * Expected: Command succeeds with all tags removed from the company.
     */
    @Test
    public void execute_clearTagsUnfilteredList_success() {
        Company thirdCompany = model.getFilteredCompanyList().get(INDEX_THIRD_COMPANY.getZeroBased());

        Company editedCompany = new CompanyBuilder(thirdCompany).withTags().build();
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder().withTags().build();
        EditCommand editCommand = new EditCommand(INDEX_THIRD_COMPANY, descriptor);

        String expectedMessage =
                String.format(EditCommand.MESSAGE_EDIT_COMPANY_SUCCESS, Messages.format(editedCompany));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setCompany(thirdCompany, editedCompany);

        // Command: edit 3 with no tag values (clear every tag).
        // Expectation: success leaving the company with zero tags.
        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    /**
     * Tests the equals method of EditCommand for various scenarios.
     * Validates equality based on index and descriptor values.
     * Expected: Returns true for equal commands, false otherwise.
     */
    @Test
    public void equals() {
        final EditCommand standardCommand = new EditCommand(INDEX_FIRST_COMPANY, DESC_AIRBUS);

        // same values -> returns true
        EditCompanyDescriptor copyDescriptor = new EditCompanyDescriptor(DESC_AIRBUS);
        EditCommand commandWithSameValues = new EditCommand(INDEX_FIRST_COMPANY, copyDescriptor);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different index -> returns false
        assertFalse(standardCommand.equals(new EditCommand(INDEX_SECOND_COMPANY, DESC_AIRBUS)));

        // different descriptor -> returns false
        assertFalse(standardCommand.equals(new EditCommand(INDEX_FIRST_COMPANY, DESC_BOEING)));
    }

    /**
     * Tests the toString method of EditCommand for single edit operations.
     * Verifies that the string representation includes the index and descriptor.
     * Expected: String matches the expected format with index and descriptor details.
     */
    @Test
    public void toStringMethod() {
        Index index = Index.fromOneBased(1);
        EditCompanyDescriptor editCompanyDescriptor = new EditCompanyDescriptor();
        EditCommand editCommand = new EditCommand(index, editCompanyDescriptor);
        String expected = EditCommand.class.getCanonicalName() + "{index=" + index
                + ", indices=null, editCompanyDescriptor=" + editCompanyDescriptor + "}";
        // String form should echo the single-index command metadata.
        assertEquals(expected, editCommand.toString());
    }

    // ================ Batch Edit Tests ================
    // Edge case scenarios for testing the batch editing of tags were generated with AI assistance
    // (OpenAI ChatGPT) to improve coverage and identify non-trivial cases.

    /**
     * Tests batch editing multiple companies at valid indices in an unfiltered list.
     * Validates that tags can be updated for multiple companies simultaneously.
     * Expected: Command succeeds and updates tags for all specified companies.
     */
    @Test
    public void execute_batchEditValidIndicesUnfilteredList_success() {
        // Edit tags for multiple companies
        List<Index> indices = Arrays.asList(INDEX_FIRST_COMPANY, INDEX_SECOND_COMPANY);
        Set<Tag> newTags = new HashSet<>(Arrays.asList(new Tag("applied")));
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder().withTags("applied").build();
        EditCommand editCommand = new EditCommand(indices, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_BATCH_EDIT_SUCCESS, 2);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        Company firstCompany = model.getFilteredCompanyList().get(INDEX_FIRST_COMPANY.getZeroBased());
        Company secondCompany = model.getFilteredCompanyList().get(INDEX_SECOND_COMPANY.getZeroBased());

        Company editedFirstCompany = new CompanyBuilder(firstCompany).withTags("applied").build();
        Company editedSecondCompany = new CompanyBuilder(secondCompany).withTags("applied").build();

        expectedModel.setCompany(firstCompany, editedFirstCompany);
        expectedModel.setCompany(secondCompany, editedSecondCompany);

        // Command: edit 1,2 t/applied.
        // Expectation: batch success updating tags for both companies.
        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    /**
     * Tests batch editing with only a single index in the list.
     * Ensures that batch edit with one index behaves like a normal single edit.
     * Expected: Command succeeds and updates the single specified company.
     */
    @Test
    public void execute_batchEditSingleIndexUnfilteredList_success() {
        // Single index in a list should work like normal edit
        List<Index> indices = Arrays.asList(INDEX_FIRST_COMPANY);
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder().withTags("applied").build();
        EditCommand editCommand = new EditCommand(indices, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_BATCH_EDIT_SUCCESS, 1);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        Company firstCompany = model.getFilteredCompanyList().get(INDEX_FIRST_COMPANY.getZeroBased());
        Company editedFirstCompany = new CompanyBuilder(firstCompany).withTags("applied").build();
        expectedModel.setCompany(firstCompany, editedFirstCompany);

        // Command: edit 1 (provided as single-element list) t/applied.
        // Expectation: treated like single edit, succeeds.
        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    /**
     * Tests batch editing with one valid and one invalid index.
     * Validates that the command fails if any index is out of bounds.
     * Expected: Command fails with index out of range error message.
     */
    @Test
    public void execute_batchEditInvalidIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredCompanyList().size() + 1);
        List<Index> indices = Arrays.asList(INDEX_FIRST_COMPANY, outOfBoundIndex);
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder().withTags("applied").build();
        EditCommand editCommand = new EditCommand(indices, descriptor);

        String expectedMessage = String.format(IndexParser.MESSAGE_INDEX_OUT_OF_RANGE,
                outOfBoundIndex.getOneBased(), model.getFilteredCompanyList().size());
        // Command: edit 1,<invalid> t/applied.
        // Expectation: failure because one index is out of bounds.
        assertCommandFailure(editCommand, model, expectedMessage);
    }

    /**
     * Tests batch editing with all indices being invalid (out of bounds).
     * Ensures that all invalid indices are properly rejected.
     * Expected: Command fails with index out of range error message for the first invalid index.
     */
    @Test
    public void execute_batchEditAllInvalidIndicesUnfilteredList_failure() {
        Index outOfBound1 = Index.fromOneBased(model.getFilteredCompanyList().size() + 1);
        Index outOfBound2 = Index.fromOneBased(model.getFilteredCompanyList().size() + 2);
        List<Index> indices = Arrays.asList(outOfBound1, outOfBound2);
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder().withTags("applied").build();
        EditCommand editCommand = new EditCommand(indices, descriptor);

        String expectedMessage = String.format(IndexParser.MESSAGE_INDEX_OUT_OF_RANGE,
                outOfBound1.getOneBased(), model.getFilteredCompanyList().size());
        // Command: edit <invalid>,<invalid> t/applied.
        // Expectation: failure flagged on the first invalid index.
        assertCommandFailure(editCommand, model, expectedMessage);
    }

    /**
     * Tests batch editing in a filtered list with valid indices.
     * Validates that batch editing respects the filtered view of companies.
     * Expected: Command succeeds and updates only the visible companies.
     */
    @Test
    public void execute_batchEditFilteredList_success() {
        showCompanyAtIndex(model, INDEX_FIRST_COMPANY);

        // Only first company is shown, so only index 1 should be valid
        List<Index> indices = Arrays.asList(INDEX_FIRST_COMPANY);
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder().withTags("applied").build();
        EditCommand editCommand = new EditCommand(indices, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_BATCH_EDIT_SUCCESS, 1);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        Company firstCompany = model.getFilteredCompanyList().get(INDEX_FIRST_COMPANY.getZeroBased());
        Company editedFirstCompany = new CompanyBuilder(firstCompany).withTags("applied").build();
        expectedModel.setCompany(firstCompany, editedFirstCompany);

        // Command: edit 1 (only visible index) t/applied while filtered.
        // Expectation: success updating the visible company.
        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    /**
     * Tests batch editing in a filtered list with an index that's valid in full list but not in filtered list.
     * Ensures that indices are validated against the filtered list, not the full list.
     * Expected: Command fails with index out of range error message.
     */
    @Test
    public void execute_batchEditFilteredListInvalidIndex_failure() {
        showCompanyAtIndex(model, INDEX_FIRST_COMPANY);

        // Second company is not shown in filtered list
        List<Index> indices = Arrays.asList(INDEX_FIRST_COMPANY, INDEX_SECOND_COMPANY);
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder().withTags("applied").build();
        EditCommand editCommand = new EditCommand(indices, descriptor);

        String expectedMessage = String.format(IndexParser.MESSAGE_INDEX_OUT_OF_RANGE,
                INDEX_SECOND_COMPANY.getOneBased(), model.getFilteredCompanyList().size());
        // Command: edit 1,2 while only index 1 is in filtered results.
        // Expectation: failure because index 2 is out of filtered range.
        assertCommandFailure(editCommand, model, expectedMessage);
    }

    /**
     * Tests batch editing that would result in a duplicate company name.
     * Validates that duplicate company detection works during batch operations.
     * Expected: Command fails with duplicate company error message.
     */
    @Test
    public void execute_batchEditDuplicateCompany_failure() {
        // Try to edit second company to have same name as first company
        Company firstCompany = model.getFilteredCompanyList().get(INDEX_FIRST_COMPANY.getZeroBased());
        List<Index> indices = Arrays.asList(INDEX_SECOND_COMPANY);
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder(firstCompany).build();
        EditCommand editCommand = new EditCommand(indices, descriptor);

        // Command: batch edit [2] with descriptor copying company 1.
        // Expectation: failure due to duplicate company.
        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_DUPLICATE_COMPANY);
    }

    /**
     * Tests batch editing tags for three companies simultaneously.
     * Validates that batch operations work correctly with multiple indices.
     * Expected: Command succeeds and updates tags for all three companies.
     */
    @Test
    public void execute_batchEditTagsOfMultipleCompanies_success() {
        // Edit tags for three companies
        List<Index> indices = Arrays.asList(INDEX_FIRST_COMPANY, INDEX_SECOND_COMPANY, INDEX_THIRD_COMPANY);
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder().withTags("interview").build();
        EditCommand editCommand = new EditCommand(indices, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_BATCH_EDIT_SUCCESS, 3);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        Company firstCompany = model.getFilteredCompanyList().get(INDEX_FIRST_COMPANY.getZeroBased());
        Company secondCompany = model.getFilteredCompanyList().get(INDEX_SECOND_COMPANY.getZeroBased());
        Company thirdCompany = model.getFilteredCompanyList().get(INDEX_THIRD_COMPANY.getZeroBased());

        Company editedFirstCompany = new CompanyBuilder(firstCompany).withTags("interview").build();
        Company editedSecondCompany = new CompanyBuilder(secondCompany).withTags("interview").build();
        Company editedThirdCompany = new CompanyBuilder(thirdCompany).withTags("interview").build();

        expectedModel.setCompany(firstCompany, editedFirstCompany);
        expectedModel.setCompany(secondCompany, editedSecondCompany);
        expectedModel.setCompany(thirdCompany, editedThirdCompany);

        // Command: edit 1,2,3 t/interview.
        // Expectation: success updating tags for all three companies.
        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    /**
     * Tests the equals method for batch edit commands.
     * Validates equality based on the list of indices and descriptor values.
     * Expected: Returns true for equal batch commands, false for different indices or descriptors.
     */
    @Test
    public void equals_batchEditCommand() {
        List<Index> indices1 = Arrays.asList(INDEX_FIRST_COMPANY, INDEX_SECOND_COMPANY);
        List<Index> indices2 = Arrays.asList(INDEX_FIRST_COMPANY, INDEX_THIRD_COMPANY);
        final EditCommand batchCommand1 = new EditCommand(indices1, DESC_AIRBUS);
        final EditCommand batchCommand2 = new EditCommand(indices2, DESC_AIRBUS);

        // same values -> returns true
        EditCommand commandWithSameValues = new EditCommand(indices1, new EditCompanyDescriptor(DESC_AIRBUS));
        assertTrue(batchCommand1.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(batchCommand1.equals(batchCommand1));

        // null -> returns false
        assertFalse(batchCommand1.equals(null));

        // different types -> returns false
        assertFalse(batchCommand1.equals(new ClearCommand()));

        // different indices -> returns false
        assertFalse(batchCommand1.equals(batchCommand2));

        // different descriptor -> returns false
        assertFalse(batchCommand1.equals(new EditCommand(indices1, DESC_BOEING)));

        // single edit vs batch edit -> returns false
        EditCommand singleEditCommand = new EditCommand(INDEX_FIRST_COMPANY, DESC_AIRBUS);
        assertFalse(batchCommand1.equals(singleEditCommand));
    }

    /**
     * Tests the toString method for batch edit commands.
     * Verifies that the string representation includes the list of indices and descriptor.
     * Expected: String matches the expected format with indices list and descriptor details.
     */
    @Test
    public void toStringMethod_batchEdit() {
        List<Index> indices = Arrays.asList(INDEX_FIRST_COMPANY, INDEX_SECOND_COMPANY);
        EditCompanyDescriptor editCompanyDescriptor = new EditCompanyDescriptor();
        EditCommand editCommand = new EditCommand(indices, editCompanyDescriptor);
        String expected = EditCommand.class.getCanonicalName() + "{index=null, indices=" + indices
                + ", editCompanyDescriptor=" + editCompanyDescriptor + "}";
        assertEquals(expected, editCommand.toString());
    }

    /**
     * Tests batch editing the status field for multiple companies.
     * Validates that status can be updated across multiple companies in a single operation.
     * Expected: Command succeeds and updates status for all specified companies.
     */
    @Test
    public void execute_batchEditStatus_success() {
        List<Index> indices = Arrays.asList(INDEX_FIRST_COMPANY, INDEX_SECOND_COMPANY);
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder().withStatus(VALID_STATUS_BOEING).build();
        EditCommand editCommand = new EditCommand(indices, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_BATCH_EDIT_SUCCESS, 2);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        Company firstCompany = model.getFilteredCompanyList().get(INDEX_FIRST_COMPANY.getZeroBased());
        Company secondCompany = model.getFilteredCompanyList().get(INDEX_SECOND_COMPANY.getZeroBased());

        Company editedFirstCompany = new CompanyBuilder(firstCompany).withStatus(VALID_STATUS_BOEING).build();
        Company editedSecondCompany = new CompanyBuilder(secondCompany).withStatus(VALID_STATUS_BOEING).build();

        expectedModel.setCompany(firstCompany, editedFirstCompany);
        expectedModel.setCompany(secondCompany, editedSecondCompany);

        // Command: edit 1,2 status/to-apply.
        // Expectation: success updating status field for both entries.
        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    /**
     * Tests batch editing the remark field for multiple companies with non-contiguous indices.
     * Validates that remarks can be set identically across selected companies.
     * Expected: Command succeeds and updates remarks for companies at indices 1 and 3.
     */
    @Test
    public void execute_batchEditRemark_success() {
        List<Index> indices = Arrays.asList(INDEX_FIRST_COMPANY, INDEX_THIRD_COMPANY);
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder().withRemark(VALID_REMARK_BOEING).build();
        EditCommand editCommand = new EditCommand(indices, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_BATCH_EDIT_SUCCESS, 2);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        Company firstCompany = model.getFilteredCompanyList().get(INDEX_FIRST_COMPANY.getZeroBased());
        Company thirdCompany = model.getFilteredCompanyList().get(INDEX_THIRD_COMPANY.getZeroBased());

        Company editedFirstCompany = new CompanyBuilder(firstCompany).withRemark(VALID_REMARK_BOEING).build();
        Company editedThirdCompany = new CompanyBuilder(thirdCompany).withRemark(VALID_REMARK_BOEING).build();

        expectedModel.setCompany(firstCompany, editedFirstCompany);
        expectedModel.setCompany(thirdCompany, editedThirdCompany);

        // Command: edit 1,3 r/"Lacking pay but good experience".
        // Expectation: success applying remark to both non-contiguous indices.
        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    /**
     * Tests batch editing multiple fields (status, remark, and tags) simultaneously.
     * Validates that multiple fields can be updated together in a batch operation.
     * Expected: Command succeeds and updates all specified fields for all target companies.
     */
    @Test
    public void execute_batchEditMultipleFields_success() {
        List<Index> indices = Arrays.asList(INDEX_SECOND_COMPANY, INDEX_THIRD_COMPANY);
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder()
                .withStatus(VALID_STATUS_AIRBUS).withRemark(VALID_REMARK_BOEING).withTags("applied").build();
        EditCommand editCommand = new EditCommand(indices, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_BATCH_EDIT_SUCCESS, 2);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setCompany(model.getFilteredCompanyList().get(INDEX_SECOND_COMPANY.getZeroBased()),
                new CompanyBuilder(model.getFilteredCompanyList().get(INDEX_SECOND_COMPANY.getZeroBased()))
                        .withStatus(VALID_STATUS_AIRBUS).withRemark(VALID_REMARK_BOEING).withTags("applied").build());
        expectedModel.setCompany(model.getFilteredCompanyList().get(INDEX_THIRD_COMPANY.getZeroBased()),
                new CompanyBuilder(model.getFilteredCompanyList().get(INDEX_THIRD_COMPANY.getZeroBased()))
                        .withStatus(VALID_STATUS_AIRBUS).withRemark(VALID_REMARK_BOEING).withTags("applied").build());

        // Command: edit 2,3 with combined updates (status, remark, tags).
        // Expectation: success applying all fields in batch.
        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    // ================ Batch Edit Validation Tests ================

    /**
     * Tests that batch editing with the name field fails.
     * Batch edit is restricted to tags, remark, and status only to prevent mass name conflicts.
     * Expected: Command fails with error indicating batch edit is for tags/remark/status only.
     */
    @Test
    public void execute_batchEditWithName_failure() {
        List<Index> indices = Arrays.asList(INDEX_FIRST_COMPANY, INDEX_SECOND_COMPANY);
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder().withName(VALID_NAME_BOEING).build();
        EditCommand editCommand = new EditCommand(indices, descriptor);

        // Command: edit 1,2 n/Boeing.
        // Expectation: rejected because batch edit forbids name changes.
        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_BATCH_EDIT_FOR_TAGS_REMARK_ONLY);
    }

    /**
     * Tests that batch editing with the phone field fails.
     * Phone numbers are unique per company and should not be batch edited.
     * Expected: Command fails with error indicating batch edit is for tags/remark/status only.
     */
    @Test
    public void execute_batchEditWithPhone_failure() {
        List<Index> indices = Arrays.asList(INDEX_FIRST_COMPANY, INDEX_SECOND_COMPANY);
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder().withPhone(VALID_PHONE_BOEING).build();
        EditCommand editCommand = new EditCommand(indices, descriptor);

        // Command: edit 1,2 p/22222222.
        // Expectation: rejected because batch edit cannot change phone numbers.
        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_BATCH_EDIT_FOR_TAGS_REMARK_ONLY);
    }

    /**
     * Tests that batch editing with the email field fails.
     * Email addresses are unique per company and should not be batch edited.
     * Expected: Command fails with error indicating batch edit is for tags/remark/status only.
     */
    @Test
    public void execute_batchEditWithEmail_failure() {
        List<Index> indices = Arrays.asList(INDEX_FIRST_COMPANY, INDEX_SECOND_COMPANY);
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder()
                .withEmail("newemail@example.com").build();
        EditCommand editCommand = new EditCommand(indices, descriptor);

        // Command: edit 1,2 e/newemail@example.com.
        // Expectation: rejected because batch edit cannot change emails.
        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_BATCH_EDIT_FOR_TAGS_REMARK_ONLY);
    }

    /**
     * Tests that batch editing with the address field fails.
     * Addresses are specific to each company and should not be batch edited.
     * Expected: Command fails with error indicating batch edit is for tags/remark/status only.
     */
    @Test
    public void execute_batchEditWithAddress_failure() {
        List<Index> indices = Arrays.asList(INDEX_FIRST_COMPANY, INDEX_SECOND_COMPANY);
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder()
                .withAddress(VALID_ADDRESS_BOEING).build();
        EditCommand editCommand = new EditCommand(indices, descriptor);

        // Command: edit 1,2 a/<new address>.
        // Expectation: rejected because batch edit cannot change addresses.
        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_BATCH_EDIT_FOR_TAGS_REMARK_ONLY);
    }

    /**
     * Tests batch editing both tags and remarks together for multiple companies.
     * Validates that allowed batch edit fields can be combined in a single operation.
     * Expected: Command succeeds and updates both tags and remarks for all specified companies.
     */
    @Test
    public void execute_batchEditTagsAndRemarks_success() {
        List<Index> indices = Arrays.asList(INDEX_FIRST_COMPANY, INDEX_SECOND_COMPANY);
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder()
                .withTags(VALID_TAG_GOOD_PAY).withRemark(VALID_REMARK_BOEING).build();
        EditCommand editCommand = new EditCommand(indices, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_BATCH_EDIT_SUCCESS, 2);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        Company firstCompany = model.getFilteredCompanyList().get(INDEX_FIRST_COMPANY.getZeroBased());
        Company secondCompany = model.getFilteredCompanyList().get(INDEX_SECOND_COMPANY.getZeroBased());

        Company editedFirstCompany = new CompanyBuilder(firstCompany)
                .withTags(VALID_TAG_GOOD_PAY).withRemark(VALID_REMARK_BOEING).build();
        Company editedSecondCompany = new CompanyBuilder(secondCompany)
                .withTags(VALID_TAG_GOOD_PAY).withRemark(VALID_REMARK_BOEING).build();

        expectedModel.setCompany(firstCompany, editedFirstCompany);
        expectedModel.setCompany(secondCompany, editedSecondCompany);

        // Command: edit 1,2 t/good-pay r/"Lacking pay but good experience".
        // Expectation: success updating both allowed fields in batch.
        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    /**
     * Tests batch editing both tags and status together for multiple companies.
     * Validates that tags and status can be updated simultaneously in batch mode.
     * Expected: Command succeeds and updates both tags and status for all specified companies.
     */
    @Test
    public void execute_batchEditTagsAndStatus_success() {
        List<Index> indices = Arrays.asList(INDEX_FIRST_COMPANY, INDEX_THIRD_COMPANY);
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder()
                .withTags(VALID_TAG_GOOD_PAY).withStatus(VALID_STATUS_BOEING).build();
        EditCommand editCommand = new EditCommand(indices, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_BATCH_EDIT_SUCCESS, 2);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        Company firstCompany = model.getFilteredCompanyList().get(INDEX_FIRST_COMPANY.getZeroBased());
        Company thirdCompany = model.getFilteredCompanyList().get(INDEX_THIRD_COMPANY.getZeroBased());

        Company editedFirstCompany = new CompanyBuilder(firstCompany)
                .withTags(VALID_TAG_GOOD_PAY).withStatus(VALID_STATUS_BOEING).build();
        Company editedThirdCompany = new CompanyBuilder(thirdCompany)
                .withTags(VALID_TAG_GOOD_PAY).withStatus(VALID_STATUS_BOEING).build();

        expectedModel.setCompany(firstCompany, editedFirstCompany);
        expectedModel.setCompany(thirdCompany, editedThirdCompany);

        // Command: edit 1,3 t/good-pay status/to-apply.
        // Expectation: success updating tags and status together.
        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    /**
     * Tests batch editing both remarks and status together for multiple companies.
     * Validates that remarks and status can be updated simultaneously in batch mode.
     * Expected: Command succeeds and updates both remarks and status for all specified companies.
     */
    @Test
    public void execute_batchEditRemarksAndStatus_success() {
        List<Index> indices = Arrays.asList(INDEX_SECOND_COMPANY, INDEX_THIRD_COMPANY);
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder()
                .withRemark(VALID_REMARK_BOEING).withStatus(VALID_STATUS_AIRBUS).build();
        EditCommand editCommand = new EditCommand(indices, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_BATCH_EDIT_SUCCESS, 2);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        Company secondCompany = model.getFilteredCompanyList().get(INDEX_SECOND_COMPANY.getZeroBased());
        Company thirdCompany = model.getFilteredCompanyList().get(INDEX_THIRD_COMPANY.getZeroBased());

        Company editedSecondCompany = new CompanyBuilder(secondCompany)
                .withRemark(VALID_REMARK_BOEING).withStatus(VALID_STATUS_AIRBUS).build();
        Company editedThirdCompany = new CompanyBuilder(thirdCompany)
                .withRemark(VALID_REMARK_BOEING).withStatus(VALID_STATUS_AIRBUS).build();

        expectedModel.setCompany(secondCompany, editedSecondCompany);
        expectedModel.setCompany(thirdCompany, editedThirdCompany);

        // Command: edit 2,3 r/"Lacking pay but good experience" status/tech-interview.
        // Expectation: success applying remark and status pair.
        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    /**
     * Tests batch editing tags only for three companies.
     * Validates that tags can be updated in isolation without affecting other fields.
     * Expected: Command succeeds and updates only tags for all three companies.
     */
    @Test
    public void execute_batchEditTagsOnly_success() {
        List<Index> indices = Arrays.asList(INDEX_FIRST_COMPANY, INDEX_SECOND_COMPANY, INDEX_THIRD_COMPANY);
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder()
                .withTags(VALID_TAG_GOOD_PAY).build();
        EditCommand editCommand = new EditCommand(indices, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_BATCH_EDIT_SUCCESS, 3);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        Company firstCompany = model.getFilteredCompanyList().get(INDEX_FIRST_COMPANY.getZeroBased());
        Company secondCompany = model.getFilteredCompanyList().get(INDEX_SECOND_COMPANY.getZeroBased());
        Company thirdCompany = model.getFilteredCompanyList().get(INDEX_THIRD_COMPANY.getZeroBased());

        Company editedFirstCompany = new CompanyBuilder(firstCompany).withTags(VALID_TAG_GOOD_PAY).build();
        Company editedSecondCompany = new CompanyBuilder(secondCompany).withTags(VALID_TAG_GOOD_PAY).build();
        Company editedThirdCompany = new CompanyBuilder(thirdCompany).withTags(VALID_TAG_GOOD_PAY).build();

        expectedModel.setCompany(firstCompany, editedFirstCompany);
        expectedModel.setCompany(secondCompany, editedSecondCompany);
        expectedModel.setCompany(thirdCompany, editedThirdCompany);

        // Command: edit 1,2,3 t/good-pay (tags only).
        // Expectation: success updating tags without touching other fields.
        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    // ================ Batch Edit Index Combination Tests ================

    /**
     * Tests batch editing with non-contiguous indices (e.g., 1 and 3, skipping 2).
     * Validates that batch operations work with non-sequential company selections.
     * Expected: Command succeeds and updates companies at indices 1 and 3 only.
     */
    @Test
    public void execute_batchEditNonContiguousIndices_success() {
        // Test with non-contiguous indices: 1, 3 (skipping 2)
        List<Index> indices = Arrays.asList(INDEX_FIRST_COMPANY, INDEX_THIRD_COMPANY);
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder()
                .withStatus(VALID_STATUS_BOEING).build();
        EditCommand editCommand = new EditCommand(indices, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_BATCH_EDIT_SUCCESS, 2);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        Company firstCompany = model.getFilteredCompanyList().get(INDEX_FIRST_COMPANY.getZeroBased());
        Company thirdCompany = model.getFilteredCompanyList().get(INDEX_THIRD_COMPANY.getZeroBased());

        Company editedFirstCompany = new CompanyBuilder(firstCompany).withStatus(VALID_STATUS_BOEING).build();
        Company editedThirdCompany = new CompanyBuilder(thirdCompany).withStatus(VALID_STATUS_BOEING).build();

        expectedModel.setCompany(firstCompany, editedFirstCompany);
        expectedModel.setCompany(thirdCompany, editedThirdCompany);

        // Command: edit 1,3 status/to-apply (skip index 2).
        // Expectation: success updating only the chosen non-contiguous indices.
        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    /**
     * Tests batch editing with indices in descending order (3, 2, 1).
     * Validates that the order of indices in input does not affect the operation.
     * Expected: Command succeeds and updates all three companies regardless of index order.
     */
    @Test
    public void execute_batchEditDescendingOrder_success() {
        // Test with indices in descending order: 3, 2, 1
        List<Index> indices = Arrays.asList(INDEX_THIRD_COMPANY, INDEX_SECOND_COMPANY, INDEX_FIRST_COMPANY);
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder()
                .withRemark(VALID_REMARK_BOEING).build();
        EditCommand editCommand = new EditCommand(indices, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_BATCH_EDIT_SUCCESS, 3);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        Company firstCompany = model.getFilteredCompanyList().get(INDEX_FIRST_COMPANY.getZeroBased());
        Company secondCompany = model.getFilteredCompanyList().get(INDEX_SECOND_COMPANY.getZeroBased());
        Company thirdCompany = model.getFilteredCompanyList().get(INDEX_THIRD_COMPANY.getZeroBased());

        Company editedFirstCompany = new CompanyBuilder(firstCompany).withRemark(VALID_REMARK_BOEING).build();
        Company editedSecondCompany = new CompanyBuilder(secondCompany).withRemark(VALID_REMARK_BOEING).build();
        Company editedThirdCompany = new CompanyBuilder(thirdCompany).withRemark(VALID_REMARK_BOEING).build();

        expectedModel.setCompany(firstCompany, editedFirstCompany);
        expectedModel.setCompany(secondCompany, editedSecondCompany);
        expectedModel.setCompany(thirdCompany, editedThirdCompany);

        // Command: edit 3,2,1 r/"Lacking pay but good experience".
        // Expectation: success regardless of descending index order.
        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    /**
     * Tests batch editing with indices in random order (2, 1, 3).
     * Ensures that arbitrary ordering of indices works correctly.
     * Expected: Command succeeds and updates all three companies regardless of index order.
     */
    @Test
    public void execute_batchEditRandomOrder_success() {
        // Test with indices in random order: 2, 1, 3
        List<Index> indices = Arrays.asList(INDEX_SECOND_COMPANY, INDEX_FIRST_COMPANY, INDEX_THIRD_COMPANY);
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder()
                .withTags(VALID_TAG_GOOD_PAY).withStatus(VALID_STATUS_AIRBUS).build();
        EditCommand editCommand = new EditCommand(indices, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_BATCH_EDIT_SUCCESS, 3);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        Company firstCompany = model.getFilteredCompanyList().get(INDEX_FIRST_COMPANY.getZeroBased());
        Company secondCompany = model.getFilteredCompanyList().get(INDEX_SECOND_COMPANY.getZeroBased());
        Company thirdCompany = model.getFilteredCompanyList().get(INDEX_THIRD_COMPANY.getZeroBased());

        Company editedFirstCompany = new CompanyBuilder(firstCompany)
                .withTags(VALID_TAG_GOOD_PAY).withStatus(VALID_STATUS_AIRBUS).build();
        Company editedSecondCompany = new CompanyBuilder(secondCompany)
                .withTags(VALID_TAG_GOOD_PAY).withStatus(VALID_STATUS_AIRBUS).build();
        Company editedThirdCompany = new CompanyBuilder(thirdCompany)
                .withTags(VALID_TAG_GOOD_PAY).withStatus(VALID_STATUS_AIRBUS).build();

        expectedModel.setCompany(firstCompany, editedFirstCompany);
        expectedModel.setCompany(secondCompany, editedSecondCompany);
        expectedModel.setCompany(thirdCompany, editedThirdCompany);

        // Command: edit 2,1,3 t/good-pay status/tech-interview.
        // Expectation: success independent of index ordering.
        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    /**
     * Tests batch editing all companies in the address book.
     * Validates that batch operations can handle editing the entire company list.
     * Expected: Command succeeds and updates status for all companies in the list.
     */
    @Test
    public void execute_batchEditAllCompanies_success() {
        // Test editing all companies in the list
        int totalCompanies = model.getFilteredCompanyList().size();
        List<Index> indices = new ArrayList<>();
        for (int i = 1; i <= totalCompanies; i++) {
            indices.add(Index.fromOneBased(i));
        }

        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder()
                .withStatus(VALID_STATUS_BOEING).build();
        EditCommand editCommand = new EditCommand(indices, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_BATCH_EDIT_SUCCESS, totalCompanies);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        for (Index index : indices) {
            Company companyToEdit = model.getFilteredCompanyList().get(index.getZeroBased());
            Company editedCompany = new CompanyBuilder(companyToEdit).withStatus(VALID_STATUS_BOEING).build();
            expectedModel.setCompany(companyToEdit, editedCompany);
        }

        // Command: edit 1..N status/to-apply across the entire list.
        // Expectation: success updating every company in the address book.
        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

}
