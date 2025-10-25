package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.company.Status;

/**
 * Filters and lists all companies that match a given application status.
 */
public class FilterCommand extends Command {

    public static final String COMMAND_WORD = "filter";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Filters companies by application status and displays "
            + "them as a list with index numbers.\n"
            + "Parameters: s/STATUS (one of: to-apply, applied, oa, tech-interview, hr-interview, in-process, "
            + "offered, accepted, rejected)\n"
            + "Example: " + COMMAND_WORD + " s/in-process";

    private final Status status;

    /**
     * Creates a FilterCommand object with the given status.
     *
     * @param status the status to filter by
     */
    public FilterCommand(Status status) {
        requireNonNull(status);
        this.status = status;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredCompanyList(company -> company.getStatus().equals(status));
        return new CommandResult(String.format(
                Messages.MESSAGE_COMPANIES_LISTED_OVERVIEW, model.getFilteredCompanyList().size()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof FilterCommand)) {
            return false;
        }
        FilterCommand otherCommand = (FilterCommand) other;
        return status.equals(otherCommand.status);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("status", status)
                .toString();
    }
}

