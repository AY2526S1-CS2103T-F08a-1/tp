package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.DESC_AIRBUS;
import static seedu.address.logic.commands.CommandTestUtil.DESC_BOEING;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOEING;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOEING;
import static seedu.address.logic.commands.CommandTestUtil.VALID_REMARK_BOEING;
import static seedu.address.logic.commands.CommandTestUtil.VALID_STATUS_AIRBUS;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_GOOD_PAY;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showCompanyAtIndex;
import static seedu.address.logic.commands.EditCommand.MESSAGE_INVALID_BATCH_EDIT_FIELD;
import static seedu.address.testutil.TypicalCompanies.getTypicalAddressBook;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_COMPANY;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_COMPANY;
import static seedu.address.testutil.TypicalIndexes.INDEX_THIRD_COMPANY;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditCommand.EditCompanyDescriptor;
import seedu.address.logic.parser.IndexParser;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.company.Company;
import seedu.address.testutil.CompanyBuilder;
import seedu.address.testutil.EditCompanyDescriptorBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for EditCommand.
 */
public class EditCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    /**
     * Creates a fresh expected model based on the current model state.
     */
    private Model createExpectedModel() {
        return new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
    }

    /**
     * Performs a batch edit test with the given parameters.
     */
    private void performBatchEditTest(List<Index> indices, EditCompanyDescriptor descriptor,
                                    String expectedMessage, CompanyBuilder... companyBuilders) {
        EditCommand editCommand = new EditCommand(indices, descriptor);
        Model expectedModel = createExpectedModel();

        for (int i = 0; i < indices.size(); i++) {
            Company originalCompany = model.getFilteredCompanyList().get(indices.get(i).getZeroBased());
            Company editedCompany = companyBuilders[i].build();
            expectedModel.setCompany(originalCompany, editedCompany);
        }

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_allFieldsSpecifiedUnfilteredList_success() {
        Company editedCompany = new CompanyBuilder().build();
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder(editedCompany).build();
        EditCommand editCommand = new EditCommand(List.of(INDEX_FIRST_COMPANY), descriptor);

        String expectedMessage = EditCommand.MESSAGE_EDIT_SUCCESS_SINGLE;

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setCompany(model.getFilteredCompanyList().get(0), editedCompany);
        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_someFieldsSpecifiedUnfilteredList_success() {
        Index indexLastCompany = Index.fromOneBased(model.getFilteredCompanyList().size());
        Company lastCompany = model.getFilteredCompanyList().get(model.getFilteredCompanyList().size() - 1);

        CompanyBuilder companyInList = new CompanyBuilder(lastCompany);
        Company editedCompany = companyInList.withName(VALID_NAME_BOEING).withPhone(VALID_PHONE_BOEING)
                .withTags(VALID_TAG_GOOD_PAY).withRemark(VALID_REMARK_BOEING).build();

        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder().withName(VALID_NAME_BOEING)
                .withPhone(VALID_PHONE_BOEING).withTags(VALID_TAG_GOOD_PAY).withRemark(VALID_REMARK_BOEING)
                .build();
        EditCommand editCommand = new EditCommand(List.of(indexLastCompany), descriptor);

        String expectedMessage = EditCommand.MESSAGE_EDIT_SUCCESS_SINGLE;

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setCompany(lastCompany, editedCompany);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_filteredList_success() {
        showCompanyAtIndex(model, INDEX_FIRST_COMPANY);

        Company companyInFilteredList = model.getFilteredCompanyList().get(INDEX_FIRST_COMPANY.getZeroBased());
        Company editedCompany = new CompanyBuilder(companyInFilteredList).withName(VALID_NAME_BOEING).build();
        EditCommand editCommand = new EditCommand(List.of(INDEX_FIRST_COMPANY),
                new EditCompanyDescriptorBuilder().withName(VALID_NAME_BOEING).build());

        String expectedMessage = EditCommand.MESSAGE_EDIT_SUCCESS_SINGLE;

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setCompany(model.getFilteredCompanyList().get(0), editedCompany);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_duplicateCompanyUnfilteredList_failure() {
        Company firstCompany = model.getFilteredCompanyList().get(INDEX_FIRST_COMPANY.getZeroBased());
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder(firstCompany).build();
        EditCommand editCommand = new EditCommand(List.of(INDEX_SECOND_COMPANY), descriptor);

        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_DUPLICATE_COMPANY);
    }

    @Test
    public void execute_duplicateCompanyFilteredList_failure() {
        showCompanyAtIndex(model, INDEX_FIRST_COMPANY);

        // edit company in filtered list into a duplicate in address book
        Company companyInList = model.getAddressBook().getCompanyList().get(INDEX_SECOND_COMPANY.getZeroBased());
        EditCommand editCommand = new EditCommand(List.of(INDEX_FIRST_COMPANY),
                new EditCompanyDescriptorBuilder(companyInList).build());

        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_DUPLICATE_COMPANY);
    }

    @Test
    public void execute_invalidCompanyIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredCompanyList().size() + 1);
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder().withName(VALID_NAME_BOEING).build();
        EditCommand editCommand = new EditCommand(List.of(outOfBoundIndex), descriptor);

        String expectedMessage = String.format(IndexParser.MESSAGE_INDEX_OUT_OF_RANGE,
                outOfBoundIndex.getOneBased(), model.getFilteredCompanyList().size());
        assertCommandFailure(editCommand, model, expectedMessage);
    }

    /**
     * Edit filtered list where index is larger than size of filtered list,
     * but smaller than size of address book
     */
    @Test
    public void execute_invalidCompanyIndexFilteredList_failure() {
        showCompanyAtIndex(model, INDEX_FIRST_COMPANY);
        Index outOfBoundIndex = INDEX_SECOND_COMPANY;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getCompanyList().size());

        EditCommand editCommand = new EditCommand(List.of(outOfBoundIndex),
                new EditCompanyDescriptorBuilder().withName(VALID_NAME_BOEING).build());

        String expectedMessage = String.format(IndexParser.MESSAGE_INDEX_OUT_OF_RANGE,
                outOfBoundIndex.getOneBased(), model.getFilteredCompanyList().size());
        assertCommandFailure(editCommand, model, expectedMessage);
    }

    @Test
    public void equals() {
        final EditCommand singleEditCommand = new EditCommand(List.of(INDEX_FIRST_COMPANY), DESC_AIRBUS);
        List<Index> batchIndices = Arrays.asList(INDEX_FIRST_COMPANY, INDEX_SECOND_COMPANY);
        final EditCommand batchEditCommand = new EditCommand(batchIndices, DESC_AIRBUS);

        // same values -> returns true (single edit)
        EditCompanyDescriptor copyDescriptor = new EditCompanyDescriptor(DESC_AIRBUS);
        EditCommand singleCommandWithSameValues = new EditCommand(List.of(INDEX_FIRST_COMPANY), copyDescriptor);
        assertTrue(singleEditCommand.equals(singleCommandWithSameValues));

        // same values -> returns true (batch edit)
        EditCommand batchCommandWithSameValues = new EditCommand(batchIndices, new EditCompanyDescriptor(DESC_AIRBUS));
        assertTrue(batchEditCommand.equals(batchCommandWithSameValues));

        // same object -> returns true
        assertTrue(singleEditCommand.equals(singleEditCommand));
        assertTrue(batchEditCommand.equals(batchEditCommand));

        // null -> returns false
        assertFalse(singleEditCommand.equals(null));
        assertFalse(batchEditCommand.equals(null));

        // different types -> returns false
        assertFalse(singleEditCommand.equals(new ClearCommand()));
        assertFalse(batchEditCommand.equals(new ClearCommand()));

        // different index -> returns false (single edit)
        assertFalse(singleEditCommand.equals(new EditCommand(List.of(INDEX_SECOND_COMPANY), DESC_AIRBUS)));

        // different indices -> returns false (batch edit)
        List<Index> differentBatchIndices = Arrays.asList(INDEX_FIRST_COMPANY, INDEX_THIRD_COMPANY);
        assertFalse(batchEditCommand.equals(new EditCommand(differentBatchIndices, DESC_AIRBUS)));

        // different descriptor -> returns false
        assertFalse(singleEditCommand.equals(new EditCommand(List.of(INDEX_FIRST_COMPANY), DESC_BOEING)));
        assertFalse(batchEditCommand.equals(new EditCommand(batchIndices, DESC_BOEING)));

        // single edit vs batch edit -> returns false
        assertFalse(singleEditCommand.equals(batchEditCommand));
    }

    @Test
    public void toStringMethod() {
        // Test single edit toString
        Index singleIndex = Index.fromOneBased(1);
        EditCompanyDescriptor editCompanyDescriptor = new EditCompanyDescriptor();
        EditCommand singleEditCommand = new EditCommand(List.of(singleIndex), editCompanyDescriptor);
        String expectedSingle = EditCommand.class.getCanonicalName() + "{indices=" + List.of(singleIndex)
                + ", editCompanyDescriptor=" + editCompanyDescriptor + "}";
        assertEquals(expectedSingle, singleEditCommand.toString());

        // Test batch edit toString
        List<Index> batchIndices = Arrays.asList(INDEX_FIRST_COMPANY, INDEX_SECOND_COMPANY);
        EditCommand batchEditCommand = new EditCommand(batchIndices, editCompanyDescriptor);
        String expectedBatch = EditCommand.class.getCanonicalName() + "{indices=" + batchIndices
                + ", editCompanyDescriptor=" + editCompanyDescriptor + "}";
        assertEquals(expectedBatch, batchEditCommand.toString());
    }

    // ================ Batch Edit Tests ================

    @Test
    public void execute_batchEditValidIndicesUnfilteredList_success() {
        // Edit tags for multiple companies
        List<Index> indices = Arrays.asList(INDEX_FIRST_COMPANY, INDEX_SECOND_COMPANY);
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder().withTags("applied").build();
        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_SUCCESS_MULTIPLE, 2);

        Company firstCompany = model.getFilteredCompanyList().get(INDEX_FIRST_COMPANY.getZeroBased());
        Company secondCompany = model.getFilteredCompanyList().get(INDEX_SECOND_COMPANY.getZeroBased());

        performBatchEditTest(indices, descriptor, expectedMessage,
            new CompanyBuilder(firstCompany).withTags("applied"),
            new CompanyBuilder(secondCompany).withTags("applied"));
    }

    @Test
    public void execute_batchEditWithMixedCaseTags_convertedToLowercase() {
        List<Index> indices = Arrays.asList(INDEX_FIRST_COMPANY, INDEX_SECOND_COMPANY);
        // provide mixed-case tags in descriptor
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder().withTags("ApPlied", "InTERView").build();
        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_SUCCESS_MULTIPLE, 2);

        Company firstCompany = model.getFilteredCompanyList().get(INDEX_FIRST_COMPANY.getZeroBased());
        Company secondCompany = model.getFilteredCompanyList().get(INDEX_SECOND_COMPANY.getZeroBased());

        // expected companies use lowercase tag names
        performBatchEditTest(indices, descriptor, expectedMessage,
            new CompanyBuilder(firstCompany).withTags("applied", "interview"),
            new CompanyBuilder(secondCompany).withTags("applied", "interview"));
    }

    @Test
    public void execute_batchEditAllInvalidIndicesUnfilteredList_failure() {
        Index outOfBound1 = Index.fromOneBased(model.getFilteredCompanyList().size() + 1);
        Index outOfBound2 = Index.fromOneBased(model.getFilteredCompanyList().size() + 2);
        List<Index> indices = Arrays.asList(outOfBound1, outOfBound2);
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder().withTags("applied").build();
        EditCommand editCommand = new EditCommand(indices, descriptor);

        String expectedMessage = String.format(IndexParser.MESSAGE_INDEX_OUT_OF_RANGE,
                outOfBound1.getOneBased(), model.getFilteredCompanyList().size());
        assertCommandFailure(editCommand, model, expectedMessage);
    }

    @Test
    public void execute_batchEditFilteredList_success() {
        showCompanyAtIndex(model, INDEX_FIRST_COMPANY);

        // Only first company is shown, so only index 1 should be valid
        List<Index> indices = Arrays.asList(INDEX_FIRST_COMPANY);
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder().withTags("applied").build();
        EditCommand editCommand = new EditCommand(indices, descriptor);

        String expectedMessage = EditCommand.MESSAGE_EDIT_SUCCESS_SINGLE;

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        Company firstCompany = model.getFilteredCompanyList().get(INDEX_FIRST_COMPANY.getZeroBased());
        Company editedFirstCompany = new CompanyBuilder(firstCompany).withTags("applied").build();
        expectedModel.setCompany(firstCompany, editedFirstCompany);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_batchEditFilteredListInvalidIndex_failure() {
        showCompanyAtIndex(model, INDEX_FIRST_COMPANY);

        // Second company is not shown in filtered list
        List<Index> indices = Arrays.asList(INDEX_FIRST_COMPANY, INDEX_SECOND_COMPANY);
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder().withTags("applied").build();
        EditCommand editCommand = new EditCommand(indices, descriptor);

        String expectedMessage = String.format(IndexParser.MESSAGE_INDEX_OUT_OF_RANGE,
                INDEX_SECOND_COMPANY.getOneBased(), model.getFilteredCompanyList().size());
        assertCommandFailure(editCommand, model, expectedMessage);
    }

    @Test
    public void execute_batchEditDuplicateCompany_failure() {
        // Try to edit second company to have same name as first company
        Company firstCompany = model.getFilteredCompanyList().get(INDEX_FIRST_COMPANY.getZeroBased());
        List<Index> indices = Arrays.asList(INDEX_SECOND_COMPANY);
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder(firstCompany).build();
        EditCommand editCommand = new EditCommand(indices, descriptor);

        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_DUPLICATE_COMPANY);
    }

    @Test
    public void execute_batchEditTagsOfMultipleCompanies_success() {
        // Edit tags for three companies
        List<Index> indices = Arrays.asList(INDEX_FIRST_COMPANY, INDEX_SECOND_COMPANY, INDEX_THIRD_COMPANY);
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder().withTags("interview").build();
        EditCommand editCommand = new EditCommand(indices, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_SUCCESS_MULTIPLE, 3);

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

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }






    @Test
    public void execute_batchEditMultipleFields_success() {
        List<Index> indices = Arrays.asList(INDEX_SECOND_COMPANY, INDEX_THIRD_COMPANY);
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder()
                .withStatus(VALID_STATUS_AIRBUS).withRemark(VALID_REMARK_BOEING).withTags("applied").build();
        EditCommand editCommand = new EditCommand(indices, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_SUCCESS_MULTIPLE, 2);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setCompany(model.getFilteredCompanyList().get(INDEX_SECOND_COMPANY.getZeroBased()),
                new CompanyBuilder(model.getFilteredCompanyList().get(INDEX_SECOND_COMPANY.getZeroBased()))
                        .withStatus(VALID_STATUS_AIRBUS).withRemark(VALID_REMARK_BOEING).withTags("applied").build());
        expectedModel.setCompany(model.getFilteredCompanyList().get(INDEX_THIRD_COMPANY.getZeroBased()),
                new CompanyBuilder(model.getFilteredCompanyList().get(INDEX_THIRD_COMPANY.getZeroBased()))
                        .withStatus(VALID_STATUS_AIRBUS).withRemark(VALID_REMARK_BOEING).withTags("applied").build());

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }




    @Test
    public void execute_batchEditAllFieldsExceptName_success() {
        List<Index> indices = Arrays.asList(INDEX_FIRST_COMPANY, INDEX_SECOND_COMPANY);
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder()
                .withEmail("batch@example.com")
                .withAddress("456 Batch Street")
                .withPhone("88776655")
                .withTags("batch-edited")
                .withStatus("applied")
                .withRemark("Batch edited companies").build();
        EditCommand editCommand = new EditCommand(indices, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_SUCCESS_MULTIPLE, 2);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        Company firstCompany = model.getFilteredCompanyList().get(INDEX_FIRST_COMPANY.getZeroBased());
        Company secondCompany = model.getFilteredCompanyList().get(INDEX_SECOND_COMPANY.getZeroBased());

        Company editedFirstCompany = new CompanyBuilder(firstCompany)
                .withEmail("batch@example.com")
                .withAddress("456 Batch Street")
                .withPhone("88776655")
                .withTags("batch-edited")
                .withStatus("applied")
                .withRemark("Batch edited companies").build();
        Company editedSecondCompany = new CompanyBuilder(secondCompany)
                .withEmail("batch@example.com")
                .withAddress("456 Batch Street")
                .withPhone("88776655")
                .withTags("batch-edited")
                .withStatus("applied")
                .withRemark("Batch edited companies").build();

        expectedModel.setCompany(firstCompany, editedFirstCompany);
        expectedModel.setCompany(secondCompany, editedSecondCompany);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_batchEditWithNameField_failure() {
        // Batch edit should not allow name field to be edited
        List<Index> indices = Arrays.asList(INDEX_FIRST_COMPANY, INDEX_SECOND_COMPANY);
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder()
                .withName("New Company Name")
                .withEmail("test@example.com").build();
        EditCommand editCommand = new EditCommand(indices, descriptor);

        assertCommandFailure(editCommand, model, MESSAGE_INVALID_BATCH_EDIT_FIELD);
    }

    @Test
    public void execute_editAfterFilter_usesFilteredListIndices() {
        // Use showCompanyAtIndex to create a simple filtered list with only the first company
        showCompanyAtIndex(model, INDEX_FIRST_COMPANY);

        // Verify filtered list has exactly 1 company
        assertEquals(1, model.getFilteredCompanyList().size());

        // Get the company that should be at index 1 in the filtered list
        Company companyInFilteredList = model.getFilteredCompanyList().get(INDEX_FIRST_COMPANY.getZeroBased());

        // Test editing using index 1 - should edit the first (and only) company in filtered list
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder()
            .withRemark("Edited after filtering").build();
        EditCommand editCommand = new EditCommand(List.of(INDEX_FIRST_COMPANY), descriptor);

        String expectedMessage = EditCommand.MESSAGE_EDIT_SUCCESS_SINGLE;
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        // Create the expected edited company
        Company editedCompany = new CompanyBuilder(companyInFilteredList)
            .withRemark("Edited after filtering").build();
        expectedModel.setCompany(companyInFilteredList, editedCompany);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_batchEditAfterFilter_editsFilteredListNotOriginalList() {
        // Apply a filter to show companies with "technology" tag (TechVision and Digital Innovations have this)
        FilterCommand filterCommand = new FilterCommand(Optional.empty(), List.of("technology"));
        filterCommand.execute(model);

        // Check how many companies have "technology" tag
        int filteredSize = model.getFilteredCompanyList().size();

        // If we have at least 2 companies, proceed with batch edit test
        if (filteredSize >= 2) {
            // Get the first 2 companies in the filtered list
            Company firstInFiltered = model.getFilteredCompanyList().get(0);
            Company secondInFiltered = model.getFilteredCompanyList().get(1);

            // Test: Edit indices 1,2 in filtered list - should edit the companies at filtered positions 1,2
            List<Index> indices = Arrays.asList(INDEX_FIRST_COMPANY, INDEX_SECOND_COMPANY);
            EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder()
                .withRemark("Batch edited after filter").build();
            EditCommand editCommand = new EditCommand(indices, descriptor);

            String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_SUCCESS_MULTIPLE, 2);
            Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

            // Apply same filter to expected model
            FilterCommand expectedFilterCommand = new FilterCommand(Optional.empty(), List.of("technology"));
            expectedFilterCommand.execute(expectedModel);

            // Update the 2 companies that should be edited (the ones in filtered positions 1,2)
            Company editedFirst = new CompanyBuilder(firstInFiltered).withRemark("Batch edited after filter").build();
            Company editedSecond = new CompanyBuilder(secondInFiltered).withRemark("Batch edited after filter").build();

            expectedModel.setCompany(firstInFiltered, editedFirst);
            expectedModel.setCompany(secondInFiltered, editedSecond);

            assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
        } else {
            // Fall back to testing with single edit if not enough companies
            if (filteredSize >= 1) {
                Company firstInFiltered = model.getFilteredCompanyList().get(0);
                EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder()
                    .withRemark("Single edited after filter").build();
                EditCommand editCommand = new EditCommand(List.of(INDEX_FIRST_COMPANY), descriptor);

                String expectedMessage = EditCommand.MESSAGE_EDIT_SUCCESS_SINGLE;
                Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

                FilterCommand expectedFilterCommand = new FilterCommand(Optional.empty(), List.of("technology"));
                expectedFilterCommand.execute(expectedModel);

                Company editedFirst = new CompanyBuilder(firstInFiltered)
                    .withRemark("Single edited after filter").build();
                expectedModel.setCompany(firstInFiltered, editedFirst);

                assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
            }
        }
    }

}
