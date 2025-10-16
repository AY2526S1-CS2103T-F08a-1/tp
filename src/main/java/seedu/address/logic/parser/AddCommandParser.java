package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMARK;
import static seedu.address.logic.parser.CliSyntax.PREFIX_STATUS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.company.Address;
import seedu.address.model.company.Company;
import seedu.address.model.company.Email;
import seedu.address.model.company.Name;
import seedu.address.model.company.Phone;
import seedu.address.model.company.Remark;
import seedu.address.model.company.Status;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new AddCommand object
 */
public class AddCommandParser implements Parser<AddCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the AddCommand
     * and returns an AddCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public AddCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS, PREFIX_TAG,
                PREFIX_REMARK, PREFIX_STATUS);

        // Check if this is a simple name format (only n/ prefix used)
        if (isSimpleNameFormat(argMultimap)) {
            return parseSimpleNameFormat(argMultimap);
        }

        if (!arePrefixesPresent(argMultimap, PREFIX_NAME, PREFIX_ADDRESS, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_REMARK)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS,
                PREFIX_REMARK, PREFIX_STATUS);
        Name name = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get());
        Phone phone = ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE).get());
        Email email = ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL).get());
        Address address = ParserUtil.parseAddress(argMultimap.getValue(PREFIX_ADDRESS).get());
        Set<Tag> tagList = ParserUtil.parseTags(argMultimap.getAllValues(PREFIX_TAG));
        Remark remark = ParserUtil.parseRemark(argMultimap.getValue(PREFIX_REMARK).get());
        Status status = argMultimap.getValue(PREFIX_STATUS).isPresent()
                ? ParserUtil.parseStatus(argMultimap.getValue(PREFIX_STATUS).get())
                : new Status(); // Use default if not provided

        Company company = new Company(name, phone, email, address, tagList, remark, status);

        return new AddCommand(company);
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

    /**
     * Returns true if this appears to be a simple name format (only n/ prefix used).
     */
    private static boolean isSimpleNameFormat(ArgumentMultimap argumentMultimap) {
        return argumentMultimap.getValue(PREFIX_NAME).isPresent()
                && !argumentMultimap.getValue(PREFIX_PHONE).isPresent()
                && !argumentMultimap.getValue(PREFIX_EMAIL).isPresent()
                && !argumentMultimap.getValue(PREFIX_ADDRESS).isPresent()
                && !argumentMultimap.getValue(PREFIX_TAG).isPresent()
                && !argumentMultimap.getValue(PREFIX_REMARK).isPresent()
                && !argumentMultimap.getValue(PREFIX_STATUS).isPresent()
                && argumentMultimap.getPreamble().trim().isEmpty();
    }

    /**
     * Parses a simple name format input and creates an AddCommand with placeholder values.
     */
    private AddCommand parseSimpleNameFormat(ArgumentMultimap argMultimap) throws ParseException {
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME);
        Name name = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get());
        Phone phone = new Phone("000"); // Placeholder that meets validation (3+ digits)
        Email email = new Email("noemailprovided@placeholder.com"); // Placeholder that meets validation
        Address address = new Address("No address provided"); // Placeholder that meets validation
        Set<Tag> tagList = new HashSet<>(); // Empty tags
        Remark remark = new Remark("No remark provided"); // Placeholder remark

        Company company = new Company(name, phone, email, address, tagList, remark, status);
        return new AddCommand(company);
    }

}
