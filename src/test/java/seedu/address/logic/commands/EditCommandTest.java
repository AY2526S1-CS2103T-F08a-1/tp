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
import static seedu.address.logic.commands.CommandTestUtil.VALID_STATUS_BOEING;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
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
        final EditCommand standardCommand = new EditCommand(List.of(INDEX_FIRST_COMPANY), DESC_AIRBUS);

        // same values -> returns true
        EditCompanyDescriptor copyDescriptor = new EditCompanyDescriptor(DESC_AIRBUS);
        EditCommand commandWithSameValues = new EditCommand(List.of(INDEX_FIRST_COMPANY), copyDescriptor);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different index -> returns false
        assertFalse(standardCommand.equals(new EditCommand(List.of(INDEX_SECOND_COMPANY), DESC_AIRBUS)));

        // different descriptor -> returns false
        assertFalse(standardCommand.equals(new EditCommand(List.of(INDEX_FIRST_COMPANY), DESC_BOEING)));
    }

    @Test
    public void toStringMethod() {
        Index index = Index.fromOneBased(1);
        EditCompanyDescriptor editCompanyDescriptor = new EditCompanyDescriptor();
        EditCommand editCommand = new EditCommand(List.of(index), editCompanyDescriptor);
        String expected = EditCommand.class.getCanonicalName() + "{indices=" + List.of(index)
                + ", editCompanyDescriptor=" + editCompanyDescriptor + "}";
        assertEquals(expected, editCommand.toString());
    }

    // ================ Batch Edit Tests ================

    @Test
    public void execute_batchEditValidIndicesUnfilteredList_success() {
        // Edit tags for multiple companies
        List<Index> indices = Arrays.asList(INDEX_FIRST_COMPANY, INDEX_SECOND_COMPANY);
        Set<Tag> newTags = new HashSet<>(Arrays.asList(new Tag("applied")));
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder().withTags("applied").build();
        EditCommand editCommand = new EditCommand(indices, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_SUCCESS_MULTIPLE, 2);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        Company firstCompany = model.getFilteredCompanyList().get(INDEX_FIRST_COMPANY.getZeroBased());
        Company secondCompany = model.getFilteredCompanyList().get(INDEX_SECOND_COMPANY.getZeroBased());

        Company editedFirstCompany = new CompanyBuilder(firstCompany).withTags("applied").build();
        Company editedSecondCompany = new CompanyBuilder(secondCompany).withTags("applied").build();

        expectedModel.setCompany(firstCompany, editedFirstCompany);
        expectedModel.setCompany(secondCompany, editedSecondCompany);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_batchEditWithMixedCaseTags_convertedToLowercase() {
        List<Index> indices = Arrays.asList(INDEX_FIRST_COMPANY, INDEX_SECOND_COMPANY);
        // provide mixed-case tags in descriptor
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder().withTags("ApPlied", "InTERView").build();
        EditCommand editCommand = new EditCommand(indices, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_SUCCESS_MULTIPLE, 2);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        Company firstCompany = model.getFilteredCompanyList().get(INDEX_FIRST_COMPANY.getZeroBased());
        Company secondCompany = model.getFilteredCompanyList().get(INDEX_SECOND_COMPANY.getZeroBased());

        // expected companies use lowercase tag names
        Company editedFirstCompany = new CompanyBuilder(firstCompany).withTags("applied", "interview").build();
        Company editedSecondCompany = new CompanyBuilder(secondCompany).withTags("applied", "interview").build();

        expectedModel.setCompany(firstCompany, editedFirstCompany);
        expectedModel.setCompany(secondCompany, editedSecondCompany);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
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
        EditCommand singleEditCommand = new EditCommand(List.of(INDEX_FIRST_COMPANY), DESC_AIRBUS);
        assertFalse(batchCommand1.equals(singleEditCommand));
    }

    @Test
    public void toStringMethod_batchEdit() {
        List<Index> indices = Arrays.asList(INDEX_FIRST_COMPANY, INDEX_SECOND_COMPANY);
        EditCompanyDescriptor editCompanyDescriptor = new EditCompanyDescriptor();
        EditCommand editCommand = new EditCommand(indices, editCompanyDescriptor);
        String expected = EditCommand.class.getCanonicalName() + "{indices=" + indices
                + ", editCompanyDescriptor=" + editCompanyDescriptor + "}";
        assertEquals(expected, editCommand.toString());
    }


    @Test
    public void execute_batchEditStatus_success() {
        List<Index> indices = Arrays.asList(INDEX_FIRST_COMPANY, INDEX_SECOND_COMPANY);
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder().withStatus(VALID_STATUS_BOEING).build();
        EditCommand editCommand = new EditCommand(indices, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_SUCCESS_MULTIPLE, 2);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        Company firstCompany = model.getFilteredCompanyList().get(INDEX_FIRST_COMPANY.getZeroBased());
        Company secondCompany = model.getFilteredCompanyList().get(INDEX_SECOND_COMPANY.getZeroBased());

        Company editedFirstCompany = new CompanyBuilder(firstCompany).withStatus(VALID_STATUS_BOEING).build();
        Company editedSecondCompany = new CompanyBuilder(secondCompany).withStatus(VALID_STATUS_BOEING).build();

        expectedModel.setCompany(firstCompany, editedFirstCompany);
        expectedModel.setCompany(secondCompany, editedSecondCompany);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_batchEditRemark_success() {
        List<Index> indices = Arrays.asList(INDEX_FIRST_COMPANY, INDEX_THIRD_COMPANY);
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder().withRemark(VALID_REMARK_BOEING).build();
        EditCommand editCommand = new EditCommand(indices, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_SUCCESS_MULTIPLE, 2);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        Company firstCompany = model.getFilteredCompanyList().get(INDEX_FIRST_COMPANY.getZeroBased());
        Company thirdCompany = model.getFilteredCompanyList().get(INDEX_THIRD_COMPANY.getZeroBased());

        Company editedFirstCompany = new CompanyBuilder(firstCompany).withRemark(VALID_REMARK_BOEING).build();
        Company editedThirdCompany = new CompanyBuilder(thirdCompany).withRemark(VALID_REMARK_BOEING).build();

        expectedModel.setCompany(firstCompany, editedFirstCompany);
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
    public void execute_batchEditEmail_success() {
        List<Index> indices = Arrays.asList(INDEX_FIRST_COMPANY, INDEX_SECOND_COMPANY);
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder()
                .withEmail("newemail@example.com").build();
        EditCommand editCommand = new EditCommand(indices, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_SUCCESS_MULTIPLE, 2);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        Company firstCompany = model.getFilteredCompanyList().get(INDEX_FIRST_COMPANY.getZeroBased());
        Company secondCompany = model.getFilteredCompanyList().get(INDEX_SECOND_COMPANY.getZeroBased());

        Company editedFirstCompany = new CompanyBuilder(firstCompany).withEmail("newemail@example.com").build();
        Company editedSecondCompany = new CompanyBuilder(secondCompany).withEmail("newemail@example.com").build();

        expectedModel.setCompany(firstCompany, editedFirstCompany);
        expectedModel.setCompany(secondCompany, editedSecondCompany);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_batchEditAddress_success() {
        List<Index> indices = Arrays.asList(INDEX_FIRST_COMPANY, INDEX_THIRD_COMPANY);
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder()
                .withAddress("123 New Street, New City").build();
        EditCommand editCommand = new EditCommand(indices, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_SUCCESS_MULTIPLE, 2);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        Company firstCompany = model.getFilteredCompanyList().get(INDEX_FIRST_COMPANY.getZeroBased());
        Company thirdCompany = model.getFilteredCompanyList().get(INDEX_THIRD_COMPANY.getZeroBased());

        Company editedFirstCompany = new CompanyBuilder(firstCompany).withAddress("123 New Street, New City").build();
        Company editedThirdCompany = new CompanyBuilder(thirdCompany).withAddress("123 New Street, New City").build();

        expectedModel.setCompany(firstCompany, editedFirstCompany);
        expectedModel.setCompany(thirdCompany, editedThirdCompany);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_batchEditPhone_success() {
        List<Index> indices = Arrays.asList(INDEX_SECOND_COMPANY, INDEX_THIRD_COMPANY);
        EditCompanyDescriptor descriptor = new EditCompanyDescriptorBuilder()
                .withPhone("99887766").build();
        EditCommand editCommand = new EditCommand(indices, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_SUCCESS_MULTIPLE, 2);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        Company secondCompany = model.getFilteredCompanyList().get(INDEX_SECOND_COMPANY.getZeroBased());
        Company thirdCompany = model.getFilteredCompanyList().get(INDEX_THIRD_COMPANY.getZeroBased());

        Company editedSecondCompany = new CompanyBuilder(secondCompany).withPhone("99887766").build();
        Company editedThirdCompany = new CompanyBuilder(thirdCompany).withPhone("99887766").build();

        expectedModel.setCompany(secondCompany, editedSecondCompany);
        expectedModel.setCompany(thirdCompany, editedThirdCompany);

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

}
