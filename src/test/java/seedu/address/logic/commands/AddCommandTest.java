package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalCompanies.ALPHA;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.company.Company;
import seedu.address.testutil.CompanyBuilder;

public class AddCommandTest {

    @Test
    public void constructor_nullCompany_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddCommand(null));
    }

    /**
     * Tests that a valid company is successfully added to the model.
     */
    @Test
    public void execute_companyAcceptedByModel_addSuccessful() throws Exception {
        ModelStubAcceptingCompanyAdded modelStub = new ModelStubAcceptingCompanyAdded();
        Company validCompany = new CompanyBuilder().build();

        CommandResult commandResult = new AddCommand(validCompany).execute(modelStub);

        assertEquals(String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(validCompany)),
                commandResult.getFeedbackToUser());
        assertEquals(Arrays.asList(validCompany), modelStub.companiesAdded);
    }

    /**
     * Tests that executing AddCommand with a duplicate company
     * throws CommandException.
     */
    @Test
    public void execute_duplicateCompany_throwsCommandException() {
        Company validCompany = new CompanyBuilder().build();
        AddCommand addCommand = new AddCommand(validCompany);
        ModelStub modelStub = new ModelStubWithCompany(validCompany);

        assertThrows(CommandException.class, AddCommand.MESSAGE_DUPLICATE_COMPANY, () -> addCommand.execute(modelStub));
    }

    @Test
    public void equals() {
        Company apple = new CompanyBuilder().withName("Apple").build();
        Company bayer = new CompanyBuilder().withName("Bayer").build();
        AddCommand addAppleCommand = new AddCommand(apple);
        AddCommand addBayerCommand = new AddCommand(bayer);

        // same object -> returns true
        assertTrue(addAppleCommand.equals(addAppleCommand));

        // same values -> returns true
        AddCommand addAliceCommandCopy = new AddCommand(apple);
        assertTrue(addAppleCommand.equals(addAliceCommandCopy));

        // different types -> returns false
        assertFalse(addAppleCommand.equals(1));

        // null -> returns false
        assertFalse(addAppleCommand.equals(null));

        // different company -> returns false
        assertFalse(addAppleCommand.equals(addBayerCommand));
    }

    @Test
    public void toStringMethod() {
        AddCommand addCommand = new AddCommand(ALPHA);
        String expected = AddCommand.class.getCanonicalName() + "{toAdd=" + ALPHA + "}";
        assertEquals(expected, addCommand.toString());
    }

    /**
     * Tests that adding a company with multiple tags is successful.
     * Verifies that the command result contains the correct success message
     * and that the company with all its tags is added to the model.
     */
    @Test
    public void execute_companyWithMultipleTags_addSuccessful() throws Exception {
        ModelStubAcceptingCompanyAdded modelStub = new ModelStubAcceptingCompanyAdded();
        Company companyWithMultipleTags = new CompanyBuilder()
                .withName("Tech Corp")
                .withPhone("88888888")
                .withEmail("contact@techcorp.com")
                .withAddress("123 Tech Street")
                .withTags("client", "partner", "supplier")
                .build();

        CommandResult commandResult = new AddCommand(companyWithMultipleTags).execute(modelStub);

        assertEquals(String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(companyWithMultipleTags)),
                commandResult.getFeedbackToUser());
        assertEquals(Arrays.asList(companyWithMultipleTags), modelStub.companiesAdded);
    }

    /**
     * Tests that executing AddCommand with a null model throws NullPointerException.
     * This is a defensive programming check to ensure proper error handling
     * when the command is executed with invalid parameters.
     */
    @Test
    public void execute_nullModel_throwsNullPointerException() {
        Company validCompany = new CompanyBuilder().build();
        AddCommand addCommand = new AddCommand(validCompany);

        assertThrows(NullPointerException.class, () -> addCommand.execute(null));
    }

    /**
     * Tests that adding a company without any tags is successful.
     * Verifies that tags are optional and a company can be added
     * with an empty tag set.
     */
    @Test
    public void execute_companyWithNoTags_addSuccessful() throws Exception {
        ModelStubAcceptingCompanyAdded modelStub = new ModelStubAcceptingCompanyAdded();
        Company companyWithNoTags = new CompanyBuilder()
                .withName("Simple Company")
                .withPhone("99999999")
                .withEmail("info@simple.com")
                .withAddress("456 Simple Road")
                .withTags()
                .build();

        CommandResult commandResult = new AddCommand(companyWithNoTags).execute(modelStub);

        assertEquals(String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(companyWithNoTags)),
                commandResult.getFeedbackToUser());
        assertEquals(Arrays.asList(companyWithNoTags), modelStub.companiesAdded);
    }

    /**
     * Tests that adding a company with numbers in its name is successful.
     * Company names can contain alphanumeric characters, so names like
     * "3M Corporation" should be valid and successfully added.
     */
    @Test
    public void execute_companyWithNumbersInName_addSuccessful() throws Exception {
        ModelStubAcceptingCompanyAdded modelStub = new ModelStubAcceptingCompanyAdded();
        Company companyWithNumbers = new CompanyBuilder()
                .withName("3M Corporation")
                .withPhone("77777777")
                .withEmail("contact@3m.com")
                .withAddress("100 Innovation Street")
                .withTags("manufacturer")
                .build();

        CommandResult commandResult = new AddCommand(companyWithNumbers).execute(modelStub);

        assertEquals(String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(companyWithNumbers)),
                commandResult.getFeedbackToUser());
        assertEquals(Arrays.asList(companyWithNumbers), modelStub.companiesAdded);
    }

    /**
     * Tests that adding a company with a very long address is successful.
     * Verifies that the system can handle addresses with multiple components
     * and lengthy text without issues.
     */
    @Test
    public void execute_companyWithLongAddress_addSuccessful() throws Exception {
        ModelStubAcceptingCompanyAdded modelStub = new ModelStubAcceptingCompanyAdded();
        Company companyWithLongAddress = new CompanyBuilder()
                .withName("Far Away Industries")
                .withPhone("66666666")
                .withEmail("info@faraway.com")
                .withAddress("Block 999, Very Long Street Name Avenue 123, #45-678, "
                        + "Near The Big Shopping Mall, Behind The Park")
                .withTags("client")
                .build();

        CommandResult commandResult = new AddCommand(companyWithLongAddress).execute(modelStub);

        assertEquals(String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(companyWithLongAddress)),
                commandResult.getFeedbackToUser());
        assertEquals(Arrays.asList(companyWithLongAddress), modelStub.companiesAdded);
    }

    /**
     * Tests that adding multiple different companies sequentially is successful.
     * Verifies that the model can accept and store multiple distinct companies
     * in the correct order.
     */
    @Test
    public void execute_multipleDifferentCompanies_addSuccessful() throws Exception {
        ModelStubAcceptingCompanyAdded modelStub = new ModelStubAcceptingCompanyAdded();
        Company firstCompany = new CompanyBuilder().withName("First Company").build();
        Company secondCompany = new CompanyBuilder().withName("Second Company").build();
        Company thirdCompany = new CompanyBuilder().withName("Third Company").build();

        new AddCommand(firstCompany).execute(modelStub);
        new AddCommand(secondCompany).execute(modelStub);
        new AddCommand(thirdCompany).execute(modelStub);

        assertEquals(Arrays.asList(firstCompany, secondCompany, thirdCompany), modelStub.companiesAdded);
    }

    /**
     * Tests that adding a duplicate company after multiple successful additions
     * throws CommandException. Verifies that duplicate detection works correctly
     * even when the duplicate has different phone and email but same name.
     */
    @Test
    public void execute_duplicateCompanyAfterMultipleAdds_throwsCommandException() throws Exception {
        ModelStubAcceptingCompanyAdded modelStub = new ModelStubAcceptingCompanyAdded();
        Company firstCompany = new CompanyBuilder().withName("Unique Corp").build();
        Company secondCompany = new CompanyBuilder().withName("Different Corp").build();
        Company duplicateCompany = new CompanyBuilder().withName("Unique Corp")
                .withPhone("99999999").withEmail("different@email.com").build();

        new AddCommand(firstCompany).execute(modelStub);
        new AddCommand(secondCompany).execute(modelStub);
        AddCommand addDuplicateCommand = new AddCommand(duplicateCompany);

        assertThrows(CommandException.class, AddCommand.MESSAGE_DUPLICATE_COMPANY, () ->
                addDuplicateCommand.execute(modelStub));
    }

    /**
     * Tests that companies with the same name but different casing are not
     * considered duplicates. The system treats "google" and "GOOGLE" as
     * distinct companies since name comparison is case-sensitive.
     */
    @Test
    public void execute_companyWithDifferentNameCasing_notConsideredDuplicate() throws Exception {
        ModelStubAcceptingCompanyAdded modelStub = new ModelStubAcceptingCompanyAdded();
        Company lowerCaseCompany = new CompanyBuilder().withName("google").build();
        Company upperCaseCompany = new CompanyBuilder().withName("GOOGLE").build();

        new AddCommand(lowerCaseCompany).execute(modelStub);
        CommandResult commandResult = new AddCommand(upperCaseCompany).execute(modelStub);

        assertEquals(String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(upperCaseCompany)),
                commandResult.getFeedbackToUser());
        assertEquals(Arrays.asList(lowerCaseCompany, upperCaseCompany), modelStub.companiesAdded);
    }

    /**
     * Tests that executing AddCommand returns a non-null CommandResult
     * with the correct success message. Verifies that the command properly
     * constructs and returns a result object.
     */
    @Test
    public void execute_commandResultNotNull_success() throws Exception {
        ModelStubAcceptingCompanyAdded modelStub = new ModelStubAcceptingCompanyAdded();
        Company validCompany = new CompanyBuilder().build();
        AddCommand addCommand = new AddCommand(validCompany);

        CommandResult result = addCommand.execute(modelStub);

        // verify that the command result is not null and has correct message
        assertTrue(result != null);
        assertEquals(String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(validCompany)),
                result.getFeedbackToUser());
    }

    /**
     * Tests that the CommandResult from AddCommand does not trigger help or exit.
     * Verifies that AddCommand executes normally without side effects like showing
     * help or terminating the application.
     */
    @Test
    public void execute_commandResultFlags_noHelpOrExit() throws Exception {
        ModelStubAcceptingCompanyAdded modelStub = new ModelStubAcceptingCompanyAdded();
        Company validCompany = new CompanyBuilder().build();
        AddCommand addCommand = new AddCommand(validCompany);

        CommandResult result = addCommand.execute(modelStub);

        assertNotNull(result);
        assertEquals(String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(validCompany)),
                result.getFeedbackToUser());
        assertFalse(result.isShowHelp(), "AddCommand should not trigger help window");
        assertFalse(result.isExit(), "AddCommand should not exit the application");
    }

    /**
     * A default model stub that have all the methods failing.
     */
    private class ModelStub implements Model {
        @Override
        public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyUserPrefs getUserPrefs() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public GuiSettings getGuiSettings() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setGuiSettings(GuiSettings guiSettings) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Path getAddressBookFilePath() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setAddressBookFilePath(Path addressBookFilePath) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addCompany(Company company) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setAddressBook(ReadOnlyAddressBook newData) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasCompany(Company company) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deleteCompany(Company target) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setCompany(Company target, Company editedCompany) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Company> getFilteredCompanyList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredCompanyList(Predicate<Company> predicate) {
            throw new AssertionError("This method should not be called.");
        }
    }

    /**
     * A Model stub that contains a single company.
     */
    private class ModelStubWithCompany extends ModelStub {
        private final Company company;

        ModelStubWithCompany(Company company) {
            requireNonNull(company);
            this.company = company;
        }

        @Override
        public boolean hasCompany(Company company) {
            requireNonNull(company);
            return this.company.isSameCompany(company);
        }
    }

    /**
     * A Model stub that always accept the company being added.
     */
    private class ModelStubAcceptingCompanyAdded extends ModelStub {
        final ArrayList<Company> companiesAdded = new ArrayList<>();

        @Override
        public boolean hasCompany(Company company) {
            requireNonNull(company);
            return companiesAdded.stream().anyMatch(company::isSameCompany);
        }

        @Override
        public void addCompany(Company company) {
            requireNonNull(company);
            companiesAdded.add(company);
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            return new AddressBook();
        }
    }

}
