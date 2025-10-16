package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMARK;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STATUS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.company.Company;
import seedu.address.model.company.NameContainsKeywordsPredicate;
import seedu.address.testutil.EditCompanyDescriptorBuilder;

/**
 * Contains helper methods for testing commands.
 */
public class CommandTestUtil {

    public static final String VALID_NAME_AIRBUS = "Airbus";
    public static final String VALID_NAME_BOEING = "Boeing";
    public static final String VALID_PHONE_AIRBUS = "11111111";
    public static final String VALID_PHONE_BOEING = "22222222";
    public static final String VALID_EMAIL_AIRBUS = "airbus@example.com";
    public static final String VALID_EMAIL_BOEING = "boeing@example.com";
    public static final String VALID_ADDRESS_AIRBUS = "Block 312, Amy Street 1";
    public static final String VALID_ADDRESS_BOEING = "Block 123, Bobby Street 3";
    public static final String VALID_TAG_GOOD_PAY = "good-pay";
    public static final String VALID_TAG_DECENT_LOCATION = "decent-location";
    public static final String VALID_REMARK_AIRBUS = "Great location and pay";
    public static final String VALID_REMARK_BOEING = "Lacking pay but good experience";
    public static final String VALID_STATUS_AIRBUS = "tech-interview";
    public static final String VALID_STATUS_BOEING = "to-apply";

    public static final String NAME_DESC_AIRBUS = " " + PREFIX_NAME + VALID_NAME_AIRBUS;
    public static final String NAME_DESC_BOEING = " " + PREFIX_NAME + VALID_NAME_BOEING;
    public static final String PHONE_DESC_AIRBUS = " " + PREFIX_PHONE + VALID_PHONE_AIRBUS;
    public static final String PHONE_DESC_BOEING = " " + PREFIX_PHONE + VALID_PHONE_BOEING;
    public static final String EMAIL_DESC_AIRBUS = " " + PREFIX_EMAIL + VALID_EMAIL_AIRBUS;
    public static final String EMAIL_DESC_BOEING = " " + PREFIX_EMAIL + VALID_EMAIL_BOEING;
    public static final String ADDRESS_DESC_AIRBUS = " " + PREFIX_ADDRESS + VALID_ADDRESS_AIRBUS;
    public static final String ADDRESS_DESC_BOEING = " " + PREFIX_ADDRESS + VALID_ADDRESS_BOEING;
    public static final String TAG_DESC_DECENT_LOCATION = " " + PREFIX_TAG + VALID_TAG_DECENT_LOCATION;
    public static final String TAG_DESC_GOOD_PAY = " " + PREFIX_TAG + VALID_TAG_GOOD_PAY;
    public static final String REMARK_DESC_AIRBUS = " " + PREFIX_REMARK + VALID_REMARK_AIRBUS;
    public static final String REMARK_DESC_BOEING = " " + PREFIX_REMARK + VALID_REMARK_BOEING;
    public static final String STATUS_DESC_AIRBUS = " " + PREFIX_STATUS + VALID_STATUS_AIRBUS;
    public static final String STATUS_DESC_BOEING = " " + PREFIX_STATUS + VALID_STATUS_BOEING;

    public static final String INVALID_NAME_DESC = " " + PREFIX_NAME + "James&"; // '&' not allowed in names
    public static final String INVALID_PHONE_DESC = " " + PREFIX_PHONE + "911a"; // 'a' not allowed in phones
    public static final String INVALID_EMAIL_DESC = " " + PREFIX_EMAIL + "bob!yahoo"; // missing '@' symbol
    public static final String INVALID_ADDRESS_DESC = " " + PREFIX_ADDRESS; // empty string not allowed for addresses
    public static final String INVALID_TAG_DESC = " " + PREFIX_TAG + "hubby*"; // '*' not allowed in tags
    public static final String INVALID_STATUS_DESC = " " + PREFIX_STATUS + "pending application"; // spaces not allowed

    public static final String PREAMBLE_WHITESPACE = "\t  \r  \n";
    public static final String PREAMBLE_NON_EMPTY = "NonEmptyPreamble";

    public static final EditCommand.EditCompanyDescriptor DESC_AIRBUS;
    public static final EditCommand.EditCompanyDescriptor DESC_BOEING;

    static {
        DESC_AIRBUS = new EditCompanyDescriptorBuilder().withName(VALID_NAME_AIRBUS)
                .withPhone(VALID_PHONE_AIRBUS).withEmail(VALID_EMAIL_AIRBUS).withAddress(VALID_ADDRESS_AIRBUS)
                .withTags(VALID_TAG_DECENT_LOCATION).withRemark(VALID_REMARK_AIRBUS)
                .withStatus(VALID_STATUS_AIRBUS).build();
        DESC_BOEING = new EditCompanyDescriptorBuilder().withName(VALID_NAME_BOEING)
                .withPhone(VALID_PHONE_BOEING).withEmail(VALID_EMAIL_BOEING).withAddress(VALID_ADDRESS_BOEING)
                .withTags(VALID_TAG_GOOD_PAY, VALID_TAG_DECENT_LOCATION).withRemark(VALID_REMARK_BOEING)
                .withStatus(VALID_STATUS_BOEING).build();
    }

    /**
     * Executes the given {@code command}, confirms that <br>
     * - the returned {@link CommandResult} matches {@code expectedCommandResult} <br>
     * - the {@code actualModel} matches {@code expectedModel}
     */
    public static void assertCommandSuccess(Command command, Model actualModel, CommandResult expectedCommandResult,
            Model expectedModel) {
        try {
            CommandResult result = command.execute(actualModel);
            assertEquals(expectedCommandResult, result);
            assertEquals(expectedModel, actualModel);
        } catch (CommandException ce) {
            throw new AssertionError("Execution of command should not fail.", ce);
        }
    }

    /**
     * Convenience wrapper to {@link #assertCommandSuccess(Command, Model, CommandResult, Model)}
     * that takes a string {@code expectedMessage}.
     */
    public static void assertCommandSuccess(Command command, Model actualModel, String expectedMessage,
            Model expectedModel) {
        CommandResult expectedCommandResult = new CommandResult(expectedMessage);
        assertCommandSuccess(command, actualModel, expectedCommandResult, expectedModel);
    }

    /**
     * Executes the given {@code command}, confirms that <br>
     * - a {@code CommandException} is thrown <br>
     * - the CommandException message matches {@code expectedMessage} <br>
     * - the address book, filtered company list and selected company in {@code actualModel} remain unchanged
     */
    public static void assertCommandFailure(Command command, Model actualModel, String expectedMessage) {
        // we are unable to defensively copy the model for comparison later, so we can
        // only do so by copying its components.
        AddressBook expectedAddressBook = new AddressBook(actualModel.getAddressBook());
        List<Company> expectedFilteredList = new ArrayList<>(actualModel.getFilteredCompanyList());

        assertThrows(CommandException.class, expectedMessage, () -> command.execute(actualModel));
        assertEquals(expectedAddressBook, actualModel.getAddressBook());
        assertEquals(expectedFilteredList, actualModel.getFilteredCompanyList());
    }
    /**
     * Updates {@code model}'s filtered list to show only the company at the given {@code targetIndex} in the
     * {@code model}'s address book.
     */
    public static void showCompanyAtIndex(Model model, Index targetIndex) {
        assertTrue(targetIndex.getZeroBased() < model.getFilteredCompanyList().size());

        Company company = model.getFilteredCompanyList().get(targetIndex.getZeroBased());
        final String[] splitName = company.getName().fullName.split("\\s+");
        model.updateFilteredCompanyList(new NameContainsKeywordsPredicate(Arrays.asList(splitName[0])));

        assertEquals(1, model.getFilteredCompanyList().size());
    }

}
