package seedu.address.logic.commands;

import seedu.address.model.Model;

/**
 * Shows application metrics with status distribution.
 */
public class MetricsCommand extends Command {

    public static final String COMMAND_WORD = "metrics";

    public static final String SHOWING_METRICS_MESSAGE = "Opened metrics window.";

    @Override
    public CommandResult execute(Model model) {
        return new CommandResult(SHOWING_METRICS_MESSAGE, false, false, true);
    }
}
