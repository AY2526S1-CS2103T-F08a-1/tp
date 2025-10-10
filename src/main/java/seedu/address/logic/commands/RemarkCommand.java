package seedu.address.logic.commands;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.company.Company;
import seedu.address.model.company.Remark;

/**
 * Changes the remark of an existing person in the address book.
 */
public class RemarkCommand extends Command {

    public static final String COMMAND_WORD = "remark";
    public static final String MESSAGE_ARGUMENTS = "Index: %1$d, Remark: %2$s";
    public static final String MESSAGE_ADD_REMARK_SUCCESS = "Added remark to Company: %1$s";
    public static final String MESSAGE_DELETE_REMARK_SUCCESS = "Removed remark from Company: %1$s";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Edits the remark of the company identified "
            + "by the index number used in the last company listing. "
            + "Existing remark will be overwritten by the input.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "r/ [REMARK]\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + "r/ Likes to swim.";

    private final Index index;
    private final Remark remark;

    /**
     * @param index  of the person in the filtered person list to edit the remark
     * @param remark of the person to be updated to
     */
    public RemarkCommand(Index index, Remark remark) {
        requireAllNonNull(index, remark);

        this.index = index;
        this.remark = remark;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        List<Company> lastShownList = model.getFilteredCompanyList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_COMPANY_DISPLAYED_INDEX);
        }

        Company companyToEdit = lastShownList.get(index.getZeroBased());
        Company editedCompany = new Company(
                companyToEdit.getName(), companyToEdit.getPhone(), companyToEdit.getEmail(),
                companyToEdit.getAddress(), companyToEdit.getTags(), remark, companyToEdit.getStatus());

        model.setCompany(companyToEdit, editedCompany);
        model.updateFilteredCompanyList(Model.PREDICATE_SHOW_ALL_COMPANIES);

        return new CommandResult(generateSuccessMessage(editedCompany));
    }

    /**
     * Generates a command execution success message based on whether
     * the remark is added to or removed from
     * {@code personToEdit}.
     */
    private String generateSuccessMessage(Company companyToEdit) {
        String message = !remark.value.isEmpty() ? MESSAGE_ADD_REMARK_SUCCESS : MESSAGE_DELETE_REMARK_SUCCESS;
        return String.format(message, Messages.format(companyToEdit));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof RemarkCommand)) {
            return false;
        }

        RemarkCommand e = (RemarkCommand) other;
        return index.equals(e.index)
                && remark.equals(e.remark);
    }
}
