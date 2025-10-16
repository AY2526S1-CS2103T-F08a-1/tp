package seedu.address.logic.commands;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.company.Company;
import seedu.address.model.company.Status;

/**
 * Changes the status of an existing company in the address book.
 */
public class StatusCommand extends Command {

    public static final String COMMAND_WORD = "status";
    public static final String MESSAGE_UPDATE_STATUS_SUCCESS = "Updated status of Company: %1$s";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Edits the application status of the company identified "
            + "by the index number used in the last company listing. "
            + "Existing status will be overwritten by the input.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "s/STATUS (one of: to-apply, applied, oa, tech-interview, hr-interview, in-process, rejected, "
            + "offered, accepted)\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + "s/in-process";

    private final Index index;
    private final Status status;

    /**
     * @param index  of the company in the filtered company list to edit the status
     * @param status of the company to be updated to
     */
    public StatusCommand(Index index, Status status) {
        requireAllNonNull(index, status);

        this.index = index;
        this.status = status;
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
                companyToEdit.getAddress(), companyToEdit.getTags(), companyToEdit.getRemark(), status);

        model.setCompany(companyToEdit, editedCompany);
        model.updateFilteredCompanyList(Model.PREDICATE_SHOW_ALL_COMPANIES);

        return new CommandResult(String.format(MESSAGE_UPDATE_STATUS_SUCCESS, Messages.format(editedCompany)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof StatusCommand)) {
            return false;
        }

        StatusCommand e = (StatusCommand) other;
        return index.equals(e.index)
                && status.equals(e.status);
    }

    @Override
    public String toString() {
        return StatusCommand.class.getCanonicalName()
                + "{index=" + index + ", status=" + status + "}";
    }
}
