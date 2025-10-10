package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.company.Company;

/**
 * Deletes one or more companies by their displayed indices from the address book.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the company (or companies) identified by the index/indices in the displayed company list.\n"
            + "Simple format: " + COMMAND_WORD + " INDEX\n"
            + "Multiple indices: " + COMMAND_WORD + " INDEX [INDEX]...\n"
            + "Range: " + COMMAND_WORD + " START-END\n"
            + "Examples:\n"
            + "  " + COMMAND_WORD + " 1\n"
            + "  " + COMMAND_WORD + " 1 3 5\n"
            + "  " + COMMAND_WORD + " 2-4";

    public static final String MESSAGE_DELETE_COMPANY_SUCCESS = "Deleted Company: %1$s";

    private final List<Index> targetIndices;

    /**
     * Make a list for multiple target indices and sorted in ascending order
     *
     * @param targetIndices
     */
    public DeleteCommand(List<Index> targetIndices) {
        // Defensive copy + normalize ascending for display; we’ll delete in descending later.
        this.targetIndices = new ArrayList<>(requireNonNull(targetIndices));
        this.targetIndices.sort(Comparator.comparingInt(Index::getZeroBased));
    }

    /**
     * Make a list for single target index
     *
     * @param targetIndex
     */
    public DeleteCommand(Index targetIndex) {
        this(List.of(requireNonNull(targetIndex)));
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        final List<Company> lastShownList = model.getFilteredCompanyList();
        final int listSize = lastShownList.size();

        // Validate all indices first (fail-fast if any is out of bounds)
        for (Index idx : targetIndices) {
            if (idx.getZeroBased() >= listSize) {
                throw new CommandException(Messages.MESSAGE_INVALID_COMPANY_DISPLAYED_INDEX);
            }
        }

        // Snapshot the companies to be deleted (before mutation), in ascending order for message.
        final List<Company> companiesToDelete = targetIndices.stream()
                .map(i -> lastShownList.get(i.getZeroBased()))
                .collect(Collectors.toList());

        // Delete in descending index order to avoid shifting
        targetIndices.stream()
                .sorted((a, b) -> Integer.compare(b.getZeroBased(), a.getZeroBased()))
                .forEach(idx -> {
                    Company c = lastShownList.get(idx.getZeroBased());
                    model.deleteCompany(c);
                });

        // Build user feedback: comma-separated formatted names
        String formatted = companiesToDelete.stream()
                .map(Messages::format)
                .collect(Collectors.joining(", "));

        return new CommandResult(String.format(MESSAGE_DELETE_COMPANY_SUCCESS, formatted));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof DeleteCommand)) {
            return false;
        }
        DeleteCommand otherDeleteCommand = (DeleteCommand) other;
        return this.targetIndices.equals(otherDeleteCommand.targetIndices);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndices", targetIndices)
                .toString();
    }
}