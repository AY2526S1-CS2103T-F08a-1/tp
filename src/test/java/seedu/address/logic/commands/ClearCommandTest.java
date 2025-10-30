package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalCompanies.getTypicalAddressBook;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;

/**
 * Extra tests for ClearCommand that avoid showing UI by forcing ConfirmWindow to auto-continue
 * via the seedu.skipPrompts system property.
 */
public class ClearCommandTest {
    private String prevSkipPrompts;

    @BeforeEach
    void forceBypassPrompts() {
        prevSkipPrompts = System.getProperty("seedu.skipPrompts");
        System.setProperty("seedu.skipPrompts", "true");
    }

    @AfterEach
    void restoreBypassFlag() {
        if (prevSkipPrompts == null) {
            System.clearProperty("seedu.skipPrompts");
        } else {
            System.setProperty("seedu.skipPrompts", prevSkipPrompts);
        }
    }

    @Test
    public void execute_emptyAddressBook_success() {
        Model model = new ModelManager();
        Model expectedModel = new ModelManager();

        assertCommandSuccess(new ClearCommand(), model, ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_nonEmptyAddressBook_success() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel.setAddressBook(new AddressBook());

        assertCommandSuccess(new ClearCommand(), model, ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }
}
