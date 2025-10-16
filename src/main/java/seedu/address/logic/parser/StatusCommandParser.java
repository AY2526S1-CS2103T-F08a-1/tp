package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STATUS;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.StatusCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.company.Status;

/**
 * Parses input arguments and creates a new StatusCommand object
 */
public class StatusCommandParser implements Parser<StatusCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the
     * StatusCommand and returns a StatusCommand object for execution.
     *
     * @param args the input arguments string containing the index and status
     * @return a StatusCommand object with the parsed index and status
     * @throws ParseException if the user input does not conform the expected format
     */
    public StatusCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_STATUS);

        ParseException pe = new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                StatusCommand.MESSAGE_USAGE));
        Index index;
        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException e) {
            throw pe;
        }
        String statusText = argMultimap.getValue(PREFIX_STATUS).orElseThrow(() -> pe);
        Status status = ParserUtil.parseStatus(statusText);
        return new StatusCommand(index, status);
    }

}
